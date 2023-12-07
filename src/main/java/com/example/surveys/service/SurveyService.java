package com.example.surveys.service;

import com.example.surveys.dto.QuestionDTO;
import com.example.surveys.dto.SurveyDTO;
import com.example.surveys.enums.AnswerType;
import com.example.surveys.enums.SurveyStatus;
import com.example.surveys.model.CompletedSurvey;
import com.example.surveys.model.Question;
import com.example.surveys.model.Survey;
import com.example.surveys.model.User;
import com.example.surveys.repository.QuestionRepository;
import com.example.surveys.repository.SurveyRepository;
import com.example.surveys.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository, UserRepository userRepository, QuestionRepository questionRepository) {
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    public void closeSurveyToggle(String id, String username) {
        try {
            User user = userRepository.findByLogin(username).orElseThrow();
            Survey survey = surveyRepository.findById(Long.parseLong(id)).orElseThrow();
            if (survey.getCount() < survey.getAmount() && survey.getBusinessId() == user.getId()) {
                if (survey.getSurveyStatus().equals(SurveyStatus.CLOSED)) {
                    survey.setSurveyStatus(SurveyStatus.IN_PROCESS);
                    surveyRepository.save(survey);
                    return;
                }
                if (survey.getSurveyStatus().equals(SurveyStatus.IN_PROCESS)) {
                    survey.setSurveyStatus(SurveyStatus.CLOSED);
                    surveyRepository.save(survey);
                    return;
                }
            } else {
                return;
            }
        } catch (Exception ignored) {}
    }

    public List<Survey> getAllSurveysByBusinessLogin(String login) {
        try {
            Long id = userRepository.findByLogin(login).orElseThrow().getId();
            return surveyRepository.findByBusinessId(id).stream()
                    .sorted(Comparator.comparingLong(Survey::getId))
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void deleteSurveyById(String id) {
        try {
            if (surveyRepository.existsById(Long.parseLong(id))) {
                surveyRepository.deleteById(Long.parseLong(id));
            }
        } catch (Exception ignored) {}
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    public Page<Survey> getAllPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Survey> surveys = surveyRepository.findAll()
                .stream().sorted(Comparator.comparingLong(Survey::getId)).toList();
        List<Survey> list;
        if (surveys.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, surveys.size());
            list = surveys.subList(startItem, toIndex);
        }
        return new PageImpl<Survey>(list, PageRequest.of(currentPage, pageSize), surveys.size());
    }

    public SurveyStatus checkAccessToView(Long id, String username) {
        User user;
        Survey survey;
        try {
            user = userRepository.findByLogin(username).orElseThrow();
        } catch (NoSuchElementException e) {
            return SurveyStatus.ERROR;
        }
        try {
            survey = surveyRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            return SurveyStatus.NOT_FOUND;
        }
        if (survey.getBusinessId() == user.getId() || user.isAdmin()) {
            return SurveyStatus.READ_ONLY;
        } else if (survey.getSurveyStatus().equals(SurveyStatus.CLOSED)) {
            return SurveyStatus.CLOSED;
        } else return SurveyStatus.ACCESS_DENIED;
    }

    @Transactional
    public SurveyDTO getSurveyDtoById(Long id) {
        try {
            Survey survey = surveyRepository.findById(id).orElseThrow();
            List<QuestionDTO> questionDTOS = survey.getQuestions()
                    .stream().map(q -> new QuestionDTO(
                            q.getPics(),
                            q.getDescription(),
                            q.getAnswerType(),
                            q.getVariants()))
                    .toList();
            return new SurveyDTO(
                    survey.getId(),
                    survey.getSurveyStatus(),
                    questionDTOS,
                    survey.getAmount(),
                    survey.getCount(),
                    survey.getAward()
            );
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<Survey> getAvailableSurveys(String forUsername) {
        User user;
        try {
            user = userRepository.findByLogin(forUsername).orElseThrow();
        } catch (Exception e) {
            return Collections.emptyList();
        }
        List<Survey> surveysByUser = user.getCompletedSurveys()
                .stream().map(CompletedSurvey::getSurvey).toList();
        var result = new ArrayList<>(surveyRepository.findAll().stream()
                .filter(survey -> !surveysByUser.contains(survey)
                        && !survey.getSurveyStatus().equals(SurveyStatus.CLOSED)
                )
                .sorted(Comparator.comparingLong(Survey::getId))
                .toList());
        Collections.shuffle(result);
        return result.stream().limit(5).toList();
    }

    public Long createSurvey(Map<String, String> form, MultipartFile[] pics, String creator) {
        User surveyCreator;
        Survey survey = new Survey();
        try {
            surveyCreator = userRepository.findByLogin(creator).orElseThrow();
            survey.setBusinessId(surveyCreator.getId());
            survey = surveyRepository.save(survey);
        } catch (NoSuchElementException e) {
            return -1L;
        }
        int question_count = Integer.parseInt(form.get("question_count"));
        int amount = Integer.parseInt(form.get("amount"));
        int award = Integer.parseInt(form.get("award"));
        if (amount*award > surveyCreator.getBalance()) {
            return -2L;
        } else {
            surveyCreator.setBalance(surveyCreator.getBalance() - amount*award);
            surveyCreator = userRepository.save(surveyCreator);
        }
        survey.setAmount(amount);
        survey.setAward(award);
        survey.setCount(0);
        survey.setSurveyStatus(SurveyStatus.IN_PROCESS);
        //parsing questions
        for (int i = 1; i < question_count + 1; i++) {
            String prefix_q = "q" + i + "-";
            // parsing question params
            int picsCount;
            int variantsCount;
            try {
                picsCount = Integer.parseInt(form.getOrDefault(prefix_q + "picsCount", "0"));
            } catch (NumberFormatException e) {
                picsCount = 0;
            }
            try {
                variantsCount = Integer.parseInt(form.get(prefix_q + "variantsCount"));
            } catch (NumberFormatException e) {
                variantsCount = 0;
            }
            List<MultipartFile> arrayListPics = picsCount == 0 ? null : new ArrayList<>(Arrays.asList(pics));
            String description = form.getOrDefault(prefix_q + "description", "");
            AnswerType answerType = AnswerType.valueOf(
                    form.getOrDefault(prefix_q + "answerType", "TEXT_ANSWER"));
            // parsing pics
            List<MultipartFile> questionPics = new ArrayList<>();
            for (int j = 0; j < picsCount; j++) {
                questionPics.add(arrayListPics.get(0));
                arrayListPics.remove(0);
            }
            // parsing variants
            List<String> variants = new ArrayList<>();
            switch (answerType) {
                case DATE_ANSWER, TEXT_ANSWER, NUMERIC_ANSWER -> {}
                case CHECKBOX_ANSWER -> {
                    for (int k = 0; k < variantsCount; k++) {
                        variants.add(form.get(prefix_q + "checkbox" + k));
                    }
                }
                case RATIO_ANSWER -> {
                    for (int k = 0; k < variantsCount; k++) {
                        variants.add(form.get(prefix_q + "ratio" + k));
                    }
                }
            }
            Question question = questionRepository.save(new Question());
            question.setSurvey(survey);
            question.setDescription(description);
            question.setAnswerType(answerType);
            question.setVariants(variants);

            //saving pictures
            for (var pic : questionPics) {
                if (!pic.isEmpty()) {
                    File uploadDir = new File(uploadPath);

                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    String uuidFile = survey.getId().toString() + "_" + UUID.randomUUID().toString();
                    String resultFilename = uuidFile + "_" + pic.getOriginalFilename();

                    try {
                        pic.transferTo(new File(uploadPath + "/" + resultFilename));
                    } catch (IOException e) {
                        return -3L;
                    }
                    question.addPic(resultFilename);
                }
            }
            question = questionRepository.save(question);
            survey.addQuestion(question);
        }
        surveyRepository.save(survey);
        return survey.getId();
    }
}
