package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
public class UserControllerTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createUserTest() {
        User user = new User();
        user.setLogin("basementdoor");
        user.setName("Charles");
        user.setEmail("someemail@gmail.com");
        user.setBirthday(LocalDate.of(1998, 12, 12));

        Set<ConstraintViolation<User>> errors = validator.validate(user);
        Assertions.assertTrue(errors.isEmpty(), "Не должно быть ошибок");
    }

    @Test
    void incorrectEmailErrorTest() {
        User user = new User();
        user.setLogin("basementdoor");
        user.setName("Charles");
        user.setEmail("blablabla");
        user.setBirthday(LocalDate.of(1998, 12, 12));

        Set<ConstraintViolation<User>> errors = validator.validate(user);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<User> error = errors.iterator().next();
        Assertions.assertEquals("Некорректный email", error.getMessage());
    }

    @Test
    void emptyEmailErrorTest() {
        User user = new User();
        user.setLogin("basementdoor");
        user.setName("Charles");
        user.setBirthday(LocalDate.of(1998, 12, 12));

        Set<ConstraintViolation<User>> errors = validator.validate(user);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<User> error = errors.iterator().next();
        Assertions.assertEquals("Укажите email", error.getMessage());
    }

    @Test
    void emptyLoginErrorTest() {
        User user = new User();
        user.setName("Charles");
        user.setEmail("someemail@gmail.com");
        user.setBirthday(LocalDate.of(1998, 12, 12));

        Set<ConstraintViolation<User>> errors = validator.validate(user);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<User> error = errors.iterator().next();
        Assertions.assertEquals("Укажите логин", error.getMessage());
    }

    @Test
    void futureBirthdayErrorTest() {
        User user = new User();
        user.setLogin("basementdoor");
        user.setName("Charles");
        user.setEmail("someemail@gmail.com");
        user.setBirthday(LocalDate.of(2049, 12, 12));

        Set<ConstraintViolation<User>> errors = validator.validate(user);
        Assertions.assertFalse(errors.isEmpty(), "Ожидалась ошибка");
        ConstraintViolation<User> error = errors.iterator().next();
        Assertions.assertEquals("Дата рождения не может быть в будущем", error.getMessage());
    }

}
