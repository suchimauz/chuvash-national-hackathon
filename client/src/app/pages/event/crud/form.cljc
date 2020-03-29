(ns app.pages.event.crud.form
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]
            [app.form.events :as ze]
            [zenform.validators        :as validators]
            [app.helpers :as h]))

(defmethod validators/validate
  ::float
  [{msg :message} v]
  (when (and v (not (re-matches #"^[+-]?([0-9]+([,][0-9]*)?|[,][0-9]+)$" (str v))))
    msg))

(defmethod validators/validate
  ::integer
  [{msg :message} v]
  (when (and v (not (re-matches #"^\d+$" (str v))))
    msg))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name        {:type :string
                          :validators {:required {:message "Укажите название"}}
                          }
            :id          {:type :string}
            :project     {:type :object}
            :description {:type :string}
            :payment     {:type   :form
                          :fields {:regional  {:type :string
                                               :validators {::float {:message "Неправильный формат"}}
                                               }
                                   :municipal {:type :string
                                               :validators {::float {:message "Неправильный формат"}}
                                               }
                                   :federal   {:type :string
                                               :validators {::float {:message "Неправильный формат"}}
                                               }
                                   :other     {:type :string
                                               :validators {::float {:message "Неправильный формат"}}
                                               }}}
            :task        {:type :form
                          :fields {:unit {:type :string}
                                   :target {:type :string
                                            :validators {::integer {:message "Неправильный формат"}}
                                            }
                                   :complete {:type :string
                                              :validators {::integer {:message "Неправильный формат"}}}}}
            :period      {:type   :form
                          :fields {:start {:type :string}
                                   :end   {:type :string}}}}})

(def object-path [:form ::object])
(def object
  {:type   :form
   :fields {:name    {:type :string
                      :validators {:required {:message "Укажите название"}}}
            :address {:type   :form
                      :fields {:district   {:type :string
                                            :items h/districts}
                               :city       {:type :string}
                               :street     {:type :string}
                               :house      {:type :string}
                               :appartment {:type :string}}}
            :event   {:type :object}}})

(rf/reg-event-fx
 ::init
 (fn [{db :db} [_ {:keys [data]}]]
   {:dispatch [:zf/init path schema data]}))

(rf/reg-event-fx
 ::object-init
 (fn [{db :db} [_ {:keys [data]}]]
   {:dispatch [:zf/init object-path object data]}))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db
                                        (get-in path)
                                        zf/eval-form)]
    (merge
     {:db (assoc-in db path form)}
     (if (empty? errors)
       (cb value)
       #?(:clj (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))

(defn eval-object [db cb]
  (let [{:keys [errors value form]} (-> db
                                        (get-in object-path)
                                        zf/eval-form )]
    (merge
     {:db (assoc-in db object-path form)}
     (if (empty? errors)
       (cb value)
       #?(:clj  (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))
