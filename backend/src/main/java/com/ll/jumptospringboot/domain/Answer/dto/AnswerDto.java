package com.ll.jumptospringboot.domain.Answer.dto;

import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.domain.User.SiteUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {

    private Integer id;

    private String content;

    private String title;

    private LocalDateTime createDate;

    private Integer questionId;

    private SiteUser author;

    private LocalDateTime modifyDate;

    private Integer voter = 0;
    private Set<SiteUser> voterInfo;

    public AnswerDto(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.title = answer.getTitle();
        this.createDate = answer.getCreateDate();
        this.author = answer.getAuthor();
        this.modifyDate = answer.getModifyDate();
        this.voter = answer.getVoter();
        this.voterInfo = answer.getVoterInfo();
    }

}
