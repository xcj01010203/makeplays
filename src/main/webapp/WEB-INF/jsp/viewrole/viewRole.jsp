<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isRoleReadonly = false;     //角色表是否只读
Object hasDeleteViewRoleBatchAuth = false;
Object hasExportRoleAuth = false;

Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.ROLE_VIEW)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.ROLE_VIEW) == 1){
            isRoleReadonly = true;
        }
    }
    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.DELETE_VIEWROLE_BATCH)){
        hasDeleteViewRoleBatchAuth = true;
    }
    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_VIEWROLE)){
    	hasExportRoleAuth = true;
    }
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="<%=basePath%>/css/viewRole.css" type="text/css" />
<link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css">

<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=basePath%>/js/easy-ui/easyui.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>/js/easy-ui/icon.css" type="text/css"></link>
<!-- bootstrap JS -->
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
 <!-- 实现表格的拖动 -->
<script type="text/javascript" src="<%=basePath%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/easy-ui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/easy-ui/datagrid-dnd.js"></script>

<script type="text/javascript" src="<%=basePath%>/js/echarts/echarts-all.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/report/grade.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/viewrole/produceProgressBar.js"></script>

<!-- 拖拽 -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
<script>
var isRoleReadonly = <%=isRoleReadonly %>;
var hasDeleteViewRoleBatchAuth = <%=hasDeleteViewRoleBatchAuth %>;
var hasExportRoleAuth = <%=hasExportRoleAuth%>;
</script>
<script type="text/javascript" src="<%=basePath%>/js/viewrole/viewRole.js"></script>
</head>
<body>
    <div class="my-container">
        <div class="toolbar" id="toolbar">
             <input type='button' class='advance-search-btn' id='advarceSearchBtn' onclick='openAdvanceSearch()' title="高级搜索">
             <input type='button' class='create-role-btn' id='createRoleBtn' onclick='addViewRoleInfo()' title="创建角色">
             <input type='button' class='make-role-toone-btn' id='makeRoleToOneBtn' onclick='showMakeRoleToOne()' title="统一角色名称">
             <div id='setRoleTypeDiv' class='set-roletype-btn set-roletype-div' title="设置角色类型">
               <div class='jqx-tree-drop' id='jqxTreeDrop'>
                 <ul style='display:block;'>
                   <li  value='1'>主要演员</li>
                   <li  value='2'>特约演员</li>
                   <li  value='3'>群众演员</li>
                   <li  value='4'>待定</li>
                 </ul>
               </div>
             </div>
             <input type='button' class='delete-view-list' id='deleteViewRoleBatch' onclick='deleteViewRoleBatch()' title="批量删除">
             <input type='button' class='export-view-btn' id='exportBtn' onclick='downLoadSence()' title="导出角色场景表">
             <input type='button' class='export-btn' id='exportRoleBtn' onclick='downLoadRoleTab()' title="导出角色表">
             <!--  <div id='setAttentionRoleDiv' class='set-attention-btn set-attention-role-div' title="设置关注角色">
               <div class='jqx-tree-drop' id='attentionJqxTreeDrop'>
                 <ul style='display:block;'>
                   <li  value='1'>关注角色</li>
                   <li  value='0'>取消关注</li>
                 </ul>
               </div>
             </div> -->
         </div>
         
         <!-- 标题头的table -->
         <div class="title-table-div">
	          <table class='title-table' cellspacing = 0, cellpadding = 0>
	         	<tr>
	         		<td style='width:3%; min-width: 3%; max-width: 3%;'><input type='checkbox' id='checkAll' onclick="checkAllRoles()"></td>
	         		<td style='width:10%; min-width: 10%; max-width: 10%; text-align: left;'>角色名称</td>
	         		<td style='width:4%; min-width: 64%; max-width: 4%;'>简称</td>
	         		<td style='width:8%; min-width: 8%; max-width: 8%;'>演员类型</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>演员姓名</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>首次出场</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>场</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>页</td>
	         		<!-- <td style='width:8%; min-width: 8%; max-width: 8%;'>已完成场数</td>
	         		<td style='width:8%; min-width: 8%; max-width: 8%;'>未完成场数</td> -->
	         		<td style='width: 10%; min-width: 10%; max-width: 10%;'>完成/总场数</td>
	         		<td style='width:7%; min-width: 7%; max-width: 7%;'>入组时间</td>
	         		<td style='width:7%; min-width: 7%; max-width: 7%;'>离组时间</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>在组天数</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>工作时长</td>
	         		<td style='width:6%; min-width: 6%; max-width: 6%;'>休息时长</td>
	         		<td style='width:9%; min-width: 9%; max-width: 9%;'>请假记录</td>
	         		<!-- <td style='width:16px;'></td> -->
	         	</tr>
	         </table>
         </div>
        <div class="view-role-div" id="viewRoleDiv">
        
            <!-- <div id="viewRoleListGrid"></div> -->
            <table class='view-role-list-table' id="viewRoleListGrid" cellspacing = 0, cellpadding = 0>
            
            </table>
            
            
        </div>
        <!-- 角色数量统计 -->
       <div class="view-role-count">
          <table cellspacing = "0" cellpadding="0" id="viewRoleCountTable">
              <tr>
                  <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="viewRoleCountNum"></div></td>
              </tr>
          </table>
       </div>
        
        <!-- 高级查询窗口 -->
        <div class="jqx-window my-window" id="advanceSearchWin">
            <div>条件查询</div>
            <div>
                <ul>
                    <li>
                        <p>角色名称:</p>
                        <input type="text" id="queryViewRoleName">
                    </li>
                    <li>
                        <p>演员类型:</p>
                        <select id="queryViewRoleType">
                            <option value="">全部演员</option>
                            <option value="1">主要演员</option>
                            <option value="2">特约演员</option>
                            <option value="3">群众演员</option>
                            <option value="4">待定</option>
                        </select>
                    </li>
                    <li>
                        <p>场数:</p>
                        <input type="text" class="field" id="queryMinViewCount" onkeyup="this.value=this.value.replace(/\D/g,'')">
                        <span class="to">到</span>  
                        <input type="text" class="last-field" id="queryMaxViewCount" onkeyup="this.value=this.value.replace(/\D/g,'')">
                    </li>
                    <li>
                        <p>完成:</p>
                        <input type="text" class="field-percent" id="queryMinFinished" onkeyup="this.value=this.value.replace(/\D/g,'')">
                        <span class="percent">%</span> 
                        <span class="to">到</span>  
                        <input type="text" class="last-field-percent" id="queryMaxFinished" onkeyup="this.value=this.value.replace(/\D/g,'')">
                        <span class="percent">%</span> 
                    </li>
                    
                </ul>
                <div class="win-btn-list-div">
                    <input type="button" value="确定" onclick="advanceQuery()">
                    <input type="button" value="取消" onclick="cancelQuery()">
                    <input type="button" value="清空" onclick="clearQuery()">
                </div>
            </div>
        </div>
        
        <!-- 创建角色窗口 /修改角色窗口-->
        
        <div class="jqx-window my-window" id="viewRoleDetail">
            <div>创建角色</div>  
            <div>
                <ul>
                    <li class="view-role-name">
                        <p><span>*</span>角色名称:</p>
                        <input id="viewRoleName" type= "text" onblur="isAddRoleNameEmpty()" onfocus="clearAddNameTips()" >
                        <!-- viewRoleId-新增时为空，修改时必有 -->
                        <input type="hidden" id="viewRoleId">
                        <!-- viewRoleId-新增时为空，修改时必有 -->
                        <!-- <input type="hidden" id="actorId"> -->
                    </li>
                    <li class="view-role-tips"><span class="tips1">角色名称不能为空</span></li>
                    <li>
                        <p>角色简称:</p>
                        <input id="shortName" type= "text" placeHolder="一个汉字的简称">
                    </li>
                    <li class="view-role-type">
                        <p><span>*</span>演员类型:</p>
                        <select id="viewRoleType" onblur="isAddRoleTypeEmpty()" onfocus="clearAddTypeTips()">
                            <option value= "" selected= "selected">--请选择--</option>
                            <option value= "1">主要演员</option>
                            <option value= "2">特约演员</option>
                            <option value= "3">群众演员</option>
                        </select>
                    </li>
                    <li class="view-role-tips"><span class="tips2">演员类型不能为空</span></li>
                    <li>
                        <p>演员姓名:</p>
                        <input id="actorName" type="text">
                    </li>
                    <li>
                        <p>入组时间:</p>
                        <input id="enterDate" type="text" readonly onfocus="WdatePicker({readOnly:true,startDate:''})">
                    </li>
                    <li>
                        <p>离组时间:</p>
                        <input id="leaveDate" type="text" readonly onfocus="WdatePicker({readOnly:true,startDate:''})">
                    </li>
                    <!--<li class="tips3">离组日期小于入组日期</li>  -->
                    <!-- <li>
                        
                    </li> -->
                </ul>
                <div class="win-btn-list-div">
                            <input type="button" value="确定" onclick="createRole(this)">
                            <input type="button" value="取消" onclick="cancelCreateRole()">
                </div>
            </div>      
        </div>
        
        <!-- 统一角色名称窗口 -->
        <div class="jqx-window my-window" id="MakeRoleToOne">
            <div>统一角色名称</div>
            <div>
                <ul>
                    <li class="view-role-name">
                        <p><span>*</span>角色名称:</p>
                        <input id="makeViewRoleName" type= "text" onblur="isMakeRoleNameEmpty()" onfocus="clearMakeNameTips()">
                    </li>
                    <li class="view-role-tips"><span class="tips1">角色名称不能为空</span></li>
                    <li>
                        <p>角色简称:</p>
                        <input id="makeShortName" type= "text" placeHolder="一个汉字的简称">
                    </li>
                    <li class="view-role-type">
                        <p><span>*</span>演员类型:</p>
                        <select id="makeViewRoleType" onblur="isMakeRoleTypeEmpty()" onfocus="clearMakeTypeTips()">
                            <option value="" selected= "selected">--请选择--</option>
                            <option value= "1">主要演员</option>
                            <option value= "2">特约演员</option>
                            <option value= "3">群众演员</option>
                        </select>
                    </li>
                    <li class="view-role-tips"><span class="tips2">演员类型不能为空</span></li>
                    <li>
                        <p>演员姓名:</p>
                        <input id="makeActorName" type="text">
                    </li>
                    <li>
                        <p>入组时间:</p>
                        <input id="makeEnterDate" type="text" readonly onfocus="WdatePicker({readOnly:true,startDate:''})" onchange="showShootDays('make')">
                    </li>
                    <li>
                        <p>离组时间:</p>
                        <input id="makeLeaveDate" type="text" readonly onfocus="WdatePicker({readOnly:true,startDate:''})" onchange="showShootDays('make')">
                    </li>
                    <li>
                        <p>在组天数:</p>
                        <input id="makeShootDays" type="text" onkeyup="onlyNumber(this)">
                    </li>
                    <!--<li class="tips3">离组日期小于入组日期</li>  -->
                    <li>
                        <div class="win-btn-list-div">
                            <input type="button" value="确定" onclick="unifiedRoleName()">
                            <input type="button" value="取消" onclick="cancelUnifiedRole()">
                        </div>
                    </li>
                </ul>
            </div>      
        </div>
       
       
        <!-- 请假列表 -->
        <!-- <div class="jqx-window my-window" id="setLeaveRecord">
            <div>请假设置</div>
            <div>
                <ul>
                    <li>
                        <h4 class="add-leave-explain">添加请假记录</h4>
                        <input class="add-leave-start" id="leaveStartDate" type="text"  readonly onfocus="WdatePicker({isShowClear:false,readOnly:true})">
                        <span class="to">到</span>
                        <input class="add-leave-end" id="leaveEndDate" type="text" readonly onfocus="WdatePicker({isShowClear:false,readOnly:true})">
                        <div class="win-btn-list-div btn-list-div-leave">
                            <input type="button" value="添加" onclick="addLeaveDate()">
                            演员Id
                            <input type="hidden" id="actorId">
                        </div>
                    </li>
                </ul>
                <div class="jqx-leave-date" id="jqxGridLeaveDate"></div>                
            </div>
        </div>   -->   
        
        
        <!-- 戏量统计窗口 -->
        <!-- <div class="jqx-window my-window" id="roleViewPlayStat"> -->
        <div class="rightdiv" id="roleViewPlayStat">
            <input type="hidden" id="viewRoleGridId">
            <input type="hidden" id="viewRoleGridName">
            <input type="hidden" id="modifyActorId">
            <div class="right-popup-header">
              <table cellspacing="0" cellpadding="0">
                    <tbody><tr>
                      <td class="popup-title">
                        <span class="right-popup-title">角色信息</span>
                      </td><td class="popup-btn-con">
                        <input class="right-popup-close" type="button" value="关闭" onclick="closeRightPopup();">
                    </td></tr>
                  </tbody></table>
            </div>
            <div class="tab-body-wrap">
                <!-- tab键容器 -->
                <div class="btn_tab_wrap">
                    <!-- tab键空白处 -->
                    <div class="btn_wrap"></div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="tab_0" class="tab_li_current">角色信息</li>
                            <li id="tab_1">场景戏量分布</li>
                            <li id="tab_2">分集戏量分布</li>
                            <li id="tab_3_viewGrid">角色场景表</li>
                        </ul>
                    </div>
                </div>
               
                <div class="play-count-general" id="playGeneralDetail">
                  <div class="play-count-radio">
                  <label><input type="radio" name="roleTotalRadio" value="1" checked onclick="showRoleTotal(1)">场 </label>&nbsp;&nbsp;
                  <label><input type="radio" name="roleTotalRadio" value="2" onclick="showRoleTotal(2)">页</label>
                  </div>
                  
                  
                </div>
                
                <div class="danju danju0">
                    <div class="title-div">
                        <div class="border-left"></div>
                        <p class="danju-title">保存 / 修改角色信息</p>
                    </div>
                    <div class="viewrole-info-div">
				                <table class="view-info-table">
				                    <tr>
				                        <td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p><span style="color: #f00;">*</span>角色名称:</p>
				                            <input id="modifyViewRoleName" type= "text" onblur="isRoleNameEmpty(this)" onfocus="clearRoleNameTips(this)" >
				                            <span class="tips1">角色名称不能为空</span>
				                        </td>
				                        <td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p>角色简称:</p>
                                    <input id="modifyShortName" type= "text" placeHolder="一个汉字的简称">
				                        </td>
				                        <td>
				                            <p><span style="color: #f00;">*</span>演员类型:</p>
		                                <select id="modifyViewRoleType" onblur="isRoleTypeEmpty(this)" onfocus="clearRoleTypeTips(this)">
		                                    <option value= "" selected= "selected">--请选择--</option>
		                                    <option value= "1">主要演员</option>
		                                    <option value= "2">特约演员</option>
		                                    <option value= "3">群众演员</option>
		                                </select>
		                                <span class="tips2">演员类型不能为空</span>
				                        </td>
				                    </tr>
				                    <tr>
				                        <td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p>演员姓名:</p>
                                    		<input id="modifyActorName" type="text">
				                        </td>
				                        <td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p>在组时间:</p>
                                    		<input id="modifyEnterDate" type="text" class="date1" readonly onfocus="WdatePicker({readOnly:true,startDate:''})" onchange="showShootDays('modify')">
                                    		&nbsp;至&nbsp;
                                    		<input id="modifyLeaveDate" type="text" class="date2" readonly onfocus="WdatePicker({readOnly:true,startDate:''})" onchange="showShootDays('modify')">
				                        </td>
				                        <td>
					                    	<p>在组天数:</p>
					                    	<input type="text" id="modifyShootDays" onkeyup="onlyNumber(this)">
				                        </td>
				                    </tr>
				                    
				                    <tr>
				                    	<td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p>工作时长:</p>
                                    		<input id="modifyWorkHours" type="text" placeholder="演员每天工作时长（小时）" onkeyup="onlyNumber(this)">
				                        </td>
				                        <td style="width: 33.3%; min-width: 33.3%; max-width: 33.3%;">
				                            <p>休息时长:</p>
                                    		<input id="modifyRestHours" type="text" placeholder="演员每天休息时长（小时）" onkeyup="onlyNumber(this)">
				                        </td>
				                    </tr>
				                </table>
				                <div class="save-btn-list-div">
				                            <input type="button" id="saveModifyRole" value="保存角色" onclick="saveModifyRole(this)">
				                            <!-- <input type="button" value="取消" onclick="cancelCreateRole()"> -->
				                </div>
				            </div>
				            
				            <!-- 分隔线 -->
				            <div class="div-line"></div>
				            
				            <div class="title-div">
				                <div class="border-left"></div>
				                <p class="danju-title">请假记录</p>
				            </div>
				            <div class="leave-record-tips" id="leaveRecordTips">
				                <p class="leave-tips-p">为角色确定演员后，可记录相关请假信息！</p>
				            </div>
				            <div class="leave-record-div" id="leaveRecordDiv">
				                <ul class="leave-record-ul">
				                    <li>
				                        <!-- <h4 class="add-leave-explain">添加请假记录</h4> -->
				                        <input class="add-leave-start" id="leaveStartDate" type="text"  readonly onfocus="WdatePicker({isShowClear:false,readOnly:true})">
				                        <span class="to">至</span>
				                        <input class="add-leave-end" id="leaveEndDate" type="text" readonly onfocus="WdatePicker({isShowClear:false,readOnly:true})">
				                        <input class="add-record-btn" id="setLeaveButton" type="button" value="请假" onclick="addLeaveDate(this)">
				                            <!-- 演员Id -->
				                            <!-- <input type="hidden" id="actorId"> -->
				                        
				                    </li>
				                </ul>
				                <div class="jqx-leave-date" id="jqxGridLeaveDate"></div> 
				            </div>
				                  
                </div>
                
                <div class="danju danju1">
                    <span id="viewspan" style="padding-left: 15px;"></span>
                  <!-- 进度条 -->
                  <div class="my-progress-container" id="accountTheView">
                     <div class="progress-con-div">
                         <p id="viewCount">场数</p>
                         <div class="progress-div">
                             <div id="viewCountProgress"><span id="viewCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                     </div>
                     <div class="progress-con-div">
                         <p id="staticView">内景</p>
                         <div class="progress-div">
                             <div id="neiProgress"><span id="neiCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="waiProgress"><span id="waiCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="outsideView">外景</p>
                     </div>
                     <div class="progress-con-div">
                         <p id="dayView">日戏</p>
                         <div class="progress-div">
                             <div id="dayProgress"><span id="dayCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="nightProgress"><span id="nightCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="nightView">夜戏</p>
                     </div>
                     <!-- <div class="progress-con-div">
                         <p id="literateView">文戏</p>
                         <div class="progress-div">
                             <div id="wenProgress"><span id="wenCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="wuProgress"><span id="wuCountFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="kongfuView">武戏</p>
                     </div> -->
                     
                     
                  </div>
                  
                  
                  
                  <span id="pagespan" style="display: none; padding-left: 15px;"></span>
                  <!-- 进度条 -->
                  <div class="my-progress-container" id="accountThePage" style="display: none;">
                      <div class="progress-con-div">
                         <p id="viewPage">页数</p>
                         <div class="progress-div">
                             <div id="viewPageProgress"><span id="viewPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                      </div>
                      <div class="progress-con-div">
                         <p id="staticPage">内景</p>
                         <div class="progress-div">
                             <div id="neiPageProgress"><span id="neiPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="waiPageProgress"><span id="waiPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="outsidePage">外景</p>
                      </div>
                      <div class="progress-con-div">
                         <p id="dayPage">日戏</p>
                         <div class="progress-div">
                             <div id="dayPageProgress"><span id="dayPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="nightPageProgress"><span id="nightPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="nightPage">夜戏</p>
                      </div>
                      <!-- <div class="progress-con-div">
                         <p id="literatePage">文戏</p>
                         <div class="progress-div">
                             <div id="wenPageProgress"><span id="wenPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                             <div id="wuPageProgress"><span id="wuPageFlag"><img src="../images/roleform/icon_jindu_s(1).png"></span></div>
                         </div>
                         <p id="kongfuPage">武戏</p>
                      </div> -->
                      
                  </div>
                
                
                
                    <table class="play-count-title">
                        <thead>
                            <tr>
                                <td class="play-count-td">
                                    <div class="play-td-div">拍摄地点/戏量</div>
                                </td>
                                <td class="play-count-td">
                                    <div class="play-td-div">主场景</div>
                                </td>
                                <td class="play-count-td">
                                    <div class="play-td-div">场数</div>
                                </td>
                                <td class="play-count-td">
                                    <div class="play-td-div">页数</div>
                                </td>
                                <td class="play-count-td">
                                    <div class="play-td-div">气氛</div>
                                </td>
                            </tr>
                        </thead>
                    </table>
                     <!-- 表格显示数据 -->
                    <div class="play-count-list">
                          <div id="playCountDetail"></div>                      
                    </div>
                    
                    
                </div>
                
                <div class="danju danju2">
                    <div class="play-count-tips">
                                                                                    参演集数:<span class="actorNumber"></span>
                        <!-- <div class="play-count-radio">
                            <input id="radio_1" name="statisticsType" type="radio" checked value="1">场&nbsp;&nbsp;&nbsp;
                            <input id="radio_2" name="statisticsType" type="radio" value="2">页 
                        </div> -->
                    </div>
                    <div class="echar-show-data">
                        <div class="echar-play-count" id="echarPlayView"></div>
                        <div class="echar-play-count echar-play-page" id="echarPlayPage"></div>
                    </div>
                   
                </div>
                
                
                <div class="danju danju3" id="viewGridPage" style="height: calc(100% - 25px);">
                
                    <iframe src="" name="f_scene_list" id="fViewList" frameborder="0" width="100%" height="100%"></iframe>
                </div>
                
            </div>
        </div>
        
        
        <!-- 演员评价 -->
        <div class="jqx-window" id="actroEvaluateWindow">
            <div>演员评价</div>
            <div class="actor-evaluate" id="actorEvaluate">
                <div class="wrap">
                    <ul class="content">
                        <li>
                            <label class="lh45">评价:</label>
                            <input type="hidden" id="actorIdEvaluate">
                            <ul class="grade-star">
                                <li>
                                    <span class="star-left"></span><span class="star-right"></span>
                                    <span class="star-info">很差</span>
                                </li>
                                <li>
                                    <span class="star-left"></span><span class="star-right"></span>
                                    <span class="star-info">差</span>
                                </li>
                                <li>
                                    <span class="star-left"></span><span class="star-right"></span>
                                    <span class="star-info">一般</span>
                                </li>
                                <li>
                                    <span class="star-left"></span><span class="star-right"></span>
                                    <span class="star-info">好</span>
                                </li>
                                <li>
                                    <span class="star-left"></span><span class="star-right"></span>
                                    <span class="star-info">非常好</span>
                                </li>
                            </ul>
                            <label class="lh45">得分:</label><span class="grade-df">0</span>
                        </li>
                        <li class="yx">
                            <label>印象:</label>
                            <ul class="grade-impression best">
                              <!-- js加载数据 -->                         
                            </ul>
                            <div class="hr"></div>
                            <ul class="grade-impression bad">
                               <!-- js加载数据 -->    
                            </ul>
                        </li>
                        <li class="py">
                            <label>评语:</label>
                            <textarea class="grade-py"></textarea>
                        </li>
                    </ul>
                    <div class="win-btn-list-div actor-evaluate-btn">
                        <input type="button" id="actorEvaluateSubmit" value="提交" onclick="actorEvaluateButton()">
                        <input type="button" id="actorEvaluateCencle" value="取消">
                    </div>
                </div>
            </div> 
        </div>
        
        
        <!-- 角色场景表 -->
        <!-- <div id="actroCeneWindow">
            <div>角色场景表</div>
            <div>
                <iframe src="" name="f_scene_list" id="fViewList" frameborder="0" width="100%" height="100%">
                </iframe>
            </div>
        </div> -->
        
        <!-- 导出 -->
        <div id="loadingDiv" class="show-loading-container">
            <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
        </div>
        
    </div>
    
    <!-- 正在加载 -->
	<div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;">
	</div>
	<div id="loadingDataDiv" class="show-loading-container" style="display: none;">
		<div class="show-loading-div"> 正在加载数据，请稍候... </div>
	</div>
</body>
</html>