package com.library.bible.email.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String FROM_MAIL;
    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    
    // 인증 메일 발송
    public void sendVerificationEmail(String email) {
    	// 인증 번호
        String code = generateVerificationCode();

        // 내용
        String title = "Bible 회원가입 인증번호 발급"; // 이메일 제목
        String content = // html 형식으로 작성하기
            "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #dddddd; border-radius: 10px; background-color: #f9f9f9;\">" +
                "<h2 style=\"color: #333333; text-align: center;\">회원 가입 인증</h2>" +
                "<p style=\"font-size: 16px; color: #666666;\">안녕하세요,</p>" +
                "<p style=\"font-size: 16px; color: #666666;\">회원 가입해 주셔서 감사합니다. 아래는 요청하신 인증 번호입니다:</p>" +
                "<div style=\"text-align: center; margin: 20px 0;\">" +
                "<h1 style=\"color: #7B98FF; font-size: 48px; margin: 0;\">" + code + "</h1>" +
                "</div>" +
                    "<p style=\"font-size: 16px; color: #666666;\">해당 인증 번호를 입력란에 입력하여 회원 가입을 완료해 주세요.</p>" +
                    "<p style=\"font-size: 14px; color: #999999; text-align: center;\">만약 이 요청을 하지 않으셨다면, 이 이메일을 무시해 주세요.</p>" +
                    "<div style=\"text-align: center; margin-top: 30px;\">" +
                "</div>" +
            "</div>"; //이메일 내용 삽입        
        // 이메일 발송 로직
        sendEmail(email, title, content);
        
        // Redis에 저장 (5분 유효)
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));
    }
    
    // 인증 코드 검증
    public void verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(email);
        if(!code.equals(savedCode))
        	throw new CustomException(ExceptionCode.NOT_MATCH_AUTHNUM);

        // 인증받은 Redis에 저장 (60분 유효)
        redisTemplate.opsForValue().set("VERIFIED_"+email, code, Duration.ofMinutes(60));
    }
    
    // 6자리 랜덤 숫자 생성
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    
    // 이메일 전송
    private void sendEmail(String email, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(content);
        emailConfig(email, title, content);
    }
    
    //이메일 전송 설정 메서드
    private void emailConfig(String toMail, String title, String content) {
        MimeMessage message = emailSender.createMimeMessage();

        // true 매개값을 전달하면 multipart 형식의 메세지 전달이 가능.문자 인코딩 설정도 가능
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(FROM_MAIL);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true); // true 전달 > html 형식으로 전송 , 작성하지 않으면 단순 텍스트로 전달됨
            emailSender.send(message); // 이메일 전송
        } catch (MessagingException e) {
            throw new CustomException(ExceptionCode.FAIL_SEND_EMAIL);
        }
    }
}
