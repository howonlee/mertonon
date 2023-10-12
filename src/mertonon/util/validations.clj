(ns mertonon.util.validations
  "Bunch of generic validations and validation templates"
  (:require [mertonon.util.queries :as q]
            [mertonon.util.registry :as registry]
            ))

;; ---
;; Doing the validations
;; ---

(defn validate
  [inp validations]
  (let [reses  (vec (for [curr-validation validations]
                      (let [validation-res (curr-validation inp)]
                        (cond
                          (and (some? validation-res) (map? validation-res))
                          validation-res
                          (and (some? validation-res) (keyword? validation-res))
                          {validation-res []}
                          :else
                          {}))))
        errors (apply (partial merge-with into) reses)]
    errors))

(defn throw-if-invalid!
  [inp validations]
  (let [res (validate inp validations)]
    (if (seq res)
      (throw (ex-info "Validations failed." res))
      nil)))

;; ---
;; Validations
;; ---

(defn table-count-check
  [table-model pred-count curr-keyword]
  (fn [req] 
    (if (= ((table-model :count)) pred-count)
      nil
      curr-keyword)))

(defn join-count-check
  [first-table-model snd-table-name fkey-vec]
  (fn [req]
    (let [join-res ((first-table-model :read-where-joined)
                    {:join-tables      [snd-table-name]
                     :join-col-edges   [fkey-vec]
                     :raw-table->table registry/raw-table->table
                     :table->model     registry/table->model})
          printo   (println join-res)]
      nil)))
