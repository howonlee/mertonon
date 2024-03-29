(ns mertonon.generators.data
  "Explicit lists of data for generation

  Sometimes we want a bunch of line noise to test serde and other stuff.

  Sometimes we want to generate the demo, which is also generated, so it would be good to not show line noise in the demo"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]))

(def psql-string
  "Postgres doesn't do null bytes in their strings
  (https://www.postgresql.org/message-id/1171970019.3101.328.camel%40coppola.muc.ecircle.de)
  but we want to do involutions with these so we filter out postgres-incompatbile strings"
  (gen/fmap #(str/escape % {\  ""}) gen/string))

(def grid-names
  ["Acme Co." "Widgets LLC" "Widgets GmbH" "Widgets SARL" "NewCo GmbH" "XYZ Widgets Co." "Foobar Corp" "John Doe GmbH"
   "NewCo Pvt. Ltd." "Acme LLC" "Foobar SRL" "Widgets Ltd" "Widgets d.o.o"])

(def gen-grid-names
  {:line-noise psql-string
   :display    (gen/elements grid-names)})

(def lastnames
  ["smith" "johnson" "williams" "brown" "jones" "garcia" "miller" "davis" "rodriguez"
   "martinez" "thomas" "taylor" "moore" "jackson" "martin" "perez" "thompson" "clark"
   "lewis" "robinson" "young" "allen" "nguyen" "lee" "li" "liu" "wang"
   "chen" "devi" "huang" "singh" "yang" "kumar" "kim" "jiang" "gao" "zheng" "ahmed" "rahman"])

(def gen-cobj-names
  {:line-noise psql-string
   :display    (gen/elements lastnames)})

(def layer-names
  ["Oduciveling" "Galotobing" "Sconization" "Spline Reticulation"
   "Gozanckling Service" "Tupacasing" "Excerocization" "Hejelinkation"
   "Nekmitization" "Wazzaoking" "Spormozance" "Widgets" "Floyans" "Vansating" "Wooblockos"])

(def gen-layer-names
  {:line-noise psql-string
   :display    (gen/elements layer-names)})

(def label-words
  ["lorem", "ipsum", "dolor", "sit", "amet", "consectetur",
   "adipiscing", "elit", "curabitur", "vel", "hendrerit", "libero",
   "eleifend", "blandit", "nunc", "ornare", "odio", "ut",
   "orci", "gravida", "imperdiet", "nullam", "purus", "lacinia",
   "a", "pretium", "quis", "congue", "praesent", "sagittis",
   "laoreet", "auctor", "mauris", "non", "velit", "eros",
   "dictum", "proin", "accumsan", "sapien", "nec", "massa",
   "volutpat", "venenatis", "sed", "eu", "molestie", "lacus",
   "quisque", "porttitor", "ligula", "dui", "mollis", "tempus",
   "at", "magna", "vestibulum", "turpis", "ac", "diam",
   "tincidunt", "id", "condimentum", "enim", "sodales", "in",
   "hac", "habitasse", "platea", "dictumst", "aenean", "neque",
   "fusce", "augue", "leo", "eget", "semper", "mattis",
   "tortor", "scelerisque", "nulla", "interdum", "tellus", "malesuada",
   "rhoncus", "porta", "sem", "aliquet", "et", "nam",
   "suspendisse", "potenti", "vivamus", "luctus", "fringilla", "erat",
   "donec", "justo", "vehicula", "ultricies", "varius", "ante",
   "primis", "faucibus", "ultrices", "posuere", "cubilia", "curae",
   "etiam", "cursus", "aliquam", "quam", "dapibus", "nisl",
   "feugiat", "egestas", "class", "aptent", "taciti", "sociosqu",
   "ad", "litora", "torquent", "per", "conubia", "nostra",
   "inceptos", "himenaeos", "phasellus", "nibh", "pulvinar", "vitae",
   "urna", "iaculis", "lobortis", "nisi", "viverra", "arcu",
   "morbi", "pellentesque", "metus", "commodo", "ut", "facilisis",
   "felis", "tristique", "ullamcorper", "placerat", "aenean", "convallis",
   "sollicitudin", "integer", "rutrum", "duis", "est", "etiam",
   "bibendum", "donec", "pharetra", "vulputate", "maecenas", "mi",
   "fermentum", "consequat", "suscipit", "aliquam", "habitant", "senectus",
   "netus", "fames", "quisque", "euismod", "curabitur", "lectus",
   "elementum", "tempor", "risus", "cras"])

(def gen-labels
  {:line-noise psql-string
   :display    (gen/fmap (partial clojure.string/join " ")
                         (gen/vector (gen/elements label-words) 1 10))})

(def entry-names
  ["Widgets" "Gizmos" "Contraptions" "Doodads" "Doohickeys" "Doobles"
   "Thingamabobs" "Contrivances" "Whatchamacallits" "Thingamajigs"
   "Perpcherks" "Charktirs" "Twirmflirds" "Starbverks" "Jarmthirs" "Yargwhirms"
   "Patjeks" "Daggaxes" "Hovlims" "Ribpujes" "Maccegs" "Debhigs"
   "Kabmuts" "Kelpojes" "Bavjuts" "Zedkips" "Gofhubs"])

(def gen-entry-names
  {:line-noise psql-string
   :display    (gen/elements entry-names)})

(def usernames
  ["Bob Dobbs" "Bob Lobbs" "Cobb Mobbs" "Hobb Zobbs" "Zobb Wobbs" "Tobb Yobbs"
   "Pob Qobbs" "Rob Tobbs" "Nob Xobbs"])

(def gen-mt-user-usernames
  {:line-noise (gen/fmap clojure.string/join (gen/vector gen/char-alphanumeric 1 20))
   :display    (gen/elements usernames)})

(def emails
  ["irrublor@example.com" "syndumeister@example.com" "inquerepi@example.com"
   "natanizor@example.com" "wetchop@example.com" "walriexpets@example.com"
   "lamiconds@example.com" "furnicalliment@example.com" "smugzoomek@example.com"
   "confiraquent@example.com" "squidesiment@example.com" "cheediship@example.com"
   "izzilent@example.com" "suitamapes@example.com" "afferraron@example.com"])

(def gen-mt-user-emails
  {:line-noise (gen/fmap (fn [member]
                           (str (str/escape member {\  "" "@" ""}) "@example.com"))
                         gen/string)
   :display    (gen/elements emails)})

(def gen-passwords
  {:line-noise (gen/fmap clojure.string/join (gen/vector gen/char 1 20))
   :display    (gen/fmap clojure.string/join (gen/vector gen/char 1 20))})
