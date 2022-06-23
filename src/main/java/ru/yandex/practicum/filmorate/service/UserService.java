package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserIdStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage databaseUserStorage, FriendshipStorage databaseFriendshipStorage) {
        this.userStorage = databaseUserStorage;
        this.friendshipStorage = databaseFriendshipStorage;
    }

    public void addFriend(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);
        friendshipStorage.save(Friendship
                .builder()
                .user(getUserById(id))
                .friend(getUserById(friendId))
                .build());
    }

    public void deleteFriend(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);
        friendshipStorage.delete(Friendship
                .builder()
                .user(getUserById(id))
                .friend(getUserById(friendId))
                .build());
    }

    public List<User> getFriends(Long id) throws UserNotFoundException {
        checkNullUser(id);
        return friendshipStorage.getFriendsIds(getUserById(id).getId())
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long friendId) throws UserNotFoundException {
        checkNullUser(id);
        checkNullUser(friendId);

        Set<Long> intersection = new HashSet<>(friendshipStorage.getFriendsIds(id));
        intersection.retainAll(friendshipStorage.getFriendsIds(friendId));

        return intersection
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(Long id) throws UserNotFoundException {
        User user =  userStorage.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        } else return user;
    }

    public User create(User user) throws ValidationException {
        return userStorage.create(user);
    }

    public User update(User newUser) throws ValidationException, UserNotFoundException {
        final User oldUser = getUserById(newUser.getId());
        if (oldUser.equals(newUser)) {
            return newUser;
        }
        return userStorage.update(newUser);
    }

    public void deleteUser(Long id) throws UserNotFoundException {
        userStorage.deleteUser(getUserById(id));
    }

    private void checkNullUser(Long id) throws UserNotFoundException {
        if (userStorage.getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%s", id));
        }
    }
}