package com.example.surveys.repository;

import com.example.surveys.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findById(Long id);
    List<Survey> findByBusinessId(Long id);
}
