(ns app.pages.project.crud.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [app.pages.project.crud.form :as form]))

(def create-page ::create)
(def edit-page ::edit)

(def create-regional-page ::create-regional)
(def edit-regional-page ::edit-regional)

(rf/reg-event-fx
 create-page
 (fn [{db :db} [pid phase {:keys [id]}]]
   (case phase
     :init
     {:dispatch [::form/init {:data {:category "national"}}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 create-page
 (fn [_]
   {:header "Создание национального проекта"}))

(rf/reg-event-fx
 ::create-request
 (fn [{db :db} _]
   (form/eval-form db
    (fn [value]
      {:xhr/fetch {:uri "/Project"
                   :method :POST
                   :body value
                   :success {:event ::create-success :params "/project/"}}}))))

(rf/reg-event-fx
 edit-page
 (fn [{db :db } [pid phase {:keys [id]}]]
   (case phase
     :init {:xhr/fetch {:uri (str "/Project/" id)
                        :success {:event ::form/init}}}
     :params {:db db}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 edit-page
 (fn [{{{:keys [id]} :fragment-params} :db} _]
   {:header "Редактирование национального проекта"
    :cancel-uri (str "/project/" id)}))

(rf/reg-event-fx
 ::edit-request
 (fn [{db :db} _]
   (form/eval-form db
    (fn [value]
      {:xhr/fetch {:uri (str "/Project/" (:id value))
                   :method :PUT
                   :body value
                   :success {:event ::edit-success :params {:uri (str "/project/" (:id value))}}}}))))

(rf/reg-event-fx
 ::delete-request
 (fn [{db :db} [_ id]]
   {:xhr/fetch {:uri (str "/Project/" id)
                :method :DELETE
                :success {:event ::delete-success :params "/project"}}}))



(rf/reg-event-fx
 create-regional-page
 (fn [{db :db} [pid phase {:keys [reg-id id]}]]
   (case phase
     :init
     {:dispatch [::form/init {:data {:category "regional"
                                     :project {:id (helpers/parse-int id)
                                               :resourceType "Project"}}}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-event-fx
 ::create-regional-request
 (fn [{{{:keys [id reg-id]} :fragment-params :as db} :db} _]
   (form/eval-form db
    (fn [value]
      {:xhr/fetch {:uri "/Project"
                   :method :POST
                   :body value
                   :success {:event ::create-success :params (str "/project/" id )}}}))))

(rf/reg-sub
 create-regional-page
 :<- [:route-map/fragment-params]
 (fn [{:keys [id]} _]
   {:header "Создание регионального проекта"
    :cancel-uri (str "/project/" id)}))


(rf/reg-event-fx
 edit-regional-page
 (fn [{db :db } [pid phase {:keys [reg-id id]}]]
   (case phase
     :init {:xhr/fetch {:uri (str "/Project/" reg-id)
                        :success {:event ::form/init}}}
     :params {:db db}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 edit-regional-page
 :<- [:route-map/fragment-params]
 (fn [{:keys [id reg-id]} _]
   {:header "Редактирование регионального проекта"
    :cancel-uri (str "/project/" id "/regional/" reg-id)}))

(rf/reg-event-fx
 ::edit-regional-request
 (fn [{{{:keys [id]} :fragment-params :as db} :db} _]
   (form/eval-form db
    (fn [value]
      {:xhr/fetch {:uri (str "/Project/" (:id value))
                   :method :PUT
                   :body value
                   :success {:event ::edit-success :params {:uri (str "/project/" id "/regional/" (:id value))}}}}))))

(rf/reg-event-fx
 ::delete-regional-request
 (fn [{{{:keys [id]} :fragment-params :as db} :db} [_ reg-id]]
   {:xhr/fetch {:uri (str "/Project/" reg-id)
                :method :DELETE
                :success {:event ::delete-success :params (str "/project/" id)}}}))

(rf/reg-event-fx
 ::create-success
 (fn [_ [_ {:keys [id]} s]]
   {:zframes.redirect/redirect {:uri s}
    :flash/flash [:success {:msg "Проект успешно создан" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::edit-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Проект успешно отредактирован" :title "Успешно!"}]}))

(rf/reg-event-fx
 ::delete-success
 (fn [_ [_ _ uri]]
   {:zframes.redirect/redirect uri
    :flash/flash [:success {:msg "Проект успешно удалён" :title "Успешно!"}]}))


(rf/reg-event-fx
 ::create-author
 (fn [{db :db} _]
   (form/eval-author db
                     (fn [value]
                       {:xhr/fetch {:uri    "/Author"
                                    :method :POST
                                    :body   value}}))))
