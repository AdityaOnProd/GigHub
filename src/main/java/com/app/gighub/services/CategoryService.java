package com.app.gighub.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.gighub.models.Category;
import com.app.gighub.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category get(long id) {
        // ✅ Updated to use `findById(id).orElse(null)`
        return categoryRepository.findById(id).orElse(null);
    }

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> list() {
        return categoryRepository.findAll();
    }
}
