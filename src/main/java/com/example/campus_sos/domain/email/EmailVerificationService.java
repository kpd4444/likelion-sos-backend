package com.example.campus_sos.domain.email;

// EmailVerificationService.java

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailVerificationService {
    private final Map<String, String> tokenStorage = new HashMap<>();
    private final Map<String, Boolean> verifiedStorage = new HashMap<>();

    public void saveToken(String email, String token) {
        tokenStorage.put(email, token);
        verifiedStorage.put(email, false);
    }

    public boolean isValid(String email, String token) {
        return token.equals(tokenStorage.get(email));
    }

    public void markAsVerified(String email) {
        verifiedStorage.put(email, true);
    }

    public boolean isVerified(String email) {
        return verifiedStorage.getOrDefault(email, false);
    }
}
