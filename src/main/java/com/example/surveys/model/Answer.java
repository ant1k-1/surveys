package com.example.surveys.model;

import com.example.surveys.enums.AnswerType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_survey_id", nullable = false)
    private CompletedSurvey completedSurvey;

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @Lob
    private String answer;

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", question=" + question +
                ", completedSurveyUUID=" + completedSurvey.getUuid() +
                ", answerType=" + answerType +
                ", answer='" + answer + '\'' +
                '}';
    }
}
