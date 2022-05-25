package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);
        makeFriends(id, friendId);
        makeFriends(friendId, id);
    }

    public void deleteFriend(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);
        makeNotFriends(id, friendId);
        makeNotFriends(friendId, id);
    }

    public List<User> getFriends(Long id) throws UserNotFoundException {
        checkNullUser(id);
        List<User> friends = new ArrayList<>();
        Set<Long> friendsIds = userStorage.getUserById(id).getFriends();
        for (Long i : friendsIds) {
            friends.add(userStorage.getUserById(i));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);

        List<User> friends = new ArrayList<>(getFriends(id));
        List<User> friendsOfFriend = new ArrayList<>(getFriends(friendId));
        List<User> mutual = new ArrayList<>(friends);
        mutual.retainAll(friendsOfFriend);
        return mutual;
    }

    private void makeFriends(Long userId, Long friendId) {
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    private void makeNotFriends(Long userId, Long friendId) {
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    private void checkNullUser(Long id) throws UserNotFoundException {
        if (userStorage.getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) throws ValidateException, UserAlreadyExistException {
        return userStorage.create(user);
    }

    public User update(User user) throws UserNotFoundException, ValidateException {
        return userStorage.update(user);
    }
}