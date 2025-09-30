package com.backend.dto;

import com.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID idUser;
    private String name;
    private String email;
    private String profileImage;
    private String preference;
    private String address;
    private String phoneNumber;
    private Role role;
}
