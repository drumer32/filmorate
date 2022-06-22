package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
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
    public User getUserById(Long id) {
        return (users.get(id));
    }

    @Override
    public User create(User user) throws ValidateException, UserAlreadyExistException {
        validate(user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя не должно быть пустым - {}", user.getName());
        }
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
    public User update(User user) throws ValidateException, UserNotFoundException {
        validate(user);
        if (!(users.containsKey(user.getId()))) {
            throw new UserNotFoundException(String.format("Пользователь %s не найден", user.getName()));
        } else {
            users.put(user.getId(), user);
            log.info("Пользователь обновлен {}", user);
        }
        return user;
    }

    private void validate (User user) throws ValidateException {
       if (!user.getEmail().contains("@")) {
            log.debug("Адрес email веден некорректно - {}", user.getName());
            throw new ValidateException("Адрес email веден некорректно");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Логин пустой или содержит пробелы - {}", user.getName());
            throw new ValidateException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Ошибка валидации пользователя - {}", user.getName());
            throw new ValidateException("Дата рождения должна быть не позже " + LocalDate.now());
        }
    }
}
