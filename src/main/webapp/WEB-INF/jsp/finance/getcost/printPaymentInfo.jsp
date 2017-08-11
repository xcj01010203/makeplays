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
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/paymentPrint.css">
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/paymentPrint.js"></script>
    
  </head>
  
  <body>
	  <div class="my-container" id="myContainer">
	       <input type="hidden" id="paymentIds" value="${paymentIds}">
           <input type="hidden" id="needClosePage" value="${needClosePage}">
	       <input type="hidden" id="needBacktoPage" value="${needBacktoPage}">
	  </div>
  </body>
</html>
