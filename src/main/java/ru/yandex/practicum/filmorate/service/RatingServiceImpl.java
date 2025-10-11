package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.RatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public List<Rating> getAll() {
        return ratingRepository.getAll();
    }

    @Override
    public Rating getById(Long id) {
        return ratingRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID: " + id + " не найден"));
    }
}
