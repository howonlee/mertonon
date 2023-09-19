(ns mertonon.util.validations
  "Bunch of generic validations and validation templates"
  ;; (:require nil)
  )

(defn table-count-check
  [table-model pred-count curr-keyword]
  (fn [req] 
    (if (= ((table-model :count)) pred-count)
      (do
        (println "fuck we passed")
        nil)
      (do
        (println "fuck we failed")
        (println ((table-model :count)))
        curr-keyword))))
