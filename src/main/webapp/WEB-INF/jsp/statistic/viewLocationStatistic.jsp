<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object hasExportViewTotalAuth = false;
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_VIEW_TOTAL) != null){
	    	hasExportViewTotalAuth = true;
	    }
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<!-- bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	<!-- bootstrap JS -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/statistic/viewLocationStatistic.css">
	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=path%>/js/makeplays/statistic/viewLocationStatistic.js"></script>
	<script type="text/javascript">
		var hasExportViewTotalAuth = <%=hasExportViewTotalAuth%>;
		var crewType=${crewInfo.crewType};
	</script>
</head>

<body>
	<div class="maindiv" >		
		<div class="operate_box">
	      	<div class="search_button" onclick="showSearchWin()" title='搜索'></div>
	      	<div class="export_button" onclick="exportExcel()" title='导出'></div>
	      	<div style="display: inline-block;" id="dropdowndiv">
		      	<div class="dropdown_button" onclick="toggletd(event)" title='显示/隐藏列'></div>
		      	<ul id="togglecolumn" class="togglecolumnul"></ul>
	      	</div>
		</div>
	    <div class="table_head" id="table_head">
	      <table class="maintable headtable" cellpadding="0" cellspacing="0">
	        <thead id="maintable_thead"></thead>
	      </table>
	    </div>
	    <div class="table_body" id="table_body" onscroll="bodyScroll()">
	      <table class="maintable" id="mainTableBody" cellpadding="0" cellspacing="0">
	        <tbody id="maintable_tbody"></tbody>
	      </table>
	    </div>
		<div class="bottom-div" id="total_info"></div>
		<!-- 右侧滑出框 begin-->
		<div class="right-popup-win" id="rightPopUpWin">
	        <div class="right-popup-body">
	            <div class="header-btn-list">
	            	<div class="export-button" id="exportSubjectDetailBtn" onclick="exportViewList()" title='导出'></div>
			        <input type="button" class="oper-button" value="关闭" onclick="closeRightWin()">
			        <input type="button" style="float:right;margin-top: 5px;" value="" id="viewColorExample" class="colorExample3">
			    </div>
			    <div class="main-content">
			    	<input type="hidden" id="shootLocationId">
			    	<input type="hidden" id="locationId">
			    	<input type="hidden" id="locationName">
					<div id="viewListDiv" class="viewList-div">
					</div>
			    </div>
	        </div>
	    </div>
		<!-- 右侧滑出框 end -->
	</div>
	<div id="searchdiv" class="my-jqx-window">
		<div>搜索</div>
		<div class="searchcon_div my-window-content">
			<ul class="searchUl">
				<li>
				<label class="stitle">拍摄地点：</label>
				<select id="shootLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
                    <option value="blank">[空]</option>
                </select>
                <input type="hidden" class="preValue">
                <a class="clearSelection clear-a-selection">清空</a>
		        </li>
	        </ul>
	        <ul class="searchUl">
				<li>
				<label class="stitle">主&nbsp;场&nbsp;景：</label>
				<select id="firstLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
                    <option value="blank">[空]</option>
                </select>
                <input type="hidden" class="preValue">
                <a class="clearSelection clear-a-selection">清空</a>
		        </li>
	        </ul>
	        <ul class="searchUl">
				<li>
				<label class="stitle">主要演员：</label>
				<select id="majorRoleSelect" class="selectpicker show-tick" multiple data-live-search="true" onchange="changeMajorRole()">
                    <option value="blank">[空]</option>
                </select>
                <input type="hidden" class="preValue">
                <a class="clearSelection clear-a-selection">清空</a>
		        </li>		        
                <span class="checkradiospan">
                	<label id="anyOneAppear"><input name="searchMode" value="0" type="radio" checked="checked">出现即可</label>
	         		<label id="NoOneAppear"><input name="searchMode" value="1" type="radio">不出现</label>
	         		<label id="everyOneAppear"><input name="searchMode" value="2" type="radio">同时出现</label>
	         		<!-- <label id="notEvenyOneAppear"><input name="searchMode" value="3" type="radio">不同时出现</label> -->
	         	</span>
	        </ul>
	        <!-- <ul class="searchUl">
	        	<li>
					<label class="stitle">排序：</label>
			        <label class="sortlabel"><input type="radio" name="sortField" value="shootLocation" checked="checked">拍摄地点</label>
					<label class="sortlabel"><input type="radio" name="sortField" value="viewNum">场数</label>
				</li>
			</ul> -->
			<ul class="searchUl">
	        	<li>
					<label class="stitle">场数：</label>
					<input type="text" class="search-text figure-input" id="minViewNum">&nbsp;-&nbsp;<input type="text" class="search-text figure-input" id="maxViewNum">
				</li>
			</ul>
			<ul class="searchUl">
	        	<li style="line-height: 25px;">
					<label class="stitle">主场景完成情况：</label>
					<label><input type="checkbox" name="completionChk" value=""> 不限 </label>
					<label><input type="checkbox" name="completionChk" value="1"> 全部完成 </label>
					<label><input type="checkbox" name="completionChk" value="2"> 部分完成 </label>
					<label><input type="checkbox" name="completionChk" value="3"> 未开始</label>
				</li>
			</ul>
			<ul class="operUl">
				<input type="button" id="querybutton" value="查询" onclick="gotoquery()">
				<input type="button" id="cancelbutton" value="取消">
				<input type="button" id="clearbutton" value="清空" onclick="clearSearchCon()">
			</ul>
		</div>
	</div>
	<!-- 显示正在加载中 -->
	<div id="loadingTableDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> Loading... </div>
	</div>
	<div id="loadingDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
	</div>
</body>
</html>
