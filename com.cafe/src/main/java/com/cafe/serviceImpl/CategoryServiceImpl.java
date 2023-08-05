package com.cafe.serviceImpl;

import com.cafe.POJO.Category;
import com.cafe.POJO.Product;
import com.cafe.constants.CafeConstant;
import com.cafe.dao.CategoryDao;
import com.cafe.dao.ProductDao;
import com.cafe.jwt.jwtFilter;
import com.cafe.service.CategoryService;
import com.cafe.utils.CafeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    jwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap)){
                    categoryDao.save(getCategoryFromMap(requestMap));
                    return CafeUtil.getResponseEntity("Category added successfully.", HttpStatus.OK);
                }else{
                    return CafeUtil.getResponseEntity("It is either the category name is already existing or the name field you passed is empty.", HttpStatus.BAD_REQUEST);
                }
            }else{
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(Integer id) {
        try {
            List<Category> categories = new ArrayList<>();
            if(!Objects.isNull(id)){
                categories = categoryDao.getOneCategory(id);
            }else{
                categories = categoryDao.getAllCategory();
            }
            System.out.println(id);
            return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteCategory(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                List<Category> category = categoryDao.getOneCategory(id);
                if(category.size()==0){
                    return CafeUtil.getResponseEntity("Category not found.", HttpStatus.NOT_FOUND);
                }else{
                    Stream<Product> products = productDao.getAllProducts().stream().filter(p->category.get(0).getId().equals(p.getCategory().getId()));
                    products.forEach(item->productDao.deleteById(item.getId()));
                    categoryDao.deleteById(id);
                    return CafeUtil.getResponseEntity("Category deleted successfully.", HttpStatus.OK);
                }
            }else{
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap, Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                categoryDao.updateCategory(id, requestMap.get("name"));
                return CafeUtil.getResponseEntity("Category updated successfully.", HttpStatus.OK);
            }else{
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Category getCategoryFromMap(Map<String, String> requestMap){
        Category category = new Category();
        category.setName(requestMap.get("name"));
        return  category;
    }

    private Boolean validateCategoryMap(Map<String, String> requestMap){
        List<Category> categories = categoryDao.getAllCategory();
        if(requestMap.containsKey("name")&&categories.stream().filter(item->item.getName().equals(requestMap.get("name"))).toList().size()==0){
            return true;
        }
        return false;
    }


}
