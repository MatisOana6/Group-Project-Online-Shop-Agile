package com.backend;

import com.backend.dto.LoginDTO;
import com.backend.dto.LoginResponseDTO;
import com.backend.dto.RegistrationBody;
import com.backend.entity.Role;
import com.backend.entity.User;
import com.backend.exception.UserAlreadyExistsException;
import com.backend.mapper.UserMapper;
import com.backend.neo4j.service.UserGraphService;
import com.backend.repository.UserRepository;
import com.backend.service.AuthenticationService;
import com.backend.service.EncryptionService;
import com.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserGraphService userGraphService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldSaveUserAndCreateGraphNode() {
        RegistrationBody registrationBody = new RegistrationBody(
                "Test User", "test@example.com", "password123",
                null, "Address", "0700000000", Role.CLIENT
        );

        User user = new User();
        user.setIdUser(UUID.randomUUID());

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(encryptionService.encryptPassword("password123")).thenReturn("hashedPassword");
        when(userMapper.toUser(registrationBody, "hashedPassword")).thenReturn(user);

        authenticationService.registerUser(registrationBody);

        verify(userRepository).save(user);
        verify(userGraphService).createUserNode(user.getIdUser());
    }

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyExists() {
        RegistrationBody registrationBody = new RegistrationBody(
                "Test User", "test@example.com", "password123",
                null, "Address", "0700000000", Role.CLIENT
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authenticationService.registerUser(registrationBody));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setIdUser(UUID.randomUUID());
        user.setRole(Role.CLIENT);
        user.setProfileImage("image.png");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encryptionService.verifyPassword("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateJwtToken("test@example.com", user.getIdUser().toString(), "CLIENT"))
                .thenReturn("token123");

        LoginResponseDTO result = authenticationService.login(loginDTO);

        assertNotNull(result);
        assertEquals("token123", result.getToken());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("CLIENT", result.getRole());
        assertEquals("image.png", result.getProfileImage());
    }

    @Test
    void login_shouldThrow_whenInvalidCredentials() {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "wrongpassword");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encryptionService.verifyPassword("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authenticationService.login(loginDTO));
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        LoginDTO loginDTO = new LoginDTO("unknown@example.com", "password");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authenticationService.login(loginDTO));
    }
}
