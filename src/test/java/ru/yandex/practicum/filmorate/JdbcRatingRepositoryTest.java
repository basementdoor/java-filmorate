package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.JdbcRatingRepository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingRowMapper.class,
        JdbcRatingRepository.class})
public class JdbcRatingRepositoryTest {

    private final JdbcRatingRepository ratingRepository;

    @Test
    void getAllRatingsTest() {
        Collection<Rating> ratings = ratingRepository.getAll();
        assertEquals(5, ratings.size());
    }

    @Test
    void getRatingByIdTest() {
        Optional<Rating> optRating = ratingRepository.getById(1L);
        assertThat(optRating).isPresent();

        Rating rating = optRating.get();
        assertEquals(1L, rating.getId());
        assertEquals("G", rating.getName());
    }
}
