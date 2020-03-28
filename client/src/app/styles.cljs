(ns app.styles
  (:require [garden.core :as garden]))

(defn styles [& css]
  [:style (garden/css css)])

(def style
  (styles
   [:body {:color "#333" :font-size "15px" :font-family "GothamPro" :height "100%"}
    [:.app
     [:h1 {:font-size "32px" :font-family "GothamPro-bold" :font-weight "900"}]
     [:h2 {:font-size "24px" :font-family "GothamPro-medium" :font-weight "900"}]
     [:b {:font-size "16px" :font-family "GothamPro"}]
     [:.pointer {:cursor :pointer}]
     [:span
      [:btn {:cursor :pointer}]]
     [:.separator {:height "100px"}]]]))
