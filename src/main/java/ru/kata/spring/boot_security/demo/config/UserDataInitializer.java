package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class UserDataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserDataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
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

            Role userRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role role = new Role("ROLE_USER");
                        roleService.saveRole(role);
                        return role;
                    });
            user.getRoles().add(userRole);

            userService.saveUser(user);
        }
    }
}
