package com.ll.jumptospringboot.global.exception;

import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        return ResponseEntity.ok().body(authResponse);
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

    //예상치 못한 데이터베이스 예외 로길
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AuthResponse>DataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException", e);
        AuthResponse authResponse = new AuthResponse(e.getMessage());
        return ResponseEntity.badRequest().body(authResponse);
    }

    //최상위 예외 클래스를 핸들링하여 로그가 클라이언트에게 빠지지 않도록 함
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AuthResponse>runTimeExceptionHandler(RuntimeException e) {
        log.error("예상치못한 예외", e);
        AuthResponse authResponse = new AuthResponse(e.getMessage());
        return ResponseEntity.internalServerError().body(authResponse);
    }
}
