(ns app.pages.project.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.project.crud.model :as model]
            [app.pages.project.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]))

(def form
  [:div.card
   [:div.card-header [:h3.mb-0 "Datepicker"]]
   [:div.card-body
    [:form
     [:div.row
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "One of two cols"]
        [:input#example2cols2Input.form-control
         {:placeholder "One of two cols", :type "text"}]]]
      [:div.row.input-daterange.datepicker.align-items-center
       [:div.col
        [:div.form-group
         [:label.form-control-label "Start date"]
         [:input.form-control
          {:value "06/18/2018",
           :type "text",
           :placeholder "Start date"}]]]
       [:div.col
        [:div.form-group
         [:label.form-control-label "End date"]
         [:input.form-control
          {:value "06/22/2018",
           :type "text",
           :placeholder "End date"}]]]]
      [:div.col-md-6
       [:div.form-group
        [:label.form-control-label
         "Example textarea"]
        [:textarea#exampleFormControlTextarea1.form-control
         {:rows "3"}]]]]]]])




(pages/reg-subs-page
 model/edit-page
 (fn [{:keys [unity]}]
   [:div "123"]))


(pages/reg-subs-page
 model/create-page
 (fn []
   [:div.container
    [:h1 "Форма создания нац. проекта"]
    form
    [:div.btn-component
     [:button.btn.save  "Сохранить"]
     [:button.btn.cancel  "Отмена"]]]))

(pages/reg-subs-page
 model/edit-page
 (fn []
   [:div.container
    [:h1 "Форма редактирования нац. проекта"]
    form
    [:div.btn-component
     [:button.btn.save  "Сохранить"]
     [:button.btn.cancel  "Отмена"]]]))
