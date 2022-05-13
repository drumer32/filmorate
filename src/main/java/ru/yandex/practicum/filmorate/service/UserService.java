package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
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

    public void addFriend(Long id, Long friendId) {
        checkNullUser(id);
        checkNullUser(friendId);
        makeFriends(id, friendId);
        makeFriends(friendId, id);
    }

    public void deleteFriend(Long id, Long friendId) {
        checkNullUser(id);
        checkNullUser(friendId);
        makeNotFriends(id, friendId);
        makeNotFriends(friendId, id);
    }

    public List<User> getFriends(Long id) {
        checkNullUser(id);
        return friends.getOrDefault(id, new HashSet<>()).stream()
                .map(x -> userStorage.getUserById(x).get())
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long friendId) {
        checkNullUser(id);
        checkNullUser(friendId);
        Set<Long> commonFriends = new TreeSet<>();
        commonFriends.addAll(friends.get(id));
        commonFriends.addAll(friends.get(friendId));
        return commonFriends.stream()
                .map(x -> userStorage.getUserById(x).get())
                .collect(Collectors.toList());
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

    private void checkNullUser(Long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }
}