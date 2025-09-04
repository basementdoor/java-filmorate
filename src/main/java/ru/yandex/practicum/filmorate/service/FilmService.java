package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(userId, filmId);
    }

    public void removeLike(Long userId, Long filmId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int amount) {
        return filmStorage.getPopular(amount);
    }
}
