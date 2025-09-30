package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileDTO {
    private String name;
    private String profileImage;
    private String preference;
    private String address;
    private String phoneNumber;
}
