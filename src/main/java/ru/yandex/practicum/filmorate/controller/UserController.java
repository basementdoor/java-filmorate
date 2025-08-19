package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

        if (newUser.getId() == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() != null && users.values().stream()
                    .anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
                throw new ValidationException("Этот имейл уже используется");
            } else oldUser.setEmail(newUser.getEmail());

            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Информация о пользователе обновлена: {}", oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
