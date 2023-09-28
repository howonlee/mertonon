(ns mtfe.util
  "Miscellaneous util things"
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [com.fulcrologic.statecharts :as fsc]
            [goog.string :as gstring]
            [mtfe.statecharts.core :as mt-statechart]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))

;; ---
;; Core Route Match
;; ---
;; (Defined here instead of mtfe.core to allow us to stick refresh and mutation things here)

(defonce core-match (r/atom nil))

;; ---
;; Non-URL (non-fragment) router handling
;; ---

;; Note: Why are we so jazzed about routers everywhere?
;; Event-driven programming has a bad spaghetti thing where you lose the locality in code of everything
;; because all the event-handling code is spread out everywhere.
;; It might seem weird and pointless to do a router without actually having a uri displayed to user,
;; but a routing thing is a really programmer-accessible tool to get locality back, at least in code.
;; Overall, the goal is something like CGI through a glass, darkly

(defn to-router-path!
  "For sidebar or action or whatever router, path to it"
  [event-id path & [body]]
  (let [evt-obj  {:detail {:path path}}
        evt-obj  (clj->js evt-obj)
        evt      (new (.-CustomEvent js/window) event-id evt-obj)]
    (.dispatchEvent js/window evt)))

(defn to-main-path!
  "Path to the main-router path"
  [path]
  (.assign (.-location js/window) path))

(defn nav-to-sidebar-for-current-main-view!
  "Nav to the canonical default sidebar view, which corresponds to the 'default modal' if we think of sidebar as permanent modal"
  []
  (let [pathname (subs (.-hash (.-location js/window)) 1)]
    (if (clojure.string/blank? pathname)
      (to-router-path! "sidebar-change" "/")
      (to-router-path! "sidebar-change" pathname))))

(defn custom-route-start!
  "reitit.frontend.easy is supposed to hijack your html5 history.
  We don't want sidebar semantics to be in history, but we do want a router for sidebar.

  Doing an alternate start! that's not the reitit.frontend.easy start! is the way to avoid this."
  [router event-id on-match]
  (let [handler  (fn [e] (on-match (rf/match-by-path router (.. e -detail -path))))]
    (.addEventListener js/window event-id handler)))

;; Best not to use this resolve stuff too much, but the monstrosity required to avoid circular dep was too much
(defn refresh!
  []
  ((resolve 'mtfe.core/main-mount!)))

;; ---
;; Parsing and string stuff
;; ---

(defn json-parse
  "Ajax resp comes as text. Parse it and deal with the keys..."
  [ajax-resp]
  (->> (.parse js/JSON ajax-resp) js->clj walk/keywordize-keys))

(defn params->str
  [params]
  (let [res (.toString (new (.-URLSearchParams js/window) (clj->js params)))]
    res))

(defn differs-in [fst fst-in snd snd-in]
  (not= (get-in fst fst-in) (get-in snd snd-in)))

(defn iso-date-params [params]
  (into {} (map (fn [[k v]] (if (= js/Date (type v)) [k (.toISOString v)] [k v])) params)))

;; ---
;; Dealing with paths
;; ---

(defn path
  "Takes a list of path members, gets a url path

  WARNING: No defense against that sort of .. hijacking, other url attack shenanigans"
  ([path-vec] (gstring/format "/%s" (str/join "/" path-vec)))
  ([path-vec query-params]
   (gstring/format "/%s?%s" (str/join "/" path-vec)
                   (params->str query-params))))

(defn hash-path
  "Takes a list of path members, get a url path for our hash links

  WARNING: No defense against that sort of .. hijacking, other url attack shenanigans"
  ([path-vec] (gstring/format "#%s" (path path-vec)))
  ([path-vec query-params] (gstring/format "#%s" (path path-vec query-params))))

;; ---
;; Different links in different routers
;; ---

(defn fl
  "Fragment-only link"
  [fragment-path content]
  [:a.white.underline.hover-gray.pointer {:href fragment-path} content])

(defn sl
  "Sidebar-only link"
  [sidebar-path content]
  [:span.white.underline.hover-gray.pointer
   {:on-click #(to-router-path! "sidebar-change" sidebar-path)}
   content])

(defn stl
  "Statechart state transition link"
  [sc-state event-key content & [data]]
  [:a.white.underline.hover-gray.pointer
   {:on-click #(mt-statechart/send-event-and-reset! sc-state event-key data)}
   content])

;; TODO: get the ones that combine w/ fragment to actually work properly with html5 history state

(defn fsl
  "Fragment and sidebar link, both at same time. Usually you want to use path-fsl"
  [fragment-path sidebar-path content]
  [:a.white.underline.hover-gray.pointer
   {:href     fragment-path
    :on-click #(to-router-path! "sidebar-change" sidebar-path)}
   content])

(defn path-fsl
  "Fragment and sidebar link, pathing to same path, both at same time"
  [path-vec content]
  [:a.white.underline.hover-gray.pointer
   {:href     (hash-path path-vec)
    :on-click #(to-router-path! "sidebar-change" (path path-vec))}
   content])

(defn staged-fsl
  "Single click is a sidebar link. Double click is a fragment link.

  As opposed to ordinary fsl, wherein one single click is both sidebar and fragment link."
  [fragment-path sidebar-path content]
  [:div.white.underline.hover-gray.pointer
   {:on-click        #(to-router-path! "sidebar-change" sidebar-path)
    :on-double-click #(to-main-path! fragment-path)}
   content])
