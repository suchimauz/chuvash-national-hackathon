(ns app.pages.project.model
  (:require [re-frame.core :as rf]
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
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 show-page
 :<- [:xhr/response show-page]
 :<- [:xhr/response ::show-regional]
 (fn [[{national :data} {regionals :data}] _]
   {:national national
    :regionals regionals}))

(def show-regional ::regional)

(rf/reg-event-fx
 show-regional
 (fn [_ [pid phase {:keys [reg-id]}]]
   (case phase
     :init
     {:xhr/fetch [{:uri    (str "/Project/" reg-id)
                   :req-id :regional}
                  {:uri    (str "/Purpose")
                   :params {:.priject.id reg-id}
                   :req-id :purpose}]}
     nil)))

(rf/reg-sub
 show-regional
 :<- [:xhr/response :regional]
 :<- [:xhr/response :purpose]
 (fn [[{project :data} {purposes :data}] _]
   {:project  project
    :purposes purposes}))
