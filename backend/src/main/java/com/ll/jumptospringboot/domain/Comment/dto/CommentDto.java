package com.ll.jumptospringboot.domain.Comment.dto;

import com.ll.jumptospringboot.domain.Comment.entity.Comment;
import com.ll.jumptospringboot.domain.User.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Integer id;

    private String content;

    private SiteUser author;

    private Integer questionId;

    private Integer answerId;

    private LocalDateTime createDate;

    public CommentDto (Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor();
        this.createDate = comment.getCreateDate();
    }

}
