(ns app.actions
  (:require [clj-pg.honey  :as pg]
            [honeysql.core :as hsql]))

(defn ok          [response] {:status 200 :body response})
(defn created     [response] {:status 201 :body response})
(defn bad-request [response] {:status 404 :body response})

(defn validation [body {validator :validator}]
  (let [result (validator body)]
    (:errors result)))

(defn -get [table {:keys [params] db :db/connection}]
  (let [query {:select [:resource]
               :from   [(:table table)]
               :limit  (or (:count params) 100)}]
    (ok (pg/query db query))))

(defn -post [table {:keys [body] db :db/connection}]
  (let [result (validation body table)]
    (if (empty? (:errors result))
      (let [insert   (pg/create db table {:resource body})
            response (pg/update db table
                                (update insert :resource assoc
                                        :resourceType  (-> table :table name)
                                        :id            (:id insert)))]
        (created response))
      (bad-request (:errors result)))))

(defn -delete [table {:keys [params] db :db/connection}]
  (let [response (pg/delete db table (:id params))]
    (ok response)))
