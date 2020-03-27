(ns app.migration
  (:require [clj-pg.honey  :as pg]
            [app.db        :as db]
            (app.resources
             [categories :as categories])))

(defn migrate [db table]
  (when-not (pg/table-exists? db (:table table))
    (pg/create-table db table)))

(defn migration [db]
  (migrate db categories/table))

(comment
  (pg/drop-table (db/connect) categories/table))
