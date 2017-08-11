<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object hasExportSeriesTotalAuth = false;
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_SERIES_TOTAL) != null){
	    	hasExportSeriesTotalAuth = true;
	    }
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<link rel="stylesheet" href="<%=path%>/css/exportLoading.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=path%>/css/statistic/seriesnoTotalInfo.css">
	<script type="text/javascript" src="<%=path%>/js/makeplays/statistic/seriesnoTotalInfo.js"></script>
	<script type="text/javascript">
		var hasExportSeriesTotalAuth = <%=hasExportSeriesTotalAuth%>
	</script>
</head>

<body>
	<div class="topDiv">
		<div id="seriesnoDiv"></div>
	</div>
	<div class="totalDiv" id="totalDiv">
		<table id="totalTable" cellpadding="0" cellspacing="0" class="totalTable"></table>
	</div>
	<div id="loadingDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
	</div>
</body>
</html>
