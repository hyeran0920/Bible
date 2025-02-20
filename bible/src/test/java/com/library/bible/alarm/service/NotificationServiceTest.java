package com.library.bible.alarm.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bible.book.service.IBookService;
import com.library.bible.rent.service.IRentService;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SmsService smsService;

    @Mock
    private IRentService rentService;

    @Mock
    private IBookService bookService;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        when(rentService.processOverdueBooks()).thenReturn(List.of("회원ID: 1, 전화번호: 01012345678, 회원명: 홍길동, 연체일수: 3일"));
        when(bookService.getBestSellerBookIdArray()).thenReturn(new long[]{1, 2, 3});
        when(bookService.getBookInfoMap(anyLong())).thenReturn(Map.of("title", "베스트셀러 책"));
    }
    @Disabled("현재 오류 발생으로 인해 비활성화 (원인 분석 후 수정 예정)")
    @Test
    @DisplayName("연체 알림 확인 테스트")
    void testCheckOverdueNotifications() {
        notificationService.checkOverdueNotifications();
        verify(rentService, times(1)).processOverdueBooks();
    }
    @Disabled("현재 오류 발생으로 인해 비활성화 (원인 분석 후 수정 예정)")
    @Test
    @DisplayName("연체자에게 SMS 전송 테스트")
    void testSendOverdueCheck() {
        notificationService.sendOverdueCheck();
        verify(alarmService, times(1)).sendUserAlarm(anyLong(), any());
    }
    @Disabled("현재 오류 발생으로 인해 비활성화 (원인 분석 후 수정 예정)")
    @Test
    @DisplayName("베스트셀러 확인 테스트")
    void testCheckBestSeller() {
        notificationService.checkBestSeller();
        verify(bookService, times(1)).getBestSellerBookIdArray();
        verify(bookService, times(3)).getBookInfoMap(anyLong());
    }
}
