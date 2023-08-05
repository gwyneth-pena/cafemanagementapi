package com.cafe.dao;

import com.cafe.POJO.User;
import com.cafe.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmail(@Param("email") String email);

    List<UserWrapper> getAllUser();

    List<UserWrapper> getAllAdmin();

    @Transactional
    @Modifying
    void updateStatus(@Param("status") String status, @Param("id") Integer id);


    @Transactional
    @Modifying
    void updatePassword(@Param("password") String password, @Param("id") Integer id);

    @Transactional
    @Modifying
    void updateActiveToken(@Param("isActive") String isActive, @Param("id") Integer id);
}
