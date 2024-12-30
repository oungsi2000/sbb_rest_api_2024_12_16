package com.ll.jumptospringboot.domain.User;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    TEMPORARY_USER("ROLE_TEMPORARY"),
    ANONYMOUS("ROLE_ANONYMOUS");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
