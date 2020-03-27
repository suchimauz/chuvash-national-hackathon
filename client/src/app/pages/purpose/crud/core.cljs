(ns app.pages.purpose.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.purpose.crud.model :as model]
            [app.pages.purpose.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]))

(def form
  [:div.card
   [:div.card-header [:h2.mb-0 "Форма создания цели"]]
   [:div.card-body
    [:form
     [:div.flex-row
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "Название"]
        [:input#example2cols2Input.form-control
         {:placeholder "Введите название", :type "text"}]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "Проект"]
        [:input#example2cols2Input.form-control
         {:placeholder "Здесь должен быть DROPDOWN", :type "text"}]]]]]]])

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
