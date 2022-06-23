package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class DatabaseFriendshipStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Long> getFriendsIds(Long id) {
        final String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, numRow) -> rs.getLong("friend_id"), id);
    }

    @Override
    public void save(Friendship friendship) {
        final String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, friendship.getUser().getId(), friendship.getFriend().getId());
    }

    @Override
    public void delete(Friendship friendship) {
        final String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendship.getUser().getId(), friendship.getFriend().getId());
    }
}
