package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * создание пользователя;
 * обновление пользователя;
 * получение списка всех пользователей.
 */

@RestController
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Map<Integer, User> findAllUsers() {
        return users;
    }

    @PostMapping(value = "/users")
    public void create(@Validated @RequestBody User user) {
        if (!validation(user)) {
            throw new ValidationException("Ошибка валидации. Проверьте введенные данные");
        } else {
            users.put(user.getUserId(), user);
            log.info("Пользователь добавлен {}", user);
        }
    }

    @PutMapping(value = "/users")
    public void update(@Validated @RequestBody User user) {
        if (!validation(user)) {
            throw new ValidationException("Ошибка валидации. Проверьте введенные данные");
        } else {
            if (users.containsKey(user.getUserId())) {
                User oldUser = users.get(user.getUserId());
                oldUser.setName(user.getName());
                oldUser.setEmail(user.getEmail());
                oldUser.setLogin(user.getLogin());
                oldUser.setBirthday(user.getBirthday());
                log.info("Пользователь обновлен {}", user);
            } else {
                System.out.println("Пользователь не найден");
                log.info("Пользователь не найден {}", user);
            }
        }
    }

    /**
     * электронная почта не может быть пустой и должна содержать символ @;
     * логин не может быть пустым и содержать пробелы;
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     * дата рождения не может быть в будущем.
     */
    private boolean validation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            return false;
        } else if (!user.getEmail().contains("@")) {
            return false;
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            return false;
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        } else return true;
    }
}
