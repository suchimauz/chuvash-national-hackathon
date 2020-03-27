(ns app.components.chart.doughnut
  (:require [reagent.core :as r]
            [cljsjs.chartjs]))

(defn usage-metrics-chart
  [data]
  (let [context (.getContext (.getElementById js/document (str "doughnut_" (:key data))) "2d")]
    (js/Chart. context (clj->js data))))

(defn component [arg]
  (r/create-class
   {:component-did-mount #(usage-metrics-chart arg)
    :reagent-render      (fn []
                           [:canvas
                            {:style {:width "120px"
                                     :height "120px"}
                             :id (str "doughnut_" (:key arg))}])}))
