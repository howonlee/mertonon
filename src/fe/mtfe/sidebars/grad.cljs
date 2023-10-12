(ns mtfe.sidebars.grad
  "Sidebar for grad kickoff"
  (:require [applied-science.js-interop :as j]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.action-button :as act]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-fx subscribe]]
            [tick.core :as t]))

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
;; Dates
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

(defn stringify-dates [curr-action-params]
  (-> curr-action-params
      (update :start-date #(.toISOString %))
      (update :end-date   #(.toISOString %))))

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
     :validations   [(validations/min-num-elems [:grid-graph :layers] 2 :few-layers)
                     (validations/grouped-min-num-elems
                       [:grid-dump] [:layers :cost-objects] [[:uuid :layer-uuid]] 2 :few-cobjs)
                     (validations/grouped-min-num-elems
                       [:grid-dump] [:weightsets :weights] [[:uuid :weightset-uuid]] 2 :few-weights)
                     (input-has-entries-validation 1)
                     (loss-has-entries-validation 1)

                     (validations/min-num-elems [:grid-graph :weightsets] 1 :no-weightsets)
                     (validations/min-num-elems [:grid-view :inputs] 1 :no-inputs)
                     (validations/min-num-elems [:grid-view :losses] 1 :no-losses)]
     :nav-to        :refresh}))

(defn grad-before-fx [m]
  (let [grid-uuid          (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-action-state (action-config m)]
                   [:select-with-custom-success [:grad :action :grid-graph]
                    (api/grid-graph grid-uuid) {} :sidebar-selection-success]
                   [:select-with-custom-success [:grad :action :grid-view]
                    (api/grid-view grid-uuid) {} :sidebar-selection-success]]]]))

(reg-event-fx
  ::manual-check
  (fn [{:keys [db]} [evt action-params]]
    {:dispatch-n [[:select-with-custom-success [:grad :action :grid-dump]
                                               (api/grid-dump (action-params :grid-uuid))
                                               (stringify-dates action-params)
                                               :sidebar-selection-success]
                  [:validate-action-state [:grad :action]]]}))

;; ---
;; Action Sidebar
;; ---

(defn grad-sidebar [m]
  (let [grid-uuid          (->> m :path-params :uuid)
        curr-config        (action-config m)
        state-path         (curr-config :state-path)
        grid-contents      @(subscribe [:sidebar-state :grad :action :grid-graph :layers])
        curr-action-params @(subscribe [:sidebar-state :grad :action :action-params])]
    [:<>
     [:h1 "Gradient Determination Kickoff"]
     [:p "Currently, determination of gradients and deltas is done by Mertonon but kicked off manually by you, the user. When you press the button Mertonon will go and determine gradients and deltas for weights and cost objects, which comprise Mertonon's combination of local determinations into a global one."]
     [:p [vblurbs/validation-toast state-path :few-layers "Mertonon has to have at least 2 responsibility centers to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :no-weightsets "Mertonon has to have a weightset to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :no-inputs "Mertonon has to have an input annotation to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :no-losses "Mertonon has to have a goal annotation to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :few-cobjs "Mertonon has to have multiple cost objects in each responsibility center to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :few-weights "Mertonon has to have some weights in each weightset to determine a gradient flow."]]
     [:p
      [vblurbs/validation-toast state-path :few-input-entries "Mertonon has to have some entries for the dates selected in the responsibility center corresponding to inputs to determine a gradient flow. Make sure that there's entries specifically in the dates selected, not just whenever."]]
     [:p
      [vblurbs/validation-toast state-path :few-loss-entries "Mertonon has to have some entries for the dates selected in the responsibility center corresponding to goals to determine a gradient flow. Make sure that there's entries specifically in the dates selected, not just whenever."]]
   
     [sc/mgn-border-region
      [sc/form-label "Start Date"]
      [fi/state-datepicker state-path [:action-params :start-date] :mutate-action-state]]
     [sc/mgn-border-region
      [sc/form-label "End Date"]
      [fi/state-datepicker state-path [:action-params :end-date] :mutate-action-state]]
     [sc/mgn-border-region
      [util/evl ::manual-check
       [sc/button "Check if gradient can be kicked off"]
       curr-action-params]
      [act/action-button curr-config]]]))
