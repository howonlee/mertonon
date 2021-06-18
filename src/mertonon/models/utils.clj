(ns mertonon.models.utils
  "Misc utils for Mertonon models"
  (:require [clojure.walk :as walk]
            [mertonon.util.queries :as q]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [tick.core :as t]))

(defn maybe-update [m opt-key f]
  (if (contains? m opt-key)
    (update m opt-key f)
    m))

(defn default-canonicalize [member]
  (-> member
      (walk/keywordize-keys)
      (update-keys q/kebabify)
      (update :uuid uutils/uuid)
      (update :created-at t/instant)
      (update :updated-at t/instant)
      (maybe-update :version #(if (string? %)
                                (Integer/parseInt %)
                                %))
      (maybe-update :grid-uuid uutils/uuid)
      (maybe-update :cobj-uuid uutils/uuid)
      (maybe-update :src-cobj-uuid uutils/uuid)
      (maybe-update :tgt-cobj-uuid uutils/uuid)
      (maybe-update :layer-uuid uutils/uuid)
      (maybe-update :src-layer-uuid uutils/uuid)
      (maybe-update :tgt-layer-uuid uutils/uuid)
      (maybe-update :weightset-uuid uutils/uuid)))

(defn default-member->row [member]
  ;; No-op for now, will place things here when we need to
  member)

(defn default-row->member [row]
  (-> row
      (maybe-update :activation uio/round-to-four)
      (maybe-update :delta uio/round-to-four)
      (maybe-update :grad uio/round-to-four)))
