package com.example.campus_sos.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("캠퍼스 SOS 이메일 인증");
        message.setText("다음 링크를 클릭해 이메일 인증을 완료해주세요:\n" + link);
        mailSender.send(message);
    }
}
