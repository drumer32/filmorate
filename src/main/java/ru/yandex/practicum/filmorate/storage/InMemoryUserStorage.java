package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public User create(User user) throws ValidationException {
        try {
            validation(user);
            user.setUserId(userIdStorage.generateId());
            users.put(user.getUserId(), user);
            log.info("Пользователь добавлен {}", user);
        } catch (ValidationException e) {
            log.info(e.getMessage() + user);
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        try {
            validation(user);
            if (users.containsKey(user.getUserId())) {
                User oldUser = users.get(user.getUserId());
                oldUser.setName(user.getName());
                oldUser.setEmail(user.getEmail());
                oldUser.setLogin(user.getLogin());
                oldUser.setBirthday(user.getBirthday());
                log.info("Пользователь обновлен {}", user);
            } else {
                log.info("Пользователь не найден {}", user);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage() + user);
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user;
        try {
            user = users.get(id);
        } catch (UserNotFoundException e) {
            log.info(e.getMessage() + id);
            throw new UserNotFoundException(e.getMessage());
        }
        return user;
    }

    private void validation(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            throw new ValidationException("Имя не должно быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Адрес email веден некорректно");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения должна быть не позже " + LocalDate.now());
        }
    }
}
