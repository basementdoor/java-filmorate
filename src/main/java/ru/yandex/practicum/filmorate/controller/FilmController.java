package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        if (film.getReleaseDate().isBefore(MIN_RELEASE)) {
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }

        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            throw new ValidationException("ID фильма должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.info("Фильм обновлен: {}", newFilm);
            return newFilm;
        }
        throw new NotFoundException("Пользователь с id = " + newFilm.getId() + " не найден");
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
