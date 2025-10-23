package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.GenreRepository;
import ru.yandex.practicum.filmorate.dal.repository.RatingRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    @Override
    public Collection<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmRepository.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID: " + id + " не найден"));
    }

    @Override
    public Film create(Film film) {
        throwIfRatingNotExist(film);
        throwIfGenreNotExist(film);
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        throwIfRatingNotExist(newFilm);
        throwIfGenreNotExist(newFilm);
        return filmRepository.update(newFilm);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        throwIfUserNotFound(userId);
        filmRepository.addLike(userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        throwIfUserNotFound(userId);
        filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(int amount) {
        return filmRepository.getPopular(amount);
    }

    private void throwIfUserNotFound(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    private void throwIfRatingNotExist(Film film) {
        ratingRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID: " + film.getMpa().getId() + " не найден"));
    }

    private void throwIfGenreNotExist(Film film) {
        if (film.getGenres() == null) return;
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
    }
}
