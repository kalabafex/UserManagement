package com.kai.Registration.service;

import com.kai.Registration.entity.User;
import com.kai.Registration.repository.UserRepository;
import com.kai.Registration.entity.Role;
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
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("your-512-bit-secret-key-example-this-is-a-secure-key-and-must-be-long-enough".getBytes());

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
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_USER"); // Default to "ROLE_USER" if no roles assigned
        // Generate and return the JWT token
        return generateJwtToken(user, role);
    }

    /**
     * Generate a JWT token for the authenticated user.
     *
     * @param user The authenticated user.
     * @return A JWT token.
     */

    public String generateJwtToken(User user, String role) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("role", role)
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

        return userRepository.save(user);
    }
}
