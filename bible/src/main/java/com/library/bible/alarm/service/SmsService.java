package com.library.bible.alarm.service;

import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
public class SmsService {

    public static void sendSms(String to, String text) {
    	DefaultMessageService messageService 
    		=  NurigoApp.INSTANCE.initialize("NCSMCSRDHDXMWNOA", "BSRCNYR2U5UEXLOQBZAGSCIJLWNRBAU6", "https://api.coolsms.co.kr");
    	// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환
    	
    	Message message = new Message();
    	message.setFrom("01041610209");
    	message.setTo(to);
    	message.setText(text);

    	try {
    	  // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
    	  messageService.send(message);
    	} catch (NurigoMessageNotReceivedException exception) {
    	  // 발송에 실패한 메시지 목록을 확인
    	  System.out.println(exception.getFailedMessageList());
    	  System.out.println(exception.getMessage());
    	} catch (Exception exception) {
    	  System.out.println(exception.getMessage());
    	}
    }
}
