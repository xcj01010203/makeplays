<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isShootLogReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CLIP)) {
	    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CLIP) == 1){
	    	isShootLogReadonly = true;
	    }
    }
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>css/webuploader.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/shootLog/shootLogList.css">
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/webuploader/webuploader.min.js"></script>
	
	  <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/shootLog/shootLogList.js"></script>
	<script type="text/javascript">
	   
	   //跳转到更新拍摄日志信息页面
       function updateShootLog (shootLogId) {
        window.location.href = "<%=basePath%>/shootLogManager/shootLogDetail?shootLogId=" + shootLogId;
       } 
       var isShootLogReadonly = <%=isShootLogReadonly%>;
	   
	</script>
  </head>
  
  <body>
    <div id="shootLogGrid" class="main-class">
    	<table id="shootLogTable">
    		<thead>
    			<tr>
    				<td style="width: 8%; min-width: 8%;">拍摄日期</td>
    				<td style="width: 6%; min-width: 6%;">分组</td>
    				<td style="width: 20%; min-width: 20px; max-width: 20%;">拍摄地点</td>
    				<td style="width: 8%; min-width: 8%;">页数</td>
    				<td style="width: 8%; min-width: 8%;">场数</td>
    				<td style="width: 30%; min-width: 30%; max-width: 30%;">场景</td>
    				<td style="width: 10%; min-width: 10%;">开拍时间</td>
    				<td style="width: 10%; min-width: 10%;">结束时间</td>
    			</tr>
    		</thead>
    		<tbody id="clipListTbody">
    		
    		</tbody>
    	</table>
    </div>
    <!-- 右边弹出窗 -->
    <div class="right-popup-win" id="rightPopUpWin">
        <input type="hidden" id="noticeId">
        <input type="hidden" id="nowShootTime">
        <div class="win-btn-list">
		      <input type="button" value="关闭" onclick="closePopUpWin()">
		    </div>
		    
		    <div class="scence-content" id="scenceContentDiv">
		       <!-- tab页 -->
		        <div class="tab-body-wrap">
		            <!-- tab键容器 -->
                <div class="btn_tab_wrap">
                    <!-- tab键空白处 -->
                    <div class="btn_wrap"></div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="site_information" class="tab_li_current" onclick="showSiteInformation(this)">现场信息</li>
                            <li id="actor_attendance" onclick="actorAttendance(this)">演员出勤</li>
                            <li id="department_performance" onclick="departPerformance(this)">部门表现</li>
                            <li id="special_item" onclick="specialItemInfo(this)">特殊道具</li>
                            <li id="important_remark" onclick="importantRemark(this)">重要备注</li>
                        </ul>
                    </div>
                    
                    
                </div>
                <!-- 现场信息div -->
                <div class="shootlog-public site-information-div">
                    <div class="site-information-tips">
                        <p class="shootlog-basic-title">基本信息</p>
                        <ul class="shootlog-basic-info">
                            <li>
                                <p class="title-p">拍摄带号:</p>
                                <input type="text" class="content-input" id="tapNoDD">
                            </li>
                            <li>
                                <p class="title-p">拍摄地点:</p>
                                <input type="text" class="content-input" id="shootLocationDD">
                            </li>
                            <li>
                                <p class="title-p">拍摄场景:</p>
                                <input type="text" class="content-input" id="shootSeneDD">
                            </li>
                            <li>
                                <p class="title-p">出发时间:</p>
                                <input type="text" class="content-input" id="startTimeDD" onclick="WdatePicker({dateFmt:'HH:mm'})">
                            </li>
                            <li>
                                <p class="title-p">到场时间:</p>
                                <input type="text" class="content-input" id="arriveTimeDD" onclick="WdatePicker({dateFmt:'HH:mm'})">
                            </li>
                            <li>
                                <p class="title-p">开拍时间:</p>
                                <input type="text" class="content-input" id="bootTimeDD" onclick="WdatePicker({dateFmt:'HH:mm'})">
                            </li>
                            <li>
                                <p class="title-p">结束时间:</p>
                                <input type="text" class="content-input" id="packupTimeDD" onclick="WdatePicker({dateFmt:'HH:mm'})">
                                <input type="text" style="display: none;">
                            </li>
                        </ul>
                        <p class="shootlog-basic-title transition-site-title">
                            <span>转场信息</span>
                            <input type="button" class="add-transition-site" onclick="addTransitionSite()" title="添加转场信息">
                        </p>
                        <div class="transition-site-info">
                            <div class="transition-table-header">
                                <table class="transition-table"  cellspacing = 0, cellpadding = 0>
		                                    <tr>
		                                        <td style="width: 20%; min-width: 20%; max-width: 20%; text-align: left; box-sizing: border-box; padding-left: 5px;">拍摄地点</td>
		                                        <td style="width: 20%; min-width: 20%; max-width: 20%; text-align: left; box-sizing: border-box; padding-left: 5px;">拍摄场景</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center; box-sizing: border-box;">出发时间</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center; box-sizing: border-box;">到场时间</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center; box-sizing: border-box;">开拍时间</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center; box-sizing: border-box;">结束时间</td>
		                                    </tr>
		                                <!-- <tbody id="convertInfoTbody">
		                                
		                                </tbody> -->
                                </table>
                            </div>
                            
                            <div class="transition-table-body">
                                <table class="transition-table" id="transitionTable" cellspacing = 0, cellpadding = 0>
                                    
                                </table>
                            </div>
                            
                        </div>
                        
                        <div class="btn-list">
                            <input type="button" value="保存" onclick="saveLiveInfo()">
                        </div>
                        
                    </div>
                </div>
                
                <!-- 演员出勤 -->
                <div class="shootlog-public actor-attendance-div">
                    <div class="actor-information-tips">
                        <p class="gate-card-title">
                            <label><input type="radio" name="gateCard" checked value="1" onclick="showActorGrid(1)">主要演员、特约演员出勤表</label>
                            <!-- <label><input type="radio" name="gateCard" onclick="showActorGrid(2)">特约演员出勤表</label> -->
                            <label><input type="radio" name="gateCard" value="2" onclick="showActorGrid(3)">群众演员出勤表</label>
                        </p>
                        <!-- 主要演员 -->
                        <div class="actor-information-body main-info-body">
		                        <div class="main-actor-div" id="mainActorInfo">
		                            <p class="add-row-title">
                                    <input type= "button" class="add-actor" onclick="addMainSepcialActor()">
                                </p>
		                            <div class="main-actor-header">
		                                <table class="main-actor-table" cellspacing = 0, cellpadding = 0>
		                                    <tr>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; box-sizing: border-box; padding-left: 5px;">姓名</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; box-sizing: border-box; padding-left: 5px;">角色</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center;">演员类型</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center;">到场时间</td>
		                                        <td style="width: 15%; min-width: 15%; max-width: 15%; text-align: center;">离场时间</td>
		                                        <td style="width: 25%; min-width: 25%; max-width: 25%; text-align: center;">状态</td>
		                                    </tr>
		                                </table>
		                            </div>
		                            <div class="main-actor-body">
		                                <table class="main-actor-grid" id="mainActorGrid" cellspacing = 0, cellpadding = 0>
		                                    
		                                </table>
		                            </div>
		                        </div>
		                    </div>
		                    <!-- 群众演员 -->
                        <div class="actor-information-body mass-info-body">
                            <div class="mass-actor-div" id="massActorInfo">
                                <p class="add-row-title">
                                    <input type= "button" class="add-actor" onclick="addMassActor()">
                                </p>
                                <div class="mass-actor-header">
                                    <table class="mass-actor-table" cellspacing = 0, cellpadding = 0>
                                        <tr>
                                            <td style="width: 35%; min-width: 35%; max-width: 35%; box-sizing: border-box; padding-left: 5px;">角色</td>
                                            <td style="width: 15%; min-width: 15%; max-width: 15%; box-sizing: border-box; padding-left: 5px;">数量</td>
                                            <td style="width: 25%; min-width: 25%; max-width: 25%; text-align: center;">到场时间</td>
                                            <td style="width: 25%; min-width: 25%; max-width: 25%; text-align: center;">离场时间</td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="mass-actor-body">
                                    <table class="mass-actor-grid" id="massActorGrid" cellspacing = 0, cellpadding = 0>
                                        
                                    </table>
                                </div>
                            </div>
                        </div>
		                    
                    </div>
                    <div class="btn-list">
                        <input type="button" value="保存" onclick="saveActorAttenInfo()">
                    </div>
                </div>
                
                <!-- 部门表现 -->
                <div class="shootlog-public depart-performance-div">
                    <div class="department-grade-div" id="departmentGrade">
                        <p class="pingjia-p">评价列表</p>
                        <div class="department-grade-container" id="departmentGradeCon"></div>
                    </div>
                    <div class="btn-list">
                        <input type="button" value="保存" onclick="saveDepartmentInfo()">
                    </div>
                </div>
                
                
                
                
                <!-- 特殊道具 -->
                <div class="shootlog-public special-item-div">
                    <p class="add-special-p">
                        <input type="button" class="add-special-prop" title="添加特殊道具" onclick="addSpecialProp()">
                    </p> 
                    <div class="special-information-tips">
                        <div class="special-prop-header">
                            <table class="special-item-table" cellspacing = 0, cellpadding = 0>
                                <tr>
                                    <td style="width: 30%; min-width: 30%; max-width: 30%; box-sizing: border-box; padding-left: 5px;">道具名称</td>
                                    <td style="width: 20%; min-width: 20%; max-width: 20%; text-align: center;">道具数量</td>
                                    <td style="width: 30%; min-width: 30%; max-width: 30%; box-sizing: border-box; padding-left: 5px;">摘要</td>
                                    <td style="width: 20%; min-width: 20%; max-width: 20%; text-align: center;">道具照片</td>
                                </tr>
                            </table>
                        </div>
                        <div class="special-prop-body">
                            <table class="special-prop-grid" id="specialPropGrid" cellspacing = 0, cellpadding = 0>
                                
                            </table>
                        </div>
                    </div>
                    <div class="btn-list">
                        <input type="button" value="保存" onclick="saveSpecialPropInfo()">
                    </div>
                </div>
                
                <!-- 重要备注 -->
                <div class="shootlog-public important-remark-div">
                    <p class="add-remark-container">
                        <input type="button" class="add-remark-btn" onclick="addRemark()">
                    </p>
                    <div class="important-remark-tips">
                        <div class="remark-header-div">
                            <table class="remark-header-table" cellspacing = 0, cellpadding = 0>
                                <tr>
                                    <td style="width: 70%; min-width: 70%; max-width: 70%; box-sizing: border-box; padding-left: 5px;">备注</td>
                                    <td style="width: 30%; min-width: 30%; max-width: 30%; box-sizing: border-box; padding-left: 5px;">图片/录音</td>
                                </tr>
                            </table>
                        </div>
                        <div class="remark-body-div">
                            <table class="remark-body-table" id="remarkBodyTable" cellspacing = 0, cellpadding = 0>
                                
                            </table>
                        </div>
                        <div class="btn-list">
		                        <input type="button" value="保存" onclick="saveImportantRemark()">
		                        <input type="text" style="display: none;">
		                    </div>
                    </div>
                </div>
                
                
		        </div>
		        <!-- tab页结束 -->
		        
		    </div>
    </div>
    <!-- 图片上传窗口 -->
    <div class="jqx-window" id="uploadWindow">
        <div class="">查看/上传图片</div>
        <div class="jqx-content">
            <input type="hidden" id="attpackId">
            <div class="upload-btn-container">
                <div class="select-file-btn" id="uploadFileBtn">选择图片</div>
            </div>
            <p class="upload-title">图片列表</p>
            <div class="file-list">
                <ul class="upload-file-list" id="uploadFileList">
                
                </ul>
            </div>
            <p class="upload-title video-list-p">音频列表</p>
            <div class="vedio-file-list">
                <ul class="upload-file-vedio" id="uploadFilevedio">
                
                </ul>
                <video class="play-video" src="" id="myVideo" controls></video>
            </div>
            <div class="btn-list upload-win-btn">
                <input type="button" onclick="uploadPropImg()" value="上传">
                <input type="button" onclick="closeUploadWin()" value="取消">
            </div>
        </div>
    </div>
    
    <div class="ui dimmer body" id="myLoader">
        <div class="ui large text loader">正在上传附件，请稍后...</div>
   </div>
    
  </body>
</html>
