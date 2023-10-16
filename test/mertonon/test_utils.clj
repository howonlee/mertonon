(ns mertonon.test-utils
  (:require [clojure.data :as cd]
            [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [next.jdbc :as jdbc]
            [mertonon.server.handler :as handlers]
            [mertonon.util.db :as db]
            [mertonon.util.munge :as mun]
            [mertonon.util.registry :as reg]))

;; ---
;; Test cardinality presets
;; ---

(def few
  "1 isn't quite 1 if test fails, which is why 1 is named 'few'"
  1)

(def middle
  20)

(def many
  100)

;; ---
;; Test transaction macro and middleware
;; ---

(def ^:dynamic ^:private *in-test-txn?* false)

(defn do-with-test-txn
  "Impl for with-test-txn"
  [thunk]
  (if *in-test-txn?*
    (thunk)
    (jdbc/with-transaction [txn (db/connpool-singleton)]
      (binding [*in-test-txn?* true
                db/*defined-connection* txn]
        (let [thunk-res (thunk)
              rollback  (.rollback txn)]
          thunk-res)))))

(defmacro with-test-txn
  "Every transaction with this macro is rolled back automatically,
  so just mutate the DB then make your assertions within the scope
  Returns the result of the body, not the rollback result

  Be aware that, say, calling out via HTTP client or something like that
  busts out of the transaction"
  [& body]
  `(do-with-test-txn (fn [] ~@body)))

(defn test-txn-middleware [txn handler]
  ;; middleware closure should have the test txn state,
  ;; _not_ within the middlware handler
  ;; because we want the state to be shared between different requests
  (fn [req]
    (binding [db/*defined-connection* txn]
      (handler req))))

(defn app-with-test-txn
  "Try not to use this in tandem with the with-test-txn macro by itself,
  because it can get real confusing that way"
  [txn]
  (handlers/test-handler [(partial test-txn-middleware txn)]))

;; ---
;; More value-based check for throwing stuff
;; ---

(defn expect-thrown
  "Not a macro, just call it"
  [{:keys [checker curr-fn]} & args]
  (try
    (do
      (apply curr-fn args)
      false)
    (catch Exception e (checker e))))

;; ---
;; Generic CRUD property predicates
;; ---

(defn create-and-generate-consonance
  [{:keys [gen-net model-instance create-one! read-one hard-delete-one! setup]}]
  (let [setup-res  (setup gen-net)
        delete-res (hard-delete-one! (:uuid model-instance))
        create-res (create-one! model-instance)
        read-res   (read-one (:uuid model-instance))]
    (every? true? [(= create-res model-instance)
                   (= read-res model-instance)])))

(defn member->row-round-trip
  [{:keys [gen-net model-instance read-one member->row row->member setup]}]
  (let [setup-res  (setup gen-net)
        read-res   (read-one (:uuid model-instance))]
    (every? true? [(= (->> read-res member->row row->member) read-res)
                   (= read-res model-instance)])))

(defn create-and-read-consonance
  [{:keys [gen-net model-instance create-one! read-one hard-delete-one! setup]}]
  (let [setup-res  (setup gen-net)
        delete-res (hard-delete-one! (:uuid model-instance))
        create-res (create-one! model-instance)
        read-res   (read-one (:uuid model-instance))]
    (= create-res read-res)))

(defn create-one-create-many-consonance
  [{:keys [gen-net model-instances create-one! create-many! hard-delete-many! setup]}]
  (let [setup-res       (setup gen-net)
        delete          (hard-delete-many! (mapv :uuid model-instances))
        create-res      (vec (for [instance model-instances] (create-one! instance)))
        delete          (hard-delete-many! (mapv :uuid model-instances))
        bulk-create-res (create-many! model-instances)]
    (= create-res bulk-create-res)))

(defn update-then-update-back
  [{:keys [gen-net model-instances update-one! setup]}]
  (let [setup-res  (setup gen-net)
        fst-member (first model-instances)
        snd-member (second model-instances)
        fst-update (update-one! (:uuid fst-member) snd-member)
        snd-update (update-one! (:uuid fst-member) fst-member)]
    ;; Dissoc updated-at values because they're not quite exactly the same instant
    (and
      (= (dissoc fst-member :updated-at) (dissoc snd-update :updated-at))
      (not= (fst-member :updated-at) (snd-update :updated-at)))))

(defn update-many-then-update-back
  [{:keys [gen-net model-instances update-many! setup]}]
  (let [setup-res   (setup gen-net)
        ;; Depends rather a lot on immutability of clojure-land stuff
        fst-members (mun/compact [(first model-instances) (second model-instances)])
        snd-members (mun/compact [(second model-instances) (first model-instances)])
        fst-update  (update-many! (mapv :uuid fst-members) snd-members)
        snd-update  (update-many! (mapv :uuid fst-members) fst-members)]
    ;; Dissoc updated-at values because they're not quite exactly the same instant
    ;; Note how fst-members and snd-members can't be sorted, which is why we compare hash-sets
    (and
      (= (apply hash-set (map #(dissoc % :updated-at) fst-members))
         (apply hash-set (map #(dissoc % :updated-at) snd-update)))
      (not= (mapv :updated-at fst-members) (mapv :updated-at snd-update)))))


(defn read-one-read-many-consonance
  [{:keys [gen-net model-instances read-one read-many setup]}]
  (let [setup-res   (setup gen-net)
        ;; Important to note that there is no guarantee of sorting
        indiv-reads (->> (for [instance model-instances] (read-one (:uuid instance)))
                         vec
                         flatten
                         (sort-by :uuid)
                         vec)
        bulk-read   (->> (read-many (mapv :uuid model-instances))
                         (sort-by :uuid)
                         vec)]
    (= indiv-reads bulk-read)))

(defn read-one-read-where-consonance
  [{:keys [gen-net model-instances read-one read-where setup]}]
  (let [setup-res   (setup gen-net)
        ;; Important to note that there is no guarantee of sorting
        indiv-reads (->> (for [instance model-instances] (read-one (:uuid instance)))
                         vec
                         flatten
                         (sort-by :uuid)
                         vec)
        bulk-read   (->> (read-where [:in :uuid (mapv :uuid model-instances)])
                         (sort-by :uuid)
                         vec)]
    (= indiv-reads bulk-read)))

(defn create-and-delete-inversion
  [{:keys [gen-net model-instance create-one! hard-delete-one! read-all setup]}]
  (let [setup-res    (setup gen-net)
        state-1      (->> (read-all) (sort-by :uuid) vec)
        delete-res   (hard-delete-one! (:uuid model-instance))
        create-res   (create-one! model-instance)
        state-2      (->> (read-all) (sort-by :uuid) vec)]
    (= state-1 state-2)))

(defn delete-one-delete-many-consonance
  [{:keys [gen-net model-instances create-many! hard-delete-one!
           hard-delete-many! read-all setup]}]
  (let [setup-res     (setup gen-net)
        ;; for can get sometimes lazy...
        indiv-deletes (vec (for [instance model-instances] (hard-delete-one! (:uuid instance))))
        state-1       (read-all)
        create-res-2  (create-many! model-instances)
        bulk-deletes  (hard-delete-many! (mapv :uuid model-instances))
        state-2       (read-all)]
    (= state-1 state-2)))

;; ---
;; Generators and dealing with generates
;; ---

(defn maybe-strip-schema [table-name]
  (->> (str/split (name table-name) #"\.")
       last
       keyword))

(defn generates->members
  [generates table-name]
  (let [stripped-table (maybe-strip-schema table-name)]
    (cond
      (vector? generates) (flatten (mapv stripped-table generates))
      :else               (generates stripped-table))))

(defn generates->member
  [generates table-name]
  (let [stripped-table (maybe-strip-schema table-name)]
    (cond (not (contains? generates stripped-table))
          nil
          (= stripped-table :weights)
          (first (flatten (generates->members generates stripped-table)))
          :else
          (first (generates->members generates stripped-table)))))

(defn setup-generates!
  [tables-under-test]
  (fn [generates]
    (let [all-members (for [table tables-under-test]
                        (when (generates->members generates table)
                          [table (generates->members generates table)]))
          insert-all! (doall
                        (for [[table members] all-members]
                          (when members
                            (((reg/table->model table) :create-many!) (flatten [members])))))]
      nil)))

(defn table-and-generates
  [tables-under-test & [banset]]
  (let [curr-banset (or banset #{})]
        (gen/let [table     (gen/such-that
                              #(not (contains? banset %))
                              (gen/elements tables-under-test))
                  generates (reg/table->generator table)]
          [table generates])))
