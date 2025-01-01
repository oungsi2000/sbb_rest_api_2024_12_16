package com.ll.jumptospringboot.domain.Category.dto;

import com.ll.jumptospringboot.domain.Category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CategoryListDto {
    private List<Category> categories;
}
