(ns app.migration
  (:require [clj-pg.honey  :as pg]
            [honeysql.core :as hsql]
            [honeysql.types :as hsql-types]
            [cheshire.core :as json]
            [app.db        :as db]
            (app.resources
             [purpose :as purpose]
             [user :as user]
             [project :as project]
             [author :as author])))

(defn migrate [db table]
  (when-not (pg/table-exists? db (:table table))
    (pg/create-table db table)))

(defn migration [db]
  (when-not
      (:resource
       (pg/query-first db
        {:select [:*]
         :from [:user]
         :where ["@>"
                 :resource
                 (json/generate-string
                  {:email "admin@admin.admin"})]}))
    (pg/execute db "insert into \"user\" (resource) values (jsonb_build_object('email', 'admin@admin.admin', 'password', 'bcrypt+sha512$94630e02733d4aa27ea3804ac1cf232a$12$9465947ca3cba9f60e4652bf2a2bf1b343be798dd89d2440'))"))

  (migrate db purpose/table)
  (migrate db user/table)
  (migrate db project/table)
  (migrate db author/table))

(comment
  (pg/drop-table (db/connect) user/table))
