package com.example.surveys.service;

import com.example.surveys.dto.UserDTO;
import com.example.surveys.model.User;
import com.example.surveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean create(User user) {
        if (userRepository.existsByLogin(user.getLogin())) return false;
        user.setBalance(0);
        user.setCreationDate(LocalDateTime.now());
        user.setIsActiveStatus(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public Boolean editProfile(String username, Map<String, String> userData) {
        if (userData.get("phone").length() < 12) {
            return false;
        }
        try {
            User user = userRepository.findByLogin(username).orElseThrow();
            user.setEmail(userData.get("email"));
            user.setName(userData.get("name"));
            user.setSurname(userData.get("surname"));
            user.setPatronymic(userData.get("patronymic"));
            user.setPhone(userData.get("phone"));
            userRepository.save(user);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean withdraw(String username) {
        try {
            User user = userRepository.findByLogin(username).orElseThrow();
            var balance = user.getBalance();
            if (balance >= 1000) {
                user.setBalance(balance - 1000);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            User user = userRepository.findByLogin(username).orElseThrow();
            if (!passwordEncoder.encode(oldPassword).equals(user.getPassword()) || oldPassword.equals(newPassword)) {
                return false;
            } else {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean deposit(String username, String deposit) {
        try {
            User user = userRepository.findByLogin(username).orElseThrow();
            Integer nDeposit = Integer.parseInt(deposit);
            nDeposit += user.getBalance();
            user.setBalance(nDeposit);
            userRepository.save(user);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public UserDTO getUserDTO(String username) {
        var userOptional = userRepository.findByLogin(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserDTO(
                    user.getId(),
                    user.getRoles(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBalance(),
                    user.getName(),
                    user.getSurname(),
                    user.getPatronymic(),
                    user.getPhone(),
                    user.getBirthday(),
                    user.getCreationDate(),
                    user.getIsActiveStatus(),
                    user.getQuiz()
            );
        }
        return null;
    }
}
