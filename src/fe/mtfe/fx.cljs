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

;; TODO: Get this in localstorage instead... or the db
(def sidebar-history (atom []))

(def history-length 100)

(defn stack-push
  "Won't conj if the peek is the exact same, but will otherwise"
  [coll [path params max-len]]
  (let [x         [path params]
        is-same?  (= (peek coll) x)
        res       (if is-same? coll (conj coll x))
        too-long? (> (count res) max-len)
        res       (if too-long?
                    (subvec res (- (count res) max-len) (count res))
                    res)]
    res))

(reg-fx
  :sidebar-histpush
  (fn [[path params]]
    (swap! sidebar-history stack-push [path params history-length])))

(reg-fx
  :sidebar-histpop
  (fn []
    (when (some? (peek @sidebar-history))
      (swap! sidebar-history pop))))

(reg-cofx
  :sidebar-histpeek
  (fn [coeffects _]
    (assoc coeffects :last-sidebar (peek @sidebar-history))))
