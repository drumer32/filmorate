package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * создание пользователя;
 * обновление пользователя;
 * получение списка всех пользователей.
 */

@RestController
@Slf4j
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Map<Integer, User> findAllUsers() {
        return users;
    }

    @PostMapping(value = "/users")
    public String create(@Validated @RequestBody User user) {
        String response = "";
        try {
            validation(user);
            users.put(user.getUserId(), user);
            response = ("Пользователь " + user.getUserId() + " добавлен");
            log.info("Пользователь добавлен {}", user);
        } catch (ValidationException e) {
            log.info(e.getMessage() + user);
            response = e.getMessage();
        }
        return response;
    }

    @PutMapping(value = "/users")
    public String update(@Validated @RequestBody User user) throws ValidationException {
        String response = "";
        try {
            validation(user);
            if (users.containsKey(user.getUserId())) {
                User oldUser = users.get(user.getUserId());
                oldUser.setName(user.getName());
                oldUser.setEmail(user.getEmail());
                oldUser.setLogin(user.getLogin());
                oldUser.setBirthday(user.getBirthday());
                response = ("Пользователь c id " + user.getUserId() + " обновлен");
                log.info("Пользователь обновлен {}", user);
            } else {
                response = "Пользователь не найден";
                log.info("Пользователь не найден {}", user);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage() + user);
            response = e.getMessage();
        }
        return response;
    }

    /**
     * электронная почта не может быть пустой и должна содержать символ @;
     * логин не может быть пустым и содержать пробелы;
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     * дата рождения не может быть в будущем.
     */
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
