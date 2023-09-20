(ns mtfe.views.intro
  "Introduction view to look at while you fill out deets in the intro sidebar"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.views.grid :as grid-view]
            [mtfe.views.layer :as layer-view]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn intro-page-render []
  [:div
   [:h1 "🥞 Mertonon"]
   [:h2 "Open Pre-Alpha"]
   [:p "Mertonon is a tool for neural organizational management."]
   [:p "In accounting terms, Mertonon is a tool for semi-computational attribution of
       P&L to individual cost objects within responsibility
       centers, for an overall P&L. More than one P&L, targets besides competitiveness, and other KPI's forthcoming."]
   [:p "In computing terms, Mertonon is a neural network model of your organization."]
   [:p "Create an administrator account from the sidebar."]])

(defn intro-page [m]
  [intro-page-render])
