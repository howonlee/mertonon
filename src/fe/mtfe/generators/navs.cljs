(ns mtfe.generators.navs
  "Generate FE navigation things - routes, paths, etc"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.routes :as main-routes]))

(defn allowed-members [all-members]
  (filter
    (fn [[_ data]]
      (not (data :exclude-from-gen?)))
    all-members))

(defn gen-nav-route* [] (gen/elements (allowed-members main-routes/main-routes)))

(def gen-nav-route (gen-nav-route*))

(defn nav-route->nav-path
  [nav-route tables]
  nil)

(defn gen-nav-path* [] nil)

(comment
  (try
    (gen/sample gen-nav-route 100)
    (catch :default e (println e))))
