package com.example.surveys.service;

import com.example.surveys.dto.*;
import com.example.surveys.enums.SurveyStatus;
import com.example.surveys.model.*;
import com.example.surveys.repository.AnswerRepository;
import com.example.surveys.repository.CompletedSurveyRepository;
import com.example.surveys.repository.SurveyRepository;
import com.example.surveys.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompletedSurveyService {
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyService surveyService;
    private final AnswerRepository answerRepository;
    private final CompletedSurveyRepository completedSurveyRepository;

    @Autowired
    public CompletedSurveyService(UserRepository userRepository, SurveyRepository surveyRepository, SurveyService surveyService, AnswerRepository answerRepository, CompletedSurveyRepository completedSurveyRepository) {
        this.userRepository = userRepository;
        this.surveyRepository = surveyRepository;
        this.surveyService = surveyService;
        this.answerRepository = answerRepository;
        this.completedSurveyRepository = completedSurveyRepository;
    }

    @Transactional
    public String exportJSON(String id, String username) {
        Survey survey;
        User user;
        try {
            survey = surveyRepository.findById(Long.parseLong(id)).orElseThrow();
            user = userRepository.findByLogin(username).orElseThrow();
        } catch (Exception e) {
            return "";
        }
        if (survey.getBusinessId() == user.getId()) {
            List<Question> questions = survey.getQuestions().stream().toList();
            List<ExportQuestionDTO> exportQuestions = new ArrayList<>();
            for (var question: questions) {
                List<ExportAnswerDTO> exportAnswers = new ArrayList<>();
                for (var answer : question.getAnswers()) {
                    exportAnswers.add(new ExportAnswerDTO(
                            answer.getId(),
                            answer.getAnswerType().name(),
                            "@@@",
                            answer.getAnswer()
                    ));
                }
                exportQuestions.add(new ExportQuestionDTO(
                        question.getId(),
                        question.getDescription(),
                        question.getAnswerType().name(),
                        question.getVariants(),
                        exportAnswers
                ));
            }
            ExportSurveyDTO exportSurveyDTO = new ExportSurveyDTO(
                    survey.getId(),
                    survey.getBusinessId(),
                    exportQuestions,
                    survey.getSurveyStatus().name(),
                    survey.getAmount(),
                    survey.getCount(),
                    survey.getAward()
            );
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(exportSurveyDTO);
            return json;
        } else {
            return "";
        }
    }

    public String newSurvey(String id, String username) {
        Survey survey;
        User user;
        try {
            survey = surveyRepository.findById(Long.parseLong(id)).orElseThrow();
            user = userRepository.findByLogin(username).orElseThrow();
        } catch (NoSuchElementException e) {
            return "ERROR";
        }
        if (survey.getCount() >= survey.getAmount() || survey.getSurveyStatus().equals(SurveyStatus.CLOSED)) {
            return "CLOSED";
        }
        CompletedSurvey completedSurvey = new CompletedSurvey();
        completedSurvey.setSurvey(survey);
        completedSurvey.setStatus(0);
        completedSurvey.setUser(user);
        return completedSurveyRepository.save(completedSurvey).getUuid();
    }

    public SurveyStatus checkAccessToUser(String uuid, String username) {
        CompletedSurvey completedSurvey;
        User user;
        try {
            user = userRepository.findByLogin(username).orElseThrow();
            completedSurvey = completedSurveyRepository.findByUuid(uuid).orElseThrow();
        } catch (NoSuchElementException e) {
            return SurveyStatus.NOT_FOUND;
        }
        if (!completedSurvey.getUser().equals(user)) {
            return SurveyStatus.ACCESS_DENIED;
        } else {
            return switch(completedSurvey.getStatus()) {
                case 0 -> SurveyStatus.STARTED;
                case 1 -> SurveyStatus.FINISHED;
                default -> SurveyStatus.ERROR;
            };
        }
    }

    @Transactional
    public SurveyStatus finishSurvey(Map<String, String> form, String id, String uuid, String username) {
        SurveyDTO surveyDTO; Survey survey; CompletedSurvey completedSurvey; User user;
        try {
            surveyDTO = surveyService.getSurveyDtoById(Long.parseLong(id));
            survey = surveyRepository.findById(Long.parseLong(id)).orElseThrow();
            completedSurvey = completedSurveyRepository.findByUuid(uuid).orElseThrow();
            user = userRepository.findByLogin(username).orElseThrow();
        } catch (Exception e) {
            return SurveyStatus.ERROR;
        }
        if (surveyDTO == null) {
            return SurveyStatus.NOT_FOUND;
        }
        List<Question> questions = survey.getQuestions().stream().toList();
        List<String> checkboxes = new ArrayList<>();
        for (var key : form.keySet()) {
            if (key.contains("checkbox")) {
                checkboxes.add(key + "=" + form.get(key));
            }
        }

        for (int i = 0; i < questions.size(); i++) {
            Answer answer = new Answer();
            Question question = questions.get(i);
            String q_prefix = "q" + (i + 1) + "-";
            answer.setQuestion(question);
            answer.setCompletedSurvey(completedSurvey);
            answer.setAnswerType(question.getAnswerType());
            StringJoiner joiner = new StringJoiner("@@@");
            for (var checkbox : checkboxes) {
                if (checkbox.startsWith(q_prefix)) {
                    joiner.add(checkbox);
                }
            }

            switch (answer.getAnswerType()) {
                case TEXT_ANSWER -> answer.setAnswer(form.get(q_prefix + "text"));
                case NUMERIC_ANSWER -> answer.setAnswer(form.get(q_prefix + "numeric"));
                case DATE_ANSWER -> answer.setAnswer(form.get(q_prefix + "date"));
                case CHECKBOX_ANSWER -> answer.setAnswer(joiner.toString());
                case RATIO_ANSWER -> answer.setAnswer(form.get(q_prefix + "ratio"));
            }
            //answer creation is done, now update db
            answerRepository.save(answer);
        }
        completedSurvey.setStatus(1);
        survey.setCount(survey.getCount() + 1);
        if (survey.getCount() >= survey.getAmount()) {
            survey.setSurveyStatus(SurveyStatus.CLOSED);
        } else {
            survey.setSurveyStatus(SurveyStatus.IN_PROCESS);
        }
        user.setBalance(user.getBalance() + survey.getAward());
        user.addQuiz(uuid, survey.getAward());
        surveyRepository.save(survey);
        userRepository.save(user);
        completedSurveyRepository.save(completedSurvey);
        return SurveyStatus.FINISHED;
    }
}
