(ns mtfe.generators.navs
  "Generate FE navigation things - routes, paths, etc"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.generators.net-store :as net-store]
            [mtfe.routes :as main-routes]))

(defn allowed-members [all-members]
  (filter
    (fn [[_ data]]
      (not (data :exclude-from-gen?)))
    all-members))

(def gen-nav-route (gen/elements (allowed-members main-routes/main-routes)))

(defn nav-route->nav-path
  [nav-route curr-store]
  (let [[path params] nav-route
        ]
    nil))

(defn gen-nav-path* [] nil)

(comment
  (try
    (gen/sample gen-nav-route 100)
    (catch :default e (println e))))
