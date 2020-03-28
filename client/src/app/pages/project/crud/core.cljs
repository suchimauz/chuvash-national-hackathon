(ns app.pages.project.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.project.crud.model :as model]
            [app.pages.project.crud.form :as form]
            [app.form.inputs :as inputs]))

(def buttons
  [:div.card-body
   [:button.btn.btn-success "Сохранить"]
   [:button.btn.btn-secondary "Отменить"]
   [:button.btn.btn-outline-danger "Удалить"]])

(def form
  [:<>
   [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
    [:div.container.d-flex.align-items-center
     [:div.row
      [:div.col
       [:h1.display-2.text-white "Создание национального проекта"]]]]
    [:div.separator.separator-bottom.separator-skew.zindex-100
     [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
      [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]]
   [:div.container.mt--8.pb-5
    [:div.row.justify-content-center
     [:div.col
      [:div.card
       [:div.card-body
        [:form
         [:div.form-group
          [:label.form-control-label
           "Название"]
          [inputs/input form/path [:name] {:placeholder "Введите название"}]]
         [:div.row
          [:div.col.form-group
           [:label.form-control-label "Дата начала"]
           [inputs/time-input form/path [:startDate] {:placeholder "ДД.ММ.ГГГГ"}]]
          [:div.col.form-group
           [:label.form-control-label "Дата окончания"]
           [inputs/time-input form/path [:endDate] {:placeholder "ДД.ММ.ГГГГ"}]]]
         [:div.form-group
          [:label.form-control-label "Автор"]
          [:select.form-control
           {:data-toggle "select"}
           [:option "Краснов"]
           [:option "Багров"]
           [:option "Федулов"]
           [:option "Туктанов"]]]
         [:div.form-group
          [:label.form-control-label
           "Описание"]
          [inputs/input form/path [:description] {:placeholder "Введите название"}]]]
        buttons]]]]]])

(pages/reg-subs-page
 model/create-page
 (fn []
   [:<> form]))

(pages/reg-subs-page
 model/edit-page
 (fn []
   [:<> form]))
