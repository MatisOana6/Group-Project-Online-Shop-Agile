package com.backend.controller;

import com.backend.dto.LoggedUserDTO;
import com.backend.dto.UpdateUserProfileDTO;
import com.backend.dto.UserDTO;
import com.backend.service.JwtService;
import com.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/sellers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<UserDTO>> getSellers() {
        return ResponseEntity.ok(userService.getSellers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CLIENT')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','SELLER')")
    public ResponseEntity<LoggedUserDTO> getProfile(HttpServletRequest request) {
        try {
            UUID loggedUserId = jwtService.extractId(request.getHeader("Authorization"));
            LoggedUserDTO userProfile = userService.getLoggedUserById(loggedUserId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UpdateUserProfileDTO dto, @RequestHeader("Authorization") String token) {
        UUID userId = jwtService.extractId(token);
        UserDTO updatedUser = userService.updateUserProfile(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }
}