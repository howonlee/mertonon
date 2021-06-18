(ns mertonon.util.schemas
  "Schemas which are used lots of places
  
  TODO: get rid of these and replace with malli"
  (:require [schema.core :as s]))

;; ---
;; Net definitions
;; ---

(s/defschema Grid
  "Place to put metadata about a Mertonon org"
  {:uuid s/Uuid})

(s/defschema Layer
  "A layer in the Mertonon value flow.

  Corresponds to a neural net layer and some part of the value chain at the same time."
  {:uuid      s/Uuid
   :grid-uuid s/Uuid})

(s/defschema CostObject
  "A member of a layer, corresponding to an individual or other cost object
  responsible for something in the Mertonon value flow. Corresponds to a neural net node."
  {:uuid       s/Uuid
   :layer-uuid s/Uuid})

(s/defschema Entry
  "A ledger journal entry in a Mertonon value flow.

  Corresponds to neural net data, both inputs and outputs in a pattern."
  {:uuid      s/Uuid
   :cobj-uuid s/Uuid})

(s/defschema Weight
  "Corresponds to a relation between two cost object of different layers in a Mertonon value flow."
  {:uuid           s/Uuid
   :weightset-uuid s/Uuid
   :src-cobj-uuid  s/Uuid
   :tgt-cobj-uuid  s/Uuid})

(s/defschema Weightset
  "Set of weights between two layers.

  Corresponds to a neural net matrix parameter"
  {:uuid           s/Uuid
   :src-layer-uuid s/Uuid
   :tgt-layer-uuid s/Uuid})

(s/defschema Loss
  "Corresponds to a loss for optimization in Mertonon. Just a neural net loss"
  {:uuid           s/Uuid
   :layer-uuid     s/Uuid
   :type           s/Keyword})
