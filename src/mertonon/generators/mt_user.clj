(ns mertonon.generators.mt-users
  "Generating mertonon users and authentication methods"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [loom.graph :as graph]
            [loom.alg :as graph-alg]
            [loom.attr :as graph-attr]
            [loom.alg-generic :as graph-alg-generic]))

(defn generate-mt-user*
  [{:keys [name-type] :as params}]
  (gen/let [mt-user-uuid     gen/uuid
            mt-user-email    (gen/data-gen-mt-user-emails name-type)
            mt-user-username (gen-data/gen-mt-user-usernames name-type)]
    {:mt-users [(mtc/->MtUser mt-user-uuid
                              mt-user-email
                              mt-user-username)]}))

;; (def generate-mt-user
;;   some crap)
