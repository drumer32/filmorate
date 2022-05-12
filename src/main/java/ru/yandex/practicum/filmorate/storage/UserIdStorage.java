package ru.yandex.practicum.filmorate.storage;

public class UserIdStorage {
    static long id = 1;

    public long generateId() {
        return id++;
    }
}
