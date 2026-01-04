package com.akyabhishek.repository;

public class UserRepository {
    public String findByEmail(String email) {

        System.out.println("Real database query for: " + email);
        return "RealDB-12345";  // Pretend this came from database
    }
}
