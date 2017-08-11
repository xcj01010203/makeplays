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
	
	<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/usercenter/userCenter.css">
	
	<script type="text/javascript" src="<%=basePath %>/js/usercenter/userCenter.js"></script>
	<script type="text/javascript">
		var loginUserType = ${loginUserType};
	</script>
  </head>
  
  <body>
	  <div class="my-container">
	    <input id="activeTagType" type="hidden" value="${activeTagType }">
        <div class="user-center">
		    <div class="float-left user-center-left">
		        <ul>
                    <li><img class="user-img" id="userImg" src="" width="50px" height="50px"></li>
                </ul>
		        <ul>
                    <li><a id="myCrewList" href="javascript:void(0)" onclick="showRightDiv(3)">我的剧组</a></li>
                    <li><a id="myMessage" href="javascript:void(0)" onclick="showRightDiv(2)">我的消息</a>
		            	<span class="unReadNum"></span>
		            </li>
		            <li><a id="userDetail" href="javascript:void(0)" onclick="showRightDiv(1)">个人信息</a></li>
		        </ul>
		        <ul>
		            <li><a id="userAgreement" href="javascript:void(0)" onclick="showRightDiv(4)">用户协议</a></li>
		            <li><a id="feedback" href="javascript:void(0)" onclick="showRightDiv(5)">意见反馈</a></li>
		            <!-- <li><a id="aboutUs" href="javascript:void(0)" onclick="showRightDiv(6)">关于我们</a></li> -->
		        </ul>
		        <ul>
                    <li><a id="joinCrew" href="javascript:void(0)" onclick="showRightDiv(7)">新建/加入剧组</a></li>
		            <li><a href="/logout">退出登录</a></li>
		        </ul>
		    </div>
		    <div class="float-left user-center-right">
		      <iframe width="100%" height="100%" src="" id="rightContentIframe"></iframe>
		      <div id="rightContentDiv"></div>
		    </div>
	    </div>
	  </div>
  </body>
</html>
