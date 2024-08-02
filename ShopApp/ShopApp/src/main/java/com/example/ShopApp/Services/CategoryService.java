package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.CategoryDTO;
import com.example.ShopApp.Models.Category;
import com.example.ShopApp.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements InterfaceCategroyService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder().name(categoryDTO.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category existsingCategory = getCategoryById(categoryId);
        existsingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existsingCategory);
        return existsingCategory;
    }
    // Xóa xong mất luôn
    @Override
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }
}
