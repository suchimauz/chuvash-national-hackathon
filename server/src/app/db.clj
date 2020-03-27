(ns app.db
  (:require [clj-pg.pool :as pool]
            (app.resources
             [categories :as categories])))

(def database-url "jdbc:postgresql://localhost:5432/marsell?user=panthevm&stringtype=unspecified")

(defonce ds (atom nil))

(defn connect []
  (if-let [p @ds]
    p
    (reset! ds
            {:datasource (pool/create-pool {:idle-timeout        10000
                                            :minimum-idle        1
                                            :maximum-pool-size   3
                                            :connection-init-sql "select 1"
                                            :data-source.url     database-url})})))
