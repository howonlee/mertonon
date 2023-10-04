(ns mtfe.views.admin
  "Mertonon admin view"
  (:require [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch [:selection :mt-users (api/mt-user) {}]]])

(defn admin-page [m]
  [sc/main-section
   (let [mt-users @(subscribe [:selection :mt-users])]
     [:<>]
     )])
