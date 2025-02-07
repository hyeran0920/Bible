package com.library.bible.alarm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bible.alarm.WebSocketHandler;
import com.library.bible.alarm.dto.AlarmMessage;

@Service
public class AlarmService {

    @Autowired
    private WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void sendAlarm(AlarmMessage alarmMessage) {
        try {
            // DTO → JSON 변환
            String jsonMessage = objectMapper.writeValueAsString(alarmMessage);
            webSocketHandler.sendMessageToAll(jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
