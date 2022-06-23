package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.impl.DatabaseMPAStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final DatabaseMPAStorage databaseMPAStorage;

    @GetMapping
    Collection<RatingMPA> getAll() {
        return databaseMPAStorage.getAll();
    }

    @GetMapping("{id}")
    RatingMPA get(@PathVariable final int id) {
        RatingMPA ratingMPA = databaseMPAStorage.get(id);
        if (ratingMPA == null) throw new NoSuchElementException();
        return ratingMPA;
    }
}