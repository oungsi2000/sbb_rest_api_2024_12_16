package com.ll.jumptospringboot.domain.Comment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    @JsonBackReference
    private Question question;

    @ManyToOne
    @JsonBackReference
    private Answer answer;

    private LocalDateTime createDate;

    @PrePersist
    @PreUpdate
    public void updateLastCommentedAt() {
        if(this.question != null){
            this.question.setLastCommentedAt(LocalDateTime.now());
        }
        if (this.answer != null) {
            this.answer.getQuestion().setLastCommentedAt(LocalDateTime.now());
        }

    }
}