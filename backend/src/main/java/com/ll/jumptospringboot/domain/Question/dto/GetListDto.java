package com.ll.jumptospringboot.domain.Question.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetListDto {
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private String kw = "";
    @Builder.Default
    private String sortBy = "";

}
