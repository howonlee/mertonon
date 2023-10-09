(ns mtfe.sidebars.layer
  "Layer sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.util :as util]
            [mtfe.views.layer :as layer-view]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe]]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid      (str (random-uuid))
   :grid-uuid ""
   :name      ""
   :label     ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)
                                :selection          {}}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))
(defonce delete-sc-state
   (r/atom nil))

(def create-sc
  (mt-statechart/simple-create :layer-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler
                                                 sidebar-state
                                                 [(sc-validation/non-blank [:curr-create-params :name] :name-blank)])
                                :action-fn     (sc-handlers/creation-handler api/layer create-sc-state mc/->Layer [:uuid :grid-uuid :name :label])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :layer-delete
                               {:action-fn   (sc-handlers/deletion-handler api/layer-member delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :layer-create create-sc-state create-sc)
(mt-statechart/init-sc! :layer-delete delete-sc-state delete-sc)

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

(defn layer-create-sidebar-render [m]
  (let [grid-uuid     (->> m :path-params :uuid)]
    [:<>
     [:h1 [sc/layer-icon] " Add Responsibility Center"]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 [sc/grid-icon] " Grid UUID - " (->> grid-uuid str)]
     [sc-components/validation-popover sidebar-state :name-blank "Responsibility Center Name is blank"
      [sc-components/state-text-input create-sc-state "Responsibility Center Name" [:curr-create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

(defn layer-create-sidebar [m]
  (let [grid-uuid (->> m :path-params :uuid str)]
    (sel/swap-if-changed! grid-uuid
                          sidebar-state
                          [:curr-create-params :grid-uuid])
    (mt-statechart/send-reset-event-if-finished! create-sc-state)
    [layer-create-sidebar-render m]))

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
