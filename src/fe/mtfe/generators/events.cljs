(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.routes :as main-routes]))

(defn gen-nav-route* [] (gen/elements main-routes/main-routes))

(def gen-nav-route (gen-nav-route*))

(defn gen-nav-path* [] nil)

(comment
  (try
    (gen/generate gen-nav-path)
    (catch :default e (println e))))
