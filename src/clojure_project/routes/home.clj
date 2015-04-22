(ns clojure-project.routes.home
  (:require [compojure.core :refer :all]
            [clojure-project.views.layout :as layout]
            [clojure-project.models.db :as db]
            [hiccup.page :as page]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]
            [noir.session :as session]))



(defn title-lenght [title]
  (if (> (count title) 40)
    40
    (count title)))


(defn show-books []


  [:div {:class "row"}
    (for [{:keys [isbn book-title image-url-m]} (db/list-books-by-user 99)]
      [:div {:class "col-xs-6 col-md-3"}
      [:a {:class "thumbnail"}
      [:img  {:src image-url-m :onerror="this.src='/img/Default.png'"}]
      [:p {:class "book-title"} (subs book-title 0 (title-lenght book-title))]]])])


(defn home []

  (layout/common
    (layout/nav-bar)

   [:br][:br][:br]

   [:h1 "Books rated by you:"]
      (show-books)))

(defn process-login [username password]

  (let [user (db/get-user username)]
    (if (and (= username (:username user)) (= password (:password user)))
        (do (session/put! :user username) (redirect "/"))
        (redirect "/login"))))

(defn login []
   (layout/common
   (form-to [:post "/login"]
           [:div {:class "form-group"}
           [:label {:for "username"} "Username:"]
           [:input {:type "text" :class "form-control" :id "username" :name "username"}]
           [:label {:for "pass"} "Password:"]
           [:input {:type "password" :class "form-control" :id "pass" :name "pass"}]]
           [:input {:type "submit" :value "Login"}])))


(defroutes home-routes
   (GET "/" []    (if (session/get :user) (home) (redirect "/login")))
   (GET "/login" [] (login))
   (POST "/login" [username pass]
        (process-login username pass))
   (GET "/logout" [] (session/clear!) (redirect "/login")))


