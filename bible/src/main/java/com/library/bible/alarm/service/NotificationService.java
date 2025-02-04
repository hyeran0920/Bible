package com.library.bible.alarm.service;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
	
	
	private final SmsService smsService;

    public NotificationService(SmsService smsService) {
        this.smsService = smsService;
    }

    private String formatMessage(String memName, String bookTitle, String msg) {
        return String.format("%s님, %s, %s", memName, bookTitle, msg);
    }

    
    private void sendSmsNotification(String memName, String bookTitle, String msg, String phone) {
        String text = formatMessage(memName, bookTitle, msg);
        //SmsService.sendSms(phone, text); //이거 활성화하면 정해진 시간에 문자가 갑니다
        System.out.println("SMS 전송 시간: " + new Date() + ", 내용: " + text);
    }

    // 반납 마감 당일 알림
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBookReturnDDayReminder() {
        sendSmsNotification("우경", "누가 내 머리에 똥 쌌어", "당장 반납", "01041610209");
    }

    // 반납 연체 알림
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendWarnLateReturn() {
        sendSmsNotification("소희", "누가 내 머리에 똥 쌌어", "반납 연체 3일", "01041610209");
    }

    // 반납 마감 임박 알림 (하루 전)
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBookReturnReminder() {
        sendSmsNotification("수민", "누가 내 머리에 똥 쌌어", "내일까지 반납", "01041610209");
    }

    // 대여 신청 취소 알림
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendRentalRequestCancelled() {
        sendSmsNotification("유진", "책 먹는 여우", "금일 방문하지 않아 대여 신청 취소됨", "01041610209");
    }

    // 대여 신청 성공 알림
    public void sendRentalRequestSuccess(String memName, String bookTitle, String dueDate, String phone) {
        String msg = String.format("대여 신청이 완료. %s 전까지 대출", dueDate);
        sendSmsNotification(memName, bookTitle, msg, phone);
    }

    
    
	// 초,분,시,일,월,요일(연도)
    // ‘*’는 모든값
	// ‘?’는	사용 안함,	‘-’는 기간, ‘,’는 값 지정+나열, ‘/’는 시작/반복 간격
}

