(ns app.routes)

(def ^:const routes
  {:-      :app.pages.login.model/index
   "login" {:-    :app.pages.login.model/index
            [:id] {:- :app.pages.login.model/index}}
   "home"  {:- :app.pages.home.model/index}})
