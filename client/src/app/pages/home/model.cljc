(ns app.pages.home.model
  (:require [re-frame.core :as rf]))

(def ^:const index-page ::index)

(rf/reg-event-fx
 index-page
 (fn [_ [_ phase]]
   (cond-> {}
     (#{:init} phase)
     (assoc :json/fetch {:uri     "/categories"
                         :method  "get"
                         :success {:event ::success}}))))

(rf/reg-event-db
 ::success
 (fn [db [_ data]]
   (assoc db :data data)))

(rf/reg-sub
 index-page
 (fn [db]
   {:data (:data db)}))
