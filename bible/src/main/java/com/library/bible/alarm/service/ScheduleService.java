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

    public ScheduleService(RentService rentService) {
        this.rentService = rentService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void scheduleOverdueCheck() {
        List<String> overdueMessages = rentService.processOverdueBooks();
        // 여기서 overdueMessages를 처리합니다 (예: 로깅, 알림 발송 등)
        for (String message : overdueMessages) {
            System.out.println(message); // 또는 로그로 기록
        }
    }
	
}
