package com.akyabhishek;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HookTests {

    @BeforeAll
    public static void setup() {
        System.out.println("Setting up before all tests...");
    }
    @BeforeEach
    public void init() {
        System.out.println("Initializing before each test...");
    }
    @Test
    public void sampleTest() {
        System.out.println("Executing sample test...");
    }

    @AfterEach
    public void cleanup() {
        System.out.println("Cleaning up after each test...");
    }

    @AfterAll
    public static void teardown() {
        System.out.println("Tearing down after all tests...");
    }

}
