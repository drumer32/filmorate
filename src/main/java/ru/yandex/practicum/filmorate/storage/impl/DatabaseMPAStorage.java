package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DatabaseMPAStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<RatingMPA> getAll() {
        final String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public RatingMPA get(int id) {
        final String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        final List<RatingMPA> mpaRating = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
        return mpaRating.size() > 0 ? mpaRating.get(0) : null;
    }

    private RatingMPA mapRowToMpa(ResultSet rs) throws SQLException {
        return RatingMPA.builder()
                .id(rs.getInt("mpa_id"))
                .title(rs.getString("title"))
                .build();
    }
}
