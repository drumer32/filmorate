package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

public interface FriendshipStorage {

    Collection<Long> getFriendsIds(Long id);

    void save(Friendship friendship);

    void delete(Friendship friendship);
}
