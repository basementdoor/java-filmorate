package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRowMapper.class,
        JdbcGenreRepository.class})
public class JdbcGenreRepositoryTest {

    private final JdbcGenreRepository genreRepository;

    @Test
    void getAllGenresTest() {
        Collection<Genre> genres = genreRepository.getAll();
        assertEquals(6, genres.size());
    }

    @Test
    void getGenreByIdTest() {
        Optional<Genre> optGenre = genreRepository.getById(1L);
        assertThat(optGenre).isPresent();

        Genre genre = optGenre.get();
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }
}
