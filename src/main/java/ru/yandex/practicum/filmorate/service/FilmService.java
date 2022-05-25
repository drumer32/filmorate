package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final Map<Long, Set<Long>> allLikes = new HashMap<>(); // Film Id -- Set из Id тех, кто лайкнул

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        checkNullFilm(filmId);
        checkNullUser(userId);
        Set<Long> filmLikes = allLikes.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        allLikes.put(filmId, filmLikes);
        log.info((allLikes.keySet() + " Лайк добавлен"));
    }

    public void deleteLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        checkNullFilm(filmId);
        checkNullUser(userId);
        Set<Long> filmLikes = allLikes.get(filmId);
        if (!(filmLikes == null)) {
            filmLikes.remove(userId);
            if (filmLikes.size() == 0) {
                allLikes.remove(filmId); // Если у фильма нет лайков, то он удаляется из хит-парада :)
            } else {
                allLikes.put(filmId, filmLikes);
                log.info(allLikes.keySet() + " Лайк удален");
            }
        } else {
            throw new NullPointerException("У фильма нет лайков");
        }
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        List<Film> all = new ArrayList<>(filmStorage.findAll());
        List<Film> liked = new ArrayList<>(sortedPopularFilms(count));
        List<Film> sorted = new ArrayList<>(liked);
        for (Film film : all) {
            if (!(liked.contains(film.getId()))) {
                sorted.add(film);
            }
        }
        return sorted.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> sortedPopularFilms(Integer count) {
        return allLikes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(x -> 1 - x.size())))
                .limit(count)
                .map(x -> filmStorage.getFilmById(x.getKey()).get())
                .collect(Collectors.toList()
                );
    }

    private void checkNullUser(Long id) throws UserNotFoundException {
        if (userStorage.getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }

    private void checkNullFilm(Long id) throws FilmNotFoundException {
        if (filmStorage.getFilmById(id).isEmpty()) {
            throw new FilmNotFoundException(String.format("Не найден фильм с id=%s", id));
        }
    }

    public Film update(Film film) throws FilmNotFoundException, ValidateException {
        return filmStorage.update(film);
    }

    public Film create(Film film) throws ValidateException, FilmAlreadyExistException {
        return filmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Optional<Film> getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }
}
