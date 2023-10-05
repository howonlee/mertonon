(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require []))

(def default-blurbs
  {:blank    "Enter data."
   :filled   "Press Create button to create."
   :creating "Creating..."
   :success  "Successfully created"
   :failure  "Failed to create."
   :finished "Finished!"})

(defn create-button [sidebar-state-path button-state-path blurbs]
  [sc/border-region
   [:div.pa2
    (blurbs curr-state)]
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
