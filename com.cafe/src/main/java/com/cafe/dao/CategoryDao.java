package com.cafe.dao;

import com.cafe.POJO.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
    List<Category> getOneCategory(@Param("id") Integer id);

    @Transactional
    @Modifying
    void updateCategory(@Param("id") Integer id, @Param("name") String name);
}
