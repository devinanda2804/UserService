package com.example.userService.controller;


import com.example.userService.dto.LoginRequest;
import com.example.userService.model.User;
import com.example.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){

        User registerdUser=userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerdUser);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){

        User authenticateUser=userService.authenticate(loginRequest.getUsername(),
                loginRequest.getPassword());
       /* return ResponseEntity.ok(authenticateUser);*/
        if (authenticateUser != null) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

    }


}
