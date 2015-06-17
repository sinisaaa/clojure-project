(ns clojure-project.criterium
   (use criterium.core)
   (:require [compojure.core :refer :all]
             [clojure-project.models.db :as db]
             [clojure-project.recommend :as rec]))


   ;Execution time mean : 68.879274 ms
   ;Execution time std-deviation : 5.338567 ms
   ;Execution time lower quantile : 66.207869 ms ( 2.5%)
   ;Execution time upper quantile : 78.083985 ms (97.5%)
   ;               Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (db/list-users)))


   ;Execution time mean : 63.414195 ms
   ;Execution time std-deviation : 1.327281 ms
   ;Execution time lower quantile : 62.500907 ms ( 2.5%)
   ;Execution time upper quantile : 65.631496 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (db/list-books)))
   ;---------------------------------------------------------------

   ;Compare two users

   ;Execution time mean : 20.541930 ms
   ;Execution time std-deviation : 131.300291 µs
   ;Execution time lower quantile : 20.436375 ms ( 2.5%)
   ;Execution time upper quantile : 20.760961 ms (97.5%)
   ;          Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/euclidean-distance (rec/change-to-map((db/base-similar-users2 99) 99)) (rec/change-to-map((db/base-similar-users2 99) 8)))))


   ;Execution time mean : 20.543512 ms
   ;Execution time std-deviation : 65.447146 µs
   ;Execution time lower quantile : 20.464086 ms ( 2.5%)
   ;Execution time upper quantile : 20.629271 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/pearson-simillarity (rec/change-to-map((db/base-similar-users2 99) 99)) (rec/change-to-map((db/base-similar-users2 99) 8)))))


   ;Execution time mean : 20.959117 ms
   ;Execution time std-deviation : 1.210481 ms
   ;Execution time lower quantile : 20.332203 ms ( 2.5%)
   ;Execution time upper quantile : 23.042366 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/cosine-simillarity (rec/change-to-map((db/base-similar-users2 99) 99)) (rec/change-to-map((db/base-similar-users2 99) 8)))))

   ;----------------------------------------------------------------------

   ;Recommend books by most rated author

   ;Execution time mean : 62.359252 ms
   ;Execution time std-deviation : 526.607226 µs
   ;Execution time lower quantile : 61.764732 ms ( 2.5%)
   ;Execution time upper quantile : 63.001882 ms (97.5%)
   ;                 Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (db/get-books-by-author 99)))

   ;----------------------------------------------------------------------

   ;Recommend books by similar users

    ;EUCLIDEAN DISTANCE with optimisation
   ;Execution time mean : 180.148741 ms
   ;Execution time std-deviation : 1.109671 ms
   ;Execution time lower quantile : 178.901470 ms ( 2.5%)
   ;Execution time upper quantile : 181.569169 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting(quick-bench (rec/book-recommendation 99)))

   ;EUCLIDEAN DISTANCE without optimisation
   ;Execution time mean : 182.568234 ms
   ;Execution time std-deviation : 1.857340 ms
   ;Execution time lower quantile : 180.987477 ms ( 2.5%)
   ;Execution time upper quantile : 185.624066 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting(quick-bench (rec/book-recommendation 99)))


   ;COSINE SIMILARITY
   ;Execution time mean : 186.142700 ms
   ;Execution time std-deviation : 5.808394 ms
   ;Execution time lower quantile : 181.749953 ms ( 2.5%)
   ;Execution time upper quantile : 194.219121 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting(quick-bench (rec/book-recommendation 99)))

   ;--------------------------------------------------------------------------------

   ;Recommend similar books

   ;EUCLIDEAN DISTANCE with optimisation
   ;Execution time mean : 78.112574 ms
   ;Execution time std-deviation : 1.374715 ms
   ;Execution time lower quantile : 77.363392 ms ( 2.5%)
   ;Execution time upper quantile : 80.480109 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/similar-books "0006476333")))

   ;EUCLIDEAN DISTANCE without optimisation
   ;Execution time mean : 78.851173 ms
   ;Execution time std-deviation : 2.147427 ms
   ;Execution time lower quantile : 77.273589 ms ( 2.5%)
   ;Execution time upper quantile : 81.982047 ms (97.5%)
   ;               Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/similar-books "0006476333")))

   ;PEARSON CORRELATION
   ;Execution time mean : 78.836241 ms
   ;Execution time std-deviation : 3.691706 ms
   ;Execution time lower quantile : 77.093554 ms ( 2.5%)
   ;Execution time upper quantile : 85.241166 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/similar-books "0006476333")))


   ;COSINE SIMILARITY
   ;Execution time mean : 80.540477 ms
   ;Execution time std-deviation : 4.152712 ms
   ;Execution time lower quantile : 77.353770 ms ( 2.5%)
   ;Execution time upper quantile : 87.203133 ms (97.5%)
   ;                Overhead used : 2.099331 ns
   ;(with-progress-reporting (quick-bench (rec/similar-books "0006476333")))

