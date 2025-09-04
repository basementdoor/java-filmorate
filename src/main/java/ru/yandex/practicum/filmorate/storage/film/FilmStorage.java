package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film newFilm);

    Film getFilmById(Long id);

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    List<Film> getPopular(int amount);
}
