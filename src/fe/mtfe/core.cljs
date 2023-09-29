(ns mtfe.core
  "Mertonon Frontend"
  (:require [mtfe.events.core]
            [mtfe.fx]
            [mtfe.subs.core]
            [mtfe.sidebars.core :as sidebar]
            [mtfe.stylecomps :as sc]
            [mtfe.views.cost-object :as cost-object]
            [mtfe.views.grid :as grid]
            [mtfe.views.grid-selector :as grid-selector]
            [mtfe.views.intro :as intro]
            [mtfe.views.layer :as layer]
            [mtfe.views.session :as session]
            [mtfe.views.weightset :as weightset]
            [mtfe.views.weight :as weight]
            [mtfe.util :as util]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))

(defn admin-page []
  [sc/main-section
   [:h1 "Admin"]
   [:p "We don't have an admin screen yet. Placeholder for admin screen here."]])

(defn user-page []
  [sc/main-section
   [:h1 "User"]
   [:p "We don't have user accounts yet. They're coming, along with AD and SAML and OAUTH for authz and RBAC and ABAC for authn and the rest of that whole menagerie. Placeholder for user screen here."]])

(def main-routes
  "Main browser URL (fragment) routes, as opposed to the separate sidebar routes, or the action routes"
  [["/"                  {:name ::home
                          :view grid-selector/grid-selector
                          :before-fx grid-selector/before-fx}]
   ["/intro"             {:name ::intro :view intro/intro-page}]
   ["/login"             {:name ::login :view session/session-page}]
   ["/logout"            {:name ::logout :view session/session-page}]
   ["/admin"             {:name ::admin :view admin-page}]
   ["/user"              {:name ::user :view user-page}]
   ["/grid/:uuid"        {:name ::grid :view grid/grid-page}]
   ["/grid_demo"         {:name ::grid-demo :view grid/grid-demo-page}]
   ["/cost_object/:uuid" {:name ::cost-object :view cost-object/cost-object-page}]
   ["/layer/:uuid"       {:name ::layer :view layer/layer-page}]
   ["/weightset/:uuid"   {:name ::weightset :view weightset/weightset-page}]
   ["/weight/:uuid"      {:name ::weight :view weight/weight-page}]])


(defn nav []
  [sc/nav
   [:span.w-20.pa2 (util/fsl "#/" "/" "🥞 Mertonon")]
   [:span.w-80.pa2]
   [:span.w-1.pa2 (util/fsl "#/admin" "/admin" [:i.fa-solid.fa-gear])]
   [:span.w-1.pa2 (util/fsl "#/user" "/user" [:i.fa-solid.fa-user])]])

(defn mertonon-app
  []
  (let [curr-page-match @(subscribe [:curr-page-match])]
    [sc/whole-page
     [nav]
     (when curr-page-match
       (let [view (with-meta (-> curr-page-match :data :view)
                             {:query-params (-> curr-page-match :query-params)})]
         [sc/main-section-container
          [view curr-page-match]]))
     [sidebar/sidebar]]))

(defn main-mount!
  "Mounts the main page. Can also just be called to refresh app"
  []
  (let [app-elem (.getElementById js/document "app")]
    (rdom/unmount-component-at-node app-elem)
    (rdom/render [mertonon-app] app-elem)))

(defn init! []
  (rfe/start!
    (rf/router main-routes)
    (fn [m]
      (do
        (dispatch [:nav-page-match m])
        (util/to-router-path! "sidebar-change" (:path m))))
    {:use-fragment true})
  (main-mount!)
  (sidebar/init!))
