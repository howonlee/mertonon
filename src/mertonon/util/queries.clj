(ns mertonon.util.queries
  "We don't use a true ORM because the ones for Clojure are all festooned with macros and annoyingly magic (sorry Cam, you know it's true),
  so we just want to do query-building with pure functions in honeysql language"
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [honey.sql :as sql]
            [mertonon.util.db :as db]
            [tick.core :as t]))

;; -----
;; Proprocessing and coercions
;; -----

(defn snakify
  "Coerces a keyword identifier to snake_case"
  [identifier]
  (-> (name identifier)
      (str/replace #"-" "_")
      keyword))

(defn kebabify
  "Coerces a keyword identifier to kebab-case"
  [identifier]
  (-> (name identifier)
      (str/replace #"_" "-")
      keyword))

(defn preprocess-columns
  "Universal preprocessing for columns"
  [columns]
  (->> columns (map snakify)))

(defn stringify-vals-for-update
  "Coerces values of map which are typed as keywords or maps or arrays to strings
  Can't use member->row because that assumes uuid is there, which it isn't in updates"
  [attrs]
  (into {} (for [[k v] attrs]
             {k (condp = (type v)
                  clojure.lang.Keyword            (name v)
                  clojure.lang.PersistentVector   (json/write-str v)
                  clojure.lang.PersistentArrayMap (json/write-str v)
                  v)})))


(defn rowify [columns member]
  (vec (for [column columns
             :let [entry  (member column)]]
         (cond (keyword? entry) (name entry)
               :else            entry))))

;; Renormalized like EF Codd, not like KG Wilson
(defn renormalize-joined-row
  [raw-table->table table->model joined-row]
  (let [grouped (group-by
                  (fn [[fst snd]] (keyword (namespace fst)))
                  joined-row)
        member  (into
                  {}
                  (for [[k v] grouped]
                    (let [curr-table       (raw-table->table k)
                          curr-row->member ((table->model curr-table) :row->member)]
                    [curr-table
                     [(curr-row->member (into {} v))]])))]
    member))

(defn renormalize-joined-res
  [raw-table->table table->model joined-res]
  (if (empty? joined-res)
    {}
    (let [renorm-res  (mapv (partial renormalize-joined-row raw-table->table table->model) joined-res)
          reduced-res (reduce (fn [fst snd] (merge-with into fst snd)) renorm-res)]
      reduced-res)))

;; -----
;; Queries and query application
;; -----

;; -----
;; Create
;; -----

(defn create-one-q [columns table member member->row]
  (let [row (rowify columns (member->row member))]
    {:insert-into table
     :columns     (preprocess-columns columns)
     :values      [row]
     :returning   :*}))

(defn create-one [{:keys [columns table member row->member member->row]}]
  (->> (create-one-q columns table member member->row) (db/query) first row->member))

(defn create-many-q [columns table members member->row]
  (let [serialized (map member->row members)
        rows       (mapv #(rowify columns %) serialized)]
    {:insert-into table
     :columns     (preprocess-columns columns)
     :values      rows
     :returning   :*}))

(defn create-many [{:keys [columns table members row->member member->row]}]
  (assert (> (count members) 0))
  (->> (create-many-q columns table members member->row) (db/query) (mapv row->member)))

;; -----
;; Read
;; -----

(defn select-all-q [columns table]
  {:select (preprocess-columns columns) :from table})

(defn select-all [{:keys [columns table row->member]}]
  (->> (select-all-q columns table) (db/query) (mapv row->member)))

(defn count-all-q [table]
  {:select :%count.* :from table})

(defn count-all [{:keys [table]}]
  (->> (count-all-q table) (db/query) first :count))

(defn select-many-q [columns table uuids]
  {:select (preprocess-columns columns) :from table :where [:in :uuid uuids]})

(defn select-many [{:keys [columns table uuids row->member]}]
  (assert (> (count uuids) 0))
  (->> (select-many-q columns table uuids) (db/query) (mapv row->member)))

(defn select-one-q [columns table uuid]
  {:select (preprocess-columns columns) :from table :where [:= :uuid uuid] :limit 1})

(defn select-one [{:keys [columns table uuid row->member]}]
  (->> (select-one-q columns table uuid) (db/query) first row->member))

(defn select-where-q [columns table where-clause]
  {:select (preprocess-columns columns) :from table :where where-clause})

(defn select-where [{:keys [columns table where-clause row->member]}]
  (->> (select-where-q columns table where-clause) (db/query) (mapv row->member)))

(defn select-where-joined-q
  "This is inner join only
  TODO: write left join too
  TODO: non splat
  TODO: make the ordering of tables and col edges not matter somehow. For now, just deal with it"
  [table join-tables join-col-edges where-clause]
  {:select     [:*]
   :from       table
   :inner-join (->>
                 (for [[join-table [join-col-child join-col-parent]] (map vector join-tables join-col-edges)]
                   [join-table [:= join-col-child join-col-parent]])
                 (apply concat)
                 vec)
   :where      where-clause})

(defn select-where-joined
  [{:keys [table join-tables join-col-edges where-clause raw-table->table table->model]}]
  (let [curr-query (select-where-joined-q table join-tables join-col-edges where-clause)]
    (->> curr-query (db/query) (renormalize-joined-res raw-table->table table->model))))

;; -----
;; Update
;; -----

(defn update-one-q [table to-update-uuid member]
  (let [member-attrs      (-> member
                              (dissoc :uuid)
                              (assoc :updated-at (t/instant)))]
    {:update    table
     ;; TODO: make this canonicalized with member->row also
     :set       (stringify-vals-for-update member-attrs)
     :where     [:= :uuid to-update-uuid]
     :returning :*}))

(defn update-one [{:keys [table uuid member row->member]}]
  ;; If it would be a noop, act like a noop and don't make a query
  (if (empty? (dissoc member :uuid))
    member
    (->> (update-one-q table uuid member) (db/query) first row->member)))

;; update test as t set
;;     column_a = c.column_a,
;;     column_c = c.column_c
;; from (values
;;     ('123', 1, '---'),
;;     ('345', 2, '+++')  
;; ) as c(column_b, column_a, column_c) 
;; where c.column_b = t.column_b;

(defn update-many-set-clause [table columns]
  {:updated-at :temp.updated-at})

(defn update-many-from-clause [members]
  [{:values [[:bleh :whleh] [:mleh :vleh]]}])

(defn update-many-where-clause [table]
  (let [table-uuid (-> table name (str ".uuid") keyword)]
    [:= :temp.uuid table-uuid]))

(defn update-many-q [table columns uuids members]
  {:update    [table :curr-table]
   :set       (update-many-set-clause table columns)
   :from      (update-many-from-clause members)
   :where     (update-many-where-clause table)
   :returning :*})

(comment
  (require '[mertonon.generators.net :as gen-net])
  (require '[clojure.test.check.generators :as gen])
  (let [grids [(gen/generate gen-net/generate-grid)
               (gen/generate gen-net/generate-grid)]]
    (sql/format (update-many-q :mertonon.grid
                               [:uuid :version :created-at :updated-at :name :label :optimizer-type :hyperparams]
                               (mapv :uuid grids) grids))))

(defn update-many [{:keys [table columns uuids members row->member]}]
  (if (empty? uuids)
    members
    (->> (update-many-q table columns uuids members) (db/query) (mapv row->member))))

;; -----
;; Delete
;; -----

(defn hard-delete-one-q [table uuid]
  {:delete-from table :where [:= :uuid uuid]})

(defn hard-delete-one [{:keys [table uuid]}]
  (db/query (hard-delete-one-q table uuid)))

(defn hard-delete-many-q [table uuids]
  {:delete-from table :where [:in :uuid uuids]})

(defn hard-delete-many [{:keys [table uuids]}]
  ;; TODO: Catch the footgun of deleting all explicitly instead of implicitly, and cry harder when we find it
  ;; If you skip the assert it's your fault if you shoot your foot off with the delete all sql footgun
  (assert (> (count uuids) 0))
  (if (= (count uuids) 1)
    (hard-delete-one {:table table :uuid (first uuids)})
    (db/query (hard-delete-many-q table uuids))))


;; -----
;; Define default model
;; -----

(defn default-model [{:keys [columns table row->member member->row] :as query-info}]
  {:create-one!       (fn [member] (create-one (assoc query-info :member member)))
   :create-many!      (fn [members] (create-many (assoc query-info :members members)))
   :read-one          (fn [uuid] (select-one (assoc query-info :uuid uuid)))
   ;; Do not forget they don't come sorted!
   :read-where        (fn [where-clause] (select-where (assoc query-info :where-clause where-clause)))
   :read-where-joined (fn [{:keys [where-clause join-tables join-col-edges
                                   raw-table->table table->model]}]
                        (select-where-joined (assoc query-info
                                                    :where-clause     where-clause
                                                    :join-tables      join-tables
                                                    :join-col-edges   join-col-edges
                                                    :raw-table->table raw-table->table
                                                    :table->model     table->model)))
   :read-many         (fn [uuids] (select-many (assoc query-info :uuids uuids)))
   :read-all          (fn [] (select-all query-info))
   :count             (fn [] (count-all query-info))
   :update-one!       (fn [uuid member] (update-one (assoc query-info :uuid uuid :member member)))
   :update-many!      (fn [uuids members] (update-many (assoc query-info :uuids uuids :members members)))
   :hard-delete-one!  (fn [uuid] (hard-delete-one (assoc query-info :uuid uuid)))
   :hard-delete-many! (fn [uuids] (hard-delete-many (assoc query-info :uuids uuids)))
   :row->member       row->member
   :member->row       member->row})
