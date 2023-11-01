(ns mtfe.sidebars.core
  "State and core stuff for sidebar, which is itself a separate browsing environment, basically.

  Most things that would become a modal in normal sites go in sidebar"
  (:require [mtfe.api :as api]
            [mtfe.sidebars.routes :as sidebar-routes]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [reitit.frontend :as rf]
            [reitit.core :as re]))

(defn missing-sidebar []
  [:div
   [:h1 "🥞 Mertonon"]
   [:h2 "Sidebar not found"]
   [:p "We messed up linking something up or something, because Mertonon doesn't know what this sidebar is."]])

(defn sidebar []
  (let [curr-sidebar-match @(subscribe [:curr-sidebar-match])
        sidebar-history    @(subscribe [:sidebar-history])]
    [sc/main-sidebar-container
     (if (seq sidebar-history)
       [util/evl :sidebar-histpop [sc/back-icon]])
     (if curr-sidebar-match
       ;; Having the metadata procs refreshes if we have different query params
       (let [view (with-meta (-> curr-sidebar-match :data :view)
                             {:query-params (-> curr-sidebar-match :query-params)})]
         [view curr-sidebar-match])
       [missing-sidebar])]))

(defn init! []
  (util/custom-route-start!
    (rf/router sidebar-routes/sidebar-routes)
    "sidebar-change"
    (fn [m]
      (dispatch [:nav-sidebar-match m]))))
