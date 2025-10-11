package ru.yandex.practicum.filmorate.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmRepository implements FilmRepository {

    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper filmMapper;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;

    @Override
    public Collection<Film> getAll() {
        String sql = """
                SELECT f.*, r.mpa_id, r.mpa_name FROM films AS f
                JOIN ratings AS r ON f.rating_id = r.mpa_id
                """;
        log.info("Возвращен список всех фильмов");
        return jdbc.query(sql, filmMapper);
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE)) {
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        validateRating(film);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        String sql = """
                INSERT INTO films (name, description, release_date, duration, rating_id)
                VALUES (:name, :description, :release_date, :duration, :mpa_id)
                """;
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);
        }
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkExist(film.getId());
        if (film.getReleaseDate().isBefore(MIN_RELEASE)) {
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        validateRating(film);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        String sql = """
                UPDATE films SET
                name = :name,
                description = :description,
                release_date = :release_date,
                duration = :duration,
                rating_id = :mpa_id
                WHERE id = :id
                """;
        jdbc.update(sql, params);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);
        }
        log.info("Фильм обновлен: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = """
                SELECT f.*, r.mpa_id, r.mpa_name FROM films AS f
                JOIN ratings AS r ON f.rating_id = r.mpa_id
                WHERE f.id = :id
                """;
        log.info("Получен запрос на фильм с ID {}", id);
        Film film = jdbc.query(sql, params, filmMapper).stream()
                .findFirst().orElseThrow();
        loadFilmGenres(film);
        loadFilmLikes(film);
        return Optional.of(film);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        checkExist(filmId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (:film_id, :user_id)";
        jdbc.update(sql, params);
        log.info("Пользователь с ID: {} поставил лайк фильму с ID: {}", userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        checkExist(filmId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        String sql = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id";
        jdbc.update(sql, params);
        log.info("Пользователь с ID: {} убрал лайк фильму с ID: {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int amount) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("amount", amount);

        String sql = """
                SELECT f.*, r.mpa_id, r.mpa_name from films AS f
                JOIN ratings AS r ON f.rating_id = r.mpa_id
                LEFT JOIN likes AS l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT :amount
                """;
        log.info("Получен список популярных фильмов");
        return jdbc.query(sql, params, filmMapper);
    }

    private void checkExist(Long id) {
        getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с ID: " + id + " не найден"));
    }

    private void saveGenres(Film film) {
        // сначала убираем сохраненные для фильма жанры
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        jdbc.update("DELETE FROM film_genres WHERE film_id = :film_id", params);

        List<Genre> dbGenres = genreRepository.getAll();
        Set<Long> dbGenreIds = dbGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        boolean allGenresExist = film.getGenres().stream()
                .map(Genre::getId)
                .allMatch(dbGenreIds::contains);
        if (!allGenresExist) {
            throw new NotFoundException("Для фильма указаны несуществующие жанры");
        }

        // добавляем жанры для фильма батчем в таблицу
        SqlParameterSource[] batch = film.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genre.getId()))
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate("INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)", batch);
    }

    private void validateRating(Film film) {
        ratingRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID: " + film.getMpa().getId() + " не найден"));
    }

    private void loadFilmGenres(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        String sql = "SELECT g.id, g.name FROM film_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = :film_id ORDER BY g.id";

        List<Genre> genres = jdbc.query(sql, params, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        });

        film.setGenres(new HashSet<>(genres));
    }

    private void loadFilmLikes(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        String sql = "SELECT user_id FROM likes WHERE film_id = :film_id";
        List<Long> likes = jdbc.query(sql, params, (rs, rowNum) -> rs.getLong("user_id"));
        film.setLikes(new HashSet<>(likes));
    }

}
