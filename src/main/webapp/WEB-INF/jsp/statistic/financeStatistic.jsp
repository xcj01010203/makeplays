<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/statistic/financeStatistic.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	<script type="text/javascript" src="<%=path%>/js/report/jqmeter.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/makeplays/statistic/financeStatistic.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/echarts/echarts.js"></script>
</head>

<body>
	<div style="width:100%;height: 100%;overflow: auto;">
		<!-- 内容区 begin -->
		<div id="con_box">
			<div class="bd_wrap">
				<dl class="fold_body">
					<span class="total-span">总费用进度：</span>
					<div id="jqmeter-container" class="total-div"></div>
				</dl>
				<dl class="statis_fold">
					<dt class="statis_fold_head">
						<div class="head_select">
							<span id="subject_label">
							<input type="radio" name="statType" class="radio_select" checked="checked" value="1" onclick="subjectClick(null,'0')" />
							<span class='span_select' id="subjectbase" onclick="subjectClick(this,'0')">预算科目</span>
							</span>
							&nbsp;&nbsp;
							<span>
							<input type="radio" name="statType" class="radio_select" value="2" onclick="selfClick()" />
							<span class='span_select' onclick="selfClick()">自定义科目组</span>
							</span>
							<label id="self_label" class="self_label"></label>
							<span id="selfaddbutton" class="selfsubject_add" title='添加自定义科目组' onclick="showSelfWin()">✚</span>
						</div>
					</dt>
					<div class="div_chart" id="statis_budgetpayed"></div>
					<div class="div_chart" id="statis_budgetpayed2"></div>
					<div class="div_chart" id="statis_daypayed"></div>
					<div class="div_chart" id="statis_totaldaypayed"></div>
				</dl>
			</div>
		</div>
		<!-- 内容区 end -->
		<!-- 右侧滑出框 begin-->
		<div class="right-popup-win" id="rightPopUpWin">
	        <div class="right-popup-body">
	            <div class="header-btn-list">
	            	<div class="export-button" id="exportSubjectDetailBtn" onclick="exportSubjectDetail()" title='导出'></div>
			        <input type="button" class="oper-button" value="关闭" onclick="closeRightWin()">
			    </div>
			    <div class="main-content">
					<div id="subjectDetail"></div>
			     	<div id="subjectTotal" class="totaldiv"></div>
			    </div>
	        </div>
	    </div>
		<!-- 右侧滑出框 end -->
		<!-- 添加窗口 -->
		<div class="jqx-window" id="selfWin">
		    <div>添加/修改自定义科目组</div>
		    <div class="my-jqx-content">
		        <ul class="subj-name-ul">
		            <li>
		                <p><span class="need-content">*</span>名称:</p>
		                <input type="text" id="groupName">
		                <input type="hidden" id="groupId">
		                <input type="hidden" id="subjectId">
		            </li>
		        </ul>
		        <div class="subject-tree-list" id="subjTree"></div>
		        <div class="win-btn-list">
		            <input type="button" value="保存" onclick="saveFinanceAccountGroup()">
		            <input type="button" value="删除" onclick="deleteFinanceAccountGroup()">
		            <input type="button" value="关闭" onclick="closeSelfWin()">
		        </div>
		    </div>
		</div>		
		<div id="loadingDiv" class="show-loading-container" style="display: none;">
			<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
		</div>
	</div>
</body>
</html>
