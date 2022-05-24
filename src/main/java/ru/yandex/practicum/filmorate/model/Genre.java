package ru.yandex.practicum.filmorate.model;

public enum Genre {
    ACTION  ("Боевик"),
    ADVENTURE("Приключенческий фильм"),
    CARTOON ("Мультфильм"),
    CATASTROPHE ("Фильм-катастрофа"),
    COMEDY ("Комедия"),
    DETECTIVE ("Детектив"),
    DOCUMENTARY ("Документальный"),
    DRAMA ("Драма"),
    FANTASY ("Фантастический фильм"),
    HISTORICAL ("Исторический фильм"),
    HORROR ("Фильм ужасов"),
    MELODRAMA ("Мелодрама"),
    MUSICAL ("Музыкальный фильм"),
    NOIR ("Нуар"),
    TRAGEDY ("Трагедия"),
    THRILLER ("Триллер"),
    WESTERN ("Вестерн");

    private String genre;

    Genre(String genre) {
        this.genre = genre;
    }
}
