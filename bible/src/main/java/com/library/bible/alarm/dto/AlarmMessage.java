package com.library.bible.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessage {
    private String alarmTitle;
    private String alarmText;
    private String alarmImgUrl;
}
