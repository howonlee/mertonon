(ns mtfe.sidebars.loss
  "Loss sidebar. Call em 'Goals' but they're just very strange neural net losses"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.update-button :as up]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.events.util :as event-util]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Validation Utils
;; ---

(defn input-layer-uuid-set-getter [curr-state]
  (apply hash-set
         (->> curr-state :grid-view :inputs (mapv :layer-uuid))))

(defn curr-layer-uuid-member-getter [curr-state]
  (->> curr-state :create-params :layer-uuid))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 "Goals"]
   [:p "This is an annotation for the gradient descent to tell Mertonon that this is an overall goal center."]])

;; ---
;; Mutation view
;; ---

(defn mutation-view [state-path param-key grid-contents]
  [:<>
     [sc/mgn-border-region
      [vblurbs/validation-popover state-path :also-an-input "Responsibility center is also a input; inputs cannot also be goals"
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
  {:resource      :curr-loss
   :endpoint      (api/loss)
   :state-path    [:loss :create]
   :init-state-fn (fn []
                    {:uuid       (str (random-uuid))
                     :layer-uuid ""
                     :name       ""
                     :label      ""
                     :type       "competitiveness"})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)
                   (validations/non-blank [:create-params :layer-uuid] :layer-blank)
                   (validations/not-in-set
                     input-layer-uuid-set-getter
                     curr-layer-uuid-member-getter
                     :also-an-input)]
   :ctr           mc/->Loss
   :ctr-params    [:uuid :layer-uuid :name :label :type]
   :nav-to        :refresh})

(defn loss-create-before-fx [m]
  (let [grid-uuid (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-create-state create-config]
                   [:select-custom :grid-graph
                    (api/grid-graph grid-uuid) {} :sidebar-selection-success]
                   [:select-custom :grid-view
                    (api/grid-view grid-uuid) {} :sidebar-selection-success]]]]))

(defn loss-create-sidebar [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        grid-contents @(subscribe [:sidebar-state :grid-graph :layers])
        state-path    (create-config :state-path)]
    [:<>
     [:h1 "Denote Responsibility Center as Overall Goal Center"]
     [:div.mb2 "Grid UUID - " (str grid-uuid)]
     [mutation-view state-path :create-params grid-contents]
     [cr/create-button create-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-loss
     :endpoint   (api/loss-member uuid)
     :state-path [:loss :delete]
     :model-name "Goal Annotation"
     :nav-to     :reload}))

(defn loss-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn loss-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
