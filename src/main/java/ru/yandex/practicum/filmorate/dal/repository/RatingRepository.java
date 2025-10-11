package ru.yandex.practicum.filmorate.dal.repository;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    List<Rating> getAll();

    Optional<Rating> getById(Long id);
}
