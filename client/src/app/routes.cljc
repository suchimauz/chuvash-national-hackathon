(ns app.routes)

(def routes
  {:.         :app.pages.home.model/index
   "login"    {:. :app.pages.login.model/index}
   "home"     {:. :app.pages.home.model/index}
   "project"  {:.       :app.pages.project.model/index
               "create" {:. :app.pages.project.crud.model/create}
               [:id]    {:. :app.pages.project.model/show
                         "edit" {:. :app.pages.project.crud.model/edit}}}
   "purpose"  {:.       :app.pages.purpose.model/index
               "create" {:. :app.pages.purpose.crud.model/create}
               [:id]    {:. :app.pages.purpose.model/show
                         "edit" {:. :app.pages.purpose.crud.model/edit}}}
   "event"     {:.       :app.pages.event.model/index
                "create" {:. :app.pages.event.crud.model/create}
                [:id]    {:. :app.pages.event.model/show
                          "edit" {:. :app.pages.event.crud.model/edit}}}})
