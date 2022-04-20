package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

/**
 * добавление фильма;
 * обновление фильма;
 * получение всех фильмов.
 */

@RestController
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Map<Integer, Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Validated @RequestBody Film film) {
        if (!validation(film)) {
            throw new ValidationException("Ошибка валидации. Проверьте введенные данные");
        } else {
            films.put(film.getId(), film);
        }
        log.info("Фильм добавлен {}", film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Validated @RequestBody Film film) {
        if (!validation(film)) {
            throw new ValidationException("Ошибка валидации. Проверьте введенные данные");
        } else {
            try {
                Film oldFilm = films.get(film.getId());
                oldFilm.setName(film.getName());
                oldFilm.setDescription(film.getDescription());
                oldFilm.setDuration(film.getDuration());
                oldFilm.setReleaseDate(film.getReleaseDate());
            } catch (NullPointerException e) {
                throw new ValidationException("Данного фильма пока что нет в базе");
            }
        }
        log.info("Фильм обновлен {}", film);
        return film;
    }

    /**
     * название не может быть пустым;
     * максимальная длина описания — 200 символов;
     * дата релиза — не раньше 28 декабря 1895 года;
     * продолжительность фильма должна быть положительной.
     */
    private boolean validation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            return false;
        } else if (film.getDescription().length() > 200) {
            return false;
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            return false;
        } else if (film.getDuration() < 0) {
            return false;
        } else {
            return true;
        }
    }
}