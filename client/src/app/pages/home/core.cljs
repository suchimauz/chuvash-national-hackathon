(ns app.pages.home.core
  (:require [re-frame.core        :as rf]
            [app.pages.model      :as page]
            [app.pages.home.model :as model]

            [app.components.card.core :as card]))

(page/reg-subs-page
 model/index-page
 (fn [{:keys [items]} _ {:keys [auth?]}]
   [:<>
    [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
     [:div.container.d-flex.align-items-center
      [:div.row
       [:div.col-lg-8.col-md-10
        [:h1.display-2.text-white "Портал о нацпроектах в Чувашии"]
        [:p.text-white.mt-0.mb-5
         "Проекты федерального масштаба, принятые в России в 2018 году в соответствии с Указом Президента Российской Федерации от 07.05.2018 № 204
«О национальных целях и стратегических задачах развития Российской Федерации на период до 2024 года» по 13 направлениям"]
        ]]]
     [:div.separator.separator-bottom.separator-skew.zindex-100
      [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
       [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]]
    [:div.container
     [:div.row.align-items-center.py-4.justify-content-between
      [:h3.display-3 "Национальные проекты"]
      (when auth?
        [:span.pointer.btn.btn.btn-neutral {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri "/project/create"}])} "Создать национальный проект"])]
     [:div.row
      (map-indexed
       (fn [idx item] ^{:key idx}
         [:div.col-xl-6.col-md-6
          [card/component item]])
       items)]]]))
