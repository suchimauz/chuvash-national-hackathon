(ns app.pages.project.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(def show-page ::show)

(rf/reg-event-fx
 ::index
 (fn [_ _]
   {:zframes.redirect/redirect {:uri "/home"}}))

(rf/reg-sub
 ::breadcrumb
 :<- [:xhr/response show-page]
 (fn [{{:keys [id name]} :data}]
   name))

(rf/reg-event-fx
 show-page
 (fn [{db :db} [pid phase {:keys [id]}]]
   (case phase
     :init
     {:xhr/fetch [{:uri (str "/Project/" id)
                   :req-id pid}
                  {:uri "/Project"
                   :params {:.project.id id}
                   :req-id ::show-regional}]}
     nil)))

(rf/reg-sub
 show-page
 :<- [:xhr/response show-page]
 :<- [:xhr/response ::show-regional]
 (fn [[{national :data} {regionals :data}] _]
   {:national  national
    :regionals regionals}))

(def show-regional ::regional)

(rf/reg-event-fx
 show-regional
 (fn [_ [_ phase {:keys [reg-id]}]]
   (case phase
     :init {:xhr/fetch [{:uri    (str "/Project/" reg-id)
                         :params {:assoc "project"}
                         :req-id :regional}
                        {:uri    (str "/Purpose")
                         :params {:.project.id reg-id}
                         :req-id :purpose}
                        {:uri    (str "/Event")
                         :params {:.project.id reg-id}
                         :req-id :event}]}
     nil)))

(defn event-item-map [{:keys [name id period amount task]}]
  {:id id
   :name name
   :date (helpers/date-iso->rus-format (:end period))})

(rf/reg-sub
 show-regional
 :<- [:xhr/response :regional]
 :<- [:xhr/response :purpose]
 :<- [:xhr/response :event]
 (fn [[{project :data} {purposes :data} {events :data}] _]
   {:project  project
    :purposes purposes
    :events   (map
               event-item-map
               events)}))
