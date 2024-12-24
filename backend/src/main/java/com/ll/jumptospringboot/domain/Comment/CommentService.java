package com.ll.jumptospringboot.domain.Comment;

import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final QuestionService questionService;
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

    public Map<Integer, List<Comment>> getAnswerComments(Question question) {
        Map<Integer, List<Comment>> result = new HashMap<>();
        commentRepository.findAllAnswerComments(question).forEach(
            (it)->{
                Integer answerId = (Integer) it[1];
                Comment comment = (Comment) it[0];
                result.putIfAbsent(answerId, new ArrayList<>());
                result.get(answerId).add(comment);
            }
        );
        return result;
    }

    public List<Comment> getCommentByUser(SiteUser user) {
        return commentRepository.findAllByAuthor(user);
    }
}
