(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require []))

(def default-blurbs
  {})

(defn create-button [sidebar-state-path button-state-path blurbs]
  [sc/border-region
   [:div.pa2
    (create-state-blurb (->> curr-create-state ::fsc/configuration))]
   [:div
    (if (and (empty? (->> @sidebar-state :validation-errors))
             (->> curr-create-state ::fsc/configuration :filled))
      [util/stl create-sc-state :submit
       [sc/button "Create"]
       (->> @sidebar-state :curr-create-params)]
      [sc/disabled-button "Create"])
    [:span.pa2
     (if (->> curr-create-state ::fsc/configuration :creating)
       [sc/spinny-icon]
       [sc/blank-icon])]]
   [:div
    (if (seq (clojure.set/intersection #{:success :failure} (->> curr-create-state ::fsc/configuration)))
      [util/stl create-sc-state :finish
       [sc/button "Finish"]]
      [sc/disabled-button "Finish"])]])
