package com.ll.jumptospringboot.domain.User;

import com.ll.jumptospringboot.domain.Answer.dto.AnswerDto;
import com.ll.jumptospringboot.domain.Comment.dto.CommentDto;
import com.ll.jumptospringboot.domain.Question.dto.QuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDataDto {
    private SiteUser user;
    private List<QuestionDto> questions;
    private List<AnswerDto> answers;
    private List<CommentDto> comments;
}
