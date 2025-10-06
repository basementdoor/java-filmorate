package ru.yandex.practicum.filmorate.dal.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getFilmById(Long id);

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    List<Film> getPopular(int amount);
}
