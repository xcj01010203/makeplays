<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<!-- bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
	
	<!-- bootstrap JS -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/sys/syslogList.css?version=20170725">
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/sys/syslogList.js?version=20170725"></script>
	<script type="text/javascript">
		var loginUserType='${loginUserType}';
	</script>
</head>

<body>
	<div id="syslogdiv" style="width: 100%;height: calc(100% - 36px);">
    </div>
	<div class="pagediv">
		<a onclick="firstPage()">首页</a>
		<a onclick="previousPage()">上一页</a>
		<a onclick="nextPage()">下一页</a>
		当前条数：<span id="recordNum"></span>
	</div>
	<div id="queryWindow" class="searchWindow" style="display: none;">
		<div>高级查询</div>
		<div id="dropDownDIV" class='Popups_box'>
			<ul class="searchUl">
				<li>
					<label style="">剧组名称：</label> 
					<select id="searchCrewName" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue">
                   <a class="clearSelection clear-a-selection">清空</a>
				</li>
			</ul>
			<ul class="searchUl">
				<li>
					<label style="">制片公司：</label> 
					<input type="text" id="searchCompany" class="search-class" style="width: 455px;" name="searchCompany">
				</li>
			</ul>
			<ul>
				<li>
					<label>操作IP：</label> 
					<input type="text" id="searchIp" class="search-class" style="width: 380px;" name="searchIp" />&nbsp;&nbsp;
					<input type="checkbox" id="searchIsIp" />不包含</li>
			</ul>
			<ul>
				<li>
					<label>操作地点：</label> 
					<input type="text" id="searchAddress" class="search-class" style="width: 455px;" name="searchAddress" />
				</li>
			</ul>
			<ul style="width: 100%;">
				<li>
					<label style="">姓名(手机)：</label> 
					<input type="text" class="search-class" id="searchUserName" style="width: 455px;" name="searchUserName" />
					<ul class="dropdown-box user-dropdown" style="display: none;position: absolute;z-index: 999;width: 455px;height:auto;">
						<table>
							<thead>
								<tr>
									<td>姓名</td>
									<td>用户名</td>
									<td>手机</td>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</ul> 
					<input type="hidden" class="search-class" id="searchUserId" name="searchUserId" />
				</li>
			</ul>

			<ul style="width: 100%;">
				<li>
					<label>操作时间：</label> 
					<input type="text" class="Wdate search-class" id="searchstartTime" style="width: 215px;" name="searchstartTime"
						onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 00:00:00'})" />至
					<input type="text" class="Wdate search-class" id="searchendTime" style="width: 215px;" name="searchendTime"
						onFocus="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 23:59:59'})" />
				</li>
			</ul>

			<ul>
				<li>
					<label>终端类型：</label>
					<label><input name="searchType" value="" type="checkbox"/>全选</label>&nbsp;&nbsp; 
					<label><input name="searchType" value="0" type="checkbox" />pc</label> &nbsp;&nbsp;
					<label><input name="searchType" value="1" type="checkbox" />ios</label> &nbsp;&nbsp;
					<label><input name="searchType" value="2" type="checkbox" />android</label>
					<label style="margin-left: 100px;">
					含内部项目：
					<input type="checkbox" id="isIncludeInternalProject">
					</label>
				</li>
			</ul>

			<ul style="">
				<li>
					<label>操作类型：</label>
					<label><input type="checkbox" class="searchOperType" value=""/>全选</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="0"/>查询</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="1"/>插入</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="2"/>修改</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="3"/>删除</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="4"/>导入</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="5"/>导出</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="6"/>异常</label>&nbsp;&nbsp; 
					<label><input type="checkbox" class="searchOperType" value="99"/>其他</label>
				</li>
			</ul>
			<ul style="">
				<li>
					<label>日志摘要：</label>
					<div>
						<input type="text" class="search-class" id="searchlogDesc" style="width: 455px;" name="searchlogDesc" />
					</div>
				</li>
			</ul>
			<ul style="">
				<li>
					<label>操作对象：</label>
					<div>
						<input type="text" class="search-class" id="searchObject" style="width: 455px;" name="searchObject" />
					</div>
				</li>
			</ul>

			<ul class="oper_ul">
				<li class='claer'>
					<center>
					<input type='button' value='查询' id='searchSubmit' onclick="querySysLog()"/>&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type='button' value='关闭' id='closeSearchSubmit' />&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type='button' value='清空' id='clearSearchButton' onclick="clearSearchCon()"/>
					</center>
				</li>
			</ul>
		</div>
	</div>

	<div id="propTips" class="propTips_div">加载中...</div>
	<div id="logDesc" class="logDesc_div"></div>
	
	<div style="display: none;border: none;border-radius:0px;" id="logDescWindow">
		<div>日志摘要</div>
		<div id="contentBox" class="contentBox">
			<div class="topdiv">
			</div>
			<div class="bottomdiv">
				<input type="button" class="button" id="cancelButton" value="关闭">
			</div>
		</div>
	</div>
</body>
</html>
