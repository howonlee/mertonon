(ns mtfe.generators.navs
  "Generate FE navigation things - routes, paths, etc"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.routes :as main-routes]))

(defn gen-nav-route* [] (gen/elements main-routes/main-routes))

(def gen-nav-route (gen-nav-route*))

(defn nav-route->nav-path
  [nav-route tables]
  nil)

(defn gen-nav-path* [] nil)

(comment
  (try
    (gen/generate gen-nav-path)
    (catch :default e (println e))))
