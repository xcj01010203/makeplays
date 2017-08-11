<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/version/webVersion.css?version=20170710">
<script type="text/javascript" src="<%=basePath %>/js/version/webVersion.js"></script>
</head>
<body>
    <div class="my-container">
    	<div class="tab-body-wrap">
            <!-- tab键容器 -->
            <div class="btn_tab_wrap">
                <!-- tab键空白处 -->
                <div class="btn_wrap"></div>
                <!-- tab键 -->
                <div class="tab_wrap">
                    <ul>
                        <li id="searchteam" onclick="showAppVersion()">APP版本管理</li>
                        <li id="searchteam" class="tab_li_current" onclick="showWebVersion()">WEB版本管理</li>
                    </ul>
                </div>                    
            </div>
        </div>
        <!-- 日志列表 -->
        <div class="version-list" id="webVersionGrid"></div>
        <!-- 滑动窗口 -->
        <div class="right-popup-win" id="rightPopUpWin">
            <input type="hidden" id="versionId">
            <div class="win-btn-list">
              <p class="win-title" id="headerTitle">新增版本信息</p>
	          <input type="button" value="确定" onclick="saveVersion()">
	          <input type="button" value="关闭" onclick="closePopUpWin()">
	        </div>
	        <div class="win-content">
	            <ul class="version-info">
	                <li>
	                    <p><span>*</span>版本名称&nbsp;&nbsp;:</p>
	                    <input type="text" id="versionName">
	                    <input type="text" style="display: none;">
	                </li>		                
	                <li>
	                    <p><span>*</span>内部更新日志&nbsp;&nbsp;:</p>
	                    <textarea id="insideUpdateLog" class="inner"></textarea>
	                </li>		                
	                <li>
	                    <p><span>*</span>用户更新日志&nbsp;&nbsp;:</p>
	                    <textarea id="userUpdateLog"></textarea>
	                </li>
	              </ul>	                
	            </div>
	        </div>
      </div>
</body>
</html>