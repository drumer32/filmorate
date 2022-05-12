package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

/**
 * название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */

public class FilmControllerTest {

    private FilmStorage filmStorage = new InMemoryFilmStorage();
    private UserStorage userStorage = new InMemoryUserStorage();
    private FilmService filmService = new FilmService(filmStorage, userStorage);
    private FilmController filmController = new FilmController(filmStorage, filmService);
    static Film film;

    @BeforeEach
    void generateUser() {
        film = new Film(
                1L,
                "Interstellar",
                "Следующий шаг человечества станет величайшим",
                LocalDate.of(2014, 10,26),
                169
        );
    }

    @Test
    void filmCreationTest() throws ValidationException {
        filmController.addFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void filmUpdateTest() throws ValidationException {
        Film newFilm = new Film(
                1L,
                "Interstellar2",
                "Следующий шаг человечества станет величайшим",
                LocalDate.of(2014, 10,26),
                170
        );
        filmController.addFilm(film);
        filmController.updateFilm(newFilm);
        Assertions.assertEquals(170, filmController.getFilmById(newFilm.getId()).getDuration());
    }

    @Test
    void blankFilmNameTest() {
        film.setName("");
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void moreThan200CharsDescriptionTest() {
        film.setDescription("Когда засуха, пыльные бури и вымирание растений" +
                " приводят человечество к продовольственному " +
                "кризису, коллектив исследователей и учёных " +
                "отправляется сквозь червоточину (которая предположительно " +
                "соединяет области пространства-времени через большое расстояние)" +
                " в путешествие, чтобы превзойти прежние " +
                "ограничения для космических путешествий человека и найти планету с " +
                "подходящими для человечества условиями.");
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void earlyReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1895, 12,26));
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void negativeDurationTest() {
        film.setDuration(-1);
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }
}
