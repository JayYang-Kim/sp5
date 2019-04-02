<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>
<script type="text/javascript">
	function ajaxHTML(url, type, query, id) {
		$.ajax({
			type:type
			,url:url
			,data:query
			,success:function(data) {
				$("#" + id).html(data);
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
</script>

<div class="body-container" style="width: 700px;">
    <div class="body-title">
        <h3><span style="font-family: Webdings">2</span> AJAX-게시판 </h3>
    </div>
    
    <div id="board-body"></div>
</div>