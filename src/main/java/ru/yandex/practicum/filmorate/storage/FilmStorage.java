package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film createFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;

    void deleteFilm(Film film);

    void saveLike(Like like);

    void deleteLike(Like like);
}
