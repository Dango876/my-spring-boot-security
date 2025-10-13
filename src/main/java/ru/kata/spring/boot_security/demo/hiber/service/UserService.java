package ru.kata.spring.boot_security.demo.hiber.service;

import java.util.List;
import java.util.Optional;

import ru.kata.spring.boot_security.demo.hiber.model.User;

public interface UserService {
    void saveUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByName(String name);

    void deleteUser(Long id);
}
