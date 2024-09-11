package com.eCommerce.application.Service;

import com.eCommerce.application.Model.CategoryDTO;
import com.eCommerce.application.Model.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO deleteCategory(Long id);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
