(ns app.pages.event.crud.model
  (:require [re-frame.core :as rf]
            [app.pages.project.crud.form :as form]))

(def create-page ::create)
(def edit-page ::edit)


(rf/reg-event-fx
 create-page
 (fn [{db :db} [pid phase _]]
   (case phase
     :init
     {:dispatch [::form/init {:data {:active true}}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 create-page
 (fn [_]
   {:text "123"}))


(rf/reg-event-fx
 edit-page
 (fn [{db :db } [pid  phase _]]
   (case phase
     (:init :params)
     {:db       db
      :dispatch [::form/init]}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 edit-page
 (fn [_ _]
   {:text "123"}))
