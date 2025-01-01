package com.ll.jumptospringboot.global.exception;

import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
//글로벌 예외 처리 맛보기...REST로 바꾸면 bindingResult를 싹 글로벌 예외 처리 방식으로 갈아엎을 예정
public class ControllerExceptionHandler {

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<AuthResponse> AlreadyVotedExceptionHandler(AlreadyVotedException e) {
        log.error("AlreadyVotedException", e);
        AuthResponse authResponse = new AuthResponse(e.getMessage());
        return ResponseEntity.badRequest().body(authResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<AuthResponse> DataNotFoundExceptionHandler(DataNotFoundException e) {
        log.error("DataNotFoundException", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PasswordNotSameException.class)
    public ResponseEntity<AuthResponse> PasswordNotSameExceptionHandler(PasswordNotSameException e) {
        log.error("PasswordNotSameException", e);
        AuthResponse authResponse = new AuthResponse(e.getMessage());
        return ResponseEntity.badRequest().body(authResponse);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<AuthResponse>UserDuplicateExceptionHandler(UserDuplicateException e) {
        log.error("UserDuplicateException", e);
        AuthResponse authResponse = new AuthResponse(e.getMessage());
        return ResponseEntity.badRequest().body(authResponse);
    }
}
