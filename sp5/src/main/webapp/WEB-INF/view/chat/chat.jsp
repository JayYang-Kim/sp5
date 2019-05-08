<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<style type="text/css">
.room-btn-container {
	clear: both;
	text-align: center;
}
.chatting-room-btn {
	display:inline-block;
    width:200px;
    height:100px; line-height:100px;
    text-align:center;
    margin : 7px;
    color:#333333;
    font-weight:500;
    font-family:"Malgun Gothic", "맑은 고딕", NanumGothic, 나눔고딕, 돋움, sans-serif;
    border:1px solid #cccccc;
    background-color:#fff;
    text-align:center;
    cursor:pointer;
    border-radius:4px;
}
.chatting-room-btn:active, .chatting-room-btn:hover {
    background-color:#e6e6e6;
    border-color: #adadad;
    color: #333333;
}

/* 대화상자 내부 스타일 */
.ui-widget-content {
    /* background: none;
	background: #9DCFFF;*/
}

.chatting-header {
	clear: both;
	width: 100%; height: 25px;
	box-sizing: border-box;
}
.chatting-content-list {
	width: 100%; height: 420px;
  	box-sizing: border-box;
	background:#8BBCFF; overflow: auto; clear: both;
}
.chatting-content-list hr {
    border-bottom:1px solid #4c4c4c;
    position: relative;
	top: 5px;
	margin: 0px 3px;
}

</style>

<script src="http://211.238.142.187:3001/socket.io/socket.io.js"></script>
<script type="text/javascript">
function convertStringToDate(str) {
	// yyyy-mm-dd hh:mi:ss
    
    return new Date(str.substr(0,4), str.substr(5,2)-1, str.substr(8,2), str.substr(11,2), str.substr(14,2), str.substr(17,2));
}

function convertDateTimeToString(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = (m>9 ? '' : '0') + m;
    var d = date.getDate();
    d = (d>9 ? '' : '0') + d;

    var hh = date.getHours();
    hh = (hh>9 ? '' : '0') + hh;
    var mi=date.getMinutes();
    mi = (mi>9 ? '' : '0') + mi;
    var ss=date.getSeconds();
    ss = (ss>9 ? '' : '0') + ss;
    
    return y + '-' + m + '-' + d +' ' + hh + ':'+mi+":"+ss;
}

function convertDateToString(date) {
	var week = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    var w = week[date.getDay()];
    
    return y + '년 ' + m + '월 ' + d +"일 " + w;
}

function yyyymmdd(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    
    return [y,
        (m>9 ? '' : '0') + m,
        (d>9 ? '' : '0') + d
       ].join('');
}

function convertTimeToString(date) {
    var h = date.getHours();
    var m=date.getMinutes();
    var ampm='오전';
    if (h>=12) ampm='오후';
    if (h>12) h=h-12;
    if (h==0) h=12;
    
    return ampm+" "+h+":"+m;
}

function compareToDate(date1, date2) {
	var d1, d2;
	
	if (typeof date1 === 'object' && date1 instanceof Date && typeof date2 === 'object' && date2 instanceof Date) {
		d1 = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
		d2 = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
	} else {
		// yyyymmdd
		d1 = new Date(date1.substr(0,4), date1.substr(4,2)-1, date1.substr(6,2));
		d2 = new Date(date2.substr(0,4), date2.substr(4,2)-1, date2.substr(6,2));
	}
	
	return d1.getTime() - d2.getTime();
}

$(function(){
	var uid = "${sessionScope.member.userId}";
	var nickName = "${sessionScope.member.userName}";
    if(! uid) {
    	location.href="<%=cp%>/member/login";
    	return;
    }

	var first_date = null; // 화면에 출력된 메시지의 최초의 날짜 
	var last_date = null;  // 화면에 출력된 메시지의 마지막 날짜 
	var room = null;
	
	// 채팅 서버에 접속
	var sock = io('http://localhost:3001/chat');
	
	// 채팅방 입장
	$(".chatting-room-btn").on("click", function(){
		room = $(this).attr("data-room");
		first_date = last_date = new Date();
		
		var roomName = $(this).text();
		$(".chatting-content-list").empty();
		$(".chatting-room-name").html("["+roomName+"]");
		
		$('#chatting-dialog').dialog({
			  modal: true,
			  height: 550,
			  width: 390,
			  title: '채팅',
			  close: function(event, ui) {
			  }
		});
		
		// 오늘 날짜의 룸 채팅 문자열 리스트 요청
		sock.emit("chat-msg-list", {
			room : room,
			writeDate : convertDateTimeToString(last_date)
		});
	});
	
	// 채팅 메시지 보내기
	$("#chatMsg").on("keydown",function(event) {
    	// 엔터키가 눌린 경우, 서버로 메시지를 전송한다.
        if (event.keyCode == 13) {
        	var message = $("#chatMsg").val().trim();
        	
        	if(!message) {
        		return false;
        	}
        	
        	var msg = {
        		room:room,	
        		writeDate:convertDateTimeToString(new Date()),	
        		userId:uid,
        		nickName:nickName,
        		message:message,	
        	};
        	
			sock.emit("chat-msg", msg);
        	
        	$("#chatMsg").val("");
        	$("#chatMsg").focus();
        }
    });
	
	// 더보기 보내기
	$(".chatting-msg-more").click(function(){
		first_date.setDate(first_date.getDate()-1);
		
		// 이전 날짜의 룸 채팅 문자열 리스트 요청
		sock.emit("chat-msg-list", {
			room : room,
			writeDate : convertDateTimeToString(first_date)
		});
		
		/*
		var out;
		var dispDate = convertDateToString(first_date);
		var strDate = yyyymmdd(first_date);
    	var cls = "date-"+strDate;
		out =  "<div class='"+cls+"'>";
    	out += " <div style='clear: both; margin: 7px 5px 3px;'>";
	    out += "   <div style='float: left; font-size: 10px; padding-right: 5px;'><i class='far fa-calendar'></i> "+dispDate+"</div>";
	    out += "   <hr>";
	    out += "  </div>";
	    out += "</div>";
	    $(".chatting-content-list").prepend(out);
	    */
	});
	
	// 채팅 문자열이 전송된 경우
	sock.on("chat-msg", function(data) {
		writeToScreen(data);
	});	
	
	function writeToScreen(data) {
		var room = data.room;
		var writeDate = convertStringToDate(data.writeDate);
		var userId = data.userId;
		var nickName = data.nickName;
		var message = data.message;

		var out;
		var dispDate = convertDateToString(writeDate);
		var dispTime = convertTimeToString(writeDate);
		var strDate = yyyymmdd(writeDate);
    	var cls = "date-"+strDate;

		if(! $(".chatting-content-list").children("div").hasClass(cls)) {
			// 날짜 출력
			out =  "<div class='"+cls+"'>";
	    	out += " <div style='clear: both; margin: 7px 5px 3px;'>";
		    out += "   <div style='float: left; font-size: 10px; padding-right: 5px;'><i class='far fa-calendar'></i> "+dispDate+"</div>";
		    out += "   <hr>";
		    out += "  </div>";
		    out += "</div>";
		    
		    if(compareToDate(strDate, yyyymmdd(last_date)) >=0 ) {
		    	last_date = writeDate;
		    	$(".chatting-content-list").append(out);
		    } else {
		    	$(".chatting-content-list").prepend(out);
		    }
		}
		
		// 메시지 출력
		if(uid==userId) {
        	out =  "<div class='chatting-content' style='clear: both; margin: 3px 5px;'>";
        	out += " <div style='float: right; background: #ffff00; cursor: pointer;' >"+message+"</div>";
			out += " <div style='float: right; font-size: 10px; margin-right: 3px;'>"+dispTime+"</div>";
			out += " <div style='clear:both; height:3px;'></div>";
			out += "</div>";
			
		} else {
        	out =  "<div class='chatting-content' style='clear: both; margin: 3px 5px;'>";
			out += " <div style='font-size: 10px; margin-bottom: 3px;'>"+nickName+"</div>"
			out += " <div style='display:inline-block; background: #ffffff; cursor: pointer;' >"+message+"</div>";
			out += " <div style='font-size: 10px; display:inline-block;'>"+dispTime+"</div>";
			out += " <div style='clear:both; height:3px;'></div>";
			out += "</div>";
		}
		
		$("."+cls).append(out);
		$('.chatting-content-list').scrollTop($('.chatting-content-list').prop('scrollHeight'));
	}
	
});
</script>

<div class="body-container" style="width: 700px;">
    <div class="body-title">
        <h3><i class="far fa-comment-alt"></i> 채팅 <small style="font-size:65%; font-weight: normal;">Chatting</small></h3>
    </div>
    
    <div style="clear: both;">
       <div class="room-btn-container">
          <div class="chatting-room-btn" data-room="java">자바</div>
          <div class="chatting-room-btn" data-room="spring">스프링</div>
          <div class="chatting-room-btn" data-room="servlet">서블릿</div>
       </div>
       <div class="room-btn-container">
          <div class="chatting-room-btn" data-room="oracle">오라클</div>
          <div class="chatting-room-btn" data-room="web">웹</div>
          <div class="chatting-room-btn" data-room="other">기타</div>
       </div>
    </div>
</div>

<div id="chatting-dialog" style="display: none;">
  <div class="chatting-header">
     <div style="float: left;"><span class="chatting-room-name"></span></div>
     <div class="chatting-msg-more" style="float: right; cursor: pointer;">더보기</div>
  </div>
  <div class="chatting-content-list"></div>
  
  <div style="clear: both; padding-top: 5px;">
     <input type="text" id="chatMsg" class="boxTF" style="width: 100%; box-sizing: border-box;"
            placeholder="채팅 메시지를 입력 하세요...">
  </div>

</div>