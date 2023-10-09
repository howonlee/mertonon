(ns mtfe.sidebars.cost-object
  "Sidebar for cost-object"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 [sc/cobj-icon] " Cost Node"]
   [:p "Cost nodes in Mertonon are the entities that Mertonon suggests allocations on. They could be people, cost objects for paperclips, etc etc."]
   [:strong "Mertonon contribution percentages and suggested contribution percentages are _only_ updated whenever gradient calculations are kicked off, not on initial creation."]
   [:p]])

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
     [vblurbs/validation-popover state-path :name-blank "Cost Node Name is blank"
      [fi/state-text-input state-path [:create-params :name] "Cost Node Name"]]
     [fi/state-text-input state-path [:create-params :label] "Label"]
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
      [:select-with-custom-success
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
  (let [curr-cobj-state @(subscribe [:selection :cobj-view])
        val-path        [:cobj-view]
        is-demo?        @(subscribe [:is-demo?])
        cobj-uuid       (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     (when (not is-demo?)
       [:<>
        [vblurbs/validation-toast val-path :not-input-or-loss "Journal Entries must be for cost nodes in an input or goal responsibility center"]
        [vblurbs/validated-link val-path :not-input-or-loss "Create Journal Entry"
         [util/sl (util/path ["cost_object" cobj-uuid "entry_create"])
          [sc/button [sc/entry-icon] " Create Journal Entry"]]]])]))

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
