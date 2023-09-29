(ns mtfe.views.mt-user
  "Mertonon User view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn before-fx [m]
  [[:dispatch [:selection :curr-mt-user (api/curr-mt-user) {}]]])

(defn mt-user-page [m]
  [sc/main-section
   ;; (let some crap
   "some crap here"])
