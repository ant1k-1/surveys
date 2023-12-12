package com.example.surveys.service;

import com.example.surveys.dto.UserDTO;
import com.example.surveys.model.User;
import com.example.surveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteUser(String id) {
        try {
            if (userRepository.existsById(Long.parseLong(id))) {
                userRepository.deleteById(Long.parseLong(id));
            }
        } catch (Exception ignored) {}
    }

    public void banToggle(String id) {
        try {
            User user = userRepository.findById(Long.parseLong(id)).orElseThrow();
            user.setIsActiveStatus(!user.getIsActiveStatus());
            userRepository.save(user);
        } catch (Exception ignored) {}
    }

    public Page<User> getAllPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> users = userRepository.findAll()
                .stream().sorted(Comparator.comparingLong(User::getId)).toList();
        List<User> userList;
        if (users.size() < startItem) {
            userList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            userList = users.subList(startItem, toIndex);
        }
        return new PageImpl<>(userList, PageRequest.of(currentPage, pageSize), users.size());
    }

    public Boolean create(User user) {
        if (userRepository.existsByLogin(user.getLogin())) return false;
        user.setBalance(0);
        user.setCreationDate(LocalDateTime.now());
        user.setIsActiveStatus(false);
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
                user.addQuiz("Вывод " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), -1000);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean setPassword(String id, String newPassword) {
        try {
            User user = userRepository.findById(Long.parseLong(id)).orElseThrow();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean editUser(String id, Map<String, String> userData) {
        if (userData.get("phone").length() < 12) {
            return false;
        }
        try {
            User user = userRepository.findById(Long.parseLong(id)).orElseThrow();
            user.setEmail(userData.get("email"));
            user.setName(userData.get("name"));
            user.setSurname(userData.get("surname"));
            user.setPatronymic(userData.get("patronymic"));
            user.setPhone(userData.get("phone"));
            user.setBirthday(LocalDate.parse(userData.get("birthday")));
            user.setBalance(Integer.parseInt(userData.get("balance")));
            userRepository.save(user);
            return true;
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

    public UserDTO getUserDTObyId(String id) {
        try {
            return getUserDTO(userRepository.findById(Long.parseLong(id)).orElseThrow().getLogin());
        } catch (Exception e) {
            return null;
        }
    }
}
