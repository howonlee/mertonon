(ns mtfe.sidebars.weight
  "Sidebar for weight"
  (:require [mertonon.models.constructors :as mc]
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
            [mtfe.views.weight :as weight-view]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid           (str (random-uuid))
   :weightset-uuid ""
   :src-cobj-uuid  ""
   :tgt-cobj-uuid  ""
   :label          ""
   :type           "default"
   :value          0})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)
                                :selection          {}}))

;; ---
;; Validations
;; ---

(defn weight-coord-getter [curr-state]
  (apply hash-set
         (for [weight (->> curr-state :ws-selection :weights)]
           [(weight :src-cobj-uuid) (weight :tgt-cobj-uuid)])))

(defn curr-coord-getter [curr-state]
  [(->> curr-state :curr-create-params :src-cobj-uuid) (->> curr-state :curr-create-params :tgt-cobj-uuid)])

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/non-blank [:curr-create-params :src-cobj-uuid] :src-cobj-blank)
   (sc-validation/non-blank [:curr-create-params :tgt-cobj-uuid] :tgt-cobj-blank)
   (sc-validation/non-blank [:curr-create-params :value] :value-blank)
   (sc-validation/is-integer [:curr-create-params :value] :value-not-int)
   (sc-validation/or-predicate
     (sc-validation/min-num-elems [:ws-selection :src-cobjs] 1 :few-src)
     (sc-validation/min-num-elems [:ws-selection :tgt-cobjs] 1 :few-tgt)
     :few-cobjs)
   (sc-validation/not-in-set weight-coord-getter curr-coord-getter :duplicate-weight)])

(def create-sc
  (mt-statechart/simple-create :weight-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                :action-fn     (sc-handlers/creation-handler api/weight create-sc-state mc/->Weight [:uuid :weightset-uuid :src-cobj-uuid :tgt-cobj-uuid :label :type :value])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(mt-statechart/init-sc! :weight-create create-sc-state create-sc)

;; ---
;; Creation
;; ---

(defn weight-create-sidebar-render [m]
  (let [ws-uuid   (->> m :path-params :uuid)
        src-cobjs (->> @sidebar-state :ws-selection :src-cobjs)
        tgt-cobjs (->> @sidebar-state :ws-selection :tgt-cobjs)]
    [:<>
     [sc-components/validation-popover sidebar-state :few-cobjs
      "You need at least two cost nodes, one in the source and one in the target responsibility center (layer)."
      [:h1 [sc/weight-icon] " Add Weight"]]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 [sc/ws-icon] " Weightset UUID - " (->> ws-uuid str)]
     [:p "Values currently have to be a positive integer only right now. We might do other things eventually."]
     [sc/mgn-border-region
      [sc-components/validation-popover sidebar-state :duplicate-weight "That weight already exists."
       [:<>
        [:p "Weight prompt: "]
        [:strong (->> @sidebar-state :alloc-cue :cue str)]]]]
     [sc/mgn-border-region
      [sc/form-label [sc/cobj-icon] " Source Cost Node"]
      [sc-components/validation-popover sidebar-state :src-cobj-blank "Must choose source cost node"
       [sc-components/state-select-input create-sc-state sidebar-state src-cobjs [:curr-create-params :src-cobj-uuid]]]]

     [sc/mgn-border-region
      [sc/form-label [sc/cobj-icon] " Target Cost Node"]
      [sc-components/validation-popover sidebar-state :tgt-cobj-blank "Must choose target cost node"
       [sc-components/state-select-input create-sc-state sidebar-state tgt-cobjs [:curr-create-params :tgt-cobj-uuid]]]]

     [sc/mgn-border-region
      [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]]

     [sc/mgn-border-region
      [sc/form-label "Weight Value"]
      [sc/form-label "(no units, normalized automatically)"]
      [sc-components/validation-popover sidebar-state :value-blank "Value is blank"
       [sc-components/validation-popover sidebar-state :value-not-int "Value is not a positive integer"
        [sc-components/state-range-input create-sc-state sidebar-state [:curr-create-params :value]
         10
         1000
         10]]]
      [sc/form-label (->> @sidebar-state :curr-create-params :value str)]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 [sc/weight-icon] " Weight"]
   [:p "This is an in-Mertonon judgement of the relation of one cost node to another."]
   [:p "Eventually we will have a whole menagerie of integrations for figuring this out, but because they can inevitably be gamed the first method must be by arbitrary manual setting."]])

(defn weight-data-partial [curr-weight-state]
  [:<>
   [:h2 "Double-Click or click \"Dive In\" to Dive In"]
   (when (seq (->> curr-weight-state :src-cobj))
     [:<>
      [:h3 [sc/cobj-icon] " Source cost node"]
      [util/path-fsl ["cost_object" (->> curr-weight-state :src-cobj :uuid str)]
       [sc/link (str (->> curr-weight-state :src-cobj :name))]]])
   (when (seq (->> curr-weight-state :tgt-cobj))
     [:<>
      [:h3 [sc/cobj-icon] " Target cost node"]
      [util/path-fsl ["cost_object" (->> curr-weight-state :tgt-cobj :uuid str)]
       [sc/link (str (->> curr-weight-state :tgt-cobj :name))]]])
   [:h3 "Weight Value"]
   [:p (->> curr-weight-state :weight :value str)]

   [:h3 "Weight Gradient"]
   [:p (->> curr-weight-state :weight :grad str)]

   [:h3 "Label"]
   (let [curr-label (->> curr-weight-state :weight :label str)]
     (if (clojure.string/blank? curr-label)
       [:p " --- "]
       [:p curr-label]))])

;; ---
;; Top-level render
;; ---

(defn weight-create-sidebar [m]
  (let [ws-uuid        (->> m :path-params :uuid str)
        src-cobj-uuid  (->> m :query-params :src_cobj_uuid)
        tgt-cobj-uuid  (->> m :query-params :tgt_cobj_uuid)]
    (sel/swap-if-changed! ws-uuid
                          sidebar-state
                          [:curr-create-params :weightset-uuid])
    ;; always reset state because otherwise mutations from layers mucking don't show up on ws
    ;; TODO: fix this, maybe by sticking like a second prop system on there,
    ;; maybe by converting everything else to hooks and suffering,
    ;; maybe in a less monstrous way
    (sel/set-state-with-results! sidebar-state
                                 api/weightset-view
                                 [:ws-selection]
                                 ws-uuid)
    (sel/set-state-with-results! sidebar-state
                                 api/allocation-cue
                                 [:alloc-cue])
    (mt-statechart/send-reset-event-if-finished! create-sc-state)
    (fn [m]
      (swap! sidebar-state assoc-in [:curr-create-params :src-cobj-uuid] src-cobj-uuid)
      (swap! sidebar-state assoc-in [:curr-create-params :tgt-cobj-uuid] tgt-cobj-uuid)
      [weight-create-sidebar-render m])))

(defn weight-sidebar [m]
  (let [is-demo?    @(subscribe [:is-demo?])
        weight-uuid (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     (if (not is-demo?)
       [util/sl (util/path ["weight" weight-uuid "delete"]) [sc/button "Delete"]])]))

(defn weight-selection-sidebar [m]
  (let [is-demo?          @(subscribe [:is-demo?])
        weight-uuid       (->> m :path-params :uuid str)
        curr-weight-state @(subscribe [:selection :weight-view])]
    [:<>
     [header-partial]
     [weight-data-partial curr-weight-state]
     [:div [util/path-fsl
            ["weight" weight-uuid]
            [sc/button "Dive In"]]]
     (if (not is-demo?)
       [util/sl (util/path ["weight" weight-uuid "delete"]) [sc/button "Delete"]])]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-weight
     :endpoint   (api/weight-member uuid)
     :state-path [:weight :delete]
     :model-name "Weight"
     :nav-to     "#/"}))

(defn weight-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn weight-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
