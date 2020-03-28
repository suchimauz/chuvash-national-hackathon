(ns app.components.card.core
  (:require [clojure.string :as str]))

(defn component [{:keys [href name img]}]
  [:a.card.bg-dark.text-white.border-0 {:href href}
   [:img.card-img {:style {:height "200px"}
                   :src (str "http://localhost:8990" img)}]
   [:div.card-img-overlay.d-flex.align-items-center.rounded
    [:span.mask.bg-gradient-default.opacity-5.rounded]
    [:div {:style {:z-index "100"}}
     [:h5.h2.card-title.text-white.mb-2 name]]]])
