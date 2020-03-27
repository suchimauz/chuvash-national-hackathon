(ns app.pages.project.model
  (:require [re-frame.core :as rf]))

(def ^:const show-page ::show)

(rf/reg-event-fx
 show-page
 (fn [_ [_ phase]]
   {}))

(rf/reg-sub
 show-page
 (fn [db]
   {}))
