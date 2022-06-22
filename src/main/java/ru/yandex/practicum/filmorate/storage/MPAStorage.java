package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.Collection;

public interface MPAStorage {

    Collection<RatingMPA> getAll();

    RatingMPA get(int id);
}
