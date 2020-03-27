(ns app.routes)

(def routes
  {:.         :app.pages.home.model/index
   "login"    {:. :app.pages.login.model/index}
   "home"     {:. :app.pages.home.model/index}
   "project"  {:.       :app.pages.project.model/index
               "create" {:. :app.pages.project.crud.model/create}
               [:id]    {:. :app.pages.project.model/show
                         "edit" {:. :app.pages.project.crud.model/edit}}}})
