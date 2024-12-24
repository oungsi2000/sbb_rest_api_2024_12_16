package com.ll.jumptospringboot.domain.Category;

import com.ll.jumptospringboot.domain.Question.QuestionForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/api/category/create")
    public String create(@Valid CategoryForm categoryForm, BindingResult bindingResult, QuestionForm questionForm, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getList();
            model.addAttribute("categories", categories);
            return "question_form";
        }
        categoryService.create(categoryForm);
        return "redirect:/question/create";
    }
}
