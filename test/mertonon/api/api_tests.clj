(ns mertonon.api.api-tests
  "Consolidated API tests for API endpoints that are just CRUD on tables"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.server.handler :as handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.db :as db]
            [mertonon.util.munge :as mun]
            [mertonon.util.registry :as reg]
            [mertonon.util.io :as uio]))

;; ---
;; Preliminaries
;; ---

(def setup-tables
  "Order is linearized version of DAG implied by fkeys.
  Better not have cycles in our fkey dependencies!"
  [:mertonon.grids :mertonon.layers :mertonon.cost-objects :mertonon.entries
   :mertonon.weightsets :mertonon.weights :mertonon.inputs :mertonon.losses

   :mertonon.mt-users])

(def tables-under-test
  "Mess with this when you have bugs localized to one model"
  setup-tables)

;; Multiple times, I've changed tables-under-test to be one table then forgotten to change it back
;; Make sure we didn't forget to change it back
(deftest testing-all-tables
  (is (= (count setup-tables) (count tables-under-test))))

(def endpoints-under-test
  {:mertonon.grids           "/api/v1/grid/"
   :mertonon.layers          "/api/v1/layer/"
   :mertonon.cost-objects    "/api/v1/cost_object/"
   :mertonon.entries         "/api/v1/entry/"
   :mertonon.weightsets      "/api/v1/weightset/"
   :mertonon.weights         "/api/v1/weight/"
   :mertonon.losses          "/api/v1/loss/"
   :mertonon.inputs          "/api/v1/input/"
   :mertonon.mt-users        "/api/v1/mt_user/"})

(defn encode-to-stream [inp]
  (io/input-stream (.getBytes (json/write-str inp))))

(defn process-app-response [resp]
  (->> resp
       :body
       uio/maybe-slurp
       uio/maybe-json-decode))

(defn test-inp
  [table generates curr-db-txn]
  (let [app             (tu/app-with-test-txn curr-db-txn)
        endpoint        (endpoints-under-test table)
        elem            (tu/generates->member generates table)
        indiv-endpoint  #(format "%s%s" endpoint (or (:uuid %) %))
        member->row     ((reg/table->model table) :member->row)
        row->member     ((reg/table->model table) :row->member)

        ;; NB: slurp is not idempotent! slurp is stateful!
        api-create-one!  (fn [member]
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
                             (mapv row->member processed)))
        api-update-one!  (fn [_ member]
                           (let [res       (app {:uri endpoint :request-method :put :body-params member})
                                 processed (process-app-response res)]
                             (if (seq processed)
                               (row->member processed)
                               processed)))
        api-update-many! (fn [_ member]
                           (let [res       (app {:uri endpoint :request-method :put :body-params member})
                                 processed (process-app-response res)]
                             (->> processed
                                  mun/compact
                                  (mapv row->member))))]
    {:gen-net           generates
     :model-instance    elem
     :model-instances   (tu/generates->members generates table)
     :create-one!       api-create-one!
     :create-many!      api-create-many!
     :read-one          api-read-one
     :read-many         api-read-many
     :read-all          api-read-all
     :update-one!       api-update-one!
     :update-many!      api-update-many!
     :hard-delete-one!  #(app {:uri (indiv-endpoint %) :request-method :delete})
     :hard-delete-many! #(app {:uri endpoint :request-method :delete :body (encode-to-stream %)})
     :member->row       member->row
     :row->member       row->member
     :setup             (tu/setup-generates! tables-under-test)}))

(def table-and-generates (tu/table-and-generates tables-under-test))

;; ---
;; Actual Tests
;; ---

(defspec create-and-generate-consonance
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-generate-consonance
                                    (test-inp table generates db/*defined-connection*)))))

(defspec member->row-round-trip
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/member->row-round-trip
                                    (test-inp table generates db/*defined-connection*)))))

(defspec create-and-read-consonance
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-read-consonance
                                    (test-inp table generates db/*defined-connection*)))))

(defspec create-one-create-many-consonance
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-one-create-many-consonance
                                    (test-inp table generates db/*defined-connection*)))))

(defspec read-one-read-many-consonance
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance
                    (test-inp table generates db/*defined-connection*)))))

;; API will not have arbitrary read-where semantics. That's a terrible idea.

(defspec update-then-update-back
  tu/middle
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-then-update-back (test-inp table generates db/*defined-connection*)))))

(defspec update-many-then-update-back
  tu/middle
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-many-then-update-back (test-inp table generates db/*defined-connection*)))))

(defspec create-and-delete-inversion
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-delete-inversion
                                    (test-inp table generates db/*defined-connection*)))))

(defspec delete-one-delete-many-consonance
  tu/middle
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance
                                    (test-inp table generates db/*defined-connection*)))))

(comment (run-tests))
