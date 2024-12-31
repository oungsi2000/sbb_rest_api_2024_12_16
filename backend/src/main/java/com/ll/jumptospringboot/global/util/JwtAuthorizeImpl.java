package com.ll.jumptospringboot.global.util;

import com.ll.jumptospringboot.AppController;
import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.ll.jumptospringboot.AppController.*;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class JwtAuthorizeImpl {

    @Autowired
    private AppController appController;

    @Around("@annotation(jwtAuthorize)")
    public Object authorize(ProceedingJoinPoint joinPoint, JwtAuthorize jwtAuthorize) throws Throwable {
        UserRole role = jwtAuthorize.role();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("RequestContext is not available");
        }
        HttpServletRequest request = attributes.getRequest();
        UserContextDto userContext = appController.getUserContext(request);

        if (userContext.getRole().compareTo(role) > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        Object proceed = joinPoint.proceed();

        return proceed;

    }
}
