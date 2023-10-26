(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [mtfe.core :as mtfe]
            [mtfe.sidebar.core :as sidebar]
            ))
(def gen-nav-route (gen/elements mtfe/main-routes))

(def gen-sidebar-route (gen/elements sidebar/sidebar-routes))

(defn gen-nav-path* []
  nil)

(defn gen-sidebar-path* []
  nil)

(defn gen-match* []
  nil)

(def gen-match (gen-match*)
  nil)

(defn gen-selection* []
  nil)

(defn gen-dag-selection* []
  nil)

(defn gen-error* []
  nil)

(defn gen-api-error* []
  nil)
