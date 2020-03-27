(ns app.pages.login.core
  (:require [app.pages.model       :as page]
            [app.pages.login.model :as model]))

(page/reg-subs-page
 model/index-page
 (fn [page fragment]
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
         [:h1 "Welcome!"]]
        [:div.card-body.px-lg-5.py-lg-5
         [:div.text-center.text-muted.mb-4
          [:small "Or sign in with credentials"]]
         [:form
          {:role "form"}
          [:div.form-group.mb-3
           [:div.input-group.input-group-merge.input-group-alternative
            [:div.input-group-prepend
             [:span.input-group-text [:i.ni.ni-email-83]]]
            [:input.form-control
             {:type "email", :placeholder "Email"}]]]
          [:div.form-group
           [:div.input-group.input-group-merge.input-group-alternative
            [:div.input-group-prepend
             [:span.input-group-text [:i.ni.ni-lock-circle-open]]]
            [:input.form-control
             {:type "password", :placeholder "Password"}]]]
          [:div.custom-control.custom-control-alternative.custom-checkbox
           [:input#customCheckLogin.custom-control-input
            {:type "checkbox"}]
           [:label.custom-control-label
            {:for " customCheckLogin"}
            [:span.text-muted "Remember me"]]]
          [:div.text-center
           [:button.btn.btn-primary.my-4
            {:type "button"}
            "Sign in"]]]]]]]]]))
