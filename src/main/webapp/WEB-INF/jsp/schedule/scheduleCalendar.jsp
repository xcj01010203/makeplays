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
	    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
		  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/schedule/scheduleCalendar.css" />
		  <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
		  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
		  <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/schedule/scheduleCalendar.js"></script>
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
	              <li class="tab_li_current" onclick="viewPlanInfo()">查看计划</li>
	              <li onclick="planDetail()">计划详情</li>
	              <li id="editedPlanTab" onclick="editorialPlan()">编辑计划</li>
	          </ul>
        </div>
		    <div class="view-wrap">
			      <div class="toolbar">
			         <input type="button" value="设置关注项" onclick="showActiveWin()">
			      </div>
			      <div class="view-plan-content">
				        <div class="concerns-list" id="concernsList"></div><!--关注列表-->
				        <div class="plan-view-list"><!--计划视图-->
					          <div class="plan-grid" id="planGrid">
						            <div class="grid-header" id="gridHeader">
						                <table class="grid-header-table"  cellpadding="0" cellspacing="0" id="headerTable"></table>
						            </div>
						            <div class="grid-body" id="gridBody">
						                <table class="grid-body-table" cellpadding="0" cellspacing="0" id="bodyTable" ></table>
						            </div>
					          </div>
				        </div>
			      </div> 
			      
			      <!-- 添加关注弹窗 -->
			      <div class="jqx-window" id="addActiveWin">
			         <div>设置关注项</div>
			         <div class="jqx-content">
			         
			             <div class="search-div">
			                 <input class="search-input" id="keySearch" type="text" placeholder="请输入关键字搜索" onkeyup="keySearch(event)">
			                 <input class="search-button" type="button" onclick="searchKeyContent()">
			             </div>
			             <div class="active-item-select" id="activeItemSelect">
			                 <ul>
			                     <li>
                               <p>主要演员:</p>
                               <div class="active-item-opinion" id="shootLocationList"></div>
                           </li>
                           <li>
                               <p>特殊道具:</p>
                               <div class="active-item-opinion" id="specialPropList"></div>
                           </li>
                           <li>
                               <p>主&nbsp;场&nbsp;&nbsp;景:</p>
                               <div class="active-item-opinion" id="mainViewList"></div>
                           </li>
			                 </ul>
			             </div>
			             <p class="jqx-tips">已选择<span class="gray-color">（最多添加10个关注）</span></p>
			             <div class="already-active-wrap">
                       <ul class="already-active-list" id="alreadyActiveList">
                           <li class="active-option" data-color="#2196f3" data-type="" itemid="" onclick="selectActiveOption(this)">
                               <span class="active-option-name">ssss</span>
                               <input class="delete-btn" type="button" value="x" onclick="deleteActiveOption(this)">
                           </li>
                           <!-- <li>
                               <input class="add-active-option" id="addActiveOptionBtn" type="button" title="添加" onclick="addActiveOption(this)">
                           </li> -->
                       </ul>
                   </div>
			             
			             <div class="win-btn-list">
			                 <input type="button" value="确定" onclick="saveActiveContent()">
			                 <input type="button" value="取消" onclick="closeActiveWin()">
			             </div>
			             
			         </div>
			      </div>
			      
		    </div>      
  </body>
</html>
