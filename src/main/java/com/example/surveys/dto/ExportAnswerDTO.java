package com.example.surveys.dto;

public record ExportAnswerDTO(
        Long id,
        String answerType,
        String delimiter,
        String answer
) {
}
