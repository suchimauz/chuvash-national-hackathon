(ns app.postal
  (:require [postal.core :as postal]
            [honeysql.core :as hsql]
            [clj-pg.honey  :as pg]))

(defn send-msg [{resource :resource} body]
  (postal/send-message {:host "smtp.yandex.ru"
                        :ssl  true
                        :port 465
                        :user "richhickeyteam@yandex.ru"
                        :pass "richhickeyteam123"}
                       {:from    "richhickeyteam@yandex.ru"
                        :to      (:mail resource)
                        :subject "multipart/alternative test"
                        :body    [:alternative {:type "text/plain" :content "<b>Обновление регионального проекта</b>"}
                                  {:type    "text/html; charset=utf-8"
                                   :charset "utf-8"
                                   :content
                                   (str "<html><head></head><body> <h1>" (:name body)"</h1><p>Обновление регионального проекта</p> </body></html>")}]}))

(defn send-all [db {:keys [id] :as body}]
  (let [from (distinct (pg/query db {:select [:subscriber.resource]
                                     :from [:subscriber]
                                     :where [:= (str id) (hsql/raw "subscriber.resource#>>'{resource,id}'")]}))]
    (doseq [p from]
      (send-msg p body))))
