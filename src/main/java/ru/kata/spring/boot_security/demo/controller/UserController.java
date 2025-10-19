package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public String userInfo(Model model, Principal principal) {
        User user = userService.getUserByName(principal.getName());
        model.addAttribute("user", user);
        return "user/userInfo";
    }

    @GetMapping("/update")
    public String updateUserForm(@RequestParam("id") Long id, Model model, Principal principal) {
        try {
            User currentUser = userService.getUserByName(principal.getName());
            if (!currentUser.getId().equals(id)) {
                return "accessDenied";
            }

            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "user/userForm";
        } catch (EntityNotFoundException e) {
            return "userNotFound";
        }
    }

    @PostMapping("/update")
    public String saveUser(@ModelAttribute("user") User user, Principal principal) {
        try {
            User currentUser = userService.getUserByName(principal.getName());
            if (!currentUser.getId().equals(user.getId())) {
                return "accessDenied";
            }
            userService.updateUser(user);
            return "redirect:/users/info";
        } catch (EntityNotFoundException e) {
            return "userNotFound";
        }
    }
}
