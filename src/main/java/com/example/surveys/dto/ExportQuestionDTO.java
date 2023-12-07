package com.example.surveys.dto;

import com.example.surveys.model.Answer;

import java.util.List;

public record ExportQuestionDTO(
        Long id,
        String description,
        String answerType,
        List<String> variants,
        List<ExportAnswerDTO> answers
) {
}
