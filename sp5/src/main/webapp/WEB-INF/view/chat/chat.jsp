<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();

   String wsURL = "ws://"+request.getServerName()+":"+request.getServerPort()+cp+"/chat.msg";
%>

<style type="text/css">
#chatMsgContainer{
   clear:both;
   border: 1px solid #ccc;
   height: 285px;
   overflow-y: scroll;
   padding: 3px;
   width: 100%;
}
#chatMsgContainer p{
   padding-bottom: 0px;
   margin-bottom: 0px;
}
#chatConnectionList{
	clear:both;
	width: 100%;
	height: 315px;
	text-align:left;
	padding:5px 5px 5px 5px;
	overflow-y:scroll;
    border: 1px solid #ccc;
}
</style>

<script type="text/javascript">
// ---------------------------------------------
$(function(){
	var socket=null;
	
	// 채팅 서버(IP 주소를 사용 해야함)
	// ws://ip주소:포트번호/cp/chat.msg
	var host = "<%=wsURL%>";
	/* var host = "ws://211.238.142.187:9090/sp5/chat.msg"; */
	
	// WebSocket 객체가 있는지 확인
	if("WebSocket" in window) {
		socket = new WebSocket(host);	
	} else if("MozWebSocket" in window) {
		socket = new MozWebSocket(host);
	} else {
		writeToScreen("WebSocket을 지원하지 않는 브라우저를 사용하고 있습니다.");
		return;
	}
	
	socket.onopen = function(evt) {onOpen(evt)};
	socket.onclose = function(evt) {onClose(evt)};
	socket.onmessage = function(evt) {onMessage(evt)};
	socket.onerror = function(evt) {onError(evt)};
	
	// 서버 접속이 성공한 경우 호출되는 콜백함수
	function onOpen(evt) {
		var userId = "${sessionScope.member.userId}";
		var nickName = "${sessionScope.member.userName}";
		
		if(!userId) {
			location.href = "<%=cp%>/member/login";
			return;
		}
		
		writeToScreen("채팅방에 입장했습니다.");
		
		// 서버에 접속이 성공하면 아이디와 닉넴임을 JSON 형식으로 서버에 전송
		var obj = {};
		var jsonStr;
		obj.cmd = "connect";
		obj.userId = userId;
		obj.nickName = nickName;
		jsonStr = JSON.stringify(obj); // 스크립트 객체르르 JSON 문자열로 변환
		
		socket.send(jsonStr);
		
		$("#chatMsg").on("keydown", function(event){
			if(event.keyCode == 13) {
				sendMessage();
			}
		});
	}

	// 연결이 끊어진 경우에 호출되는 콜백함수
	function onClose(evt) {
		// 채팅 입력창 이벤트를 제거 한다.
		$("#chatMsg").on("keydown", null);
	}

	// 서버로부터 메시지를 받은 경우에 호출되는 콜백함수
	function onMessage(evt) {
		// JSON으로 넘겨 받는다.
		// parse : 순수 자바스크립트로 파싱
		var data = JSON.parse(evt.data); // JSON 파싱
		var cmd = data.cmd;
		
		if(cmd == "connectList") {
			var userId = data.userId;
			var nickName = data.nickName;
			var sp = "<span style='display:block;'";
			sp += "id='guest-" + userId + "'>" + nickName + "</span>";
			
			$("#chatConnectionList").append(sp);
		} else if(cmd == "connect") {
			var userId = data.userId;
			var nickName = data.nickName;
			var sp = "<span style='display:block;'";
			sp += "id='guest-" + userId + "'>" + nickName + "</span>";
			
			$("#chatConnectionList").append(sp);
			
			var msg = nickName + " 님이 입장하셨습니다.";
			writeToScreen(msg);
		} else if(cmd == "disconnect") {
			var userId = data.userId;
			var nickName = data.nickName;
			
			$("#guest-" + userId).remove();
			
			var msg = nickName + " 님이 퇴장하셨습니다.";
			writeToScreen(msg);
		} else if(cmd == "message") {
			var userId = data.userId;
			var nickName = data.nickName;
			var msg = data.chatMsg;
			
			writeToScreen(nickName + "]" + msg);
		}
	}

	// 에러가 발생시 호출되는 콜백함수
	function onError(evt) {

	}
	
	// 메시지 전송
	function sendMessage() {
		var msg = $("#chatMsg").val().trim();
		if(msg != "") {
			var obj = {};
			var jsonStr;
			obj.cmd = "message";
			obj.chatMsg = msg;
			jsonStr = JSON.stringify(obj);
			
			socket.send(jsonStr);
			
			$("#chatMsg").val("");
			
			writeToScreen("전송]" + msg);
		}
	}
	
});
//---------------------------------------------

// 채팅 메시지를 출력하기 위한 함수
function writeToScreen(message) {
    var $chatContainer = $("#chatMsgContainer");
	
    $chatContainer.append("<p>");
    $chatContainer.find("p:last").css("word-break", "break-word");
    $chatContainer.find("p:last").html(message);
}
</script>

<div class="body-container" style="width: 700px;">
    <div class="body-title">
        <h3><span style="font-family: Webdings">2</span> 채팅 <small style="font-size:65%; font-weight: normal;">Chatting</small></h3>
    </div>
    
    <div style="clear: both;">
        <div style="float: left; width: 350px;">
            <div style="clear: both; padding-bottom: 5px;">
                <span style="font-weight: 600;">＞</span>
                <span style="font-weight: 600; font-family: 나눔고딕, 맑은 고딕, 돋움; color: #424951;">채팅 메시지</span>
            </div>
            <div id="chatMsgContainer"></div>
            <div style="clear: both; padding-top: 5px;">
                <input type="text" id="chatMsg" class="boxTF"  style="width: 99%;"
                            placeholder="채팅 메시지를 입력 하세요...">
            </div>
        </div>
        
        <div style="float: left; width: 20px;">&nbsp;</div>
        
        <div style="float: left; width: 170px;">
            <div style="clear: both; padding-bottom: 5px;">
                <span style="font-weight: 600;">＞</span>
                <span style="font-weight: 600; font-family: 나눔고딕, 맑은 고딕, 돋움; color: #424951;">접속자 리스트</span>
            </div>
            <div id="chatConnectionList"></div>
        </div>
    </div>

</div>