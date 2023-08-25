(ns mertonon.api.api-tests
  "Consolidated API tests"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.server.handler :as app-handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]
            [mertonon.util.io :as uio]))

(def gen-nets
  (gen/vector aug-net-gen/net-enriched-with-entries 2 4))

(def setup-tables
  "Order is linearized version of DAG implied by fkeys.
  Better not have cycles in our fkey dependencies!"
  [:mertonon.grids :mertonon.layers :mertonon.cost-objects :mertonon.entries
   :mertonon.weightsets :mertonon.weights :mertonon.inputs :mertonon.losses])

(def tables-under-test
  "Mess with this when you have bugs localized to one model"
  setup-tables)

;; Multiple times, I've changed tables-under-test to be one table then forgotten to change it back
;; Make sure we didn't forget to change it back
(deftest testing-all-tables
  (is (= (count setup-tables) (count tables-under-test))))

(def endpoints-under-test
  {:mertonon.grids        "/api/v1/grid/"
   :mertonon.layers       "/api/v1/layer/"
   :mertonon.cost-objects "/api/v1/cost_object/"
   :mertonon.entries      "/api/v1/entry/"
   :mertonon.weightsets   "/api/v1/weightset/"
   :mertonon.weights      "/api/v1/weight/"
   :mertonon.losses       "/api/v1/loss/"
   :mertonon.inputs       "/api/v1/input/"})

(defn setup! [net]
  (let [all-members (for [table setup-tables]
                      [table (tu/generates->members net table)])
        ;; `vec` to proc the side effect
        insert-all! (vec (for [[table members] all-members]
                      (((reg/table->model table) :create-many!) (flatten [members]))))]
    nil))

(defn encode-to-stream [inp]
  (io/input-stream (.getBytes (json/write-str inp))))

(defn process-app-response [resp]
  (->> resp
       :body
       uio/maybe-slurp
       uio/maybe-json-decode))

(defn test-inp [net table]
  (let [app             (app-handler/app-handler)
        endpoint        (endpoints-under-test table)
        elem            (tu/generates->member net table)
        indiv-endpoint  #(format "%s%s" endpoint (or (:uuid %) %))
        member->row     ((reg/table->model table) :member->row)
        row->member     ((reg/table->model table) :row->member)

        ;; NB: slurp is not idempotent! slurp is stateful!
        api-create-one! (fn [member]
                          (let [res       (app {:uri endpoint :request-method :post :body-params member})
                                processed (process-app-response res)]
                            (row->member processed)))
        api-create-many! (fn [member]
                           (let [res       (app {:uri endpoint :request-method :post :body-params member})
                                 processed (process-app-response res)]
                             (mapv row->member processed)))
        api-read-one     (fn [member]
                           (let [res       (app {:uri (indiv-endpoint member) :request-method :get})
                                 processed (process-app-response res)]
                             (row->member processed)))
        api-read-many    (fn [member]
                           (let [res       (app {:uri endpoint :request-method :get :body (encode-to-stream member)})
                                 processed (process-app-response res)]
                             (mapv row->member processed)))
        api-read-all     (fn []
                           (let [res       (app {:uri endpoint :request-method :get})
                                 processed (process-app-response res)]
                             (mapv row->member processed)))]
    {:gen-net           net
     :model-instance    elem
     :model-instances   (tu/generates->members net table)
     :create-one!       api-create-one!
     :create-many!      api-create-many!
     :read-one          api-read-one
     :read-many         api-read-many
     :read-all          api-read-all
     :hard-delete-one!  #(app {:uri (indiv-endpoint %) :request-method :delete})
     :hard-delete-many! #(app {:uri endpoint :request-method :delete :body (encode-to-stream %)})
     :member->row       member->row
     :row->member       row->member
     :setup             setup!}))

(defspec create-and-generate-consonance
  100
  (prop/for-all [net   aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-generate-consonance (test-inp net table)))))

(defspec member->row-round-trip
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/member->row-round-trip (test-inp net table)))))

(defspec create-and-read-consonance
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-read-consonance (test-inp net table)))))

(defspec create-one-create-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-one-create-many-consonance (test-inp nets table)))))

(defspec read-one-read-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance (test-inp nets table)))))

;; API will not have arbitrary read-where semantics. That's a terrible idea.

(defspec create-and-delete-inversion
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-delete-inversion (test-inp net table)))))

(defspec delete-one-delete-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance (test-inp nets table)))))

(comment (create-and-generate-consonance))
