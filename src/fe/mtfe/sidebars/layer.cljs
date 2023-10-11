(ns mtfe.sidebars.layer
  "Layer sidebar"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 [sc/layer-icon] " Responsibility Center"]
   [:p "Responsibility centers in Mertonon contain the cost nodes which Mertonon suggests allocations on."]
   [:strong "Mertonon contribution percentages and suggested contribution percentages are _only_ updated whenever gradient calculations are kicked off."]
   [:p]])

(defn in-weightsets-partial [curr-layer-state]
  (when (seq (->> curr-layer-state :selection :src-weightsets))
    [:<>
     [:h3 [sc/ws-icon] " Weightsets targeting this center"]
     (for [src-weightset (->> curr-layer-state :selection :src-weightsets)] ^{:key (:uuid src-weightset)}
       [util/path-fsl ["weightset" (:uuid src-weightset)] [sc/link (str (:name src-weightset))]])]))

(defn out-weightsets-partial [curr-layer-state]
  (when (seq (->> curr-layer-state :selection :tgt-weightsets))
    [:<>
     [:h3 [sc/ws-icon] " Weightsets stemming from this center"]
     (for [tgt-weightset (->> curr-layer-state :selection :tgt-weightsets)] ^{:key (:uuid tgt-weightset)}
         [util/path-fsl ["weightset" (:uuid tgt-weightset)] [sc/link (str (:name tgt-weightset))]])]))

;; ---
;; Create
;; ---

(defn create-config [m]
  {:resource      :curr-layer
   :endpoint      (api/layer)
   :state-path    [:layer :create]
   :init-state-fn (fn []
                    {:uuid      (str (random-uuid))
                     :grid-uuid (->> m :path-params :uuid)
                     :name      ""
                     :label     ""})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)]
   :ctr           mc/->Layer
   :ctr-params    [:uuid :grid-uuid :name :label]
   :nav-to        :refresh})

(defn layer-create-before-fx [m]
  (cr/before-fx (create-config m) m))

(defn layer-create-sidebar [m]
  (let [grid-uuid   (->> m :path-params :uuid)
        curr-config (create-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 [sc/layer-icon] " Add Responsibility Center"]
     [:div.mb2 [sc/grid-icon] " Grid UUID - " (str grid-uuid)]
     [vblurbs/validation-popover state-path :name-blank "Responsibility Center Name is blank"
      [fi/state-text-input state-path [:create-params :name] "Responsibility Center Name"]]
     [fi/state-text-input state-path [:create-params :label] "Label"]
     [cr/create-button curr-config]]))

;; ---
;; Sidebar Views
;; ---

(defn layer-sidebar-before-fx [m]
  (let [is-demo?       @(subscribe [:is-demo?])
        uuid           (->> m :path-params :uuid)
        layer-endpoint (if is-demo?
                         (api/generator-layer uuid)
                         (api/layer-view uuid))]
    [[:dispatch [:selection :layer-view layer-endpoint {}]]]))

(defn layer-sidebar [{:keys [data] :as req}]
  (let [curr-layer-state @(subscribe [:selection :layer-view])
        is-demo?         @(subscribe [:is-demo?])
        layer-uuid       (->> curr-layer-state :layer :uuid)]
    [:<>
     [header-partial]
     (if (not is-demo?)
       [util/sl (util/path ["layer" layer-uuid "cost_object_create"]) [sc/button [sc/cobj-icon] " Create Cost Node"]])
     [in-weightsets-partial curr-layer-state]
     [out-weightsets-partial curr-layer-state]]))

(defn layer-selection-sidebar
  "For when a layer is selected in a grid or something"
  [m]
  (let [is-demo?         @(subscribe [:is-demo?])
        layer-uuid       (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     [:h2 "Double-Click or click \"Dive In\" to Dive In"]
     [:div [util/path-fsl
            ["layer" layer-uuid]
            [sc/button "Dive In"]]]
     (if (not is-demo?)
       [:div [util/sl (util/path ["layer" layer-uuid "delete"]) [sc/button "Delete"]]])]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-layer
     :endpoint   (api/layer-member uuid)
     :state-path [:layer :delete]
     :model-name "Responsibility Center (Layer)"
     :nav-to     :reload}))

(defn layer-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn layer-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
