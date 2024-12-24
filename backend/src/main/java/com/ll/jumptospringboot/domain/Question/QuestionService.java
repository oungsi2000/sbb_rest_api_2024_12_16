package com.ll.jumptospringboot.domain.Question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.exception.AlreadyVotedException;
import com.ll.jumptospringboot.domain.Category.Category;
import com.ll.jumptospringboot.domain.Category.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.exception.DataNotFoundException;
import com.ll.jumptospringboot.domain.User.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    private boolean isAlreadyVoted (SiteUser user, Question question) {
        Optional<Question> currentQuestion = questionRepository.findById(question.getId());
        assert currentQuestion.isPresent() : "question should be found when voting";
        return currentQuestion.get().getVoterInfo().contains(user);
    }

    public Page<Question> getList(int page, String kw, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (Objects.equals(sortBy, "recent-answer")) {
            sorts.add(Sort.Order.desc("lastAnsweredAt"));
        } else if (Objects.equals(sortBy, "recent-comment")) {
            sorts.add(Sort.Order.desc("lastCommentedAt"));
        } else {
            sorts.add(Sort.Order.desc("createDate"));
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            setView(question.get());
            question.get().getAnswerList().sort((a,b)->b.getVoter() - a.getVoter());
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    private void setView(Question question) {
        question.setView(question.getView()+1);
        questionRepository.save(question);
    }

    public void create(QuestionForm questionForm, SiteUser user) {
        Question q = new Question();
        if (questionForm.getCategoryId() != null) {
            Optional<Category> c =  categoryRepository.findById(questionForm.getCategoryId());
            c.ifPresent(q::setCategory);
        }
        q.setTitle(questionForm.getSubject());
        q.setContent(questionForm.getContent());
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content) {
        question.setTitle(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    @Transactional
    public void vote(Question question, SiteUser siteUser) {
        if (isAlreadyVoted(siteUser, question)) {
            throw new AlreadyVotedException("이미 추천하였습니다");
        }
        question.setVoter(question.getVoter()+1);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("title"), "%" + kw + "%"), // 제목
                    cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                    cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                    cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                    cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public List<Question> getQuestionByUser(SiteUser user) {
        return questionRepository.findAllByAuthor(user);
    }
}
