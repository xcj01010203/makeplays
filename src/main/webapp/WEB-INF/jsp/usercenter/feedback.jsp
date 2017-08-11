<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/usercenter/feedback.css">
    
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/usercenter/feedback.js"></script>
	<script type="text/javascript">
		var currentUserId = '${user.userId}';
	</script>
  </head>
  
  <body>
    <div class="my-container">
	    <div class="feedback-div">
	        <div class="border-left"></div><span class="title">您的宝贵意见将是我们前进的动力，谢谢！</span>
	        <textarea class="message-area" id="message" rows="5" cols="10" placeholder="请输入您的意见或建议"></textarea>
	        <input class="contact-text" id="contact" type="text" placeholder="请留下手机号和邮箱，方便我们沟通">
	        <input class="submit-btn" type="button" value="提交" onclick="saveFeedBackInfo()">
	    </div>
	    <div class="title-div">
	    	<div class="border-left"></div>
	    	<label class="title-label">以往反馈</label>
	    </div>
	    <div class="list-div" >
	    	
	    </div>
    </div>
  </body>
</html>
