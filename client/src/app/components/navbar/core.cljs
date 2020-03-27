(ns app.components.navbar.core
  (:require [re-frame.core :as rf]
            [app.components.navbar.model :as model]))

(defn component []
  (let [*node (rf/subscribe [::model/data])]
    (fn []
      (let [node (deref *node)]
        [:nav#navbar-main.navbar.navbar-horizontal.navbar-transparent.navbar-main.navbar-expand-lg.navbar-light
         [:div.container
          [:a.navbar-brand
           {:href "../../pages/dashboards/dashboard.html"}
           [:img {:src "../../assets/img/brand/white.png"}]]
          [:button.navbar-toggler
           {:aria-label    "Toggle navigation",
            :aria-expanded "false",
            :aria-controls "navbar-collapse",
            :data-target   "#navbar-collapse",
            :data-toggle   "collapse",
            :type          "button"}
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
               {:aria-label    "Toggle navigation",
                :aria-expanded "false",
                :aria-controls "navbar-collapse",
                :data-target   "#navbar-collapse",
                :data-toggle   "collapse",
                :type          "button"}
               [:span]
               [:span]]]]]
           [:ul.navbar-nav.mr-auto
            [:li.nav-item
             [:a.nav-link
              {:href "../../pages/dashboards/dashboard.html"}
              [:span.nav-link-inner--text "Dashboard"]]]
            [:li.nav-item
             [:a.nav-link
              {:href "../../pages/examples/pricing.html"}
              [:span.nav-link-inner--text "Pricing"]]]
            [:li.nav-item
             [:a.nav-link
              {:href "../../pages/examples/login.html"}
              [:span.nav-link-inner--text "Login"]]]
            [:li.nav-item
             [:a.nav-link
              {:href "../../pages/examples/register.html"}
              [:span.nav-link-inner--text "Register"]]]
            [:li.nav-item
             [:a.nav-link
              {:href "../../pages/examples/lock.html"}
              [:span.nav-link-inner--text "Lock"]]]]
           [:hr.d-lg-none]
           [:ul.navbar-nav.align-items-lg-center.ml-lg-auto
            [:li.nav-item
             [:a.nav-link.nav-link-icon
              {:data-original-title "Like us on Facebook",
               :data-toggle         "tooltip",
               :target              "_blank",
               :href                "https://www.facebook.com/creativetim"}
              [:i.fab.fa-facebook-square]
              [:span.nav-link-inner--text.d-lg-none "Facebook"]]]
            [:li.nav-item
             [:a.nav-link.nav-link-icon
              {:data-original-title "Follow us on Instagram",
               :data-toggle         "tooltip",
               :target              "_blank",
               :href                "https://www.instagram.com/creativetimofficial"}
              [:i.fab.fa-instagram]
              [:span.nav-link-inner--text.d-lg-none "Instagram"]]]
            [:li.nav-item
             [:a.nav-link.nav-link-icon
              {:data-original-title "Follow us on Twitter",
               :data-toggle         "tooltip",
               :target              "_blank",
               :href                "https://twitter.com/creativetim"}
              [:i.fab.fa-twitter-square]
              [:span.nav-link-inner--text.d-lg-none "Twitter"]]]
            [:li.nav-item
             [:a.nav-link.nav-link-icon
              {:data-original-title "Star us on Github",
               :data-toggle         "tooltip",
               :target              "_blank",
               :href                "https://github.com/creativetimofficial"}
              [:i.fab.fa-github]
              [:span.nav-link-inner--text.d-lg-none "Github"]]]
            [:li.nav-item.d-none.d-lg-block.ml-lg-4
             [:a.btn.btn-neutral.btn-icon
              {:target "_blank",
               :href
               "https://www.creative-tim.com/product/argon-dashboard-pro"}
              [:span.btn-inner--icon [:i.fas.fa-shopping-cart.mr-2]]
              [:span.nav-link-inner--text "Purchase now"]]]]]]]))))
