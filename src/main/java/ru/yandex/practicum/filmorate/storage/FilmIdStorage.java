package ru.yandex.practicum.filmorate.storage;

public class FilmIdStorage {
    static long id = 0;

    public long generateFilmId() {
        return ++id;
    }
}
