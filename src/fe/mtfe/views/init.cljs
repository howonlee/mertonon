(ns mtfe.views.init
  "Initial view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.views.layer :as layer-view]
            [mtfe.views.grid :as grid-view]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn init-page [m]
  nil)
