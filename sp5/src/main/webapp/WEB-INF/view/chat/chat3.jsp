<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
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

<script src="http://211.238.142.187:3001/socket.io/socket.io.js"></script>
<script type="text/javascript">
$(function(){
	var uid = "${sessionScope.member.userId}";
	var nickName = "${sessionScope.member.userName}";
    if(! uid) {
    	location.href="<%=cp%>/member/login";
    	return;
    }
    
	var chat = io("http://211.238.142.187:3001/chat");
	
	// 접속 사실을 전송함
	chat.emit("connUser", {
		userId:uid, nickName:nickName
	});
	
	$("#chatMsg").on("keydown",function(event) {
    	// 엔터키가 눌린 경우, 서버로 메시지를 전송한다.
        if (event.keyCode == 13) {
        	var message = $("#chatMsg").val().trim();
        	
        	chat.emit("chatMsg", {
        		userId : uid,
        		nickName:nickName,
        		message : message
    		});
        	
        	$("#chatMsg").val("");
        	$("#chatMsg").focus();
        }
    });
	
	// 서버로 부터 접속자 리스트 받기
	chat.on("connList", function(data){
		console.log(data);
		
		var userId, nickName, s;
		for(var idx=0; idx<data.length; idx++) {
			userId=data[idx].userId;
			nickName=data[idx].nickName;
			
			s="<span style='display: block;' id='guest-"+userId+"'>"+userId+"("+nickName+")<span>";
			$("#chatConnectionList").append(s);
		}
	});

	// 서버로 부터 채팅방에 입장한 사람 받기
	chat.on("connUser", function(data){
		var userId=data.userId;
		var nickName=data.nickName;
		
		var s;
		if(userId==uid) {
			s = "채팅방에 입장 했습니다.";
		} else {
			s="<span style='display: block;' id='guest-"+userId+"'>"+userId+"("+nickName+")<span>";
			$("#chatConnectionList").append(s);
			
			s = nickName+"("+userId+") 님이 입장하였습니다.";
		}
		writeToScreen(s);
	});
	
	// 서버로 부터 채팅 메세지 받기
	chat.on("chatMsg", function(data){
		var userId=data.userId;
		var nickName=data.nickName;
		var msg=data.message;
		
		var s;
		if(userId==uid) {
			s = "보냄] " + msg;
		} else {
			s = nickName+"] " + msg;
		}
		
		writeToScreen(s);
	});
	
	// 서버로 부터 채팅방을 나간 사람 받기
	chat.on("disUser", function(data){
		var userId=data.userId;
		var nickName=data.nickName;

		var s=userId+"("+nickName+") 님이 나가셨습니다.";
		$("#guest-"+userId).remove();

		writeToScreen(s);
	});
	
	// 메세지 출력
	function writeToScreen(message) {
	    var $chatContainer = $("#chatMsgContainer");
	    $chatContainer.append("<p/>");
	    $chatContainer.find("p:last").css("wordWrap","break-word"); // 강제로 끊어서 줄 바꿈
	    $chatContainer.find("p:last").html(message);

	    // 추가된 메시지가 50개를 초과하면 가장 먼저 추가된 메시지를 한개 삭제
	    while ($chatContainer.find("p").length > 50) {
	    	$chatContainer.find("p:first").remove();
		}

	    // 스크롤을 최상단에 있도록 설정함
	    $chatContainer.scrollTop($chatContainer.prop("scrollHeight"));
	}
});
</script>

<div class="body-container" style="width: 700px;">
    <div class="body-title">
        <h3><i class="far fa-comment-alt"></i> 채팅 <small style="font-size:65%; font-weight: normal;">Chatting</small></h3>
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