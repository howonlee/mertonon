(ns mtfe.sidebars.entry
  "Sidebar for entry"
  (:require [mertonon.models.constructors :as mc]
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
   [:p "Values currently have to be an arbitrary integer only right now."]
   [:p "Currency and lots of other stuff is coming."]
   [vblurbs/validation-popover state-path :name-blank "Journal Entry Name is blank"
    [fi/state-text-input state-path [param-key :name] "Journal Entry Name"]]
   [fi/state-text-input state-path [param-key :label] "Label"]
   [vblurbs/validation-popover state-path :value-not-int-str "Value is not an integer"
    [vblurbs/validation-popover state-path :value-blank "Value is blank"
     [fi/state-text-input state-path [param-key :value] "Value"]]]
   [sc/border-region
    [sc/form-label "Entry Date"]
    [fi/state-datepicker state-path [param-key :date]]]])

;; ---
;; Creation
;; ---

(defn create-config [m]
  {:resource      :curr-entry
   :endpoint      (api/entry)
   :state-path    [:entry :create]
   :init-state-fn (fn []
                    {:uuid       (str (random-uuid))
                     :cobj-uuid  (->> m :path-params :uuid)
                     :name       ""
                     :label      ""
                     :type       "abstract.arbitrary.value"
                     :value      0
                     :date       (js/Date.)})
   :validations   [(validations/non-blank [:create-params :name] :name-blank)
                   (validations/non-blank [:create-params :value] :value-blank)
                   (validations/is-integer-string [:create-params :value] :value-not-int-str)]
   :ctr           mc/->Entry
   :ctr-params    [:uuid :cobj-uuid :name :label :type :value :date]
   :nav-to        :refresh})

(defn entry-create-before-fx [m]
  (cr/before-fx (create-config m) m))

(defn entry-create-sidebar [m]
  (let [cobj-uuid   (->> m :path-params :uuid)
        curr-config (create-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 [sc/entry-icon] " Add Journal Entry"]
     [:div.mb2 [sc/cobj-icon] " Cost Node UUID - " (->> cobj-uuid str)]
     [mutation-view state-path :create-params]
     [cr/create-button curr-config]]))

;; ---
;; Update
;; ---

(defn update-config [m]
  (let [entry-uuid (->> m :path-params :uuid)]
    {:resource    :curr-entry
     :endpoint    (api/entry-member entry-uuid)
     :state-path  [:entry :update]
     :validations [(validations/non-blank [:update-params :name] :name-blank)
                   (validations/non-blank [:update-params :value] :value-blank)
                   (validations/is-integer-string [:update-params :value] :value-not-int-str)]
     :nav-to      :refresh}))

(defn entry-update-before-fx [m]
  (up/before-fx (update-config m) m))

(defn entry-update-sidebar [m]
  (let [curr-config (update-config m)
        state-path  (curr-config :state-path)]
    [:<>
     [:h1 "Update Journal Entry"]
     [mutation-view state-path :update-params]
     [up/update-button curr-config]]))

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid (->> m :path-params :uuid)]
    {:resource   :curr-entry
     :endpoint   (api/entry-member uuid)
     :state-path [:entry :delete]
     :model-name "Journal Entry"
     :nav-to     :reload}))

(defn entry-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn entry-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
