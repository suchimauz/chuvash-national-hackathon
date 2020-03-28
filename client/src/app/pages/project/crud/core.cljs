(ns app.pages.project.crud.core
  (:require [app.pages.model :as pages]
            [re-frame.core :as rf]
            [app.pages.project.crud.model :as model]
            [app.pages.project.crud.form :as form]
            [app.pages.project.crud.file :as file]
            [app.form.inputs :as inputs]))

(defn form []
  (let [category (rf/subscribe [:zf/get-value form/path [:category]])
        img      (rf/subscribe [:zf/get-value form/path [:img]])]
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
            (when (#{"regional"} @category)
              [:div.row
               [:div.col.form-group
                [:label.form-control-label "Дата начала"]
                [inputs/time-input form/path [:startDate] {:placeholder "ДД.ММ.ГГГГ"}]]
               [:div.col.form-group
                [:label.form-control-label "Дата окончания"]
                [inputs/time-input form/path [:endDate] {:placeholder "ДД.ММ.ГГГГ"}]]])
            [:div.form-group
             [:label.form-control-label "Автор"]
             [inputs/combobox form/path [:author]]]
            [:div.form-group
             [:label.form-control-label
              "Описание"]
             [inputs/textarea form/path [:description] {:placeholder "Введите описание проекта"}]]
            [:div.row
             [:div.col
              [:div.custom-file
               [:input#customFileLang.custom-file-input
                {:lang "en", :type "file"
                 :on-change #(rf/dispatch [::file/upload (-> % .-target .-files array-seq first)])}]
               [:input.form-control.custom-file-label
                {:placeholder "Укажите фон"}]]]
             [:div.col-1
              [:img.avatar {:src (str "http://localhost:8990" @img)}]]]]]]]]])))

(defn header [{:keys [header]}]
  [:div.header.bg-gradient-primary.py-8.py-lg-8.pt-lg-9
   [:div.container.d-flex.align-items-center
    [:div.row
     [:div.col
      [:h1.display-2.text-white header]]]]
   [:div.separator.separator-bottom.separator-skew.zindex-100
    [:svg {:viewBox "0 0 10 100" :y "0" :x "0"}
     [:polygon.fill-white {:points "2560 0 2560 100 0 100"}]]]])

(pages/reg-subs-page
 model/create-page
 (fn [{:keys [cancel-uri] :as page}]
   [:<>
    [header page]
    [form]
    [:div.container.card-body
     [:span.pointer.btn.btn-success
      {:on-click #(rf/dispatch [::model/create-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]]]))

(pages/reg-subs-page
 model/edit-page
 (fn [{:keys [cancel-uri] :as page} {:keys [id]}]
   [:<>
    [header page]
    [form]
    [:div.card-body
     [:span.pointer.btn.btn-success
      {:on-click #(rf/dispatch [::model/edit-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]
     [:span.pointer.btn.btn-outline-danger
      {:on-click #(rf/dispatch [::model/delete-request id])}
      "Удалить"]]]))

(pages/reg-subs-page
 model/create-regional-page
 (fn [{:keys [cancel-uri] :as page}]
   [:<>
    [header page]
    [form]
    [:div.card-body
     [:span.pointer.btn.btn-success
      {:on-click #(rf/dispatch [::model/create-regional-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]]]))

(pages/reg-subs-page
 model/edit-regional-page
 (fn [{:keys [cancel-uri] :as page} {:keys [regional-id]}]
   [:<>
    [header page]
    [form]
    [:div.card-body
     [:span.pointer.btn.btn-success
      {:on-click #(rf/dispatch [::model/edit-regional-request])}
      "Сохранить"]
     [:span.pointer.btn.btn-secondary
      {:on-click #(rf/dispatch [:zframes.redirect/redirect {:uri cancel-uri}])}
      "Отменить"]
     [:span.pointer.btn.btn-outline-danger "Удалить"
      {:on-click #(rf/dispatch [::model/delete-regional-request regional-id])}]]]))
