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
	<meta name="viewport" content="width=device-width,initial-scale=1">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/statistic/dayShootStatistic.css">
	<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/echarts/echarts.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/makeplays/statistic/dayShootStatistic.js?version=20170807"></script>
  
  
  </head>
  
  <body>
    <input type="hidden" id="crewId" value="${crewId }">
    <div class="change-btn-list">
       <!-- <label><input type="radio" name="totalRadio" value="1" checked onclick="showTotalEchart(1)">场 </label>&nbsp;&nbsp;
       <label><input type="radio" name="totalRadio" value="2" onclick="showTotalEchart(2)">页</label> -->
       <div class="left-btn-group">
          <button class="blue" value="1" onclick="showTotalEchart(this, 1)">场</button>
          <button class="gray" value="2" onclick="showTotalEchart(this, 1)">页</button>
       </div>
       <div class="right-btn-group">
          <button class="blue" value="1" onclick="showTotalEchart(this, 2)">周</button>
          <button class="gray" value="2" onclick="showTotalEchart(this, 2)">日</button>
       </div>
    </div>
    <div class="echart-container">
        <div id="echartDiv" style="width: 100%; height:calc(100% - 10px);"></div>
    </div>
    
  </body>
</html>
