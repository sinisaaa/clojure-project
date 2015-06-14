(ns clojure-project.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn common [& body]
  (html5
    [:head
     [:title "Clojure project"]
     [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"}]
     (include-css "/css/bootstrap.min.css")
     (include-css "/css/screen.css")
     (include-js "/js/bootstrap-rating-input.js")
     (include-js "/js/bootstrap.min.js")
     (include-js "/js/custom.js")]
    [:body body]))

(defn nav-bar []
  (html5 [:nav {:class "navbar navbar-default navbar-fixed-top"}
   [:div {:class "container"}
   [:div {:class "navbar-header"}
   ]
    [:div {:id "navbar" :class "collapse navbar-collapse"}
    [:ul {:class "nav navbar-nav"}
     [:li [:a {:href "/"} "Recommended books"]]
     [:li [:a {:href "/recommend-by-author"} "Favorite author best books"]]
     [:li [:a {:href "/your-books"} "Books rated by you"]]
     [:li [:a {:href "/logout"} "Logout"]]]]]]))
