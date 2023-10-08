(ns mtfe.views.weightset
  "Weightset / Matrix view."
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [goog.color :as gcolor]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.views.grid :as grid-view]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]))

;; ---
;; Before-fx
;; ---

(defn before-fx [m]
  (let [is-demo?    @(subscribe [:is-demo?])
        uuid        (->> m :path-params :uuid)
        ws-endpoint (if is-demo?
                        (api/generator-weightset uuid)
                        (api/weightset-view uuid))]
    [[:dispatch [:selection :ws-view ws-endpoint {}]]]))

(defn cost-object-member [cost-object]
  [:div.pa3
   (util/path-fsl ["cost_object" (:uuid cost-object)]
                  [:div (subs (str (:name cost-object)) 0 6)])])

(defn weight-member [weight max-weight-val ws-mode]
  (let [curr-val (condp = ws-mode
                   :grad (- (:value weight) (:grad weight))
                   ;; else
                   (:value weight))
        frac     (int (* 128 (/ curr-val max-weight-val)))]
    [:div.pa3
     {:style {:background-color (gcolor/rgbToHex frac frac frac)}}
     (util/staged-fsl (util/hash-path ["weight" (:uuid weight)])
                      (util/path ["weight_selection" (:uuid weight)])
                      [:div (subs (str (:uuid weight)) 0 6)])]))

(defn to-create-member [src-cobj tgt-cobj ws-uuid]
  (let [src-cobj-uuid (:uuid src-cobj)
        tgt-cobj-uuid (:uuid tgt-cobj)]
    (util/sl (util/path ["weightset" ws-uuid "weight_create"] {:src_cobj_uuid src-cobj-uuid :tgt_cobj_uuid tgt-cobj-uuid})
             [sc/button
              [sc/plus-icon]])))

(defn display-matrix
  "Ad hoc DOK thing to have other stuff in there eventually"
  [{:keys [src-cobjs tgt-cobjs weights] :as ws-state} ws-mode]
  (let [max-weight-val   (condp = ws-mode
                           :grad (apply max (for [{:keys [value grad]} weights]
                                              (- value grad)))
                           ;; else:
                           (apply max (map :value weights)))
        filled-src-cobjs (into {:rows           (+ (count src-cobjs) 1)
                                :cols           (+ (count (:tgt-cobjs ws-state)) 1)}
                               (map-indexed (fn [idx member]
                                              [[(+ idx 1) 0] (cost-object-member member)]) src-cobjs))
        filled-tgt-cobjs (into filled-src-cobjs
                               (map-indexed (fn [idx member]
                                              [[0 (+ idx 1)] (cost-object-member member)]) tgt-cobjs))
        src-cobj-idx     (into {} (map-indexed (fn [idx member] [(:uuid member) (+ idx 1)]) src-cobjs))
        tgt-cobj-idx     (into {} (map-indexed (fn [idx member] [(:uuid member) (+ idx 1)]) tgt-cobjs))

        filled-weights   (into filled-tgt-cobjs
                               (map (fn [member]
                                      [[(src-cobj-idx (:src-cobj-uuid member))
                                       (tgt-cobj-idx (:tgt-cobj-uuid member))]
                                       (weight-member member max-weight-val ws-mode)]) weights))]
    filled-weights))

(defn weightset-page [_]
  (let [ws-view                @(subscribe [:selection :ws-view])
        {src-cobjs :src-cobjs
         tgt-cobjs :tgt-cobjs
         weightset :weightset} ws-view
        ws-mode                @(subscribe [:weightset-mode])
        is-demo?               @(subscribe [:is-demo?])
        curr-matrix            (display-matrix ws-view ws-mode)]
    [:div.fl.pa2
     [:h1 [sc/ws-icon] " Weightset " [:strong (->> weightset :name str)]]
     [:p (->> weightset :label str)]
     [sc/weightset-table
      [:tbody
       (for [curr-row (range (:rows curr-matrix))] ^{:key (str curr-row)}
         [:tr
          (for [curr-col (range (:cols curr-matrix))] ^{:key (str curr-col)}
            [:td
             (if (or
                   (some? (curr-matrix [curr-row curr-col]))
                   (and (= curr-row 0) (= curr-col 0)))
               (curr-matrix [curr-row curr-col])
               (if is-demo?
                 ""
                 (to-create-member
                   (nth src-cobjs (- curr-row 1))
                   (nth tgt-cobjs (- curr-col 1))
                   (->> weightset :uuid))))])])]]]))
