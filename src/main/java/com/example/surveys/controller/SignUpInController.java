package com.example.surveys.controller;

import com.example.surveys.model.User;
import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignUpInController {
    private final UserService userService;

    @Autowired
    public SignUpInController(UserService userService) {
        this.userService = userService;
    }

    // Редирект на / если юзер авторизован, чтобы он не заходил на страницу авторизации
    @GetMapping("/sign-in")
    public String showLoginForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "sign-in";
        }
        return "redirect:/";
    }

    // Редирект на / если юзер авторизован, чтобы он не заходил на страницу регистрации
    @GetMapping("/sign-up")
    public String signUp(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("user", new User());
            return "sign-up";
        }
        return "redirect:/";
    }

    @PostMapping("/sign-up")
    public String create(@ModelAttribute("user") User user) {
        userService.create(user);
        System.out.println(user);
        return "redirect:/sign-in";
    }

}
