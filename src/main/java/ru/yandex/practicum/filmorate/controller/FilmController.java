package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

/**
 * добавление фильма;
 * обновление фильма;
 * получение всех фильмов.
 */

@RestController
@Slf4j
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Map<Integer, Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public String create(@Validated @RequestBody Film film) {
        String response = "";
        try {
            validation(film);
            films.put(film.getId(), film);
            response = ("Фильм " + film.getName() + " добавлен");
            log.info("Фильм добавлен {}", film);
        } catch (ValidationException e) {
            log.info(e.getMessage() + film);
            response = e.getMessage();
        }
        return response;
    }

    @PutMapping(value = "/films")
    public String update(@Validated @RequestBody Film film) {
        String response = "";
        try {
            validation(film);
            if (films.containsKey(film.getId())) {
                Film oldFilm = films.get(film.getId());
                oldFilm.setName(film.getName());
                oldFilm.setDescription(film.getDescription());
                oldFilm.setDuration(film.getDuration());
                oldFilm.setReleaseDate(film.getReleaseDate());
                response = ("Фильм " + film.getName() + " обновлен");
                log.info("Фильм обновлен {}", film);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage() + film);
            response = e.getMessage();
        }
        return response;
    }

    /**
     * название не может быть пустым;
     * максимальная длина описания — 200 символов;
     * дата релиза — не раньше 28 декабря 1895 года;
     * продолжительность фильма должна быть положительной.
     */
    private void validation(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма должно быть не длиннее 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не должна быть раньше 1985-12-28");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
    }
}