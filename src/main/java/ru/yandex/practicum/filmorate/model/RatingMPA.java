package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class RatingMPA {

    Integer id;

    @JsonProperty("name")
    @NotBlank String title;
}


