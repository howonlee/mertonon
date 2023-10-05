(ns mtfe.views.admin
  "Mertonon admin view"
  (:require [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch [:selection :mt-users (api/mt-user) {}]]])

(defn admin-page [m]
  (let [mt-users @(subscribe [:selection :mt-users])]
    [:div.fl.pa2
     [:h1 "Admin"]
     [:p "There are no non-admin accounts at this time. We will add normal user accounts when we do it."]
     (when (seq mt-users)
       [:<>
        [:h2 "Users"]
        [sc/main-table
         [:thead
          [:tr
           [sc/table-head "UUID"]
           [sc/table-head "Username"]
           [sc/table-head "Email"]
           [sc/table-head "Created Date"]
           [sc/table-head "Updated Date"]
           [sc/table-head ""]]]
         [:tbody
          (for [mt-user mt-users] ^{:key (:uuid mt-user)}
            [:tr
             [sc/table-member (str (:uuid mt-user))]
             [sc/table-member (str (:username mt-user))]
             [sc/table-member (str (:email mt-user))]
             [sc/table-member (str (:created-at mt-user))]
             [sc/table-member (str (:updated-at mt-user))]
             [sc/table-member [util/sl (util/path ["mt_user" (str (:uuid mt-user)) "delete"])
                               [sc/trash-icon]]]])]]])]))
