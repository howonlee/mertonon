(ns mtfe.views.mt-user
  "Mertonon User view"
  (:require [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
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
