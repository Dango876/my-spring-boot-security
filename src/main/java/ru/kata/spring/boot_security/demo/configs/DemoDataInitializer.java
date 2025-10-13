package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import ru.kata.spring.boot_security.demo.hiber.model.Role;
import ru.kata.spring.boot_security.demo.hiber.model.User;
import ru.kata.spring.boot_security.demo.hiber.service.RoleService;
import ru.kata.spring.boot_security.demo.hiber.service.UserService;

@Component
public class DemoDataInitializer implements ApplicationRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DemoDataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userService.getUserByName("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword("admin");
            Role adminRole = roleService.getRoleByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role("ROLE_ADMIN");
                        roleService.saveRole(role);
                        return role;
                    });
            admin.getRoles().add(adminRole);
            userService.saveUser(admin);
        }

        if (userService.getUserByName("user").isEmpty()) {
            User user = new User();
            user.setName("user");
            user.setPassword("user");
            Role adminRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role role = new Role("ROLE_USER");
                        roleService.saveRole(role);
                        return role;
                    });
            user.getRoles().add(adminRole);
            userService.saveUser(user);
        }

    }
}
