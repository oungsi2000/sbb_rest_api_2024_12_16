package com.ll.jumptospringboot.domain.Answer;

import java.time.LocalDateTime;
import java.util.Set;

import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
