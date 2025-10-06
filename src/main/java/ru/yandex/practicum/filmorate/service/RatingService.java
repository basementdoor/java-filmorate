package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingService {

    List<Rating> getAll();

    Rating getById(Long id);
}
