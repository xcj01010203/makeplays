<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
	SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Date currentTime = new java.util.Date();//得到当前系统时间
	String str_date1 = formatter.format(currentTime); //将日期时间格式化 
	String crewFilter = request.getParameter("crewFilter");
	if(crewFilter == null) {
		crewFilter="";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/easy-ui/easyui.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/easy-ui/icon.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/webuploader.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
	<!-- bootstrap JS -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/webuploader/webuploader.min.js"></script>
    <!-- 实现表格的拖动 -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/datagrid-dnd.js"></script>
	
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/crew/crewManage.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/crew/crewManage.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
	<script type="text/javascript">
		var today = "<%=str_date1%>";

		var crewFilter = {};
		if('<%=crewFilter%>' && '<%=crewFilter%>' != 'null') {
			crewFilter=eval('(<%=crewFilter%>)');
		}		
	</script>
</head>
<body>
	<!-- 剧组管理列表 begin -->
	
	<div class="box_wrap">
		<div class="operate_box">
        	<div class="icon_jia" onclick="showAddCrewWin()" title='添加剧组'></div>
        	<div class="icon_sort" onclick="showSelectSortWin()" title='排序'></div>
		</div>
		
		<div id="jqxgrid" class="table-div">
		<table class="crew-table" cellspacing=0 cellpadding=0>
            <tr id="firstTr" class="crew-table-th">
              <th class="th-crewtype">剧组类型
                  <select class="selectpicker crewtype-select" id="crewtypeSelect"  multiple data-live-search="true" style="display: none;">
                  	<option value='1'>电视剧</option>
                  	<option value='0'>电影</option>
                  	<option value='2'>网剧</option>
                  	<option value='3'>网大</option>
                  </select>
                  <a class="clearSelection" onclick="clearSelection(this)">[清空]</a> 
              </th>
              <th class="th-crewname">剧组名称
              	<div class="select-main-div">
              	<div class="btn-div" onclick="showDropdown(this,event)"><span class="filter-option pull-left"></span>&nbsp;<span class="caret"></span></div>
                  <ul class="dropdown_box" id="crewnameDrop">
                  <li class="coll-first-li">
                  <input type="text" class="search-text" name="crewnameSelect" id="crewnameSelect"></li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchContent()">
                      <!-- <input type="button" value="清空" onclick="clearContent('crewname')"> -->
                    </div>
                  </li>
                </ul>                
                </div>
                  <a class="clearSelection" onclick="clearContent('crewname')">[清空]</a>
              </th>
              <th class="th-date">有效期                
              	<div class="select-main-div">
              	<div class="btn-div" onclick="showDropdown(this,event)"><span class="filter-option pull-left"></span>&nbsp;<span class="caret"></span></div>
                  <ul class="dropdown_box box_date" id="dateDrop">
                  <li class="coll-first-li">
                    <input type="text" class="text-input" name="startDateSelect" style="width: 102px;height: 30px;" id="startDateSelect" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" /><span style="margin-left: 10px;">-</span>
                   <input type="text" class="text-input" name="endDateSelect" style="width: 102px;height: 30px;margin-left: 10px;" id="endDateSelect" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" />
                  </li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchContent()">
                      <!-- <input type="button" value="清空" onclick="clearContent('date')"> -->
                    </div>
                  </li>
                </ul>
                </div>
                  <a class="clearSelection" onclick="clearContent('date')">[清空]</a>
              </th>
              <th class="th-shootdate">拍摄期              	
              	<div class="select-main-div">
              	<div class="btn-div" onclick="showDropdown(this,event)"><span class="filter-option pull-left"></span>&nbsp;<span class="caret"></span></div>
                  <ul class="dropdown_box box_date" id="shootdateDrop">
                  <li class="coll-first-li">
                    <input type="text" class="text-input" name="shootStartDateSelect" style="width: 102px;height: 30px;" id="shootStartDateSelect" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" /><span style="margin-left: 10px;">-</span>
                   <input type="text" class="text-input" name="shootEndDateSelect" style="width: 102px;height: 30px;margin-left: 10px;" id="shootEndDateSelect" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" />
                  </li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchContent()">
                      <!-- <input type="button" value="清空" onclick="clearContent('shootdate')"> -->
                    </div>
                  </li>
                </ul>
                </div>
                  <a class="clearSelection" onclick="clearContent('shootdate')">[清空]</a>
              </th>
              <th class="th-company">制片公司              
              	<div class="select-main-div">
              	<div class="btn-div" onclick="showDropdown(this,event)"><span class="filter-option pull-left"></span>&nbsp;<span class="caret"></span></div>
                  <ul class="dropdown_box" id="companyDrop">
                  <li class="coll-first-li">
                  <input type="text" class="search-text" name="companySelect" id="companySelect"></li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchContent()">
                      <!-- <input type="button" value="清空" onclick="clearContent('company')"> -->
                    </div>
                  </li>
                </ul>
                </div>
                  <a class="clearSelection" onclick="clearContent('company')">[清空]</a>
              </th>
              <th class="th-director">导演
              	<div class="select-main-div">
              	<div class="btn-div" onclick="showDropdown(this,event)"><span class="filter-option pull-left"></span>&nbsp;<span class="caret"></span></div>
                  <ul class="dropdown_box" id="directorDrop">
                  <li class="coll-first-li">
                  <input type="text" class="search-text" name="directorSelect" id="directorSelect"></li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchContent()">
                      <!-- <input type="button" value="清空" onclick="clearContent('director')"> -->
                    </div>
                  </li>
                </ul>
                </div>
                  <a class="clearSelection" onclick="clearContent('director')">[清空]</a>
              </th>
              <th class="th-projecttype">项目类型
                  <select class="selectpicker special-actor-select" id="projecttypeSelect"  multiple data-live-search="true" style="display: none;">
                  	<option value='0'>普通项目</option>
                  	<option value='1'>试用项目</option>
                  	<option value='2'>内部项目</option>
                  </select>
                  <a class="clearSelection" onclick="clearSelection(this)">[清空]</a>
              </th>
              <th class="th-shootprogress">拍摄进度</th>
               <th class="th-costprogress">费用进度</th>	
               <th class="th-outofdate">
               	<select class="sel" id="outofdate" onchange="loadCrewList()">
               		<option value="0">全部</option>
               		<option value="1">有效</option>
               		<option value="2">已过期</option>
               		<option value="3">已停用</option>
               	</select>
               </th>	
            </tr>
         	</table>
		</div>
	</div>
	<!-- end剧组管理列表 -->
	<!-- 创建剧组  -->
	<div id='addCrewDiv' style='display:none;'>
        <div>创建剧组</div>
        <div>
            <iframe frameborder="0" scrolling="yes" width="100%" height="100%" src=""></iframe>
        </div>
    </div>
	<!-- 创建剧组 end  -->
	<!-- 排序 -->
	<div id="sortdiv" style="display: none;">
		<div>选择排序字段</div>
		<input type="button" value="确定" onclick="sortSure()" class="surebutton">
		<div>
		<table id="sortGrid" style="width:100%;"></table>
		</div>
	</div>
	<!-- 排序end --> 
	<div id="loadingDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> Loading... </div>
	</div>
	
	<!-- 右侧滑动窗口 -->
	<div class="right-popup-win" id="rightPopUpWin">
       <div class="win-btn-list">
           <input class="close-btn" type="button" value="关闭" onclick="closeRightPopupWin()">
       </div>
       <div class="crew-content" id="crewContentDiv">
            <!-- <iframe id="crewContentIframe" width="100%" height="100%"></iframe> --> 
           <!-- <div id="crewContent"></div> -->
       </div>
   </div>  
	
</body>
</html>