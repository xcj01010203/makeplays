<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<base href="<%=basePath%>">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" href="<%=basePath%>/css/finance/selectTemplate.css">
	<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/finance/selectBudgetTemplate.js"></script>
</head>
<body>
	<div id="financeGrayBg" class="finance-gray-bg"></div>
	<div id="financeWrap" class="finance-wrap">
		<div class="finance-mode-head">模板</div>
		<div class="finance-mode-cont clearfix">
			<div class="fl finance-department-wrap" onclick="selectDepartTemp()">
				<div class="finance-select-title">按部门</div>
				<div class="finance-department" id="department" ></div>
				<div id="hideDeparmentDiv" class="hide-deparment-div"></div>
				<div class="finance-radio-wrap">
					<input type="radio" name="selectTeam" id="radioDepart" class="finance-select-radio" value="1" />
				</div>
			</div>
			<div class="fl finance-period-wrap" onclick="selectPeriodTemp()">
				<div class="finance-select-title">按周期</div>
				<div class="finance-period" id="production" ></div>
				<div id="hideProductionDiv" class="hide-deparment-div"></div>
				<div class="finance-radio-wrap">
					<input type="radio" name="selectTeam" id="radioPeriod" class="finance-select-radio" value="0"/>
				</div>
			</div>
			<div class="fl finance-history-wrap" onclick="selectHistoryTemp()">
				<div class="finance-select-title">
					<input type="text" id="from-history-select" value="从历史项目导入" class="from-history-select" onclick="historySelectShow()" readonly/>
					<input type="text"  id="hideInput" style="display: none;" />
					<input type="text"  id="" style="display: none;" />
					<span class="down-icon"></span>
				</div>
				<div class="history-temp-wrap" ></div>
				<div class="finance-history" id="financeHistory"></div>
				<div id="hideHistoryDiv" class="hide-history-div"></div>
				<div class="finance-radio-wrap">
					<input type="radio" name="selectTeam" id="radioHistoey" class="finance-select-radio" value="9" />
				</div>
			</div>
		</div>
		<div class="finance-mode-foot">
			<input type="button" id="finance-popup-confirm" class="finance-popup-confirm" value="确    定" onclick="productCycleButton()"/>
			<input type="button" id="noUseTemplate" class="finance-nouse-mode" value="不使用模板" onclick="makeNoTemplate()"/>
		</div>
	</div>
	<div id="financePopupContent" onClick="showCoverWindow();"></div>
	
</body>

</html>
