package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public Rating getById(@PathVariable Long id) {
        return ratingService.getById(id);
    }
}
