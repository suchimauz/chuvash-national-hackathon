(ns app.styles
  (:require [garden.core :as garden]))

(defn styles [& css]
  (garden/css css))

(def ^:const style
  (styles
   [:#app {:height "100%"}
    [:.container-fluid {:padding-left  "17rem"
                        :padding-right "17rem"}]
    [:.separator {:height "100px"}]]))
