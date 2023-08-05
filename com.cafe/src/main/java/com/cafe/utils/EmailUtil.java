package com.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gwenpenadev@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list.size()>0&&list!=null){
            message.setCc(getCcArray(list));
        }
        javaMailSender.send(message);
    }

    private String[] getCcArray(List<String> list){
        String[] cc = new String[list.size()];
        for(int i = 0; i<list.size(); i++){
            cc[i] = list.get(i);
        }
        return cc;
    }

    public void forgotMail(String to, String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("gwenpenadev@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg = "<p><b>Your Login details for Cafe Corner</b><br><b>Email: </b> " +
                to + " <br><b>Password: </b> " + password + "<br><a href=\"http://localhost:4200/login\">Click here to login</a></p>";
        message.setContent(htmlMsg, "text/html");
        javaMailSender.send(message);
    }
}
