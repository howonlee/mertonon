(ns mertonon.util.io
  "Miscellaneous IO utilities, and extension of the json protocols"
  (:require [clojure.data.json :as json]
            [clojure.core.matrix :as cm]
            [clojure.string :as str]
            [tick.core :as t]))

;; ---
;; JSONWriter protocol implementations for different stuff that comes in handly
;; ---

(defn- write-datetime [x out options]
  (#'clojure.data.json/write-instant (t/instant x) out options))

(extend java.time.LocalDateTime clojure.data.json/JSONWriter
  ;; This is their implementation lol
  {:-write write-datetime})

(defn- write-array [x out options]
  (#'clojure.data.json/write-array (cm/to-vector x) out options))

;; ndarray loaded implementation is fully dynamic, so we have to eagerly load it to extend that JSONWriter
(defn load-array-impl! []
  (let
    [member (#'clojure.core.matrix.implementations/load-implementation :ndarray)
     _      (extend (class member) clojure.data.json/JSONWriter {:-write write-array})]))

(defn maybe-slurp [str-or-iostream]
  (cond (instance? java.io.InputStream str-or-iostream)
        (slurp str-or-iostream)
        :else
        str-or-iostream))

(defn maybe-json-decode [str-or-map]
  (cond (string? str-or-map)
        ;; Cheshire behaved differently with empty string, mimicking that behavior here
        (if (empty? (str/trim str-or-map))
          {}
          (json/read-str str-or-map))
        :else
        str-or-map))

(defn maybe-flatten
  "Sometimes results come in vecs of hashes, sometimes in vecs of vecs of hashes in case of weights.

  Canonicalize to flat vecs of hashes. Can't use normal flattening because we still want a vec of hashes"
  [ls]
  (if (or (list? (first ls))
          (vector? (first ls)))
    (apply concat ls)
    ls))

(defn maybe-json-encode [str-or-map]
  (cond (string? str-or-map)
        str-or-map
        :else
        (json/write-str str-or-map)))

(defn maybe-parse-int [int-or-str]
  (cond (string? int-or-str)
        (Integer/parseInt int-or-str)
        :else
        int-or-str))

(defn round-to-four
  "Rounds 'x' to 4 decimal places"
  [x]
  (->> x
       bigdec
       (#(.setScale % 4 java.math.RoundingMode/HALF_UP))))
