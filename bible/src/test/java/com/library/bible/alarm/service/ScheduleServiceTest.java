package com.library.bible.alarm.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private ScheduleService scheduleService;
    @Disabled
    @BeforeEach
    void setUp() {
        //when(notificationService.checkOverdueNotifications()).thenReturn(true);
        //when(notificationService.checkBestSeller()).thenReturn(true);
    }

    @Test
    void testScheduleOverdueCheck() {
        scheduleService.scheduleOverdueCheck();
        verify(notificationService, times(1)).checkOverdueNotifications();
        verify(notificationService, times(1)).checkBestSeller();
        verify(alarmService, times(1)).sendAlarm(any());
    }

    @Test
    void testScheduleOverdueSms() {
        scheduleService.scheduleOverdueSms();
        verify(notificationService, times(1)).sendOverdueCheck();
    }
}
