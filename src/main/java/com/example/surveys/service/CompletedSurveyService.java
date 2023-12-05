package com.example.surveys.service;

import com.example.surveys.model.CompletedSurvey;
import com.example.surveys.model.Survey;
import com.example.surveys.model.User;
import com.example.surveys.repository.CompletedSurveyRepository;
import com.example.surveys.repository.SurveyRepository;
import com.example.surveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CompletedSurveyService {
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;

    private final CompletedSurveyRepository completedSurveyRepository;

    @Autowired
    public CompletedSurveyService(UserRepository userRepository, SurveyRepository surveyRepository, CompletedSurveyRepository completedSurveyRepository) {
        this.userRepository = userRepository;
        this.surveyRepository = surveyRepository;
        this.completedSurveyRepository = completedSurveyRepository;
    }

    public String newSurvey(String id, String username) {
        Survey survey;
        User user;
        try {
            survey = surveyRepository.findById(Long.parseLong(id)).orElseThrow();
            user = userRepository.findByLogin(username).orElseThrow();
        } catch (NoSuchElementException e) {
            return "error";
        }
        CompletedSurvey completedSurvey = new CompletedSurvey();
        completedSurvey.setSurvey(survey);
        completedSurvey.setStatus(-1);
        completedSurvey.setUser(user);
        return completedSurveyRepository.save(completedSurvey).getUuid();
    }
}
