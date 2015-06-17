# Books recommandation system

Application using technique called collaborative filtering to recommend books for user based on already rated books.
Algorithms used in this application are simple algorithms caled: 
<br />
<br />
-Euclidean Distance Score
<br />-Pearson Correlation Score
<br />-Cosine Similarity Score

It has been developed in the LightTable IDE, and it uses SQLite database with three tables: bx_books (books details), bx_users (users details), bx_book_ratings (books ratings - from 1 to 10).

Functionalities:
<br />-Recommend books for user based on similar users books ratings
<br />-Recommend best rated books for users based on user favorite author (most rated by user)
<br />-Recommend similar books for book
<br />-User can rate recommended books
<br />-Criterium tests
<br />
<br />
Login data:
<br />user: user
<br />pass: user

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 Sinisa Nogic
