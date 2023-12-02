package com.example.surveys.repository;

import com.example.surveys.model.CompletedSurvey;
import com.example.surveys.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompletedSurveyRepository extends JpaRepository<CompletedSurvey, String> {
    Optional<CompletedSurvey> findByUuid(String uuid);
    List<CompletedSurvey> findBySurvey(Survey survey);
}
