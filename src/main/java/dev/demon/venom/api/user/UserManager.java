package dev.demon.venom.api.user;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Getter
public class UserManager {
    private Map<UUID, User> users = Collections.synchronizedMap(new ConcurrentHashMap<>());

    public User getUser(UUID uuid) {
        return users.getOrDefault(uuid, null);
    }

    public void addUser(User user) {
        if (!users.containsKey(user.getUuid())) {
            users.put(user.getUuid(), user);
        }
    }

    public void removeUser(User user) {
        users.remove(user.getUuid());
    }

    public List<User> userMapToList() {
        return new ArrayList<>(this.users.values());
    }
}