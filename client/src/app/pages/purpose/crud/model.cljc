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
   {:text "123"}))

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
 (fn [_ [_ {:keys [id]} s]]
   {;:zframes.redirect/redirect (str s id)
    :flash/flash [:success {:msg "Проект успешно создан" :title "Успешно!"}]}))

(def edit-page ::edit)

(rf/reg-event-fx
 edit-page
 (fn [{db :db} [_  phase {id :id}]]
   (case phase
     (:init :params) {:dispatch [::form/init]}
     nil)))

(rf/reg-sub
 edit-page
 (fn [_]
   {:text "123"}))
