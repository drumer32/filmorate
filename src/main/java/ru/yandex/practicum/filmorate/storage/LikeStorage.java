package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeStorage {

    Collection<Film> getPopularFilms(Integer limit);

    void saveLike(Like like);

    void deleteLike(Like like);
}