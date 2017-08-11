<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object isScheduleReadonly = false;
Object hasImportScheduleAuth = false;
Object hasExportScheduleAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_SCHEDULE)) {
      if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_SCHEDULE) == 1){
        isScheduleReadonly = true;
      }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_SCHEDULE) != null){
      hasImportScheduleAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_SCHEDULE) != null){
      hasExportScheduleAuth = true;
    }
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">    
    <title></title>
    <!-- juqery ui -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
	  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/schedule/scheduleGroupList.css" />
	  
	  <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	  <!-- juqery ui -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/schedule/scheduleGroupList.js"></script>
    <script>
      var isScheduleReadonly = <%=isScheduleReadonly%>;
      var hasImportScheduleAuth = <%=hasImportScheduleAuth%>;
      var hasExportScheduleAuth = <%=hasExportScheduleAuth%>;
  </script>
  </head>
  
  <body>
    <div class="container">
        <%-- <input type="hidden" id="groupFlag" value="${flag}"> --%>
        <div class="public-group rough-info" id="roughGroupInfo"><!-- 分组大概信息 -->
             <ul id="groupRoughList">
             </ul>
         </div>
         <div class="public-group detail-info" id="detailGroupInfo"><!-- 分组详细信息 -->
             <div class="group-toolbar">
                  <input class="add-btn" type="button" title="添加分组" onclick="addGroup()">
                  <input class="delete-btn" type="button" title="删除分组" onclick="deleteGroup()">
                  <input class="import-btn" type="button" title="导入" onclick="importPlan()">
                  <input class="export-btn" type="button" title="导出" onclick="exportPlan()">
                  <!-- <input class="intelligent-arrange" type="button" title="智能整理" onclick="showIntelligent()"> -->
             </div>
             <div class="opera-list">
                <label><input class="select-all" id="selectAll" type="checkbox" onclick="selectAllGroup(this)">全选</label>
                <button class="reverse-select" id="reverseCheck" onclick="selectNoCheck()">反选</button>
                <div class="search-div"><input type="text" id="groupKey" onkeyup="searchKeyGroup(this,event)"><input class="search-btn" type="button" onclick="searchGroup()"></div>
             </div>
             <div class="group-detail-con" id="groupDetailCon">
                <ul class="group-detail-list" id="noneGroupItem">
                    
                </ul>
                <ul class="group-detail-list" id="groupDetailList">
                
                </ul>
             </div>
             
         </div>
    </div>
  </body>
</html>
