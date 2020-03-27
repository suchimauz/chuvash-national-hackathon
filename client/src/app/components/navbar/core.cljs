(ns app.components.navbar.core
  (:require [re-frame.core :as rf]
            [app.components.navbar.model :as model]))

(defn component []
  (let [*node (rf/subscribe [::model/data])]
    (fn []
      (let [node (deref *node)]
        [:nav.navbar.navbar-top.navbar-expand.navbar-dark.bg-default.border-bottom
         [:div.container-fluid
          [:div.collapse.navbar-collapse
           [:ul.navbar-nav.align-items-center.ml-md-auto
            [:li.nav-item.d-xl-none
             [:div.pr-3.sidenav-toggler.sidenav-toggler-dark
              [:div.sidenav-toggler-inner
               [:i.sidenav-toggler-line]
               [:i.sidenav-toggler-line]
               [:i.sidenav-toggler-line]]]]]
           [:ul.navbar-nav.align-items-center.ml-auto.ml-md-0
            [:li.nav-item.dropdown
             [:div.media.align-items-center
              [:span.avatar.avatar-sm.rounded-circle
               [:img {:src "../../assets/img/theme/team-4.jpg",}]]
              [:div.media-body.ml-2.d-none.d-lg-block
               [:span.mb-0.text-sm.font-weight-bold "John Snow"]]]]]]]]))))
