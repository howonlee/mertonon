(ns mtfe.sidebars.loss
  "Loss sidebar. Call em 'Goals' but they're just very strange neural net losses"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Validation Utils
;; ---

(defn input-layer-uuid-set-getter [curr-state]
  (apply hash-set
         (->> curr-state :grid-view-selection :inputs (mapv :layer-uuid))))

(defn curr-layer-uuid-member-getter [curr-state]
  (->> curr-state :create-params :layer-uuid))
;;     (sel/set-state-if-changed! sidebar-state
;;                                api/grid-graph
;;                                grid-uuid
;;                                [:grid-graph-selection :grids 0 :uuid]
;;                                [:grid-graph-selection])
;;     (sel/set-state-if-changed! sidebar-state
;;                                api/grid-view
;;                                grid-uuid
;;                                [:grid-view-selection :grids 0 :uuid]
;;                                [:grid-view-selection])

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 "Goals"]
   [:p "This is an annotation for the gradient descent to tell Mertonon that this is an overall goal center."]])

;; ---
;; Creation
;; ---

(def create-config
  {:resource      :curr-loss
   :endpoint      (api/loss)
   :state-path    [:loss :create]
   :init-state-fn (fn []
                    {:uuid       (str (random-uuid))
                     :layer-uuid ""
                     :name       ""
                     :label      ""
                     :type       "competitiveness"})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)
                   (validations/non-blank [:create-params :layer-uuid] :layer-blank)
                   (validations/not-in-set
                     input-layer-uuid-set-getter
                     curr-layer-uuid-member-getter
                     :also-an-input)]
   :ctr           mc/->Loss
   :ctr-params    [:uuid :layer-uuid :name :label :type]
   :nav-to        :refresh})

(defn loss-create-before-fx [m]
  [[:dispatch-n
    [[some crap]]]])

(defn loss-create-sidebar [m]
  (let [grid-uuid     (->> m :path-params :uuid)
        grid-contents (->> @sidebar-state :grid-graph-sidebar :layers)]
    [:<>
     [:h1 "Denote Responsibility Center as Overall Goal Center"]
     [:div.mb2 "UUID - " (->> @sidebar-state :create-params :uuid str)]
     [:div.mb2 "Grid UUID - " (->> grid-uuid str)]
     [sc/mgn-border-region
      [sc-components/validation-popover sidebar-state :also-an-input "Responsibility center is also a input; inputs cannot also be goals"
       [sc/form-label "Responsibility Center"]]
      [sc-components/validation-popover sidebar-state :layer-blank "Must choose responsibility center"
       [sc-components/state-select-input create-sc-state sidebar-state grid-contents [:create-params :layer-uuid]]]]
     [sc-components/validation-popover sidebar-state :name-blank "Annotation Name is blank"
      [sc-components/state-text-input create-sc-state "Annotation Name" [:create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:create-params :label]]
     [cr/create-button create-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid   (->> m :path-params :uuid)]
    {:resource   :curr-loss
     :endpoint   (api/loss-member uuid)
     :state-path [:loss :delete]
     :model-name "Goal Annotation"
     :nav-to     :reload}))

(defn loss-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn loss-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
