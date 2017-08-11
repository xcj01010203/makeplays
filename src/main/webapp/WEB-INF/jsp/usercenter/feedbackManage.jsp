<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	
	<link rel="stylesheet" href="<%=path %>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
	<link rel="stylesheet" href="<%=path%>/css/style.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=path %>/css/usercenter/feedbackManage.css">
    
    <script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=path %>/js/jqwidgets/jqxwindow.js"></script> 
	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=path %>/js/usercenter/feedbackManage.js"></script>
	<script type="text/javascript">
		var currentUserId='${user.userId}';
	</script>
  </head>
  
  <body>
    <div class="my-container">
    	<div id="user-div">
		  	<div class="title-div">
		  		<div class="border-left"></div>
			   	<label class="title-label">反馈用户</label>
			   	<div class="oper-div">
			   		<div class="search_button" title="高级查询" onclick="showSearchWin();"></div>
			   	</div>
		  	</div>
		    <div class="content-div" id="userList"></div>
	    </div>
	    <div id="feedback-div" style="display: none;">
	    	<div class="button-div">
		  		<div class="return_button" title="返回" onclick="goback();"></div>
		  	</div>
	    	<div class="content-div" id="feedbackList"></div>
	    </div>
    </div>
    <div id="queryWindow" class="searchWindow" style="display: none;border: none;border-radius:0px;">
		<div>高级查询</div>
		<div class='Popups_box'>
			<ul>
				<li>
					<label>内容：</label>
					<input type="text" class="search-text" id="content" style="width: 355px;">
				</li>
			</ul>
			<ul>
				<li>
					<label>姓名：</label>
					<input type="text" class="search-text" id="userName" style="width: 355px;"/>
				</li>
			</ul>
			<ul>
				<li>
					<label>状态：</label>
					<label><input type="radio" name="searchStatus" value="" checked="checked"/>全部</label>&nbsp;&nbsp; 
					<label><input type="radio" name="searchStatus" value="0"/>未读</label>&nbsp;&nbsp; 
					<label><input type="radio" name="searchStatus" value="1"/>已读</label>
				</li>
			</ul>
			<ul style="width: 100%;">
				<li>
					<label>时间：</label>
					<input type="text" class="Wdate search-text" id="searchStartTime" style="width: 165px;" name="searchStartTime"
						onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',startDate:'%y-%M-%d'})" />至
					<input type="text" class="Wdate search-text" id="searchEndTime" style="width: 165px;" name="searchEndTime"
						onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',startDate:'%y-%M-%d'})" />
				</li>
			</ul>	
			<ul class="oper_ul">
				<li class='claer'>
					<input type='button' class="button" value='查询' id='searchSubmit' onclick="query()"/>&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type='button' class="button" value='关闭' id='closeSearchSubmit' />&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type='button' class="button" value='清空' id='clearSearchButton' onclick="clearSearchCon()"/>
				</li>
			</ul>
		</div>
	</div>
  </body>
</html>
