(ns clojure-project.routes.home
  (:require [compojure.core :refer :all]
            [clojure-project.views.layout :as layout]
            [clojure-project.models.db :as db]
            [hiccup.page :as page]))

(defn title-lenght [title]
  (if (> (count title) 40) 40 (count title)))


(defn show-books []

  [:div {:class "row"}

    (for [{:keys [isbn book-title image-url-m]} (db/list-books-by-user 99)]
      [:div {:class "col-xs-6 col-md-3"}
      [:a {:class "thumbnail"}
      [:img  {:src image-url-m}]
      [:p {:class "book-title"} (subs book-title 0 (title-lenght book-title))]]])])


(defn home []
  (layout/common
   (page/include-css "/css/bootstrap.min.css")
   [:h1 "Books rated by you:"]
                 (show-books)))


(defroutes home-routes
  (GET "/" [] (home)))
