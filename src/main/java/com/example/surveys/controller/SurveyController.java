package com.example.surveys.controller;

import com.example.surveys.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
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
        //TODO: сделать отображение опроса.
        //TODO: Сначала идет запрос на /id, там создается completedSurvey с uuid, redirect:/uuid
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        return "createSurvey";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @PostMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @RequestParam Map<String, String> map,
            @RequestParam("pics") MultipartFile[] pics
            ) {
        for (var key : map.keySet()) {
            System.out.println(key + '=' + map.get(key));
        }
        System.out.println(Arrays.toString(pics));
        //TODO:обработать данные и создать опрос

        return "redirect:/create";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/view/{id}")
    public String viewSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String id
    ) {

        return "survey";
    }


}
