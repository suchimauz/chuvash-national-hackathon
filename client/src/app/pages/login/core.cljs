(ns app.pages.login.core
  (:require [app.pages.model       :as page]
            [app.form.inputs       :as input]
            [app.pages.login.model :as model]
            [re-frame.core :as rf]))

(page/reg-page
 model/index-page
 (fn [page]
   [:<>
    [:div.header.bg-gradient-primary.py-7.py-lg-8.pt-lg-9
     [:div.separator.separator-bottom.separator-skew.zindex-100
      [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
       [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]]
    [:div.container.mt--8.pb-5
     [:div.row.justify-content-center
      [:div.col-lg-5.col-md-7.mt-8
       [:div.card.bg-ligth.border-0.mb-0
        [:div.card-header.bg-transparent.text-center
         [:h1 "Авторизация"]]
        [:div.separator.separator-bottom.separator-skew.zindex-100
         [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
          [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]
        [:div.card-body.px-lg-5.py-lg-5.bg-primary
         [:div
          [:div.form-group.mb-3
           [input/input model/path [:email] {:placeholder "Email"}]]
          [:div.form-group
           [input/input model/path [:password] {:placeholder "Пароль"
                                                :type "password"
                                                :autoComplete "new-password"}]]
          [:div.custom-control.custom-control-alternative.custom-checkbox
           [:input#customCheckLogin.custom-control-input
            {:type "checkbox"}]
           [:label.custom-control-label
            [:span.text-muted "Запомнить"]]]
          [:div.text-center
           [:span.btn.btn-white.my-4.pointer
            {:on-click #(rf/dispatch [::model/eval {:success {:event ::model/send}}])}
            "Войти"]]]]]]]]]))

(page/reg-page
 model/register-page
 (fn [page]
   [:<>
    [:div.header.bg-gradient-primary.py-7.py-lg-8.pt-lg-9
     [:div.separator.separator-bottom.separator-skew.zindex-100
      [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
       [:polygon.fill-default {:points "2560 0 2560 100 0 100"}]]]]
    [:div.container.mt--8.pb-5
     [:div.row.justify-content-center
      [:div.col-lg-5.col-md-7
       [:div.card.bg-secondary.border-0.mb-0
        [:div.card-header.bg-transparent.text-center
         [:h1 "Регистрация"]]
        [:div.card-body.px-lg-5.py-lg-5
         [:div.text-center.text-muted.mb-4
          [:small "Есть аккаунт? "]
          [:a.small {:href "#/login" } "Войдите"]]
         [:form
          [:div.form-group.mb-3
           [:div.input-group.input-group-merge.input-group-alternative
            [:div.input-group-prepend
             [:span.input-group-text [:i.ni.ni-email-83]]]
            [input/input model/path [:login] {:placeholder "Email"}]]]
          [:div.form-group
           [:div.input-group.input-group-merge.input-group-alternative
            [:div.input-group-prepend
             [:span.input-group-text [:i.ni.ni-lock-circle-open]]]
            [input/input model/path [:password] {:placeholder "Пароль"}]]]
          [:div.custom-control.custom-control-alternative.custom-checkbox
           [:input#customCheckLogin.custom-control-input
            {:type "checkbox"}]
           [:label.custom-control-label
            {:for " customCheckLogin"}
            [:span.text-muted "Remember me"]]]
          [:div.text-center
           [:button.btn.btn-primary.my-4
            {:on-click #(rf/dispatch [::model/eval {:success {:event ::model/register}}])}
            "Зарегистрироваться"]]]]]]]]]))
