package com.library.bible.email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.email.dto.EmailRequest;
import com.library.bible.email.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailController {
    private final EmailService emailService;
    
    @PostMapping("/verifications")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody EmailRequest request) {
        emailService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/verifications")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailRequest request) {
        emailService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
