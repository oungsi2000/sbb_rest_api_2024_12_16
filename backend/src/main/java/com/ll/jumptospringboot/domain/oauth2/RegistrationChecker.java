package com.ll.jumptospringboot.domain.oauth2;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RegistrationChecker implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession() == null) return true;
        HttpSession session = request.getSession();
        if (session.getAttribute("isFulfilled") == null) return true;

        if (!(Boolean) session.getAttribute("isFulfilled")) {
            session.invalidate();
            return true;
        } else {
            session.setAttribute("isFulfilled", true);
        }

        return true; // 어노테이션이 없거나 인증 성공 시 컨트롤러 실행
    }
}
