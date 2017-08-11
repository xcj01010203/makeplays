<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isHotelInfoReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_HOTEL)) {
	    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_HOTEL) == 1){
	    	isHotelInfoReadonly = true;
	    }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/hotelList/hotelDetail.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.css">

<script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>

<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script>

<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=kkz9NgUHHA0yVwnYfgBbqpiB"></script>
<script type="text/javascript" src="<%=basePath %>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/hotelList/hotelDetail.js"></script>

<script type="text/javascript">
	var isHotelInfoReadonly = <%=isHotelInfoReadonly%>;
</script>
</head>
<body>
    <div class="container">
      <input type="hidden" id="hotelId" value="${hotelId}">
      <div class="win-header-btn">
        <input type="button" value="确定" id='confirmSaveHotelInfo' onclick="saveHotelInfo()">
        <input type="button" style="display: none;" id="delHotelBtn"  value="删除" onclick="deleteHotelInfo()">
        <input type="button" value="关闭" onclick="closeCheckRightWin()">
      </div>
      <div class="hotel-detail-info" id="hotelDetailInfo">
        <div class="hotel-basic-info">
          <ul class="basic-info-ul">
            <li>
              <p>酒&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;店:</p>
              <input type="text" id="hotelName">
            </li>
            <li>
              <p>位&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;置:</p>
              <input  type="text" id="location" log="" lat="">
              <span class="location-span" onclick="showMapWindow()"></span>
            </li>
            <li>
              <p>酒店电话:</p>
              <input type="text" id="hotelNum">
            </li>
          </ul>
          <ul class="basic-info-ul">
            <li>
              <p>房间数量:</p>
              <input type="text" id="roomNum" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
            </li>
            <li>
              <p>联&nbsp;&nbsp;系&nbsp;人:</p>
              <input type="text" id="contacts">
            </li>
            <li>
              <p>联系电话:</p>
              <input type="text" id="phone">
            </li>
          </ul>
          <ul class="basic-info-ul">
            <li>
              <p>报价说明:</p>
              <textarea id="priceDescription"></textarea>
            </li>
          </ul>
        </div>
        
        <div class="hotel-people-detial">
          <div class="detail-people-header">
            <span>入住人员</span>
            <input class="add-hotel-people" id='addCheckinPeople' type="button" title="添加" onclick="addHotePeople()">
            <input class="repeat-set-btn" type="button" title="批量设置" onclick="showRepeatSetList(this, event)">
            <!-- <input class="set-start-date" type="button"  title="统一设置入住时间" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){reSetStartTime(dp.cal.getNewDateStr(), dp.el)}})">
            <input class="set-end-date" type="button" title="统一设置退房时间" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){reSetEndTime(dp.cal.getNewDateStr(), dp.el)}})"> -->
            <div class="repeat-set-list" id="repeatSetList" onclick="preventPopEvent(event)">
                <div class="repeat-of-set">
                    <label><input type="checkbox" name="setEnabled" onclick="setDisabled(this)">房&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;价&nbsp;:</label>
                    <input type="text"  disabled="disabled" id="value_standard" onkeyup="checkOutNumber(this)">
                </div>
                <div class="repeat-of-set">
                    <label><input type="checkbox" name="setEnabled" onclick="setDisabled(this)">入住日期&nbsp;:</label>
                    <input class="set-start-date" type="text" id="checkin_standard" disabled="disabled" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked:function(dp){reSetStartTime(dp.cal.getNewDateStr(), dp.el)}})">
                </div>
                <div class="repeat-of-set">
                    <label><input type="checkbox" name="setEnabled" onclick="setDisabled(this)">退房日期&nbsp;:</label>
                    <input class="set-end-date" type="text" id="checkout_standard" disabled="disabled" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked:function(dp){reSetStartTime(dp.cal.getNewDateStr(), dp.el)}})">
                </div>
                <div class="list-btn">
                    <input type="button" value="确定" onclick="repeatSetValue()">
                    <input type="button" value="清空" onclick="cancelSetValue()">
                </div>
            </div>
          </div>
          <div class="detail-people-div">
            <div class="hotel-people-header">
                <table class="hotel-people-table" id="hotelPeopleTable" cellspacing = 0, cellpadding = 0>
		                <tr>
		                    <td style="width: 8%; min-width: 8%; max-width: 8%;">房间号
		                    	<select class="selectpicker hotel-name-select roomNo-select" id="roomNumSelect"  multiple data-live-search="true" style="display: none;"></select>
		                    </td>
		                    
		                    <td style="width: 3%; min-width: 3%; max-width: 3%;"><input id="selectAll" type="checkbox" onclick="selectAll(this)"></td>
		                    
		                    <td style="width: 12%; min-width: 12%; max-width: 12%;">姓名
		                    	<select class="selectpicker hotel-name-select people-name-select" id="peopleNameSelect"  multiple data-live-search="true" style="display: none;"></select>
		                    </td>
		                    
		                    <td style="width: 12%; min-width: 12%; max-width: 12%;">入住时间
		                     	<span class="select-hotel-date" id="selectCheckInDateBtn" onclick="showCheckInDatePanel(this, event)"></span>
		                    	  <div class="hotel-date-panel" id="hotelCheckInDatePanel" onclick="stopSlideup(event)">
	                                <p class="date-panel-title">请输入要查询的时间段</p>
	                                <input class="start-date" type="text" id="checkInStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">&nbsp;&nbsp;-&nbsp;
	                                <input class="end-date" type="text" id="checkInEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
	                                <input type="text" style="display: none">
	                                <div class="btn-list">
	                                    <input type="button" value="确定" onclick="queryCheckInDateTimeData(event)">
	                                    <input type="button" value="清空" onclick="clearCheckInDateTimeData(event)">
	                                </div>
	                                
	                            </div>
		                    </td>
		                    
                       		<td style="width: 12%; min-width: 12%; max-width: 12%;">退房时间
                       			<span class="select-hotel-date" id="selectCheckOutDateBtn" onclick="showCheckOutDatePanel(this, event)"></span>
                       			  <div class="hotel-date-panel" id="hotelCheckOutDatePanel" onclick="stopSlideup(event)">
	                                <p class="date-panel-title">请输入要查询的时间段</p>
	                                <input class="start-date" type="text" id="checkOutStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">&nbsp;&nbsp;-&nbsp;
	                                <input class="end-date" type="text" id="checkOutEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
	                                <input type="text" style="display: none">
	                                <div class="btn-list">
	                                    <input type="button" value="确定" onclick="queryCheckOutDateTimeData(event)">
	                                    <input type="button" value="清空" onclick="clearCheckOutDateTimeData(event)">
	                                </div>
	                                
	                            </div>
                       		</td>
                       		
                        	<td style="width: 10%; min-width: 10%; max-width: 10%;">入住天数
                        		<span class="select-hotel-date" id="selectIndaysDateBtn" onclick="showIndaysPanel(this, event)"></span>
                       			  <div class="hotel-date-panel" id="hotelIndaysPanel" onclick="stopSlideup(event)">
	                                <p class="date-panel-title">请输入要查询的入住天数区间</p>
	                                <input class="start-date" type="text" id="indaysStart" >&nbsp;&nbsp;-&nbsp;
	                                <input class="end-date" type="text" id="indaysEnd" >
	                                <input type="text" style="display: none">
	                                <div class="btn-list">
	                                    <input type="button" value="确定" onclick="queryIndaysTimeData(event)">
	                                    <input type="button" value="清空" onclick="clearIndaysTimeData(event)">
	                                </div>
	                            </div>
                        	</td>
                        	
                        	<td style="width: 10%; min-width: 10%; max-width: 10%;">房间类型
                        		<select class="selectpicker hotel-name-select room-type-select" id="roomTypeSelect"  multiple data-live-search="true" style="display: none;"></select>
                        	</td>
                        	
		                    <td style="width: 10%; min-width: 10%; max-width: 10%;">房价
		                    	<span class="select-hotel-date" id="selectRoomPriceDateBtn" onclick="showRoomPricePanel(this, event)"></span>
                       			  <div class="hotel-date-panel" id="hotelRoomPricePanel" onclick="stopSlideup(event)">
	                                <p class="date-panel-title">请输入要查询的价格区间</p>
	                                <input class="start-date" type="text" id="roomPriceStart" >&nbsp;&nbsp;-&nbsp;
	                                <input class="end-date" type="text" id="roomPriceEnd" >
	                                <input type="text" style="display: none">
	                                <div class="btn-list">
	                                    <input type="button" value="确定" onclick="queryRoomPriceData(event)">
	                                    <input type="button" value="清空" onclick="clearRoomPriceData(event)">
	                                </div>
	                            </div>
		                    </td>
		                    
		                    <td style="width: 10%; min-width: 10%; max-width: 10%;">分机号
		                    	<select class="selectpicker hotel-name-select extension-select" id="extensionSelect"  multiple data-live-search="true" style="display: none;"></select>
		                    </td>
		                    
		                    <td style="width: 13%; min-width: 13%; max-width: 13%;">备注
		                    	<span class="select-hotel-reamrk" id="selectRemarkBtn" onclick="showRemarkPanel(this, event)"></span>
                       			  <div class="hotel-remark-panel" id="hotelRemarkPanel" onclick="stopSlideup(event)">
	                                <input class="remark-text" type="text" placeholder='请输入需要查询的内容' id="remarkText" >
	                                <input type="text" style="display: none">
	                                <div class="btn-list">
	                                    <input type="button" value="确定" onclick="queryRemarkData(event)">
	                                    <input type="button" value="清空" onclick="clearRemarkData(event)">
	                                </div>
	                            </div>
		                    </td>
		                    
		                </tr>
		            </table>
            </div>
            <div class="hotel-people-body">
                <table class="hotel-people-data" id="hotelPeopleData" cellspacing = 0, cellpadding = 0>
                    <tr class="blank-tr">
                        <td style="text-align: center; vertical-align: center;" colspan="9">暂无数据</td>
                    </tr>
                </table>
                <ul class="search-person-ul" id="searchPersonList"></ul>
                <ul class="search-person-ul" id="searchRoomTypeList"></ul>
            </div>
          </div>
        </div>
        
        
      </div>
      
      <!-- 百度地图弹窗 -->
      <div id="baiduMapWindow" style="display: none; border: 0px; border-radius: 0px;">
        <div>详细位置</div>
        <div>
            <div class="map-con-div">
                <div class="search-address-div">
                    <p><span style="color: #f00;">*</span>&nbsp;请用搜索框搜索地址并拖动红色标志进行精确定位</p>
                    <input type="button" value="保存并关闭" onclick="saveLocation()">
                </div>
               <div id="r-result" style='position: absolute; top: 43px; left: 30px; z-index: 999; width: 30%;'>
                  <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="搜索地点" style="width:100%;" />
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%; height: calc(100% - 30px);"></div>
              </div>
        </div>
      </div>  
      
    </div>
  </body>
</html>