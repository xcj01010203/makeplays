<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxdatatable.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxtreegrid.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxtooltip.js"></script>
	<!-- 实现表格的拖动 -->
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-easyui-1.4.5/themes/default/easyui.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.draggable.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/treegrid-dnd.js"></script>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/user/roleList.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/user/roleList.js"></script>
</head>
<body>

	<div id='roleListdiv' style="width:100%;height: 100%;">
		<div id="roleListgrid"></div>
	</div>
	<!-- 修改角色 -->
	<div style="display: none" id="updateWindow">
		<div id='uprole'>修改角色</div>
		<div style="overflow: hidden;">
			<div>
				<form id="updateRoleForm" action="/role/roleSave" method="post">
					<input type="hidden" name="roleId" id='roleId' value="" />
					<input type="hidden" name="crewId" id="crewId">
					<input type="hidden" name="parentId" id="parentId">
					<input type="hidden" name="level" id="level">
					<input type="hidden" name="orderNo" id="orderNo">
					<table class="register-table" style="margin-left: 30px;">
						<tr style="height: 59px">
							<td>
								<span style="color: red;margin-left: -9px;">* </span>
								<span id='roleGroup'></span>
							</td>
							<td>
								<input type="text" name="roleName" id="roleName" class="text-input" value="" />
								<div id='roleBlooen' style="color: #dd4b39;display: none;">名称已存在</div>
							</td>
						</tr>
						<tr style="height:59px">
							<td>角色描述:</td>
							<td><input type="text" name="roleDesc" id="roleDesc" class="text-input" value="" /></td>
						</tr>
						<tr style="height: 59px">
							<td colspan="2"><input type="button" value="保存" style="margin-left: 40px;margin-top: 10px;" id="sendButton" onclick="saveRole()"/>
							<td><input type="button" style="margin-left: -88px;margin-top: 10px;" value="关闭" onclick="sendCloses();" id='sendClose' /></td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
	<!-- 设置权限 -->
	<div id='roleDetailDiv' style='display:none;'>
		<div>权限详细信息</div>
		<div>
			<iframe frameborder="0" scrolling="yes" width="100%" height="100%"
				src=""></iframe>
		</div>
	</div>

	<!--  调整顺序弹出窗  -->
	<div class="jqx-window" id="sortWindow">
		<div class="sort-win-title">调整角色顺序</div>
		<div class="sort-win-container">
			<div class="sort-win-div-tips">
				<span class="sort-win-span-point">·</span>&nbsp;&nbsp;<span class="sort-win-span-content">拖动只能在同级之间进行。</span>
			</div>
			<div id="sortGrid"></div>
			<div class="close-sort-win">
				<input class="close-sort-btn" type="button" value="关闭" id="closeSortWinBtn" onclick="closeSortListWin()">
			</div>
		</div>
	</div>
</body>
</html>