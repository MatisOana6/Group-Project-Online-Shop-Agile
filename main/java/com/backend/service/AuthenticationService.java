package com.backend.service;

import com.backend.dto.LoginDTO;
import com.backend.dto.LoginResponseDTO;
import com.backend.dto.RegistrationBody;
import com.backend.entity.User;
import com.backend.exception.UserAlreadyExistsException;
import com.backend.exception.UserNotFoundException;
import com.backend.mapper.UserMapper;
import com.backend.neo4j.service.UserGraphService;
import com.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserGraphService userGraphService;

    @Autowired
    private JavaMailSender mailSender;

    public AuthenticationService(EncryptionService encryptionService, UserRepository userRepository, UserMapper userMapper, JwtService jwtService, JavaMailSender mailSender, PasswordEncoder passwordEncoder, UserGraphService userGraphService) {
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.userGraphService = userGraphService;
    }

    @Transactional("transactionManager")
    public void registerUser(RegistrationBody registrationBody) {
        if (userRepository.existsByEmail(registrationBody.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + registrationBody.getEmail());
        }

        String encryptedPassword = encryptionService.encryptPassword(registrationBody.getPassword());
        User user = userMapper.toUser(registrationBody, encryptedPassword);

        userRepository.save(user);

        userGraphService.createUserNode(user.getIdUser());
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null || !encryptionService.verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateJwtToken(
                user.getEmail(),
                String.valueOf(user.getIdUser()),
                user.getRole().name()
        );

        return new LoginResponseDTO(token, user.getEmail(), user.getRole().name(), user.getProfileImage());
    }


    public boolean initiatePasswordReset(String email) {
        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {

            String token = UUID.randomUUID().toString();
            Long expirationTime = System.currentTimeMillis() + 3600000;

            existingUser.setPasswordResetToken(token);
            existingUser.setPasswordResetTokenExpiration(expirationTime);
            userRepository.save(existingUser);

            sendPasswordResetEmail(existingUser.getEmail(), token);
            return true;
        } else {
            throw new UserNotFoundException("User not found with email: " + email);
        }
    }

    private void sendPasswordResetEmail(String toEmail, String token) {
        System.out.println("Attempting to send password reset email to: " + toEmail);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String emailContent = "<html><body>" +
                "<h3>Password Reset Request</h3>" +
                "<p>We received a request to reset your password. Please click the link below to reset your password:</p>" +
                "<p><a href=\"" + resetLink + "\">Reset your password</a></p>" +
                "<p>If you did not request a password reset, please ignore this email. The link will expire in 1 hour.</p>" +
                "</body></html>";

        try {
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setText(emailContent, true);
            mailSender.send(message);
            System.out.println("Password reset email sent successfully to: " + toEmail);
        } catch (MessagingException e) {

            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token);

        if (user == null || user.getPasswordResetTokenExpiration() < System.currentTimeMillis()) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(hashedPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiration(null);
        userRepository.save(user);

        return true;
    }



}
