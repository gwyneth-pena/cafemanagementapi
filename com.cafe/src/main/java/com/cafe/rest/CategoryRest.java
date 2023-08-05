package com.cafe.rest;

import com.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryRest {

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String, String> requestMap);

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(required = true) Integer id);

    @GetMapping({"/get", "/get/{id}"})
    public ResponseEntity<List<Category>> getAllCategory(@PathVariable(required = false) Integer id);

    @PatchMapping(path = "/update/{id}")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap, @PathVariable(required = true) Integer id);

}
