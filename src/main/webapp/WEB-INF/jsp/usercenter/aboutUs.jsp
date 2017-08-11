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
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <link rel="stylesheet" href="<%=basePath%>/css/usercenter/aboutUs.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/scripts/bootstrap.min.js"></script>

  </head>
  
  <body>
	  <div class="container panel panel-default">
	    <div class="content panel-body">
	        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;剧易拍是北京小土科技有限公司（以下简称“小土科技”）为您提供的制片管理软件，通过对剧本进行规范化解析和自动化分析，精确把握剧组各类管理报表，根据剧组各部门汇总的时间档期以及准备工作的情况，按生产计划排期，生成各种计划、通告等生产单据；通过制定剧组预算、合同以及对剧组发生的收款、付款、借款等财务数据的录入，实时对剧组的财务信息进行把控。</p>
	        <p><a href="javascript:void(0)">联系电话：010-56051006</a></p>
	        <p><a href="http://www.trinityearth.com.cn">公司网址：http://www.trinityearth.com.cn</a></p>
	    </div>
	  </div>
  </body>
</html>
