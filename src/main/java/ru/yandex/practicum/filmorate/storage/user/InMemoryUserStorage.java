package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User create(User user) {

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {

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

    @Override
    public User getUserById(Long id) {
        log.info("Получен запрос на пользователя с ID: {}", id);
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new NotFoundException("Пользователь с id = " + id + " не найден");
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        checkUserExist(new Long[] {userId, friendId});

        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriendsIds().add(friendId);
        friend.getFriendsIds().add(userId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        checkUserExist(new Long[] {userId, friendId});

        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriendsIds().remove(friendId);
        friend.getFriendsIds().remove(userId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long friendId) {
        log.info("Запрос общих друзей пользователей {} и {}", userId, friendId);
        checkUserExist(new Long[] {userId, friendId});

        Set<Long> userFriends = users.get(userId).getFriendsIds();
        Set<Long> friendFriends = users.get(friendId).getFriendsIds();

        Set<Long> commonFriends = userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toSet());

        if (commonFriends.isEmpty()) {
            throw new NotFoundException("У пользователей с ID: %s и %s нет общих друзей".formatted(userId, friendId));
        } else return commonFriends;
    }

    @Override
    public Set<Long> getAllFriends(Long userId) {
        checkUserExist(new Long[] {userId});
        log.info("Возвращен список друзей пользователя {}", userId);
        return users.get(userId).getFriendsIds();
    }

    private void checkUserExist(Long[] ids) {
        Arrays.stream(ids)
                .filter(id -> !users.containsKey(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new NotFoundException("Пользователь с id = " + id + " не найден");
                });
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
