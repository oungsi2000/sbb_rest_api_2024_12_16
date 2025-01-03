package com.ll.jumptospringboot;

import com.ll.jumptospringboot.domain.Answer.dto.AnswerDto;
import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Comment.dto.CommentDto;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.dto.GetListDto;
import com.ll.jumptospringboot.domain.Question.entity.Question;
import com.ll.jumptospringboot.domain.Question.dto.QuestionDto;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.*;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import com.ll.jumptospringboot.global.auth.dto.UserCreateDto;
import com.ll.jumptospringboot.global.auth.dto.UserCreateOauthDto;
import com.ll.jumptospringboot.global.auth.password.ResetPasswordService;
import com.ll.jumptospringboot.global.exception.DataNotFoundException;
import com.ll.jumptospringboot.global.exception.PasswordNotSameException;
import com.ll.jumptospringboot.global.util.annotation.JwtAuthorize;
import com.ll.jumptospringboot.global.util.JwtProvider;
import com.ll.jumptospringboot.global.util.annotation.JwtUserContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AppController {
    private final QuestionService service;
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final JwtProvider jwtProvider;
    @Value("${jwt.expiration_time}")
    Long expirationTime;

    private ResponseCookie setCookie(String name) {
        String token = jwtProvider.regenerateToken(name, UserRole.USER);
        return ResponseCookie.from("Authorization", token)
            .maxAge(Math.toIntExact(expirationTime))
            .path("/")
            .httpOnly(true)
            .build();
    }

    @GetMapping("/question-list")
    public ResponseEntity<Page<Question>> root(@RequestBody GetListDto getListDto) {
        int page = getListDto.getPage();
        String kw = getListDto.getKw() != null ? getListDto.getKw() : "";
        String sortBy = getListDto.getSortBy() != null ? getListDto.getSortBy() : "";
        Page<Question> paging = service.getList(page, kw, sortBy);

        return ResponseEntity.ok().body(paging);
    }

    @PostMapping(value = "/get-user-context")
    public UserContextDto getUserContext(HttpServletRequest request) {
        String authentication = null;
        UserContextDto userContextDto = new UserContextDto();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    authentication = cookie.getValue();
                }
            }
        }
        try {
            return jwtProvider.validate(authentication);
        } catch (Exception e) {
            userContextDto.setName("null");
            userContextDto.setRole(UserRole.ANONYMOUS);
            return userContextDto;
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid UserCreateDto userCreateDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }

        if (!userCreateDto.getPassword1().equals(userCreateDto.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                "2개의 패스워드가 일치하지 않습니다.");
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.create(userCreateDto.getUsername(),
            userCreateDto.getEmail(), userCreateDto.getPassword1());
        } catch(DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
        AuthResponse authResponse = new AuthResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, setCookie(userCreateDto.getUsername()).toString())
            .body(authResponse);
    }

    @PostMapping("/signup/oauth")
    public ResponseEntity<AuthResponse> oauthSignup(@Valid UserCreateOauthDto userCreateOauthDto,
                                                    BindingResult bindingResult,
                                                    @JwtUserContext UserContextDto userContextDto) {

        if (bindingResult.hasErrors()) {
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            String email = userContextDto.getName();
            SiteUser user = userService.getUserByEmail(email);
            userCreateOauthDto.setEmail(email);
            userCreateOauthDto.setOauthId(user.getOauthId());
            userCreateOauthDto.setProviderId(user.getProviderId());
            userService.createOauth(userCreateOauthDto);

        } catch(DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
        AuthResponse authResponse = new AuthResponse("성공", HttpServletResponse.SC_OK);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, setCookie(userCreateOauthDto.getUsername()).toString())
            .body(authResponse);
    }


    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/my-page")
    public ResponseEntity<UserDataDto> myPage(@JwtUserContext UserContextDto userContextDto) {
        UserDataDto userDataDto = new UserDataDto();
        SiteUser user = userService.getUser(userContextDto.getName());
        List<QuestionDto> questions = questionService.getQuestionByUser(user);
        List<AnswerDto> answers = answerService.getAnswerByUser(user);
        List<CommentDto> comments = commentService.getCommentByUser(user);
        userDataDto.setUser(user);
        userDataDto.setQuestions(questions);
        userDataDto.setAnswers(answers);
        userDataDto.setComments(comments);

        return ResponseEntity.ok().body(userDataDto);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestParam(value = "email") String email) {
        return resetPasswordService.generateTempPassword(email);
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@RequestParam(value="password") String password,
                                              @RequestParam(value="newPassword") String newPassword,
                                              @RequestParam(value="newPasswordConfirm") String newPasswordConfirm,
                                              HttpServletRequest request
                               ) {
        UserContextDto userContext = getUserContext(request);

        Optional<SiteUser> userOptional =  userRepository.findByusername(userContext.getName());
        if (userOptional.isEmpty()) {
            throw new DataNotFoundException("유저를 찾을 수 없습니다");
        }
        SiteUser user = userOptional.get();
        return resetPasswordService.changePassword(password, newPassword, user);

    }
}

