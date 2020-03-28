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


(defn modal-author []
  [:modal {:style  {:max-width "800px"}
           :body   (fn []
                   [:div.container
                    [:form
                     [:div.form-group
                      [:label.form-control-label
                       "Название объекта"]
                      [inputs/input form/object-path [:name]]]
                     [:div.form-group
                      [:label.form-control-label
                       "Район"]
                      [inputs/input form/object-path [:address :distinct]]]
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
                       [inputs/input form/object-path [:address :appartment]]]]]])
           :title  "Создание автора"
           :accept {:text "Сохранить"
                    :fn   (fn []
                          (rf/dispatch [::model/create-object]))}}])


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
            "Название"]
           [inputs/input form/path [:name] {:placeholder "Введите название"}]]

          [:div.row
           [:div.form-group.col
            [:label.form-control-label "Обьекты"]]
           [:div.form-group.col-auto
            [:label.form-control-label.pt-3 ""]
            [:div.pt-1.text-primary.pointer
             [:span.mega-octicon.octicon-plus
              {:on-click #(do
                            (rf/dispatch [:zf/init form/object-path form/object])
                            (rf/dispatch (modal-author)))}]]]]

          [:div.form-group
           [:label.form-control-label "Цель"]
           [inputs/combobox form/path [:purpose]]]
          [:div.form-group
           [:label.form-control-label
            "Описание"]
           [inputs/input form/path [:description] {:placeholder "Введите описание"}]]
          [:div.form-group
           [:label.form-control-label
            "Дата начала"]
           [inputs/time-input form/path [:period :start] {:placeholder "Введите дату начала"}]]
          [:div.form-group
           [:label.form-control-label
            "Дата завершения"]
           [inputs/time-input form/path [:period :end] {:placeholder "Введите дату завершения"}]]
          [:div.form-group
           [:label.form-control-label
            "Бюджет"]
           [inputs/input form/path [:amount] {:placeholder "Выделенный бюджет"}]]]]]]]]))

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
 (fn [{:keys [cancel-uri] :as page} {:keys [id]}]
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
