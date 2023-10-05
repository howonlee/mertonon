(ns mtfe.sidebars.admin
  "Mertonon admin sidebar"
  (:require [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn admin-sidebar
  [m]
  [:<>
   [:h1 "Admin Sidebar"]
   [:p "Placeholder for admin sidebar"]])
