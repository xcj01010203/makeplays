<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object isCarInfoReadonly = false;
	Object hasImportCarInfoAuth = false;
	Object hasExportCarInfoAuth = false;
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.CAR_INFO)) {
		    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.CAR_INFO) == 1){
		    	isCarInfoReadonly = true;
		    }
	    }
	    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_CARINFO) != null){
	    	hasImportCarInfoAuth = true;
	    }
	    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_CARINFO) != null){
	    	hasExportCarInfoAuth = true;
	    }
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />
	
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />

  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/car/carList.css">
  
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
	
	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=path%>/js/car/carList.js"></script>
	<script>
	    var isCarInfoReadonly = <%=isCarInfoReadonly%>;
	    var hasImportCarInfoAuth = <%=hasImportCarInfoAuth%>;
	    var hasExportCarInfoAuth = <%=hasExportCarInfoAuth%>;
	</script>
</head>

<body>
	  <div class="my-container">
	     <!-- tab页 -->
        <div class="tab-body-wrap">
            <!-- tab键容器 -->
                <div class="btn_tab_wrap">
                    <!-- tab键空白处 -->
                    <div class="btn_wrap"></div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="car_information" class="tab_li_current" onclick="showCarInfo()">车辆信息</li>
                            <li id="car_costs" onclick="showCarFinance()">油费日报</li>
                        </ul>
                    </div>
                    
                    
                </div>
        </div>
	     <!-- 车辆列表模块 -->
	     <div class="car-public car-info-modal">
	         <!-- 车辆管理主列表 -->
		       <div id="carInfoList">
		       		<!-- 按钮组 -->
		       		<div id='opreateBtn'>
		       			<input type='button' class='add-car-btn' id='addCarInfoBtn' onclick='addCarInfo()' title='添加车辆信息'>
		       			<input type='button' class='export-car' id='exportCarInfoBtn' onclick='exportCarInfo()' title='导出车辆信息'>
		       			<input type='button' class='import-btn' id='importCarInfoBtn' onclick='showImportWin()' title='导入车辆信息'>
		       			<input type='text' class='search-content-input' id='searchCarNoText' placeholder='请输入需要查询的车牌号' onkeyup='searchCarNoInfo(event)'>
		       			<div class='icon_cha1' onclick='confirmSearchCarInfo()'>
		       				<img src='../images/find.png'>
		       			</div>
		       		</div>
		       
		       	<!-- 数据列表 -->
		       	<div id="dataDiv">
		       		<table class='data-table'>
		       			<tbody id='dataTable'>
			       			<!-- 第一行 -->
			       			<tr style="line-height: 40px;background-color: #E8E8E8;text-align: center;">
			       				<td><span style='text-align: center;width: 7%;'>编号</span></td>
			       				<td><span style='text-align: center;width: 8%;'>部门</span></td>
			       				<td><span style='text-align: center;width: 9%;'>用途</span></td>
			       				<td><span style='text-align: center;width: 10%;'>车牌号</span></td>
			       				<td><span style='text-align: center;width: 8%;'>车辆类型</span></td>
			       				<td><span style='text-align: center;width: 8%;'>电话号</span></td>
			       				<td><span style='text-align: center;width: 10%;'>累计油费</span></td>
			       				<td><span style='text-align: center;width: 10%;'>累计里程</span></td>
			       				<td><span style='text-align: center;width: 10%;'>累计油量</span></td>
			       				<td><span style='text-align: center;width: 10%;'>实际油耗</span></td>
			       				<td><span style='text-align: center;width: 8%;'>状态</span></td>
			       			</tr>
		       			</tbody>
		       		</table>
		       	</div>
		       	
		       </div>
		       <!-- 添加/修改窗口 -->
		       <div class="right-popup-win" id="rightPopUpWin">
		           <div class="right-popup-body">
		               <iframe id="carInfoDetailIframe" width="100%" height="100%"></iframe>
		           </div>
		       </div>
	     </div>
	     <!-- 车辆费用模块 -->
	     <div class="car-public car-finance-modal">
	         <div class="header-btn-list">
               <input type="button" class="export-money-count" title="导出油费日报" onclick="exportCarFinance()">
           </div>
	         <div class='car-finance-div'>
	             <div class="car-finance-header">
	                 <table class="car-header-table" cellspacing=0 cellpadding=0>
	                     <tr>
	                         <td style="width: 25%; min-width: 25%; max-width: 25%;">日期
	                             <span class="select-hotel-date" id="selectDateBtn" onclick="showDatePanel(this, event)"></span>
                               <div class="hotel-date-panel" id="hotelDatePanel">
                                   <p class="date-panel-title">请输入要查询的时间段</p>
                                   <input class="start-date" type="text" id="startDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">&nbsp;&nbsp;-&nbsp;
                                   <input class="end-date" type="text" id="endDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
                                   <input type="text" style="display: none">
                                   <div class="btn-list">
                                       <input type="button" value="确定" onclick="queryDateTimeData(event)">
                                   </div>
                                   
                               </div>
	                         </td>
	                         
	                         <td style="width: 25%; min-width: 25%; max-width: 25%;">车牌号
	                             <select class="selectpicker car-no-select" id="carNoSelect"  multiple data-live-search="true" style="display: none;"></select>
	                         </td>
	                         <td style="width: 25%; min-width: 25%; max-width: 25%;">加油升数</td>
	                         <td style="width: 25%; min-width: 25%; max-width: 25%;">加油金额</td>
	                     </tr>
	                 </table>
	             </div>
	             <div class="car-finance-body">
	                 <table class="car-body-table" id="carFinanceGrid" cellspacing=0 cellpadding=0>
	                 
	                 </table>
	             </div>
	             
	         </div>
	         <div class="oil-cost-count">
               <table>
                   <tr>
                       <td style="width: 25%; min-width: 25%; max-width: 25%;"></td>
                       
                       <td style="width: 25%; min-width: 25%; max-width: 25%;"></td>
                       <td style="width: 25%; min-width: 25%; max-width: 25%; box-sizing: border-box; text-align: right; padding-right: 5px;">合计:</td>
                       <td style="width: 25%; min-width: 25%; max-width: 25%;">
                          <div class="oil-cost-cell" id="oilCostCount"></div>
                       </td>
                   </tr>
               </table>
           </div>
	     </div>
	  </div>
	  
	  <!-- 提示信息 -->
		<div id="eventAll" style="display: none;">
		      <div>
		                提示
		      </div>
		      <div>
		         <div style="margin-top: 25px;font-size: 16px;margin-left: 10px;" id="eventContent">
		                       是否确定此操作？
		         </div>
		         <div>
		           <div style=" margin: 30px 0px 0px 60px;">
		              <input type="button" id="sure" value="确定" style="margin-right: 10px;" />
		              <input type="button" id="closeBtn" value="取消" />
		           </div>
		         </div>
		       </div>
		</div>

	 <!--导入窗口  -->
	  <div id="importExportCarDetailWin" class="jqx-window" style="display: none;">
				<div>导入</div>
					<div>
					    <iframe id="importIframe" width="100%" height="100%"></iframe>
				</div>
	  </div>
</body>
</html>