package com.example.surveys.dto;

import com.example.surveys.model.Question;

import java.util.List;

public record ExportSurveyDTO(
        Long id,
        Long businessId,
        List<ExportQuestionDTO> questions,
        String surveyStatus,
        Integer amount,
        Integer count,
        Integer award
) {

}
