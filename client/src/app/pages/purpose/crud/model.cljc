(ns app.pages.purpose.crud.model
  (:require [re-frame.core :as rf]
            [app.pages.purpose.crud.form :as form]))

(def create-page ::create)


(rf/reg-event-fx
 create-page
 (fn [{db :db} [_ phase {:keys [reg-id] :as ss}]]
   (case phase
     :init {:xhr/fetch {:uri     (str "/Project/" reg-id)
                        :success {:event ::form/init}}}
     nil)))

(rf/reg-sub
 create-page
 (fn [_]
   {:header "Создание показателя"}))

(rf/reg-event-fx
 ::create-resource
 (fn [{db :db}]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri     "/Purpose"
                                  :method  :POST
                                  :body    value
                                  :success {:event ::create-success}}}))))

(rf/reg-event-fx
 ::create-success
 (fn [{{{:keys [id reg-id]} :fragment-params} :db} _]
   {:zframes.redirect/redirect {:uri (str "/project/" id "/regional/" reg-id)}
    :flash/flash [:success {:msg "Показатель успешно создан" :title "Успешно!"}]}))

(def edit-page ::edit)

(rf/reg-event-fx
 edit-page
 (fn [_ [_  phase {id :p-id}]]
   (case phase
     (:init :params) {:xhr/fetch {:uri     (str "/Purpose/" id)
                                  :success {:event ::success-purp}}}
     nil)))

(rf/reg-event-fx
 ::success-purp
 (fn [_ [_ data]]
   {:dispatch [::form/init {} data]}))

(rf/reg-sub
 edit-page
 (fn [_]
   {:header "Редактирование показателя"}))

(rf/reg-event-fx
 ::delete
 (fn [_ [_ {:keys [id reg-id p-id]}]]
   {:xhr/fetch {:uri     (str "/Purpose/" p-id)
                :method  :DELETE
                :success {:event ::delete-success :params (str "/project/" id "/regional/" reg-id)}}}))

(rf/reg-event-fx
 ::delete-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect {:uri uri}
    :flash/flash [:success {:msg "Показатель удалён" :title "Успешно!"}]}))
