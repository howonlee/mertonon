(ns mtfe.sidebars.core
  "State and router for sidebar, which is itself a separate browsing environment, basically.

  Most things that would become a modal in normal sites go in sidebar"
  (:require [mtfe.sidebars.cost-object :as cost-object]
            [mtfe.sidebars.entry :as entry]
            [mtfe.sidebars.grad :as grad]
            [mtfe.sidebars.grid :as grid]
            [mtfe.sidebars.grid-select :as grid-select]
            [mtfe.sidebars.input :as input]
            [mtfe.sidebars.intro :as intro]
            [mtfe.sidebars.layer :as layer]
            [mtfe.sidebars.loss :as loss]
            [mtfe.sidebars.session :as session]
            [mtfe.sidebars.weight :as weight]
            [mtfe.sidebars.weightset :as weightset]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [reitit.frontend :as rf]
            [reitit.core :as re]))

(defonce sidebar-match (r/atom nil))

(defn home-sidebar []
  [:div
   [:h1 "🥞 Mertonon"]
   [:h2 "Open Pre-Alpha"]
   [:p "Mertonon is a tool for neural organizational management."]
   [:p "In accounting terms, Mertonon is a tool for semi-computational attribution of
        P&L to individual cost objects within responsibility
        centers, for an overall P&L. More than one P&L, targets besides competitiveness, and other KPI's forthcoming."]
   [:p "In computing terms, Mertonon is a neural network model of your organization."]
   [:p "A grid corresponds to an individual neural network and delimits a set of intertwined responsibility centers dealing with a budget."]
   [:p "Click on the Demo button to see a demo, click an existing grid to see
        that grid, or click the + button to create a new grid."]])

(defn admin-sidebar []
  [:div
   [:h1 "Mertonon"]
   [:p "Placeholder for admin panel stuff"]])

(defn user-sidebar []
  [:div
   [:h1 "Mertonon"]
   [:p "Placeholder for user panel stuff"]])

;; It's not restful or hateoas, deal with it for now

(def sidebar-routes
  [["/"                               {:name ::home-sidebar :view home-sidebar}]
   ["/intro"                          {:name ::intro-sidebar :view intro/intro-sidebar}]
   ["/login"                          {:name ::login-sidebar :view session/login-sidebar}]
   ["/logout"                         {:name ::logout-sidebar :view session/logout-sidebar}]
   ["/admin"                          {:name ::admin-sidebar :view admin-sidebar}]
   ["/user"                           {:name ::user-sidebar :view user-sidebar}]

   ["/grid/:uuid/grad_kickoff"        {:name ::grad-sidebar :view grad/grad-sidebar}]

   ["/grid_create"                    {:name ::grid-create-sidebar :view grid-select/grid-create-sidebar}]
   ["/grid/:uuid/delete"              {:name ::grid-delete-sidebar :view grid-select/grid-delete-sidebar}]
   ["/grid/:uuid"                     {:name ::grid-sidebar :view grid/grid-sidebar}]
   ["/grid/:uuid/layer_create"        {:name ::layer-create-sidebar :view layer/layer-create-sidebar}]
   ["/grid/:uuid/weightset_create"    {:name ::weightset-create-sidebar :view weightset/weightset-create-sidebar}]
   ["/grid/:uuid/input_create"        {:name ::input-create-sidebar :view input/input-create-sidebar}]
   ["/grid/:uuid/loss_create"         {:name ::loss-create-sidebar :view loss/loss-create-sidebar}]
   ["/grid_demo"                      {:name ::grid-demo-sidebar :view grid/grid-demo-sidebar}]

   ["/layer/:uuid"                    {:name ::layer-sidebar :view layer/layer-sidebar}]
   ["/layer_selection/:uuid"          {:name ::layer-selection-sidebar :view layer/layer-selection-sidebar}]
   ["/layer/:uuid/delete"             {:name ::layer-delete-sidebar :view layer/layer-delete-sidebar}]
   ["/layer/:uuid/cost_object_create" {:name ::cost-object-create-sidebar :view cost-object/cost-object-create-sidebar}]

   ["/cost_object/:uuid"              {:name ::cost-object-sidebar :view cost-object/cost-object-sidebar}]
   ["/cost_object/:uuid/entry_create" {:name ::entry-create-sidebar :view entry/entry-create-sidebar}]
   ["/cost_object/:uuid/delete"       {:name ::cost-object-delete-sidebar :view cost-object/cost-object-delete-sidebar}]
   ["/entry/:uuid/delete"             {:name ::entry-delete-sidebar :view entry/entry-delete-sidebar}]

   ["/weightset/:uuid"                {:name ::weightset-sidebar :view weightset/weightset-sidebar}]
   ["/weightset_selection/:uuid"      {:name ::weightset-selection-sidebar :view weightset/weightset-selection-sidebar}]
   ["/weightset/:uuid/delete"         {:name ::weightset-delete-sidebar :view weightset/weightset-delete-sidebar}]
   ["/weightset/:uuid/weight_create"  {:name ::weight-create-sidebar :view weight/weight-create-sidebar}]

   ["/weight/:uuid"                   {:name ::weight-sidebar :view weight/weight-sidebar}]
   ["/weight_selection/:uuid"         {:name ::weight-selection-sidebar :view weight/weight-selection-sidebar}]
   ["/weight/:uuid/delete"            {:name ::weight-delete-sidebar :view weight/weight-delete-sidebar}]
   
   ["/input/:uuid/delete"             {:name ::input-delete-sidebar :view input/input-delete-sidebar}]
   ["/loss/:uuid/delete"              {:name ::loss-delete-sidebar :view loss/loss-delete-sidebar}]])

(defn sidebar []
  (let [curr-sidebar-match @(subscribe [:curr-sidebar-match])]
    [sc/main-sidebar-container
     (if curr-sidebar-match
       ;; Having the metadata procs refreshes if we have different query params
       (let [view (with-meta (-> curr-sidebar-match :data :view)
                             {:query-params (-> curr-sidebar-match :query-params)})]
         [view curr-sidebar-match]))]))

;; TODO: sidebar histories

(defn init! []
  (let [curr-page-match @(subscribe [:curr-page-match])]
    (util/custom-route-start!
      (rf/router sidebar-routes)
      "sidebar-change"
      (fn [m] 
        (do
          (println m)
          (dispatch [:nav-sidebar m])))
      ;; TODO: be more sophisticated about the default grid match...
      (or (:path curr-page-match) "/"))))
