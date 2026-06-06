package com.akyabhishek.repository;

import org.springframework.stereotype.Component;

@Component
public class UserRepository {
    public String findByEmail(String email) {

        System.out.println("Real database query for: " + email);
        return "RealDB-12345";  // Pretend this came from database
    }
}
