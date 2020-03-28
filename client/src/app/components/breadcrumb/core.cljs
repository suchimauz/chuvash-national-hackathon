(ns app.components.breadcrumb.core
  (:require [zframes.breadcrumb :as breadcrumb]
            [re-frame.core      :as rf]))

(defn component [links]
  [:nav.d-none.d-md-inline-block.ml-md-4
   [:ol.breadcrumb.breadcrumb-links.breadcrumb-dark
    (map-indexed
     (fn [idx {:keys [display href class current]}]
       [:li.breadcrumb-item {:key idx :class class}
        (if (or current (empty? href))
          display
          [:a {:href href} display])])
     (butlast links))
    [:li.breadcrumb-item.active.align-self-center (:display (last links))]]])

(defn component-with-sub []
  (let [breadcrumb* (rf/subscribe [::breadcrumb/breadcrumb])]
    (fn []
      [component @breadcrumb*])))
