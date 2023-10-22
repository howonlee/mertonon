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
            [mtfe.components.update-button :as up]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.events.util :as event-util]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe reg-event-db]]))

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
  (when (seq (->> curr-ws-state :src-layer))
    [:<>
     [:h3 [sc/layer-icon] " Source responsibilty center"]
     [util/path-fsl ["layer" (->> curr-ws-state :src-layer :uuid)]
      [sc/link (str (->> curr-ws-state :src-layer :name))]]]))

(defn tgt-layer-partial [curr-ws-state]
  (when (seq (->> curr-ws-state :tgt-layer))
    [:<>
     [:h3 [sc/layer-icon] " Target responsibilty center"]
     [util/path-fsl ["layer" (->> curr-ws-state :tgt-layer :uuid)]
      [sc/link (str (->> curr-ws-state :tgt-layer :name))]]]))

;; ---
;; Mutation view
;; ---

(defn mutation-view [state-path param-key grid-contents]
  [:<>
   [vblurbs/validation-popover state-path :cyclic
    "We don't have support for cyclic weightsets at this time. We will put them in eventually."
    [:div]]
   [vblurbs/validation-popover state-path :few-layers
    "You need at least two responsibility centers (layers) to have a weightset."
    [:div]]
   [vblurbs/validation-popover state-path :duplicate-weightset "That weightset already exists."
    [:div]]
   [sc/mgn-border-region
    [sc/form-label [sc/layer-icon] " Source Responsibility Center"]
    [vblurbs/validation-popover state-path :src-layer-blank "Must choose source responsiblilty center"
     [fi/state-select-input state-path [param-key :src-layer-uuid] grid-contents]]]

   [sc/mgn-border-region
    [sc/form-label [sc/layer-icon] " Target Responsibility Center"]
    [vblurbs/validation-popover state-path :tgt-layer-blank "Must choose target responsiblilty center"
     [fi/state-select-input state-path [param-key :tgt-layer-uuid] grid-contents]]]

   [vblurbs/validation-popover state-path :name-blank "Weightset Name is blank"
    [fi/state-text-input state-path [param-key :name] "Weightset Name"]]
   [fi/state-text-input state-path [param-key :label] "Label"]])

;; ---
;; Validations
;; ---

(defn acyclic-validation
  "Procs if the new weightset we're making would make the graph cyclic"
  [param-key grid-graph-path]
  (fn [curr-state]
    (let [src-uuid (->> curr-state param-key :src-layer-uuid)
          tgt-uuid (->> curr-state param-key :tgt-layer-uuid)]
      ;; Can only proc if we define both things
      (if (or (str/blank? src-uuid) (str/blank? tgt-uuid))
        nil
        (let [new-edge    [src-uuid tgt-uuid]
              grid-graph  (get-in curr-state grid-graph-path)
              old-graph   (apply graph/digraph (:edges grid-graph))
              new-graph   (graph/add-edges old-graph new-edge)
              topsort-res (graph-alg/topsort new-graph)]
          (if (nil? topsort-res)
            :cyclic
            nil))))))

(defn weightset-coord-getter [curr-state]
  (apply hash-set
         (for [ws (->> curr-state :grid-graph :weightsets)]
           [(ws :src-layer-uuid) (ws :tgt-layer-uuid)])))

(defn curr-coord-getter [param-key]
  (fn [curr-state]
    [(->> curr-state param-key :src-layer-uuid)
     (->> curr-state param-key :tgt-layer-uuid)]))

;; ---
;; Creation
;; ---

(defn create-config [m]
  {:resource      :curr-weightset
   :endpoint      (api/weightset)
   :state-path    [:weightset :create]
   :init-state-fn (fn []
                    {:uuid           (str (random-uuid))
                     :src-layer-uuid ""
                     :tgt-layer-uuid ""
                     :name           ""
                     :label          ""})
   :validations
   [(validations/non-blank [:create-params :name] :name-blank)
    (validations/non-blank [:create-params :src-layer-uuid] :src-layer-blank)
    (validations/non-blank [:create-params :tgt-layer-uuid] :tgt-layer-blank)
    (acyclic-validation :create-params [:grid-graph])
    (validations/min-num-elems [:grid-graph :layers] 2 :few-layers)
    (validations/not-in-set weightset-coord-getter (curr-coord-getter :create-params) :duplicate-weightset)]
   :ctr           mc/->Weightset
   :ctr-params    [:uuid :src-layer-uuid :tgt-layer-uuid :name :label]
   :nav-to        :refresh})

(defn weightset-create-before-fx [m]
  (let [grid-uuid (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-create-state (create-config m)]
                   [:select-custom [:weightset :create :grid-graph]
                    (api/grid-graph grid-uuid) {} :sidebar-selection-success]]]]))

(defn weightset-create-sidebar [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        curr-config   (create-config m)
        state-path    (curr-config :state-path)
        grid-contents @(subscribe [:sidebar-state :weightset :create :grid-graph :layers])]
    [:<>
     [:h1 [sc/ws-icon] " Add Weightset"]
     [:div.mb2 [sc/grid-icon] " Grid UUID - " (->> grid-uuid str)]
     [mutation-view state-path :create-params grid-contents]
     [cr/create-button curr-config]]))

;; ---
;; Sidebar Views
;; ---

(defn weightset-before-fx [m]
  (let [is-demo?    @(subscribe [:is-demo?])
        uuid        (->> m :path-params :uuid)
        ws-endpoint (if is-demo?
                      (api/generator-weightset uuid)
                      (api/weightset-view uuid))]
    [[:dispatch [:selection :ws-sidebar ws-endpoint {}]]]))

(reg-event-db
  ::toggle-weightset-adjustment
  (fn [db _]
    (if (= (get-in db [:sidebar-state :ws-adjustment]) :default)
      (assoc-in db [:sidebar-state :ws-adjustment] :grad)
      (assoc-in db [:sidebar-state :ws-adjustment] :default))))

(defn adjustment-checkbox-partial []
  [:div.ba.pa2.b--white-20
   [sc/checkbox {:type     "checkbox"
                 :on-click #(dispatch [::toggle-weightset-adjustment])}]
   [:label.lh-copy {:for "ws-mode"} "Show weights with suggested Mertonon adjustments"]])

(defn weightset-sidebar [m]
  (let [ws-state @(subscribe [:selection :ws-sidebar])
        is-demo? @(subscribe [:is-demo?])
        ws-uuid  (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     [adjustment-checkbox-partial]
     (if (not is-demo?)
       [util/sl
        (util/path ["weightset" ws-uuid "weight_create"])
        [sc/button [sc/weight-icon] " Create Weight"]])
     [src-layer-partial ws-state]
     [tgt-layer-partial ws-state]]))

(defn weightset-selection-sidebar [m]
  (let [is-demo? @(subscribe [:is-demo?])
        ws-uuid  (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     [:h2 "Double-Click or click \"Dive In\" to Dive In"]
     [:div [util/path-fsl
            ["weightset" ws-uuid]
            [sc/button "Dive In"]]]
     (if (not is-demo?)
       [:div
        [util/sl (util/path ["weightset" ws-uuid "update"]) [sc/button "Change"]]
        [:span " "]
        [util/sl (util/path ["weightset" ws-uuid "delete"]) [sc/button "Delete"]]])]))

;; ---
;; Update
;; ---

(defn update-config [m]
  (let [weightset-uuid (->> m :path-params :uuid)]
    {:resource    :curr-weightset
     :endpoint    (api/weightset-member weightset-uuid)
     :state-path  [:weightset :update]
     :validations
     [(validations/non-blank [:update-params :name] :name-blank)
      (validations/non-blank [:update-params :src-layer-uuid] :src-layer-blank)
      (validations/non-blank [:update-params :tgt-layer-uuid] :tgt-layer-blank)
      (acyclic-validation :update-params [:grid-graph])
      (validations/min-num-elems [:grid-graph :layers] 2 :few-layers)]
      ;; Note duplicate-weightset is missing because I need to munge it to do self-instance
      ;; TODO: get duplicate-weightset to not cry about no-change-topology changes
     :nav-to      :refresh}))

(defn weightset-update-before-fx [m]
  (let [curr-config (update-config m)
        endpoint    (curr-config :endpoint)
        state-path  (curr-config :state-path)
        children-fn (event-util/layer-join-dag-step
                      (into state-path [:curr-weightset])
                      (event-util/grid-view-terminal-step (into state-path [:grid-graph])
                                                          (into state-path [:grid-view])))]
    [[:dispatch-n [[:reset-update-state curr-config]
                   [:select-cust
                    {:resource       (into state-path [:update-params])
                     :endpoint       endpoint
                     :params         {}
                     :success-event  :sidebar-dag-success
                     :success-params {:children-fn children-fn}}]]]]))

(defn weightset-update-sidebar [m]
  (let [curr-config   (update-config m)
        state-path    (curr-config :state-path)
        grid-contents @(subscribe [:sidebar-state :weightset :update :grid-graph :layers])
        curr-params   @(subscribe [:sidebar-state :weightset :update :update-state])]
    [:<>
     [:h1 [sc/ws-icon] " Weightset"]
     [mutation-view state-path :update-params grid-contents]
     [up/update-button curr-config]]))

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
