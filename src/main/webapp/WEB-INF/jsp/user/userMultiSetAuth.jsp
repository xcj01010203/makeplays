<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<link rel="stylesheet" href="<%=path%>/css/user/userMultiSetAuth.css" type="text/css" />	
	<script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/user/userMultiSetAuth.js"></script>
	<script>
		var userIds = "${userIds}";
	</script>
</head>

<body>
	<div class="main-div">
		<div class="title-tab">
			<p class="selected" onclick="siwtchPlatform(1, this)">PC端权限</p>
			<p onclick="siwtchPlatform(2, this)">APP端权限</p>
			<p style="margin-left: 25px;font-size: 13px;width: 220px;color: #999;">图例：<label class="single-auth-sample">全没有</label><label class="single-auth-sample selected">全有</label><label class="single-auth-sample selected-part">部分有</label></p>
		</div>
		<div class="auth-info">
			<!-- PC端权限 -->
			<div id="pcAuthList" class="pc-auth-list"></div>

			<!-- APP端权限 -->
			<div id="appAuthList" class="app-auth-list"></div>
		</div>
	</div>
</body>
</html>
