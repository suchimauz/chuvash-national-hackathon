(ns app.pages.event.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.event.crud.model :as model]
            [app.pages.event.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.form.inputs :as inputs]))

(defn header [{:keys [header]}]
  [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
   [:div.container.d-flex.align-items-center
    [:div.row
     [:div.col
      [:h1.display-2.text-white header]]]]
   [:div.separator.separator-bottom.separator-skew.zindex-100
    [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
     [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]])

(defn obj-form []
  (fn []
    [:div.container.mt--8
     [:div.row.justify-content-center
      [:div.col
       [:div.card
        [:div.card-body
         [:div.container
          [:form
           [:div.form-group
            [:label.form-control-label
             "Название объекта"]
            [inputs/input form/object-path [:name]]]
           [:div.form-group
            [:label.form-control-label
             "Район"]
            [inputs/z-dropdown form/object-path [:address :district] {:placeholder "Выберите район"}]]
           [:div.row
            [:div.form-group.col
             [:label.form-control-label
              "Город"]
             [inputs/input form/object-path [:address :city]]]
            [:div.form-group.col
             [:label.form-control-label
              "Улица"]
             [inputs/input form/object-path [:address :street]]]]
           [:div.row
            [:div.form-group.col
             [:label.form-control-label
              "Дом"]
             [inputs/input form/object-path [:address :house]]]
            [:div.form-group.col
             [:label.form-control-label
              "Квартира"]
             [inputs/input form/object-path [:address :appartment]]]]]]]]]]]))

(defn form []
  (fn []
    [:div.container.mt--8
     [:div.row.justify-content-center
      [:div.col
       [:div.card
        [:div.card-body
         [:form
          [:div.form-group
           [:label.form-control-label
            "Цель"]
           [inputs/input form/path [:name] {:placeholder "Введите цель"}]
           [:div.row.pt-2
            [:div.col-sm-5
             [:small "Кол-во"]
             [inputs/input form/path [:task :target] {:placeholder "100"}]]
            [:div.col-sm-5
             [:small "Текущее кол-во"]
             [inputs/input form/path [:task :complete] {:placeholder "20"}]]
            [:div.col-sm-2
             [:small "Ед.изм."]
             [inputs/input form/path [:task :unit] {:placeholder "Например: шт."}]]]]]
          [:div.form-group
           [:label.form-control-label
            "Бюджет - млн.руб."]
           [:div.row
            [:div.col-sm-6
             [:small "Федеральный"]
             [inputs/input form/path [:payment :federal] {:placeholder "Например: 321,1"}]]
            [:div.col-sm-6
             [:small "Региональный"]
             [inputs/input form/path [:payment :regional] {:placeholder "Например: 100,5"}]]
            [:div.col-sm-6
             [:small "Муниципальный"]
             [inputs/input form/path [:payment :municipal] {:placeholder "Например: 42,32"}]]
            [:div.col-sm-6
             [:small "Внебюджет"]
             [inputs/input form/path [:payment :other] {:placeholder "Например: 10"}]]]]
          [:div.form-group
           [:label.form-control-label
            "Описание"]
           [inputs/input form/path [:description] {:placeholder "Введите описание"}]]
          [:div.form-group
           [:label.form-control-label
            "Дата"]
           [:div.row
            [:div.col-sm-6
             [:small "Начало"]
             [inputs/time-input form/path [:period :start] {:placeholder "Введите дату начала"}]]
            [:div.col-sm-6
             [:small "Конец"]
             [inputs/time-input form/path [:period :end] {:placeholder "Введите дату завершения"}]]]]]]]]]))

(pages/reg-subs-page
 model/create-page
 (fn [{:keys [cancel-uri] :as page}]
   [:<>
    [header page]
    [form]
    [:div.container.card-body
     [:span.pointer.btn.btn-primary.btn-lg
      {:on-click #(rf/dispatch [::model/create-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary.btn-lg
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]]]))

(pages/reg-subs-page
 model/edit-page
 (fn [{:keys [cancel-uri] :as page} {:keys [event-id]}]
   [:<>
    [header page]
    [form]
    [:div.container.card-body
     [:span.pointer.btn.btn-primary.btn-lg
      {:on-click #(rf/dispatch [::model/edit-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary.btn-lg
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]
     [:a.ml-2.text-danger.pointer
      {:on-click #(rf/dispatch [::model/delete-request event-id])}
      "Удалить"]]]))

(pages/reg-subs-page
 model/object-create-page
 (fn [{:keys [cancel-uri] :as page} {:keys [obj-id]}]
   [:<>
    [header page]
    [obj-form]
    [:div.container.card-body
     [:span.pointer.btn.btn-primary.btn-lg
      {:on-click #(rf/dispatch [::model/object-create-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary.btn-lg
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]]]))

(pages/reg-subs-page
 model/object-edit-page
 (fn [{:keys [cancel-uri] :as page} {:keys [obj-id]}]
   [:<>
    [header page]
    [obj-form]
    [:div.container.card-body
     [:span.pointer.btn.btn-primary.btn-lg
      {:on-click #(rf/dispatch [::model/object-edit-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary.btn-lg
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]
     [:a.ml-2.text-danger.pointer "Удалить"]]]))
