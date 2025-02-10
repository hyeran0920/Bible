package com.library.bible.alarm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.alarm.dto.AlarmMessage;
import com.library.bible.alarm.service.AlarmService;

@RestController
@RequestMapping("/api/alarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @PostMapping("/send")
    public ResponseEntity<String> sendAlarm(@RequestBody AlarmMessage alarmMessage) {
        try {
            // 알람 전송 시도
            alarmService.sendAlarm(alarmMessage);
            return ResponseEntity.ok("전체 알람 전송 완료");
        } catch (Exception e) {
            // 실패 응답 반환 (500 상태 코드)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("전체 알람 전송 실패: " + e.getMessage());
        }
    }

    
    @PostMapping
    public ResponseEntity<String> sendUserAlarm(
            @RequestBody AlarmMessage alarmMessage,
            @RequestParam long memId) {
        try {
            // 알람 전송 시도
            alarmService.sendUserAlarm(memId, alarmMessage);
            return ResponseEntity.ok("유저에게 알람 전송 완료");
            
        } catch (Exception e) {
            // 실패 반환 (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("알람 전송 실패: " + e.getMessage());
        }
    }

}
