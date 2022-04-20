package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * целочисленный идентификатор;
 * название;
 * описание;
 * дата релиза;
 * продолжительность фильма.
 */
@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @PastOrPresent
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
