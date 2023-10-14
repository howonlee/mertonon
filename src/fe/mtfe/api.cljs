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
(defn generator-layer [uuid] (gstring/format "%s/api/v1/generators/layers/%s" host uuid))
(defn generator-weightset [uuid] (gstring/format "%s/api/v1/generators/weightsets/%s" host uuid))
(defn generator-weight [uuid] (gstring/format "%s/api/v1/generators/weights/%s" host uuid))
(defn generator-cost-object [uuid] (gstring/format "%s/api/v1/generators/cost_objects/%s" host uuid))
(defn generator-grid [] (gstring/format "%s/api/v1/generators/grids" host))
(defn generator-grad [] (gstring/format "%s/api/v1/generators/grad" host))

;; ---
;; Ordinary network CRUD APIs
;; ---

(defn grid [] (gstring/format "%s/api/v1/grid/" host))
(defn grid-grad [] (gstring/format "%s/api/v1/grid/_/grad" host))
(defn grid-member [uuid] (gstring/format "%s/api/v1/grid/%s" host uuid))
(defn grid-view [uuid] (gstring/format "%s/api/v1/grid/%s/view" host uuid))
(defn grid-dump [uuid] (gstring/format "%s/api/v1/grid/%s/dump" host uuid))
(defn grid-graph [uuid] (gstring/format "%s/api/v1/grid/%s/graph" host uuid))

(defn layer [] (gstring/format "%s/api/v1/layer" host))
(defn layer-member [uuid] (gstring/format "%s/api/v1/layer/%s" host uuid))
(defn layer-view [uuid] (gstring/format "%s/api/v1/layer/%s/view" host uuid))

(defn cost-object [] (gstring/format "%s/api/v1/cost_object" host))
(defn cost-object-member [uuid] (gstring/format "%s/api/v1/cost_object/%s" host uuid))
(defn cost-object-view [uuid] (gstring/format "%s/api/v1/cost_object/%s/view" host uuid))

(defn entry [] (gstring/format "%s/api/v1/entry" host))
(defn entry-member [uuid] (gstring/format "%s/api/v1/entry/%s" host uuid))
(defn entry-view [uuid] (gstring/format "%s/api/v1/entry/%s/view" host uuid))

(defn weightset [] (gstring/format "%s/api/v1/weightset" host))
(defn weightset-member [uuid] (gstring/format "%s/api/v1/weightset/%s" host uuid))
(defn weightset-view [uuid] (gstring/format "%s/api/v1/weightset/%s/view" host uuid))

(defn weight [] (gstring/format "%s/api/v1/weight" host))
(defn weight-member [uuid] (gstring/format "%s/api/v1/weight/%s" host uuid))
(defn weight-view [uuid] (gstring/format "%s/api/v1/weight/%s/view" host uuid))

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

(defn mt-user [] (gstring/format "%s/api/v1/mt_user/" host))
(defn mt-user-password-login []
  (gstring/format "%s/api/v1/mt_user/_/password_login" host))
(defn mt-user-member [uuid] (gstring/format "%s/api/v1/mt_user/%s" host uuid))
(defn mt-user-member-password-login [uuid]
  (gstring/format "%s/api/v1/mt_user/%s/password_login" host uuid))

(defn password-login [] (gstring/format "%s/api/v1/password_login/" host))
(defn password-login-member [uuid] (gstring/format "%s/api/v1/password_login/%s" host uuid))

(defn curr-mt-user
  "The mt user that is currently authed"
  []
  (gstring/format "%s/api/v1/mt_user/_/me" host))

(defn session [] (gstring/format "%s/api/v1/session/" host))
