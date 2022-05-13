package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> getFilmById(Long id);

    Film create(Film film) throws ValidationException;

    Film update(Film film) throws ValidationException;
}
