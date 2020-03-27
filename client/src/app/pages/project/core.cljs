(ns app.pages.project.core
  (:require [re-frame.core           :as rf]
            [app.pages.model         :as page]
            [app.pages.project.model :as model]
            [app.components.chart.doughnut :as doughnut]
            [app.components.breadcrumb.core :as breadcrumb]))

(page/reg-subs-page
 model/show-page
 (fn [page]
   [:<>
    [:div.header.bg-gradient-primary.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-10
       [:h1.display-4.text-white "Национальные проекты"]
       [:h1.display-2.text-white "Здравоохранение"]]]
     [:div.container-fluid
      [:div.row.align-items-center.py-4
       [:div.col-lg-6.col-7
        [breadcrumb/component]]
       [:div.col-lg-6.col-5.text-right
        [:a.btn.btn.btn-neutral {:href "#"} "Редактировать"]]]]]

    [:div.container
     [:div.my-4.border-bottom.py-2
      [:h3.display-3 "Региональные проекты"]]
     (map-indexed
      (fn [idx item]^{:key idx}
        [:div.card
         [:div.card-header [:h5.h3.mb-0 "Развитие экспорта медицинских услуг"]]
         [:div.card-body
          [:div.card-body
           [:div.d-flex.justify-content-between
            [:div.row.align-items-center
             [:div.col-auto
              [:a.avatar.avatar-xl.rounded-circle
               {:href "#"}
               [:img
                {:src "http://localhost:8990/storage/download/test/1.png"
                 :alt "Image placeholder"}]]]
             [:div.col.ml--2
              [:h4.mb-0 [:a {:href "#!"} "Руководитель проекта Филиппов Е.Ф."]]
              [:p.text-sm.text-muted.mb-0 "министр здравоохранения Краснодарского края "]]]
            [:div.col-auto
             [doughnut/component {:key     idx
                                  :type    "doughnut"
                                  :stacked true
                                  ;:options {:scales {:yAxes [{:ticks {:beginAtZero true}}]}}
                                  :data    {:labels   ["1" "2"]
                                            :datasets [{:data            [1 2 3]
                                                        :label           "Registered users"
                                                        :backgroundColor "#90EE90"}
                                                       {:data            [1 2 3]
                                                        :label           "Users with address"
                                                        :backgroundColor "#F08080"}]}}]]]]]])
      (range 3))

     ]
    ]))
