(ns app.pages.event.core
  (:require [re-frame.core           :as rf]
            [app.pages.model         :as page]
            [app.helpers :as h]
            [app.pages.event.model :as model]
            [app.components.chart.doughnut :as doughnut]
            [app.components.breadcrumb.core :as breadcrumb]))

(page/reg-subs-page
 model/show-page
 (fn [{:keys [header items project] {:keys [name]} :event} {:keys [id reg-id event-id]} {:keys [auth?]}]
   [:<>
    [:div.header.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     {:style {:background-image (str "url(http://localhost:8990" (:img project) ")")}}
     [:span.mask.bg-gradient-default.opacity-8]
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-9
       [:h1.display-4.text-white name]
       [:h1.display-2.text-white header]
       [:p.text-white.mt-0.mb-5 (:description project)]]]
     [:div.container-fluid.d-flex.justify-content-end
      [:div.row.align-items-center.py-4
       (when auth?
         [:div.col-lg-6.col-5.text-right
          [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/regional/" reg-id "/event/" event-id "/edit")} "Редактировать"]])]]]
    [:div.container
     [:div
      [:div.card-header.bg-transparent.row.align-items-center
       {:style {:justify-content :space-between}}
       [:h3.mb-0 "Объекты"]
       [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/event/" event-id "/object/create")} "Добавить объект"]]
      [:div.list-group.list-group-flush.mb-5
       (map-indexed
        (fn [idx item] ^{:key idx}
          [:div.list-group-item.list-group-item-action.pointer
           {:style {:width "100%"}
            :on-click #(rf/dispatch [:zframes.redirect/redirect {:uri (str "#/project/" id "/regional/" reg-id "/event/" event-id "/object/" (:id item) "/edit")}])}
           [:small.text-muted.font-weight-bold (:name item)]
           [:h5.mt-3.mb-0 (-> item :address :city)]])
        items)]]]]))
