<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" style="text/css" href="<%=basePath%>/css/prepare/prepareMain.css">
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareMain.js"></script>
<title></title>
</head>
<body>
    <div class="main-container">
        <!-- 左部导航 -->
        <div class="left-navigation">
            <ul class="navigation-ul">
                <li class="click"><a href="####" onclick="scriptProgress(this)">剧本进度</a></li>
                <li><a href="####" onclick="selectRole(this)">选角进度</a></li>
                <li><a href="####" onclick="crewPeople(this)">剧组人员</a></li>
                <li><a href="####;" onclick="sceneView(this)">勘景情况</a></li>
                <li><a href="####" onclick="artVertion(this)">美术视觉</a></li>
                <li><a href="####" onclick="extension(this)">宣传进度</a></li>
                <li><a href="####" onclick="officePrepare(this)">办公筹备</a></li>
                <li><a href="####" onclick="commerOperation(this)">商务运营</a></li>
            </ul>
        </div>
        <!-- 正文内容 -->
        <div class="main-content" id="mainContent">
           <div class="load-div" id="scenceDiv"></div>
           <iframe name="mainIframe" id="mainIframe" width="100%" height="100%" ></iframe>
        </div>
    </div>
</body>
</html>