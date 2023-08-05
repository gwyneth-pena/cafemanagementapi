package com.cafe.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CafeUtil {
    public CafeUtil() {
    }

    public static ResponseEntity<String> getResponseEntity(String responseMsg, HttpStatus httpStatus){
        return  new ResponseEntity<String>("{\"message\":\""+responseMsg+"\"}", httpStatus);
    }

    public static String getUUID(){
        Date date = new Date();
        long time = date.getTime();
        return "BILL-"+time;
    }

    public static JSONArray getJSONArray(String data) throws JSONException{
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, String> getMapFromJson(String data){
        if(!Strings.isEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){
            }.getType());
        return new HashMap<>();
    }

    public static Boolean isFileExist(String path){
        try{
            File file = new File(path);
            return (file!=null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Boolean.FALSE;
    }
}
