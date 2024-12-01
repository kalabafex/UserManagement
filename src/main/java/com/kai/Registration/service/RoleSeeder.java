package com.kai.Registration.service;

import com.kai.Registration.entity.Role;
import com.kai.Registration.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("USER") == null) {
            roleRepository.save(Role.builder().name("USER").build());
        }
        if (roleRepository.findByName("ADMIN") == null) {
            roleRepository.save(Role.builder().name("ADMIN").build());
        }
    }
}

