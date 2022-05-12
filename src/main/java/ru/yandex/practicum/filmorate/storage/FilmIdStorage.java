package ru.yandex.practicum.filmorate.storage;

public class FilmIdStorage {
    static long id = 1;

    public long generateFilmId() {
        return id++;
    }
}
