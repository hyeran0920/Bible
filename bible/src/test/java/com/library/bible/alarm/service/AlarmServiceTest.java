//package com.library.bible.alarm.service;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.library.bible.alarm.dto.AlarmMessage;
//@Disabled
//@ExtendWith(MockitoExtension.class)
//class AlarmServiceTest {
//
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private AlarmService alarmService;
//
//    private AlarmMessage alarmMessage;
//
//    @BeforeEach
//    void setUp() {
//        alarmMessage = new AlarmMessage();
//        alarmMessage.setAlarmTitle("Test Alarm");
//        alarmMessage.setAlarmImgUrl("test-url");
//        alarmMessage.setAlarmText("Test message");
//        
//        when(notificationService.sendNotification(any())).thenReturn(true);
//    }
//    
//    @Test
//    void testSendAlarm() {
//        alarmService.sendAlarm(alarmMessage);
//        verify(notificationService, times(1)).notifyUser(any());
//    }
//}

