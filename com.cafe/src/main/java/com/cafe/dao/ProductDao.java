package com.cafe.dao;

import com.cafe.POJO.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<Product> getAllProducts();
    List<Product> getOneProduct(@Param("id") Integer id);
    List<Product> getProductsByCategory(@Param("id") Integer id);
}
