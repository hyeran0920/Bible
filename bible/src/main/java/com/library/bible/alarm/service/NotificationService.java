package com.library.bible.alarm.service;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.library.bible.rent.dto.RentMemberResponse;
import com.library.bible.rent.service.RentService;

@Service
public class NotificationService {
	// 초,분,시,일,월,요일(연도)
    // ‘*’는 모든값
	// ‘?’는	사용 안함,	‘-’는 기간, ‘,’는 값 지정+나열, ‘/’는 시작/반복 간격
	private final SmsService smsService;
    private final RentService rentService;

    public NotificationService(SmsService smsService, RentService rentService) {
        this.smsService = smsService;
		this.rentService = rentService;
    }

    private void sendSmsNotification(String phone, String text) {
        SmsService.sendSms(phone, text); //이거 활성화하면 정해진 시간에 문자가 갑니다
    }
    
    public void sendOverdueNotifications() {
        List<String> overdueMessages = rentService.processOverdueBooks();
        for (String message : overdueMessages) {
        }
    }

    public void sendOverdueCheck() {
        List<String> overdueMessages = rentService.processOverdueBooks();    
        for (String message : overdueMessages) {
            String[] parts = message.split(", ");
            String phone = "";
            String memberName = "";
            String overdueDays = "";
            
            for (String part : parts) {
                if (part.startsWith("전화번호: ")) {
                    phone = part.substring("전화번호: ".length());
                } else if (part.startsWith("회원명: ")) {
                    memberName = part.substring("회원명: ".length());
                } else if (part.startsWith("연체일수: ")) {
                    overdueDays = part.substring("연체일수: ".length()).replace("일", "");
                }
            }
            
            String text = String.format("%s님 현재 %s일 연체중입니다", memberName, overdueDays);
            
            System.out.println("전화번호: " + phone);
            System.out.println("메시지: " + text);
            System.out.println("--------------------");
            
             sendSmsNotification(phone, text);
        }
    }


}
   

