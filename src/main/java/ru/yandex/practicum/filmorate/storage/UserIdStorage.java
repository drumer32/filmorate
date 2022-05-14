package ru.yandex.practicum.filmorate.storage;

public class UserIdStorage {
    static long id = 0;

    public long generateId() {
        return ++id;
    }
}
