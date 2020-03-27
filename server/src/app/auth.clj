(ns app.auth
  (:require [clj-pg.honey :as pg]
            [buddy.auth.backends.token :as token]
            [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]
            [cheshire.core :as json]
            [honeysql.core :as hsql])
  (:gen-class))

(def pkey "secret")

(def auth-backend
  (token/jws-backend {:secret pkey
                      :options {:alg :hs512}}))

(defn authorize
  [{{:keys [email password] :as body} :body db :db/connection :as req}]
  (clojure.pprint/pprint req)
  (let [user (:resource
              (pg/query-first db
               {:select [:*]
                :from [:user]
                :where ["@>"
                        :resource
                        (json/generate-string {:email email})]}))]
    (if (hashers/check password (:password user))
      {:status 200
       :body {:token (jwt/sign (dissoc user :password) pkey)}}
      (if user
        {:status 500
         :body {:error {:message "Invalid email or password"}}}
        {:status 404
         :body {:error {:message "User not found"}}}))))
