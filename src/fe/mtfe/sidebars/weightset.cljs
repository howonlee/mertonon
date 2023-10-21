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
            [mtfe.components.validation-blurbs :as vblurbs]
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
;; Validations
;; ---

(defn acyclic-validation
  "Procs if the new weightset we're making would make the graph cyclic"
  [grid-graph-path]
  (fn [curr-state]
    (let [src-uuid (->> curr-state :create-params :src-layer-uuid)
          tgt-uuid (->> curr-state :create-params :tgt-layer-uuid)]
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

(defn curr-coord-getter [curr-state]
  [(->> curr-state :create-params :src-layer-uuid)
   (->> curr-state :create-params :tgt-layer-uuid)])

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
                     :label          ""}
                    )
   :validations
   [(validations/non-blank [:create-params :name] :name-blank)
    (validations/non-blank [:create-params :src-layer-uuid] :src-layer-blank)
    (validations/non-blank [:create-params :tgt-layer-uuid] :tgt-layer-blank)
    (acyclic-validation [:grid-graph])
    (validations/min-num-elems [:grid-graph :layers] 2 :few-layers)
    (validations/not-in-set weightset-coord-getter curr-coord-getter :duplicate-weightset)]
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
     [vblurbs/validation-popover state-path :cyclic
      "We don't have support for cyclic weightsets at this time. We will put them in eventually."
      [:h1 [sc/ws-icon] " Add Weightset"]]
     [vblurbs/validation-popover state-path :few-layers
      "You need at least two responsibility centers (layers) to make a weightset."
      [:<>]]
     [vblurbs/validation-popover state-path :duplicate-weightset "That weightset already exists."
      [:<>]]
     [:div.mb2 [sc/grid-icon] " Grid UUID - " (->> grid-uuid str)]
     [sc/mgn-border-region
      [sc/form-label [sc/layer-icon] " Source Responsibility Center"]
      [vblurbs/validation-popover state-path :src-layer-blank "Must choose source responsiblilty center"
       [fi/state-select-input state-path [:create-params :src-layer-uuid] grid-contents]]]

     [sc/mgn-border-region
      [sc/form-label [sc/layer-icon] " Target Responsibility Center"]
      [vblurbs/validation-popover state-path :tgt-layer-blank "Must choose target responsiblilty center"
       [fi/state-select-input state-path [:create-params :tgt-layer-uuid] grid-contents]]]

     [vblurbs/validation-popover state-path :name-blank "Weightset Name is blank"
      [fi/state-text-input state-path [:create-params :name] "Weightset Name"]]
     [fi/state-text-input state-path [:create-params :label] "Label"]
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
       [:div [util/sl
              (util/path ["weightset" ws-uuid "delete"])
              [sc/button "Delete"]]])]))

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
