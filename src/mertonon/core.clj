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

;; Official docs for connecting shadow-cljs
;; https://github.com/Olical/conjure/wiki/Quick-start:-ClojureScript-(shadow-cljs)

;; That one has the autocommand:
;; function! AutoConjureSelect()
;;   let shadow_build=system("ps aux | grep 'shadow-cljs watch' | head -1 | sed -E 's/.*?shadow-cljs watch //' | tr -d '\n'")
;;   let cmd='ConjureShadowSelect ' . shadow_build
;;   execute cmd
;; endfunction
;; command! AutoConjureSelect call AutoConjureSelect()
;; autocmd BufReadPost *.cljs :AutoConjureSelect

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
