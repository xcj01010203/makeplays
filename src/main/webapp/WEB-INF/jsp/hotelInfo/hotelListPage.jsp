<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isHotelListReadonly = false;
Object hasExportInhotelCostAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_HOTEL)) {
	    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_HOTEL) == 1){
	    	isHotelListReadonly = true;
	    }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_INHOTEL_COST) != null){
        hasExportInhotelCostAuth = true;
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/hotelList/hotelList.css">

<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.css">
  
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>  
<script type="text/javascript" src="<%=basePath %>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/hotelList/hotelList.js"></script>
<script type="text/javascript">
	var isHotelListReadonly = <%=isHotelListReadonly%>;
	var hasExportInhotelCostAuth = <%=hasExportInhotelCostAuth%>;
</script>
<title></title>
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
                            <li id="hotel_information" class="tab_li_current" onclick="showHotelInfo()">入住登记</li>
                            <li id="lodging_costs" onclick="showHotelFinance()">住宿费用</li>
                        </ul>
                    </div>
                    
                    
                </div>
        </div>
    
        <!-- 住宿管理模块 -->
        <div class="hotel-public hotel-manage-div">
            <div class="header-btn-list">
            <input type="button" title="添加" id='addNewHotel' onclick="addHotelInfo()">
		        </div>
		        <!-- 主列表 -->
		        <div class="hotel-main-grid">
		            <div id="hotelMainGrid"></div>
		        </div>
		        <!-- 滑动窗口 -->
		        <div class="right-popup-win" id="rightPopUpWin">
		           <div class="right-popup-body">
		               <iframe id="hotelDetailIframe" width="100%" height="100%"></iframe>
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
        </div>
        <!-- 住宿费用模块 -->
        <div class="hotel-public hotel-finance-div">
            <div class="operate_box">
				         <div class="export_button" id="exportInhotelCostBtn"  title='导出' onclick='exportExcel();' ></div>
				    </div>
				    <div class="hotel-cost-container">
				        <!-- 表格主容器 -->
				        <div class="table-main-container">
				            <!-- 表格表头 -->
				            <div class="table-main-header-div">
				                <table class="table-main-header" id="tableMainHeader" cellspacing=0 cellpadding=0>
				                    <tr>
				                        <th style="width: 17%; min-width: 17%;" class="hotel-date">日期
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
				                            <!-- <select class="selectpicker hotel-date-select" id="hotelDateSelect"  multiple data-live-search="true" style="display: none;"></select> -->
				                            <!-- <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'sans-serif';" class="clearSelection" onclick="clearAtmoSelection(this)">[清空]</a>  -->
				                        </th>
				                        <th style="width: 20%; min-width: 20%;" class="hotel-name">宾馆名称
				                            <select class="selectpicker hotel-name-select" id="hotelNameSelect"  multiple data-live-search="true" style="display: none;"></select>
				                            <!-- <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSiteSelection(this)">[清空]</a>  -->
				                        </th>
				                        <th style="width: 15%; min-width: 15%;" class="people-num">人数</th>
				                        <th style="width: 16%; min-width: 16%;" class="room-num">房间数</th>
				                        <th style="width: 16%; min-width: 16%;" class="average-room-cost">平均房价</th>
				                        <th style="width: 16%; min-width: 16%;" class="account-cost">总费用</th>
				                    </tr>
				                </table>
				            </div>
				            <!-- 表格表体 -->
				            <div class="table-main-body-div">
				                <table class="table-main-body" id="tableMainBody" cellspacing=0 cellpadding=0>
				                    
				                </table>
				            </div>
				            
				            <!-- 合计 -->
				            <div class="account-div">
				                <table>
				                    <tr>
				                        <td style="width: 12%; min-width: 12%;" class="hotel-date"></td>
				                        <td style="width: 15%; min-width: 15%;" class="hotel-name"></td>
				                        <td style="width: 10%; min-width: 10%;" class="people-num"></td>
				                        <td style="width: 11%; min-width: 11%;" class="room-num"></td>
				                        <td style="width: 11%; min-width: 11%; text-align: right; padding-right: 25px;">合计:</td>
				                        <td style="width: 11%; min-width: 11%;"><div style="text-align: right; padding-right: 25px;" id="accountMoney"></div></td>
				                    </tr>
				                </table>
				            </div>
				            
				        </div>
				        <div class="contact-right-win" id="contactRightWin">
				            <div class="win-btn-list">
				              <input type="button" value="关闭" onclick="closePopUpWin()">
				              <input type="text" style="display: none;">
				            </div>
				            <div class="scence-content" id="scenceContentDiv">
				                   <!-- 显示单个联系人入住详情 -->
				                    <!-- <div class="hidden-tag" id="showInHotelCostDetailInfoWindow" > -->
				                      
				                      <div class="inhotel-title" id="inhotelTitle"></div>
				                      <div class="inhotel-grid">
				                           <div id="jqxgrid"></div>
				                      </div>
				                    <!-- </div> -->
				              </div>
				        </div>
				    </div>
        </div>        
    </div>
</body>
</html>