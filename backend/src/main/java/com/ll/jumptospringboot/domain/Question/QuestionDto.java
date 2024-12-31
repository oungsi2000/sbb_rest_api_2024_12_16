package com.ll.jumptospringboot.domain.Question;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Answer.AnswerDto;
import com.ll.jumptospringboot.domain.Category.Category;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Comment.CommentDto;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class QuestionDto {

    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private List<AnswerDto> answerList;

    private SiteUser author;

    private LocalDateTime modifyDate;

    private Integer voter;

    private Set<SiteUser> voterInfo;

    private List<CommentDto> comments;

    private int view;

    private Category category;
    private LocalDateTime lastAnsweredAt;
    private LocalDateTime lastCommentedAt;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.createDate = question.getCreateDate();
        this.author = question.getAuthor();
        this.modifyDate = question.getModifyDate();
        this.voter = question.getVoter();
        this.voterInfo = question.getVoterInfo();
        this.view = question.getView();
        this.category = question.getCategory();
        this.lastAnsweredAt = question.getLastAnsweredAt();
        this.lastCommentedAt = question.getLastCommentedAt();
    }
}
