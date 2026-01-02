package com.akyabhishek.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppPropertyRecord(String name, String description, String version) {
}
