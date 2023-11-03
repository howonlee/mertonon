(ns mtfe.api
  "API endpoint namespace"
  (:require [goog.string :as gstring]
            [goog.string.format]))

(def host "Current host, terribly injected from backend template handler"
  (or (.. js/window -mertonon_host) "http://localho.st:5036"))

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
(defn ^{:table :grids} grid-member  [uuid] (gstring/format "%s/api/v1/grid/%s" host uuid))
(defn ^{:table :grids} grid-view [uuid] (gstring/format "%s/api/v1/grid/%s/view" host uuid))
(defn ^{:table :grids} grid-dump [uuid] (gstring/format "%s/api/v1/grid/%s/dump" host uuid))
(defn ^{:table :grids} grid-graph [uuid] (gstring/format "%s/api/v1/grid/%s/graph" host uuid))

(defn layer [] (gstring/format "%s/api/v1/layer" host))
(defn ^{:table :layers} layer-member [uuid] (gstring/format "%s/api/v1/layer/%s" host uuid))
(defn ^{:table :layers} layer-view [uuid] (gstring/format "%s/api/v1/layer/%s/view" host uuid))

(defn cost-object [] (gstring/format "%s/api/v1/cost_object" host))
(defn ^{:table :cost-objects} cost-object-member [uuid] (gstring/format "%s/api/v1/cost_object/%s" host uuid))
(defn ^{:table :cost-objects} cost-object-view [uuid] (gstring/format "%s/api/v1/cost_object/%s/view" host uuid))

(defn entry [] (gstring/format "%s/api/v1/entry" host))
(defn ^{:table :entries} entry-member [uuid] (gstring/format "%s/api/v1/entry/%s" host uuid))
(defn ^{:table :entries} entry-view [uuid] (gstring/format "%s/api/v1/entry/%s/view" host uuid))

(defn weightset [] (gstring/format "%s/api/v1/weightset" host))
(defn ^{:table :weightsets} weightset-member [uuid] (gstring/format "%s/api/v1/weightset/%s" host uuid))
(defn ^{:table :weightsets} weightset-view [uuid] (gstring/format "%s/api/v1/weightset/%s/view" host uuid))

(defn weight [] (gstring/format "%s/api/v1/weight" host))
(defn ^{:table :weights} weight-member [uuid] (gstring/format "%s/api/v1/weight/%s" host uuid))
(defn ^{:table :weights} weight-view [uuid] (gstring/format "%s/api/v1/weight/%s/view" host uuid))

(defn input [] (gstring/format "%s/api/v1/input" host))
(defn ^{:table :inputs} input-member [uuid] (gstring/format "%s/api/v1/input/%s" host uuid))
(defn ^{:table :inputs} input-view [uuid] (gstring/format "%s/api/v1/input/%s/view" host uuid))

(defn loss [] (gstring/format "%s/api/v1/loss" host))
(defn ^{:table :losses} loss-member [uuid] (gstring/format "%s/api/v1/loss/%s" host uuid))
(defn ^{:table :losses} loss-view [uuid] (gstring/format "%s/api/v1/loss/%s/view" host uuid))

(defn allocation-cue [] (gstring/format "%s/api/v1/allocation_cue" host))

;; ---
;; Non-net CRUD APIs
;; ---

(defn intro [] (gstring/format "%s/api/v1/intro/" host))

(defn mt-user [] (gstring/format "%s/api/v1/mt_user/" host))
(defn mt-user-password-login []
  (gstring/format "%s/api/v1/mt_user/_/password_login" host))
(defn ^{:table :mt-users} mt-user-member [uuid] (gstring/format "%s/api/v1/mt_user/%s" host uuid))
(defn ^{:table :mt-users} mt-user-member-password-login [uuid]
  (gstring/format "%s/api/v1/mt_user/%s/password_login" host uuid))

(defn password-login [] (gstring/format "%s/api/v1/password_login/" host))
(defn ^{:table :password-logins} password-login-member [uuid] (gstring/format "%s/api/v1/password_login/%s" host uuid))

(defn curr-mt-user
  "The mt user that is currently authed"
  []
  (gstring/format "%s/api/v1/mt_user/_/me" host))

(defn session [] (gstring/format "%s/api/v1/session/" host))
