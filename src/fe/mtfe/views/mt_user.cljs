(ns mtfe.views.mt-user
  "Mertonon User view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]))

(defn before-fx [m]
  [[:dispatch [:selection :curr-mt-user (api/currMtUserApi) {}]]])

(defn mt-user-page [m]
  [sc/main-section
   (let [curr-user @(subscribe [:selection :curr-mt-user])
         ;;;; destructure
         ]
     [:pre (with-out-str (cljs.pprint/pprint curr-user))])])
