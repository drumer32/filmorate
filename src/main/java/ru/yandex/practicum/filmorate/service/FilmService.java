package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;

    @Autowired
    FilmService(FilmStorage databaseFilmStorage, LikeStorage databaseLikeStorage,
                UserService userService) {
        this.filmStorage = databaseFilmStorage;
        this.userService = userService;
        this.likeStorage = databaseLikeStorage;
    }

    public void addLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        checkNullFilm(filmId);
        checkNullUser(userId);
        likeStorage.saveLike(Like
                .builder()
                .film(filmStorage.getFilmById(filmId))
                .user(userService.getUserById(userId))
                .build());
    }

    public void deleteLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        checkNullFilm(filmId);
        checkNullUser(userId);
        likeStorage.deleteLike(Like
                .builder()
                .film(filmStorage.getFilmById(filmId))
                .user(userService.getUserById(userId))
                .build());
    }

    public Collection<Film> sortedPopularFilms(Integer count) {
        return likeStorage.getPopularFilms(count != null ? count : 10);
    }

    private void checkNullUser(Long id) throws UserNotFoundException {
        if (userService.getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }

    private void checkNullFilm(Long id) throws FilmNotFoundException {
        if (filmStorage.getFilmById(id) == null) {
            throw new FilmNotFoundException(String.format("Не найден фильм с id=%s", id));
        }
    }

    public void createFilm(Film film) throws ValidationException {
        filmStorage.createFilm(film);
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) throws FilmNotFoundException {
        final Film film = filmStorage.getFilmById(id);
        if (film == null) throw new FilmNotFoundException("Фильм не найден");
        return film;
    }

    public void updateFilm(Film newFilm) throws ValidationException, FilmNotFoundException {
        final Film film = getFilmById(newFilm.getId());
        if (newFilm.equals(film)) return;
        filmStorage.updateFilm(newFilm);
    }

    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
    }
}
