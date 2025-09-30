package com.backend.dto;

import com.backend.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggedUserDTO {
    private String name;
    private String email;
    private String profileImage;
    private String preference;
    private String address;
    private String phoneNumber;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
}
