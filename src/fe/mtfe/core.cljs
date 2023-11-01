(ns mtfe.core
  "Mertonon Frontend

  In order to actually work with the repl in any way, the octopus starting from this module has to be able to see it
  Hence the weird imports"
  (:require [mtfe.events.core]
            [mtfe.fx]
            [mtfe.generators.core :as gen-core]
            [mtfe.routes :as main-routes]
            [mtfe.sidebars.core :as sidebar]
            [mtfe.stylecomps :as sc]
            [mtfe.subs]
            [mtfe.util :as util]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))

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
    (rf/router main-routes/main-routes)
    (fn [m]
      (do
        (dispatch [:nav-page-match m])
        (dispatch [:nav-sidebar (:path m)])))
    {:use-fragment true})
  (main-mount!)
  (sidebar/init!))
