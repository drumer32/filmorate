package ru.yandex.practicum.filmorate.storage;

public class UserIdStorage {
    static Long id = 0L;

    public static Long generateId() {
        return ++id;
    }
}
