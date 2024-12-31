package com.ll.jumptospringboot.domain.User;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Answer.AnswerDto;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Comment.CommentDto;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.Question.QuestionDto;
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
