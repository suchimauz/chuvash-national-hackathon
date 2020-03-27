(ns app.pages.project.core
  (:require [re-frame.core           :as rf]
            [app.pages.model         :as page]
            [app.pages.project.model :as model]
            [app.components.breadcrumb.core :as breadcrumb]))

(page/reg-subs-page
 model/show-page
 (fn [page]
   [:<>
    [:div.header.bg-gradient-primary.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     [:div.container-fluid.d-flex.align-items-center
      [:div.row
       [:div.col-lg-7.col-md-10
        [:h1.display-2.text-white "Hello Jesse"]
        [:p.text-white.mt-0.mb-5
         "This is your profile page. You can see the progress you've made with your work and manage your projects or assigned tasks"]]]]
     [:div.container-fluid
      [:div.row.align-items-center.py-4
       [:div.col-lg-6.col-7
        [breadcrumb/component]]
       [:div.col-lg-6.col-5.text-right
        [:a.btn.btn.btn-neutral {:href "#"} "Редактировать"]]]]
     ]

    [:div.container

     [:h1 "!@3"]]
    ]))
