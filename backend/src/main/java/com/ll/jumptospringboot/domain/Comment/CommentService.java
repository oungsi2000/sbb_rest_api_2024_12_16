package com.ll.jumptospringboot.domain.Comment;

import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.domain.Comment.dto.CommentDto;
import com.ll.jumptospringboot.domain.Comment.entity.Comment;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Lazy
    @Autowired
    private QuestionService questionService;
    private final AnswerService answerService;
    private final CommentRepository commentRepository;

    public void createQuestionComment(SiteUser user, String content, Integer id) {
        Comment comment = new Comment();
        Question question = questionService.getQuestion(id);
        comment.setContent(content);
        comment.setQuestion(question);
        comment.setAuthor(user);
        commentRepository.save(comment);
    }

    public void createAnswerComment(SiteUser user, String content, Integer id) {
        Comment comment = new Comment();
        Answer answer = answerService.getAnswer(id);
        comment.setContent(content);
        comment.setAnswer(answer);
        comment.setAuthor(user);
        commentRepository.save(comment);
    }

    public List<Comment> getQuestionComments(Question question) {
        return commentRepository.findAllByQuestion(question);
    }

    public Map<Integer, List<CommentDto>> getAnswerComments(Question question) {
        Map<Integer, List<CommentDto>> result = new HashMap<>();
        commentRepository.findAllAnswerComments(question).forEach(
            (it)->{
                Integer answerId = (Integer) it[1];
                Comment comment = (Comment) it[0];
                result.putIfAbsent(answerId, new ArrayList<>());
                result.get(answerId).add(toCommentDto(comment));
            }
        );
        return result;
    }

    public List<CommentDto> getCommentByUser(SiteUser user) {
        return commentRepository.findAllByAuthor(user).stream().map(
            CommentService::toCommentDto
        ).toList();
    }
    public static CommentDto toCommentDto (Comment comment) {
        CommentDto commentDto = new CommentDto(comment);

        if (comment.getAnswer() != null) {
            commentDto.setAnswerId(comment.getAnswer().getId());
        }

        if (comment.getQuestion() != null) {
            commentDto.setQuestionId(comment.getQuestion().getId());
        }
        return commentDto;
    }
}
