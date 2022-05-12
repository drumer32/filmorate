package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

public class UserControllerTest {

    private FilmStorage filmStorage = new InMemoryFilmStorage();
    private UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(userStorage);
    private UserController userController = new UserController(userStorage, userService);
    static User user;

    @BeforeEach
    void generateUser() {
        user = new User(
                1L,
                "email@email.com",
                "login",
                "name",
                LocalDate.of(1996,1,11));
    }

    @Test
    void userCreationTest() throws ValidationException {
        userController.addUser(user);
        System.out.println(userController.getAllUsers());
        Assertions.assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void userUpdateTest() throws ValidationException {
        User newUser = new User(
                1L,
                "email@email.com",
                "login",
                "nameNew",
                LocalDate.of(1996,1,11));
        userController.addUser(user);
        userController.updateUser(newUser);
        Assertions.assertEquals("nameNew", userController.getUserById(newUser.getUserId()).getName());
    }

    @Test
    void userNameValidationTest() {
        user.setLogin("name new");
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }
    @Test
    void userEmailValidationTest() {
        user.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void userLoginValidationTest() {
        user.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void userBirthdayValidationTest() {
        user.setBirthday(LocalDate.of(2030,12,12));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }
}
