package com.example.surveys.dto;

import com.example.surveys.enums.AnswerType;

import java.util.List;

public record QuestionDTO(
        List<String> images,
        String description,
        AnswerType answerType,
        List<String> variants
) {

}
