package com.ll.jumptospringboot.domain.Answer;

import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.exception.AlreadyVotedException;
import com.ll.jumptospringboot.exception.DataNotFoundException;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    private boolean isAlreadyVoted (SiteUser user, Integer AnswerId) {
        Optional<Answer> currentAnswer = answerRepository.findById(AnswerId);
        assert currentAnswer.isPresent() : "question should be found when voting";
        return currentAnswer.get().getVoterInfo().contains(user);
    }

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setTitle("title");
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    @Transactional
    public void vote(Answer answer, SiteUser siteUser) {
        if (isAlreadyVoted(siteUser, answer.getId())) {
            throw new AlreadyVotedException("이미 추천하였습니다");
        }
        answer.setVoter(answer.getVoter()+1);
        this.answerRepository.save(answer);
    }

    public Page<Answer> getList(Question question, int page, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (Objects.equals(sortBy, "mostVoted")) {
            sorts.add(Sort.Order.desc("voter"));
            sorts.add(Sort.Order.desc("createDate"));
            Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
            return this.answerRepository.findAllByQuestion(question, pageable);
        } else {
            sorts.add(Sort.Order.desc("createDate"));
            Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
            return this.answerRepository.findAllByQuestion(question, pageable);
        }
    }

    public List<Answer> getAnswerByUser(SiteUser user) {
        return answerRepository.findAllByAuthor(user);
    }
}
