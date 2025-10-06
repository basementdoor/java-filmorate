INSERT INTO genres (id, name)
VALUES (1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO ratings (mpa_id, mpa_name)
VALUES (1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

INSERT INTO users (id, email, login, name, birthday) VALUES
(1, 'user1@example.com', 'userOne', 'Jack', '2000-01-01'),
(2, 'user2@example.com', 'userTwo', 'John', '1997-05-02'),
(3, 'user3@example.com', 'userThree', 'Michael', '1976-12-03');

INSERT INTO films (id, name, description, release_date, duration, rating_id) VALUES
(1, 'Terminator 2', 'I''ll be back!', '1992-07-03', 137, 1),
(2, 'Forrest Gump', 'Run, Forrest, run!', '1994-07-06', 142, 2),
(3, 'Titanic', 'They had enough space to survive', '1997-12-19', 195, 3);

INSERT INTO friendships (user_id, friend_id, confirmed) VALUES
(1, 2, true),
(1, 3, true),
(2, 3, true);
