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
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/version/appVersion.css">


<link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
<link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
<script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/version/appVersion.js"></script>
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
                        <li id="searchteam" class="tab_li_current" onclick="showAppVersion()">APP版本管理</li>
                        <li id="searchteam" onclick="showWebVersion()">WEB版本管理</li>
                    </ul>
                </div>                    
            </div>
        </div>
        <!-- 日志列表 -->
        <div class="version-list" id="appVersionGrid"></div>
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
		                    <p><span>*</span>版&nbsp;&nbsp;本&nbsp;号&nbsp;&nbsp;:</p>
		                    <input type="text" id="versionNo" onkeyup="onlyNumber(this)">
		                </li>
		                <li>
		                    <p><span>*</span>版本名称&nbsp;&nbsp;:</p>
		                    <input type="text" id="versionName">
		                    <input type="text" style="display: none;">
		                </li>
		                
		                <li>
		                    <p>&nbsp;&nbsp;更新日志&nbsp;&nbsp;:</p>
		                    <textarea id="updateLog"></textarea>
		                </li>
		                <li>
                        <p><span>*</span>文&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;件&nbsp;&nbsp;:</p>
                        <div class="upload-container">
                            <div class="select-file-btn" id="uploadFileBtn">选择文件</div>
                            <ul class="upload-file-list" id="uploadFileList"></ul>
                        </div>
                        
                    </li>
		              </ul>
		                
		                
		            </div>
		        </div>
		        <div class="ui dimmer body" id="myLoader">
			           <div class="ui large text loader">正在上传文件，请稍后...</div>
			      </div>
      </div>
</body>
</html>