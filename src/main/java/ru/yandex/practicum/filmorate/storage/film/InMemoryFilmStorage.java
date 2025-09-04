package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> getAll() {
        log.info("Возвращен список всех фильмов");
        return films.values();
    }

    @Override
    public Film create(Film film) {

        if (film.getReleaseDate().isBefore(MIN_RELEASE)) {
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Добавлен новый фильм: {}", film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {

        if (newFilm.getId() == null) {
            throw new ValidationException("ID фильма должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.info("Фильм обновлен: {}", newFilm);
            return newFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            log.info("Возвращен фильм с ID {}", id);
            return films.get(id);
        } else throw new NotFoundException("Фильм с id = " + id + " не найден");
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        Film film = getFilmById(filmId);

        film.getLikes().add(userId);
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        Film film = getFilmById(filmId);

        film.getLikes().remove(userId);
        log.info("Пользователь с ID {} убрал лайк фильму с ID {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int amount) {

        List<Film> popularFilms = films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(amount)
                .toList();

        log.info("Получен список популярных фильмов");

        return popularFilms;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
