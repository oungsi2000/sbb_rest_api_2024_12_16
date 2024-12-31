package com.ll.jumptospringboot;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.GetListDto;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.*;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import com.ll.jumptospringboot.global.auth.dto.UserCreateDto;
import com.ll.jumptospringboot.global.auth.dto.UserCreateOauthDto;
import com.ll.jumptospringboot.domain.password.ResetPasswordService;
import com.ll.jumptospringboot.domain.password.UserResetPasswordDto;
import com.ll.jumptospringboot.global.exception.PasswordNotSameException;
import com.ll.jumptospringboot.global.util.JwtAuthorize;
import com.ll.jumptospringboot.global.util.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
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

    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<Page<Question>> root(@RequestBody GetListDto getListDto) {
        int page = getListDto.getPage();
        String kw = getListDto.getKw() != null ? getListDto.getKw() : "";
        String sortBy = getListDto.getSortBy() != null ? getListDto.getSortBy() : "";
        Page<Question> paging = service.getList(page, kw, sortBy);

        return ResponseEntity.ok().body(paging);
    }

    @PostMapping(value = "/get-user-context")
    @ResponseBody
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

        if (authentication == null){
            userContextDto.setName("null");
            userContextDto.setRole(UserRole.ANONYMOUS);
            return userContextDto;
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
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity<AuthResponse> oauthSignup(@Valid UserCreateOauthDto userCreateOauthDto, BindingResult bindingResult, Principal principal, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            AuthResponse authResponse = new AuthResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            UserContextDto userContextDto = getUserContext(request);
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

    @GetMapping("/reset-password")
    public String findPassword() {
        return "reset-password";
    }

    @JwtAuthorize(role=UserRole.USER)
    @PostMapping("/my-page")
    @ResponseBody
    public ResponseEntity<UserDataDto> myPage(HttpServletRequest request) {
        UserDataDto userDataDto = new UserDataDto();
        UserContextDto userContext = getUserContext(request);
        SiteUser user = userService.getUser(userContext.getName());
        List<Question> questions = questionService.getQuestionByUser(user);
        List<Answer> answers = answerService.getAnswerByUser(user);
        List<Comment> comments = commentService.getCommentByUser(user);
        userDataDto.setUser(user);
        userDataDto.setQuestions(questions);
        userDataDto.setAnswers(answers);
        userDataDto.setComments(comments);

        return ResponseEntity.ok().body(userDataDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/change-password")
    public String changePassword(UserResetPasswordDto userResetPasswordDto) {
        return "change_password";
    }

    @PostMapping("/api/reset-password")
    @ResponseBody
    public Map<String, String> resetPassword(@RequestParam(value = "email") String email) {
        try {
            return resetPasswordService.generateTempPassword(email);
        } catch (UsernameNotFoundException e) {
            //TODO 전역 예외 처리를 통해 실패했으면 400 던지도록 하기
           throw new UsernameNotFoundException("유저를 찾을 수 없습니다");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/change-password")
    @ResponseBody
    public Map<String, String> changePassword(@RequestParam(value="password") String password,
                                              @RequestParam(value="newPassword") String newPassword,
                                              @RequestParam(value="newPasswordConfirm") String newPasswordConfirm,
                                              Principal principal) {
        SiteUser user = userRepository.findByusername(principal.getName()).get();
        try {
            return resetPasswordService.changePassword(password, newPassword, user);
        } catch (PasswordNotSameException e) {
            //TODO 전역 예외 처리를 통해 실패했으면 400 던지도록 하기
            throw new PasswordNotSameException("비밀번호가 일치하지 않습니다");
        }
    }
}

