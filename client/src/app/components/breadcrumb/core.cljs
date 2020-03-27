(ns app.components.breadcrumb.core)


(defn component []
  [:nav.d-none.d-md-inline-block.ml-md-4
   [:ol.breadcrumb.breadcrumb-links.breadcrumb-dark
    [:li.breadcrumb-item [:a {:href "#"} "Widgets"]]
    [:li.breadcrumb-item [:a {:href "#"} "Widgets"]]
    [:li.breadcrumb-item.active.align-self-center "Widgets"]]])
