# Books recommandation system

Application using technique called collaborative filtering to recommend books for user based on already rated books.
Algorithms used in this application are simple algorithms caled: 

-Euclidean Distance Score
-Pearson Correlation Score
-Cosine Similarity Score

It has been developed in the LightTable IDE, and it uses SQLite database with three tables: bx_books (books details), bx_users (users details), bx_book_ratings (books ratings - from 1 to 10).

Functionalities:
-Recommend books for user based on similar users books ratings
-Recommend best rated books for users based on user favorite author (most rated by user)
-Recommend similar books for book
-User can rate recommended books
-Criterium tests

Login data:
user: user
pass: user

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 Sinisa Nogic
