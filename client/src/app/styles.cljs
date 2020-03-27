(ns app.styles
  (:require [garden.core :as garden]))

(defn styles [& css]
  (garden/css css))

(def ^:const style
  (styles
   [:#app {:height "100%"}
    [:.separator {:height "100px"}]]))
