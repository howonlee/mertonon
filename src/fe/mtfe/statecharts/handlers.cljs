(ns mtfe.statecharts.handlers
  "State transition event handlers for statecharts.

  Actual thing-doing is mostly in side-effects, except for the validations which better be non-side-effecting"
  (:require [clojure.set :as cset]
            [mtfe.util :as util]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.sideeffects :as sc-se]
            [re-frame.core :refer [dispatch]]
            ))

;; ---
;; Non-validation handlers
;; ---

(defn refresh-handler [sc-state]
  (fn [env evt]
    (dispatch [:nav-to-sidebar-for-current-main-view])
    (util/refresh!)))

(defn reset-handler
  [state path init-param-fn]
  (fn [env evt] (swap! state assoc-in path (init-param-fn))))

(defn mutation-handler
  "Mutation handler for the transitions within statecharts, not for DOM events"
  [state]
  (fn [env evt]
    (let [path   (-> evt :_event :data :path)]
      (sc-se/mutate-sidebar-state! state path evt))))

(defn creation-handler [api-url-getter create-sc-state member-constructor member-param-list]
  (fn [env evt]
    (let [curr-params (->> evt :_event :data)
          param-list  (vec (for [member-param member-param-list]
                             (curr-params member-param)))
          new-member  (apply member-constructor param-list)]
      (sc-se/post-to-endpoint!
        new-member
        (api-url-getter)
        create-sc-state
        :succeed
        :fail))))

(defn deletion-handler [api-url-getter delete-sc-state]
  (fn [env evt]
    (let [uuid (->> evt :_event :data :uuid str)]
      (sc-se/delete-endpoint! (api-url-getter uuid) delete-sc-state :succeed :fail))))

(defn action-handler [api-url-getter action-sc-state]
  (fn [env evt]
    (let [curr-params (->> evt :_event :data)]
      (sc-se/kickoff-action-endpoint! curr-params (api-url-getter) action-sc-state :succeed :fail))))

;; ---
;; Validation Handlers
;; ---

(defn noop-validation-handler []
  (fn [env evt]
    ;; noop
    {}))

(defn do-validations!
  "Can also be called directly"
  [state validations]
  (let [reses  (vec (for [curr-validation validations]
                      (let [validation-res (curr-validation @state)]
                        (cond
                          (and (some? validation-res) (map? validation-res))
                          validation-res
                          (and (some? validation-res) (keyword? validation-res))
                          {validation-res []}
                          :else
                          {}))))
        errors (apply (partial merge-with into) reses)]
    (swap! state assoc-in [:validation-errors] errors)))

(defn validation-handler
  "You never have just the one, so we get a seq of them"
  [state validations]
  (fn [env evt]
    (do-validations! state validations)))
