(ns mertonon.generators.params
  "Parameters for net generation, depending on whether net gen is for test or demoing")

;; TODO: suffuse these with generators stuff

(def test-gen-params
  "For testing"
  {:type            :test
   :optimizer-type  :sgd
   :num-layers      3
   :cobjs-per-layer 2
   :name-type       :line-noise
   :label-type      :line-noise
   :loss-type       :conformance})

(def demo-gen-params
  "For the customer-visible demo"
  {:type            :demo
   :optimizer-type  :sgd
   :num-layers      5
   :cobjs-per-layer 6
   :name-type       :display
   :label-type      :display
   :loss-type       :conformance})
