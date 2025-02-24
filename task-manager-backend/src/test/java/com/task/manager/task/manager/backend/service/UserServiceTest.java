package com.task.manager.task.manager.backend.service;

import com.task.manager.task.manager.backend.model.NewUserRecord;
import com.task.manager.task.manager.backend.model.User;
import com.task.manager.task.manager.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("Should save a new user successfully")
    @Description("Ensures that a new user is saved successfully when the username does not exist.")
    void shouldSaveNewUserSuccessfully() {
        NewUserRecord newUserRecord = new NewUserRecord("newUser", "user@example.com", "password123");

        when(userRepository.findByUsername(newUserRecord.username())).thenReturn(null);
        when(passwordEncoder.encode(newUserRecord.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.save(newUserRecord);

        assertNotNull(savedUser);
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("user@example.com", savedUser.getEmail());

        verify(userRepository, times(1)).findByUsername("newUser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    @Description("Ensures that an exception is thrown when attempting to create a user with an existing username.")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        NewUserRecord newUserRecord = new NewUserRecord("existingUser", "user@example.com", "password123");
        UserDetails existingUser = mock(UserDetails.class);

        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.save(newUserRecord));

        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should throw exception when username is null")
    @Description("Ensures that an exception is thrown when attempting to create a user with a null username.")
    void shouldThrowExceptionWhenUsernameIsNull() {
        NewUserRecord newUserRecord = new NewUserRecord(null, "user@example.com", "password123");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<NewUserRecord>> violations = validator.validate(newUserRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Name cannot be blank or null", violations.iterator().next().getMessage());

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    @Description("Ensures that an exception is thrown when attempting to create a user with a null password.")
    void shouldThrowExceptionWhenPasswordIsNull() {
        NewUserRecord newUserRecord = new NewUserRecord("newUser", "user@example.com",null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<NewUserRecord>> violations = validator.validate(newUserRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Password cannot be blank or null", violations.iterator().next().getMessage());

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    @Description("Ensures that an exception is thrown when attempting to create a user with a null email.")
    void shouldThrowExceptionWhenEmailIsNull() {
        NewUserRecord newUserRecord = new NewUserRecord("newUser",null, "password123");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<NewUserRecord>> violations = validator.validate(newUserRecord);

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank or null", violations.iterator().next().getMessage());

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

}