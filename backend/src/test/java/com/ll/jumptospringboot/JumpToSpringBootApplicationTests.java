package com.ll.jumptospringboot;

import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Question.QuestionRepository;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserService;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
class JumpToSpringBootApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        for (int i = 1; i <= 200000; i++) {
            Question q1 = new Question();
            q1.setTitle("sbb가 무엇인가요?");
            q1.setContent("sbb에 대해서 알고 싶습니다.");
            q1.setCreateDate(LocalDateTime.now());
            this.questionRepository.save(q1);
        }

    }

    @Test
    @DisplayName("더미 답변 데이터 생성")
    @Transactional
    @Rollback(false)
    void t2() {
        Question question = this.questionService.getQuestion(2);
        SiteUser user = this.userService.getUser("oungsi1000");
        for (int i = 1; i <= 100; i++) {
            String content = "테스트 답변입니다";
            this.answerService.create(question, content, user);
        }
    }

    @Test
    @DisplayName("총 답변 개수 확인")
    @Transactional
    void t3() {
        Question question = this.questionService.getQuestion(1);
        this.questionService.getQuestion(1);
        System.out.println(question.getAnswerList().size());
    }

    @Test
    @DisplayName("리스트 가져오기 속도 확인")
    void t4() {
        long st = System.currentTimeMillis();
        Page<Question> paging = questionService.getList(0, "d", "recent-answer");
        long et = System.currentTimeMillis();
        long difference = et-st;
        System.out.println(difference + "ms");

    }


}
