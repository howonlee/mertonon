(ns mertonon.core
  (:require [clojure.tools.namespace.repl :as tn]
            [mertonon.server :refer [server]]
            [mertonon.setup :as setup :refer [db-migration warmup generate]]
            [mount.core :as mount :refer [defstate]]
            [taoensso.timbre :as timbre :refer [log]])
  ;; Meaning, this is entry point for uberjar
  (:gen-class))

;; ---
;; Notes on dev
;; ---

;; Default way Howon futzes with Mertonon is Conjure, because Howon has been a vim partisan for years and years
;; Default editor in lisp-land is emacs, of course - but we don't have anything for you if you emacs it all day

;; Official docs for connecting clojure
;; https://github.com/Olical/conjure/wiki/Quick-start:-Clojure

;; How to connect cljs:
;; 1. Nav to the page (actually you have to do this, or else there won't be a js runtime)
;; 2. :ConjureConnect <autocreated-nrepl-port-probably-9999>
;; 3. maybe (require '[shadow.cljs.devtools.api :as shadow])
;; 4. (shadow/repl :frontend)
;; 5. Should be able to repl now

;; TODO: Make the above less bullcrap

(defn -main
 "Launch Mertonon with args"
  [& [cmd & args]]
  (log :info "Mertonon initialization beginning...")
  (mount/start)
  (log :info "Mertonon initialization finished!"))

;; ---
;; Utils for repling
;; ---

(defn- reset-all []
  (mount/stop)
  (tn/refresh)
  (mount/start))

(comment (reset-all))

(comment (tn/refresh))
