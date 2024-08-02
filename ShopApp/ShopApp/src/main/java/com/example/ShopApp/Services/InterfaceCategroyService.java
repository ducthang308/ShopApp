package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.CategoryDTO;
import com.example.ShopApp.Models.Category;

import java.util.List;

public interface InterfaceCategroyService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(long id);
}
