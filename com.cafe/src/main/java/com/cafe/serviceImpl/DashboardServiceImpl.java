package com.cafe.serviceImpl;

import com.cafe.dao.BillDao;
import com.cafe.dao.CategoryDao;
import com.cafe.dao.ProductDao;
import com.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    BillDao billDao;


    @Override
    public ResponseEntity<Map<String, String>> getCount() {
        try{
            Map<String, String> count = new HashMap<>();
            count.put("category", String.valueOf(categoryDao.count()));
            count.put("bill", String.valueOf(billDao.count()));
            count.put("product", String.valueOf(productDao.count()));
            return new ResponseEntity<>(count, HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
