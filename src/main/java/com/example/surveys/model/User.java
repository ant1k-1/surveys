package com.example.surveys.model;

import com.example.surveys.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private String login;
    private String password;
    private String email;
    private Integer balance;
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private LocalDate birthday;
    private LocalDateTime creationDate;
    private Boolean isActiveStatus;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_quiz", joinColumns = @JoinColumn(name = "user_id"))
    private Map<String, Integer> quiz = new HashMap<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CompletedSurvey> completedSurveys = new ArrayList<>();
    // string - uuid, int - award

    public void addQuiz(String uuid, int award) {
        quiz.put(uuid, award);
    }
    public User(
            String email,
            String login,
            String password,
            String name,
            String surname,
            String patronymic,
            String phone,
            String birthday
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.email = email;
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.birthday = LocalDate.parse(birthday, formatter);
        this.creationDate = LocalDateTime.now();
        this.isActiveStatus = true;
        this.balance = 0;
        this.roles.add(Role.ROLE_USER);
    }
    public static User makeBusiness(
            String email,
            String login,
            String password,
            String name,
            String surname,
            String patronymic,
            String phone,
            String birthday
    ) {
        User user = new User(email, login, password, name, surname, patronymic, phone, birthday);
        user.roles.add(Role.ROLE_BUSINESS);
        return user;
    }
    public static User makeAdmin(String login, String password) {
        User user = new User();
        user.login = login;
        user.password = password;
        user.roles.add(Role.ROLE_ADMIN);
        user.roles.add(Role.ROLE_USER);
        user.roles.add(Role.ROLE_BUSINESS);
        user.creationDate = LocalDateTime.now();
        user.isActiveStatus = true;
        return user;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    public boolean isBusiness() {
        return roles.contains(Role.ROLE_BUSINESS);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", roles=" + roles +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                ", creationDate=" + creationDate +
                ", isActiveStatus=" + isActiveStatus +
                ", quiz=" + quiz +
                '}';
    }
}
