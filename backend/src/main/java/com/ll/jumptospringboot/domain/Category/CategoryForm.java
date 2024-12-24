package com.ll.jumptospringboot.domain.Category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max = 200, message = "200자 이하로 입력해야 합니다.")
    private String title;

    private String info;

}