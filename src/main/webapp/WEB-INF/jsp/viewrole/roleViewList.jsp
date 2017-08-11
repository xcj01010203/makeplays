<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.xiaotu.makeplays.utils.Constants"%>
<%

Object hasExportRoleAuth = false;

Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_VIEWROLE)){
    	hasExportRoleAuth = true;
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />

<!-- bootstrap CSS -->

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/dist/makeplays.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/report/base.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/viewList.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Constants.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HashMap.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/viewrole/roleViewList.js"></script>
<script>
var hasExportRoleAuth = <%=hasExportRoleAuth%>;
</script>
</head>
<body>
  <input type="hidden" name="roles" id="roleId" value="${roleId}">
  <input type="hidden" name="viewRoleName" id="roleName" value="${viewRoleName}">
  <!-- 表格加载div -->
  <div style="width: 100%">
      <div class="statistics-data-div" id="statistics">
        </div>
      <div class="title back_1" id="rendertoolbar" > 
      </div>
      <!-- 表格 -->
    <div id="jqxgrid" class="t_i">
    </div>
        
  </div>
  <!-- 表格加载div结束 -->

<!-- 显示正在加载中 -->
<div id="loadingDiv" class="show-loading-container" style="display: none;">
  <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
</div>
</body>
</html>