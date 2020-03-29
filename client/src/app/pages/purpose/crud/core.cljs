(ns app.pages.purpose.crud.core
  (:require [app.pages.model :as pages]
            [app.pages.purpose.crud.model :as model]
            [app.pages.purpose.crud.form :as form]
            [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.form.inputs :as inputs]
            [clojure.string :as str]))

(defn header [{:keys [header]}]
  [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
   [:div.container.d-flex.align-items-center
    [:div.row
     [:div.col
      [:h1.display-2.text-white header]]]]
   [:div.separator.separator-bottom.separator-skew.zindex-100
    [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
     [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]])

(defn form []
  (let [indic (rf/subscribe [:zf/collection-indexes form/path [:indicators]])]
    (fn []
      [:div.container.mt--8
       [:div.row.justify-content-center
        [:div.col
         [:div.card
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
                  [inputs/input form/path [:indicators idx :year]]]
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
                {:on-click #(rf/dispatch [:zf/add-collection-item form/path [:indicators]])}]]]]]]]]]])))

(pages/reg-subs-page
 model/create-page
 (fn [page {:keys [id reg-id] :as params}]
   [:<>
    [header page]
    [form]
    [:div.card-body.container
     [:button.btn.btn-primary.btn-lg {:on-click #(rf/dispatch [::model/create-resource])} "Сохранить"]
     [:a.btn.btn-secondary.btn-lg {:href (helpers/href "project" id "regional" reg-id)}  "Отменить"]]]))

(pages/reg-subs-page
 model/edit-page
 (fn [page {:keys [id reg-id] :as params}]
   [:<>
    [header page]
    [form]
    [:div.card-body.container
     [:button.btn.btn-primary.btn-lg {:on-click #(rf/dispatch [::model/edit-resource])} "Сохранить"]
     [:a.btn.btn-secondary.btn-lg {:href (helpers/href "project" id "regional" reg-id)}  "Отменить"]
     [:button.btn.text-danger {:on-click #(rf/dispatch [::model/delete params])} "Удалить"]]]))
