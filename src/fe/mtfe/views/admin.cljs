(ns mtfe.views.admin
  "Mertonon admin view"
  (:require [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch-n [[:selection
                  :mt-user-password-logins
                  (api/mt-user-password-login)
                  {}]]]])

(defn login-view [curr-password-login]
  (let [message (if (seq curr-password-login)
                  "Password"
                  "None")]
    [sc/table-member message
     ;; link to deletion or creation or whatever
     ]))

(defn admin-page [m]
  (let [mt-users @(subscribe [:selection
                              :mt-user-password-logins
                              :mertonon.mt-users])
        grouped-logins (group-by :mt-user-uuid @(subscribe [:selection
                              :mt-user-password-logins
                              :mertonon.password-logins]))]
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
           [sc/table-head "Username - Email"]
           [sc/table-head "Created Date"]
           [sc/table-head "Updated Date"]
           [sc/table-head "Authentication Type"]
           [sc/table-head ""]]]
         [:tbody
          (for [mt-user mt-users] ^{:key (:uuid mt-user)}
            [:tr
             [sc/table-member (str (:uuid mt-user))]
             [sc/table-member (str (mt-user :username) " - " (mt-user :email))]
             [sc/table-member (str (:created-at mt-user))]
             [sc/table-member (str (:updated-at mt-user))]
             [login-view (grouped-logins (mt-user :uuid))]
             [sc/table-member [util/sl (util/path ["mt_user" (str (:uuid mt-user)) "delete"])
                               [sc/trash-icon]]]])]]])]))
