package ru.yandex.practicum.filmorate.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final UserRowMapper userMapper;

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id";
        return jdbc.query(sql, userMapper);
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());

        String sql = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (:email, :login, :name, :birthday)
                """;
        jdbc.update(sql, params, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        checkExist(user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());

        String sql = """
                UPDATE users SET
                email = :email,
                login = :login,
                name = :name,
                birthday = :birthday
                WHERE id = :id
                """;
        jdbc.update(sql, params);
        log.info("Информация о пользователе обновлена: {}", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Получен запрос на пользователя с ID: {}", id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = "SELECT * FROM users WHERE id = :id";
        return jdbc.query(sql, params, userMapper).stream()
                .findFirst();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = checkExist(userId);
        User friend = checkExist(friendId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", friend.getId());
        params.addValue("confirmed", true);

        String sql = """
                INSERT INTO friendships (user_id, friend_id, confirmed)
                VALUES (:user_id, :friend_id, :confirmed)
                """;
        jdbc.update(sql, params);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = checkExist(userId);
        User friend = checkExist(friendId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", friend.getId());

        String sql = "DELETE FROM friendships WHERE user_id = :user_id AND friend_id = :friend_id";
        jdbc.update(sql, params);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = checkExist(userId);
        User friend = checkExist(friendId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", friend.getId());
        String sql = """
                SELECT u.* FROM users AS u
                JOIN friendships AS f1 ON u.id = f1.friend_id
                JOIN friendships AS f2 ON u.id = f2.friend_id
                WHERE f1.user_id = :user_id AND f2.user_id = :friend_id
                """;
        log.info("Возвращаем список общих друзей пользователей {} и {}", userId, friendId);
        return jdbc.query(sql, params, userMapper);
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        User user = checkExist(userId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        String sql = """
                SELECT u.* FROM users AS u
                JOIN friendships AS f ON u.id = f.friend_id
                WHERE f.user_id = :user_id AND f.confirmed = true
                """;
        log.info("Возвращен список друзей пользователя {}", userId);
        return jdbc.query(sql, params, userMapper);
    }

    private User checkExist(Long id) {
        return getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден"));
    }
}
