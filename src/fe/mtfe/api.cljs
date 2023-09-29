(ns mtfe.api
  "API endpoint namespace"
  (:require [goog.string :as gstring]))

(def host "Current host, terribly injected from backend template handler"
  (.. js/window -mertonon_host))

;; ---
;; Generator APIs
;; ---

(defn generator-net [] (gstring/format "%s/api/v1/generators/net" host))
(defn generator-graph [] (gstring/format "%s/api/v1/generators/graph" host))
(defn generatorLayerApi [uuid] (gstring/format "%s/api/v1/generators/layers/%s" host uuid))
(defn generatorWeightsetApi [uuid] (gstring/format "%s/api/v1/generators/weightsets/%s" host uuid))
(defn generatorWeightApi [uuid] (gstring/format "%s/api/v1/generators/weights/%s" host uuid))
(defn generatorCostObjectApi [uuid] (gstring/format "%s/api/v1/generators/cost_objects/%s" host uuid))
(defn generatorGridApi [] (gstring/format "%s/api/v1/generators/grids" host))
(defn generatorGradApi [] (gstring/format "%s/api/v1/generators/grad" host))

;; ---
;; Ordinary network CRUD APIs
;; ---

(defn grid [] (gstring/format "%s/api/v1/grid/" host))
(defn grid-grad [] (gstring/format "%s/api/v1/grid/_/grad" host))
(defn grid-member [uuid] (gstring/format "%s/api/v1/grid/%s" host uuid))
(defn grid-view [uuid] (gstring/format "%s/api/v1/grid/%s/view" host uuid))
(defn grid-dump [uuid] (gstring/format "%s/api/v1/grid/%s/dump" host uuid))
(defn grid-graph [uuid] (gstring/format "%s/api/v1/grid/%s/graph" host uuid))

(defn layerApi [] (gstring/format "%s/api/v1/layer" host))
(defn layerMemberApi [uuid] (gstring/format "%s/api/v1/layer/%s" host uuid))
(defn layerViewApi [uuid] (gstring/format "%s/api/v1/layer/%s/view" host uuid))

(defn costObjectApi [] (gstring/format "%s/api/v1/cost_object" host))
(defn costObjectMemberApi [uuid] (gstring/format "%s/api/v1/cost_object/%s" host uuid))
(defn costObjectViewApi [uuid] (gstring/format "%s/api/v1/cost_object/%s/view" host uuid))

(defn entryApi [] (gstring/format "%s/api/v1/entry" host))
(defn entryMemberApi [uuid] (gstring/format "%s/api/v1/entry/%s" host uuid))
(defn entryViewApi [uuid] (gstring/format "%s/api/v1/entry/%s/view" host uuid))

(defn weightsetApi [] (gstring/format "%s/api/v1/weightset" host))
(defn weightsetMemberApi [uuid] (gstring/format "%s/api/v1/weightset/%s" host uuid))
(defn weightsetViewApi [uuid] (gstring/format "%s/api/v1/weightset/%s/view" host uuid))

(defn weightApi [] (gstring/format "%s/api/v1/weight" host))
(defn weightMemberApi [uuid] (gstring/format "%s/api/v1/weight/%s" host uuid))
(defn weightViewApi [uuid] (gstring/format "%s/api/v1/weight/%s/view" host uuid))

(defn input [] (gstring/format "%s/api/v1/input" host))
(defn input-member [uuid] (gstring/format "%s/api/v1/input/%s" host uuid))
(defn input-view [uuid] (gstring/format "%s/api/v1/input/%s/view" host uuid))

(defn loss [] (gstring/format "%s/api/v1/loss" host))
(defn loss-member [uuid] (gstring/format "%s/api/v1/loss/%s" host uuid))
(defn loss-view [uuid] (gstring/format "%s/api/v1/loss/%s/view" host uuid))

(defn allocation-cue [] (gstring/format "%s/api/v1/allocation_cue" host))

;; ---
;; Non-net CRUD APIs
;; ---

(defn intro [] (gstring/format "%s/api/v1/intro/" host))

(defn curr-mt-user [] (gstring/format "%s/api/v1/mt_user/_/me" host))

(defn session [] (gstring/format "%s/api/v1/session/" host))
