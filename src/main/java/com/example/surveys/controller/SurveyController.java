package com.example.surveys.controller;

import com.example.surveys.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/survey")
@Controller
public class SurveyController {
    private final SurveyService surveyService;

    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    @GetMapping
    public String test(){
        return "survey";
    }

    @GetMapping("/{uuid}")
    public String getSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @RequestParam Map<String, String> map
    ) {
        model.addAttribute("business_action", "creation");
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/view/{id}")
    public String viewSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String id
    ) {
        model.addAttribute("business_action", "view");
        return "survey";
    }


}
