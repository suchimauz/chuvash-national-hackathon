(ns app.pages.event.crud.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.pages.event.crud.form :as form]))

(def create-page ::create)
(def edit-page ::edit)

(def object-create-page ::object-create)
(def object-show-page ::object-show)
(def object-edit-page ::object-edit)



(rf/reg-sub
 object-create-page
 (fn [{{:keys [event-id reg-id id obj-id]} :fragment-params} _]
   {:header "Создание объекта"
    :cancel-uri (str "/project/" id "/regional/" reg-id "/event/" event-id)}))

(rf/reg-sub
 object-show-page
 (fn [{{:keys [event-id reg-id id obj-id]} :fragment-params} _]
   {:header "Создание объекта"}))

(rf/reg-sub
 object-edit-page
 (fn [{{:keys [event-id reg-id id obj-id]} :fragment-params} _]
   {:header "Редактирование объекта"
    :cancel-uri (str "/project/" id "/regional/" reg-id "/event/" event-id)}))

(rf/reg-event-fx
 object-create-page
 (fn [{db :db } [pid  phase {:keys [id reg-id event-id]}]]
   (case phase
     :init
     {:dispatch [::form/object-init {:data {:event {:id (helpers/parse-int event-id)
                                             :resourceType "Event"}}}]}
     :params
     {:db db}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-event-fx
 object-show-page
 (fn [{db :db } [pid  phase {:keys [object-id]}]]
   (case phase
     :init {:xhr/fetch {:uri (str "/Event/" object-id)
                        :success {:event ::form/init}}}
     :params {:db db}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-event-fx
 object-edit-page
 (fn [{db :db } [pid  phase {:keys [obj-id]}]]
   (case phase
     :init {:xhr/fetch {:uri (str "/Object/" obj-id)
                        :success {:event ::form/object-init}}}
     :params {:db db}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-event-fx
 create-page
 (fn [{db :db} [pid phase {:keys [id reg-id]}]]
   (case phase
     :init
     {:dispatch [::form/init {:data {:project {:id (helpers/parse-int reg-id)
                                               :resourceType "Project"}}}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 create-page
 (fn [{{:keys [event-id reg-id id]} :fragment-params} _]
   {:header "Создание результата"
    :cancel-uri (str "/project/" id "/regional/" reg-id)}))


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
 (fn [{{:keys [event-id reg-id id]} :fragment-params} _]
   {:header "Редактирование результата"
    :cancel-uri (str "/project/" id "/regional/" reg-id "/event/" event-id)}))

(rf/reg-event-fx
 ::create-request
 (fn [{{{:keys [event-id reg-id id]} :fragment-params :as db} :db} _]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri "/Event"
                                  :method :POST
                                  :body value
                                  :success {:event ::create-success :params {:uri (str "/project/" id "/regional/" reg-id)}}}}))))

(rf/reg-event-fx
 ::object-create-request
 (fn [{{{:keys [event-id reg-id id]} :fragment-params :as db} :db} _]
   (form/eval-object db
                   (fn [value]
                     {:xhr/fetch {:uri "/Object"
                                  :method :POST
                                  :body value
                                  :success {:event ::create-success :params {:uri (str "/project/" id "/regional/" reg-id "/event/" event-id)}}}}))))


(rf/reg-event-fx
 ::object-create-success
 (fn [_ [_ {:keys [id]} uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Объект успешно создан" :title "Успешно!"}]}))


(rf/reg-event-fx
 ::edit-request
 (fn [{{{:keys [event-id reg-id id]} :fragment-params :as db} :db} _]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri (str "/Event/" (:id value))
                                  :method :PUT
                                  :body value
                                  :success {:event ::edit-success :params {:uri (str "/project/" id "/regional/" reg-id "/event/" event-id)}}}}))))

(rf/reg-event-fx
 ::object-edit-request
 (fn [{{{:keys [obj-id event-id reg-id id]} :fragment-params :as db} :db} _]
   (form/eval-form db
                   (fn [value]
                     {:xhr/fetch {:uri (str "/Object/" obj-id)
                                  :method :PUT
                                  :body value
                                  :success {:event ::object-edit-success :params {:uri (str "/project/" id "/regional/" reg-id "/event/" event-id "/object/" obj-id)}}}}))))

(rf/reg-event-fx
 ::object-edit-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Проект успешно отредактирован" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::delete-request
 (fn [{{{:keys [event-id reg-id id]} :fragment-params :as db} :db} [_ id]]
   {:xhr/fetch {:uri (str "/Event/" id)
                :method :DELETE
                :success {:event ::delete-success :params (str "/project/" id "/regional/" reg-id)}}}))

(rf/reg-event-fx
 ::delete-object
 (fn [{{{:keys [event-id reg-id id obj-id]} :fragment-params :as db} :db} [_ id]]
   {:xhr/fetch {:uri (str "/Object/" id)
                :method :DELETE
                :success {:event ::delete-object-success :params (str "#/project/" id "/regional/" reg-id "/event/" event-id)}}}))

(rf/reg-event-fx
 ::delete-object-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Мероприятие успешно удалено" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::create-success
 (fn [_ [_ {:keys [id]} uri]]
   {:zframes.redirect/redirect uri
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

(rf/reg-event-fx
 ::create-object
 (fn [{db :db} _]
   (form/eval-object db
                     (fn [value]
                       {:xhr/fetch {:uri    "/Object"
                                    :method :POST
                                    :body   value}}))))
