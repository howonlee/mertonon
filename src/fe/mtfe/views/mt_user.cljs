(ns mtfe.views.mt-user
  "Mertonon User view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch [:selection :curr-mt-user (api/curr-mt-user) {}]]])

(defn mt-user-page [m]
  [sc/main-section
   (let [{username   :username
          email      :email
          created-at :created-at
          updated-at :updated-at} @(subscribe [:selection :curr-mt-user])]
     [:<>
      [:h1 (str username)]
      [:h4 (str email)]
      [:p (str "User created at : " created-at)]])])
