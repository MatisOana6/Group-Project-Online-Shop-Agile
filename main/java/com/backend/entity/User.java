package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

@Table(name = "\"USER\"")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idUser;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(name = "preference")
    private String preference;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "seller")
    private List<SellerCategory> sellerCategories;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "seller",cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiration")
    private Long passwordResetTokenExpiration;


}
