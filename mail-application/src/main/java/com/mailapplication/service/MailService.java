package com.mailapplication.service;

import com.mailapplication.dto.PasswordResetTokenEvent;
import com.mailapplication.dto.UserAdminAccessGrant;
import com.mailapplication.dto.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "user-created", groupId = "user-created-group", properties = {"spring.json.value.default.type=com.mailapplication.dto.UserCreatedEvent"})
    public UserCreatedEvent sendEmailUserCreated(UserCreatedEvent userCreatedEvent) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        logger.info("Sending user-created email...");

        mailMessage.setSubject("Your new user has been successfully created");
        mailMessage.setText("Hello, \n\n" + "We are happy to announce that your account has been successfully created. ");
        mailMessage.setTo(userCreatedEvent.email());
        mailSender.send(mailMessage);

        logger.info("Email sent successfully...");

        return userCreatedEvent;

    }


    @KafkaListener(topics = "admin-grant", groupId = "user-admin-grant", properties = {"spring.json.value.default.type=com.mailapplication.dto.UserAdminAccessGrant"})
    public UserAdminAccessGrant sendEmailUserHasAdminAccess (UserAdminAccessGrant userAdminAccessGrant) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        logger.info("Sending user-admin access grant...");

        mailMessage.setSubject("You now have admin access, " + userAdminAccessGrant.email());
        mailMessage.setText(userAdminAccessGrant.message());
        mailMessage.setTo(userAdminAccessGrant.email());
        mailSender.send(mailMessage);

        logger.info("Email sent successfully...");

        return userAdminAccessGrant;


    }


    @KafkaListener (topics = "password-reset", groupId = "user-password-reset", properties = {"spring.json.value.default.type=com.mailapplication.dto.PasswordResetTokenEvent"})
    public PasswordResetTokenEvent sendResetTokenToEmail (PasswordResetTokenEvent passwordResetTokenEvent) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        logger.info("Sending user-admin access grant...");

        mailMessage.setSubject("Password reset Token");
        mailMessage.setText("Here is your token to reset your password:\n" + passwordResetTokenEvent.token() +
                "\n" + "You need to send the token inside the request to change your password, like this: [LINK]/api/user/password?token= " + passwordResetTokenEvent.token() + "\n" + 
                "Remember, this token is only valid for 30 minutes. If it is not used to reset your password within the specified time, you will need to request a new token. ");
        mailMessage.setTo(passwordResetTokenEvent.username());
        mailSender.send(mailMessage);

        logger.info("Email sent successfully...");

        return  passwordResetTokenEvent;


    }


}
