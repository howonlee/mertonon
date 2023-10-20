(ns mtfe.sidebars.input
  "Input sidebar"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.update-button :as up]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.views.grid :as grid-view]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Validation Utils
;; ---

(defn loss-layer-uuid-set-getter [curr-state]
  (apply hash-set
         (->> curr-state :grid-view :losses (mapv :layer-uuid))))

(defn curr-layer-uuid-member-getter [param-key]
  (fn [curr-state] (->> curr-state param-key :layer-uuid)))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 "Inputs"]
   [:p "This is an annotation for the gradient descent to tell Mertonon that this is an input cost center."]])

;; ---
;; Mutation view
;; ---

(defn mutation-view [state-path param-key grid-contents]
  [:<>
   [sc/mgn-border-region
    [vblurbs/validation-popover state-path :also-a-goal "Responsibility center is also a goal; goals cannot also be inputs"
     [sc/form-label "Responsibility Center"]]
    [vblurbs/validation-popover state-path :layer-blank "Must choose responsibility center"
     [fi/state-select-input state-path [param-key :layer-uuid] grid-contents]]]
   [vblurbs/validation-popover state-path :name-blank "Annotation Name is blank"
    [fi/state-text-input state-path [param-key :name] "Annotation Name"]]
   [fi/state-text-input state-path [param-key :label] "Label"]])

;; ---
;; Creation
;; ---

(def create-config
  {:resource      :curr-input
   :endpoint      (api/input)
   :state-path    [:input :create]
   :init-state-fn (fn []
                    {:uuid       (str (random-uuid))
                     :layer-uuid ""
                     :name       ""
                     :label      ""
                     :type       "competitiveness"})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)
                   (validations/non-blank [:create-params :layer-uuid] :layer-blank)
                   (validations/not-in-set
                     loss-layer-uuid-set-getter
                     (curr-layer-uuid-member-getter :create-params)
                     :also-a-goal)]
   :ctr           mc/->Input
   :ctr-params    [:uuid :layer-uuid :name :label :type]
   :nav-to        :refresh})

(defn input-create-before-fx [m]
  (let [grid-uuid (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-create-state create-config]
                   [:select-with-custom-success :grid-graph
                    (api/grid-graph grid-uuid) {} :sidebar-selection-success]
                   [:select-with-custom-success :grid-view
                    (api/grid-view grid-uuid) {} :sidebar-selection-success]]]]))

(defn input-create-sidebar [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        grid-contents @(subscribe [:sidebar-state :grid-graph :layers])
        state-path    (create-config :state-path)]
    [:<>
     [:h1 "Denote Responsibility Center as Overall Input Center"]
     [:div.mb2 "Grid UUID - " (str grid-uuid)]
     [mutation-view state-path :create-params grid-contents]
     [cr/create-button create-config]]))

;; ---
;; Update
;; ---

(defn update-config [m]
  (let [input-uuid (->> m :path-params :uuid)]
    {:resource    :curr-input
     :endpoint    (api/input-member input-uuid)
     :state-path  [:input :update]
     :validations [(validations/non-blank [:update-params :name] :name-blank)
                   (validations/non-blank [:update-params :layer-uuid] :layer-blank)
                   (validations/not-in-set
                     loss-layer-uuid-set-getter
                     (curr-layer-uuid-member-getter :update-params)
                     :also-a-goal)]
     :nav-to      :refresh}))

(defn input-update-before-fx [m]
  (let [curr-config (update-config m)
        endpoint    (curr-config :endpoint)
        state-path  (curr-config :state-path)]
    [[:dispatch-n [[:reset-update-state curr-config]
                   [:select-with-custom-success (into state-path [:update-params])
                    endpoint {} :sidebar-selection-success]
                   ;;;;;;;;
                   ;;;;;;;;
                   ;;;;;;;;
                   ;;;;;;;;
                   ;;;;;;;;
                   ]]]))

(defn input-update-sidebar [m]
  (let [curr-config (update-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 "Update Input Annotation"]
     [mutation-view state-path :update-params]
     [up/update-button curr-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-input
     :endpoint   (api/input-member uuid)
     :state-path [:input :delete]
     :model-name "Input Annotation"
     :nav-to     :reload}))

(defn input-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn input-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
