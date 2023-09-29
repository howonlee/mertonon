(ns mtfe.fx
  (:require [re-frame.core :refer [reg-fx]]))

(reg-fx
  :main-path
  ;; Main path will bring sidebar path along for the ride
  (fn [path]
    (.assign (.-location js/window) path)))

(reg-fx
  :non-main-path
  (fn [[event-id path body]]
    (let [evt-obj  {:detail {:path path}}
          evt-obj  (clj->js evt-obj)
          evt      (new (.-CustomEvent js/window) event-id evt-obj)]
      (.dispatchEvent js/window evt))))
