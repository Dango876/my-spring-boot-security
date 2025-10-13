package ru.kata.spring.boot_security.demo.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.kata.spring.boot_security.demo.hiber.model.User;
import ru.kata.spring.boot_security.demo.hiber.service.RoleService;
import ru.kata.spring.boot_security.demo.hiber.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String allUser(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        if (userList.isEmpty()) {
            model.addAttribute("isEmpty", true);
        }
        return "admin/allUsers";
    }

    @GetMapping("/info")
    public String userInfo(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return "userNotFound";
        }
        model.addAttribute("user", user.get());
        return "admin/userFrom";
    }

    @GetMapping("/update")
    public String updateUserFrom(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return "userNotFound";
        }
        model.addAttribute("user", user.get());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/userFrom";
    }

    @GetMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        if (user.getId() == null) {
            userService.saveUser(user);
        } else {
            userService.updateUser(user);
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Principal principal) {
        User userInSession = userService.getUserByName(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(id);

        if (userInSession.getId().equals(id)) {
            return "redirect:/login";
        }
        return "redirect:/admin";
    }
}