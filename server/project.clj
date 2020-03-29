(defproject app "0.1.0-SNAPSHOT"
  :description "Clark App"
  :dependencies [[org.clojure/clojure       "1.10.1"]

                 [org.immutant/web          "2.1.10"]

                 [clj-pg                    "0.0.3"]

                 [ring/ring-json            "0.5.0"]





                 [metosin/reitit-ring       "0.3.10"]




                 [buddy/buddy-auth          "2.2.0"]
                 [buddy/buddy-hashers       "1.4.0"]
                 [buddy/buddy-sign          "3.1.0"]


                 [com.draines/postal        "2.0.3"]
                 [org.clojure/data.codec    "0.1.1"]
                 [json-schema "0.2.0-RC11"]
                 [ring-cors                 "0.1.13"]]
  :main app.rest
  :plugins [[lein-kibit "0.1.7"]]
  :uberjar-name "app.jar"
  :resource-paths ["resources"]
  :profiles {:dev     {:resource-paths ["src/resources"]}
             :repl    {:resource-paths ["src/resources"]}
             :uberjar {:aot [app.rest]}})
