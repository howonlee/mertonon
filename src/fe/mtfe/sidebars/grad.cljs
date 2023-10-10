(ns mtfe.sidebars.grad
  "Sidebar for grad kickoff"
  (:require [mtfe.components.validation-blurbs :as vblurbs]
    [applied-science.js-interop :as j]
            [com.fulcrologic.statecharts :as fsc]
            [com.fulcrologic.statecharts.protocols :as sp]
            [com.fulcrologic.statecharts.simple :as simple]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.action-button :as act]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.selectors :as sel]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]
            [tick.core :as t]))

;; ---
;; State
;; ---

(defn last-week []
  (-> (t/<< (t/instant) (t/new-duration 7 :days))
      (t/long)
      (* 1000)
      (js/Date.)))

(defn tomorrow []
  (-> (t/>> (t/instant) (t/new-duration 1 :days))
      (t/long)
      (* 1000)
      (js/Date.)))

(defn init-grad-params []
  {:start-date (last-week)
   :end-date   (tomorrow)
   :grid-uuid  ""})

(defonce sidebar-state (r/atom {:curr-action-params (init-grad-params)}))

;; ---
;; Validations
;; ---

;; TODO: shove this into the backend, querying for this directly without making the query terrifying.
;; TODO: scaling is pretty bad
(defn input-has-entries-validation
  [min-num]
  (fn [state]
    (let [grouped-cobjs      (group-by :uuid (->> state :grid-dump :cost-objects))
          grouped-entries    (group-by
                               (fn [member]
                                 (->> member :cobj-uuid grouped-cobjs first :layer-uuid))
                               (->> state :grid-dump :entries))
          inputs-layer-uuids (->> state :grid-dump :inputs (map :layer-uuid) (apply hash-set))
          filtered-entries   (into {} (filter (fn [[layer-uuid entry]]
                                                (contains? inputs-layer-uuids layer-uuid)) grouped-entries))
          has-entries?       (every?
                               (fn [layer-uuid] (<= min-num (count (filtered-entries layer-uuid))))
                               inputs-layer-uuids)]
      (if has-entries?
        nil
        :few-input-entries))))

(defn loss-has-entries-validation
  [min-num]
  (fn [state]
    (let [grouped-cobjs      (group-by :uuid (->> state :grid-dump :cost-objects))
          grouped-entries    (group-by
                               (fn [member]
                                 (->> member :cobj-uuid grouped-cobjs first :layer-uuid))
                               (->> state :grid-dump :entries))
          losses-layer-uuids (->> state :grid-dump :losses (map :layer-uuid) (apply hash-set))
          filtered-entries   (into {} (filter (fn [[layer-uuid entry]]
                                                (contains? losses-layer-uuids layer-uuid)) grouped-entries))
          has-entries?       (every?
                               (fn [layer-uuid] (<= min-num (count (filtered-entries layer-uuid))))
                               losses-layer-uuids)]
      (if has-entries?
        nil
        :few-loss-entries))))

;; ---
;; Statechart
;; ---

(defonce action-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/min-num-elems [:grid-graph :layers] 2 :few-layers)
   (sc-validation/grouped-min-num-elems
     [:grid-dump] [:layers :cost-objects] [[:uuid :layer-uuid]] 2 :few-cobjs)
   (sc-validation/grouped-min-num-elems
     [:grid-dump] [:weightsets :weights] [[:uuid :weightset-uuid]] 2 :few-weights)
   (input-has-entries-validation 1)
   (loss-has-entries-validation 1)

   (sc-validation/min-num-elems [:grid-graph :weightsets] 1 :no-weightsets)
   (sc-validation/min-num-elems [:grid-view :inputs] 1 :no-inputs)
   (sc-validation/min-num-elems [:grid-view :losses] 1 :no-losses)])

(def action-sc
  (mt-statechart/simple-action :grad-kickoff
                               {:action-fn     (sc-handlers/action-handler api/grid-grad action-sc-state)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                :finalize-fn   (sc-handlers/refresh-handler action-sc-state)}))

(mt-statechart/init-sc! :grad-kickoff action-sc-state action-sc)

;; TODO: Get job kickoffs going as opposed to current synchronous thing

;; ---
;; Action
;; ---

(defn action-config [m]
  (let [grid-uuid          (->> m :path-params :uuid)]
    {:resource      :curr-grad
     :endpoint      (api/grid-grad)
     :state-path    [:grad :action]
     :init-state-fn (fn []
                      {:start-date (last-week)
                       :end-date   (tomorrow)
                       :grid-uuid  grid-uuid})
     :validations   []
     :nav-to        :refresh}))

(defn grad-before-fx [m]
  (let [grid-uuid          (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-action-state (action-config m)]
                   [:select-with-custom-success [:grad :action :grid-graph]
                    (api/grid-graph grid-uuid) {} :sidebar-selection-success]
                   [:select-with-custom-success [:grad :action :grid-view]
                    (api/grid-view grid-uuid) {} :sidebar-selection-success]]]]))

(defn grad-sidebar [m]
  (let [grid-uuid          (->> m :path-params :uuid)
        curr-config        (action-config m)
        state-path         (curr-config :state-path)
        grid-contents      @(subscribe [:sidebar-state :grad :action :grid-graph :layers])
        curr-action-params @(subscribe [:sidebar-state :grad :action :action-params])
        printo             (println curr-action-params)
        _                  (when (seq curr-action-params)
                             (do
                               (dispatch-sync [:select-with-custom-success [:grad :action :grid-dump]
                                               (api/grid-dump grid-uuid) curr-action-params :sidebar-selection-success])
                               (dispatch-sync [:validate-action-state [:grad :action]])))]
    [:<>]))
  ;; [:<>
  ;;  [:h1 "Gradient Determination Kickoff"]
  ;;  [:p "Currently, determination of gradients and deltas is done by Mertonon but kicked off manually by you, the user. When you press the button Mertonon will go and determine gradients and deltas for weights and cost objects, which comprise Mertonon's combination of local determinations into a global one."]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :few-layers "Mertonon has to have at least 2 responsibility centers to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :no-weightsets "Mertonon has to have a weightset to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :no-inputs "Mertonon has to have an input annotation to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :no-losses "Mertonon has to have a goal annotation to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :few-cobjs "Mertonon has to have multiple cost objects in each responsibility center to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :few-weights "Mertonon has to have some weights in each weightset to determine a gradient flow."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :few-input-entries "Mertonon has to have some entries for the dates selected in the responsibility center corresponding to inputs to determine a gradient flow. Make sure that there's entries specifically in the dates selected, not just whenever."]]
  ;;  [:p
  ;;   [sc-components/validation-toast sidebar-state :few-loss-entries "Mertonon has to have some entries for the dates selected in the responsibility center corresponding to goals to determine a gradient flow. Make sure that there's entries specifically in the dates selected, not just whenever."]]
  ;;  [sc/mgn-border-region
  ;;   [sc/form-label "Start Date"]
  ;;   [sc-components/state-datepicker action-sc-state sidebar-state [:curr-action-params :start-date]]]
  ;;  [sc/mgn-border-region
  ;;   [sc/form-label "End Date"]
  ;;   [sc-components/state-datepicker action-sc-state sidebar-state [:curr-action-params :end-date]]]
  ;;  [sc/mgn-border-region
  ;;   [sc-components/action-button @action-sc-state action-sc-state sidebar-state]]])

;; ---
;; Top-level render
;; ---

;; (defn grad-sidebar [m]
;;   (let [grid-uuid (->> m :path-params :uuid str)]
;;     (sel/swap-if-changed! grid-uuid sidebar-state [:curr-action-params :grid-uuid])
;;     (sel/set-state-if-changed! sidebar-state
;;                                api/grid-graph
;;                                grid-uuid
;;                                [:grid-graph :grids 0 :uuid]
;;                                [:grid-graph])
;;     (sel/set-state-if-changed! sidebar-state
;;                                api/grid-view
;;                                grid-uuid
;;                                [:grid-view :grids 0 :uuid]
;;                                [:grid-view])
;;     (sel/set-query-state-if-changed! sidebar-state
;;                                      api/grid-dump
;;                                      grid-uuid
;;                                      (@sidebar-state :curr-action-params)
;;                                      [:grid-dump :grids 0 :uuid]
;;                                      [:grid-dump :query]
;;                                      [:grid-dump])
;;     (mt-statechart/send-reset-event-if-finished! action-sc-state)
;;     (fn [m]
;;       (sc-handlers/do-validations! sidebar-state validation-list)
;;       [grad-sidebar-render m])))
