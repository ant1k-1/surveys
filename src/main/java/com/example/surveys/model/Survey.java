package com.example.surveys.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "raw_surveys")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long business_id;
    @Lob
    private String jsonSurvey;
    private Integer amount;
    private Integer count;
    @Lob
    private String jsonStat;
}
