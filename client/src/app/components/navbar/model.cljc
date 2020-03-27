(ns app.components.navbar.model
  (:require [re-frame.core :as rf]))

(def ^:const links
  [{:title "Home"  :href "#/home"}
   {:title "Login" :href "#/login"}])

(rf/reg-sub
 ::data
 (fn []
   {:nav links}))
