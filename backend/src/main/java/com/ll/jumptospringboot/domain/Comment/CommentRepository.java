package com.ll.jumptospringboot.domain.Comment;

import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByQuestion(Question question);
    @Query("select c, a.id\n" +
        "from Comment c\n" +
        "join c.answer a\n" +
        "join c.author u\n" +
        "where a.question =:question")
    List<Object[]> findAllAnswerComments(@Param("question") Question question);
    List<Comment> findAllByAuthor(SiteUser user);
}
