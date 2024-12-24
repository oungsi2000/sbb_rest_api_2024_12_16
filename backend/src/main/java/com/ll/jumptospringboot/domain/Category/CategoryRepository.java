package com.ll.jumptospringboot.domain.Category;

import com.ll.jumptospringboot.domain.Question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
