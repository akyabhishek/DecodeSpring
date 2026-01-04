package com.akyabhishek.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;

    @Test
    public void testFindByUserName() {
        String userId = userService.findByUserName("testuser");
        assertNotNull(userId);
    }

    @Test
    public void testInvalidUserName() {
        String userId = userService.findByUserName("invaliduser");
        assertNull(userId);
    }



    @ParameterizedTest
    @CsvSource({
            "testuser, UserID-12345",
            "invaliduser, null"
    })

    public void test(String username, String expectedUserId) {
        String userId = userService.findByUserName(username);
        assertEquals(userId, expectedUserId.equals("null") ? null : expectedUserId, "User ID should match expected value");
    }

    @ParameterizedTest
    @ValueSource(strings = {"testuser", "invaliduser"})
    public void testWithValueSource(String username) {
        String userId = userService.findByUserName(username);
        if ("testuser".equals(username)) {
            assertEquals("UserID-12345", userId);
        } else {
            assertNull(userId);
        }
    }
}
