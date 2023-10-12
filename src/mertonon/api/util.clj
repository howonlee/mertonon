(ns mertonon.api.util
  "API utilities"
  (:require [clojure.walk :as walk]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]
            [taoensso.timbre :as timbre :refer [log]]
            [tick.core :as t]))

;; ---
;; Match munging
;; ---

(defn body-params
  [match]
  (-> match
      :body-params
      uio/maybe-slurp
      uio/maybe-json-decode
      walk/keywordize-keys))

(defn path-uuid
  "Lots of times we have an endpoint that has one path-param,
  which is a uuid for something. Get that path-param as uuid"
  [match]
  (-> match
      :path-params
      :uuid
      uutils/uuid))

(defn body-uuids
  [match]
  (->> match
       :body
       uio/maybe-slurp
       uio/maybe-json-decode
       (mapv uutils/uuid)))

;; ---
;; Generic endpoints
;; ---

(defn create-model [curr-model & [config]]
  (fn [match]
    (let [model-or-models            (body-params match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))
          ;; TODO: sanitize or do something so I can log stuff willy-nilly
          res             (if (map? model-or-models)
                            ((curr-model :create-one!) model-or-models)
                            ((curr-model :create-many!) model-or-models))]
      {:status 200 :body res})))

(defn get-models [curr-model & [config]]
  (fn [match]
    (let [uuid-list (body-uuids match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))
          res       (if (empty? uuid-list)
                      ((curr-model :read-all))
                      ((curr-model :read-many) uuid-list))]
      {:status 200 :body res})))

(defn get-model [curr-model & [config]]
  (fn [match]
    (let [curr-uuid (path-uuid match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))]
      {:status 200 :body ((curr-model :read-one) curr-uuid)})))

(defn update-model [curr-model & [config]]
  (fn [match]
    (let [model-or-models (body-params match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))
          res             (if (map? model-or-models)
                            ((curr-model :update-one!) (:uuid model-or-models) model-or-models)
                            ((curr-model :update-many!) (mapv :uuid model-or-models) model-or-models))]
      {:status 200 :body res})))

(defn delete-model [curr-model & [config]]
  (fn [match]
    (let [curr-uuid (path-uuid match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))]
      {:status 200 :body ((curr-model :hard-delete-one!) curr-uuid)})))

(defn delete-models [curr-model & [config]]
  (fn [match]
    (let [curr-uuids (body-uuids match)
          {validations :validations} config
          check!                     (if (seq validations)
                                       (uvals/throw-if-invalid! match validations))]
      {:status 200 :body ((curr-model :hard-delete-many!) curr-uuids)})))
