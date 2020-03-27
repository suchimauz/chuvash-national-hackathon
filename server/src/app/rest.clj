(ns app.rest
  (:require [immutant.web :as web]
            (ring.middleware
             [cors :refer [wrap-cors]]
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]
             [json :refer [wrap-json-body wrap-json-response]])
            [app.migration  :as migration]
            [app.db         :as db]
            [app.middleware :as middleware]
            [app.handler    :as handler]))


(defn -main []
  (let [db    (db/connect)
        stack (-> #'handler/handler
                  (wrap-keyword-params)
                  (wrap-params)
                  (wrap-json-body {:keywords? true})
                  (wrap-json-response)
                  (middleware/wrap-cors)
                  (middleware/add-db db))]
    (migration/migration db)
    (web/run stack)))
