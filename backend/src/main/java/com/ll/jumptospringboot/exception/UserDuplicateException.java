package com.ll.jumptospringboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "already voted")
public class UserDuplicateException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public UserDuplicateException(String message) {
        super(message);
    }
}
