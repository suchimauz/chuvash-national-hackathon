(ns app.components.card.core)

(defn component []
  [:div.card.card-stats
   [:div.card-body
    [:div.row
     [:div.col
      [:h5.card-title.text-uppercase.text-muted.mb-0
       "Total traffic"]
      [:span.h2.font-weight-bold.mb-0 "350,897"]]
     [:div.col-auto
      [:div.icon.icon-shape.bg-gradient-red.text-white.rounded-circle.shadow
       [:i.ni.ni-active-40]]]]
    [:p.mt-3.mb-0.text-sm
     [:span.text-success.mr-2 [:i.fa.fa-arrow-up] " 3.48%"]
     [:span.text-nowrap "Since last month"]]]])
