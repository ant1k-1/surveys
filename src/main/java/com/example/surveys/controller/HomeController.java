package com.example.surveys.controller;

import com.example.surveys.model.User;
import com.example.surveys.model.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/home"})
    public String welcome(Model model) {
        return "home";
    }
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
