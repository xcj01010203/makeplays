<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object isCarInfoReadonly = false;
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.CAR_INFO)) {
		    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.CAR_INFO) == 1){
		    	isCarInfoReadonly = true;
		    }
	    }
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/car/carDetailList.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
	
	<script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
	
	<script src="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=path%>/js/car/carDetailList.js"></script>
	  <script type="text/javascript" src="<%=path%>/js/bootstrap/bootstrap-select.js"></script>
    <script type="text/javascript" src="<%=path%>/js/bootstrap/bootstrap.min.js"></script>
	
	<script>
	    var isCarInfoReadonly = <%=isCarInfoReadonly%>;
	</script>	
 
</head>

<body>
	  <div class="container-coment">
	     <input type="hidden" id="carId" value="${aimCarId}">
	     <input type="hidden" id="crewId">
	     <input type="hidden" id="isExist">
	     <div class="header-btn-list">
	         <input type="button" id="saveBtn" value="确定" onclick="saveOneCarInfo()">
	         <input type="button" id ="deleteBtn" value="删除" onclick="deleteCarInfo()">
	         <input type="button" value="关闭" onclick= "closeRightWin()">
	     </div>
	     <div class="main-content">
	         <div class="form-list">
	             <ul>
	                 <li>
	                     <p>编号&nbsp;:</p>
	                     <input type="text" class='carNo-text' id="carNo" onkeyup="onlyNumber(this)">
	                     
	                     <div class="check-div">
	                         <div class="ui toggle checkbox">
		                          <span class="font-color" id="noHasGroup">离组</span>
		                          <input type="checkbox" checked name="public" id="isGroup" onclick="isHasGroup(this)">
		                          <label id="hasGroup">在组</label>
		                        </div>
	                     </div>
	                 </li>
	                 <li>
	                     <p>型号&nbsp;:</p>
	                     <input type="text" id="carModel">
	                 </li>
	                 <li>
	                     <p>车牌&nbsp;:</p>
	                     <input type="text" id="carNumber">
	                 </li>
	             </ul>
	             <ul>
	                 <li>
	                     <p>司机&nbsp;:</p>
	                     <input type="text" id="driver">
	                 </li>
	                 <li>
                       <p>身份证</p>
                       <input type="text" id="identityNum">
                   </li>
	                 <li>
	                     <p>电话&nbsp;:</p>
	                     <input type="text" id="phone">
	                 </li>
	                 
	             </ul>
	             <ul>
	                 <li>
	                     <p>入组日期</p>
	                     <input type="text" id="enterDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
	                 </li>
	                 
	                 <li>
                       <p>部门&nbsp;:</p>
                       		<input type='text' id='departmentText'  class='department-text' onclick="showDepartment(event)" onkeyup='searchDepartmentName(this)'>
                   </li>
	                 
	                 <li>
                       <p>用途&nbsp;:</p>
                       <input type="text" id="useFor" >
                   </li>
	             </ul>
	             
	         </div>
	         
	         <!-- 部门下拉框 -->
	         <ul id='department' class='department-ul' >
	         </ul>
	         
	         <!-- 加油登记表 -->
	         <div class="add-oil-div">
	             <div class="title">
	                 <h5>加油登记表</h5>
	                 <input type="button" class='add-car-btn' title="添加加油信息" onclick="addNewRecord()">
	                 <!-- <input type="button" class='export-car' title="导出加油信息" onclick="exportRecord()">
	                 <input type="button" class='import-btn' title="导入加油信息" onclick="showWorkImportWin()"> -->
	             </div>
	             <!-- 可编辑表格 -->
	             <div class="count-tips">
	                 <table class="count-table" id="countTable" cellspacing = 0, cellpadding = 0>
	                     <tr style="height: 0px;">
	                         <td style="width: 8%; max-width: 8%; min-width:8%;"></td>
	                         <td style="width: 12%; max-width: 12%; min-width:12%;"></td>
	                         <td style="width: 20%; max-width: 20%; min-width:20%;"></td>
	                         <td style="width: 15%; max-width: 15%; min-width:15%;"></td>
	                         <td style="width: 15%; max-width: 15%; min-width:15%;"></td>
	                         <td></td>
	                         <td style="width: 15%; max-width: 15%; min-width:15%;"></td>
	                     </tr>
	                     <tr>
	                         <td colspan = "3" style="text-align: right;">合计:</td>
	                         <td>
	                             <span id="kilometersCount"></span>
	                         </td>
	                         <td>
	                             <span id="oilLitresCount"></span>
	                         </td>
	                         <td>
	                             <span id="oilTotalMoney"></span>
	                         </td>
	                     </tr>
	                 </table>
	                 <div class="detail-table-div">
	                     <div class="detail-header-div">
	                         <table class="detail-table-header" id="detailTable" cellspacing = 0, cellpadding = 0>
				                       <tr>
				                          <td style="width: 8%; max-width: 8%; min-width:8%;">编号</td>
				                          <td style="width: 12%; max-width: 12%; min-width:12%;">日期</td>
				                          <td style="width: 15%; max-width: 15%; min-width:15%;">开工里程表数</td>
				                          <td style="width: 15%; max-width: 15%; min-width:15%;">收工里程表数</td>
				                          <td style="width: 10%; max-width: 10%; min-width:10%;">公里数</td>
				                          <td style="width: 10%; max-width: 10%; min-width:10%;">加油升数</td>
				                          <td>加油金额</td>
				                          <td style="width: 15%; max-width: 15%; min-width:15%;">备注</td>
				                       </tr>
				                   </table>
	                     </div>
	                     <div class="detail-body-div">
	                         <table class="detail-table-body" id="detailTableData">
	                             <tr class="blank-tr">
	                                 <td style="text-align: center;" colspan="6">暂无数据</td>
	                             </tr>
	                         </table>
	                     </div>
	                 </div>
	                 
	             </div>
	         </div>
	         
	         
	     </div>
	  </div>
	  
	  <!--导入窗口  -->
  <div id="importExportCarWorkWin" class="jqx-window" style="display: none;">
	<div>导入</div>
		<div>
		    <iframe id="importIframe" width="100%" height="100%"></iframe>
	</div>
 </div>
</body>
</html>