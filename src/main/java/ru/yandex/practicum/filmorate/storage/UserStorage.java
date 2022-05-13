package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException;

    Optional<User> getUserById(Long id);
}
