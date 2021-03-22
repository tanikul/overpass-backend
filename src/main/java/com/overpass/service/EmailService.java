package com.overpass.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${email.from}")
    private String from;

    public void sendSimpleMessage(
      String to, String subject, String text) {
     try {
    	 SimpleMailMessage message = new SimpleMailMessage(); 
         message.setFrom(from);
         message.setTo(to); 
         message.setSubject(subject); 
         message.setText(text);
         emailSender.send(message); 
     }catch(Exception ex) {
    	 throw ex;
     }
        
    }
}
