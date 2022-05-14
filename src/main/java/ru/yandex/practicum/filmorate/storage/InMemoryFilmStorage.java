package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) throws ValidateException, FilmAlreadyExistException {
        validate(film);
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException(String.format("Фильм %s уже создан", film.getName()));
        } else {
            film.setId(filmIdStorage.generateFilmId());
            films.put(film.getId(), film);
            log.info("Фильм добавлен {}", film);
        }
        return film;
    }

    @Override
    public Film update(Film film) throws ValidateException, FilmNotFoundException {
        validate(film);
        if (!(films.containsKey(film.getId()))) {
            throw new FilmNotFoundException(String.format("Фильм %s не найден ", film.getName()));
        } else {
            films.put(film.getId(), film);
            log.info("Фильм обновлен {}", film);
        }
        return film;
    }

    private void validate (Film film) throws ValidateException {
        if (!StringUtils.hasText(film.getName())) {
            log.debug("Название фильма не должно быть пустым - {}", film.getName());
            throw new ValidateException("Название фильма не должно быть пустым");
        } else if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.debug("Описание фильма должно быть не длиннее 200 символов - {}", film.getName());
            throw new ValidateException("Описание фильма должно быть не длиннее 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза не должна быть раньше 1895-12-28 - {}", film.getName());
            throw new ValidateException("Дата релиза не должна быть раньше 1895-12-28");
        } else if (film.getDuration() < 0) {
            log.debug("Длительность фильма не может быть отрицательной - {}", film.getName());
            throw new ValidateException("Длительность фильма не может быть отрицательной");
        }
    }
}
