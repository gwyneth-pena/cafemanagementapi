package com.cafe.serviceImpl;

import com.cafe.POJO.Category;
import com.cafe.POJO.Product;
import com.cafe.constants.CafeConstant;
import com.cafe.dao.ProductDao;
import com.cafe.jwt.jwtFilter;
import com.cafe.service.ProductService;
import com.cafe.utils.CafeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    jwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap)){
                    productDao.save(getProductFromMap(requestMap));
                    return CafeUtil.getResponseEntity("Product added successfully.", HttpStatus.OK);
                }else{
                    return CafeUtil.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<List<Product>> getAllProducts(Integer id) {
        try {
            List<Product> products;
            if(!Objects.isNull(id)){
                products = productDao.getOneProduct(id);
            }else{
                products = productDao.getAllProducts();
            }
            return  new ResponseEntity<List<Product>>(products, HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Product>>(new ArrayList<Product>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Product>> getAllProductsByCategory(Integer id) {
        try {
            return new ResponseEntity<List<Product>>(productDao.getProductsByCategory(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Product>>(new ArrayList<Product>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap, Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                List<Product> product = productDao.getOneProduct(id);
                if(!Objects.isNull(requestMap.get("name"))){
                    product.get(0).setName(requestMap.get("name"));
                }
                if(!Objects.isNull(requestMap.get("status"))){
                    product.get(0).setStatus(requestMap.get("status"));
                }
                if(!Objects.isNull(requestMap.get("description"))){
                    product.get(0).setDescription(requestMap.get("description"));
                }
                if(!Objects.isNull(requestMap.get("price"))){
                    product.get(0).setPrice(Integer.parseInt(requestMap.get("price")));
                }
                if(!Objects.isNull(requestMap.get("category_id"))){
                    Category category = new Category();
                    category.setId(Integer.parseInt(requestMap.get("category_id")));
                    product.get(0).setCategory(category);
                }
                productDao.save(product.get(0));
                return CafeUtil.getResponseEntity("Product updated successfully.", HttpStatus.OK);
            }else {
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                List<Product> product = productDao.getOneProduct(id);
                if(product.size()>0){
                    productDao.delete(product.get(0));
                    return CafeUtil.getResponseEntity("Product deleted successfully.", HttpStatus.OK);
                }else{
                    return CafeUtil.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else{
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductFromMap(Map<String, String> requestMap){
        Product product = new Product();
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        product.setName(requestMap.get("name"));
        product.setStatus(requestMap.get("status"));

        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("category_id")));
        product.setCategory(category);

        if(requestMap.containsKey("description")){
            product.setDescription(requestMap.get("description"));
        }

        return  product;
    }

    private Boolean validateProductMap(Map<String, String> requestMap){
        if(requestMap.containsKey("name")&&requestMap.containsKey("category_id")&&requestMap.containsKey("price")&&requestMap.containsKey("status")){
            return true;
        }
        return false;
    }
}
