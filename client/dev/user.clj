(ns user
  (:require [figwheel.main.api :as repl]))

(def figwheel-options
  {:id      "app"
   :options {:main         'app.build.dev
             :foreign-libs [{:file     "resources/public/js/imask.js"
                             :provides ["jslib.imask"]}]
             :output-to    "resources/public/js/app.js"
             :output-dir   "resources/public/js/out"}
   :config  {:watch-dirs          ["src"]
             :mode                :serve
             :ring-server-options {:port 3000}}})

(defonce state
  (future (repl/start figwheel-options)))

(comment
  (repl/cljs-repl "app"))
