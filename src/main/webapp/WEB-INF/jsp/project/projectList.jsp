<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" href="<%=path%>/css/exportLoading.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=path%>/css/project/projectList.css">
	<script type="text/javascript" src="<%=path%>/js/project/projectList.js"></script>
	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=path%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=path%>/js/jqwidgets/jqxtreegrid.js"></script>
</head>
<body>
	<div class="my-container">
		<div class="maindiv" id="projectListDiv">
			
		</div>
		<div class="right-popup-win" id="rightPopUpWin">
	        <div class="right-popup-body">
	             <div class="header-btn-list">
			         <input type="button" class="oper-button" value="关闭" onclick="closeRightWin()">
			     </div>
			     <div class="btn_tab_wrap">
					<!-- tab键空白处 -->
					<div class="btn_wrap"></div>
					<!-- tab键 -->
				     <div class="tab_wrap">
				         <ul>
				         	<li class="tab_li_current">预算支出</li>
				         	<li>制作进度</li>
				         </ul>
				     </div>
                 </div>
			     <div class="main-content">
			     	<div class="budgetPayed">
			     		<div id="budgetPayedDiv"></div>
			     		<div id="budgetPayedTotal" class="totaldiv"></div>
			     	</div>
			     	<div id="productionSchedule" style="display: none;"></div>
			     </div>
	        </div>
	    </div>
    </div>
    <div id="loadingDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
	</div>
</body>
</html>