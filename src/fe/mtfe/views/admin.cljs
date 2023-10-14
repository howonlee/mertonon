(ns mtfe.views.admin
  "Mertonon admin view"
  (:require [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch-n [[:selection :mt-users (api/mt-user) {}]
                 [:selection :mt-user-password-logins (api/mt-user-password-login) {}]]]])

(defn login-view [curr-mt-user curr-password-login]
  (let [state   (if (seq curr-password-login) :password :none)
        message ({:password "Password"
                  :none     "None"} state)
        link    ({:password
                  [:span.ma2 (util/sl (util/path ["password_login" (get curr-password-login :uuid nil) "delete"]) [sc/trash-icon])]
                  :none
                  [:span.ma2 (util/sl (util/path ["mt_user" (curr-mt-user :uuid) "password_login_create"]) [sc/plus-icon])]} state)]
    [sc/table-member message link]))

(defn admin-page [m]
  (let [mt-users       @(subscribe [:selection
                                    :mt-users])
        grouped-logins (group-by
                         :mt-user-uuid
                         @(subscribe [:selection :mt-user-password-logins :mertonon.password-logins]))]
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
            (let [curr-password-login (first (grouped-logins (mt-user :uuid)))]
              [:tr
               [sc/table-member (str (:uuid mt-user))]
               [sc/table-member (str (mt-user :username) " - " (mt-user :email))]
               [sc/table-member (str (:created-at mt-user))]
               [sc/table-member (str (:updated-at mt-user))]
               [login-view mt-user curr-password-login]
               [sc/table-member [util/sl (util/path ["mt_user" (str (:uuid mt-user)) "delete"])
                                 [sc/trash-icon]]]]))]]])]))
