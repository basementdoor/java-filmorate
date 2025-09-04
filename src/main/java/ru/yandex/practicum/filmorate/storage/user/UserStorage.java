package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    User getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getCommonFriends(Long userId, Long friendId);

    Set<Long> getAllFriends(Long userId);
}
