package com.example.surveys.controller;

import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping({"/", "/home"})
    public String welcome(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        if (auth.isAuthenticated()) {
            model.addAttribute("user", userService.getUserDTO(auth.getName()));
        }
        return "home";
    }

    //TODO: Начать дрочку с опросами
}
