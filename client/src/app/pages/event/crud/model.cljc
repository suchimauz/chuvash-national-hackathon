(ns app.pages.event.crud.model
  (:require [re-frame.core :as rf]
            [app.pages.event.crud.form :as form]))

(def create-page ::create)
(def edit-page ::edit)


(rf/reg-event-fx
 create-page
 (fn [{db :db} [pid phase {:keys [id]}]]
   (case phase
     :init
     {:dispatch [::form/init {:data {:project {:id (helpers/parse-int id)
                                               :resourceType "Project"}}}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 create-page
 (fn [_]
   {:header "Создание результата"}))


(rf/reg-event-fx
 edit-page
 (fn [{db :db } [pid  phase {:keys [event-id]}]]
   (case phase
     :init {:xhr/fetch {:uri (str "/Event/" event-id)
                        :success {:event ::form/init}}}
     :params {:db db}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 edit-page
 (fn [{{{:keys [event-id regional-id id]} :fragment-params} :db} _]
   {:header "Редактирование результата"
    :cancel-uri (str "/project/" id "/regional/" regional-id "/event/" event-id)}))

(rf/reg-event-fx
 ::create-request
 (fn [{{{:keys [event-id regional-id id]} :fragment-params :as db} :db} _]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri "/Event"
                                  :method :POST
                                  :body value
                                  :success {:event ::create-success :params (str "/project/" id "/regional/" regional-id)}}}))))

(rf/reg-event-fx
 ::edit-request
 (fn [{{{:keys [event-id regional-id id]} :fragment-params :as db} :db} _]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri (str "/Event/" (:id value))
                                  :method :PUT
                                  :body value
                                  :success {:event ::edit-success :params {:uri (str "/project/" id "/regional/" regional-id "/event/" event-id)}}}}))))


(rf/reg-event-fx
 ::delete-request
 (fn [{{{:keys [event-id regional-id id]} :fragment-params :as db} :db} [_ id]]
   {:xhr/fetch {:uri (str "/Event/" id)
                :method :DELETE
                :success {:event ::delete-success :params (str "/project/" id "/regional/" regional-id)}}}))

(rf/reg-event-fx
 ::create-success
 (fn [_ [_ {:keys [id]} s]]
   {:zframes.redirect/redirect (str s id)
    :flash/flash [:success {:msg "Мероприятие успешно создано" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::edit-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Мероприятие успешно отредактировано" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::delete-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Мероприятие успешно удалено" :title "Успешно!"}]}))
