package ru.kata.spring.boot_security.demo.hiber.service;

import ru.kata.spring.boot_security.demo.hiber.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> getRoleByName(String name);

    void save(Role role);
}
