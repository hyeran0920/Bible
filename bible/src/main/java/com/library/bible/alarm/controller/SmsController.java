package com.library.bible.alarm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.alarm.service.SmsService;
import com.library.bible.alarm.dto.SmsRequest;
@RestController
@RequestMapping("/api/sms")
public class SmsController {
    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest) {
        smsService.sendSms(smsRequest.getTo(), smsRequest.getText());
        return ResponseEntity.ok("SMS Sent Successfully");
    }
}



