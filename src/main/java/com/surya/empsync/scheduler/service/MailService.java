package com.surya.empsync.scheduler.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMailWithAttachment(byte[] excel, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String month = LocalDate.now().minusMonths(1).getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        helper.setTo("suryapotti2201@gmail.com");
        helper.setSubject(month + " month Salary and Attendance Report");
        helper.setText("Please find attached the Salary and Attendance Report.");

        ByteArrayResource resource = new ByteArrayResource(excel);
        helper.addAttachment(fileName, resource);
        mailSender.send(message);
    }
}