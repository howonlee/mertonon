(ns mtfe.sidebars.cost-object
  "Sidebar for cost-object"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.update-button :as up]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Partials
;; ---

(defn header-partial [curr-cobj-state]
  [:<>
   [:h1 [sc/cobj-icon] " Cost Node"]
   (when (some? curr-cobj-state)
     [:h2 (-> curr-cobj-state :cost-object :name)])
   [:p "Cost nodes in Mertonon are the entities that Mertonon suggests allocations on. They could be people, cost objects for paperclips, etc etc."]
   [:strong "Mertonon contribution percentages and suggested contribution percentages are _only_ updated whenever gradient calculations are kicked off, not on initial creation."]
   [:p]])

;; ---
;; Mutation view
;; ---

(defn mutation-view [state-path param-key]
  [:<>
   [vblurbs/validation-popover state-path :name-blank "Cost Node Name is blank"
    [fi/state-text-input state-path [param-key :name] "Cost Node Name"]]
   [fi/state-text-input state-path [param-key :label] "Label"]])

;; ---
;; Creation
;; ---

(defn create-config [m]
  {:resource      :curr-cobj
   :endpoint      (api/cost-object)
   :state-path    [:cobj :create]
   :init-state-fn (fn []
                    {:uuid       (str (random-uuid))
                     :layer-uuid (->> m :path-params :uuid)
                     :name       ""
                     :label      ""})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)]
   :ctr           mc/->CostObject
   :ctr-params    [:uuid :layer-uuid :name :label]
   :nav-to        :refresh})

(defn cost-object-create-before-fx [m]
  (cr/before-fx (create-config m) m))

(defn cost-object-create-sidebar [m]
  (let [layer-uuid  (->> m :path-params :uuid)
        curr-config (create-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 [sc/cobj-icon] " Add Cost Node"]
     [:div.mb2 [sc/layer-icon] " Layer UUID - " (->> layer-uuid str)]
     [mutation-view state-path :create-params]
     [cr/create-button curr-config]]))

;; ---
;; Sidebar View
;; ---

(defn cost-object-sidebar-before-fx [m]
  (let [is-demo?        @(subscribe [:is-demo?])
        cobj-uuid       (->> m :path-params :uuid)
        cobj-endpoint   (if is-demo?
                          (api/generator-cost-object cobj-uuid)
                          (api/cost-object-view cobj-uuid))]
    [[:dispatch
      [:select-custom
       :cobj-view
       cobj-endpoint
       {}
       :sidebar-selection-and-validate
       {:validations
        [(validations/and-predicate
           (validations/min-num-elems [:losses] 1 :no-loss)
           (validations/min-num-elems [:inputs] 1 :no-input)
           :not-input-or-loss)]}]]]))

(defn cost-object-sidebar [m]
  (let [curr-cobj-state @(subscribe [:sidebar-state :cobj-view])
        val-path        [:cobj-view]
        is-demo?        @(subscribe [:is-demo?])
        cobj-uuid       (->> m :path-params :uuid)]
    [:<>
     [header-partial curr-cobj-state]
     (when (not is-demo?)
       [:<>
        [:div
         [vblurbs/validation-toast val-path :not-input-or-loss "Journal Entries must be for cost nodes in an input or goal responsibility center"]]
        [vblurbs/validated-link val-path :not-input-or-loss "Create Journal Entry"
         [:div
          [util/sl (util/path ["cost_object" cobj-uuid "entry_create"])
           [sc/button [sc/entry-icon] " Create Journal Entry"]]]]])]))

;; ---
;; Update
;; ---

(defn update-config [m]
  (let [cobj-uuid (->> m :path-params :uuid)]
    {:resource    :curr-cobj
     :endpoint    (api/cost-object-member cobj-uuid)
     :state-path  [:cobj :update]
     :validations [(validations/non-blank [:update-params :name] :name-blank)]
     :nav-to      :refresh}))

(defn cost-object-update-before-fx [m]
  (up/before-fx (update-config m) m))

(defn cost-object-update-sidebar [m]
  (let [curr-config (update-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 "Change Cost Object"]
     [mutation-view state-path :update-params]
     [up/update-button curr-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid (->> m :path-params :uuid)]
    {:resource   :curr-cobj
     :endpoint   (api/cost-object-member uuid)
     :state-path [:cobj :delete]
     :model-name "Cost Object"
     :nav-to     :reload}))

(defn cost-object-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn cost-object-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
