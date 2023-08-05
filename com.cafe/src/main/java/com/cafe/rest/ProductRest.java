package com.cafe.rest;


import com.cafe.POJO.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);

    @GetMapping({"/get", "/get/{id}"})
    public ResponseEntity<List<Product>> getAllProducts(@PathVariable(required = false) Integer id);

    @GetMapping({"/get/category/{id}"})
    public ResponseEntity<List<Product>> getAllProductsByCategory(@PathVariable(required = false) Integer id);

    @PatchMapping(path = "/update/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody(required = true) Map<String, String> requestMap, @PathVariable(required = true) Integer id);

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(required = true) Integer id);
}
