(ns app.pages.event.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(def show-page ::show)
(def filter-path [:form ::filter])
(def filter-schema
  {:type :form
   :on-change [[::filter-change]]
   :fields {:district {:type :string
                       :items helpers/districts}
            :ilike {:type :string}}})

(rf/reg-event-fx
 ::filter-change
 (fn [{db :db} [_ v]]
   {:dispatch [:zframes.redirect/set-params v]}))

(rf/reg-event-fx
 ::filter-init
 (fn [coeff [_ {:keys [data]}]]
   {:dispatch-n [[:zf/init filter-path filter-schema data]]}))

(rf/reg-event-fx
 show-page
 (fn [{db :db} [pid phase {:keys [id event-id params]}]]
   (case phase
     :init
     {:xhr/fetch [{:uri (str "/Project/" id)
                   :req-id ::national-project}
                  {:uri (str "/Event/" event-id)
                   :req-id show-page}
                  {:uri (str "/Object")
                   :params {:.event.id event-id}
                   :req-id ::object}]
      :dispatch [::filter-init params]}
     :params
     {:xhr/fetch {:uri (str "/Object")
                  :params {:.event.id event-id
                           :.address.district (:district params)
                           :ilike (:ilike params)}
                  :req-id ::object}}
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
