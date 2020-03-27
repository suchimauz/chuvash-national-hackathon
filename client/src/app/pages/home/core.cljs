(ns app.pages.home.core
  (:require [re-frame.core        :as rf]
            [app.pages.model      :as page]
            [app.pages.home.model :as model]))

(page/reg-page
 model/index-page
 (let [page (rf/subscribe [::model/index])]
   (fn []
     [:<>
      [:div.header.pb-6.d-flex.align-items-center.py-7.py-lg-8.pt-lg-9
       {:style {:min-height          "800px"
                :background-image    "url(https://sun1-87.userapi.com/DNIngcTQugeCr5OrNArv_AsCYaJC4Wm_pTsi5g/tIIwEtF0gQ0.jpg)"
                :background-size     "cover"
                :background-position "center top"}}
       [:span.mask.bg-gradient-default.opacity-8]
       [:div.container-fluid.d-flex.align-items-center
        [:div.row
         [:div.col-lg-7.col-md-10
          [:h1.display-2.text-white "Hello Jesse"]
          [:p.text-white.mt-0.mb-5
           "This is your profile page. You can see the progress you've made with your work and manage your projects or assigned tasks"]
          [:a.btn.btn-neutral {:href "#!"} "Edit profile"]]]]
       [:div.separator.separator-bottom.separator-skew.zindex-100
        [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
         [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]]])))
