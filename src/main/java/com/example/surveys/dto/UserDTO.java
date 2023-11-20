package com.example.surveys.dto;

import com.example.surveys.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record UserDTO(
        Long id,
        Set<Role> roles,
        String login,
        String email,
        Integer balance,
        String name,
        String surname,
        String patronymic,
        String phone,
        LocalDate birthday,
        LocalDateTime creationDate,
        Boolean isActiveStatus,
        Map<String, Integer> quiz
) {
    // Конструкторы и другие методы не требуют явного определения, они автоматически создаются
}
