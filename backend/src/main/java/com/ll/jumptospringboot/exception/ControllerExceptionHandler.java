package com.ll.jumptospringboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
//글로벌 예외 처리 맛보기...REST로 바꾸면 bindingResult를 싹 글로벌 예외 처리 방식으로 갈아엎을 예정
public class ControllerExceptionHandler {

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<ErrorResponse> AlreadyVotedExceptionHandler(AlreadyVotedException e) {
        log.error("AlreadyVotedException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> DataNotFoundExceptionHandler(DataNotFoundException e) {
        log.error("DataNotFoundException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PasswordNotSameException.class)
    public ResponseEntity<ErrorResponse> PasswordNotSameExceptionHandler(PasswordNotSameException e) {
        log.error("PasswordNotSameException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<ErrorResponse>UserDuplicateExceptionHandler(UserDuplicateException e) {
        log.error("UserDuplicateException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
