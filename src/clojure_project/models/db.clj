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


;; Recommendation by euclidean-distance

;;Compare to users with at least one common book
(defn base-similar-users [id]
(let [results (sql/query db
      ["SELECT * FROM bx_book_ratings WHERE User_ID IN (select DISTINCT User_ID from bx_books INNER JOIN bx_book_ratings ON bx_books.ISBN=bx_book_ratings.ISBN WHERE bx_book_ratings.ISBN IN (SELECT bx_book_ratings.ISBN FROM bx_book_ratings WHERE User_ID=?))" id])]
   (group-by :user_id results)))

;;Compare to all users
(defn list-users []
(let [results (sql/query db
      ["select * from bx_users inner join bx_book_ratings on bx_book_ratings.User_ID=bx_users.User_ID"])]
        (group-by :user_id results)))

(defn change-to-map [users]
  (reduce
   (fn [m v]
     (assoc m (:isbn v) (:book_rating v))) {} users))


(defn euclidean-distance [person1 person2]
  (let [person1 (change-to-map person1)]
  (let [person2 (change-to-map person2)]
  (let [same-items (filter person1 (keys person2))]
    (if (= 0 (count same-items))
     0
     (let [result (/ 1.0 (inc (reduce (fn [acc v]
                         (let [score1 (person1 v)
                               score2 (person2 v)]
                          (+ acc (Math/pow (- score1 score2) 2))))
                                      0 same-items)))]
       result))))))


(defn top-matches [similarity prefs person]
   (sort-by second >
            (map (fn [[k v]]
                [k (similarity (prefs person) (prefs k))])
     (dissoc prefs person))))
