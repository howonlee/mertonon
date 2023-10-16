(ns mertonon.models.model-test
  "One test suite to test all the models! Model selected at testing time randomly!"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

;; ---
;; Preliminaries
;; ---

(def tables-under-test
  "Order of these matters, with foreign key dependencies.
  Basically the linearized version of the DAG of them
  Better not have cycles in our fkey dependencies!"
  [:mertonon.grids :mertonon.layers :mertonon.cost-objects
   :mertonon.weightsets :mertonon.weights
   :mertonon.losses :mertonon.inputs :mertonon.entries

   :mertonon.mt-users])

(defn test-inp [table generates]
  (merge (reg/table->model table)
         {:gen-net         generates
          :model-instance  (tu/generates->member generates table)
          :model-instances (tu/generates->members generates table)
          :setup           (tu/setup-generates! tables-under-test)}))

(def table-and-generates (tu/table-and-generates tables-under-test))

;; ---
;; Actual tests
;; ---

(defspec model-instance-singular
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (not (vector? (:model-instance (test-inp table generates))))))

(defspec create-and-generate-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-generate-consonance (test-inp table generates)))))

(defspec member->row-round-trip
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/member->row-round-trip (test-inp table generates)))))

(defspec create-and-read-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-read-consonance (test-inp table generates)))))

(defspec create-one-create-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-one-create-many-consonance (test-inp table generates)))))

(defspec update-then-update-back
  tu/many
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-then-update-back (test-inp table generates)))))

(defspec update-many-then-update-back
  tu/many
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-many-then-update-back (test-inp table generates)))))

(defspec read-one-read-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance (test-inp table generates)))))

(defspec read-one-read-where-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn
                  (tu/read-one-read-where-consonance (test-inp table generates)))))

(defspec create-and-delete-inversion
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-delete-inversion (test-inp table generates)))))

(defspec delete-one-delete-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance (test-inp table generates)))))

(comment (update-many-then-update-back))

(comment
  (let [[table generates] [:mertonon.cost-objects
                           {:grids
                            [{:uuid #uuid "863e675d-ce1b-47cb-8d4c-59a59e195bf9",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362444Z",
                              :updated-at #time/instant "2023-10-16T18:21:26.362445Z",
                              :name ".Ã/È\b·wg<",
                              :label
                              "Ä_ÆiY¬¬ÐË¹hå?C7X?àr³®xÞ ë3M\b\\çÛÁÒ2\rÏ®\bU#",
                              :optimizer-type :sgd,
                              :hyperparams {}}],
                            :layers
                            [{:uuid #uuid "cc7e90bb-b32a-48ab-a1a1-140a7b70f9ca",
                              :grid-uuid #uuid "863e675d-ce1b-47cb-8d4c-59a59e195bf9",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362630Z",
                              :updated-at #time/instant "2023-10-16T18:21:26.362630Z",
                              :name "4¼·\f)W\f9P^|b%¹WUìô",
                              :label
                              "}PåÚ`!ìOãsÛ@£Wc\\eV[7ôÑ!nÆÂ]höaEã¶nÏH¡Lí"}
                             {:uuid #uuid "ec7c0cd4-64e1-48f2-a39a-76c05ca4c73a",
                              :grid-uuid #uuid "863e675d-ce1b-47cb-8d4c-59a59e195bf9",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362630Z",
                              :updated-at #time/instant "2023-10-16T18:21:26.362631Z",
                              :name "Bf&ÀRtûfÍ/n·ñüLÙµRÞ#W,E-Íízz$x",
                              :label "G³²1îdm¿A7hBÔ"}
                             {:uuid #uuid "ef296547-3f1b-4727-bb93-1689d17d0c00",
                              :grid-uuid #uuid "863e675d-ce1b-47cb-8d4c-59a59e195bf9",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362628Z",
                              :updated-at #time/instant "2023-10-16T18:21:26.362629Z",
                              :name
                              "$È}ì{itÍÓlbzãfÿJ-F©Uå ãÌT\"ÿ7`8yÏpg©Æ¦kð(FCãÒhÜ5õ¶D",
                              :label "LAHróïÈTéZ<µã"}],
                            :cost-objects
                            [{:updated-at #time/instant "2023-10-16T18:21:26.362987Z",
                              :name
                              "äønßqÖÈ7¬ìäGOj(N\fgSÕÚØZ3¢<ûÐy]åªíÊáètR_? 'J#ÿN*",
                              :activation 0.0000M,
                              :label "^s¸Ë\")ÏÏ*4\bS´FA¡Ù®²nxF\\dÝK%4ëc°:b45Ev9",
                              :delta 0.0000M,
                              :uuid #uuid "803929fe-c31d-4e5d-a850-7189d47f3ef0",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362988Z",
                              :layer-uuid #uuid "cc7e90bb-b32a-48ab-a1a1-140a7b70f9ca"}
                             {:updated-at #time/instant "2023-10-16T18:21:26.363010Z",
                              :name "¿i",
                              :activation 0.0000M,
                              :label "ÿiÿ¯$åúw]ÇË±-´i`\f£",
                              :delta 0.0000M,
                              :uuid #uuid "90ea202f-60e3-48a0-8670-c1503cc773bb",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.363010Z",
                              :layer-uuid #uuid "ef296547-3f1b-4727-bb93-1689d17d0c00"}
                             {:updated-at #time/instant "2023-10-16T18:21:26.363008Z",
                              :name "ñz]$G]0h!f'¸{79\fç",
                              :activation 0.0000M,
                              :label "E;paÚ",
                              :delta 0.0000M,
                              :uuid #uuid "a8bf1d24-072a-429c-bb1c-2b95fe668492",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.363008Z",
                              :layer-uuid #uuid "ef296547-3f1b-4727-bb93-1689d17d0c00"}
                             {:updated-at #time/instant "2023-10-16T18:21:26.363001Z",
                              :name
                              "®Æ}L´Í³ò0Wå°óGGýØàZ±íòMí*:w0K8£w4Lý3ÛméÝW°ÄôÛ©",
                              :activation 0.0000M,
                              :label "=j]\f@xLUò¶2Õ\"£ÕÃ)ªkU7+%è¿ýã",
                              :delta 0.0000M,
                              :uuid #uuid "eea443b7-de89-45e7-8bd6-4064f9a49926",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.363001Z",
                              :layer-uuid #uuid "ec7c0cd4-64e1-48f2-a39a-76c05ca4c73a"}
                             {:updated-at #time/instant "2023-10-16T18:21:26.362991Z",
                              :name
                              "O,ÞY/ÄzßÈvCÊÄ¤×õö×Dÿk~¯¶Cäñ'¬xö ËQsÜâõõV¼-®NþP\".áóÐþ",
                              :activation 0.0000M,
                              :label "ß3¤<|·_¾ ¨¥¢Ç&ïàlÝW]- ",
                              :delta 0.0000M,
                              :uuid #uuid "19f1ae71-98b9-47b8-99f5-07304bb0bd38",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.362992Z",
                              :layer-uuid #uuid "cc7e90bb-b32a-48ab-a1a1-140a7b70f9ca"}
                             {:updated-at #time/instant "2023-10-16T18:21:26.363003Z",
                              :name "Þ*{Ñ3½ÑÁmuÜèØ·A¡ýcõ\tEÄÇ¤X",
                              :activation 0.0000M,
                              :label "8þÙ:",
                              :delta 0.0000M,
                              :uuid #uuid "462aa884-21ca-4723-b881-7c0e0c46e562",
                              :version 0,
                              :created-at #time/instant "2023-10-16T18:21:26.363003Z",
                              :layer-uuid #uuid "ec7c0cd4-64e1-48f2-a39a-76c05ca4c73a"}]}]]
    (tu/with-test-txn (tu/update-many-then-update-back (test-inp table generates)))))
