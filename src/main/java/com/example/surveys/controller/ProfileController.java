package com.example.surveys.controller;

import com.example.surveys.dto.WithdrawDTO;
import com.example.surveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            Model model
    ) {
        model.addAttribute("user", userService.getUserDTO(auth.getName()));
        model.addAttribute("withdraw", new WithdrawDTO());
        //TODO: добавить в модель список пройденных опросов и выводы денег.
        return "profile";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @ModelAttribute("withdraw") WithdrawDTO withdrawCredentials
    ) {
        //TODO: опционально добавить обработки ошибок и вывод на веб
        userService.withdraw(auth.getName());
        return "redirect:/profile";
    }

    @PostMapping("/deposit")
    public String deposit(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam("deposit") String deposit
    ) {
        //TODO: опционально добавить обработки ошибок и вывод на веб
        userService.deposit(auth.getName(), deposit);
        return "redirect:/profile";
    }

    @PostMapping("/edit")
    public String editProfile(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> userData
    ) {
        //TODO: опционально добавить обработки ошибок и вывод на веб
        userService.editProfile(auth.getName(), userData);
        return "redirect:/profile";
    }
    @PostMapping("/password")
    public String changePassword(
            @CurrentSecurityContext(expression = "authentication") Authentication auth,
            @RequestParam Map<String, String> userData
    ) {
        //TODO: опционально добавить обработки ошибок и вывод на веб
        userService.changePassword(auth.getName(), userData.get("oldPassword"), userData.get("newPassword"));
        return "redirect:/profile";
    }
}
