(ns mtfe.statecharts.sideeffects
  "Side effecting stuff for statecharts.

  This is the main side-effect thingy of the FE app overall.

  Basically all side-effecting stuff should be from calling functions that get defined from here"
  (:require [ajax.core :refer [GET POST DELETE]]
            [mtfe.api :as api]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.util :as util]
            [reagent.core :as r]
            [reitit.frontend :as rf]))

(defn post-to-endpoint!
  "Returns a function that can be an api action to post to the endpoint"
  [query-params api-endpoint sc-state success-event failure-event]
  (POST api-endpoint {:params        query-params
                      :format        :json
                      :handler       (fn [resp]
                                       (mt-statechart/send-event-and-reset! sc-state success-event {:resp resp}))
                      :error-handler (fn [status status-text]
                                       (mt-statechart/send-event-and-reset!
                                         sc-state
                                         failure-event
                                         {:status status :status-text status-text})) }))

(defn delete-endpoint!
  "Api action to delete the endpoint"
  [api-endpoint sc-state success-event failure-event]
  (DELETE api-endpoint {:params        {}
                        :format        :json
                        :handler       (fn [resp]
                                         (mt-statechart/send-event-and-reset! sc-state success-event {:resp resp}))
                        :error-handler (fn [status status-text]
                                         (mt-statechart/send-event-and-reset!
                                           sc-state
                                           failure-event
                                           {:status status :status-text status-text}))}))

(defn kickoff-action-endpoint!
  "Action endpoints should just take a POST - this kicks off the POST"
  [query-params api-endpoint sc-state success-event failure-event]
  (POST api-endpoint {:params        query-params
                      :format        :json
                      :handler       (fn [resp]
                                       (mt-statechart/send-event-and-reset! sc-state success-event {:resp resp}))
                      :error-handler (fn [status status-text]
                                       (mt-statechart/send-event-and-reset!
                                         sc-state
                                         failure-event
                                         {:status status :status-text status-text}))}))

(defn mutate-sidebar-state!
  "Mutate some state from a JS change event value or from some rando result from a function or something"
  [state path change]
  (let [datum (->> change :_event :data)]
    (cond
      (contains? datum :event)
      (swap! state assoc-in path (.. (datum :event)
                                     -nativeEvent
                                     -srcElement
                                     -value))
      (contains? datum :result)
      (swap! state assoc-in path (datum :result)))))
