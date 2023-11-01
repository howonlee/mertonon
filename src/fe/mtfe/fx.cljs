(ns mtfe.fx
  "Effects and coeffects for re-frame

  Note that the cofx are here, not elsewhere"
  (:require [re-frame.core :refer [reg-fx reg-cofx]]))

;; ---
;; Paths
;; ---

(reg-fx
  :main-path
  ;; Main path will bring sidebar path along for the ride
  (fn [path]
    (.assign (.-location js/window) path)))

(reg-fx
  :non-main-path
  (fn [[event-id path]]
    (let [evt-obj  {:detail {:path path}}
          evt-obj  (clj->js evt-obj)
          evt      (new (.-CustomEvent js/window) event-id evt-obj)]
      (.dispatchEvent js/window evt))))

;; ---
;; Sidebar history
;; Main history is handled by reitit.frontend
;; ---

;; TODO: Get this in localstorage instead
(def sidebar-history (atom []))

(reg-fx
  :sidebar-histpush
  (fn [[path params]]
    (swap! sidebar-history conj [path params])))

(reg-fx
  :sidebar-histpop
  (fn []
    (when (some? (peek @sidebar-history))
      (swap! sidebar-history pop))))

(reg-cofx
  :last-sidebar
  (fn [coeffects _]
    (assoc coeffects :last-sidebar (peek @sidebar-history))))
