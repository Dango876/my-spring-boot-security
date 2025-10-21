package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public String getUserInfo(Model model, Principal principal) {
        User user = userService.getUserByName(principal.getName());
        model.addAttribute("user", user);
        return "user/userInfo";
    }

    @GetMapping("/update")
    public String showUpdateUserForm(Model model, Principal principal) {
        User user = userService.getUserByName(principal.getName());
        model.addAttribute("user", user);
        return "user/userForm";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user, Principal principal) {
        User currentUser = userService.getUserByName(principal.getName());
        user.setId(currentUser.getId());
        userService.updateUser(user);
        return "redirect:/users/info";
    }
}
