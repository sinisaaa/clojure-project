(ns clojure-project.models.db
  (:require [clojure.java.jdbc :as sql]
            [hiccup.page :as hic-p])
   (:import java.sql.DriverManager))

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/clojure_recommendations"
         :user "root"
         :password "root"})

(defn list-books []
(let [results (sql/query db
      ["select * from bx_books ORDER BY ISBN DESC LIMIT 100"])]
        results))

(defn list-books-by-user [id]
(let [results (sql/query db
      ["select * from bx_books INNER JOIN bx_book_ratings ON bx_books.ISBN=bx_book_ratings.ISBN Where User_ID=?" id])]
    results))

(defn get-user [id]
  (let [res (sql/query db ["select * from bx_users where Username = ?" id])] (first res)))

(defn get-book [isbn]
  (let [res (sql/query db ["select * from bx_books where ISBN = ?" isbn])] (first res)))

(defn get-book-rating [isbn id]
    (let [res (sql/query db ["select * from bx_book_ratings where ISBN = ? and User_ID = ?" isbn id])] (first res)))

(defn add-rating [isbn rating id]
   (sql/insert! db :bx_book_ratings {:user_id id :isbn isbn :book_rating rating}))
