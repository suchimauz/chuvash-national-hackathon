(ns app.report
  (:require [clj-pg.honey  :as pg]
            [honeysql.core :as hsql]
            [app.utils :as utils]))

(defmulti report (fn [{{:keys [id]} :path-params}]
                   (keyword id)))

(defmethod report :default
  [_]
  {:status 404
   :body {:error {:message "Report not found"}}})

(defmethod report :national-project-payment-sum
  [{{:keys [id]} :params db :db/connection}]
  (if id
    {:status 200
     :body
     (pg/query-first
      db
      [(str "
select SUM((reg.resource#>>'{payment,federal}')::decimal) federal,
SUM((reg.resource#>>'{payment,regional}')::decimal) regional,
SUM((reg.resource#>>'{payment,municipal}')::decimal) municipal,
SUM((reg.resource#>>'{payment,other}')::decimal) other
from project nat
join project reg
on reg.resource#>>'{project,id}' = nat.id::text
where nat.id::text = '" id "'
and nat.resource->>'category' = 'national'")])}
    {:status 500
     :body {:error {:message "Does not find project id"}}}))
