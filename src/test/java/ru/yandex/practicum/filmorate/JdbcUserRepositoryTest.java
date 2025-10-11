package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.JdbcUserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcUserRepository.class,
        UserRowMapper.class})
public class JdbcUserRepositoryTest {

    private final JdbcUserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testFindAllUsers() {
        Collection<User> users = userRepository.getAllUsers();
        assertThat(users).hasSize(3);
    }

    @Test
    void getUserByIdTest() {
        Optional<User> userOptional = userRepository.getUserById(1L);

        assertThat(userOptional).isPresent();

        userOptional.ifPresent(user -> assertAll(
                "User properties",
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("user1@example.com", user.getEmail()),
                () -> assertEquals("userOne", user.getLogin()),
                () -> assertEquals("Jack", user.getName())
        ));
    }

    @Test
    void updateUserTest() {
        Optional<User> existedUser = userRepository.getUserById(1L);
        assertThat(existedUser).isPresent();

        User userToUpdate = existedUser.get();
        userToUpdate.setName("Jimmy 2000");
        userToUpdate.setEmail("updated@example.com");

        User updatedUser = userRepository.update(userToUpdate);
        assertThat(updatedUser.getName()).isEqualTo("Jimmy 2000");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void getCommonFriendsTest() {
        Collection<User> commonFriends = userRepository.getCommonFriends(1L, 2L);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.iterator().next().getId()).isEqualTo(3L);
    }

    @Test
    void getUserFriendsTest() {
        Collection<User> friends = userRepository.getAllFriends(1L);

        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void addAndRemoveFriendTest() {
        // очищаем таблицу перед тестом
        jdbcTemplate.update("DELETE FROM friendships");

        userRepository.addFriend(1L, 3L);
        Collection<User> friendsAfterAdd = userRepository.getAllFriends(1L);
        assertThat(friendsAfterAdd).extracting(User::getId).contains(3L);

        userRepository.removeFriend(1L, 3L);
        Collection<User> friendsAfterRemove = userRepository.getAllFriends(1L);
        assertThat(friendsAfterRemove).extracting(User::getId).doesNotContain(3L);
    }

    @Test
    void createUserTest() {
        // очищаем таблицу перед тестом
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setLogin("starLord");
        newUser.setName("Chris Pratt");
        newUser.setBirthday(LocalDate.of(1979, 6, 21));

        User savedUser = userRepository.create(newUser);

        assertThat(savedUser).isNotNull();
        assertAll("Saved User properties",
                () -> assertEquals(1L, savedUser.getId()),
                () -> assertEquals("newuser@example.com", savedUser.getEmail()),
                () -> assertEquals("starLord", savedUser.getLogin()),
                () -> assertEquals("Chris Pratt", savedUser.getName()),
                () -> assertEquals(LocalDate.of(1979, 6, 21), savedUser.getBirthday())
        );
    }
}
