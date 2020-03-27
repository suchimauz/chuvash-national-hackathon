(ns user
  (:require [figwheel.main.api :as repl]))

(def figwheel-options
  {:id      "app"
   :options {:main       'app.build.dev
             :output-to  "resources/public/js/app.js"
             :output-dir "resources/public/js/out"}
   :config  {:watch-dirs          ["src"]
             :mode                :serve
             :ring-server-options {:port 3000}}})

(defonce state
  (future (repl/start figwheel-options)))

(comment
  (repl/cljs-repl "app"))
