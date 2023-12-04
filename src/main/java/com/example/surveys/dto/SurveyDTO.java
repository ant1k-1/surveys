package com.example.surveys.dto;

import java.util.List;

public record SurveyDTO(
        Long id,
        List<QuestionDTO> questions,
        Integer amount,
        Integer count,
        Integer award
) {
}
