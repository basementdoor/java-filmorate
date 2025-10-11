package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.dal.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.dal.repository.JdbcRatingRepository;
import ru.yandex.practicum.filmorate.dal.repository.JdbcUserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcFilmRepository.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        RatingRowMapper.class,
        JdbcUserRepository.class,
        JdbcGenreRepository.class,
        JdbcRatingRepository.class,
        UserRowMapper.class})
public class JdbcFilmRepositoryTest {

    private final JdbcFilmRepository filmRepository;
    private final JdbcTemplate jdbc;

    @Test
    void getAllFilmsTest() {
        Collection<Film> allFilms = filmRepository.getAll();
        assertEquals(3, allFilms.size());
    }

    @Test
    void updateFilmTest() {
        Optional<Film> existedFilm = filmRepository.getFilmById(1L);
        assertThat(existedFilm).isPresent();

        Film filmToUpdate = existedFilm.get();
        filmToUpdate.setName("Updated Film Name");
        filmToUpdate.setDescription("Updated Description");

        Film updatedFilm = filmRepository.update(filmToUpdate);

        assertAll(
                () -> assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name"),
                () -> assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description")
        );
    }

    @Test
    void getFilmByIdTest() {
        Optional<Film> optFilm = filmRepository.getFilmById(2L);

        assertThat(optFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(2L);
                    assertThat(film.getName()).isEqualTo("Forrest Gump");
                    assertThat(film.getDescription()).isEqualTo("Run, Forrest, run!");
                });
    }

    @Test
    void addAndRemoveLikeTest() {
        filmRepository.addLike(1L, 1L);

        Optional<Film> optFilm = filmRepository.getFilmById(1L);
        assertThat(optFilm).isPresent();
        Film likedFilm = optFilm.get();
        assertThat(likedFilm.getLikes()).contains(1L);

        filmRepository.removeLike(1L, 1L);
        Optional<Film> optUnlikedFilm = filmRepository.getFilmById(1L);
        assertThat(optUnlikedFilm).isPresent();
        Film unlikedFilm = optUnlikedFilm.get();
        assertThat(unlikedFilm.getLikes()).isEmpty();
    }

    @Test
    void getPopularFilmsTest() {
        Collection<Film> popularFilms = filmRepository.getPopular(2);
        assertThat(popularFilms).hasSize(2);
    }

    @Test
    void createNewFilmTest() {

        // очищаем таблицу перед тестом
        jdbc.update("DELETE FROM films");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        Film newFilm = new Film();
        newFilm.setName("The Big Lebowski");
        newFilm.setDescription("Where is the money?");
        newFilm.setReleaseDate(LocalDate.of(1998, 3, 6));
        newFilm.setDuration(117);

        Rating rating = new Rating();
        rating.setId(1L);
        newFilm.setMpa(rating);

        Genre genre = new Genre();
        genre.setId(1L);
        newFilm.setGenres(Set.of(genre));

        Film savedFilm = filmRepository.create(newFilm);
        assertThat(savedFilm).isNotNull();
        assertAll("savedFilm",
                () -> assertThat(savedFilm.getId()).isEqualTo(1L),
                () -> assertThat(savedFilm.getName()).isEqualTo("The Big Lebowski"),
                () -> assertThat(savedFilm.getDescription()).isEqualTo("Where is the money?"),
                () -> assertThat(savedFilm.getReleaseDate()).isEqualTo(LocalDate.of(1998, 3, 6)),
                () -> assertThat(savedFilm.getDuration()).isEqualTo(117),
                () -> assertThat(savedFilm.getMpa()).isNotNull(),
                () -> assertThat(savedFilm.getMpa().getId()).isEqualTo(1L),
                () -> assertThat(savedFilm.getGenres())
                        .isNotEmpty()
                        .anyMatch(g -> g.getId().equals(1L))
        );
    }
}
