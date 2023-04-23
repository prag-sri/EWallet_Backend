package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = {"send_mail"}, groupId = "test1234")
    public void sendEmailMessage(String message) throws JsonProcessingException, MessagingException {

        //DECODING THE MESSAGE TO JSONObject
        //User email ....message
        JSONObject emailRequest = objectMapper.readValue(message,JSONObject.class);

        //Get the email and message from JSONObject
        String email = (String)emailRequest.get("email");
        String messageBody = (String)emailRequest.get("message");

        System.out.println("We are in the notification service"+email+" -- "+messageBody);

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("your_email@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setText(messageBody);
        mimeMessageHelper.setSubject("Hi");

        javaMailSender.send(mimeMessage);
        System.out.printf("Mail with attachment sent successfully..");
    }

    @KafkaListener(topics = {"register_user"}, groupId = "test1234")
    public void sendRegistrationEmailMessage(String message) throws JsonProcessingException, MessagingException {

        //DECODING THE MESSAGE TO JSONObject
        JSONObject emailRequest = objectMapper.readValue(message,JSONObject.class);

        String email= (String) emailRequest.get("email");
        String messageBody = (String)emailRequest.get("message");

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("your_email@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setText(messageBody);
        mimeMessageHelper.setSubject("User Registration Successful!");

        javaMailSender.send(mimeMessage);
        System.out.printf("Registration Mail with attachment sent successfully..");
    }
}
