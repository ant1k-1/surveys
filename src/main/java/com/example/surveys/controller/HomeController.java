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
        if (auth.isAuthenticated()) {
            //TODO: тут баг, надо сделать прогрузку доступных опросов по кнопке, иначе они грузят раньше, чем пользователь авторизуется
            model.addAttribute("user", userService.getUserDTO(auth.getName()));
            List<Survey> availableSurveys = surveyService.getAvailableSurveys(auth.getName());
            System.out.println(availableSurveys.size());
            for (var s :
                    availableSurveys) {
                System.out.println(s.getId());
            }
            model.addAttribute("surveys", availableSurveys);
        }
        return "home";
    }



    //TODO: Продолжить дрочку с опросами
}
