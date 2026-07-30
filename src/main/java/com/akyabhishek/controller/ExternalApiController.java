package com.akyabhishek.controller;

import com.akyabhishek.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/external")
public class ExternalApiController {

    @Autowired
    private ExternalApiService externalApiService;


    @GetMapping("/api1")
    public ResponseEntity<?> getExternalApiData(@RequestParam String name) {
        externalApiService.callExternalApi();
        return ResponseEntity.ok("Hello " + name + ", this is data from external API!");
    }
}
