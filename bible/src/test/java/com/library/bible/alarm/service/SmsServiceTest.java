package com.library.bible.alarm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService.init();  // SMS Key 초기화
    }

    @Test
    void testSendSms() {
        SmsService.sendSms("01012345678", "테스트 메시지");
    }
}

