package com.example.surveys.repository;

import com.example.surveys.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    Boolean existsByLogin(String login);

    List<User> findAllByIsActiveStatusTrue();

    List<User> findAllByIsActiveStatusFalse();
}
