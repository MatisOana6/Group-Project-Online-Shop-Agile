package com.backend.mapper;

import com.backend.dto.LoggedUserDTO;
import com.backend.dto.RegistrationBody;
import com.backend.dto.UserDTO;
import com.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(RegistrationBody dto, String encryptedPassword) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encryptedPassword)
                .profileImage(dto.getProfileImage())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build();
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getIdUser(),
                user.getName(),
                user.getEmail(),
                user.getProfileImage(),
                user.getPreference(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

    public User toUser(UserDTO dto) {
        return User.builder()
                .idUser(dto.getIdUser())
                .name(dto.getName())
                .email(dto.getEmail())
                .profileImage(dto.getProfileImage())
                .preference(dto.getPreference())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build();
    }

    public LoggedUserDTO toLoggedUserDTO(User user) {
        return new LoggedUserDTO(
                user.getName(),
                user.getEmail(),
                user.getProfileImage(),
                user.getPreference(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
