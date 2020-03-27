(ns app.components.navbar.core
  (:require [re-frame.core :as rf]
            [app.components.navbar.model :as model]))

(defn component []
  (let [*node (rf/subscribe [::model/data])
        auth? (rf/subscribe [:auth/auth?])]
    (fn []
      (let [node (deref *node)]
        [:nav.navbar.navbar-horizontal.navbar-transparent.navbar-main.navbar-expand-lg.navbar-light
         [:div.container-fluid
          [:a.navbar-brand
           {:href "#/home"}
           [:img {:src "../../assets/img/brand/white.png"}]]
          [:button.navbar-toggler
           {:aria-label "Toggle navigation",
            :aria-expanded "false",
            :aria-controls "navbar-collapse",
            :data-target "#navbar-collapse",
            :data-toggle "collapse",
            :type "button"}
           [:span.navbar-toggler-icon]]
          [:div#navbar-collapse.navbar-collapse.navbar-custom-collapse.collapse
           [:div.navbar-collapse-header
            [:div.row
             [:div.col-6.collapse-brand
              [:a
               {:href "../../pages/dashboards/dashboard.html"}
               [:img {:src "../../assets/img/brand/blue.png"}]]]
             [:div.col-6.collapse-close
              [:button.navbar-toggler
               {:aria-label "Toggle navigation",
                :aria-expanded "false",
                :aria-controls "navbar-collapse",
                :data-target "#navbar-collapse",
                :data-toggle "collapse",
                :type "button"}
               [:span]
               [:span]]]]]
           [:hr.d-lg-none]
           [:ul.navbar-nav.align-items-lg-center.ml-lg-auto
            [:li.nav-item.d-none.d-lg-block.ml-lg-4
             (if @auth?
               [:span.btn-danger.btn {:on-click #(rf/dispatch [:zframes.auth/logout])}
                [:span.nav-link-inner--text "Выход"]]
               [:a.btn.btn-neutral.btn-icon
                [:a.nav-link-inner--text {:href "#/login"} "Авторизация"]])]]]]]))))
