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
     <link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css">
	   <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/schedule/scheduleDetail.css" />
	   <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	   <script type="text/javascript" src="<%=basePath%>/js/dist/makeplays.js"></script>
	   <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
     <script type="text/javascript" src="<%=basePath%>/js/schedule/scheduleDetail.js"></script>
     <script>
          var isScheduleReadonly = <%=isScheduleReadonly%>;
          var hasImportScheduleAuth = <%=hasImportScheduleAuth%>;
          var hasExportScheduleAuth = <%=hasExportScheduleAuth%>;
      </script>
  </head>
  
  <body>
       <!-- tab键 -->
        <div class="tab-wrap">
            <ul class="tab-btn">
                <li onclick="viewPlan()">查看计划</li>
                <li class="tab_li_current" onclick="queryPlanDetail()">计划详情</li>
                <li id="editedPlanTab" onclick="editorialPlan()">编辑计划</li>
            </ul>
        </div>
        
        <div class="toolbar">
            <input type="button" class="lenged-btn">
            <input type="button" title="导出" class="export-btn" onclick="exportPlanDetail()">
        </div>
         
        <!-- 计划详细信息列表 -->
        <div class="plan-detail-grid" id="planDetailGrid">
            
        </div> 
        <!-- 加载中 -->  
        <div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;">
			  </div>
			  <div id="loadingDataDiv" class="show-loading-container" style="display: none;">
			    <div class="show-loading-div"> 正在加载数据，请稍候... </div>
			  </div>
			  
			  <!-- 回到顶部 -->
			  <div class="back-container" id="gotoTop" onclick="gotoTop()" title="回到顶部">
			     <canvas id="backImg" width=58 height=58 style="border:0px"></canvas>
			  </div>
			  
			  
  </body>
</html>
