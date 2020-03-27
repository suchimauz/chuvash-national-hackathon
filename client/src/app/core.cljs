(ns app.core
  (:require [reagent.dom    :as dom]
            [re-frame.core  :as rf]

            [zframes.debounce]
            [zframes.routing]
            [zframes.breadcrumb]
            [zframes.redirect]
            [zframes.cookies]
            [zframes.xhr]
            [zframes.file]
            [zframes.storage]
            [zframes.auth :as auth]
            [zframes.window-location]

            [app.routes      :as routes]
            [app.pages.model :as pages]
            [app.styles      :as style]

            [app.pages.home.core]
            [app.pages.login.core]
            [app.pages.project.crud.core]

            [app.components.navbar.core :as navbar]))

(def config
  {:base-url     "http://localhost:8080"
   :redirect_uri "http://localhost:3000"
   :storage-url  "http://localhost:8990"})

(rf/reg-event-fx
 ::initialize
 [(rf/inject-cofx :storage/get [:auth])
  (rf/inject-cofx :window-location)]
 (fn [{storage :storage location :location db :db} _]
   (let [auth   (:auth storage)
         db     (-> db
                    (merge  {:config config
                             :route-map/routes app.routes/routes})
                    (assoc-in [:xhr :config] config)
                    (assoc :theme (:theme storage)))]
     (if auth
       {:db (assoc-in db [:xhr :config :token] (:access_token auth))
        :dispatch [::auth/userinfo]
        :xhr/fetch auth/appinfo-xhr
        :route-map/start {}}
       (if-let [code (get-in location [:query-string :code])]
         {:db db
          :route-map/start {}
          ;:dispatch [::auth/get-token code config]
          }
         {:db db
          :route-map/start {}
          ;:dispatch [::auth/authorize config]
          })))))
(defn content [page params]
  [:div.bg-white.main-content
   (if page
     [page params]
     [:div "Страница не найдена"])])


(defn current-page []
  (let [route  (rf/subscribe [:route-map/current-route])]
    (fn []
      (let [page (get @pages/pages (:match @route))
            params (:params @route)]
        [:<>
         [:style style/style]
         [navbar/component]
         [content page params]]))))

(defn ^:export mount []
  (rf/dispatch-sync [::initialize])
  (dom/render [current-page] (js/document.getElementById "app")))
