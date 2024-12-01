package com.kai.Registration.service;

import com.kai.Registration.entity.User;
import com.kai.Registration.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // JWT Configuration from application.properties
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Login a user and generate a JWT token.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return A JWT token.
     */
    public String login(String email, String password) {
        // Fetch the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Verify the password
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate and return the JWT token
        return generateJwtToken(user);
    }

    /**
     * Generate a JWT token for the authenticated user.
     *
     * @param user The authenticated user.
     * @return A JWT token.
     */

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("isActive", user.getIsActive())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token expires in 1 day
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Hash a plain text password.
     *
     * @param plainPassword The plain text password.
     * @return The hashed password.
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Register a new user by saving them to the database.
     *
     * @param user The new user to register.
     * @return The saved user entity.
     */
    public User registerUser(User user) {
        // Hash the user's password before saving
        user.setPasswordHash(hashPassword(user.getPasswordHash()));

        // Set default values for the user
        user.setIsActive(false); // Default to inactive until email verification

        return userRepository.save(user);
    }
}
