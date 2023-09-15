(ns mertonon.server.middleware.validations
  "Takes a bunch of validations with their own semantics and applies them all to a request"
  (:require [reitit.core :as r]))

(defn do-validations
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

(defn wrap-mertonon-validations
  "You never have just the one, so we get a seq of them"
  ([handler]
   (wrap-mertonon-validations handler []))
  ([handler validations]
   (fn [request]
     (let [error-results (do-validations request validations)]
       (if (seq error-results)
         {:status 400 :body error-results}
         (handler wrapped-request))))))
