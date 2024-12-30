package com.ll.jumptospringboot.global.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private String message;
    @JsonProperty(value = "status_code")
    private Integer statusCode = 200;

    public AuthResponse(String message) {
        this.message = message;
    }
    public AuthResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
