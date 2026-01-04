package com.akyabhishek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTests {
    @Mock
    UserService userService;

    @InjectMocks
    UserService userServiceImpl;

//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
    @Test
    @Disabled
    public void testMockedUserService() {
        when(userService.findByUserName("mockuser")).thenReturn("MockedUserID-99999");
        assertEquals("MockedUserID-99999", userService.findByUserName("mockuser"),"User ID should match mocked value");
    }

    @Test
    public void testMockedUserServiceWithInjectMocks() {
        when(userServiceImpl.findByUserName("mockuser")).thenReturn("MockedUserID-99999");
        assertEquals("MockedUserID-99999", userServiceImpl.findByUserName("mockuser"),"User ID should match mocked value via InjectMocks");
    }
}
