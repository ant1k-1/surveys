package com.example.surveys.controller;

import com.example.surveys.dto.WithdrawDTO;
import com.example.surveys.service.SurveyService;
import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/profile")
@Controller
public class ProfileController {
    private final UserService userService;
    private final SurveyService surveyService;

    @Autowired
    public ProfileController(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @GetMapping
    public String profile(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        model.addAttribute("business_surveys",
                surveyService.getAllSurveysByBusinessLogin(auth.getName()));
        model.addAttribute("user", userService.getUserDTO(auth.getName()));
        model.addAttribute("withdraw", new WithdrawDTO());
        return "profile";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @ModelAttribute("withdraw") WithdrawDTO withdrawCredentials
    ) {
        userService.withdraw(auth.getName());
        return "redirect:/profile";
    }

    @PostMapping("/deposit")
    public String deposit(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam("deposit") String deposit
    ) {
        userService.deposit(auth.getName(), deposit);
        return "redirect:/profile";
    }

    @PostMapping("/edit")
    public String editProfile(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> userData
    ) {
        userService.editProfile(auth.getName(), userData);
        return "redirect:/profile";
    }
    @PostMapping("/password")
    public String changePassword(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> userData
    ) {
        userService.changePassword(auth.getName(), userData.get("oldPassword"), userData.get("newPassword"));
        return "redirect:/profile";
    }
}
