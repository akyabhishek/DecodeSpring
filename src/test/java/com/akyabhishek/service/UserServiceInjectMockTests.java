package com.akyabhishek.service;

import com.akyabhishek.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceInjectMockTests {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    public void testWithMockAndInjectMocks() {
        // Arrange: Tell the mock what to return
        when(userRepository.findByEmail("testuser"))
                .thenReturn("MockedDB-99999");

        String result = userService.findUserByEmail("testuser");

        // Assert
        assertEquals("MockedDB-99999", result);

        // Verify the mock was called
        verify(userRepository).findByEmail("testuser");
    }

}
