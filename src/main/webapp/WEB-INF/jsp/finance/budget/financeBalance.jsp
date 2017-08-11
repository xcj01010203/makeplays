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
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  <link rel="stylesheet" type="text/css" href="<%=basePath%>css/finance/financeBudgeList.css">
  <link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css">
  <script type="text/javascript" src="<%=basePath%>/js/finance/financeBalanceList.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
  </head>
  
  <body>
    <div class="my-container">
        <div id="financeBalanceList"></div>
        <div class="table-finance-total-list"></div>
        
         <!-- 显示加载中 -->
        <div id="loadingDiv" class="show-loading-container">
            <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
        </div>
    </div>
  </body>
</html>
