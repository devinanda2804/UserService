package com.example.userService.service;

import com.example.userService.exception.BadCredentialsException;
import com.example.userService.exception.UserNameNotFoundException;
import com.example.userService.model.User;
import com.example.userService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUserTest() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setUserId(1);
            return savedUser;
        });


        User result = userService.registerUser(user);


        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));


        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("testUser", result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void authenticateUser_Success() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        User result = userService.authenticate("user", "password");


        verify(userRepository, times(1)).findByUsername("user");
        verify(passwordEncoder, times(1)).matches("password", "password");

        assertNotNull(result);
        assertEquals("user", result.getUsername());
    }

    @Test
    public void authenticateUser_UserNotFound() {

        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());


        Exception exception = assertThrows(UserNameNotFoundException.class, () ->
                userService.authenticate("unknownUser", "password")
        );

        verify(userRepository, times(1)).findByUsername("unknownUser");
        assertEquals("User not found", exception.getMessage());
    }



}
