(ns mtfe.selectors
  "Data source thingies
  Really, this is a half-assed reimplementation of reagent state but with a network hop
  We need to think pretty hard about that network hop so we put them all here"
  (:require [ajax.core :refer [GET POST]]
            [mtfe.util :as util]))

;; ---
;; General Selection making
;; ---

;; Generally, there's a lotta state mutation-hooking going on here
;; This is tremendously fragile and spams requests if something fucks up a bit
;; The half-assed stopgap is to specialize the filling but we do need a less dynamically unstable solution

(defn set-state-with-results!
  "Mutates the sidebar state to fill arbitrary results from api endpoint.

  You probably want set-state-if-changed! instead"
  [state api-endpoint-fn state-path & [uuid]]
  (let [endpoint (if (some? uuid) (api-endpoint-fn uuid) (api-endpoint-fn))]
    (GET endpoint
         {:handler (fn [resp] (swap! state assoc-in state-path (util/json-parse resp)))})))

(defn set-state-with-query-results!
  "You probably want set-query-state-if-changed! instead"
  [state api-endpoint-fn state-path query-params & [uuid]]
  (let [endpoint (if (some? uuid) (api-endpoint-fn uuid) (api-endpoint-fn))]
    (GET endpoint
         {:params  (util/iso-date-params query-params)
          :format  :json
          :handler (fn [resp] (swap! state assoc-in state-path (util/json-parse resp)))})))

(defn set-state-if-changed!
  "Mutates sidebar state to fill arbitray results in path from api endpoint but only if it changed"
  [state api-endpoint-fn maybe-changed-uuid uuid-get-path set-path]
  (when (not= maybe-changed-uuid (get-in @state uuid-get-path))
    (set-state-with-results! state api-endpoint-fn set-path maybe-changed-uuid)))

(defn set-query-state-if-changed!
  [state
   api-endpoint-fn
   maybe-changed-uuid
   maybe-changed-query-params
   uuid-get-path
   query-get-path
   set-path]
  (when (or (not= maybe-changed-uuid (get-in @state uuid-get-path))
            (not= maybe-changed-query-params (get-in @state query-get-path)))
    (set-state-with-query-results! state api-endpoint-fn set-path maybe-changed-query-params maybe-changed-uuid)))

(defn set-selection!
  "Mutates the sidebar state to fill selection from api endpoint.

  You probably want set-selection-if-changed! instead"
  [state api-endpoint-fn & [uuid]]
  (set-state-with-results! state api-endpoint-fn [:selection] uuid))

(defn set-selection-if-changed!
  "Mutates sidebar state to fill selection from api endpoint but only if it changed"
  [state api-endpoint-fn maybe-changed-uuid uuid-get-path]
  (when (not= maybe-changed-uuid (get-in @state uuid-get-path))
    (set-selection! state api-endpoint-fn maybe-changed-uuid)))

(defn swap-if-changed!
  [maybe-changed state state-path]
  (when (not= maybe-changed (get-in @state state-path))
    (swap! state assoc-in state-path maybe-changed)))
