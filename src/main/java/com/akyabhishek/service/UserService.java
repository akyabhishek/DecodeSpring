package com.akyabhishek.service;

import com.akyabhishek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public String findByUserName(String username){
        System.out.println("Finding user by username: " + username);
        // For testing, return a hardcoded value
        if ("testuser".equals(username)) {
            return "UserID-12345";
        } else {
            return null;
        }
    }

    public String findUserByEmail(String email){
        System.out.println("Finding user by email: " + email);
        return userRepository.findByEmail(email);
    }

}
