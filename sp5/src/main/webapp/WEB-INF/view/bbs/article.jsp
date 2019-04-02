<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<script type="text/javascript">
function deleteBoard(num) {
<c:if test="${sessionScope.member.userId=='admin' || sessionScope.member.userId==dto.userId}">	
	if(confirm("게시물을 삭제 하시겠습니까 ?")) {
		var url="<%=cp%>/bbs/delete?num="+num+"&${query}";
		location.href=url;
	}
</c:if>
<c:if test="${sessionScope.member.userId != 'admin' && sessionScope.member.userId != dto.userId}">	
  alert("게시글을 삭제할 수 없습니다.");
</c:if>
}

function updateBoard(num) {
<c:if test="${sessionScope.member.userId == dto.userId}">	
   var url="<%=cp%>/bbs/update?num="+num+"&page=${page}";
   location.href=url;
</c:if>
<c:if test="${sessionScope.member.userId != dto.userId}">	
  alert("게시글을 수정할 수 없습니다.");
</c:if>
}
</script>

<script type="text/javascript">
$(function(){
	listPage(1);
});

function listPage(page) {
	var query="num=${dto.num}&pageNo="+page;
	var url="<%=cp%>/bbs/listReply";
	
	$.ajax({
		type:"get"
		,url:url
		,data:query
		,success:function(data) {
			$("#listReply").html(data);
		}
	    ,beforeSend:function(e) {
	    	e.setRequestHeader("AJAX", true);
	    }
	    ,error:function(e) {
	    	if(e.status==403) {
	    		location.href="<%=cp%>/member/login";
	    		return;
	    	}
	    	console.log(e.responseText);
	    }
	});
	
}

$(function(){
	// 댓글 추가
	$(".btnSendReply").click(function() {
		var num = "${dto.num}";
		var $tb = $(this).closest("table");
		var content = $tb.find("textarea").val().trim();
		if(! content) {
			$tb.find("textarea").focus();
			return;
		}
		content = encodeURIComponent(content);
		
		var query = "num="+num+"&content="+content+"&answer=0";
		var url = "<%=cp%>/bbs/insertReply";
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data) {
				$tb.find("textarea").val("");
				listPage(1);
			}
		    ,beforeSend:function(e) {
		    	e.setRequestHeader("AJAX", true);
		    }
		    ,error:function(e) {
		    	if(e.status==403) {
		    		location.href="<%=cp%>/member/login";
		    		return;
		    	}
		    	console.log(e.responseText);
		    }
		});
		
	});
});

$(function(){
	// 답글 버튼
	$("body").on("click", ".btnReplyAnswerLayout", function(){
		var $replyAnswer=$(this).closest("tr").next();
		
		var isVisible = $replyAnswer.is(":visible");
		var replyNum = $(this).attr("data-replyNum");
		
		if(isVisible) {
			$replyAnswer.hide();
		} else {
			$replyAnswer.show();
			
			listReplyAnswer(replyNum);
		}
		
	});
});

// 댓글별 답글 리스트
function listReplyAnswer(answer) {
	var url="<%=cp%>/bbs/listReplyAnswer";
	
	$.ajax({
		type:"get"
		,url:url
		,data:{answer:answer}
		,success:function(data) {
			$("#listReplyAnswer"+answer).html(data);
			
			countReplyAnswer(answer);
		}
	    ,beforeSend:function(e) {
	    	e.setRequestHeader("AJAX", true);
	    }
	    ,error:function(e) {
	    	if(e.status==403) {
	    		location.href="<%=cp%>/member/login";
	    		return;
	    	}
	    	console.log(e.responseText);
	    }
	});
}

// 댓글별 답글 개수
function countReplyAnswer(answer) {
	var url="<%=cp%>/bbs/countReplyAnswer";
	
	$.ajax({
		type:"get"
		,url:url
		,data:{answer:answer}
		,dataType:"json"
		,success:function(data) {
			var count=data.count;
			$("#answerCount"+answer).html(count);
		}
	    ,beforeSend:function(e) {
	    	e.setRequestHeader("AJAX", true);
	    }
	    ,error:function(e) {
	    	if(e.status==403) {
	    		location.href="<%=cp%>/member/login";
	    		return;
	    	}
	    	console.log(e.responseText);
	    }
	});
}

// 답글 등록 버튼
$(function(){
	$("body").on("click", ".btnSendReplyAnswer", function(){
		var num="${dto.num}";
		var replyNum=$(this).attr("data-replyNum");
		var $td = $(this).closest("td");
		var content=$td.find("textarea").val().trim();
		if(! content) {
			$td.find("textarea").focus();
			return;
		}
		content=encodeURIComponent(content);
		
		var query="num="+num+"&content="+content+"&answer="+replyNum;
		var url="<%=cp%>/bbs/insertReply";
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data) {
				$td.find("textarea").val("");
				
				listReplyAnswer(replyNum);
			}
		    ,beforeSend:function(e) {
		    	e.setRequestHeader("AJAX", true);
		    }
		    ,error:function(e) {
		    	if(e.status==403) {
		    		location.href="<%=cp%>/member/login";
		    		return;
		    	}
		    	console.log(e.responseText);
		    }
		});
		
	});

});
</script>

<script type="text/javascript">
	$(function(){
		// 게시글 좋아요
		$(".btnSendBoardLike").click(function(){
			var url = "<%=cp%>/bbs/boardLike";
			var num = "${dto.num}";
			
			$.ajax({
				type:"post"
				,url:url
				,data:{num:num}
				,dataType:"json"
				,success:function(data) {
					if(data.state == "false") {
						alert("게시글 좋아요는 한 번만 가능합니다.");
					} else {
						$("#boardLikeCount").html(data.count);
					}
				}
			    ,beforeSend:function(e) {
			    	e.setRequestHeader("AJAX", true);
			    }
			    ,error:function(e) {
			    	if(e.status==403) {
			    		location.href="<%=cp%>/member/login";
			    		return;
			    	}
			    	console.log(e.responseText);
			    }
			});
		});
	});
	
	$(function(){
		// 게시글 좋아요 싫어요 등록 및 개수 카운트
		$("body").on("click", ".btnSendReplyLike", function(){
			var replyNum = $(this).attr("data-replyNum");
			var replyLike = $(this).attr("data-replyLike");
			var $btn = $(this);
			
			var msg = "게시물이 마음에 들지 않습니까?";
			if(replyLike) {
				msg = "게시물에 공감하십니까?";
			}
			
			if(!confirm(msg)) {
				return;
			}
			
			var url = "<%=cp%>/bbs/insertReplyLike";
			var query = "replyNum=" + replyNum + "&replyLike=" + replyLike;
			
			$.ajax({
				type:"post"
				,url:url
				,data:query
				,dataType:"json"
				,success:function(data) {
					if(data.state == "false") {
						alert("좋아요 / 싫어요는 한 번만 등록가능합니다.");
						return;
					}
					
					var likeCount = data.likeCount;
					var disLikeCount = data.disLikeCount;
					
					$btn.parent("td").children().eq(0).find("span").html(likeCount);
					$btn.parent("td").children().eq(1).find("span").html(disLikeCount);
				}
			    ,beforeSend:function(e) {
			    	e.setRequestHeader("AJAX", true);
			    }
			    ,error:function(e) {
			    	if(e.status==403) {
			    		location.href="<%=cp%>/member/login";
			    		return;
			    	}
			    	console.log(e.responseText);
			    }
			});
		});
	});
	
	$(function(){
		$("body").on("click", ".deleteReply", function(){
			if(!confirm("댓글을 삭제하시겠습니까?")) {
				return;
			}
			
			var replyNum = $(this).attr("data-replyNum");
			var page = $(this).attr("data-pageNo");
			
			var url = "<%=cp%>/bbs/deleteReply";
			var query = "replyNum=" + replyNum + "&mode=reply";
			
			$.ajax({
				type:"post"
				,url:url
				,data:query
				,dataType:"json"
				,success:function(data) {
					listPage(page);
				}
			    ,beforeSend:function(e) {
			    	e.setRequestHeader("AJAX", true);
			    }
			    ,error:function(e) {
			    	if(e.status==403) {
			    		location.href="<%=cp%>/member/login";
			    		return;
			    	}
			    	console.log(e.responseText);
			    }
			});
		});
	});
	
	$(function(){
		$("body").on("click", ".deleteReplyAnswer", function(){
			if(!confirm("답글을 삭제하시겠습니까?")) {
				return;
			}
			
			var replyNum = $(this).attr("data-replyNum");
			var answer = $(this).attr("data-answer");
			
			var url = "<%=cp%>/bbs/deleteReply";
			var query = "replyNum=" + replyNum + "&mode=answer";
			
			$.ajax({
				type:"post"
				,url:url
				,data:query
				,dataType:"json"
				,success:function(data) {
					listReplyAnswer(answer);
				}
			    ,beforeSend:function(e) {
			    	e.setRequestHeader("AJAX", true);
			    }
			    ,error:function(e) {
			    	if(e.status==403) {
			    		location.href="<%=cp%>/member/login";
			    		return;
			    	}
			    	console.log(e.responseText);
			    }
			});
		});
	});
</script>


<div class="body-container" style="width: 700px;">
    <div class="body-title">
        <h3><span style="font-family: Webdings">2</span> 게시판 </h3>
    </div>
    
    <div>
			<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
			<tr height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="center">
				   ${dto.subject}
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td width="50%" align="left" style="padding-left: 5px;">
			       이름 : ${dto.userName}
			    </td>
			    <td width="50%" align="right" style="padding-right: 5px;">
			        ${dto.created} | 조회 ${dto.hitCount}
			    </td>
			</tr>
			
			<tr>
			  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="200">
			      ${dto.content}
			   </td>
			</tr>
			
			<tr style="border-bottom: 1px solid #cccccc;">
			  <td colspan="2" align="center" style="padding-bottom: 15px;" height="40">
			      <button type="button" class="btn btnSendBoardLike"><span style="font-family: Wingdings;">C</span>&nbsp;&nbsp;<span id="boardLikeCount">${dto.boardLikeCount}</span></button>
			   </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       첨&nbsp;&nbsp;부 :
		         <c:if test="${not empty dto.saveFilename}"><a href="<%=cp%>/bbs/download?num=${dto.num}">${dto.originalFilename}</a>(<fmt:formatNumber value="${dto.filesize/1024}" pattern="#,##0.00"/> Kb)</c:if>   
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       이전글 :
                 <c:if test="${not empty preReadDto}">
                   <a href="<%=cp%>/bbs/article?num=${preReadDto.num}&${query}">${preReadDto.subject}</a>
                 </c:if>
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       다음글 :
                 <c:if test="${not empty nextReadDto}">
                   <a href="<%=cp%>/bbs/article?num=${nextReadDto.num}&${query}">${nextReadDto.subject}</a>
                 </c:if>
			    </td>
			</tr>
			</table>
			
			<table style="width: 100%; margin: 0px auto 20px; border-spacing: 0px;">
			<tr height="45">
			    <td width="300" align="left">
			          <button type="button" class="btn" onclick="updateBoard('${dto.num}')">수정</button>
			          <button type="button" class="btn" onclick="deleteBoard('${dto.num}');">삭제</button>
			    </td>
			
			    <td align="right">
			        <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/bbs/list?${query}';">리스트</button>
			    </td>
			</tr>
			</table>
    </div>
    
    <div>
        <table style='width: 100%; margin: 15px auto 0px; border-spacing: 0px;'>
        <tr height='30'> 
         <td align='left'>
         	<span style='font-weight: bold;' >댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가 주세요.</span>
         </td>
        </tr>
        <tr>
           <td style='padding:5px 5px 0px;'>
                <textarea class='boxTA' style='width:99%; height: 70px;'></textarea>
            </td>
        </tr>
        <tr>
           <td align='right'>
                <button type='button' class='btn btnSendReply' style='padding:10px 20px;' >댓글 등록</button>
            </td>
        </tr>
        </table>
        
        <div id="listReply"></div>
    </div>
    
</div>