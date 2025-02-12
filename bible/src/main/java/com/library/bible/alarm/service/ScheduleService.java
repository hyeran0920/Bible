package com.library.bible.alarm.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.library.bible.alarm.dto.AlarmMessage;

@Service
public class ScheduleService {

    private final NotificationService notificationService;
    private final AlarmService alarmService;
    
    public ScheduleService(NotificationService notificationService, AlarmService alarmService) {
        this.notificationService = notificationService;
        this.alarmService=alarmService;
    }

    @Scheduled(cron = "0 0 12 * * ?") // 매일 자정에 실행
    public void scheduleOverdueCheck() {
    	System.out.println("자정 스케줄링 실행");
    	notificationService.checkOverdueNotifications();
    	notificationService.checkBestSeller();
    	
    	
    	//send websocket alarm
        AlarmMessage alarm=new AlarmMessage();
        alarm.setAlarmTitle("알림");
        alarm.setAlarmImgUrl("");
        alarm.setAlarmText("업데이트 된 베스트셀러를 확인하세요!");
        alarmService.sendAlarm(alarm);
        
    }
    
    @Scheduled(cron = "0 0 11 * * ?") // 매일 자정에 실행
    public void scheduleOverdueSms() {
    	System.out.println("연체 메세지 전송");
        notificationService.sendOverdueCheck();

    }

}
