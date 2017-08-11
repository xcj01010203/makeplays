<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.xiaotu.makeplays.user.model.constants.UserType"%>
<%@page import="com.xiaotu.makeplays.user.model.UserInfoModel"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();

SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
Date currentTime = new Date();//得到当前系统时间
String today = formatter.format(currentTime); //将日期时间格式化
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <script>
    	var loginUserType="${loginUserType}";
  	  	var parentFlag='<%=request.getParameter("flag")%>';  
  	  	var userId='<%=request.getParameter("userId")%>';
    </script>
    <link rel="stylesheet" href="<%=path%>/css/crewSettings.css" type="text/css" />
    <link rel="stylesheet" type="text/css" href="<%=path%>/css/webuploader.css">
    <link rel="stylesheet" href="<%=path%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
	<link rel="stylesheet" href="<%=path%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
	<script type="text/javascript" src="<%=path%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
  	<link rel="stylesheet" href="<%=path%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
	<script type="text/javascript" src="<%=path%>/js/crew/crewSettings.js"></script>
    <script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>  
	<script type="text/javascript" src="<%=path%>/js/UI-Checkbox-master/checkbox.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/webuploader/webuploader.min.js"></script>
  </head>
  
  <body>
    <div class="crewSettings-rootdiv" style="width: 100%; height: 100%; overflow: auto;">
        <div class="crewSettings">
	        <div class="sub-settings" id="content">
	            <div class="title">
	               <label id="crewNameText"></label>
	               <input type="hidden" id="crewId">
	            </div>
	        </div>
	        <div class="sub-settings enter-apply-div">
	            <div class="title"><label>进组申请</label></div>
	            <div class="content">
	                <table class="enter-apply-table">
	                    <tr class="table-head">
	                        <td style="width: 10%;">申请人</td>
	                        <td style="width: 25%;">职务</td>
	                        <td style="width: 10%;">手机</td>
	                        <td style="width: 10%;">申请时间</td>
	                        <td style="width: 25%;">备注</td>
	                        <td style="width: 20%;">操作</td>
	                    </tr>
	                </table>
	                
	                <table id="enterApplyerTable" class="enter-apply-table">
                    </table>
                    
	            </div>
	        </div>
	        <div class="sub-settings">
	            <div class="tab_div">
	            	<ul>
	            		<li class="tab_li_current">剧组信息</li>
	            		<li style="display: none;">剧组权限设置</li>
	            		<li>成员管理</li>
	            		<li>成员权限查看</li>
	            	</ul>
	            </div>
	            <div id="crewInfoSet" class="crewInfoSet-div">
	            	<ul>
		                <li>
		                    <p>剧组名称：</p>
		                    <input type="text" id="crewName">
		                    <label class="necessory">*</label>
                   			<label class="error-message" id="crewNameErrorMsg"></label>
              			</li>
              			<li>
		                    <p>剧组类型：</p>
		                    <select id="crewType">
		                        <option value="1" selected>电视剧</option>
		                        <option value="0">电影</option>
		                        <option value="2">网剧</option>
		                        <option value="3">网大</option>
		                    </select>
		                    <label class="necessory">*</label>
		                </li>
              			<li style="display: none;">
		                    <p>项目类型：</p>
		                    <select id="projectType">
		                        <option value="0" selected>普通项目</option>
		                        <option value="1">试用项目</option>
		                        <option value="2">内部项目</option>
		                    </select>
		                </li>
		                <li>
		                    <p>题&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;材：</p>
		                    <select id="subject">
		                        <option value=""></option>
		                    </select>
		                </li>
		                <li>
		                    <p>立项集数：</p>
		                    <input type="text" id="seriesNo">
                   			<label class="error-message" id="seriesNoErrorMsg"></label>
		                </li>		                
		                <li>
		                    <p>制片公司：</p>
		                    <input type="text" id="company">
		                <li>
		                    <p>目前状态：</p>
		                    <select id="status">
		                        <option value=""></option>
		                        <option value="1">筹备中</option>
		                        <option value="2">拍摄中</option>
		                        <option value="3">后期制作中</option>
		                        <option value="4">已完成</option>
		                        <option value="5">播出中</option>
		                        <option value="6">暂停</option>
		                    </select>
		                </li>	                
		                <li>
		                    <p>入组密码：</p>
		                    <input type="text" id="enterPassword" placeHolder="仅支持六位数字">
		                    <label class="necessory">*</label>
		                    <label class="error-message" id="enterPassMsg"></label>
		                    <div class="descrip">入组密码为其他人进入剧组的重要凭证</div>
		                </li>
	                </ul>
	                <ul>
		                <li style="display: none;">
		                   	<p>账号开始时间：</p>
							<input type="text" id="startDate" value='<%=today %>' onFocus="WdatePicker({readOnly:true, maxDate: '#F{$dp.$D(\'endDate\')}'})">
							<label class="necessory">*</label>
						</li>
						<li style="display: none;">
	                    	<p>账号结束时间：</p>
		                    <input type="text" id="endDate" value='<%=today %>' onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'startDate\')}'})">
		                    <label class="necessory">*</label>
		                </li>
		                <li>
		                    <p>开机时间：</p>
		                    <input type="text" id="shootStartDate" onFocus="WdatePicker({readOnly:true, maxDate: '#F{$dp.$D(\'shootEndDate\')}', isShowClear: false})">
		                </li>
		                <li>
	                    	<p>杀青时间：</p>
		                    <input type="text" id="shootEndDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'shootStartDate\')}', isShowClear: false})">
		                </li>
		                <li>
	                    	<p>备&nbsp;&nbsp;案&nbsp;&nbsp;号：</p>
	                    	<input type="text" id="recordNumber">
	                    </li>
	                    <li>
	                    	<p>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演：</p>
	                    	<input type="text" id="director">
		                </li>
		                <li>
	                		<p>编&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;剧：</p>
	                    	<input type="text" id="scriptWriter">
		                </li>
		                <li>
	                    	<p>主&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演：</p>
	                    	<input type="text" id="mainactor">
		                </li>
		            </ul>
		            <ul>
		                <li>
	                    	<p>合拍协议：</p>
	                    	<select id="coProduction" onchange="coProductionChange(this)">
		                        <option value="" selected></option>
		                        <option value="0">无</option>
		                        <option value="1">已签订</option>
		                    </select>
			           	</li>
			           	<li>
		                    <p>剧组执行预算：</p>
		                    <input type="text" id="budget" class="figure-input" placeHolder="0">
		                    <label class="error-message" id="budgetErrorMsg"></label>
		                </li>
		                <li style="display: none;">
		                    <p>合拍协议金额：</p>
		                    <input type="text" id="coProMoney" class="figure-input" placeHolder="0">
		                    <label class="error-message" id="coProMoneyErrorMsg"></label>
			            </li>
			            <li style="display: none;">
	                    	<p>我方投资比例：</p>
	                    	<input type="text" id="investmentRatio" class="figure-input" placeHolder="100%">
	                    	<input type="text" style="display: none">
		                    <label class="error-message" id="investmentRatioErrorMsg"></label>
		                </li>
		            </ul>
		            <ul>
		                <li class="all-li">
	                    	<p>重要事项说明：</p>
	                    	<textarea rows="4" cols="10" id="remark"></textarea>
		                </li>
	            	</ul>
	            	<ul style="height: 160px;">
		                 <li class="cross">
		                 <p>剧组图片&nbsp;:</p>
				             <div class="upload-container">
	                          <div class="select-file-btn" id="uploadFileBtn">添加</div>
	                        </div>
                           		<ul class="upload-file-list" id="uploadFileList"></ul>
	                     </li>
	                      <li class="all-li create-btn-li">
		                    <input class="create-btn" id="modifyBtn" type="button" value="保存" onclick="modifyCrew()">
		                </li>
	                 </ul>
	            </div>
	            <div id="crewAuthSet" class="crewAuthSet_div" style="display: none;">
	            	<div class="title-tab">
		               <p class="selected" onclick="siwtchPlatform(this)">PC端权限</p>
	                   <p onclick="siwtchPlatform(this)">APP端权限</p>
		            </div>
		            <div class="auth-info">
	                    <!-- PC端权限 -->
			            <div id="pcAuthList" class="pc-auth-list"></div>
	                    <!-- APP端权限 -->
	                    <div id="appAuthList" class="app-auth-list"></div>
		            </div>
	            </div>
	            <div id="crewUserManage" style="display: none;">
		            <input class="add-user-btn" type="button" value="添加成员" onclick="showAddUserWin()">
		            <input type="text" style="display: none">
		            <div id="crewUserListDiv">
		                
		            </div>
	            </div>
	            <div id="authWatch" class="authWatch_div" style="display: none;">
	            	<div class="left_div">
		            	<div id='gridTop' class="tabdiv">
		            		<div id='pcImg' class='pcImg' style="float: left;margin-left: 50px;" onclick="tabChange(0)"></div>
							<div id='mobileImg' class='mobileImg1' style="float: right;margin-right: 50px;" onclick="tabChange(1)"></div>
						</div>
						<div class="treediv" id="pctreediv">
							<div id="pcTree"></div>
						</div>
						<div class="treediv" id="mobiletreediv" style="display: none;">
							<div id="mobileTree"></div>
						</div>
	            	</div>
	            	<div class="right_div">
	            		<div class="authtitle"><label id="authTitle"></label></div>
	            		<div id="rightDiv"></div>
	            	</div>
	            </div>
	        </div>
	    </div>   	    
    
	    <div id='crewUserDetailDiv' style='display:none;'>
	        <div>用户详细信息</div>
	        <div>
	            <iframe frameborder="0" scrolling="yes" width="100%" height="100%" src=""></iframe>
	        </div>
	    </div>
	    
	    <div id='crewDetailDiv' style='display:none;'>
	        <div>修改剧组</div>
	        <div>
	            <iframe frameborder="0" scrolling="yes" width="100%" height="100%" src=""></iframe>
	        </div>
	    </div>
	    
	    <div id='addUserDiv' style='display:none;'>
	        <div>添加成员</div>
	        <div>
	            <iframe frameborder="0" scrolling="yes" width="100%" height="100%" src=""></iframe>
	        </div>
	    </div>
	    <div class="jqx-window delete-crewmember-win" id="deletCrewMemberWin">
	        <div>清空记录</div>
	        <div class="delete-crew-content">
	             <ul>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="12">&nbsp;&nbsp;筹备进展:</label>
	                    <span class="root-data" id="prepareData"></span>
	                </li>
	                <li class="shoot-pro-li">
	                    <label><input type="checkbox" name="crewInfo" value="1">&nbsp;&nbsp;拍摄生产数据:</label>
	                    <span class="shoot-pro-data" id="ShootProData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="2">&nbsp;&nbsp;剧组联系表:</label>
	                    <span class="crew-tel-data" id="crewTelData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="10">&nbsp;&nbsp;住宿:</label>
	                    <span class="crew-tel-data" id="inHotelData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="9">&nbsp;&nbsp;勘景:</label>
	                    <span class="sceneView-data" id="sceneViewData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="8">&nbsp;&nbsp;车辆:</label>
	                    <span class="car-data" id="carData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="11">&nbsp;&nbsp;餐饮:</label>
	                    <span class="cater-data" id="caterData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="3">&nbsp;&nbsp;费用收支数据:</label>
	                    <span class="finance-data" id="financeData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="4">&nbsp;&nbsp;合同数据:</label>
	                    <span class="contract-data" id="contractData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="5">&nbsp;&nbsp;预算数据:</label>
	                    <span class="budget-data" id="budgetData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="6">&nbsp;&nbsp;财务密码:</label>
	                    <span class="finance-pass" id="financePassWord"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="13">&nbsp;&nbsp;审批:</label>
	                    <span class="receipt-data" id="receiptData"></span>
	                </li>
	                <li>
	                    <label><input type="checkbox" name="crewInfo" value="7">&nbsp;&nbsp;用户数据:</label>
	                    <span class="root-data" id="rootData"></span>
	                </li>
	            </ul> 
	            <div class="win-btn-list-del">
	                <input type="button" class="delete-crew-btn" onclick="clearCrewBtn()" value="清空">
	            </div>
	        </div>
	    </div>
	       
		<div id="loadingDiv" class="show-loading-container" style="display: none;">
			<div class="show-loading-div"> 正在删除，请稍候... </div>
		</div>
    </div>
  </body>
</html>
