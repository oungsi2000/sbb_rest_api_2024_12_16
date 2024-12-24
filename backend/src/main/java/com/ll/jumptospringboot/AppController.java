package com.ll.jumptospringboot;

import com.ll.jumptospringboot.domain.Answer.Answer;
import com.ll.jumptospringboot.domain.Answer.AnswerService;
import com.ll.jumptospringboot.domain.Comment.Comment;
import com.ll.jumptospringboot.domain.Comment.CommentService;
import com.ll.jumptospringboot.domain.Question.Question;
import com.ll.jumptospringboot.domain.Question.QuestionService;
import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRepository;
import com.ll.jumptospringboot.domain.User.UserService;
import com.ll.jumptospringboot.domain.oauth2.OathService;
import com.ll.jumptospringboot.domain.oauth2.UserCreateOauthDto;
import com.ll.jumptospringboot.domain.password.ResetPasswordService;
import com.ll.jumptospringboot.domain.password.UserResetPasswordDto;
import com.ll.jumptospringboot.exception.PasswordNotSameException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ll.jumptospringboot.domain.User.UserCreateForm;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class AppController {
    private final QuestionService service;
    private final UserService userService;
    private final UserCreateForm userCreateForm;
    private final ResetPasswordService resetPasswordService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final OathService oathService;

    @GetMapping("/")
    public String root(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sortby", defaultValue = "") String sortBy) {

        Page<Question> paging = service.getList(page, kw, sortBy);
        model.addAttribute("questionList", paging);
        model.addAttribute("kw", kw);
        return "list";
    }


    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userCreateForm", userCreateForm);
        return "signup_form";
    }
    @GetMapping("/oauth-signup")
    public String oAuthSignup(UserCreateOauthDto userCreateOauthDto) {
        return "signup_oauth";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }


    @PostMapping("/api/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
            userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @PostMapping("/api/signup/oauth")
    public String oauthSignup(@Valid UserCreateOauthDto userCreateOauthDto, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "signup_oauth";
        }
        try {
            String googleId = principal.getName();
            SiteUser user = userService.getUserByOauth(googleId);
            userCreateOauthDto.setEmail(user.getEmail());
            userCreateOauthDto.setProviderId(user.getProviderId());
            userService.createOauth(userCreateOauthDto);

        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_oauth";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_oauth";
        }
        return "redirect:/";
    }

    @GetMapping("/reset-password")
    public String findPassword() {
        return "reset-password";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-page")
    public String myPage(Model model, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        List<Question> questions = questionService.getQuestionByUser(user);
        List<Answer> answers = answerService.getAnswerByUser(user);
        List<Comment> comments = commentService.getCommentByUser(user);
        model.addAttribute("userInfo", user) ;
        model.addAttribute("questions", questions);
        model.addAttribute("answers", answers);
        model.addAttribute("comments", comments);
        return "my-page";
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

