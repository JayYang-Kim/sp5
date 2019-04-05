package com.sp.chat2;

import org.springframework.web.socket.WebSocketSession;

public class User {
	private String userId;
	private String nickName;
	private WebSocketSession session; // Web Socket
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public WebSocketSession getSession() {
		return session;
	}
	public void setSession(WebSocketSession session) {
		this.session = session;
	}
}
