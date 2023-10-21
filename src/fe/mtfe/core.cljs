(ns mtfe.core
  "Mertonon Frontend"
  (:require [mtfe.events.core]
            [mtfe.generators.core]
            [mtfe.fx]
            [mtfe.sidebars.core :as sidebar]
            [mtfe.stylecomps :as sc]
            [mtfe.subs]
            [mtfe.views.admin :as admin]
            [mtfe.views.cost-object :as cost-object]
            [mtfe.views.grid :as grid]
            [mtfe.views.grid-selector :as grid-selector]
            [mtfe.views.intro :as intro]
            [mtfe.views.layer :as layer]
            [mtfe.views.mt-user :as mt-user]
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

(defn error-page []
  (let [curr-error @(subscribe [:curr-error])]
    [sc/main-section
     [:h1 "Error"]
     [:p "Something went wrong with Mertonon. File an issue on the "
      [:a {:href "https://github.com/howonlee/mertonon/issues"} "Mertonon issue tracker."]]
     [:p "Include this error message in the issue."]
     [:pre (with-out-str (cljs.pprint/pprint curr-error))]]))

(def main-routes
  "Main browser URL (fragment) routes, as opposed to the separate sidebar routes, or the action routes"
  [["/"                  {:name      ::home
                          :view      grid-selector/grid-selector
                          :before-fx grid-selector/before-fx}]
   ["/intro"             {:name ::intro :view intro/intro-page}]
   ["/login"             {:name ::login :view session/session-page}]
   ["/logout"            {:name ::logout :view session/session-page}]
   ["/error"             {:name ::error :view error-page}]
   ["/admin"             {:name      ::admin
                          :view      admin/admin-page
                          :before-fx admin/before-fx}]
   ["/mt_user"           {:name      ::mt-user
                          :view      mt-user/mt-user-page
                          :before-fx mt-user/before-fx}]
   ["/grid/:uuid"        {:name      ::grid
                          :view      grid/grid-page
                          :before-fx grid/before-fx}]
   ["/grid_demo"         {:name      ::grid-demo
                          :view      grid/grid-page
                          :before-fx grid/demo-before-fx}]
   ["/cost_object/:uuid" {:name      ::cost-object
                          :view      cost-object/cost-object-page
                          :before-fx cost-object/before-fx}]
   ["/layer/:uuid"       {:name      ::layer
                          :view      layer/layer-page
                          :before-fx layer/before-fx}]
   ["/weightset/:uuid"   {:name      ::weightset
                          :view      weightset/weightset-page
                          :before-fx weightset/before-fx}]
   ["/weight/:uuid"      {:name      ::weight
                          :view      weight/weight-page
                          :before-fx weight/before-fx}]])


(defn nav []
  [sc/nav
   [:span.w-20.pa2 (util/fsl "#/" "/" "🥞 Mertonon")]
   [:span.w-80.pa2]
   [:span.w-1.pa2 (util/fsl "#/admin" "/admin" [:i.fa-solid.fa-gear])]
   [:span.w-1.pa2 (util/fsl "#/mt_user" "/mt_user" [:i.fa-solid.fa-user])]])

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
        (dispatch [:nav-route "sidebar-change" (:path m)])))
    {:use-fragment true})
  (main-mount!)
  (sidebar/init!))
