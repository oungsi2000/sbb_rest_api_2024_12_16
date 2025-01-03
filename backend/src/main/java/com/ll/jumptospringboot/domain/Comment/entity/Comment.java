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
    /*TODO 이벤트 기반 비동기적으로 처리를 하게 된다면 서비스단에서 처리를 해야 한다
    *  하지만 그럴 경우 최선의 방법은 AOP를 이용하여 처리하는 것이다
    *  물론 이 방법도 직관적이지만 개발자의 실수를 허용하는 단점이 있다 (까먹고 어노테이션 안붙이면 업데이트 안됨)
    * 그 대신 트랜잭션이 끝난 후 별도의 스레드 또는 nio에서 저장 프로시저를 호출하여 처리하는 것이 좋아보인다
    * 하지만 한꺼번에 많은 양의 답변과, 댓글을 추가하는 것이 아닌데 성능에 조금 영향을 미치더라도
    * 안정적으로 동기적으로 처리하는 것이 더 낮지 않을까?
    */
    public void updateLastCommentedAt() {
        if(this.question != null){
            this.question.setLastCommentedAt(LocalDateTime.now());
        }
        if (this.answer != null) {
            this.answer.getQuestion().setLastCommentedAt(LocalDateTime.now());
        }

    }
}
