package com.library.bible.alarm.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bible.alarm.WebSocketHandler;
import com.library.bible.alarm.dto.AlarmMessage;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private WebSocketHandler webSocketHandler;

    @InjectMocks
    private AlarmService alarmService;

    private AlarmMessage alarmMessage;

    @BeforeEach
    void setUp() {
        alarmMessage = new AlarmMessage();
        alarmMessage.setAlarmTitle("Test Alarm");
        alarmMessage.setAlarmImgUrl("test-url");
        alarmMessage.setAlarmText("Test message");
    }

    @Test
    @DisplayName("전체 사용자에게 알람 전송 테스트")
    void testSendAlarm() {
        alarmService.sendAlarm(alarmMessage);
        verify(webSocketHandler, times(1)).sendMessageToAll(anyString());
    }

    @Test
    @DisplayName("특정 사용자에게 알람 전송 테스트")
    void testSendUserAlarm() {
        alarmService.sendUserAlarm(1L, alarmMessage);
        verify(webSocketHandler, times(1)).sendMessageToUser(eq(1L), anyString());
    }
}

