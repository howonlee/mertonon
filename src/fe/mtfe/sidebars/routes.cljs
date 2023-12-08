(ns mtfe.sidebars.routes
  "Router for sidebar."
  (:require [mtfe.api :as api]
            [mtfe.sidebars.admin :as admin]
            [mtfe.sidebars.cost-object :as cost-object]
            [mtfe.sidebars.entry :as entry]
            [mtfe.sidebars.grad :as grad]
            [mtfe.sidebars.grid :as grid]
            [mtfe.sidebars.grid-select :as grid-select]
            [mtfe.sidebars.input :as input]
            [mtfe.sidebars.intro :as intro]
            [mtfe.sidebars.layer :as layer]
            [mtfe.sidebars.loss :as loss]
            [mtfe.sidebars.mt-user :as mt-user]
            [mtfe.sidebars.password-login :as password-login]
            [mtfe.sidebars.session :as session]
            [mtfe.sidebars.weight :as weight]
            [mtfe.sidebars.weightset :as weightset]))

(defn home-sidebar []
  [:div
   [:h1 "🥞 Mertonon"]
   [:h2 "Open Post-Pre-Alpha"]
   [:p "Mertonon is a tool for neural organizational management."]
   [:p "In accounting terms, Mertonon is a tool for semi-computational attribution of
        P&L to individual cost objects within responsibility
        centers, for an overall P&L. More than one P&L, targets besides conformance, and other KPI's forthcoming."]
   [:p "In computing terms, Mertonon is a neural network model of your organization."]
   [:p "A grid corresponds to an individual neural network and delimits a set of intertwined responsibility centers dealing with a budget."]
   [:p "Click on the Demo button to see a demo, click an existing grid to see
        that grid, or click the + button to create a new grid."]])

(def sidebar-routes
  ;; ---
  ;; Keep it in recursive alphabetical order!
  ;; ---
  [["/"
    {:name ::home-sidebar :view home-sidebar}]
   ["/admin"
    {:name ::admin-sidebar :view admin/admin-sidebar}]
   ["/admin/mt_user_create"
    {:name      ::mt-user-create-sidebar
     :view      mt-user/mt-user-create-sidebar
     :before-fx mt-user/mt-user-create-before-fx}]

   ["/cost_object/:uuid"
    {:name      ::cost-object-sidebar
     :view      cost-object/cost-object-sidebar
     :before-fx cost-object/cost-object-sidebar-before-fx
     :param-gen {":uuid" [:cost-objects :uuid]}}]
   ["/cost_object/:uuid/delete"
    {:name      ::cost-object-delete-sidebar
     :view      cost-object/cost-object-delete-sidebar
     :before-fx cost-object/cost-object-delete-before-fx
     :param-gen {":uuid" [:cost-objects :uuid]}}]
   ["/cost_object/:uuid/entry_create"
    {:name      ::entry-create-sidebar
     :view      entry/entry-create-sidebar
     :before-fx entry/entry-create-before-fx
     :param-gen {":uuid" [:cost-objects :uuid]}}]
   ["/cost_object/:uuid/update"
    {:name      ::cost-object-update-sidebar
     :view      cost-object/cost-object-update-sidebar
     :before-fx cost-object/cost-object-update-before-fx
     :param-gen {":uuid" [:cost-objects :uuid]}}]

   ["/entry/:uuid/delete"
    {:name      ::entry-delete-sidebar
     :view      entry/entry-delete-sidebar
     :before-fx entry/entry-delete-before-fx
     :param-gen {":uuid" [:entries :uuid]}}]
   ["/entry/:uuid/update"
    {:name      ::entry-update-sidebar
     :view      entry/entry-update-sidebar
     :before-fx entry/entry-update-before-fx
     :param-gen {":uuid" [:entries :uuid]}}]

   ["/grid/:uuid"
    {:name      ::grid-sidebar
     :view      grid/grid-sidebar
     :before-fx grid/before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/delete"
    {:name      ::grid-delete-sidebar
     :view      grid-select/grid-delete-sidebar
     :before-fx grid-select/grid-delete-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/grad_kickoff"
    {:name      ::grad-sidebar
     :view      grad/grad-sidebar
     :before-fx grad/grad-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/input_create"
    {:name      ::input-create-sidebar
     :view      input/input-create-sidebar
     :before-fx input/input-create-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/layer_create"
    {:name      ::layer-create-sidebar
     :view      layer/layer-create-sidebar
     :before-fx layer/layer-create-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/loss_create"
    {:name      ::loss-create-sidebar
     :view      loss/loss-create-sidebar
     :before-fx loss/loss-create-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/update"
    {:name      ::grid-update-sidebar
     :view      grid-select/grid-update-sidebar
     :before-fx grid-select/grid-update-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]
   ["/grid/:uuid/weightset_create"
    {:name      ::weightset-create-sidebar
     :view      weightset/weightset-create-sidebar
     :before-fx weightset/weightset-create-before-fx
     :param-gen {":uuid" [:grids :uuid]}}]

   ["/grid_create"
    {:name      ::grid-create-sidebar
     :view      grid-select/grid-create-sidebar
     :before-fx grid-select/grid-create-before-fx}]

   ["/grid_demo"
    {:name      ::grid-demo-sidebar
     :view      grid/grid-sidebar
     :before-fx grid/demo-before-fx}]

   ["/input/:uuid/delete"
    {:name      ::input-delete-sidebar
     :view      input/input-delete-sidebar
     :before-fx input/input-delete-before-fx
     :param-gen {":uuid" [:inputs :uuid]}}]
   ["/input/:uuid/update"
    {:name      ::input-update-sidebar
     :view      input/input-update-sidebar
     :before-fx input/input-update-before-fx
     :param-gen {":uuid" [:inputs :uuid]}}]
   ["/intro"
    {:name      ::intro-sidebar
     :view      intro/intro-sidebar
     :before-fx intro/intro-before-fx}]

   ["/layer/:uuid"
    {:name      ::layer-sidebar
     :view      layer/layer-sidebar
     :before-fx layer/layer-sidebar-before-fx
     :param-gen {":uuid" [:layers :uuid]}}]
   ["/layer/:uuid/cost_object_create"
    {:name      ::cost-object-create-sidebar
     :view      cost-object/cost-object-create-sidebar
     :before-fx cost-object/cost-object-create-before-fx
     :param-gen {":uuid" [:layers :uuid]}}]
   ["/layer/:uuid/delete"
    {:name      ::layer-delete-sidebar
     :view      layer/layer-delete-sidebar
     :before-fx layer/layer-delete-before-fx
     :param-gen {":uuid" [:layers :uuid]}}]
   ["/layer/:uuid/update"
    {:name      ::layer-update-sidebar
     :view      layer/layer-update-sidebar
     :before-fx layer/layer-update-before-fx
     :param-gen {":uuid" [:layers :uuid]}}]

   ["/layer_selection/:uuid"
    {:name      ::layer-selection-sidebar :view layer/layer-selection-sidebar
     :param-gen {":uuid" [:layers :uuid]}}]

   ["/login"
    {:name      ::login-sidebar
     :view      session/login-sidebar
     :before-fx session/login-before-fx}]

   ["/logout"
    {:name      ::logout-sidebar
     :view      session/logout-sidebar
     :before-fx session/logout-before-fx}]

   ["/loss/:uuid/delete"
    {:name      ::loss-delete-sidebar
     :view      loss/loss-delete-sidebar
     :before-fx loss/loss-delete-before-fx
     :param-gen {":uuid" [:losses :uuid]}}]
   ["/loss/:uuid/update"
    {:name      ::loss-update-sidebar
     :view      loss/loss-update-sidebar
     :before-fx loss/loss-update-before-fx
     :param-gen {":uuid" [:losses :uuid]}}]

   ["/mt_user"
    {:name ::mt-user-sidebar :view mt-user/mt-user-sidebar}]
   ["/mt_user/:uuid/delete"
    {:name      ::mt-user-delete-sidebar
     :view      mt-user/mt-user-delete-sidebar
     :before-fx mt-user/mt-user-delete-before-fx
     :param-gen {":uuid" [:mt-users :uuid]}}]
   ["/mt_user/:uuid/password_login_create"
    {:name      ::password-login-create-sidebar
     :view      password-login/password-login-create-sidebar
     :before-fx password-login/password-login-create-before-fx
     :param-gen {":uuid" [:mt-users :uuid]}}]

   ["/password_login/:uuid/delete"
    {:name      ::password-login-delete-sidebar
     :view      password-login/password-login-delete-sidebar
     :before-fx password-login/password-login-delete-before-fx
     :param-gen {":uuid" [:password-logins :uuid]}}]

   ["/weight/:uuid"
    {:name ::weight-sidebar :view weight/weight-sidebar
     :param-gen {":uuid" [:weights :uuid]}}]
   ["/weight/:uuid/delete"
    {:name      ::weight-delete-sidebar
     :view      weight/weight-delete-sidebar
     :before-fx weight/weight-delete-before-fx
     :param-gen {":uuid" [:weights :uuid]}}]
   ["/weight/:uuid/update"
    {:name      ::weight-update-sidebar
     :view      weight/weight-update-sidebar
     :before-fx weight/weight-update-before-fx
     :param-gen {":uuid" [:weights :uuid]}}]

   ["/weight_selection/:uuid"
    {:name      ::weight-selection-sidebar
     :view      weight/weight-selection-sidebar
     :before-fx weight/weight-selection-before-fx
     :param-gen {":uuid" [:weights :uuid]}}]

  ["/weightset/:uuid"
    {:name      ::weightset-sidebar
     :view      weightset/weightset-sidebar
     :before-fx weightset/weightset-before-fx
     :param-gen {":uuid" [:weightsets :uuid]}}]
   ["/weightset/:uuid/delete"
    {:name      ::weightset-delete-sidebar
     :view      weightset/weightset-delete-sidebar
     :before-fx weightset/weightset-delete-before-fx
     :param-gen {":uuid" [:weightsets :uuid]}}]
   ["/weightset/:uuid/update"
    {:name      ::weightset-update-sidebar
     :view      weightset/weightset-update-sidebar
     :before-fx weightset/weightset-update-before-fx
     :param-gen {":uuid" [:weightsets :uuid]}}]
   ["/weightset/:uuid/weight_create"
    {:name      ::weight-create-sidebar
     :view      weight/weight-create-sidebar
     :before-fx weight/weight-create-before-fx
     :param-gen {":uuid" [:weightsets :uuid]}}]

   ["/weightset_selection/:uuid"
    {:name ::weightset-selection-sidebar :view weightset/weightset-selection-sidebar
     :param-gen {":uuid" [:weightsets :uuid]}}]])
