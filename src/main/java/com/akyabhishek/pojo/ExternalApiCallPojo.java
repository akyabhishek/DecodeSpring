package com.akyabhishek.pojo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExternalApiCallPojo {

    private User data;

    @Data
    public static class User {
        private Integer id;
        private String email;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        private String avatar;
    }
}
