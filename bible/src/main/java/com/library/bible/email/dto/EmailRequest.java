package com.library.bible.email.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String email;
    private String code;
}
