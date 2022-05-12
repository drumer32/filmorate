package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();
    private FilmIdStorage filmIdStorage = new FilmIdStorage();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = null;
        try {
            film = films.get(id);
        } catch (FilmNotFoundException e) {
            log.info(e.getMessage() + id);
            throw new FilmNotFoundException(e.getMessage());
        }
        return film;
    }

    @Override
    public Film create(Film film) throws ValidationException {
        try {
            validation(film);
            film.setId(filmIdStorage.generateFilmId());
            films.put(film.getId(), film);
            log.info("Фильм добавлен {}", film);
        } catch (ValidationException e) {
            log.info(e.getMessage() + film);
            throw new ValidationException(e.getMessage());
        }
        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        try {
            validation(film);
            if (films.containsKey(film.getId())) {
                Film oldFilm = films.get(film.getId());
                oldFilm.setName(film.getName());
                oldFilm.setDescription(film.getDescription());
                oldFilm.setDuration(film.getDuration());
                oldFilm.setReleaseDate(film.getReleaseDate());
                log.info("Фильм обновлен {}", film);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage() + film);
            throw new ValidationException(e.getMessage());
        }
        return film;
    }

    private void validation(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма должно быть не длиннее 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не должна быть раньше 1895-12-28");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
    }
}
