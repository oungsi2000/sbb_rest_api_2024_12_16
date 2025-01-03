package com.ll.jumptospringboot.domain.Answer;

import com.ll.jumptospringboot.domain.Answer.dto.AnswerForm;
import com.ll.jumptospringboot.domain.Answer.dto.AnswerRedirectDto;
import com.ll.jumptospringboot.domain.Answer.entity.Answer;
import com.ll.jumptospringboot.domain.Comment.dto.CommentForm;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.domain.User.UserService;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import com.ll.jumptospringboot.global.standard.BaseResponse;
import com.ll.jumptospringboot.global.util.annotation.JwtAuthorize;
import com.ll.jumptospringboot.global.util.annotation.JwtUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/answer")
public class AnswerController {
    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final CommentService commentService;

    @JwtAuthorize(role=UserRole.USER)
    @GetMapping("/delete/{id}")
    public ResponseEntity<AnswerRedirectDto> answerDelete(@JwtUserContext UserContextDto userContextDto,
                                                          @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(userContextDto.getName()) &&
            !answer.getAuthor().getEmail().equals(userContextDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto("성공", HttpServletResponse.SC_OK);
        answerRedirectDto.setRedirectUrl("http://localhost:8080/question/detail/" + answer.getQuestion().getId());
        return ResponseEntity.ok().body(answerRedirectDto);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/vote/{id}")
    public ResponseEntity<AnswerRedirectDto> answerVote(@JwtUserContext UserContextDto userContextDto, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        this.answerService.vote(answer, siteUser);
        AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto("성공", HttpServletResponse.SC_OK);
        answerRedirectDto.setRedirectUrl("http://localhost:8080/question/detail/" + answer.getQuestion().getId());
        return ResponseEntity.ok().body(answerRedirectDto);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/create/{id}")
    public ResponseEntity<BaseResponse> createAnswer(@PathVariable("id") Integer id,
                                                     @Valid AnswerForm answerForm,
                                                     BindingResult bindingResult,
                                                     @JwtUserContext UserContextDto userContextDto) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        if (bindingResult.hasErrors()) {
            BaseResponse baseResponse = new BaseResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body(baseResponse);
        }
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/modify/{id}")
    public ResponseEntity<AnswerRedirectDto> answerModify(@Valid AnswerForm answerForm,
                                                          BindingResult bindingResult,
                                                          @PathVariable("id") Integer id,
                                                          @JwtUserContext UserContextDto userContextDto,
                                                          HttpServletRequest request) {
        String questionId = String.valueOf(answerService.getAnswer(id).getQuestion().getId());
        String referer = "http://localhost:3000/question/detail/"+ questionId;

        if (bindingResult.hasErrors()) {
            AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            answerRedirectDto.setRedirectUrl(referer);
            return ResponseEntity.badRequest().body(answerRedirectDto);
        }

        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(userContextDto.getName()) &&
            !answer.getAuthor().getEmail().equals(userContextDto.getName()) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto("성공", HttpServletResponse.SC_OK);
        answerRedirectDto.setRedirectUrl(referer);
        return ResponseEntity.ok().body(answerRedirectDto);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/create/comment")
    public ResponseEntity<AnswerRedirectDto> questionComment(@Valid CommentForm commentForm,
                                  BindingResult bindingResult,
                                  @JwtUserContext UserContextDto userContextDto,
                                  HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        if (bindingResult.hasErrors()) {
            AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            answerRedirectDto.setRedirectUrl(referer);
            return ResponseEntity.badRequest().body(answerRedirectDto);
        }
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        commentService.createAnswerComment(siteUser, commentForm.getContent(), commentForm.getId());
        AnswerRedirectDto answerRedirectDto = new AnswerRedirectDto("성공", HttpServletResponse.SC_OK);
        answerRedirectDto.setRedirectUrl(referer);
        return ResponseEntity.ok().body(answerRedirectDto); // 질문 저장후 질문목록으로 이동
    }
}
