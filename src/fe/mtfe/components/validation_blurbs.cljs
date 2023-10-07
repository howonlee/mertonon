(ns mtfe.components.validation-blurbs
  "Blurb components to go and complain when a validation fails"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            ["react-tiny-popover" :refer [Popover]]))


nil
