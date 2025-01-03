package com.ll.jumptospringboot.domain.Question;

import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Category.CategoryService;
import com.ll.jumptospringboot.domain.Comment.dto.CommentForm;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.dto.QuestionDetailDto;
import com.ll.jumptospringboot.domain.Question.dto.QuestionForm;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.domain.User.UserService;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import com.ll.jumptospringboot.global.standard.BaseResponse;
import com.ll.jumptospringboot.global.util.annotation.JwtAuthorize;
import com.ll.jumptospringboot.global.util.annotation.JwtUserContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Controller
@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {
    private final QuestionService service;
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final CategoryService categoryService;

    @PostMapping(value = "/detail/{id}")
    public QuestionDetailDto detail(@PathVariable("id") Integer id,
                         @RequestParam(value="index", defaultValue="0") int idx,
                         @RequestParam(value="sortBy", defaultValue="mostVoted") String sortBy) {
        return service.getQuestionDetailDto(id,idx,sortBy);
    }

    @JwtAuthorize(role=UserRole.USER)
    @GetMapping("/modify/{id}")
    public ResponseEntity<QuestionForm> questionModify(@PathVariable("id") Integer id, @JwtUserContext UserContextDto userContextDto) {
        Question question = this.service.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(userContextDto.getName()) &&
            !question.getAuthor().getEmail().equals(userContextDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        QuestionForm questionForm = new QuestionForm();
        questionForm.setSubject(question.getTitle());
        questionForm.setContent(question.getContent());

        return ResponseEntity.ok().body(questionForm);
    }

    @JwtAuthorize(role=UserRole.USER)
    @GetMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> questionDelete(@PathVariable("id") Integer id, @JwtUserContext UserContextDto userContextDto) {
        Question question = this.service.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(userContextDto.getName()) &&
            !question.getAuthor().getEmail().equals(userContextDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.service.delete(question);
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/vote/{id}")
    public ResponseEntity<BaseResponse> questionVote(@JwtUserContext UserContextDto userContextDto, @PathVariable("id") Integer id) {
        Question question = this.service.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        this.service.vote(question, siteUser);
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/modify/{id}")
    public ResponseEntity<BaseResponse> questionModify(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult, @JwtUserContext UserContextDto userContextDto, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            BaseResponse baseResponse = new BaseResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body(baseResponse);
        }
        Question question = this.service.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(userContextDto.getName()) &&
            !question.getAuthor().getEmail().equals(userContextDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.service.modify(question, questionForm.getSubject(), questionForm.getContent());
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> questionCreate(@Valid QuestionForm questionForm,
                                                       BindingResult bindingResult,
                                                       @JwtUserContext UserContextDto userContextDto) {
        if (bindingResult.hasErrors()) {
            BaseResponse baseResponse = new BaseResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body(baseResponse);
        }
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        this.service.create(questionForm, siteUser);
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/create/comment")
    public ResponseEntity<BaseResponse> questionComment(@Valid CommentForm commentForm,
                                  BindingResult bindingResult,
                                  @JwtUserContext UserContextDto userContextDto) {
        if (bindingResult.hasErrors()) {
            BaseResponse baseResponse = new BaseResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body(baseResponse);
        }
        SiteUser siteUser = this.userService.getUser(userContextDto.getName());
        commentService.createQuestionComment(siteUser, commentForm.getContent(), commentForm.getId());
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(baseResponse);
    }
}

