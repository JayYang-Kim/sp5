<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>spring</title>

<link rel="stylesheet" href="<%=cp%>/resource/css/style.css" type="text/css">
<script type="text/javascript" src="<%=cp%>/resource/jquery/js/jquery-1.12.4.min.js"></script>

<script type="text/javascript">
$(function(){
	$("#btnJsonOk1").click(function(){
		var url="<%=cp%>/user/jsonList1";
		var query="tmp="+new Date().getTime();
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data) {
				printJSON(data);
			}
		    ,error:function(e) {
		    	console.log(e.responseText);
		    }
		});
	});
	
	function printJSON(data) {
		var out="JSON으로 받아오기 1<br>";
		
		var dataCount=data.dataCount;
		out+="개수 : "+dataCount+"<br>";
        $.each(data.list, function(index, item){
            var num=item.num;
            var name=item.name;
            var content=item.content;
            var created=item.created;
            
            out+=num+" : "+name+" : "+content+" : "+created+"<br>";
        });
		
		$("#resultLayout").html(out);
	}
});

$(function(){
	$("#btnJsonOk2").click(function(){
		var url="<%=cp%>/user/jsonList2";
		var query="tmp="+new Date().getTime();
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data) {
				console.log(data);
				printJSON(data);
			}
		    ,error:function(e) {
		    	console.log(e.responseText);
		    }
		});
	});
	
	function printJSON(data) {
		var out="JSON으로 받아오기 2<br>";
		
		// console.log(data);
		var dataCount=data.root.dataCount;
		out+="개수 : "+dataCount+"<br>";
        $.each(data.root.record, function(index, item){
            var num=item.num;
            var name=item.name;
            var content=item.content;
            var created=item.created;
            
            out+=num+" : "+name+" : "+content+" : "+created+"<br>";
        });
		
		
		$("#resultLayout").html(out);
	}
});

$(function(){
	$("#btnData").click(function(){
		var url="<%=cp%>/user/jsonList3";
		var query="tmp="+new Date().getTime();
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data) {
				console.log(data);
				printJSON(data);
			}
		    ,error:function(e) {
		    	console.log(e.responseText);
		    }
		});
	});
	
	function printJSON(data) {
		var out="JSON으로 날씨 가져오기<br>";
		
		// console.log(data);
		/* var dataCount=data.root.dataCount;
		out+="개수 : "+dataCount+"<br>";
        $.each(data.root.record, function(index, item){
            var num=item.num;
            var name=item.name;
            var content=item.content;
            var created=item.created;
            
            out+=num+" : "+name+" : "+content+" : "+created+"<br>";
        }); */
		
		
		$("#resultLayout").html(out);
	}
});

$(function(){
	$("#btnXmlOk").click(function(){
		var url="<%=cp%>/user/xmlList";
		var query="tmp="+new Date().getTime();
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"xml"
			,success:function(data) {
				printXML(data);
			}
		    ,error:function(e) {
		    	console.log(e.responseText);
		    }
		});
	});
	
	function printXML(data) {
		var out="XML로 받아오기<br>";
		
		var dataCount=$(data).find("dataCount").text();
		out+="개수 : "+dataCount+"<br>";
        $(data).find("record").each(function(){
            var record=$(this);
            var num=record.attr("num");
            var name=record.find("name").text();
            var content=record.find("content").text();
            var created=record.find("created").text();
            
            out+=num+" : "+name+" : "+content+" : "+created+"<br>";
        });
		
		$("#resultLayout").html(out);
	}
});
</script>

</head>

<body>

<div class="body-container" style="margin:30px auto; width: 600px;">
    <div class="title">
        <h3>자바 XML 문서</h3>
    </div>
    
     <div style="width: 95%;margin-top: 5px; margin-bottom: 5px;">
     	<button type="button" id="btnJsonOk1" class="btn">JSON으로 받기 1</button>
     	<button type="button" id="btnJsonOk2" class="btn">JSON으로 받기 2</button>
     	<button type="button" id="btnData" class="btn">데이터 받아오기</button>
     	<button type="button" id="btnXmlOk" class="btn">XML로 받기</button>
     </div>

     <div id="resultLayout" style="width: 95%;"></div>
</div>

</body>
</html>