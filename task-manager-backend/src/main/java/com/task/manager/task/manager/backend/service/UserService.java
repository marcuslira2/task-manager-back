package com.task.manager.task.manager.backend.service;

import com.task.manager.task.manager.backend.model.NewUserRecord;
import com.task.manager.task.manager.backend.model.User;
import com.task.manager.task.manager.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save (@Valid NewUserRecord dto){
        if (userRepository.findByUsername(dto.username()) != null) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        log.info("Saving user: {}", dto.username());
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());

        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new NoSuchElementException("User not found."));
    }

}
