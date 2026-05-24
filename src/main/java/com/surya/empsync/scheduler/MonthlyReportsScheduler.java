package com.surya.empsync.scheduler;

import com.surya.empsync.scheduler.service.ExcelService;
import com.surya.empsync.scheduler.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class MonthlyReportsScheduler {

    @Autowired
    private MailService mailService;

    @Autowired
    private ExcelService excelService;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    public void sendSalaryAndAttendanceReport() throws MessagingException, IOException {
        byte[] excel = excelService.generateSalaryAndAttendanceExcel();
        String month = LocalDate.now().minusMonths(1).getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String fileName = month + "-Attendance-Salary.xlsx"  ;
        mailService.sendMailWithAttachment(excel, fileName);
    }
}
