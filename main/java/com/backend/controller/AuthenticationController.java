package com.backend.controller;

import com.backend.dto.*;
import com.backend.exception.UserAlreadyExistsException;
import com.backend.exception.UserNotFoundException;
import com.backend.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthenticationController {
    AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            authenticationService.registerUser(registrationBody);
            return ResponseEntity.ok("{}");
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = authenticationService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            boolean result = authenticationService.initiatePasswordReset(passwordResetRequest.getEmail());
            if (result) {

                Map<String, String> response = new HashMap<>();
                response.put("message", "Password reset link sent to email.");
                return ResponseEntity.ok(response);
            } else {

                Map<String, String> response = new HashMap<>();
                response.put("message", "Email not found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found with this email.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "An error occurred while processing the request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPasswordEmail(
            @RequestParam("token") String token,
            @RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            boolean result = authenticationService.resetPassword(token, passwordResetDTO.getNewPassword());
            Map<String, String> response = new HashMap<>();

            if (result) {
                response.put("message", "Password has been reset successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid or expired token.");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "An error occurred while processing the request.");
            return ResponseEntity.status(500).body(response);
        }
    }


}
