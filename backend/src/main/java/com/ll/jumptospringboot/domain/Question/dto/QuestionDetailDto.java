package com.ll.jumptospringboot.domain.Question.dto;

import com.ll.jumptospringboot.domain.Answer.dto.AnswerDto;
import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.domain.Comment.dto.CommentDto;
import com.ll.jumptospringboot.domain.Comment.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class QuestionDetailDto {
    private QuestionDto question;
    private Page<AnswerDto> answers;
    private List<CommentDto> questionComments;
    private Map<Integer, List<CommentDto>> answerComments;

}
