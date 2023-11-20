package com.example.surveys.repository;

import com.example.surveys.model.Answer;
import com.example.surveys.model.CompletedSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findById(Long id);
    List<Answer> findByCompletedSurvey(CompletedSurvey completedSurvey);
}
