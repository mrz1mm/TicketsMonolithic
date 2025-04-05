package com.ticketing.service;

import com.ticketing.model.Category;
import com.ticketing.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        
        // Check if the new name already exists and is not the current category
        if (!category.getName().equals(categoryDetails.getName()) && 
            categoryRepository.findByName(categoryDetails.getName()).isPresent()) {
            throw new RuntimeException("Category name already exists");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
