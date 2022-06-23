package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserIdStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAllUsers() {
        final String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User getUserById(Long id) {
        final String sql = "SELECT * FROM users WHERE user_id = ?";
        final List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        return users.size() > 0 ? users.get(0) : null;
    }

    @Override
    public User create(User newUser) {
        if (newUser.getId() == null) {
            newUser = newUser.toBuilder()
                    .id(UserIdStorage.generateId())
                    .build();
        }
        newUser = convertLoginToName(newUser);

        final String sql = "INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(),
                newUser.getBirthday());

        return newUser;
    }

    @Override
    public User update(User user) {
        final String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }


    @Override
    public void deleteUser(User user) {
        final String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private User convertLoginToName(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user = user.toBuilder()
                    .name(user.getLogin())
                    .build();
        }
        return user;
    }
}
