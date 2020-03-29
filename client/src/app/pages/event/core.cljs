(ns app.pages.event.core
  (:require [re-frame.core           :as rf]
            [app.form.inputs :as inputs]
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
     [:div.container-fluid
      [:div.row.align-items-center.py-4
       [:div.col-lg-6.col-7
        [breadcrumb/component [{:display "Главная"
                                :href (str "#/")}
                               {:display "Проекты"
                                :href (str "#/project/" id)}
                               {:display "Региональные"
                                :href (str "#/project/" id "/regional/" reg-id)}
                               {:display "Мероприятия"
                                :href (str "#/project/" id "/regional/" reg-id "/event/" event-id)}]]]
       (when auth?
         [:div.col-lg-6.col-5.text-right
          [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/regional/" reg-id "/event/" event-id "/edit")} "Редактировать"]])]]]
    [:div.container
     [:div
      [:div.card-header.bg-transparent.row.align-items-center
       {:style {:justify-content :space-between}}
       [:h3.mb-0 "Объекты"]
       [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/event/" event-id "/object/create")} "Добавить объект"]]
      [:div.list-group.list-group-flush.mt-3
       [:div.form-group
        [:div.row
         [:div.col-sm-4
          [inputs/z-dropdown model/filter-path [:district] {:placeholder "Выберите район"}]]
         [:div.col-sm-8
          [inputs/input model/filter-path [:ilike] {:placeholder "Введите поисковый запрос"}]]]]
       (when (empty? items)
         [:div.list-group.list-group-flush "Не найдено ни одного объекта"])
       (map-indexed
        (fn [idx item] ^{:key idx}
          (let [address (:address item)]
            [:div.list-group-item.list-group-item-action.pointer
             {:style {:width "100%"}
              :on-click #(rf/dispatch [:zframes.redirect/redirect {:uri (str "#/project/" id "/regional/" reg-id "/event/" event-id "/object/" (:id item) "/edit")}])}
             [:small.font-weight-bold (str (or (:district address) (:city address)) ", " (:street address) ", " (:house address) ", " (:appartment address))]
             [:p
              [:small.text-muted.font-weight-bold (:name item)]]]))
        items)]]]]))
