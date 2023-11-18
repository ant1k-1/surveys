package com.example.surveys.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long business_id;
    @Lob
    private String jsonQuiz;
    private Integer amount;
    private Integer count;
    @Lob
    private String jsonStat;
}
