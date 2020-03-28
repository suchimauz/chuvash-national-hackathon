(ns app.pages.home.core
  (:require [re-frame.core        :as rf]
            [app.pages.model      :as page]
            [app.pages.home.model :as model]

            [app.components.card.core :as card]))

(page/reg-page
 model/index-page
 (let [page (rf/subscribe [::model/index])]
   (fn []
     [:<>
      [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
       [:div.container.d-flex.align-items-center
        [:div.row
         [:div.col-lg-7.col-md-10
          [:h1.display-2.text-white "Hello Jesse"]
          [:p.text-white.mt-0.mb-5
           "This is your profile page. You can see the progress you've made with your work and manage your projects or assigned tasks"]
          [:a.btn.btn-neutral {:href "#!"} "Edit profile"]]]]
       [:div.separator.separator-bottom.separator-skew.zindex-100
        [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
         [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]]
      [:div.container
       [:div.row.align-items-center.py-4.justify-content-between
        [:h3.display-3 "Национальные проекты"]
        [:a.btn.btn.btn-neutral {:href "#/project/create"} "Создать национальный проект"]]
       [:div.row
        (map-indexed
         (fn [idx item] ^{:key idx}
           [:div.col-xl-6.col-md-6
            [card/component {:href (str "#/project/" idx)}]])
         (range 6))]]])))
