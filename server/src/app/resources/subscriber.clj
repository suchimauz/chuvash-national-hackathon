(ns app.resources.subscriber
  (:require [app.utils :as u]))

(def table
  (u/new-table
   :Subscriber
   {:type       "object"
    :properties {:mail     {:type :string}
                 :resource {:type       :object
                            :properties {:id           {:tyoe :integer}
                                         :resourceType {:type :string}}}}}))
