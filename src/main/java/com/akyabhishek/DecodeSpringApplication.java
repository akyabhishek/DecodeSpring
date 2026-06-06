package com.akyabhishek;

import com.akyabhishek.pojo.AppProperty;
import com.akyabhishek.pojo.AppPropertyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppPropertyRecord.class, AppProperty.class})
public class DecodeSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(DecodeSpringApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
