package com.akyabhishek.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AppProperty {
    private String name;
    public String description;
    private String version;
}
