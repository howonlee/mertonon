(ns mertonon.api.util
  "API utilities"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [taoensso.timbre :as timbre :refer [log]]
            [tick.core :as t]))

(defn create-model [curr-model]
  (fn [match]
    (let [body            (-> match :body-params uio/maybe-slurp)
          model-or-models (-> body uio/maybe-json-decode walk/keywordize-keys)
          ;; TODO: sanitize or do something so I can log stuff willy-nilly
          res             (if (map? model-or-models)
                            ((curr-model :create-one!) model-or-models)
                            ((curr-model :create-many!) model-or-models))]
      {:status 200 :body (json/write-str res)})))

(defn get-models [curr-model]
  (fn [match]
    (let [uuid-list (mapv uutils/uuid (-> match :body uio/maybe-slurp uio/maybe-json-decode))
          res       (if (empty? uuid-list)
                      ((curr-model :read-all))
                      ((curr-model :read-many) uuid-list))]
      {:status 200 :body (json/write-str res)})))

(defn get-model [curr-model]
  (fn [match]
    (let [curr-uuid (uutils/uuid (-> match :path-params :uuid))]
      {:status 200 :body (json/write-str
                           ((curr-model :read-one) curr-uuid))})))

;; TODO: update percolated up to API

(defn delete-model [curr-model]
  (fn [match]
    (let [curr-uuid (uutils/uuid (-> match :path-params :uuid))]
      {:status 200 :body (json/write-str
                           ((curr-model :hard-delete-one!) curr-uuid))})))

(defn delete-models [curr-model]
  (fn [match]
    (let [curr-uuids (mapv uutils/uuid (-> match :body uio/maybe-slurp uio/maybe-json-decode))]
      {:status 200 :body (json/write-str
                           ((curr-model :hard-delete-many!) curr-uuids))})))
