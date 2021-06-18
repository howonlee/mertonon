(ns mtfe.sidebars.input
  "Input sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.util :as util]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid       (str (random-uuid))
   :layer-uuid ""
   :name       ""
   :label      ""
   :type       "competitiveness"})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)
                                :selection          {}}))

;; ---
;; Validation Utils
;; ---

(defn loss-layer-uuid-set-getter [curr-state]
  (apply hash-set
         (->> curr-state :grid-view-selection :losses (mapv :layer-uuid))))

(defn curr-layer-uuid-member-getter [curr-state]
  (->> curr-state :curr-create-params :layer-uuid))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
  (r/atom nil))
(defonce delete-sc-state
  (r/atom nil))

(def create-sc
  (mt-statechart/simple-create :input-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler
                                                 sidebar-state
                                                 [(sc-validation/non-blank [:curr-create-params :name] :name-blank)
                                                  (sc-validation/non-blank [:curr-create-params :layer-uuid] :layer-blank)
                                                  (sc-validation/not-in-set
                                                    loss-layer-uuid-set-getter
                                                    curr-layer-uuid-member-getter
                                                    :also-a-loss)])
                                :action-fn     (sc-handlers/creation-handler api/inputApi create-sc-state mc/->Input [:uuid :layer-uuid :name :label :type])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :input-delete
                               {:action-fn   (sc-handlers/deletion-handler api/inputMemberApi delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :input-create create-sc-state create-sc)
(mt-statechart/init-sc! :input-delete delete-sc-state delete-sc)

;; ---
;; Creation
;; ---

(defn input-create-sidebar-render [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        grid-contents (->> @sidebar-state :grid-graph-selection :layers)]
    [:<>
     [:h1 "Denote Responsibility Center as Input Cost Center"]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 "Grid UUID - " (->> grid-uuid str)]
     [sc/mgn-border-region
      [sc-components/validation-popover sidebar-state :also-a-loss "Responsibility center is also a goal: goals cannot also be inputs"
       [sc/form-label "Responsibility Center"]]
      [sc-components/validation-popover sidebar-state :layer-blank "Must choose responsibility center"
       [sc-components/state-select-input create-sc-state sidebar-state grid-contents [:curr-create-params :layer-uuid]]]]
     [sc-components/validation-popover sidebar-state :name-blank "Annotation Name is blank"
      [sc-components/state-text-input create-sc-state "Annotation Name" [:curr-create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 "Inputs"]
   [:p "This is an annotation for the gradient descent to tell Mertonon that this is an input cost center."]])

;; ---
;; Top-level render
;; ---

(defn input-create-sidebar [m]
  (let [grid-uuid (->> m :path-params :uuid str)]
    (sel/set-state-if-changed! sidebar-state
                               api/gridGraphApi
                               grid-uuid
                               [:grid-graph-selection :grids 0 :uuid]
                               [:grid-graph-selection])
    (sel/set-state-if-changed! sidebar-state
                               api/gridViewApi
                               grid-uuid
                               [:grid-view-selection :grids 0 :uuid]
                               [:grid-view-selection])
    (mt-statechart/send-reset-event-if-finished! create-sc-state)
    [input-create-sidebar-render m]))

(defn input-delete-sidebar [m]
  [sc-components/delete-model-sidebar sidebar-state api/inputMemberApi delete-sc-state "Input" m])
