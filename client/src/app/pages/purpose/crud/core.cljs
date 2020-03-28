(ns app.pages.purpose.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.purpose.crud.model :as model]
            [app.pages.purpose.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.form.inputs :as inputs]
            [clojure.string :as str]))

(defn form []
  (let [indic (rf/subscribe [:zf/collection-indexes form/path [:indicators]])]
    (fn []
      [:div.card
       [:div.card-header [:h2.mb-0 "Форма создания цели"]]
       [:div.card-body
        [:form
         [:div.form-group
          [:label.form-control-label "Название"]
          [inputs/input form/path [:name] {:placeholder "Введите название"}]]
         [:div.border-left
          (map
           (fn [idx]
             [:div.row.pl-3
              [:div.form-group.col
               [:label.form-control-label "Год"]
               [inputs/time-input form/path [:indicators idx :year]]]
              [:div.form-group.col
               [:label.form-control-label "Текущее значение"]
               [inputs/input form/path [:indicators idx :current]]]
              [:div.form-group.col
               [:label.form-control-label "Планируемое"]
               [inputs/input form/path [:indicators idx :planning]]]])
           @indic)
          [:div.form-group.col-auto.d-flex.justify-content-center
           [:div.pt-1.text-primary.pointer
            [:span.mega-octicon.octicon-plus
             {:on-click #(rf/dispatch [:zf/add-collection-item form/path [:indicators]])}]]]]]]])))

(def buttons
  [:div.card-body
   [:button.btn.btn-success {:on-click #(rf/dispatch [::model/create-resource])} "Сохранить"]
   [:button.btn.btn-secondary  "Отменить"]
   [:button.btn.btn-outline-danger {:type "button"} "Удалить"]])

(pages/reg-subs-page
 model/create-page
 (fn []
   [:div.container
    [form]
    buttons]))

(pages/reg-subs-page
 model/edit-page
 (fn []
   [:div.container
    [form]
    buttons]))
