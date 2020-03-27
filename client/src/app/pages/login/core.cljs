(ns app.pages.login.core
  (:require [app.pages.model       :as page]
            [app.pages.login.model :as model]))

(page/reg-subs-page
 model/index-page
 (fn [page fragment]
   (prn page fragment)
   [:div
    [:h1 "Login"]]))
