package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

/**
 * целочисленный идентификатор;
 * электронная почта;
 * логин пользователя;
 * имя для отображения;
 * дата рождения.
 */
@Data
@AllArgsConstructor
public class User {

    private Long userId;

    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
