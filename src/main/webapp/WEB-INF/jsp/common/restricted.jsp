<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>访问受限</title>
    <link rel="stylesheet" href="<%=path %>/css/restricted.css"/>
    <script type="text/javascript">
		var minuteNum=3;
   		$(function(){
   			 changeTime();  
   		});

	    function changeTime(){	
		   document.getElementById("noAuth").innerHTML=minuteNum;		
		   minuteNum--;
	    }
	
		var inId=window.setInterval("changeTime()",1000);		
		
	  	function autoRedirect(){		
			if(history.length>1){
				history.go(-1);  
			}else{
				top.window.opener = null;
				top.window.close();
			}		
		}  
		
		window.setInterval("autoRedirect()",3000);
	</script>
</head>


<body>
	<div class="main-div">
		<p class="sorry-p"><img src="<%=path %>/images/warning.png" alt="error"/>抱歉，您没有权限访问该页面</p>
		<p class="minute-p">将在<span id="noAuth" class="minute-span"></span>秒后自动跳转到前一页，若未自动跳转，点此<a href="javascript:autoRedirect();">立即返回</a></p>
	</div>
</body>
</html>