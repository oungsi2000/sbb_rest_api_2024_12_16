package com.ll.jumptospringboot.global.util.annotation;

import com.ll.jumptospringboot.domain.User.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 런타임까지 어노테이션 정보 유지
@Target(ElementType.METHOD)
public @interface JwtAuthorize {
    String value() default "";
    UserRole role() default UserRole.ANONYMOUS;
}
