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
;; State
;; ---

(defn init-create-params []
  {:uuid       (str (random-uuid))
   :layer-uuid ""
   :name       ""
   :label      ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))

(def create-sc
  (mt-statechart/simple-create :cobj-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler
                                                 sidebar-state
                                                 [(sc-validation/non-blank [:curr-create-params :name] :name-blank)])
                                :action-fn     (sc-handlers/creation-handler api/cost-object create-sc-state mc/->CostObject [:uuid :layer-uuid :name :label])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(mt-statechart/init-sc! :cobj-create create-sc-state create-sc)

;; ---
;; Creation
;; ---

(defn cobj-create-sidebar-render [m]
  (let [layer-uuid (->> m :path-params :uuid)]
    [:<>
     [:h1 [sc/cobj-icon] " Add Cost Node"]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 [sc/layer-icon] " Layer UUID - " (->> layer-uuid str)]
     [sc-components/validation-popover sidebar-state :name-blank "Cost Node Name is blank"
      [sc-components/state-text-input create-sc-state "Cost Node Name" [:curr-create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

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
;; Top-level render
;; ---

(defn cost-object-create-sidebar [m]
  (sel/swap-if-changed! (->> m :path-params :uuid str)
                         sidebar-state
                         [:curr-create-params :layer-uuid])
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [cobj-create-sidebar-render m])

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
