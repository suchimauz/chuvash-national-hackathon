(ns app.routes)

(def ^:const routes
  {:.         :app.pages.login.model/index
   "login"    {:. :app.pages.login.model/index}
   "register" {:. :app.pages.login.model/register}
   "home"     {:. :app.pages.home.model/index}})
