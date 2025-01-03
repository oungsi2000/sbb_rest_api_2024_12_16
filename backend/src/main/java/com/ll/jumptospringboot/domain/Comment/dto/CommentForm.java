package com.ll.jumptospringboot.domain.Comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    @NotNull(message="id는 필수 항목입니다")
    // answer에선 answer의 id로쓰이고, question에선 question의 id로 쓰입니다
    //Todo question과 answer의 id를 따로 분리하여 만드는 것 (실수가 많을 때)
    private Integer id;
}

