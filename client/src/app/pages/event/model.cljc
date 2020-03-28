(ns app.pages.event.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(def show-page ::show)

(rf/reg-event-fx
 show-page
 (fn [{db :db} [pid phase {:keys [id event-id]}]]
   (case phase
     :init
     {:xhr/fetch [{:uri (str "/Project/" id)
                   :req-id ::national-project}
                  {:uri (str "/Event/" event-id)
                   :req-id show-page}
                  {:uri (str "/Object")
                   :params {:.event.id event-id}
                   :req-id ::object}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 show-page
 :<- [:xhr/response show-page]
 :<- [:xhr/response ::object]
 :<- [:xhr/response ::national-project]
 (fn [[{event :data} {items :data} {project :data}]]
   {:items items
    :event event
    :project project
    :header "Объекты"}))
