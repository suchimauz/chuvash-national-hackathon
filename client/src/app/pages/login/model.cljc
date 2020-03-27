(ns app.pages.login.model
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]))

(def ^:const index-page ::index)

(def ^:const path [:form ::path])
(def ^:const schema
  {:type   :form
   :fields {:login    {:type :string}
            :password {:type :string}}})

(rf/reg-event-fx
 index-page
 (fn [{db :db} [_ phase]]
   (cond-> {}
     (#{:init :params} phase)
     (update :dispatch-n conj [:zf/init path schema {}])
     (#{:deinit} phase)
     (assoc :db (update db :form dissoc ::path)))))

(rf/reg-event-fx
 ::eval
 (fn [{db :db} [_ {:keys [success error]}]]
   (let [eval (zf/eval-form (get-in db path))]
     (merge {:db (assoc-in db path (:form eval))}
            (if (empty? (:errors eval))
              {:dispatch [(:event success) (:value eval)]}
              {:dispatch [(:event error) (:errors eval)]})))))

(rf/reg-event-fx
 ::send
 (fn [_ [_ value]]
   {:xhr/fetch {:uri    "/login"
                :method "post"
                :body   value}}))


(def ^:const register-page ::register)

(rf/reg-event-fx
 register-page
 (fn [{db :db} [_ phase]]
   (cond-> {}
     (#{:init :params} phase)
     (update :dispatch-n conj [:zf/init path schema {}])
     (#{:deinit} phase)
     (assoc :db (update db :form dissoc ::path)))))


(rf/reg-event-fx
 ::register
 (fn [_ [_ value]]
   {:xhr/fetch {:uri    "/register"
                :method "post"
                :body   value}}))
