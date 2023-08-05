package com.cafe.serviceImpl;

import com.cafe.POJO.User;
import com.cafe.constants.CafeConstant;
import com.cafe.dao.UserDao;
import com.cafe.jwt.CustomerUserDetailsService;
import com.cafe.jwt.jwtFilter;
import com.cafe.jwt.jwtUtil;
import com.cafe.service.UserService;
import com.cafe.utils.CafeUtil;
import com.cafe.utils.EmailUtil;
import com.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    jwtUtil jwtUtil;

    @Autowired
    jwtFilter jwtFilter;

    @Autowired
    EmailUtil emailUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtil.getResponseEntity("Successfully registered.", HttpStatus.OK);
                }
                else{
                    return  CafeUtil.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return CafeUtil.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if(requestMap.containsKey("name")&&requestMap.containsKey("contactNumber")&&requestMap.containsKey("email")
                &&requestMap.containsKey("password")){
            return  true;
        }
        return  false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setEmail(requestMap.get("email"));
        user.setStatus("false");
        user.setRole("admin");

        return  user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            Authentication auth = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if(auth.isAuthenticated()){
                if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    String token = jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                            customerUserDetailsService.getUserDetail().getRole());
                    User user = userDao.findByEmail(jwtUtil.extractUsername(token));
                    userDao.updateActiveToken("true", user.getId());
                    return new ResponseEntity<String>("{\"token\":\""+token+ "\"}",
                            HttpStatus.OK
                            );
                }else{
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            log.info("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Invalid Credentials."+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap, Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> optional = userDao.findById(id);
                if(!optional.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), id);
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtil.getResponseEntity("User status updated successfully.", HttpStatus.OK);
                }else{
                    return CafeUtil.getResponseEntity("User does not exist.", HttpStatus.OK);
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
    public ResponseEntity<String> logout(String token) {
        try {
            User user = userDao.findByEmail(jwtUtil.extractUsername(token));
            userDao.updateActiveToken("false", user.getId());
            return CafeUtil.getResponseEntity("Logged out.", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(userObj)){
                if(passwordEncoder.matches(requestMap.get("old_password"), userObj.getPassword())){
                    userDao.updatePassword(passwordEncoder.encode(requestMap.get("new_password")), userObj.getId());
                    return CafeUtil.getResponseEntity("Password updated successfully.", HttpStatus.OK);
                }else{
                    return CafeUtil.getResponseEntity("You entered an incorrect password.", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{
            User userObj = userDao.findByEmail(requestMap.get("email"));
            if(userObj!=null){
                PasswordGenerator passwordGenerator = new PasswordGenerator();
                List<CharacterRule> rules = new ArrayList<>();
                CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit,7);
                CharacterRule alphabet = new CharacterRule(EnglishCharacterData.Alphabetical, 7);
                rules.add(digits);
                rules.add(alphabet);
                String password = passwordGenerator.generatePassword(14, rules);
                userDao.updatePassword(passwordEncoder.encode(password), userObj.getId());
                emailUtil.forgotMail(userObj.getEmail(), "Credentials for Coffee Corner", password);
                return CafeUtil.getResponseEntity("Please check your email for credentials.", HttpStatus.OK);
            }else{
                return CafeUtil.getResponseEntity("Email is not found.", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<UserWrapper> allAdmin) {
        String currentUser = jwtFilter.getCurrentUser();
        List<String> sendTo = new ArrayList<>(getEmails(allAdmin));
        sendTo.remove(sendTo.indexOf(currentUser));

        if(Objects.equals(status, "false")){
            emailUtil.sendSimpleMessage(currentUser,"Account Deactivated", "USER: "+user+" is deactivated by ADMIN: "+ currentUser, sendTo);
        }else{
            emailUtil.sendSimpleMessage(currentUser,"Account Approved", "USER: "+user+" is approved by ADMIN: "+ currentUser, sendTo);
        }
    }

    private List<String> getEmails(List<UserWrapper> list){
        List<String> emails = list.stream().map(item->item.getEmail()).toList();
        return emails;
    }
}
