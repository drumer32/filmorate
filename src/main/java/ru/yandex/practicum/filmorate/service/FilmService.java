package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Map<Long, Set<Long>> likes = new HashMap<>(); // Film Id -- Set из Id тех, кто лайкнул
    private final Map<Long, Integer> rating = new HashMap<>(); // Film Id -- кол-лайков

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        checkNullFilm(filmId);
        checkNullUser(userId);
        Set<Long> filmLikes = likes.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        likes.put(filmId, filmLikes);
        Integer rate = rating.getOrDefault(filmId, 0);
        rating.put(filmId, rate++);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkNullFilm(filmId);
        checkNullUser(userId);
        Set<Long> filmLikes = likes.get(filmId);
        filmLikes.remove(userId);
        likes.put(filmId, filmLikes);
        Integer rate = rating.get(filmId);
        rating.put(filmId, rate--);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return rating.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(count)
                .map(x -> filmStorage.getFilmById(x.getKey()))
                .collect(Collectors.toList()
                );
    }

    private void checkNullUser(Long id) {
        if (userStorage.getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }

    private void checkNullFilm(Long id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new FilmNotFoundException(String.format("Не найден фильм с id=%s", id));
        }
    }
}
