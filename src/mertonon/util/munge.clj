(ns mertonon.util.munge
  "Extremely miscellaneous data munging. Pure functions only please")

(defn compact
  "Filter out nil members"
  [coll]
  (vec (keep identity coll)))


