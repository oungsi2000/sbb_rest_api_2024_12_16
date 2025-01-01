package com.ll.jumptospringboot.global.standard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseResponse {
    private String message;
    @JsonProperty(value = "status_code")
    private Integer statusCode = 200;

    public BaseResponse(String message) {
        this.message = message;
    }
    public BaseResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
