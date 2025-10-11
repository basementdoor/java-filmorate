package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> getAll();

    Film getFilmById(Long id);

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(Long filmId, Long userId);

    void removeLike(Long userId, Long filmId);

    List<Film> getPopular(int amount);
}
