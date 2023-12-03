package com.example.surveys.service;

import com.example.surveys.model.CompletedSurvey;
import com.example.surveys.model.Survey;
import com.example.surveys.model.User;
import com.example.surveys.repository.SurveyRepository;
import com.example.surveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository, UserRepository userRepository) {
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
    }

    public List<Survey> getAvailableSurveys(String forUsername) {
        User user = userRepository.findByLogin(forUsername).get();
        List<Survey> surveysByUser = user.getCompletedSurveys()
                .stream().map(CompletedSurvey::getSurvey).toList();
        return surveyRepository.findAll().stream()
                .filter(survey -> !surveysByUser.contains(survey)).toList();
    }

    public Boolean createSurvey() {
        return true;
    }
}
