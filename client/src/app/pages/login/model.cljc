(ns app.pages.login.model
  (:require [re-frame.core :as rf]))

(def ^:const index-page ::index)

(rf/reg-event-fx
 index-page
 (fn [_ [_ hook]]
   (prn hook)))

(rf/reg-sub
 index-page
 (fn [db] {}))
