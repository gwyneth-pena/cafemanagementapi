package com.cafe.rest;

import com.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")
public interface UserRest {
    @PostMapping(path = "/sign_up")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUser();

    @PatchMapping(path = "/update/{id}")
    public ResponseEntity<String> updateUser(@RequestBody(required = true) Map<String, String> requestMap, @PathVariable Integer id);

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(@RequestBody String token);

    @PostMapping(path = "/change_password")
    public ResponseEntity<String> checkPassword(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap);
}
