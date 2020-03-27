(ns app.pages.model
  (:require [re-frame.core :as rf]))

(defonce pages (atom {}))

(rf/reg-sub
 :pages/data
 (fn [db [_ pid]]
   (get db pid)))

(defn reg-page [key page]
  (swap! pages assoc key page))

(defn subscribed-page [key f]
  (let [p (rf/subscribe [:frames.routing/fragment-params])
        m (rf/subscribe [key])]
    (fn [] [f @m @p])))

(defn reg-subs-page [key f]
  (reg-page key (subscribed-page key f)))
