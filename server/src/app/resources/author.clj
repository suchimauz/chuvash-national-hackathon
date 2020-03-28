(ns app.resources.author
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :Author
   {:type       "object"
    :properties {:name     {:type  :array
                            :items {:type       :object
                                    :properties {:family {:type :string}
                                                 :given  {:type  :array
                                                          :items {:type :string}}}}}
                 :position {:type :string}
                 :photo    {:type :string}}}))
