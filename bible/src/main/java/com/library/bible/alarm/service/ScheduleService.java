package com.library.bible.alarm.service;

import java.security.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.library.bible.rent.repository.IRentRepository;
import com.library.bible.rent.service.IRentService;
import com.library.bible.rent.service.RentService;

import lombok.RequiredArgsConstructor;

@Service
public class ScheduleService {

    private final RentService rentService;
    private final NotificationService notificationService;
    
    
    public ScheduleService(RentService rentService, NotificationService notificationService) {
        this.rentService = rentService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void scheduleOverdueCheck() {
    	System.out.println("연체 확인");
    	notificationService.sendOverdueNotifications();
    }
    
    @Scheduled(cron = "0 0 11 * * ?") // 매일 자정에 실행
    public void scheduleOverdueSms() {
    	System.out.println("연체 메세지 전송");
        notificationService.sendOverdueCheck();
    	
    }

}
