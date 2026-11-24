package com.doormate.doormat_catalog.config;

import com.doormate.doormat_catalog.entity.Admin;
import com.doormate.doormat_catalog.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin on first run if none exists
        if (adminRepository.count() == 0) {
            Admin admin = Admin.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .build();
            adminRepository.save(admin);
            System.out.println("Default Admin Created -> Username: admin | Password: admin123");
        }
    }
}
