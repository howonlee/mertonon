(ns mtfe.sidebars.grid-select
  "Sidebar for grid selection screen"
  (:require [applied-science.js-interop :as j]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.update-button :as up]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

;; ---
;; Mutation view
;; ---

(defn mutation-view [state-path param-key]
  [:<>
   [:p "More optimization types and ability to change hyperparameters are coming."]
   [vblurbs/validation-popover state-path :name-blank "Grid Name is blank"
    [fi/state-text-input state-path [param-key :name] "Grid Name"]]
   [fi/state-text-input state-path [param-key :label] "Grid Label"]
   ;; TODO: let these change, lol
   [:div.mb2 "Optimization Type - SGD"]
   [:div.mb2 "Hyperparameters"
    [:div "Adjustment Rate - 0.025"]]])

;; ---
;; Creation
;; ---

(def create-config
  {:resource      :curr-grid
   :endpoint      (api/grid)
   :state-path    [:grid :create]
   :init-state-fn (fn []
                    {:uuid           (str (random-uuid))
                     :name           ""
                     :label          ""
                     :optimizer-type :sgd
                     ;; TODO: get some recursive semantics
                     :hyperparams    (.stringify js/JSON (clj->js {:lr 0.025}))})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)]
   :ctr           mc/->Grid
   :ctr-params    [:uuid :name :label :optimizer-type :hyperparams]
   :nav-to        "#/"})

(defn grid-create-before-fx [m]
  (cr/before-fx create-config m))

(defn grid-create-sidebar [m]
  (let [state-path (create-config :state-path)]
    [:<>
     [:h1 "New Grid"]
     [mutation-view state-path :create-params]
     [cr/create-button create-config]]))

;; ---
;; Update
;; ---

(defn update-config [m]
  (let [grid-uuid (->> m :path-params :uuid)]
    {:resource    :curr-grid
     :endpoint    (api/grid-member grid-uuid)
     :state-path  [:grid :update]
     :validations [(validations/non-blank [:update-params :name] :name-blank)]
     :nav-to      "#/"}))

(defn grid-update-before-fx [m]
  (up/before-fx (update-config m) m))

(defn grid-update-sidebar [m]
  (let [curr-config (update-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 "Change Grid"]
     [mutation-view state-path :update-params]
     [up/update-button curr-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid (->> m :path-params :uuid)]
    {:resource   :curr-grid
     :endpoint   (api/grid-member uuid)
     :state-path [:grid :delete]
     :model-name "Grid"
     :nav-to     "#/"}))

(defn grid-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn grid-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
