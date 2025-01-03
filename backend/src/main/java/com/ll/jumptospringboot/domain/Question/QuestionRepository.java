package com.ll.jumptospringboot.domain.Question;

import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("select\n" +
        "        distinct q1_0\n" +
        "    from\n" +
        "        Question q1_0 \n" +
        "    where\n"+
        "        q1_0.title like %:kw%\n" +
        "        or q1_0.content like %:kw%\n"
          )
    //TODO 검색어가 없으면 where절을 뺀 findAll 메서드 만들기
    Page<Question> findAll(Pageable pageable, @Param(value="kw") String kw);
    List<Question> findAllByAuthor(SiteUser user);

}
