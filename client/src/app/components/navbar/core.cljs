(ns app.components.navbar.core
  (:require [re-frame.core :as rf]
            [app.components.navbar.model :as model]))

(defn component []
  (let [*node (rf/subscribe [::model/data])]
    (fn []
      (let [node (deref *node)]
        [:<>
         (map-indexed
          (fn [idx link] ^{:key idx}
            [:div.center.px
             [:a.pr.muted link [:b (:title link)]]])
          (:nav node))]))))
