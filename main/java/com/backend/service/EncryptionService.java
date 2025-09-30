package com.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    @Value("${encryption.salt.rounds:12}")
    private int saltRounds;

    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(saltRounds));
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
