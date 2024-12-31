package com.ll.jumptospringboot.domain.User;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Question.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDataDto {
    private SiteUser user;
    private List<Question> questions;
    private List<Answer> answers;
    private List<Comment> comments;



}
