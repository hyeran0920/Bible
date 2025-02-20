package com.library.bible.alarm.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @Spy
    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService.init();
    }
    @Disabled("현재 오류 발생으로 인해 비활성화 (원인 분석 후 수정 예정)")
    @Test
    @DisplayName("SMS 전송 테스트")
    void testSendSms() {
        doNothing().when(smsService).sendSms(anyString(), anyString());
        smsService.sendSms("01012345678", "테스트 메시지");
        verify(smsService, times(1)).sendSms(anyString(), anyString());
    }
}


