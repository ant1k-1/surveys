package com.example.surveys.controller;

import com.example.surveys.model.Survey;
import com.example.surveys.model.User;
import com.example.surveys.service.SurveyService;
import com.example.surveys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
public class AdminController {
    private final SurveyService surveyService;
    private final UserService userService;

    public AdminController(SurveyService surveyService, UserService userService) {
        this.surveyService = surveyService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String viewUsers(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size
    ) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userService.getAllPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("userPage", userPage);
        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users";
    }

    @GetMapping("/users/{id}")
    public String viewUser(
            Model model,
            @PathVariable String id
    ) {
        model.addAttribute("user", userService.getUserDTObyId(id));

        return "profile";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(
            @PathVariable String id,
            @RequestParam Map<String, String> userData
    ) {
        userService.editUser(id, userData);
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/users/{id}/password")
    public String setPassword(
            @PathVariable String id,
            @RequestParam String newPassword
    ) {
        userService.setPassword(id, newPassword);
        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/users/{id}/ban")
    public String blockUser(
            @PathVariable String id
    ) {
        userService.banToggle(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable String id
    ) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/surveys")
    public String viewSurveys(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size
    ) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Survey> surveyPage = surveyService.getAllPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("surveyPage", surveyPage);
        int totalPages = surveyPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "surveys";
    }
}
