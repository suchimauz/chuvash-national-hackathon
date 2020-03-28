(ns app.pages.home.model
  (:require [re-frame.core :as rf]))

(def ^:const index-page ::index)

(rf/reg-event-fx
 index-page
 (fn [{db :db} [pid phase]]
   (case phase
     :init
     {:xhr/fetch {:uri "/Project"
                  :req-id pid
                  :params {:.category "national"}}}
     :deinit {:db (dissoc db pid)}
     nil)))

(rf/reg-sub
 index-page
 :<- [:xhr/response index-page]
 (fn [{nationals :data}]
   {:items (map
            #(assoc % :href (str "#/project/" (:id %)))
            nationals)}))
