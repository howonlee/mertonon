(ns mtfe.statecharts.core
  "Statechart initialization. Also includes validation logic"
  (:require [com.fulcrologic.statecharts :as fsc]
            [com.fulcrologic.statecharts.chart :refer [statechart]]
            [com.fulcrologic.statecharts.elements :refer [state parallel transition on-entry script log]]
            [com.fulcrologic.statecharts.events :refer [new-event]]
            [com.fulcrologic.statecharts.protocols :as sp]
            [com.fulcrologic.statecharts.simple :as simple]))

;; We would like pretty good locality as to where to understand the transitions of the statecharts
;; so probably try not to create any statecharts by making an octopus spread out everywhere in different files

;; Cool but completely undocumented entry-fn macro from them fulcro peeps:
;; https://github.com/fulcrologic/statecharts/blob/main/src/main/com/fulcrologic/statecharts/elements.cljc#L143

;; ---
;; Global Statechart Registry
;; ---

(def env (simple/simple-env))

;; ---
;; Dealing With Statecharts From Distance
;; ---

(defn send-event!
  "You should probably use `send-event-and-reset!`"
  [sc-state event-key & [data]]
  (sp/process-event!
    (::fsc/processor env)
    env
    sc-state
    (new-event {:name event-key :data data})))

(defn send-event-and-reset! [sc-state-atom event-key & [data]]
  (reset!
    sc-state-atom
    (sp/process-event!
      (::fsc/processor env)
      env
      @sc-state-atom
      (new-event {:name event-key :data data}))))

(defn send-reset-event-if-finished! [sc-state-atom]
  (when (->> @sc-state-atom :com.fulcrologic.statecharts/configuration :finished)
    (send-event-and-reset! sc-state-atom :reset)))

(defn init-sc! [sc-key sc-atom statechart]
  (simple/register! env sc-key statechart)
  (reset! sc-atom
          (sp/start! (::fsc/processor env)
                     env
                     sc-key
                     {::fsc/session-id 1})))

(defn mutate-from-dom-event-handler [sc-state path]
  (fn [dom-evt]
    (let [annotated-evt {:event dom-evt :path path}]
      (send-event-and-reset! sc-state :mutate annotated-evt))))

(defn mutate-from-plain-function-handler [sc-state path]
  (fn [res]
    (let [annotated-evt {:result res :path path}]
      (send-event-and-reset! sc-state :mutate annotated-evt))))

;; ---
;; Simple Creation Statechart
;; ---

(defn simple-create
  "Returns a statechart that represents an individual create form's overall state."
  [id {:keys [reset-fn mutation-fn validation-fn
              action-fn finalize-fn] :as handlers}]
  (statechart
    {}
    (state
      {:id id
       :initial :blank}
      (state {:id :blank}
             (on-entry {} (script {:expr reset-fn}))
             (transition {:event :mutate :target :filled}))
      (state {:id :filled}
             (on-entry {} (script {:expr mutation-fn}))
             (on-entry {} (script {:expr validation-fn}))
             (transition {:event :mutate :target :filled})
             (transition {:event :submit :target :creating})
             (transition {:event :reset :target :blank}))
      (state {:id :creating}
             (on-entry {} (script {:expr action-fn}))
             (transition {:event :succeed :target :success})
             (transition {:event :fail :target :failure})
             ;; In case if they click really fast and their internet's real slow
             (transition {:event :reset :target :blank}))
      (state {:id :success}
             (transition {:event :finish :target :finished}))
      (state {:id :failure}
             (transition {:event :finish :target :finished}))
      (state {:id :finished}
             (on-entry {} (script {:expr finalize-fn}))
             (transition {:event :reset :target :blank})))))

;; ---
;; Simple Deletion Statechart
;; ---

(defn simple-delete
  "Returns a statechart that represents an individual deletion form's overall state."
  [id {:keys [action-fn finalize-fn] :as handlers}]
  (statechart
    {}
    (state
      {:id id
       :initial :initial}
      (state {:id :initial}
             (transition {:event :submit :target :deleting}))
      (state {:id :deleting}
             (on-entry {} (script {:expr action-fn}))
             (transition {:event :succeed :target :success})
             (transition {:event :fail :target :failure}))
      (state {:id :success}
             (transition {:event :finish :target :finished}))
      (state {:id :failure}
             (transition {:event :finish :target :finished}))
      (state {:id :finished}
             (on-entry {} (script {:expr finalize-fn}))
             (transition {:event :reset :target :initial})))))

;; ---
;; Simple Generic Action Statechart
;; ---
;; (suspiciously similar to delete statechart)

(defn simple-action
  "Returns a statechart that represents an individual form that just does the one thing's overall state."
  [id {:keys [action-fn mutation-fn finalize-fn validation-fn] :as handlers}]
  (statechart
    {}
    (state
      {:id id
       :initial :initial}
      (state {:id :initial}
             ;; TODO: on-entry validation I want, but it seems real annoying to actually do empirically
             (transition {:event :mutate :target :filled})
             (transition {:event :submit :target :acting}))
      (state {:id :filled}
             (on-entry {} (script {:expr mutation-fn}))
             (on-entry {} (script {:expr validation-fn}))
             (transition {:event :submit :target :acting}))
      (state {:id :acting}
             (on-entry {} (script {:expr action-fn}))
             (transition {:event :succeed :target :success})
             (transition {:event :fail :target :failure}))
      (state {:id :success}
             (transition {:event :finish :target :finished}))
      (state {:id :failure}
             (transition {:event :finish :target :finished}))
      (state {:id :finished}
             (on-entry {} (script {:expr finalize-fn}))
             (transition {:event :reset :target :initial})))))
