package com.library.bible.alarm.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String sendAlarm(@RequestBody AlarmMessage alarmMessage) {
        alarmService.sendAlarm(alarmMessage);
        return "알람 전송 완료";
    }
    
    @PostMapping
    public String sendUserAlarm(
    		@RequestBody AlarmMessage alarmMessage,
    		@RequestParam long memId) {
    	
    	alarmService.sendUserAlarm(memId,alarmMessage);
    	return "유저에게 알람 전송 완료";
    }
}
