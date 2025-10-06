package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;

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
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return filmRepository.update(newFilm);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmRepository.addLike(userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(int amount) {
        return filmRepository.getPopular(amount);
    }
}
