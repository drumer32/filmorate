package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    private final User user = User.builder()
            .id(1L)
            .email("email@email.ru")
            .login("login")
            .name("TestName")
            .birthday(LocalDate.of(1985, 1, 1))
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .email("email2@email2.ru")
            .login("login2")
            .name("TestName2")
            .birthday(LocalDate.of(1985, 1, 1))
            .build();

    private final User user3 = User.builder()
            .id(3L)
            .email("email3@email3.ru")
            .login("login3")
            .name("TestName3")
            .birthday(LocalDate.of(1985, 1, 1))
            .build();

    @Test
    void addUserTest() throws ValidationException {
        userController.addUser(user);
        System.out.println(userController.getAllUsers());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void userUpdateTest() throws ValidationException, UserNotFoundException {
        final User user2 = userController.addUser(user);
        final User user3 = user2.toBuilder().login("test3").build();
        userController.updateUser(user3);
        assertEquals(user3, userController.getUserById(user3.getId()));
    }

    @Test
    void addFriendTest() throws UserNotFoundException, ValidationException {
        userController.addUser(user);
        userController.addUser(user2);
        userController.addFriend(user.getId(), user2.getId());
        assertEquals(List.of(user2), userController.getUserFriends(user.getId()));
    }

    @Test
    void getUserByIdTest() throws ValidationException, UserNotFoundException {
        userController.addUser(user);
        userController.addUser(user2);
        assertEquals(user, userController.getUserById(user.getId()));
    }

    @Test
    void getAllUsersTest() throws ValidationException {
        userController.addUser(user);
        userController.addUser(user2);
        assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void getUserFriendsTest() throws ValidationException, UserNotFoundException {
        userController.addUser(user);
        userController.addUser(user2);
        userController.addUser(user3);
        userController.addFriend(user.getId(), user2.getId());
        userController.addFriend(user.getId(), user3.getId());
        assertEquals(2, userController.getUserFriends(user.getId()).size());
    }
    @Test
    void getCommonFriendsTest() throws ValidationException, UserNotFoundException {
        userController.addUser(user);
        userController.addUser(user2);
        userController.addUser(user3);
        userController.addFriend(user2.getId(), user.getId());
        userController.addFriend(user3.getId(), user.getId());
        assertEquals(List.of(user), userController.getCommonFriends(user2.getId(), user3.getId()));
    }
    @Test
    void deleteUserTest() throws ValidationException, UserNotFoundException {
        userController.addUser(user);
        userController.deleteUser(user.getId());
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(user.getId()));
    }
    @Test
    void deleteFriendTest() throws UserNotFoundException, ValidationException {
        userController.addUser(user);
        userController.addUser(user2);
        userController.addFriend(user2.getId(), user.getId());
        assertEquals(List.of(user), userController.getUserFriends(user2.getId()));
        userController.deleteFriend(user2.getId(), user.getId());
        assertEquals(0 ,userController.getUserFriends(user2.getId()).size());
    }

}
