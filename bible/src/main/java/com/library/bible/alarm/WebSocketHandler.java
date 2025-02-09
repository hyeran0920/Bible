package com.library.bible.alarm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.library.bible.security.jwt.JwtProvider;

import java.util.List;
import java.util.Optional;

public class WebSocketHandler extends TextWebSocketHandler implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<WebSocketSession> sessionSet = new HashSet<>();
    
    // JWT 기반 사용자 세션 매핑 (memId -> WebSocketSession)
    private final Map<Long, WebSocketSession> jwtSessionMap = new ConcurrentHashMap<>();

    private final JwtProvider jwtProvider; // JwtProvider 주입

    public WebSocketHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        logger.info("WebSocketHandler 인스턴스 생성됨");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionSet.remove(session);

        // memId 기반으로 세션 제거
        jwtSessionMap.values().remove(session);
        logger.info("사용자 세션 삭제");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	
    	System.out.println("after connection++++++++++++++++++++++++++++++++++");
    	System.out.println("세션 개수="+sessionSet.size());
        
        super.afterConnectionEstablished(session);
        sessionSet.add(session);
        
        // JWT 토큰에서 memId 추출
        Long memId = extractMemIdFromJwt(session);
        
        if (memId != null) {
            jwtSessionMap.put(memId, session);
            logger.info("사용자 {} WebSocket 세션 등록 완료", memId);
        } else {
            logger.warn("JWT에서 memId를 추출할 수 없음, 세션 등록 안됨");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("웹소켓 에러!", exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }

    public int getSessionCount() {
        return sessionSet.size();
    }

    // 특정 사용자에게 알람 전송 (JWT 기반)
    public void sendMessageToUser(Long memId, String messageContent) {
        
    	//System.out.println("jwt session 개수"+jwtSessionMap.size());
    	
    	
        /*
        for (Map.Entry<Long, WebSocketSession> entry : jwtSessionMap.entrySet()) {
            Long key = entry.getKey();
            WebSocketSession session = entry.getValue();
            
            System.out.println("memId: " + key + ", WebSocketSession ID: " + session.getId());
            
            if(key==memId) {
            	try{session.sendMessage(new TextMessage(messageContent));}
            	catch(Exception e) {
            		logger.error("사용자 {} 에게 메시지 전송 실패", memId, e);
            	}
            	break;
            }
        }
        */
    	
    	WebSocketSession session = jwtSessionMap.get(Long.valueOf(memId));
    	if (session != null && session.isOpen()) {
	    	try{session.sendMessage(new TextMessage(messageContent));}
	    	catch(Exception e) {
	    		logger.error("사용자 {} 에게 메시지 전송 실패", memId, e);
	    	}
    	}else {
    		logger.error("사용자 {}의 WebSocket session 없음",memId);
    	}
        /*
        WebSocketSession session = jwtSessionMap.get(memId);
        if(session==null || !session.isOpen()) {
        	System.out.println("websocket 세션이 없스니다");
        }
    	*/
        /*
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(messageContent));
                logger.info("사용자 {} 에게 메시지 전송: {}", memId, messageContent);
            } catch (Exception e) {
                logger.error("사용자 {} 에게 메시지 전송 실패", memId, e);
            }
        } else {
            logger.warn("사용자 {} 의 WebSocket 세션이 없음", memId);
        }
        */
    }

    // 모든 사용자에게 알람 전송
    public void sendMessageToAll(String messageContent) {
        for (WebSocketSession session : sessionSet) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(messageContent));
                    //logger.info("메시지 전송 성공: {}", messageContent);
                } catch (Exception e) {
                    logger.error("메시지 전송 실패", e);
                }
            }
        }
    }

    // WebSocket 세션에서 JWT를 읽고 사용자 memId 추출 (JwtProvider 사용)
    private Long extractMemIdFromJwt(WebSocketSession session) {
        try {
            // WebSocket 세션에서 "Cookie" 헤더 가져오기
            List<String> cookies = session.getHandshakeHeaders().get("Cookie");

            if (cookies != null && !cookies.isEmpty()) {
                // 일반적으로 Cookie 헤더는 하나의 문자열로 전달됨
                String cookieHeader = cookies.get(0);
                System.out.println("전체 쿠키: " + cookieHeader); // 디버깅용 출력

                // 쿠키 문자열을 `; ` 기준으로 분리
                String[] cookiePairs = cookieHeader.split("; ");

                for (String cookie : cookiePairs) {
                    // "authorization=JWT토큰값" 형태이므로 "=" 기준으로 분리
                    String[] keyValue = cookie.split("=", 2);

                    if (keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase("authorization")) {
                        String jwtToken = keyValue[1].trim();
                        System.out.println("추출된 JWT: " + jwtToken); // 디버깅용 출력

                        // JWT 검증 및 memId 추출
                        DecodedJWT decodedJWT = jwtProvider.getClaimsAndVerifyAccessToken(jwtToken);
                        return Long.valueOf(decodedJWT.getSubject());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("세션에서 JWT 토큰 추출 실패", e);
        }
        return null;
    }

    


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Runnable 시작");
        Thread t = new Thread(new AlarmRunnable());
        t.start();
    }

    private class AlarmRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                	
                	//Send All Messages
                    String alarmTitle = "알림";
                    String alarmText = "반납하세요!!!";
                    String messageContent = "{\"alarmTitle\":\"" + alarmTitle + "\",\"alarmText\":\"" + alarmText + "\"}";
                    sendMessageToAll(messageContent);

                    
                    //Send User Messages
                    String messageContent2 = "{\"alarmTitle\":\"" + "헐" + "\",\"alarmText\":\"" + "되나요" + "\"}";
                    sendMessageToUser((long) 62, messageContent2);
                    
                    
                    Thread.sleep(5000); // 간격으로 메시지 전송
                } catch (InterruptedException e) {
                    logger.error("쓰레드 중단!", e);
                    break;
                } catch (Exception e) {
                    logger.error("메시지 전송 실패!", e);
                }
            }
        }
    }
}
