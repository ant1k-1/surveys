package com.example.surveys.model;

import com.example.surveys.enums.SurveyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "raw_surveys")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long businessId;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CompletedSurvey> completedSurveys = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SurveyStatus surveyStatus;

    private Integer amount;
    private Integer count;
    private Integer award;

    public void addQuestion(Question question) {
        questions.add(question);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", businessId=" + businessId +
                ", questions=" + questions +
                ", completedSurveys=" + completedSurveys +
                ", amount=" + amount +
                ", count=" + count +
                ", award=" + award +
                '}';
    }
}
