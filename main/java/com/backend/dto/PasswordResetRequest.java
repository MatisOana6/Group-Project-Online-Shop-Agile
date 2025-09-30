package com.backend.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class PasswordResetRequest {
    private String email;

}
