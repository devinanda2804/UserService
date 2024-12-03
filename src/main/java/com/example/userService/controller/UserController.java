package com.example.userService.controller;


import com.example.userService.dto.LoginRequest;
import com.example.userService.dto.LoginResponse;
import com.example.userService.exception.BadCredentialsException;
import com.example.userService.exception.UserNameNotFoundException;
import com.example.userService.model.User;
import com.example.userService.service.UserService;
import com.example.userService.utilities.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){

        User registerdUser=userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerdUser);

    }


    /*@PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            User authenticateUser = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            LoginResponse response = new LoginResponse(authenticateUser.getUsername(), "Login successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Invalid username or password"));
        }
    }
*/
   /* @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            User authenticateUser = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(authenticateUser.getUsername(), (long) authenticateUser.getUserId());

            // Create a new Authentication object with the JWT token in the credentials
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(authenticateUser.getUsername(), token, new ArrayList<>()); // Add authorities if needed

            // Set the authentication object in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create response with token
            LoginResponse response = new LoginResponse(authenticateUser.getUsername(), "Login successful", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Invalid username or password", null));
        }
    }*/

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            User authenticateUser = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            String token = jwtTokenUtil.generateToken(authenticateUser.getUsername(), (long) authenticateUser.getUserId());

            boolean isAdmin = authenticateUser.getRole().equals("ROLE_ADMIN");

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(authenticateUser.getUsername(), token, new ArrayList<>());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponse response = new LoginResponse(authenticateUser.getUsername(), "Login successful", token);

            if (isAdmin) {
                response.setMessage("Login successful - Admin access");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Invalid username or password", null));
        }
    }



}


