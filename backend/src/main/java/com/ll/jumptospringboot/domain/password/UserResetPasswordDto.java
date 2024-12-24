package com.ll.jumptospringboot.domain.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResetPasswordDto {

    private String password;

    //TODO 사용자 정의 어노테이션으로 패스워드 검증..근데 귀찮으니 프론트단에서만 일단 처리함
    private String newPassword;
    private String newPasswordConfirm;
}
