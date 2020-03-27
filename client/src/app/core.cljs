(ns app.core
  (:require [reagent.dom    :as dom]
            [re-frame.core  :as rf]

            [frames.xhr]
            [frames.routing]

            [app.routes      :as routes]
            [app.pages.model :as pages]

            [app.pages.home.core]
            [app.pages.login.core]

            [app.components.navbar.core :as navbar]))

(rf/reg-event-fx
 ::initialize
 (fn []
   {:frames.routing/init routes/routes}))

(defn content [page]
  (if page
    [page]
    [:div "Страница не найдена"]))


(defn current-page []
  (let [route (rf/subscribe [:frames.routing/current])]
    (fn []
      (let [page (->> @route :match (get @pages/pages))]
        [:<>
         [navbar/component]
         [content page]]))))


(defn ^:export mount []
  (rf/dispatch-sync [::initialize])
  (dom/render [current-page] (js/document.getElementById "app")))
