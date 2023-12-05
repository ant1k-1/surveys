package com.example.surveys.controller;

import com.example.surveys.dto.SurveyDTO;
import com.example.surveys.model.Question;
import com.example.surveys.model.Survey;
import com.example.surveys.repository.QuestionRepository;
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

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

@RequestMapping("/survey")
@Controller
public class SurveyController {
    private final SurveyService surveyService;
    private final UserService userService;
    //TODO: потом убрать!
    private final QuestionRepository questionRepository;
    @Autowired
    public SurveyController(SurveyService surveyService, UserService userService, QuestionRepository questionRepository) {
        this.surveyService = surveyService;
        this.userService = userService;
        this.questionRepository = questionRepository;
    }
    @GetMapping
    public String test(){
        Question question = questionRepository.findById(5L).get();
        System.out.println(question.getDescription());
        return "survey";
    }

    @GetMapping("/{id}")
    public String getSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String id) {
        //TODO: сделать отображение опроса.
        //TODO: Сначала идет запрос на /id, там создается completedSurvey с uuid, redirect:/uuid
        return "redirect:/survey/" + uuid;
    }

    @GetMapping("/uuid")
    public String startSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String uuid
    ) {
        return "survey";
    }

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
