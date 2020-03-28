(ns app.pages.project.model
  (:require [re-frame.core :as rf]
            [app.helpers :as helpers]
            [clojure.string :as str]))

(def show-page ::show)

(rf/reg-event-fx
 ::index
 (fn [_ _]
   {:zframes.redirect/redirect {:uri "/home"}}))

(rf/reg-sub
 ::breadcrumb
 :<- [:xhr/response show-page]
 (fn [{{:keys [id name]} :data}]
   name))

(rf/reg-event-fx
 show-page
 (fn [{db :db} [pid phase {:keys [id]}]]
   (case phase
     :init
     {:xhr/fetch [{:uri (str "/Project/" id)
                   :req-id pid}
                  {:uri "/Project"
                   :params {:.project.id id
                            :assoc "author"}
                   :req-id ::show-regional}
                  {:uri "/report/national-project-payment-sum"
                   :params {:id id}
                   :req-id ::payments}]}
     :deinit
     {:db (dissoc db pid)}
     nil)))

(defn payment-name-mapping [{:keys [federal regional municipal other]}]
  (cond-> {}
    federal   (assoc "Федеральный" {:total (helpers/parse-float federal) :color "red"})
    regional  (assoc "Региональный" {:total (helpers/parse-float regional) :color "blue"})
    municipal (assoc "Муниципальный" {:total (helpers/parse-float municipal) :color "purple"})
    other     (assoc "Внебюджет" {:total (helpers/parse-float other) :color "#fba000"})))

(rf/reg-sub
 show-page
 :<- [:xhr/response show-page]
 :<- [:xhr/response ::show-regional]
 :<- [:xhr/response ::payments]
 (fn [[{national :data} {regionals :data} {payments :data}] _]
   {:national national
    :payments (payment-name-mapping payments)
    :regionals (map
                (fn [r]
                  (assoc r :payment (payment-name-mapping (:payment r))))
                regionals)}))

(def show-regional ::regional)

(rf/reg-event-fx
 show-regional
 (fn [_ [_ phase {:keys [reg-id]}]]
   (case phase
     :init {:xhr/fetch [{:uri    (str "/Project/" reg-id)
                         :params {:assoc "project"}
                         :req-id :regional}
                        {:uri    (str "/Purpose")
                         :params {:.project.id reg-id}
                         :req-id :purpose}
                        {:uri    (str "/Event")
                         :params {:.project.id reg-id}
                         :req-id :event}]}
     nil)))

(defn event-item-map [{:keys [name id period amount task]}]
  {:id id
   :name name
   :date (helpers/date-iso->rus-format (:end period))})

(rf/reg-sub
 show-regional
 :<- [:xhr/response :regional]
 :<- [:xhr/response :purpose]
 :<- [:xhr/response :event]
 (fn [[{project :data} {purposes :data} {events :data}] _]
   {:project  project
    :purposes purposes
    :events   (map
               event-item-map
               events)}))
