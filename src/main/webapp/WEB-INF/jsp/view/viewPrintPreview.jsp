<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/exportLoading.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/view/viewPrintPreview.css">
		<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
		<script type="text/javascript">
		  var filter = ${filter};
		</script>
    <script type="text/javascript" src="<%=basePath%>/js/view/viewPrintPreview.js"></script>
</head>
<body>
      <input type="hidden" id="hideColumn" value="${hideColumn}">
      <div class="btn-list">
         <input type="button" class="print-btn" id="printBtn" value="打印" onclick="printViewList()">
      </div>
      <div class="title">
          <h3 id="viewListTitle"></h3>
      </div>
      <!-- 正在加载 -->
		  <div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
		  <div id="loadingDataDiv" class="show-loading-container" style="display: none;">
		      <div class="show-loading-div"> 正在加载数据，请稍候... </div>
		  </div>
      <table class="view-grid" cellspacing = "0" cellpadding = "0" id="viewGrid"></table>
</body>
</html>