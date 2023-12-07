package com.example.surveys.dto;

import com.example.surveys.enums.SurveyStatus;

import java.util.List;

public record SurveyDTO(
        Long id,
        SurveyStatus surveyStatus,
        List<QuestionDTO> questions,
        Integer amount,
        Integer count,
        Integer award
) {
}
