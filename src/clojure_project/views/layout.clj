(ns clojure-project.views.layout
  (:require [hiccup.page :refer [html5 include-css]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to clojure-project"]
     (include-css "/css/bootstrap.min.css")
     (include-css "/css/screen.css")]
    [:body body]))

(defn nav-bar []
  (html5 [:nav {:class "navbar navbar-default navbar-fixed-top"}
   [:div {:class "container"}
   [:div {:class "navbar-header"}
   ]
    [:div {:id "navbar" :class "collapse navbar-collapse"}
    [:ul {:class "nav navbar-nav"}
     [:li [:a {:href "/"} "Recommended books"]]
     [:li [:a {:href "/your-books"} "Your books"]]
     [:li [:a {:href "/logout"} "Logout"]]]]]]))
