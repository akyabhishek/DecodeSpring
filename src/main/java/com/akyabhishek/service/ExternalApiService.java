package com.akyabhishek.service;

import com.akyabhishek.constants.Constants;
import com.akyabhishek.pojo.ExternalApiCallPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    @Autowired
    RestTemplate restTemplate;

    public Logger log= LoggerFactory.getLogger(this.getClass());

    public void callExternalApi(){

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", Constants.apiKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ExternalApiCallPojo> response = restTemplate.exchange(Constants.endpoint, HttpMethod.GET,request, ExternalApiCallPojo.class);
        if(response.getStatusCode().is2xxSuccessful()){
            ExternalApiCallPojo externalApiService = response.getBody();
            log.info("External API Response: " + externalApiService.toString());
        }
        else{
            log.error("External API Error: " + response.getBody().toString());
        }
    }
}
