package ru.yandex.practicum.filmorate;

import com.github.javafaker.Faker;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

public class FilmControllerTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private Faker faker = new Faker();

    @Test
    void createFilmTest() {
        Film film = new Film();
        film.setName("Lost in translation");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertTrue(errors.isEmpty(), "Не должно быть ошибок");
    }

    @Test
    void emptyNameErrorTest() {
        Film film = new Film();
        film.setName("");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<Film> error = errors.iterator().next();
        Assertions.assertEquals("Название не может быть пустым", error.getMessage());
    }

    @Test
    void withoutReleaseDateErrorTest() {
        Film film = new Film();
        film.setName("Terminator");
        film.setDescription("Classic");
        film.setDuration(120);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<Film> error = errors.iterator().next();
        Assertions.assertEquals("Дата релиза обязательна", error.getMessage());
    }

    @Test
    void negativeDurationErrorTest() {
        Film film = new Film();
        film.setName("Lost in translation");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<Film> error = errors.iterator().next();
        Assertions.assertEquals("Продолжительность должна быть положительным числом", error.getMessage());
    }

    @Test
    void description201ErrorTest() {
        Film film = new Film();
        film.setName("Lost in translation");
        film.setDescription(faker.lorem().characters(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(125);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<Film> error = errors.iterator().next();
        Assertions.assertEquals("Максимальная длина описания — 200 символов", error.getMessage());
    }

    @Test
    void description200OkTest() {
        Film film = new Film();
        film.setName("Lost in translation");
        film.setDescription(faker.lorem().characters(20));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(125);

        Set<ConstraintViolation<Film>> errors = validator.validate(film);
        Assertions.assertTrue(errors.isEmpty(), "Не должно быть ошибок");
    }

}
