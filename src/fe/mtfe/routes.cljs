(ns mtfe.routes
  "Main routes. Sidebar routes are in sidebar folder"
  (:require [cljs.pprint]
            [mtfe.stylecomps :as sc]
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
            [re-frame.core :refer [subscribe]]))

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
   ["/error"             {:name              ::error
                          :view              error-page
                          :exclude-from-gen? true}]
   ["/admin"             {:name      ::admin
                          :view      admin/admin-page
                          :before-fx admin/before-fx}]
   ["/mt_user"           {:name      ::mt-user
                          :view      mt-user/mt-user-page
                          :before-fx mt-user/before-fx}]
   ["/grid/:uuid"        {:name      ::grid
                          :view      grid/grid-page
                          :before-fx grid/before-fx
                          :param-gen {":uuid" :grids}}]
   ["/grid_demo"         {:name      ::grid-demo
                          :view      grid/grid-page
                          :before-fx grid/demo-before-fx}]
   ["/cost_object/:uuid" {:name      ::cost-object
                          :view      cost-object/cost-object-page
                          :before-fx cost-object/before-fx
                          :param-gen {":uuid" :cost-objects}}]
   ["/layer/:uuid"       {:name      ::layer
                          :view      layer/layer-page
                          :before-fx layer/before-fx
                          :param-gen {":uuid" :layers}}]
   ["/weightset/:uuid"   {:name      ::weightset
                          :view      weightset/weightset-page
                          :before-fx weightset/before-fx
                          :param-gen {":uuid" :weightsets}}]
   ["/weight/:uuid"      {:name      ::weight
                          :view      weight/weight-page
                          :before-fx weight/before-fx
                          :param-gen {":uuid" :weights}}]])
