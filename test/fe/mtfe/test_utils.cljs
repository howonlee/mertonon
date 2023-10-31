(ns mtfe.test-utils)

;; ---
;; Test cardinality presets
;; BE test cardinalities are set separately
;; ---

(def few
  "1 isn't quite 1 if test fails, which is why 1 is named 'few'"
  1)

(def middle
  20)

(def many
  100)

  
