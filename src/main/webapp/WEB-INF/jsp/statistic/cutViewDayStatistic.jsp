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
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/statistic/cutViewDayStatistic.css">
	<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/echarts/echarts.js"></script>
  	<script type="text/javascript" src="<%=basePath%>/js/makeplays/statistic/cutViewDayStatistic.js"></script>
  
  </head>
  
  <body>
    <input type="hidden" id="crewId" value="${crewId }">
    <input type="hidden" id="userId" value="${userId }">
    <div class="echart-container">
        <div id="echartDiv" style="width: 100%; height:calc(100% - 10px);"></div>
    </div>    
  </body>
</html>
