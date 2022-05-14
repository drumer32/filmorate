package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    User create(User user) throws ValidateException, UserAlreadyExistException;

    User update(User user) throws ValidateException, UserNotFoundException;

    Optional<User> getUserById(Long id);
}
