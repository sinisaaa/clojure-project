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


(defn get-most-read-author [id]
    (let [res (sql/query db ["SELECT Book_Author FROM bx_book_ratings INNER JOIN bx_books
                             ON bx_book_ratings.ISBN=bx_books.ISBN WHERE User_ID=? group by Book_Author" id])] (first res)))

(defn get-average-rating [isbn]
  (let [res (sql/query db ["select Book_Rating from bx_book_ratings where ISBN = ? " (:isbn isbn)])]
    (assoc {} :isbn (:isbn isbn) :book_title (:book_title isbn) :image_url_m (:image_url_m isbn) :avg
     (if (> (count res) 0)
       (float
         (/ (:book_rating (apply merge-with + res)) (count res)))))))

(defn get-books-by-author [id]
    (let [author (get-most-read-author id)]
    (let [res (sql/query db ["select ISBN, Book_Title, Image_URL_M from bx_books where Book_Author = ? and ISBN NOT IN (Select isbn from bx_book_ratings where User_ID= ?)" (:book_author author) id])]
      (sort-by :avg > (map get-average-rating res)))))

