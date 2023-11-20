package com.example.surveys.model;

import com.example.surveys.enums.AnswerType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "question_pics")
    private List<String> pics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @ElementCollection
    @CollectionTable(name = "question_variants")
    private List<String> variants = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private Collection<Answer> answers = new ArrayList<>();
}
