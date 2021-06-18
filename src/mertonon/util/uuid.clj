(ns mertonon.util.uuid
  "Utilities for UUID stuff")

(defn uuid
  "Generates UUID if given nothing.
  
  Returns UUID corresponding to str if given a str. If given a uuid, just returns input"
  ([]
   (java.util.UUID/randomUUID))
  ([uuid-str]
   (if (= (type uuid-str) java.util.UUID)
     uuid-str
     (java.util.UUID/fromString uuid-str))))
