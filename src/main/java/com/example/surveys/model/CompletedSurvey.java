package com.example.surveys.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@Data
@Entity
@Table(name = "completed_surveys")
public class CompletedSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid; //id of completed survey unit
    private Integer status; //статус

    @OneToMany(mappedBy = "completedSurvey")
    private Collection<Answer> answers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
