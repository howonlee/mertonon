(ns mertonon.server.middleware.validations
  "Takes a bunch of validations with their own semantics and applies them all to a request"
  (:require [reitit.core :as r]))

(defn do-validations!
  "Can also be called directly"
  [request validations]
  (let [reses  (vec (for [curr-validation validations]
                      (let [validation-res (curr-validation request)]
                        (cond
                          (and (some? validation-res) (map? validation-res))
                          validation-res
                          (and (some? validation-res) (keyword? validation-res))
                          {validation-res []}
                          :else
                          {}))))
        errors (apply (partial merge-with into) reses)]
    errors))

(defn mt-validation-middleware
  "You never have just the one, so we get a seq of them"
  [request validations]
  (fn [request]
    ;;;;
    ;;;;
    ;;;;
    (do-validations! state validations)))

