(ns app.actions
  (:require [clj-pg.honey  :as pg]
            [json-schema.core :as schema]
            [honeysql.core :as hsql]
            [app.utils :as utils]))

(defn ok          [response] {:status 200 :body response})
(defn created     [response] {:status 201 :body response})
(defn bad-request [response] {:status 422 :body {:errors {:message "Unprocessable Entity"
                                                          :detail response}}})
(defn res-not-found [id] {:status 404 :body {:errors {:message (str "Resource id = " id " not found")}}})

(defn validation [body {validator :validator}]
  (schema/validate validator body))

(defn -get [table {:keys [params] db :db/connection {:keys [id]} :path-params}]
  (let [query (merge
               {:select [(keyword (str (name (:table table)) ".*"))]
                :from   [(:table table)]
                :limit  (or (:count params) 100)}
               (utils/where-params table params))]
    (if id
      (let [res (pg/query-first db (assoc query :where [:= :id id]))]
        (if-not res
          (res-not-found id)
          (ok (first (utils/assoc-params db params [(utils/row-to-resource res)])))))
      (ok (->> query
               (pg/query db)
               (map utils/row-to-resource)
               (utils/assoc-params db params))))))

(defn -post [table {:keys [body] db :db/connection}]
  (let [result (validation body table)]
    (if (empty? (:errors result))
      (created (utils/row-to-resource (pg/create db table {:resource (dissoc body :id :resourceType)})))
      (bad-request (:errors result)))))

(defn -put [table {:keys [body] db :db/connection {:keys [id]} :path-params}]
  (let [result (validation body table)]
    (if (empty? (:errors result))
      (ok (utils/row-to-resource (pg/update db table {:id id :resource (dissoc body :id :resourceType)})))
      (bad-request (:errors result)))))

(defn -delete [table {:keys [params] db :db/connection {:keys [id]} :path-params}]
  (let [response (pg/delete db table id)]
    (ok {:id (:id response)})))
