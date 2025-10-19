package ru.kata.spring.boot_security.demo.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.persistence.EntityNotFoundException;

@Component
public class UserDataInitializer implements ApplicationRunner {
    private final UserService userService;
    private final RoleService roleService;

    public UserDataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            userService.getUserByName("admin");
        } catch (EntityNotFoundException e) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword("admin");

            Role adminRole;
            try {
                adminRole = roleService.getRoleByName("ROLE_ADMIN");
            } catch (EntityNotFoundException ex) {
                adminRole = new Role("ROLE_ADMIN");
                roleService.saveRole(adminRole);
            }
            admin.getRoles().add(adminRole);

            userService.saveUser(admin);
        }

        try {
            userService.getUserByName("user");
        } catch (EntityNotFoundException e) {
            User user = new User();
            user.setName("user");
            user.setPassword("user");

            Role userRole;
            try {
                userRole = roleService.getRoleByName("ROLE_USER");
            } catch (EntityNotFoundException ex) {
                userRole = new Role("ROLE_USER");
                roleService.saveRole(userRole);
            }
            user.getRoles().add(userRole);

            userService.saveUser(user);
        }
    }
}
