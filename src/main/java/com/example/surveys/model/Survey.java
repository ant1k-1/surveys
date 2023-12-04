package com.example.surveys.model;

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

    @OneToMany(mappedBy = "survey")
    private Collection<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private Collection<CompletedSurvey> completedSurveys = new ArrayList<>();

    private Integer amount;
    private Integer count;
    private Integer award;

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
