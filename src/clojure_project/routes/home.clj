(ns clojure-project.routes.home
  (:require [compojure.core :refer :all]
            [clojure-project.views.layout :as layout]
            [clojure-project.models.db :as db]
            [hiccup.page :as page]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]
            [noir.session :as session]))


(defn book-details [id]
  (layout/common
    (layout/nav-bar)
       [:br][:br]
       (book-details-content id)))


(defn home []
  (layout/common
    (layout/nav-bar)
      [:br][:br][:br]
      (show-books)))

(defn process-login [username password]
  (let [user (db/get-user username)]
    (if (and (= username (:username user)) (= password (:password user)))
        (do (session/put! :id (:user_id user)) (redirect "/"))
        (redirect "/login"))))

(defn add-rating [isbn rating]
   (db/add-rating isbn rating (session/get :id))
      (redirect "/"))

(defn title-lenght [title]
  (if (> (count title) 40)
    40
    (count title)))

(defn show-books []
  [:div {:class "row"}
    (for [{:keys [isbn book-title image-url-m]} (db/list-books-by-user (session/get :id))]
      [:div {:class "col-xs-6 col-md-3"}
      [:a {:class "thumbnail" :href (str "/book/" isbn) }
      [:img  {:src image-url-m :onerror="this.src='/img/Default.png'"}]
      [:p {:class "book-title"} (subs book-title 0 (title-lenght book-title))]]])])

(defn book-details-content [isbn]
  (let [book (db/get-book isbn)]
    (let [rating (db/get-book-rating isbn (session/get :id))]
    [:div {:class "panel panel-default"}
      [:div {:class "panel-body"}
        [:br]
        [:img  {:src (:image-url-l book) :style " display: block;margin-left: auto;margin-right: auto;"}]
         [:br][:br]
         [:div {:align "center" :style "font-size:medium;"}
         [:div [:b "ISBN:   "] [:i (:isbn book)]]
         [:div [:b "Title:   "] [:i(:book-title book)]]
         [:div [:b "Author:   "] [:i(:book-author book)]]
         [:div [:b "Year:   "] [:i(:year-of-publication book)]]
         [:div [:b "Publisher:   "] [:i(:publisher book)]]

          (if-not (:book_rating rating)
             [:div {:id "test"}
             [:form {:method "POST" :action "/add-rating" :id "form"}
             [:input {:class "rating" :data-max "10" :data-min "1" :name "rate" :type"number" :data-empty-value "1"}]
             [:input {:type "hidden" :name "isbn" :value (:isbn book)}]]])
          ]]])))

(defn login[]
  (layout/common
   [:div {:class "container"}
    [:div {:class "row"}
        [:div {:class "col-sm-6 col-md-4 col-md-offset-4"}
            [:div {:class "account-wall" }
                [:form {:class "form-signin" :action "/login" :method "POST"}
                 [:img  {:src "/img/book.jpg" :style " display: block;margin-left: auto;margin-right: auto;"}]
                [:input {:type "text" :class "form-control" :name "username" :id "username" :placeholder "Username"}]
                [:input {:type "password" :class "form-control" :name "pass" :id "pass" :placeholder "Password"}]
                [:br]
                [:button {:class "btn btn-lg btn-primary btn-block" :type "submit"} "Sign in"]]]
                ]]]))


(defroutes home-routes
   (GET "/" []
        (if (session/get :id) (home) (redirect "/login")))

   (GET "/your-books" []
        (if (session/get :id) (home) (redirect "/login")))

   (GET "/login" [] (login))

   (POST "/login" [username pass]
        (process-login username pass))

   (GET "/logout" []
        (session/clear!) (redirect "/login"))

   (GET "/book/:id" [id]
        (if (session/get :id) (book-details id) (redirect "/login")))

   (POST "/add-rating" [isbn rate]
         (add-rating isbn rate)))


