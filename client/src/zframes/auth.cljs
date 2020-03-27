(ns zframes.auth
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(def appinfo-xhr
  {:uri "/AppInfo/get"
   :req-id :app/info
   :success {:event ::update-check}})

(rf/reg-event-fx
 ::update-check
 [(rf/inject-cofx :storage/get [:info])]
 (fn [{{:keys [info]} :storage db :db} [_ {data :data}]]
   (when
       (and (or (not (get-in info [:nextUpdate :showedModal?]))
                (not (= (:version info) (:version data))))
            (get-in data [:nextUpdate :showModal]))
     {:dispatch [:modal {:title "Внимание!"
                         :style {:min-width "600px"}
                         :body [:div
                                [:div "Сегодня "
                                 [:b (helpers/date-iso->rus-format
                                      (get-in data [:nextUpdate :date]))]
                                 " в "
                                 [:b (helpers/time*
                                      (get-in data [:nextUpdate :date]))]
                                 " запланировано обновление "
                                 [:b "РМИС 2.0"]
                                 " в связи с чем просим вас сохранить ваши изменения за 5 минут до обновления, чтобы их не потерять."]
                                [:br]
                                [:div
                                 "Приносим свои извинения за доставленные неудобства!"]]
                         :cancel {:text "Закрыть"}
                         :persistent true}]
      :storage/set {:info (assoc-in data [:nextUpdate :showedModal?] true)}})))

(rf/reg-event-fx
 ::get-token
 (fn [fx [_ code {id :client_id :as config}]]
   {:json/fetch {:uri "/auth/token"
                 :method "post"
                 :body {:client_id  id
                        :grant_type "authorization_code"
                        :code code}
                 :success {:event ::signin-success}
                 :error   {:event ::signin-error}}}))

(rf/reg-event-fx
 ::signin-success
 [(rf/inject-cofx :storage/get [:redirect])]
 (fn [{storage :storage db :db} [_ {data :data}]]
   {:dispatch [::userinfo]
    :db (assoc-in db [:xhr :config :token] (:access_token data))
    :xhr/fetch appinfo-xhr
    :zframes.redirect/redirect {:uri (get-in storage [:redirect :uri] "/")}
    :zframes.redirect/set-query-string {:title "Alkona"
                                        :group (get-in storage [:redirect :query :group])
                                        :subject (get-in storage [:redirect :query :subject])}
    :storage/set {:auth data}
    :storage/remove [:redirect]}))

(rf/reg-event-fx
 ::signin-error (fn [fx [_ resp]] (println "SIGNIN ERROR" resp)))

(rf/reg-event-fx
 ::authorize
 [(rf/inject-cofx :window-location)]
 (fn [{location :location} [_ config]]
   {:zframes.redirect/redirect {:uri "/login"
                                :params (let [hash (:hash location)
                                              group (get-in location [:query-string :group])
                                              subject (get-in location [:query-string :subject])]
                                          (when-not (or (str/blank? hash) (str/blank? group))
                                            {:redirect_uri hash
                                             :redirect_query_group group
                                             :redirect_query_subject subject}))}}))


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
   {:json/fetch {:uri "/Session"
                 :method  :delete
                 :success {:event ::logout-done}
                 :error   {:event ::logout-done}}}))

(rf/reg-event-fx
 ::logout-done
 (fn [{{config :config :as db} :db} _]
   (merge {:db (dissoc db :user)
           :storage/remove [:auth :role]
           :zframes.cookies/remove :asid}
          (if-let [mu (:manager-url config)]
            {:zframes.redirect/page-redirect {:uri mu}}
            {:zframes.redirect/page-redirect {:uri "/"}}))))
