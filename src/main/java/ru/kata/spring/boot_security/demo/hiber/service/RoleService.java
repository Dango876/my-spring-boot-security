package ru.kata.spring.boot_security.demo.hiber.service;

import java.util.List;
import java.util.Optional;

import ru.kata.spring.boot_security.demo.hiber.model.Role;

public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> getRoleByName(String name);

    void saveRole(Role role);
}
