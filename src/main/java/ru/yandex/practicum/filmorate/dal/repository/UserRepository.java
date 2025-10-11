package ru.yandex.practicum.filmorate.dal.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    Optional<User> getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);

    List<User> getAllFriends(Long userId);
}
