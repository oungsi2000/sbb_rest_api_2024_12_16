package com.ll.jumptospringboot.domain.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getList() {
        return categoryRepository.findAll();
    }

    public void create(CategoryForm categoryForm) {
        Category category = new Category();
        category.setTitle(categoryForm.getTitle());
        category.setInfo(categoryForm.getInfo());
        categoryRepository.save(category);
    }

    public Optional<Category> find(Integer id){
        return categoryRepository.findById(id);
    }
}
