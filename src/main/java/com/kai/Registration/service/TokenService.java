package com.kai.Registration.service;

import com.kai.Registration.entity.Token;
import com.kai.Registration.entity.TokenType;
import com.kai.Registration.entity.User;
import com.kai.Registration.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public Token createActivationToken(User user) {
        Token token = Token.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .type(TokenType.ACTIVATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        return tokenRepository.save(token);
    }
}

