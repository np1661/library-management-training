package com.training.librarymanagementtraining.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.training.librarymanagementtraining.entity.User;
import com.training.librarymanagementtraining.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdminUser(UserRepository userRepository,PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setMemberId(null);

                userRepository.save(admin);

                System.out.println("Default ADMIN user created");
            }
        };
    }
}