<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>	
    <!-- 实现表格的拖动 -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-easyui-1.4.5/themes/default/easyui.css" type="text/css" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/easy-ui/icon.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	<link rel="stylesheet" href="<%=path%>/css/user/authorityList.css" type="text/css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.draggable.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/treegrid-dnd.js"></script>
	<script type="text/javascript"  src="<%=path%>/js/user/authorityList.js"></script>
</head>
<body>
	<div style="width: 100%; height: 100%; overflow: auto;">
		<input type="hidden" value='0' id='operateType' />
		<div id='authSpliter'>
			<div style="border-right:2px solid #64b5f6; ">
				<div id='gridTop' class="tabdiv">
					<div id='pcImg' class='pcImg' style="float: left;margin-left: 50px;" onclick="tabChange(0)"></div>
					<div id='mobileImg' class='mobileImg1' style="float: right;margin-right: 50px;" onclick="tabChange(1)"></div>
				</div>
				<div class='addroot' onclick="addAuth();" title='添加权限'></div>
				<div class="treediv" id="pctreediv">
					<table id="pcTree">
					</table>
				</div>
				<div class="treediv" id="mobiletreediv">
					<table id="mobileTree">
					</table>
				</div>
			</div>			
			<div style="min-width:880px;">
				<div id="right_main">
					<div class="con_right_title">
						<div class="title_div">
							<form action="" id='updateAuthForm'>
								<table class="update_table1">
									<tr>
										<td>权限名称:</td>
										<td><input type="text" name="authNameRight" id="authNameRight" class="text-input1" /></td>
									</tr>
									<tr>
										<td>操作描述:</td>
										<td><input type="text" name="operDescRight" id="operDescRight" class="text-input1" /></td>
									</tr>
									<tr>
										<td>权限URL:</td>
										<td><input type="text" name="authUrlRight" id="authUrlRight" class="text-input1" /></td>
									</tr>
									<tr>
										<td>是否区分读写操作：</td>
										<td><select class="text-input1" onchange="differInRAndWRightChange(this)" id="differInRAndWRight">
												<option value=0>否</option>
												<option value=1>是</option>
										</select></td>
									</tr>
									<tr>
										<td>菜单图标样式：</td>
										<td><input type="text" name="cssNameRight" id="cssNameRight" class="text-input1" /></td>
									</tr>
								</table>
								<table class="update_table2">
									<tr>
										<td>状态:</td>
										<td><select name="statusRight" id="statusRight" class='text-input1'>
												<option value="0">有效</option>
												<option value="1">无效</option>
										</select></td>
									</tr>
									<tr>
										<td>是否菜单:</td>
										<td><select name="ifMenuRight" id="ifMenuRight" class='text-input1'>
												<option value="1">是</option>
												<option value="0">不是</option>
										</select></td>
									</tr>
									<tr>
										<td>操作编码:</td>
										<td><input type="text" name="authCodeRight" id="authCodeRight" class="text-input1" /></td>
									</tr>
									<tr>
										<td>默认读写操作：</td>
										<td><select class="text-input1" name="defaultRorWRight" id="defaultRorWRight">
												<option value=2>可编辑</option>
												<option value=1>只读</option>
										</select></td>
									</tr>
								</table>
							</form>
							<div style="width: 100%;overflow:hidden;">
								<div style="float: right;">
									<div class='authModify' id='modifyauthroity' onclick="updateAuth(this)" title="修改"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="con_right_title">
						<div id="con_right_bottom" class="con_right_bottom">
							<div class="tab_wrap">
								<ul>
									<li class="tab_li_current">该权限的角色分布</li>
									<li>该权限的剧组分布</li>
								</ul>
							</div>
							<div class='authority_box'>
								<dl id='authRoleTable'>

								</dl>
								<dl id='authCrewTable' style="display: none">
									<div id="validCrewDiv" class="div-top"></div>
									<div class="line-cutoff"></div>
									<div id="expiredCrewDiv" class="div-bottom"></div>
								</dl>
							</div>
							<div class="bottom_button" style="height: 30px;">
								<div class='authSave' id='btnsure' onclick="saveAuthDis()" title="保存"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>		
	</div>
	<div style="display: none;" id="addAuthWindow">
		<div>创建权限</div>
		<div id="inputBox">
			<form id="addAuthForm" action="" method="post">
				<input type="hidden" name="authId" id="authId" value="0" class="text-input" />
				<input type="hidden" name="parentId" id="parentId" value="0" class="text-input" />
				<input type="hidden" name="sequence" id="sequence" value="0" class="text-input" />
				<input type="hidden" name="authPlantform" id="authPlantform" value="2" class="text-input" />
				<input type="hidden" name="operType" id="operType" value="2" class="text-input" />
				<table class="register-table">
					<tr>
						<td class="addTable_td1">权限名称:</td>
						<td><input type="text" name="authName" id="authName" class="text-input" /></td>
					</tr>
					<tr>
						<td class="addTable_td1">操作描述:</td>
						<td><input type="text" name="operDesc" id="operDesc" class="text-input" /></td>
					</tr>
					<tr>
						<td class="addTable_td1">权限URL:</td>
						<td><input type="text" name="authUrl" id="authUrl" class="text-input" /></td>
					</tr>
					<tr>
						<td class="addTable_td1">状态:</td>
						<td><select name="status" id="status">
								<option value="0">有效</option>
								<option value="1">无效</option>
						</select></td>
					</tr>
					<tr>
						<td class="addTable_td1">是否菜单:</td>
						<td><select name="ifMenu" id="ifMenu">
								<option value="1">是</option>
								<option value="0">不是</option>
						</select></td>
					</tr>
					<tr>
						<td class="addTable_td1">是否区分读写操作:</td>
						<td><select name="differInRAndW" id="differInRAndW" onchange="differInRAndWChange(this)">
								<option value="0">否</option>
								<option value="1">是</option>
						</select></td>
					</tr>
					<tr style="display: none;">
						<td class="addTable_td1">默认读写操作:</td>
						<td><select name="defaultRorW" id="defaultRorW">
								<option value="2" selected="selected">可编辑</option>
								<option value="1">只读</option>
						</select></td>
					</tr>
					<tr>
						<td class="addTable_td1">操作编码:</td>
						<td><input type="text" name="authCode" id="authCode" class="text-input" /></td>
					</tr>
					<tr>
						<td class="addTable_td1">是否赋给所有剧组:</td>
						<td><select name="isForAllCrew" id="isForAllCrew">
								<option value="0">否</option>
								<option value="1">是</option>
						</select></td>
					</tr>
					<tr>
						<td class="addTable_td1">菜单图标样式:</td>
						<td><input type="text" name="cssName" id="cssName" class="text-input" /></td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center;padding-top: 20px;">
							<input type="button" value="保存" id="sendButton" onclick="saveAuth()" /> 
							<input type="button" value="取消" id='cancelAuth' style="margin-left: 30px;"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div id="loadingDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> 请稍候... </div>
	</div>
</body>
</html>