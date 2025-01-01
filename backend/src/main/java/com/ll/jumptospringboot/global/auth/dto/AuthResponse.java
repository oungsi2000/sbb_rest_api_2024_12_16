package com.ll.jumptospringboot.global.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.jumptospringboot.global.auth.standard.BaseResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse extends BaseResponse {

    public AuthResponse(String message) {
        super(message);
    }
    public AuthResponse(String message, Integer statusCode) {
        super(message, statusCode);

    }

}
