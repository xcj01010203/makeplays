<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
      <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/usercenter/userCrewList.css">
      <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/usercenter/userCrewList.js"></script>
      <script type="text/javascript">
      	var loginUserType=${loginUserType};
      	var currentCrewId='${crewInfo.crewId}';
      </script>
  </head>
  
  <body>
      <div class="my-container" id="crewListContainer">
        <div class="empty-div" id="emptyDiv" style="display: none;">暂无剧组</div>
        <div id="contentDiv" style="display: none;">
        	<div class="tab_wrap">
	        	<ul>
	                <li id="crewTab" class="tab_li_current">有效剧组</li>
	                <li id="expiredCrewTab">过期剧组</li>
	            </ul>
            </div>
            <div class="content_wrap">
            	<div id="crewdiv" class="content-div"></div>
            	<div id="expiredCrewDiv" class="content-div" style="display: none;"></div>
            </div>
        </div>
      </div>
  </body>
</html>
