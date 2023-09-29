(ns mtfe.views.mt-user
  "Mertonon User view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn mt-user-page []
  [sc/main-section
   [:p "AD and SAML and OAUTH for authz and RBAC and ABAC for authn and the rest of that whole menagerie is coming."]])
