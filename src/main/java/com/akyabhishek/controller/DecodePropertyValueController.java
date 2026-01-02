package com.akyabhishek.controller;

import com.akyabhishek.pojo.AppProperty;
import com.akyabhishek.pojo.AppPropertyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Response;


@RestController
public class DecodePropertyValueController {

    @Value("${build.version}")
    public String buildVersion;

    @Autowired
    public AppProperty appProperty;
    @Autowired
    public AppPropertyRecord appPropertyRecord;

    @Autowired
    private Environment environment;

    @GetMapping("/version")
    public ResponseEntity<?> getBuildVersion() {
        return ResponseEntity.status(HttpStatus.OK).body("Build Version: " + buildVersion);
    }
    @GetMapping("/java-version")
    public ResponseEntity<?> getBuildJavaVersion() {
        return ResponseEntity.status(HttpStatus.OK).body("Java Version: " + environment.getProperty("JAVA_VERSION"));
    }

    @GetMapping("/config-property")
    public ResponseEntity<?> getConfigProperty() {
        return ResponseEntity.status(HttpStatus.OK).body(appProperty);
    }

    @GetMapping("/config-property-r")
    public ResponseEntity<?> getConfigPropertyR() {
        return ResponseEntity.status(HttpStatus.OK).body(appPropertyRecord);
    }

}
