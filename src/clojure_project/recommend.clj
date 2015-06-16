(ns clojure-project.recommend
  (:require [clojure-project.models.db :as db]))

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

(defn sq ^double [^double score1 ^double score2]
   (java.lang.Math/pow (- score1 score2) 2))


(defn cosine-simillarity
 "Calculate cosine simillarity"
 [user1 user2]
  (let [same-books (filter user1 (keys user2))]
    (if (= 0 (count same-books))
     0
     (let [dot-product (reduce (fn [acc v]
                         (let [score1 (user1 v)
                               score2 (user2 v)]
                          (+ acc (* score1 score2))))
                                      0 same-books)
          magnitude-user-1 (java.lang.Math/sqrt (reduce (fn [acc1 v1]
                              (let [score1 (user1 v1)]
                                (+ acc1 (* score1 score1))))
                                          0 same-books))
          magnitude-user-2 (java.lang.Math/sqrt (reduce (fn [acc2 v2]
                              (let [score2 (user2 v2)]
                                (+ acc2 (* score2 score2))))
                                          0 same-books))]
       (/ dot-product (* magnitude-user-1 magnitude-user-2) )))))


(defn euclidean-distance
 "Calculate euclidean distance"
 [user1 user2]
  (let [same-books (filter user1 (keys user2))]
    (if (= 0 (count same-books))
     0
     (let [result (/ 1.0 (+ 1 (reduce (fn [acc v]
                         (let [score1 (user1 v)
                               score2 (user2 v)]
                          (+ acc (sq score1 score2))))
                                      0 same-books)))]
       result))))


(defn pearson-simillarity
 "Calculate pearson correlation"
 [user1 user2]
  (let [same-books (filter user1 (keys user2))
        size (count same-books)]
    (if (= 0 size)
     0
     (let [sum1 (reduce (fn [acc v]
                         (let [score (user1 v)]
                          (+ acc score)))
                              0 same-books)
           sum2 (reduce (fn [acc v]
                         (let [score (user2 v)]
                          (+ acc score)))
                              0 same-books)
           sum1-sq (reduce (fn [acc v]
                         (let [score (user1 v)]
                          (+ acc (* score score))))
                              0 same-books)
           sum2-sq (reduce (fn [acc v]
                         (let [score (user2 v)]
                          (+ acc (* score score))))
                              0 same-books)
           sum-product (reduce (fn [acc v]
                         (let [score (user1 v)
                               score2 (user2 v)]
                          (+ acc (* score score2))))
                              0 same-books)
           numeratorr (- sum-product (/ (* sum1 sum2) size))
           den1 (- sum1-sq (/ (* sum1 sum1) size))
           den2 (- sum2-sq (/ (* sum2 sum2) size))
           denominatorr (java.lang.Math/sqrt (* den1 den2))]

        (if (zero? denominatorr)
         0
         (/ numeratorr denominatorr))))))


(defn top-matches [algorithm base user]
   (sort-by second >
            (map (fn [[k v]]
                [k (algorithm (change-to-map (base user)) (change-to-map (base k)))])
     (dissoc base user))))



(defn multiplyCoeff [base similar-users user]
 (reduce
   (fn [k v]
     (let
       [data (db/list-user-ratings (first v))
       multiplied-coeff (apply assoc {}
                                (interleave (map :isbn data)
                                            (map #(* % (second v)) (map :book_rating data))))]
       (assoc k (first v) multiplied-coeff))) {} similar-users))


(defn sum-sims [multiplied-coeff similar-users]
 (let [sum (doall(reduce (fn [k v] (merge-with #(+ %1 %2) k v)) {} (vals multiplied-coeff)))]
 (reduce (fn [h m]
            (let [book (first m)
                  users (reduce
                               (fn [h m] (if (contains? (val m) book)
                                          (conj h (key m)) h))
                               [] multiplied-coeff)
                  similar (apply + (map #(similar-users %) users))]
              (assoc h book (/(sum book) similar)) ) ) {} sum)))

(defn sort-recommendations [recommended-list]
      (into (sorted-map-by (fn [key1 key2]
                         (compare [(get recommended-list key2) key2]
                                  [(get recommended-list key1) key1])))
        recommended-list))

(defn book-recommendation
  "Return recommended books for user"
  [id]
  (sort-recommendations
   (sum-sims (multiplyCoeff (db/list-users) (top-matches cosine-simillarity (db/base-similar-users2 id) id)id)
                   (into {} (top-matches cosine-simillarity (db/list-users) id))))
  )

(defn similar-books
 "Return best matched books for book"
  [book]
  (let [base (db/list-books)]
   (sort-by second >
            (map (fn [[k v]]
                [k (euclidean-distance (change-to-map-books (base book)) (change-to-map-books (base k)))])
     (dissoc base book)))))
