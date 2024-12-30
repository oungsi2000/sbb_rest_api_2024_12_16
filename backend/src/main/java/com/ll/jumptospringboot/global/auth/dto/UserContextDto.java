package com.ll.jumptospringboot.global.auth.dto;

import com.ll.jumptospringboot.domain.User.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserContextDto {
    private String name;
    private UserRole role;
}
