package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Valid
    private final Film film = Film.builder()
            .id(1L)
            .name("Interstellar")
            .description("Следующий шаг человечества станет величайшим")
            .duration(169)
            .releaseDate(LocalDate.of(2014, 10,26))
            .mpa(RatingMPA.builder().id(1).title("G").build())
            .build();

    @Valid
    private final Film film2 = Film.builder()
            .id(2L)
            .name("Test")
            .description("Test")
            .duration(169)
            .releaseDate(LocalDate.of(2013, 10,26))
            .mpa(RatingMPA.builder().id(1).title("G").build())
            .build();

    private final User user = User.builder()
            .login("login")
            .email("email@email.ru")
            .birthday(LocalDate.of(1985, 1, 1))
            .build();

    @Test
    void filmCreationTest() throws ValidationException {
        filmController.addFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void filmUpdateTest() throws ValidationException, FilmNotFoundException {
        final Film film1 = filmController.addFilm(film);
        final Film newFilm = film1.toBuilder().duration(170).build();
        filmController.updateFilm(newFilm);
        Assertions.assertEquals(170, filmController.getFilmById(newFilm.getId()).getDuration());
    }

    @Test
    void getAllFilmsTest() throws ValidationException {
        filmController.addFilm(film);
        filmController.addFilm(film2);
        Assertions.assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void getFilmByIdTest() throws ValidationException, FilmNotFoundException {
        filmController.addFilm(film);
        filmController.addFilm(film2);
        Assertions.assertEquals(film2, filmController.getFilmById(film2.getId()));
    }

    @Test
    void getPopularFilmsTest() throws ValidationException, UserNotFoundException, FilmNotFoundException {
        filmController.addFilm(film);
        filmController.addFilm(film2);
        final User user1 = userController.addUser(user);
        filmController.addLike(film2.getId(), user1.getId());
        Assertions.assertEquals(List.of(film2), filmController.getPopularFilms(1));
    }

    @Test
    void addLikeTest() throws ValidationException, UserNotFoundException, FilmNotFoundException {
        filmController.addFilm(film);
        filmController.addFilm(film2);
        final User user1 = userController.addUser(user);
        filmController.addLike(film2.getId(), user1.getId());
        Assertions.assertEquals(List.of(film2), filmController.getPopularFilms(1));
    }

    @Test
    void deleteLikeTest() throws ValidationException, UserNotFoundException, FilmNotFoundException {
        filmController.addFilm(film);
        filmController.addFilm(film2);
        final User user1 = userController.addUser(user);
        filmController.addLike(film2.getId(), user1.getId());
        filmController.deleteLike(film2.getId(), user1.getId());
        Assertions.assertEquals(List.of(film), filmController.getPopularFilms(1));
    }

    @Test
    void deleteFilmTest() throws ValidationException, FilmNotFoundException {
        filmController.addFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size());
        filmController.deleteFilm(film.getId());
        Assertions.assertEquals(0, filmController.getAllFilms().size());
    }
}
