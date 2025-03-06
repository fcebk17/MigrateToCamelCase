package ntou.cse.soselab.services;

import ntou.cse.soselab.entites.Category;
import ntou.cse.soselab.payloads.CategoryDTO;
import ntou.cse.soselab.payloads.CategoryResponse;

public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
