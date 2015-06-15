(ns clojure-project.models.db
  (:require [clojure.java.jdbc :as sql]
            [hiccup.page :as hic-p])
   (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "clojure_recommendations.db3"})

(defn list-books
"Return list of all books grouped by ISBN"
[]
(let [results (sql/query db
      ["select bx_books.ISBN, bx_book_ratings.Book_Rating, bx_book_ratings.User_ID from bx_books inner join bx_book_ratings on bx_book_ratings.ISBN=bx_books.ISBN ORDER BY bx_books.ISBN DESC"])]
        (group-by :isbn results)))


(defn list-books-by-user
"Return list of books rated by user"
[id]
(let [results (sql/query db
      ["select * from bx_books INNER JOIN bx_book_ratings ON bx_books.ISBN=bx_book_ratings.ISBN Where User_ID=?" id])]
    results))

(defn get-user
"Return user data from database by User ID"
[id]
  (let [res (sql/query db ["select * from bx_users where Username = ?" id])] (first res)))

(defn get-book
"Return book data from database by ISBN"
[isbn]
  (let [res (sql/query db ["select * from bx_books where ISBN = ?" isbn])] (first res)))

(defn get-book-rating
"Return book rating rated by user"
[isbn id]
    (let [res (sql/query db ["select * from bx_book_ratings where ISBN = ? and User_ID = ?" isbn id])] (first res)))

(defn add-rating
"Add new rating into database"
[isbn rating id]
   (sql/insert! db :bx_book_ratings {:user_id id :isbn isbn :book_rating rating}))


(defn get-most-read-author
"Return name of most rated author by user"
[id]
    (let [res (sql/query db ["SELECT Book_Author FROM bx_book_ratings INNER JOIN bx_books
                             ON bx_book_ratings.ISBN=bx_books.ISBN WHERE User_ID=? group by Book_Author" id])] (first res)))

(defn get-average-rating
"Return average rating for book"
  [isbn]
  (let [res (sql/query db ["select Book_Rating from bx_book_ratings where ISBN = ? " (:isbn isbn)])]
    (assoc {} :isbn (:isbn isbn) :book_title (:book_title isbn) :image_url_m (:image_url_m isbn) :avg
     (if (> (count res) 0)
       (float
         (/ (:book_rating (apply merge-with + res)) (count res)))))))

(defn get-books-by-author
  "Return all books by author without books already rated by user"
  [id]
    (let [author (get-most-read-author id)]
    (let [res (sql/query db ["select ISBN, Book_Title, Image_URL_M from bx_books where Book_Author = ? and ISBN NOT IN (Select isbn from bx_book_ratings where User_ID= ?)" (:book_author author) id])]
      (sort-by :avg > (map get-average-rating res)))))


;; Recommendation by euclidean-distance

;;Compare to users with at least one common book
(defn base-similar-users
"Return seq of users and book ratings with at least one common rated book"
[id]
(let [results (sql/query db
      ["SELECT Book_Rating, User_ID FROM bx_book_ratings WHERE User_ID IN (select DISTINCT User_ID from bx_books INNER JOIN bx_book_ratings ON bx_books.ISBN=bx_book_ratings.ISBN WHERE bx_book_ratings.ISBN IN (SELECT bx_book_ratings.ISBN FROM bx_book_ratings WHERE User_ID=?)) and ISBN not in(select ISBN from bx_book_ratings where User_ID=?)" id id])]
   results))

(defn base-similar-users2 [id]
"Return seq of users and book ratings with at least one common rated book"
(let [results (sql/query db
      ["SELECT * FROM bx_book_ratings WHERE User_ID IN (select DISTINCT User_ID from bx_books INNER JOIN bx_book_ratings ON bx_books.ISBN=bx_book_ratings.ISBN WHERE bx_book_ratings.ISBN IN (SELECT bx_book_ratings.ISBN FROM bx_book_ratings WHERE User_ID=?))" id])]
   (group-by :user_id results)))


;;Compare to all users
(defn list-users
"Return seq of all users grouped by id"
[]
(let [results (sql/query db
      ["select * from bx_users inner join bx_book_ratings on bx_book_ratings.User_ID=bx_users.User_ID"])]
        (group-by :user_id results)))

(defn list-books-recommend
"Return seq of all books grouped by isbn"
[]
(let [results (sql/query db
      ["select bx_book_ratings.ISBN as ISBN, Book_Rating, User_ID from bx_books inner join bx_book_ratings on bx_book_ratings.ISBN=bx_books.ISBN"])]
        (group-by :isbn results)))


(defn list-user-ratings
"Return all books ratings by user"
[id]
(let [results (sql/query db
      ["select ISBN, Book_Rating from bx_book_ratings WHERE User_ID=? and ISBN not IN (Select ISBN from bx_book_ratings where User_ID=99)" id])]
        results))

(defn change-to-map
  "Change to map with isbn as key and rating as value"
  [users]
  (reduce
   (fn [m v]
     (assoc m (:isbn v) (:book_rating v))) {} users))

(defn change-to-map-books
 "Change to map with user_id as key and rating as value"
  [books]
  (reduce
   (fn [m v]
     (assoc m (:user_id v) (:book_rating v))) {} books))



(defn euclidean-distance
 "Calculate euclidean distance"
 [user1 user2]
  (let [same-books (filter user1 (keys user2))]
    (if (= 0 (count same-books))
     0
     (let [result (/ 1.0 (inc (reduce (fn [acc v]
                         (let [score1 (user1 v)
                               score2 (user2 v)]
                          (+ acc (Math/pow (- score1 score2) 2))))
                                      0 same-books)))]
       result))))


(defn top-matches [algorithm base user]
   (sort-by second >
            (map (fn [[k v]]
                [k (algorithm (change-to-map (base user)) (change-to-map (base k)))])
     (dissoc base user))))



(defn multiplyCoeff [base similar-users user]
 (reduce
   (fn [k v]
     (let
       [data (list-user-ratings (first v))
       multiplied-coeff (apply assoc {}
                                (interleave (map :isbn data)
                                            (map #(* % (second v)) (map :book_rating data))))]
       (assoc k (first v) multiplied-coeff))) {} similar-users))


(defn sum-sims [multiplied-coeff similar-users]
 (let [sum (doall(reduce (fn [k v] (merge-with #(+ %1 %2) k v)) {} (vals multiplied-coeff)))]
 (reduce (fn [h m]
            (let [movie (first m)
                  rated-users (reduce
                               (fn [h m] (if (contains? (val m) movie)
                                          (conj h (key m)) h))
                               [] multiplied-coeff)
                  similarities (apply + (map #(similar-users %) rated-users))]
              (assoc h movie (/(sum movie) similarities)) ) ) {} sum)))

(defn sort-recommendations [recommended-list]
      (into (sorted-map-by (fn [key1 key2]
                         (compare [(get recommended-list key2) key2]
                                  [(get recommended-list key1) key1])))
        recommended-list))

(defn book-recommendation
  "Return recommended books for user"
  [id]
  (sort-recommendations
   (sum-sims (multiplyCoeff (list-users) (top-matches euclidean-distance (base-similar-users2 id) id)id)
                   (into {} (top-matches euclidean-distance (list-users) id))))
  )

(defn similar-books
 "Return best matched books for book"
  [book]
  (let [base (list-books)]
   (sort-by second >
            (map (fn [[k v]]
                [k (euclidean-distance (change-to-map-books (base book)) (change-to-map-books (base k)))])
     (dissoc base book)))))
