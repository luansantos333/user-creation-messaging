package com.mailapplication.service;

import com.mailapplication.dto.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    // @Autowired
    //private JavaMailSender mailSender;


    @KafkaListener (topics = "user-created", groupId = "user-created-group")
    public UserCreatedEvent sendEmail(UserCreatedEvent userCreatedEvent) {
        //  SimpleMailMessage mailMessage = new SimpleMailMessage();


        System.out.printf("User created: %s%n", userCreatedEvent);

//        mailMessage.setSubject("Your new user has been successfully created");
//        mailMessage.setText("Hello, \n\n" + "We are happy to announce that your account has been successfully created. ");
//        mailMessage.setTo(userCreatedEvent.email());
//        mailSender.send(mailMessage);

        return userCreatedEvent;

    }



}
