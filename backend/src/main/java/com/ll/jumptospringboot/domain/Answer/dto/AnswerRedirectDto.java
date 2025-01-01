package com.ll.jumptospringboot.domain.Answer.dto;

import com.ll.jumptospringboot.global.standard.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRedirectDto extends BaseResponse {
    private String redirectUrl;
    public AnswerRedirectDto(String message) {
        super(message);
    }
    public AnswerRedirectDto(String message, Integer statusCode) {
        super(message, statusCode);
    }
}
