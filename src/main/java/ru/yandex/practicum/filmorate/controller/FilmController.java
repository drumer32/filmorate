package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmIdStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        final Film newFilm = validate(film);
        log.debug("Добавлен фильм - {}", newFilm.getName());
        filmService.createFilm(newFilm);
        return newFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) throws FilmNotFoundException {
        Film film = filmService.getFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Не найден фильм с id=%s", id));
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, FilmNotFoundException {
        final Film newFilm = validate(film);
        log.debug("Обновлен фильм - {}", newFilm.getId());
        filmService.updateFilm(newFilm);
        return newFilm;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.sortedPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/")
    public void deleteFilm(@PathVariable Long id) throws FilmNotFoundException {
        filmService.deleteFilm(getFilmById(id));
    }

    public static Film validate(Film film) throws ValidationException {
        if (film.getId() == null) {
            film = film.toBuilder().id(FilmIdStorage.generateId()).build();
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("release date can't be before the cinema day.");
        }

        return film;
    }
}