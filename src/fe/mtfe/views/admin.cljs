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
     [:<>
      [:h1 "Admin"]
      [:p "There are no non-admin accounts at this time. We will add normal user accounts when we do it."]
      (for [mt-user mt-users] ^{:key (:uuid mt-user)}
        [:pre (with-out-str (cljs.pprint/pprint mt-user))])])])
