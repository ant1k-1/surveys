package com.example.surveys.controller;

import com.example.surveys.dto.SurveyDTO;
import com.example.surveys.model.Question;
import com.example.surveys.service.CompletedSurveyService;
import com.example.surveys.service.SurveyService;
import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("/survey")
@Controller
public class SurveyController {
    private final SurveyService surveyService;
    private final UserService userService;
    private final CompletedSurveyService completedSurveyService;
    @Autowired
    public SurveyController(SurveyService surveyService,
                            UserService userService,
                            CompletedSurveyService completedSurveyService) {
        this.surveyService = surveyService;
        this.userService = userService;
        this.completedSurveyService = completedSurveyService;
    }
    @GetMapping
    public String test(){

        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/start/{id}")
    public String getSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String id) {
        return "redirect:/survey/start/" + id + "/" + completedSurveyService.newSurvey(id, auth.getName());
    }

    @GetMapping("/{id}/error/")
    public String errorSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        model.addAttribute("error", true);
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/start/{id}/{uuid}")
    public String startSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String uuid,
            @PathVariable String id
    ) {
        SurveyDTO surveyDTO = surveyService.getSurveyDtoById(Long.valueOf(id));
        model.addAttribute("survey", surveyDTO);
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping


    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        model.addAttribute("user", userService.getUserDTO(auth.getName()));
        return "createSurvey";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @PostMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @RequestParam Map<String, String> map,
            @RequestParam(name = "pics", required = false) MultipartFile[] pics
            ) {
//        for (var key : map.keySet()) {
//            System.out.println(key + "=" + map.get(key));
//        }
        int survey_id = Math.toIntExact(surveyService.createSurvey(map, pics, auth.getName()));
        System.out.println("Survey creation status: " + survey_id);
        if (survey_id > 0) {
            return "redirect:/survey/view/" + survey_id;
        } else {
            return "redirect:/survey/create";
        }

    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/view/id/{id}")
    public String viewSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String id
    ) {
        SurveyDTO surveyDTO = surveyService.getSurveyDtoById(Long.valueOf(id));
        model.addAttribute("survey", surveyDTO);
        return "survey";
    }

    @GetMapping("/view/uuid/{uuid}")
    public String viewCompletedSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String uuid
    ) {

    }

}
