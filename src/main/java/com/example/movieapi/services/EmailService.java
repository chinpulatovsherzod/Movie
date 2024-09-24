package com.example.movieapi.services;


import com.example.movieapi.dto.MailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMail(MailBody mailBody){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailBody.to());
        mailMessage.setFrom("");
        mailMessage.setSubject(mailBody.subject());
        mailMessage.setText(mailBody.text());

        javaMailSender.send(mailMessage);
    }
}
