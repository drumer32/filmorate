package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


/**
 * электронная почта не может быть пустой и должна содержать символ @;
 * логин не может быть пустым и содержать пробелы;
 * имя для отображения может быть пустым — в таком случае будет использован логин;
 * дата рождения не может быть в будущем.
 */

public class UserControllerTest {

    private final UserController userController = new UserController();
    static User user;

    @BeforeEach
    void generateUser() {
        user = new User(
                1,
                "email@email.com",
                "login",
                "name",
                LocalDate.of(1996,1,11));
    }

    @Test
    void userCreationTest() throws ValidationException {
        userController.create(user);
        System.out.println(userController.findAllUsers());
        Assertions.assertEquals(1, userController.findAllUsers().size());
    }

    @Test
    void userUpdateTest() throws ValidationException {
        User newUser = new User(
                1,
                "email@email.com",
                "login",
                "nameNew",
                LocalDate.of(1996,1,11));
        userController.create(user);
        userController.update(newUser);
        Assertions.assertEquals("nameNew", userController.findAllUsers().get(1).getName());
    }

    @Test
    void userNameValidationTest() {
        user.setLogin("name new");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }
    @Test
    void userEmailValidationTest() {
        user.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void userLoginValidationTest() {
        user.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void userBirthdayValidationTest() {
        user.setBirthday(LocalDate.of(2030,12,12));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }
}
