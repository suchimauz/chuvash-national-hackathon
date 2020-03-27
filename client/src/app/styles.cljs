(ns app.styles
  (:require [garden.core :as garden]))

(defn styles [& css]
  [:style (garden/css css)])

(def style
  (styles
   [:.app
    [:.pointer {:cursor :pointer}]
    [:span
     [:btn {:cursor :pointer}]]
    [:.separator {:height "100px"}]]))
