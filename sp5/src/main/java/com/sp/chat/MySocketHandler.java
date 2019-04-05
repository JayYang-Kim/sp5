package com.sp.chat;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocketHandler
 * 스프링은 WebSocketHandler를 구현함으로써 WebSocket 서버를 만드는것을 지원한다.
 * 
 * WebSocketHandler는 TextWebSocketHandler(텍스트 문자 통신), BinaryWebSocketHandler(이진 데이터 통신)로 나뉜다.
 * @author sist
 *
 */
public class MySocketHandler extends TextWebSocketHandler {
	
	private final Logger logger = LoggerFactory.getLogger(MySocketHandler.class);
	private Map<String, User> sessionMap = new Hashtable<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// WebSocket 연결되고 사용할 준비가 될때 호출
		super.afterConnectionEstablished(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// 클라이언트로부터 메시지가 도착했을때 호출
		super.handleMessage(session, message);
		
		JSONObject jseonReceive = null;
		try {
			// message.getPayload() : 클라이언트로부터 넘어온 메시지
			jseonReceive = new JSONObject(message.getPayload().toString());
		} catch (Exception e) {
			
		}
		
		if(jseonReceive == null) {
			return;
		}
		
		String cmd = jseonReceive.getString("cmd");
		if(cmd == null) {
			return;
		}
		
		if(cmd.equals("connect")) {
			// 처음 접속한 경우
			
			String userId = jseonReceive.getString("userId");
			String nickName = jseonReceive.getString("nickName");
			
			User user = new User();
			user.setUserId(userId);
			user.setNickName(nickName);
			user.setSession(session);
			
			sessionMap.put(userId, user); // 접속한 사용자를 map에 저장
			
			// 현재 접속중인 사용자를 전송
			Iterator<String> it = sessionMap.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(userId.equals(key)) {
					continue;
				}
				
				User u = sessionMap.get(key);
				
				JSONObject ob = new JSONObject();
				ob.put("cmd", "connectList");
				ob.put("userId", u.getUserId());
				ob.put("nickName", u.getNickName());
				sendOneMessage(ob.toString(), session);
			}
			
			// 다른 클라이언트에게 접속 사실을 알림
			JSONObject ob = new JSONObject();
			ob.put("cmd", "connect");
			ob.put("userId", userId);
			ob.put("nickName", nickName);
			sendAllMessage(ob.toString(), userId);
			
		} else if(cmd.equals("message")) {
			// 채팅 메시지를 받은 경우
			User user = getUser(session);
			String msg = jseonReceive.getString("chatMsg");
			
			JSONObject ob = new JSONObject();
			ob.put("cmd", "message");
			ob.put("chatMsg", msg);
			ob.put("userId", user.getUserId());
			ob.put("nickName", user.getNickName());
			
			// 다른 사용자에게 메시지 전달
			sendAllMessage(ob.toString(), user.getUserId());
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// 전송 에러가 발생했을때 호출
		removeUser(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// WebSocket 연결이 끊어졌을때 호출
		super.afterConnectionClosed(session, status);
		
		removeUser(session);
		
		this.logger.info("remove session");
	}
	
	// 특정 사용자에게 메시지 전송
	protected void sendOneMessage(String message, WebSocketSession session) {
		try {
			if(session.isOpen()) {
				session.sendMessage(new TextMessage(message));
			}
		} catch (Exception e) {
			this.logger.error("fail to send message", e);
			//removeUser(session);
		}
	}
	
	// 모든 사용자에게 메시지 전송 (자신을 제외한 모든 사용자)
	protected void sendAllMessage(String message, String out) {
		Iterator<String> it = sessionMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			if(out != null && out.equals(key)) { // 자기 자신
				continue;
			}
			
			User user = sessionMap.get(key);
			WebSocketSession session = user.getSession(); // session : 접속한 클라이언트 1명
			
			try {
				if(session.isOpen()) {
					session.sendMessage(new TextMessage(message));
				}
			} catch (Exception e) {
				removeUser(session);
			}
		}
	}
	
	// session 객체를 이용하여 Map에 저장된 유저 검색
	protected User getUser(WebSocketSession session) {
		User user = null;
		
		Iterator<String> it = sessionMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			User u = sessionMap.get(key);
			if(session == u.getSession()) {
				user = u;
				break;
			}
		}
		
		return user;
	}
	
	protected void removeUser(WebSocketSession session) {
		// 클라이언트가 나간 경우
		User user = getUser(session);
		if(user != null) {
			JSONObject job = new JSONObject();
			job.put("cmd", "disconnect");
			job.put("userId", user.getUserId());
			job.put("nickName", user.getNickName());
			
			sendAllMessage(job.toString(), user.getUserId());
			
			try {
				user.getSession().close();
			} catch (Exception e) {
				
			}
			
			sessionMap.remove(user.getUserId());
		}
	}
}
