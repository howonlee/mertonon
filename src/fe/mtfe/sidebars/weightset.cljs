(ns mtfe.sidebars.weightset
  "Sidebar for weightset"
  (:require [clojure.string :as str]
            [loom.graph :as graph]
            [loom.alg :as graph-alg]
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
            [mtfe.views.weightset :as ws-view]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid           (str (random-uuid))
   :src-layer-uuid ""
   :tgt-layer-uuid ""
   :name           ""
   :label          ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)
                                :selection          {}}))

;; ---
;; Validations
;; ---

(defn acyclic-validation
  "Procs if the new weightset we're making would make the graph cyclic"
  [grid-graph]
  (fn [curr-state]
    (let [src-uuid (->> curr-state :curr-create-params :src-layer-uuid)
          tgt-uuid (->> curr-state :curr-create-params :tgt-layer-uuid)]
      ;; Can only proc if we define both things
      (if (or (str/blank? src-uuid) (str/blank? tgt-uuid))
        nil
        (let [new-edge    [src-uuid tgt-uuid]
              old-graph   (apply graph/digraph (:edges grid-graph))
              new-graph   (graph/add-edges old-graph new-edge)
              topsort-res (graph-alg/topsort new-graph)]
          (if (nil? topsort-res)
            :cyclic
            nil))))))

(defn weightset-coord-getter [curr-state]
  (apply hash-set
         (for [ws (->> curr-state :grid-graph-selection :weightsets)]
           [(ws :src-layer-uuid) (ws :tgt-layer-uuid)])))

(defn curr-coord-getter [curr-state]
  [(->> curr-state :curr-create-params :src-layer-uuid)
   (->> curr-state :curr-create-params :tgt-layer-uuid)])

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/non-blank [:curr-create-params :name] :name-blank)
   (sc-validation/non-blank [:curr-create-params :src-layer-uuid] :src-layer-blank)
   (sc-validation/non-blank [:curr-create-params :tgt-layer-uuid] :tgt-layer-blank)
   (acyclic-validation (@sidebar-state :grid-graph-selection))
   (sc-validation/min-num-elems [:grid-graph-selection :layers] 2 :few-layers)
   (sc-validation/not-in-set weightset-coord-getter curr-coord-getter :duplicate-weightset)])

(def create-sc
  (mt-statechart/simple-create :weightset-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                :action-fn     (sc-handlers/creation-handler api/weightset create-sc-state mc/->Weightset [:uuid :src-layer-uuid :tgt-layer-uuid :name :label])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(mt-statechart/init-sc! :weightset-create create-sc-state create-sc)

;; ---
;; Creation
;; ---

(defn weightset-create-sidebar-render [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        grid-contents (->> @sidebar-state :grid-graph-selection :layers)]
      [:<>
       [sc-components/validation-popover sidebar-state :cyclic
        "We don't have support for cyclic weightsets at this time. We will put them in eventually."
        [:h1 [sc/ws-icon] " Add Weightset"]]
       [sc-components/validation-popover sidebar-state :few-layers
        "You need at least two responsibility centers (layers) to make a weightset."
        [:<>]]
       [sc-components/validation-popover sidebar-state :duplicate-weightset "That weightset already exists."
        [:<>]]
       [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
       [:div.mb2 [sc/grid-icon] " Grid UUID - " (->> grid-uuid str)]
       [sc/mgn-border-region
        [sc/form-label [sc/layer-icon] " Source Responsibility Center"]
        [sc-components/validation-popover sidebar-state :src-layer-blank "Must choose source responsiblilty center"
         [sc-components/state-select-input create-sc-state sidebar-state grid-contents [:curr-create-params :src-layer-uuid]]]]

       [sc/mgn-border-region
        [sc/form-label [sc/layer-icon] " Target Responsibility Center"]
        [sc-components/validation-popover sidebar-state :tgt-layer-blank "Must choose target responsiblilty center"
         [sc-components/state-select-input create-sc-state sidebar-state grid-contents [:curr-create-params :tgt-layer-uuid]]]]

       [sc-components/validation-popover sidebar-state :name-blank "Weightset Name is blank"
        [sc-components/state-text-input create-sc-state "Weightset Name" [:curr-create-params :name]]]
       [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
       [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 [sc/ws-icon] " Weightset"]
   [:p "In order to make allocation suggestions, Mertonon needs matrices of weights relating cost nodes to each other in reference to an overall performance indicator."]
   [:p "In addition to suggesting the allocations themselves, Mertonon also makes suggestions for adjustments to the weights, which are suggestions for differences to make on the relations cost nodes have to each other."]
   [:p "The lighter the background color, the stronger the determined relation."]
   [:p "Usually in neural weights, people also do negative weights, but we decided not to because of the vicious organizational politics implications. Math works out fine either way."]
   [:strong "Mertonon weight gradients are _only_ updated whenever gradient calculations are kicked off, not on initial creation."]
   ])

(defn src-layer-partial [curr-ws-state]
  (when (seq (->> curr-ws-state :selection :src-layer))
    [:<>
     [:h3 [sc/layer-icon] " Source responsibilty center"]
     [util/path-fsl ["layer" (->> curr-ws-state :selection :src-layer :uuid)]
      [sc/link (str (->> curr-ws-state :selection :src-layer :name))]]]))

(defn tgt-layer-partial [curr-ws-state]
  (when (seq (->> curr-ws-state :selection :tgt-layer))
    [:<>
     [:h3 [sc/layer-icon] " Target responsibilty center"]
     [util/path-fsl ["layer" (->> curr-ws-state :selection :tgt-layer :uuid)]
      [sc/link (str (->> curr-ws-state :selection :tgt-layer :name))]]]))

(defn adjustment-checkbox-partial []
  [:div.ba.pa2.b--white-20
   [sc/checkbox {:type "checkbox"
                 ;;;;;;
                 ;;;;;;
                 ;;;;;;
                 ;;;;;;
                 :on-click #(reset! ws-view/ws-mode
                                    (if (= @ws-view/ws-mode :default)
                                      :grad
                                      :default))}]
   [:label.lh-copy {:for "ws-mode"} "Show weights with suggested Mertonon adjustments"]])


;; ---
;; Top-level render
;; ---

(defn weightset-create-sidebar [m]
  (sel/set-state-if-changed! sidebar-state
                             api/grid-graph
                             (->> m :path-params :uuid str)
                             [:grid-graph-selection :grids 0 :uuid]
                             [:grid-graph-selection])
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [weightset-create-sidebar-render m])


(defn weightset-sidebar [m]
  (let [curr-ws-state @ws-view/ws-state
        is-demo?      @(subscribe [:is-demo?])
        ws-uuid       (->> curr-ws-state :selection :weightset :uuid)]
    [:<>
     [header-partial]
     [adjustment-checkbox-partial]
     (if (not is-demo?)
       [util/sl (util/path ["weightset" ws-uuid "weight_create"]) [sc/button [sc/weight-icon] " Create Weight"]])
     [src-layer-partial curr-ws-state]
     [tgt-layer-partial curr-ws-state]]))

(defn weightset-selection-sidebar [m]
  (let [is-demo? @(subscribe [:is-demo?])
        ws-uuid  (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     [:h2 "Double-Click to Dive In"]
     (if (not is-demo?)
       [util/sl (util/path ["weightset" ws-uuid "delete"]) [sc/button "Delete"]])]))


;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-weightset
     :endpoint   (api/weightset-member uuid)
     :state-path [:weightset :delete]
     :model-name "Weightset"
     :nav-to     :reload}))

(defn weightset-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn weightset-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
