(ns app.pages.project.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.project.crud.model :as model]
            [app.pages.project.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.form.inputs :as inputs]))

(def form
  [:div.card
   [:div.card-header [:h2.mb-0 "Форма создания национального проекта"]]
   [:div.card-body
    [:form
     [:div.flex-row
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "Название"]
        [inputs/input form/path [:name] {:placeholder "Введите название"}]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label "Дата начала"]
        [inputs/input form/path [:startDate] {:placeholder "Введите название"}]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label "Дата окончания"]
        [inputs/input form/path [:endDate] {:placeholder "Введите название"}]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label "Автор"]
        [:select.form-control
         {:data-toggle "select"}
         [:option "Краснов"]
         [:option "Багров"]
         [:option "Федулов"]
         [:option "Туктанов"]]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "Описание"]
        [inputs/input form/path [:description] {:placeholder "Введите название"}]]]]]]])

(def buttons
  [:div.card-body
   [:button.btn.btn-success {:type "button"} "Сохранить"]
   [:button.btn.btn-secondary {:type "button"} "Отменить"]
   [:button.btn.btn-outline-danger {:type "button"} "Удалить"]])

(pages/reg-subs-page
 model/create-page
 (fn []
   [:div.container
    form
    buttons]))

(pages/reg-subs-page
 model/edit-page
 (fn []
   [:div.container
    form
    buttons]))
