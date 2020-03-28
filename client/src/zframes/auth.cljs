(ns zframes.auth
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(rf/reg-sub
 :auth/auth?
 (fn [db]
   (get-in db [:xhr :config :token])))

(rf/reg-event-fx
 ::signin-success
 [(rf/inject-cofx :storage/get [:redirect])]
 (fn [{storage :storage db :db} [_ {data :data}]]
   {:db (assoc-in db [:xhr :config :token] (:token data))
    :storage/set {:auth data}
    :zframes.redirect/redirect {:uri "/"}}))

(rf/reg-event-fx
 ::signin-error (fn [fx [_ resp]] (println "SIGNIN ERROR" resp)))

(rf/reg-event-fx
 ::authorize
 (fn [_ _]
   {:zframes.redirect/redirect {:uri "/login"}}))

(rf/reg-event-fx
 ::userinfo
 (fn [{db :db} [pid]]
   {:xhr/fetch [{:uri "/auth/userinfo"
                 :req-id ::userinfo
                 :success {:event ::userinfo-success}}]}))

(rf/reg-event-fx
 ::userinfo-success
 [(rf/inject-cofx :storage/get [:role])
  (rf/inject-cofx :window-location)]
 (fn [{{role :role} :storage location :location} [_ {user :data}]]
   (let [url-user-name (get-in location [:query-string :group])
         url-subject (get-in location [:query-string :subject])
         url-role (when url-user-name
                    (filter (comp #{url-user-name} :name) (:role user)))
         url-role (or (when url-subject
                        (->> url-role
                             (filter (comp (partial some (comp #{url-subject} :id second))
                                           :links))
                             first))
                      (first url-role))]
     (cond
       (and url-role role)
       (if (= url-role (:name role))
         {:dispatch [:role-redirect role]}
         {:dispatch [:role-redirect url-role]})
       url-role
       {:dispatch [:role-redirect url-role]}
       role
       {:dispatch [:role-redirect role]}
       :else
       (let [role  (-> user :role first)]
         {:dispatch [:role-redirect role]
          :storage/set {:role role}})))))


(rf/reg-sub
 ::userinfo
 :<- [:xhr/response ::userinfo]
 (fn [{data :data} _] data))


(rf/reg-event-fx
 ::logout
 (fn [{{config :config :as db} :db} _]
   {:storage/remove [:auth]
    :db (update-in db [:xhr :config] dissoc :token)}))

(rf/reg-event-fx
 ::logout-done
 (fn [{{config :config :as db} :db} _]
   (merge {:db (dissoc db :user)
           :storage/remove [:auth :role]
           :zframes.cookies/remove :asid}
          (if-let [mu (:manager-url config)]
            {:zframes.redirect/page-redirect {:uri mu}}
            {:zframes.redirect/page-redirect {:uri "/"}}))))
