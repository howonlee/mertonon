(ns mtfe.statecharts.validations
  "State transition validations.

  They emit a keyword, which fills up a :validation-errors key in the curr state.
  Then, the view has a buncha validation-poppers or whatever yelling at user that they need to fix it"
  (:require [clojure.string :as str]
            [mtfe.util :as util]
            [mtfe.statecharts.core :as mt-statechart]))

;; Most complex validations will be idiosyncratic to one sidebar or one view or something,
;; and therefore will be stuck there

;; ---
;; Simple Validations
;; ---

;; Return nil to indicate we pass validation,
;; keyword to indicate failure otherwise,
;; map to return data in addition to function
;; "All happy families are alike; each unhappy family is unhappy in its own way."

(defn non-blank
  "Procs if that path in curr-state is blank"
  [path curr-keyword]
  (fn [curr-state]
    (if (str/blank? (get-in curr-state path))
      curr-keyword
      nil)))

;; Unfortunately, clojurescript inherits javascript's numeric type madness
(defn is-integer
  "Procs if that path in curr-state is _not_ an integer"
  [path curr-keyword]
  (fn [curr-state] 
    (let [curr-member (get-in curr-state path)]
      (cond
        (not (string? curr-member))
        curr-keyword
        ;; use global isNaN, not Number.isNaN, because we want the coercion behavior
        (js/isNaN curr-member)
        curr-keyword
        :else
        nil))))

(defn min-num-elems
  "So you're connecting two things - gotta have two elems to do so..."
  [path min-num-elems curr-keyword]
  (fn [curr-state]
    (if (<= min-num-elems (count (get-in curr-state path)))
      nil
      curr-keyword)))

(defn max-num-elems
  [path max-num-elems curr-keyword]
  (fn [curr-state]
    (if (< (count (get-in curr-state path)) max-num-elems)
      nil
      curr-keyword)))

;; ---
;; Middlingly complicated validations that aren't specific enough to go in one specific file
;; ---

(defn grouped-min-num-elems
  "So there's some fkey relation. Every cobj (child) has a layer_uuid (layer is parent), for example.

  Check that for each parent, there's a minimum number of children. For single join only, suck teeth on multijoins a bit."
  [dump-path [parent-join-table child-join-table] [[parent-pkey child-fkey]] min-num-elems curr-keyword]
  (fn [curr-state]
    (let [dump                 (get-in curr-state dump-path)
          groups               (group-by child-fkey (flatten (get dump child-join-table)))
          curr-parents         (get dump parent-join-table)
          num-groups-match     (= (count curr-parents) (count (vals groups)))
          groups-cardinalities (every? #(<= min-num-elems (count %)) (vals groups))]
      (if (and num-groups-match groups-cardinalities)
        nil
        curr-keyword))))

(defn in-set
  [set-getter member-getter curr-keyword]
  (fn [curr-state]
    (let [curr-set    (set-getter curr-state)
          curr-member (member-getter curr-state)]
      (if (contains? curr-set curr-member)
        nil
        curr-keyword))))

(defn not-in-set
  [set-getter member-getter curr-keyword]
  (fn [curr-state]
    (let [curr-set    (set-getter curr-state)
          curr-member (member-getter curr-state)]
      (if (contains? curr-set curr-member)
        curr-keyword
        nil))))

;; ---
;; Logical operators of predicates
;; ---
;; These use the given keyword and trash the keywords of inner ones
;; Naming of predication is a bit confusing because of our validation semantics being nil if good...

(defn or-predicate
  "Procs if the first validation OR the second validation procs"
  [fst snd curr-keyword]
  (fn [curr-state]
    (let [fst-val (fst curr-state)
          snd-val (snd curr-state)]
      (if (or fst-val snd-val)
        curr-keyword
        nil))))

(defn and-predicate
  "Procs if the first validation AND the second validation procs"
  [fst snd curr-keyword]
  (fn [curr-state]
    (let [fst-val (fst curr-state)
          snd-val (snd curr-state)]
      (if (and fst-val snd-val)
        curr-keyword
        nil))))

(defn not-predicate
  "Negate a validation predicate"
  [pred curr-keyword]
  (fn [curr-state]
    (if (pred curr-state)
      nil
      curr-keyword)))
