package com.example.surveys.controller;

import com.example.surveys.model.Survey;
import com.example.surveys.service.SurveyService;
import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;
    private final SurveyService surveyService;

    @Autowired
    public HomeController(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @GetMapping({"/", "/home"})
    public String welcome(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        model.addAttribute("user", userService.getUserDTO(auth.getName()));
        model.addAttribute("surveys", surveyService.getAvailableSurveys(auth.getName()));

        return "home";
    }
}
