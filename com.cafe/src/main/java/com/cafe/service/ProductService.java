package com.cafe.service;

import com.cafe.POJO.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addProduct(Map<String, String> requestMap);
    ResponseEntity<List<Product>> getAllProducts(Integer id);
    ResponseEntity<List<Product>> getAllProductsByCategory(Integer id);
    ResponseEntity<String> updateProduct(Map<String, String> requestMap, Integer id);
    ResponseEntity<String> deleteProduct(Integer id);

}
