package com.ll.jumptospringboot.domain.Answer.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 200)
    private String title;

    private LocalDateTime createDate;

    @ManyToOne
    @JsonBackReference
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @Column(nullable = false)
    private Integer voter = 0;

    @ManyToMany
    private Set<SiteUser> voterInfo;

    @PrePersist
    @PreUpdate
    public void updateQuestionLastAnsweredAt() {
        if (this.question != null) {
            this.question.setLastAnsweredAt(LocalDateTime.now());
        }
    }
}