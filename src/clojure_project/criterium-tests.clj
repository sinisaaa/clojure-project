(ns clojure-project.criterium
  (:require [clojure-project.recommend :refer :all]
            [criterium.core :refer [with-progress-reporting quick-bench]]))



(with-progress-reporting(quick-bench (book-recommendation 99)))
