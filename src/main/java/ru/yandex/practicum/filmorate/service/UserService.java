package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Map<Long, Set<Long>> friends = new HashMap<>();

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
        return friends.getOrDefault(id, new HashSet<>()).stream()
                .map(x -> userStorage.getUserById(x).get())
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);
        List<User> result = new ArrayList<>(getFriends(id));
        List<User> result2 = new ArrayList<>(getFriends(friendId));
        Set<User> all = new HashSet<>(result);
        all.addAll(result2);
        System.out.println(all.size());
        return new ArrayList<>(all);
    }

    private void makeFriends(Long userId, Long friendId) {
        Set<Long> userFriends = friends.getOrDefault(userId, new HashSet<>());
        userFriends.add(friendId);
        friends.put(userId, userFriends);
    }

    private void makeNotFriends(Long userId, Long friendId) {
        Set<Long> userFriends = friends.get(userId);
        if (!(userFriends == null)) {
            userFriends.remove(friendId);
            friends.put(userId, userFriends);
        } else {
            throw new NullPointerException("Друзей нет");
        }
    }

    private void checkNullUser(Long id) throws UserNotFoundException {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) throws ValidateException, UserAlreadyExistException {
        return userStorage.create(user);
    }

    public User update(User user) throws UserNotFoundException, ValidateException {
        return userStorage.update(user);
    }
}