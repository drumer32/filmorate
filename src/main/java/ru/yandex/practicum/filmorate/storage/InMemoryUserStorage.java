package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private UserIdStorage userIdStorage = new UserIdStorage();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) throws ValidationException {
        validation(user);
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format("Пользователь с именем %s уже существует.", user.getName()));
        } else {
            user.setId(userIdStorage.generateId());
            users.put(user.getId(), user);
            log.info("Пользователь добавлен {}", user);
        }
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        validation(user);
        if (!(users.containsKey(user.getId()))) {
            throw new UserNotFoundException(String.format("Пользователь %s не найден", user.getName()));
        } else {
            users.put(user.getId(), user);
            log.info("Пользователь обновлен {}", user);
        }
        return user;
    }

    private void validation(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя не должно быть пустым - {}", user.getName());
            throw new ValidationException("Имя не должно быть пустым");
        } else if (!user.getEmail().contains("@")) {
            log.debug("Адрес email веден некорректно - {}", user.getName());
            throw new ValidationException("Адрес email веден некорректно");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Логин пустой или содержит пробелы - {}", user.getName());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Ошибка валидации пользователя - {}", user.getName());
            throw new ValidationException("Дата рождения должна быть не позже " + LocalDate.now());
        }
    }
}
