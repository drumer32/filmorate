package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    void create(User user) throws ValidationException;

    void update(User user) throws ValidationException;

    User getUserById(Long id);

    void deleteUser(User user);
}
