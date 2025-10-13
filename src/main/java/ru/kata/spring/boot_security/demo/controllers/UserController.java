package ru.kata.spring.boot_security.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.kata.spring.boot_security.demo.hiber.model.User;
import ru.kata.spring.boot_security.demo.hiber.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String userHome() {
        return "redirect:/";
    }

    @GetMapping("/info")
    public String userInfo(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String name = ((UserDetails) principal).getUsername();
            Optional<User> user = userService.getUserByName(name);

            if (user.isEmpty()) {
                return "userNotFound";
            }

            model.addAttribute("user", user.get());
            return "user/userinfo";
        }
        return "userNotFound";
    }

    @GetMapping("/update")
    public String updateUserFrom(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return  "userNotFound";
        }
        model.addAttribute("user", user.get());
        return "user/userFrom";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/users/info";
    }
}
