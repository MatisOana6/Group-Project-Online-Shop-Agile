package com.backend.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class PasswordResetDTO {
    private String token;
    private String newPassword;
}
