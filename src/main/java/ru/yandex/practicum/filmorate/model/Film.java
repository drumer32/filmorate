package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Film {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    @Size(max = 200)
    String description;

    @Min(0)
    Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

    @NonNull
    RatingMPA mpa;

    Set<Genre> genres;
}
