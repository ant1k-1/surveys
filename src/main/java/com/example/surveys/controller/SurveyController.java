package com.example.surveys.controller;

import com.example.surveys.dto.SurveyDTO;
import com.example.surveys.enums.SurveyStatus;
import com.example.surveys.service.CompletedSurveyService;
import com.example.surveys.service.SurveyService;
import com.example.surveys.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@RequestMapping("/survey")
@Controller
public class SurveyController {
    private final SurveyService surveyService;
    private final UserService userService;
    private final CompletedSurveyService completedSurveyService;

    @Value("${export.path}")
    private String exportPath;

    @Autowired
    public SurveyController(SurveyService surveyService,
                            UserService userService,
                            CompletedSurveyService completedSurveyService) {
        this.surveyService = surveyService;
        this.userService = userService;
        this.completedSurveyService = completedSurveyService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/start/{id}")
    public String getSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @PathVariable String id) {
        String redir = completedSurveyService.newSurvey(id, auth.getName());
        if (redir.equals("ERROR") || redir.equals("CLOSED")) {
            return "redirect:/survey/" + redir;
        }
        return "redirect:/survey/start/" + id + "/" + redir;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/start/{id}/{uuid}")
    public String startSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model,
            @PathVariable String uuid,
            @PathVariable String id
    ) {
        model.addAttribute("SURVEY_STATUS",completedSurveyService.checkAccessToUser(uuid, auth.getName()));
        SurveyDTO surveyDTO = surveyService.getSurveyDtoById(Long.valueOf(id));
        model.addAttribute("survey", surveyDTO);
        model.addAttribute("uuid", uuid);
        return "survey";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/start/{id}/{uuid}")
    public String finishSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> map,
            @PathVariable String id, @PathVariable String uuid
    ) {
        return "redirect:/survey/" + completedSurveyService.finishSurvey(map, id, uuid, auth.getName()).name();
    }

    @GetMapping("/{status}")
    public String surveyStatus(
            Model model,
            @PathVariable String status
    ) {
        model.addAttribute("SURVEY_STATUS", SurveyStatus.valueOf(status));
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteSurvey(
            @PathVariable String id
    ) {
        surveyService.deleteSurveyById(id);
        return "redirect:/admin/surveys";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @PostMapping("/create")
    public String createSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> map,
            @RequestParam(name = "pics", required = false) MultipartFile[] pics
            ) {
        int survey_id = Math.toIntExact(surveyService.createSurvey(map, pics, auth.getName()));
        if (survey_id > 0) {
            return "redirect:/survey/view/id/" + survey_id;
        } else {
            return "redirect:/survey/create";
        }
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/close/{id}")
    public String closeSurvey(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @PathVariable String id
    ) {
        surveyService.closeSurveyToggle(id, auth.getName());
        return "redirect:/profile";
    }

    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    @GetMapping("/export/{id}")
    public ResponseEntity<Resource> exportJson(
            @PathVariable String id,
            @CurrentSecurityContext(expression = "authentication") Authentication auth
    ) {
        String json = completedSurveyService.exportJSON(id, auth.getName());
        File file = new File(exportPath + "/json.json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "survey" + id +".json");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().build();
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
        model.addAttribute("SURVEY_STATUS",
                surveyService.checkAccessToView(Long.valueOf(id), auth.getName()));
        model.addAttribute("survey", surveyDTO);
        return "survey";
    }
}
