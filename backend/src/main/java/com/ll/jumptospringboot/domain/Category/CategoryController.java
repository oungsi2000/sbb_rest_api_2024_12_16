package com.ll.jumptospringboot.domain.Category;

import com.ll.jumptospringboot.domain.Category.dto.CategoryForm;
import com.ll.jumptospringboot.domain.Category.dto.CategoryListDto;
import com.ll.jumptospringboot.domain.Category.entity.Category;
import com.ll.jumptospringboot.global.standard.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> create(@Valid CategoryForm categoryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            BaseResponse baseResponse = new BaseResponse(bindingResult.getAllErrors().getFirst().getDefaultMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body(baseResponse);
        }
        BaseResponse baseResponse = new BaseResponse("성공", HttpServletResponse.SC_OK);
        categoryService.create(categoryForm);
        return ResponseEntity.ok().body(baseResponse);
    }

    @GetMapping("/list")
    public CategoryListDto list() {
        List<Category> categories = categoryService.getList();
        CategoryListDto categoryDto = new CategoryListDto();
        categoryDto.setCategories(categories);
        return categoryDto;
    }
}
