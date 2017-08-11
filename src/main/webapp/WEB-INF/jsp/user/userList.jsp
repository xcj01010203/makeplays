<%@page import="com.xiaotu.makeplays.utils.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" href="<%=path%>/js/jquery.multiselect/css/multi-select.css" type="text/css" />
    <link rel="stylesheet" href="<%=path%>/css/user/userList.css" type="text/css" />
    <script type="text/javascript"  src="<%=path%>/js/jquery.multiselect/js/jquery.multi-select.js"></script>
    <script type="text/javascript"  src="<%=path%>/js/jqwidgets/jqxgrid.sort.js"></script>
    <script type="text/javascript"  src="<%=path%>/js/user/userList.js"></script>
    <script type="text/javascript">
	    var openType = ${opentype};
	    var openUserId = '${openUserId}';
	    var openUserInfo = '${openUserInfo}';
	    
	    var majorCustomerService=<%=Constants.ROLE_ID_CUSTOM_SERVICE%>;
	    var seniorCustomerService=<%=Constants.ROLE_ID_SENIOR_CUSTOMERSERVICE%>;
	    var middleCustomerService=<%=Constants.ROLE_ID_MIDDLE_CUSTOMERSERVICE%>;
	    var juniorCustomerService=<%=Constants.ROLE_ID_JUNIOR_CUSTOMERSERVICE%>;
    </script>
</head>
<body>
	<div class="maindiv">
		<div id="jqxgridToolbar" class="toolbardiv">
			<div id="searchButton" class="div_search" onclick="showSearchWindow()" title="高级搜索"></div>
			<a title='创建用户' href='javascript:createUser();' id='jqxButton' class='addBtn'></a>
			<div class="search_box">
				<input type="text"  name='searchUser' id='searchUser' onkeyup="searchUser(this);" placeholder='输入姓名或手机号' class='search_kuang'/>
				<div class='icon_cha1'>
					<img src="../images/search.png">
				</div>
			</div>
		</div>
		<div class="griddiv">
			<div id='userListGrid' class="userListGrid"></div>
		</div>
	</div>
	<!-- 修改用户 -->
	<div id='rightMain' class="rightdiv">
		<div class='con_right_title'>
			<span class="current">修改用户</span>
           	<div class="operateBtn">
                <input type="button" style="margin-right: 30px;"  onclick="sendMsg();" value="发送通知短信" id="sendMsgBtn" />
                <input type="button" style="margin: 0 30px 0 30px;"  onclick="submintUpdate();" value="保存" id="sendButton" />
                <input type="button" style="margin: 0 30px 0 30px;"  onclick="closeUpdate();" value="关闭" id="closeButton" />
           	</div>
        </div>
		<div class="con_right_content">
			<div id='updateUserDiv'>
				<form action="" id='updateForm' style="display: none;">
					<input type="hidden" name="oldUserName" id='oldUserName' value="" />
					<input type="hidden" name="oldPhone" id='oldPhone' value="" /> 
					<table class="updateUserTable">
						<tr>
							<th width="15%">姓名：</th>
							<td width="35%"><input type="text" name="realNameUpdate" id="realNameUpdateInput" class="text-input" value="" /></td>
							<th width="15%">性别：</th>
							<td width="35%">
								<select name="sexUpdate" id='sexUpdate' class="text-input">
									<option value="1">男</option>
									<option value="0">女</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>手机：</th>
							<td>
								<input type="text" id="phoneUpdate" name="phoneUpdate" class="text-input" value="" />
								<div style="color: red;display: none;" id='phoneUpdateDiv'>手机号已存在</div>
							</td>
							<th>E-mail：</th>
							<td><input type="text" id="emailUpdateInput" name="emailUpdate" placeholder="someone@mail.com" class="text-input" /></td>
						</tr>
						<tr>
							<th><font color="red">* </font>密码：</th>
							<td><input type="password" name="passwordUpdate" id="passwordUpdateInput" class="text-input" /></td>
							<th><font color="red">* </font>密码确认：</th>
							<td><input type="password" id="passwordConfirmUpdateInput" class="text-input" /></td>
						</tr>
						<tr>
							<th><font color="red">* </font>可建组次数：</th>
							<td><input type="text" name="ubCreateCrewNumUpdateInput" id="ubCreateCrewNumUpdateInput" class="text-input" value="0" />
							</td>
							<th>状态：</th>
							<td>
								<select name="statusUpdate" id='statusUserUpdate' class="text-input" style="">
									<option value="1">有效</option>
									<option value="2">无效</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>用户类型：</th>
							<td id="userTypeTd" colspan="3">
								
							</td>
						</tr>
					</table>
				</form>
			</div>
			<div class='con_right_title con_right_bot_tit' style="clear: both;">
				<span id="usercrewTab" class="current" onclick="tabChange(this,1)">所在剧组</span>
				<span onclick="tabChange(this,2)">个人简历</span>
				<div class="operateBtn">
					<!-- <div id='customservice' class="customservice" onclick="customservice();">设为客户服务</div> -->
					<div title='新增剧组' userId='' onclick='addCrew(this);' id='add' class="addCrewdiv"></div>
				</div>
			</div>
			<div id="user_crew" class="user_crew_div">
				<div id='con_right_bottom' class="con_right_bottom"></div>
				<div id='con_right_bottom1' class="con_right_bottom" style="display: none;">
					<div style="width:100%;text-align:center;line-height:50px;font-size:16px;color: #d1d1d1;">暂无剧组</div>
				</div>
			</div>
			<div id='user_work_history' class="user_work_div" style="display: none;"></div>
		</div>
	</div>
	<!-- 创建用户 -->
	<div id="adminWindow" style="display: none">
	    <div>创建用户</div>
        <div>
        	<form id="adminForm" action="">
            <input type="hidden" name="crewId" id='adminCrewId' value=""/>
            <input type="hidden" name="userId" id='userId' value=""/>
            <input type="hidden" name="operateStatus" id='operateStatus' value="0"/><!--0:添加，1：更新  -->
            <table class="register" style="margin: auto;">
                <tr>
                    <td align="right"><font color="red">* </font>姓名：</td>
                    <td><input type="text" name="realName" id="realNameInput" class="text-input" value=""/></td>
                </tr>
                <tr>
                    <td align="right">用户类型：</td>
                    <td><label><input type="radio" name="userType" value="0" checked="checked">普通用户</label> &nbsp;&nbsp; 
                    <label><input type="radio" name="userType" value="2">客户服务</label></td>
                </tr>
                <tr id="serverTypeTr" style="display: none;">
                    <td align="right">客服类型：</td>
                    <td><label><input type="radio" name="serverType" value="<%=Constants.ROLE_ID_CUSTOM_SERVICE%>">总客服</label> &nbsp;&nbsp; 
                    <label><input type="radio" name="serverType" value="<%=Constants.ROLE_ID_SENIOR_CUSTOMERSERVICE%>">高级</label> &nbsp;&nbsp;
                    <label><input type="radio" name="serverType" value="<%=Constants.ROLE_ID_MIDDLE_CUSTOMERSERVICE%>">中级</label> &nbsp;&nbsp;  
                    <label><input type="radio" name="serverType" value="<%=Constants.ROLE_ID_JUNIOR_CUSTOMERSERVICE%>" checked="checked">初级</label></td>
                </tr>
                <tr style="height: 52px" >
                    <td align="right"><font color="red">* </font>可建组次数：</td>
                    <td>
                        <input type="text" name="ubCreateCrewNum" id="ubCreateCrewNum" class="text-input" value="0"/>
                    </td>
                </tr>
                <tr style="height: 52px">
                    <td align="right"><font color="red">* </font>密码：</td>
                    <td><input type="password" name="password" id="passwordInput" class="text-input" /></td>
                </tr>
                <tr style="height: 52px">
                    <td align="right"><font color="red">* </font>密码确认：</td>
                    <td><input type="password" id="passwordConfirmInput" class="text-input" /></td>
                </tr>
                <tr>
                    <td align="right">性别：</td>
                    <td>
                    <select name="sex" id='sex' class="text-input">
                     <option value="1">男</option>
                     <option value="0">女</option>
                    </select></td>
                </tr>
                <tr>
                    <td align="right">E-mail：</td>
                    <td><input type="text" id="emailInput"  name="email" placeholder="someone@mail.com" class="text-input"/></td>
                </tr>
                <tr>
                    <td align="right"><font color="red">* </font>手机：</td>
                    <td>
                    	<input type="text" id="phone" name="phone" class="text-input" value=""/>
                    	<div style="color: red;display: none;" id='phoneDiv'>手机号已存在</div>
                    </td>
                </tr>
                
                <tr>
                    <td align="right">状态：</td>
                    <td>
                    	<select name="status" id='statusUser' class="text-input" style="">
                     	<option value="1">有效</option>
                      <option value="2">无效</option>
                    	</select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center;">
                        <input type="button" class='' style="margin-right:20px;margin-left: 20px;" onclick="addUser();" value="保存" id="sendButton" />
                        <input type="button" value="取消" onclick="closeAdmin();" />
                   </td>
                </tr>
            </table>
            </form>
        </div>
    </div>
    <!--从已有剧组中添加  -->
	<div id='addCrewToUser'>
		<div>添加剧组</div>
		<div>
			<div id="crewSelDiv" style="height: 100%;">
				<div class="crewSelTitle">剧组搜索</div>
				<div class="crewSearch">
					<div class="search_div">
						<input type="text" name='searchCrewInput' class='search_kuang' id='searchCrewInput'/>
						<div class='icon_cha'><img src="<%=request.getContextPath()%>/images/search.png"></div>
					</div>				
					<div class='searchList'>
						<dl>
							<dt>剧组类型：</dt>
							<dd>
								<ul id='searchCrewType'>
									<li class='font_color'>全部</li>
									<li sid='0'>电影</li>
									<li sid='1'>电视剧</li>
									<li sid='2'>网剧</li>
									<li sid='3'>网大</li>
								</ul>
							</dd>
						</dl>
					</div>
					<div id='addTo' class='addCrewButton' title='添加剧组' onclick="addCrewButtonClick()"></div>
				</div>
				<div id='addCrewToUserBody' class="addCrewToUser_content">
					<!-- <div id='addCrewToUserGrid'></div> -->
				</div>
			</div>
			<div id="roleSelDiv" class="roleselDiv">
				<label class="descrip" id="descrip">* 选择将要担任的职务（未选择&lt;&gt;已选择）</label>
				<label class="errormessage" id="errormessage">* 请选择期望担任的职务（未选择&lt;&gt;已选择）</label>
			 	<select id='roleSelect' multiple></select>
				<br>
			 	<center>
				 	<input type="button" value="加入到剧组" onclick="addToCrew()">
				 	<input type="button" value="返回" onclick="goback()">
			 	</center>
			</div>
		</div>
	</div>
	<div id="superQueryWindow">
		<div>高级搜索</div>
		<div class="search-condition">
			<ul>
				<li>
					<label>姓名(手机)：</label>
					<input type="text" id="userName_search">
				</li>
			</ul>
			<ul>
				<li>
					<label>状态：</label>
					<label><input name="status_search" value="" type="radio">全部</label> &nbsp;&nbsp; 
					<label><input name="status_search" value="1" type="radio">有效</label> &nbsp;&nbsp;
					<label><input name="status_search" value="2" type="radio">无效</label>
				</li>
			</ul>
			<ul>
				<li>
					<label>类型：</label>
					<label><input name="userType_search" value="" type="checkbox">全部</label> &nbsp;&nbsp; 
					<label><input name="userType_search" value="0" type="checkbox">普通用户</label> &nbsp;&nbsp;
					<label><input name="userType_search" value="1" type="checkbox">系统管理员</label>
				</li>
			</ul>
			<ul>
				<li>
					<label>&nbsp;</label>
					<label><input name="userType_search" value="<%=Constants.ROLE_ID_CUSTOM_SERVICE%>" type="checkbox">总客服</label> &nbsp;&nbsp; 
					<label><input name="userType_search" value="<%=Constants.ROLE_ID_SENIOR_CUSTOMERSERVICE%>" type="checkbox">高级客服</label> &nbsp;&nbsp; 
					<label><input name="userType_search" value="<%=Constants.ROLE_ID_MIDDLE_CUSTOMERSERVICE%>" type="checkbox">中级客服</label> &nbsp;&nbsp; 
					<label><input name="userType_search" value="<%=Constants.ROLE_ID_JUNIOR_CUSTOMERSERVICE%>" type="checkbox">初级客服</label>
				</li>
			</ul>
			<ul class="oper_ul">
				<li>
					<center>
						<input type="button" value="查询" id="searchSubmit" onclick="queryUser()">&nbsp;&nbsp;&nbsp;&nbsp; 
						<input type="button" value="关闭" id="closeSearchSubmit">&nbsp;&nbsp;&nbsp;&nbsp; 
						<input type="button" value="清空" id="clearSearchButton" onclick="clearSearchCon()">
					</center>
				</li>
			</ul>
		</div>
	</div>
</body>