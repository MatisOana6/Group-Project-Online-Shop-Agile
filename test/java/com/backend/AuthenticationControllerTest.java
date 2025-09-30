package com.backend;

import com.backend.controller.AuthenticationController;
import com.backend.dto.LoginDTO;
import com.backend.dto.LoginResponseDTO;
import com.backend.dto.RegistrationBody;
import com.backend.entity.Role;
import com.backend.exception.UserAlreadyExistsException;
import com.backend.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_shouldReturn200_whenValidRequest() throws Exception {
        RegistrationBody registration = new RegistrationBody(
                "Test User",
                "test@example.com",
                "password123",
                null,
                "123 Main St",
                "0712345678",
                Role.CLIENT
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_shouldReturn409_whenUserAlreadyExists() throws Exception {
        RegistrationBody registration = new RegistrationBody(
                "Test User",
                "test@example.com",
                "password123",
                null,
                "123 Main St",
                "0712345678",
                Role.CLIENT
        );

        doThrow(new UserAlreadyExistsException("User exists"))
                .when(authenticationService).registerUser(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_shouldReturn200_whenCredentialsAreCorrect() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");
        LoginResponseDTO responseDTO = new LoginResponseDTO("token123", "test@example.com", "CLIENT", null);

        when(authenticationService.login(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        LoginDTO loginDTO = new LoginDTO("wrong@example.com", "wrongpass");

        when(authenticationService.login(any()))
                .thenThrow(new RuntimeException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }
}
