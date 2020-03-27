(ns app.rest
  (:require [immutant.web :as web]
            [ring.middleware.json :as json]
            [app.migration  :as migration]
            [app.db         :as db]
            [app.middleware :as middleware]
            [app.handler    :as handler]))


(defn -main []
  (let [db    (db/connect)
        stack (-> #'handler/handler
                  (middleware/add-db db)
                  (json/wrap-json-body {:keywords? true})
                  json/wrap-json-response
                  middleware/wrap-cors)]
    (migration/migration db)
    (web/run stack)))
