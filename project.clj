(defproject clojure-project "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [lib-noir"0.7.6"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler clojure-project.handler/app
         :init clojure-project.handler/init
         :destroy clojure-project.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
