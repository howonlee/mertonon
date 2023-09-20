(ns mertonon.util.validations
  "Bunch of generic validations and validation templates"
  ;; (:require nil)
  )

(defn table-count-check
  [table-model pred-count curr-keyword]
  (fn [req] 
    (if (= ((table-model :count)) pred-count)
      nil
      curr-keyword)))
