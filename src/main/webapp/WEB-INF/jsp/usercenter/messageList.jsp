<%@page import="com.xiaotu.makeplays.message.model.constants.MessageType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	
	
	<script type="text/javascript" src="<%=basePath %>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/base.js"></script>
	<script type="text/javascript" src='<%=basePath %>/js/usercenter/messageList.js'></script>
	
	<link rel="stylesheet" href="<%=basePath %>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
	<link rel="stylesheet" href="<%=path%>/css/style.css" type="text/css" />
    <link type="text/css" href="<%=basePath %>/css/usercenter/messageList.css" rel="stylesheet" />
	
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxdata.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxgrid.js"></script>
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxgrid.sort.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxgrid.pager.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxgrid.selection.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxwindow.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxtooltip.js"></script> 
    <script type="text/javascript" src="<%=basePath %>/js/jqwidgets/jqxcheckbox.js"></script> 
	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=path%>/js/scripts/json2.js"></script>
</head>
<body>
	<div style="height: 100%;">
	    <div class="tag-div">
	      <a class="active" href="javascript:void(0)" onclick="selectTag(this, 0)">消息提醒</a>
	      <!-- 暂时隐藏 -->
	      <span id="bullentInfoTag" style="display: none;">|<a href="javascript:void(0)" onclick="selectTag(this, 1)">剧组公告</a></span>
	    </div>
	    <div id="messageInfoTable" class="content-div"></div>
	    <div id="bulletinInfoTable" class="content-div"></div>	    
		<div style="display: none;border: none;border-radius:0px;" id="messageDetailWindow">
			<div>查看消息</div>
			<div id="contentBox" class="contentBox">
				<div class="topdiv">
				</div>
				<div class="bottomdiv">
					<input type="button" class="button" id="closeButton" value="关闭" onclick="closeMessageDetailWindow()">
				</div>
			</div>
		</div>
		<div id="queryWindow" class="searchWindow" style="display: none;border: none;border-radius:0px;">
			<div>高级查询</div>
			<div id="dropDownDIV" class='Popups_box'>	
				<ul>
					<li>
						<label>标题(内容)：</label>
						<input type="text" class="search-class" id="searchMessageContent" style="width: 355px;" name="searchMessageContent" />
					</li>
				</ul>
				<ul>
					<li>
						<label>状态：</label>
						<label><input type="radio" name="searchStatus" value=""/>全部</label>&nbsp;&nbsp; 
						<label><input type="radio" name="searchStatus" value="0"/>未读</label>&nbsp;&nbsp; 
						<label><input type="radio" name="searchStatus" value="1"/>已读</label>
					</li>
				</ul>
				<ul style="width: 100%;">
					<li>
						<label>时间：</label> 
						<input type="text" class="Wdate search-class" id="searchstartTime" style="width: 165px;" name="searchstartTime"
							onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 00:00:00'})" />至
						<input type="text" class="Wdate search-class" id="searchendTime" style="width: 165px;" name="searchendTime"
							onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 23:59:59'})" />
					</li>
				</ul>	
				<ul class="oper_ul">
					<li class='claer'>
						<input type='button' class="button" value='查询' id='searchSubmit' onclick="queryMessage()"/>&nbsp;&nbsp;&nbsp;&nbsp; 
						<input type='button' class="button" value='关闭' id='closeSearchSubmit' />&nbsp;&nbsp;&nbsp;&nbsp; 
						<input type='button' class="button" value='清空' id='clearSearchButton' onclick="clearSearchCon()"/>
					</li>
				</ul>
			</div>
		</div>
	</div>
</body>
