package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String s) {
        super(s);
    }
}
