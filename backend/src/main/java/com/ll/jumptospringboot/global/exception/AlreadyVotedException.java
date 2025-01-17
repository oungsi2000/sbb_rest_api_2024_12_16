package com.ll.jumptospringboot.global.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK, reason = "already voted")
public class AlreadyVotedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadyVotedException(String message) {
        super(message);
    }
}