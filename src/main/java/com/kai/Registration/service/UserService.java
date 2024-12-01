package com.kai.Registration.service;

import com.kai.Registration.entity.Role;
import com.kai.Registration.entity.User;
import com.kai.Registration.repository.RoleRepository;
import com.kai.Registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User registerUser(User user) {
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new IllegalStateException("Default role not found");
        }
        user.setPasswordHash(hashPassword(user.getPasswordHash()));
        user.setIsActive(false);
        return userRepository.save(user);
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}

