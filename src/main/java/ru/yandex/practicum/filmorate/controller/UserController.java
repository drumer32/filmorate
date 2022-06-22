package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserIdStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        final User newUser = validation(user);
        log.debug("Запрос на добавление пользователя - {}", newUser.getLogin());
        userService.create(newUser);
        return newUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) throws UserNotFoundException {
        userService.addFriend(id, friendId);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException, UserNotFoundException {
        final User newUser = validation(user);
        log.debug("Запрос на обновление пользователя - {}", newUser.getLogin());
        userService.update(newUser);
        return newUser;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) throws UserNotFoundException {
        User user = userService.getUserById(id);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) throws UserNotFoundException {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) throws UserNotFoundException {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) throws UserNotFoundException {
        userService.deleteFriend(id, friendId);
    }

    private User validation(User user) {
        if (user.getId() == null) {
            user = user.toBuilder().id(UserIdStorage.generateId()).build();
        }

        if (user.getName() == null || user.getName().equals("")) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }
}

