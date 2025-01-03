package com.ll.jumptospringboot.domain.Question;

import java.time.LocalDateTime;
import java.util.*;

import com.ll.jumptospringboot.domain.Answer.dto.AnswerDto;
import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Comment.dto.CommentDto;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.dto.QuestionDetailDto;
import com.ll.jumptospringboot.domain.Question.dto.QuestionDto;
import com.ll.jumptospringboot.domain.Question.dto.QuestionForm;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.global.exception.AlreadyVotedException;
import com.ll.jumptospringboot.domain.Category.entity.Category;
import com.ll.jumptospringboot.domain.Category.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.global.exception.DataNotFoundException;
import com.ll.jumptospringboot.domain.User.SiteUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerService answerService;
    private final CommentService commentService;


    private boolean isAlreadyVoted (SiteUser user, Question question) {
        Optional<Question> currentQuestion = questionRepository.findById(question.getId());
        assert currentQuestion.isPresent() : "question should be found when voting";
        return currentQuestion.get().getVoterInfo().contains(user);
    }

    //Todo voterInfo 가져올 때 n+1 문제 해결, CriteriaBuilder와 결합
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

        return this.questionRepository.findAll(pageable, kw);


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

    //검색 쿼리 CriteriaBuilder로 빌드
    /**
     * &#064;Deprecated 기존 criteriaBuilder에서 jpql로 가져오는 방식으로 변경
     */
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

    public List<QuestionDto> getQuestionByUser(SiteUser user) {
        return questionRepository.findAllByAuthor(user).stream().map(
            this::toQuestionDto
        ).toList();
    }

    public QuestionDto toQuestionDto(Question question) {
        QuestionDto questionDto = new QuestionDto(question);
        List<AnswerDto> answerDtoList = new ArrayList<>();
        List<CommentDto> commentDtoList = new ArrayList<>();

        question.getAnswerList().forEach(
            answer -> {
                AnswerDto answerDto = AnswerService.toAnswerDto(answer);
                answerDtoList.add(answerDto);
            }
        );
        question.getComments().forEach(
            comment -> {
                CommentDto commentDto = CommentService.toCommentDto(comment);
                commentDtoList.add(commentDto);
            }
        );
        questionDto.setAnswerList(answerDtoList);
        questionDto.setComments(commentDtoList);
        return questionDto;
    }

    public QuestionDetailDto getQuestionDetailDto(Integer id, int idx, String sortBy) {
        Question question = getQuestion(id);
        Page<AnswerDto> answerList = answerService.getList(question, idx, sortBy);
        List<CommentDto> questionComments = commentService.getQuestionComments(question).stream().map(
            CommentService::toCommentDto
        ).toList();
        Map<Integer, List<CommentDto>> answerComments = commentService.getAnswerComments(question);

        QuestionDetailDto questionDetailDto = new QuestionDetailDto();
        questionDetailDto.setQuestion(toQuestionDto(question));
        questionDetailDto.setAnswers(answerList);
        questionDetailDto.setQuestionComments(questionComments);
        questionDetailDto.setAnswerComments(answerComments);

        return questionDetailDto;
    }
}
