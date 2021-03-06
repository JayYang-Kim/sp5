<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

	<form name="boardForm" method="post" enctype="multipart/form-data">
	  <table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
	  <tr align="left" height="40" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;"> 
	      <td width="100" bgcolor="#eeeeee" style="text-align: center;">제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
	      <td style="padding-left:10px;"> 
	        <input type="text" name="subject" maxlength="100" class="boxTF" style="width: 95%;" value="${dto.subject}">
	      </td>
	  </tr>
	
	  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;"> 
	      <td width="100" bgcolor="#eeeeee" style="text-align: center;">작성자</td>
	      <td style="padding-left:10px;"> 
	          ${sessionScope.member.userName}
	      </td>
	  </tr>
	
	  <tr align="left" style="border-bottom: 1px solid #cccccc;"> 
	      <td width="100" bgcolor="#eeeeee" style="text-align: center; padding-top:5px;" valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
	      <td valign="top" style="padding:5px 0px 5px 10px;"> 
	        <textarea name="content" rows="12" class="boxTA" style="width: 95%;">${dto.content}</textarea>
	      </td>
	  </tr>
	  
	  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;">
	      <td width="100" bgcolor="#eeeeee" style="text-align: center;">첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
	      <td style="padding-left:10px;"> 
	          <input type="file" name="upload" class="boxTF" size="53" style="height: 25px;">
	       </td>
	  </tr>
	  <c:if test="${mode=='update'}">
		  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;">
		      <td width="100" bgcolor="#eeeeee" style="text-align: center;">첨부된파일</td>
		      <td style="padding-left:10px;"> 
		          ${dto.originalFilename}
		          <c:if test="${not empty dto.saveFilename}">
		          		| <span id="deleteBoardFile" style="cursor:pointer;" data-num="${dto.num}">파일삭제</span>
		          </c:if>
		       </td>
		  </tr>
	  </c:if>
	  </table>
	
	  <table style="width: 100%; margin: 0px auto; border-spacing: 0px;">
	     <tr height="45"> 
	      <td align="center" >
	      	<c:if test="${mode=='update'}">
	         	 <input type="hidden" name="num" value="${dto.num}">
	         	 <input type="hidden" name="saveFilename" value="${dto.saveFilename}">
	         	 <input type="hidden" name="originalFilename" value="${dto.originalFilename}">
	        	 <input type="hidden" name="page" value="${page}">
	        </c:if>
	        <button type="button" class="btn" onclick="sendBoard('${mode}');">${mode=='update'?'수정완료':'등록하기'}</button>
	        <button type="reset" class="btn">다시입력</button>
	        <button type="button" class="btn" onclick="listPage(pageNo)">${mode=='update'?'수정취소':'등록취소'}</button>
	      </td>
	    </tr>
	  </table>
	</form>