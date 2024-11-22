package com.example.userService.controller;

import com.example.userService.dto.LoginRequest;
import com.example.userService.model.User;
import com.example.userService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
       /* testRestTemplate = testRestTemplate.withBasicAuth("user", "password");*/
    }

    @Test
    public void testRegisterUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@example.com");

        // Act
        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/api/user/register",
                user,
                User.class
        );

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());

        // Verify user is saved in the database
        assertEquals(1, userRepository.findAll().size());
        assertEquals("testuser@example.com", userRepository.findAll().get(0).getEmail());
    }

    @Test
    public void testLoginSuccess() {
        // Arrange: First, register a user with an encoded password
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("testpassword")); // Encode the password
        user.setEmail("testuser@example.com");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword"); // Plain text for the test

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "/api/user/login",
                loginRequest,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    public void testLoginFailure() {
        // Arrange: Create a login request for a non-existent user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("wrongpassword");

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "/api/user/login",
                loginRequest,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }
}
