(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [mtfe.core :as mtfe]
            [mtfe.sidebars.core :as sidebars]))

(defn gen-nav-path* [] nil)

(defn gen-sidebar-path* []
  nil)

(defn gen-match* []
  nil)

(defn gen-selection* []
  nil)

(defn gen-dag-selection* []
  nil)

(defn gen-error* []
  nil)

(defn gen-api-error* []
  nil)

(comment
  (try (println tc)
       (catch js/Error e
         (.log js/console e))))
