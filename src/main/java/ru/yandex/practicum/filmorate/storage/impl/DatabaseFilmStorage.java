package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseFilmStorage implements FilmStorage, LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();
        final String sql = "SELECT * FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(sql, (rs, numRow) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        });
    }

    @Override
    public Film getFilmById(Long id) {
        final String sql = "SELECT * FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, numRow) -> mapRowToFilm(rs, getFilmGenresById(id)), id);
        return films.size() > 0 ? films.get(0) : null;
    }

    @Override
    public void createFilm(Film film) {
        final String sql = "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());

        final Set<Genre> filmGenres = film.getGenres();

        if (filmGenres != null) {
            final String genreSaveSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            filmGenres.forEach(x -> jdbcTemplate.update(genreSaveSql, film.getId(), x.getId()));
        }
    }

    @Override
    public void updateFilm(Film film) {
        final String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?"
                + " WHERE film_id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());

        final String deleteGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());

        final Set<Genre> filmGenres = film.getGenres();

        if (filmGenres != null) {
            final String genreSaveSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            filmGenres.forEach(x -> jdbcTemplate.update(genreSaveSql, film.getId(), x.getId()));
        }
    }

    @Override
    public void deleteFilm(Film film) {
        final String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public Collection<Film> getPopularFilms(Integer limit) {
        final String sql = "SELECT * FROM films f LEFT JOIN (SELECT film_id, COUNT(*) likes_count FROM likes"
                + " GROUP BY film_id) l ON f.film_id = l.film_id LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id"
                + " ORDER BY l.likes_count DESC LIMIT ?";

        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();

        return jdbcTemplate.query(sql, (rs, numRow) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        }, limit);
    }

    @Override
    public void saveLike(Like like) {
        final String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, like.getUser().getId(), like.getFilm().getId());
    }

    @Override
    public void deleteLike(Like like) {
        final String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, like.getUser().getId(), like.getFilm().getId());
    }

    private Map<Long, Set<Genre>> getAllFilmsGenres() {
        final String sql = "SELECT * FROM film_genres INNER JOIN genres ON genres.genre_id = film_genres.genre_id";

        final Map<Long, Set<Genre>> filmsGenres = new HashMap<>();

        jdbcTemplate.query(sql, (RowCallbackHandler) rs -> {
            final Long filmId = rs.getLong("film_id");
            filmsGenres.getOrDefault(filmId, new HashSet<>()).add(Genre.builder().id(rs.getInt("genre_id"))
                    .title(rs.getString("title")).build());
        });

        return filmsGenres;
    }
    private Set<Genre> getFilmGenresById(Long id) {
        final String sql = "SELECT * FROM film_genres INNER JOIN genres ON genres.genre_id = film_genres.genre_id"
                + " WHERE film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, (rs, getNum) -> Genre.builder().id(rs.getInt("genre_id"))
                .title(rs.getString("title")).build(), id));
    }

    private Film mapRowToFilm(ResultSet rs, Set<Genre> genres) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(genres != null && genres.isEmpty() ? null : genres)
                .mpa(RatingMPA.builder().id(rs.getInt("mpa_id")).title(rs.getString("title")).build())
                .build();
    }
}
