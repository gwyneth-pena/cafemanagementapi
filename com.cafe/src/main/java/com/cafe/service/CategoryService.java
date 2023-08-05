package com.cafe.service;

import com.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);
    ResponseEntity<List<Category>> getAllCategory(Integer id);
    ResponseEntity<String> deleteCategory(Integer id);
    ResponseEntity<String> updateCategory(Map<String, String> requestMap, Integer id);
}
