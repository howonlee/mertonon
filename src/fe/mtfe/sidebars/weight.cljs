(ns mtfe.sidebars.weight
  "Sidebar for weight"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

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
;; Validations
;; ---

(defn weight-coord-getter [curr-state]
  (apply hash-set
         (for [weight (->> curr-state :ws-selection :weights)]
           [(weight :src-cobj-uuid) (weight :tgt-cobj-uuid)])))

(defn curr-coord-getter [curr-state]
  [(->> curr-state :create-params :src-cobj-uuid) (->> curr-state :create-params :tgt-cobj-uuid)])

;; ---
;; Creation
;; ---

(defn create-config [m]
  (let [ws-uuid        (->> m :path-params :uuid str)
        src-cobj-uuid  (->> m :query-params :src_cobj_uuid)
        tgt-cobj-uuid  (->> m :query-params :tgt_cobj_uuid)]
    {:resource      :curr-weight
     :endpoint      (api/weight)
     :state-path    [:weight :create]
     :init-state-fn (fn []
                      {:uuid           (str (random-uuid))
                       :weightset-uuid ws-uuid
                       :src-cobj-uuid  src-cobj-uuid
                       :tgt-cobj-uuid  tgt-cobj-uuid
                       :label          ""
                       :type           "default"
                       :value          0})
     :validations   
     [(validations/non-blank [:create-params :src-cobj-uuid] :src-cobj-blank)
      (validations/non-blank [:create-params :tgt-cobj-uuid] :tgt-cobj-blank)
      (validations/non-blank [:create-params :value] :value-blank)
      (validations/or-predicate
        (validations/min-num-elems [:ws-selection :src-cobjs] 1 :few-src)
        (validations/min-num-elems [:ws-selection :tgt-cobjs] 1 :few-tgt)
        :few-cobjs)
      (validations/not-in-set weight-coord-getter curr-coord-getter :duplicate-weight)]
     :ctr           mc/->Weight
     :ctr-params    [:uuid :weightset-uuid :src-cobj-uuid :tgt-cobj-uuid :label :type :value]
     :nav-to        :refresh}))

(defn weight-create-before-fx [m]
  (let [ws-uuid (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-create-state (create-config m)]
                   [:select-custom [:weight :create :ws-selection]
                    (api/weightset-view ws-uuid) {} :sidebar-selection-success]
                   [:select-custom :alloc-cue
                    (api/allocation-cue) {} :sidebar-selection-success]]]]))

(defn weight-create-sidebar [m]
  (let [ws-uuid     (->> m :path-params :uuid)
        curr-config (create-config m)
        state-path  (curr-config :state-path)
        alloc-cue   @(subscribe [:sidebar-state :alloc-cue])
        curr-params @(subscribe [:sidebar-state :weight :create :create-params])
        src-cobjs   @(subscribe [:sidebar-state :weight :create :ws-selection :src-cobjs])
        tgt-cobjs   @(subscribe [:sidebar-state :weight :create :ws-selection :tgt-cobjs])]
    [:<>
     [vblurbs/validation-popover state-path :few-cobjs
      "You need at least two cost nodes, one in the source and one in the target responsibility center (layer)."
      [:h1 [sc/weight-icon] " Add Weight"]]
     [:div.mb2 [sc/ws-icon] " Weightset UUID - " (->> ws-uuid str)]
     [:p "Values currently have to be a positive integer only right now. We might do other things eventually."]
     [sc/mgn-border-region
      [vblurbs/validation-popover state-path :duplicate-weight "That weight already exists."
       [:<>
        [:p "Weight prompt: "]
        [:strong (->> alloc-cue :cue str)]]]]
     [sc/mgn-border-region
      [sc/form-label [sc/cobj-icon] " Source Cost Node"]
      [vblurbs/validation-popover state-path :src-cobj-blank "Must choose source cost node"
       [fi/state-select-input state-path [:create-params :src-cobj-uuid] src-cobjs]]]

     [sc/mgn-border-region
      [sc/form-label [sc/cobj-icon] " Target Cost Node"]
      [vblurbs/validation-popover state-path :tgt-cobj-blank "Must choose target cost node"
       [fi/state-select-input state-path [:create-params :tgt-cobj-uuid] tgt-cobjs]]]

     [sc/mgn-border-region
      [fi/state-text-input state-path [:create-params :label] "Label"]]

     [sc/mgn-border-region
      [sc/form-label "Weight Value"]
      [sc/form-label "(no units, normalized automatically)"]
      [vblurbs/validation-popover state-path :value-blank "Value is blank"
       [fi/state-power-range-input state-path [:create-params :value]]]
      [sc/form-label (->> curr-params :value str)]]
     [cr/create-button curr-config]]))

;; ---
;; Sidebar View
;; ---

(defn weight-sidebar [m]
  (let [is-demo?    @(subscribe [:is-demo?])
        weight-uuid (->> m :path-params :uuid)]
    [:<>
     [header-partial]
     (if (not is-demo?)
       [util/sl (util/path ["weight" weight-uuid "delete"]) [sc/button "Delete"]])]))

(defn weight-selection-before-fx [m]
  (let [is-demo?        @(subscribe [:is-demo?])
        uuid            (->> m :path-params :uuid)
        weight-endpoint (if is-demo?
                          (api/generator-weight uuid)
                          (api/weight-view uuid))]
    [[:dispatch [:selection :weight-view weight-endpoint {}]]]))

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
     :nav-to     :refresh}))

(defn weight-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn weight-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
