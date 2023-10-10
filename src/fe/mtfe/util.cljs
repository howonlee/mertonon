(ns mtfe.util
  "Miscellaneous util things"
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [goog.string :as gstring]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))

;; ---
;; Non-URL (non-fragment) router handling
;; ---

;; Note: Why are we so jazzed about routers everywhere?
;; Event-driven programming has a bad spaghetti thing where you lose the locality in code of everything
;; because all the event-handling code is spread out everywhere.
;; It might seem weird and pointless to do a router without actually having a uri displayed to user,
;; but a routing thing is a really programmer-accessible tool to get locality back, at least in code.
;; Overall, the goal is something like CGI through a glass, darkly

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

;; TODO: do this with re-frame dispatches

(defn fl
  "Fragment-only link"
  [fragment-path content]
  [:a.white.underline.hover-gray.pointer {:href fragment-path} content])

(defn sl
  "Sidebar-only link"
  [sidebar-path content]
  [:span.white.underline.hover-gray.pointer
   {:on-click #(dispatch [:nav-route "sidebar-change" sidebar-path])}
   content])

(defn evl
  "Event dispatch link"
  [event-key content & data-members]
  [:a.white.underline.hover-gray.pointer
   {:on-click #(if (seq data-members)
                 (dispatch (into [event-key] data-members))
                 (dispatch [event-key]))}
   content])


;; TODO: get the ones that combine w/ fragment to actually work properly with html5 history state

(defn fsl
  "Fragment and sidebar link, both at same time. Usually you want to use path-fsl"
  [fragment-path sidebar-path content]
  [:a.white.underline.hover-gray.pointer
   {:href     fragment-path
    :on-click #(dispatch [:nav-route "sidebar-change" sidebar-path])}
   content])

(defn path-fsl
  "Fragment and sidebar link, pathing to same path, both at same time"
  [path-vec content]
  [:a.white.underline.hover-gray.pointer
   {:href     (hash-path path-vec)
    :on-click #(dispatch [:nav-route "sidebar-change" (path path-vec)])}
   content])

(defn staged-fsl
  "Single click is a sidebar link. Double click is a fragment link.

  As opposed to ordinary fsl, wherein one single click is both sidebar and fragment link."
  [fragment-path sidebar-path content]
  [:div.white.underline.hover-gray.pointer
   {:on-click        #(dispatch [:nav-route "sidebar-change" sidebar-path])
    :on-double-click #(dispatch [:nav-page fragment-path])}
   content])
