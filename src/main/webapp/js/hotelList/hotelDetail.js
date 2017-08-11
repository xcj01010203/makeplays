var roomTypeList;
var filter = {};
$(function(){
	//初始化百度地图弹窗
	initBaiduMapWin();
	if($("#hotelId").val() != ""){
		$("#delHotelBtn").show();
		//获取宾馆下拉列表数据
		getHotelSelectData();
		//加载宾馆详细数据
		loadHotelDetailInfo();
	}
	$(document).on("click", function(){
		$("#searchPersonList").hide();
	});
	$(document).on("click", function(){
		$("#searchRoomTypeList").hide();
	});
	
	//页面收起下拉列表框
	$(document).on("click", function(event){
		$("span[class*='open']").each(function() {
			var changeMessage = "";
			var textArr = $(this).siblings("div").find("input[type=text]");
			if (textArr != undefined && textArr != null) {
				for(var i=0; i<textArr.length; i++){
					var text = $(textArr[i]).val();
					if (text != '' && text != null) {
						changeMessage = text;
					}
				}
			}
			
			if (changeMessage == '') {
				$(this).removeClass("open");
			}
			$(this).siblings("div").slideUp(300);
		});
		event.stopPropagation();
	});
	
	//获取房间类型
	initRoomTypeList();
	
	initSelectData();
	
	//判断是否有权限
	if(isHotelInfoReadonly) {
		$("#confirmSaveHotelInfo").remove();
		$("#delHotelBtn").remove();
		$("#addCheckinPeople").remove();
		
		$("#hotelPeopleData input[type='text']").attr('disabled',true);
		$("input[type='checkbox']").attr('disabled',true);
		$(".set-start-date").attr('disabled',true).css('display','none');
		$(".set-end-date").attr('disabled',true).css('display','none');
	}
	
	$(document).on("click", function(){
		$("#repeatSetList").slideUp(200);
	});
	
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
});

//获取房间类型
function initRoomTypeList() {
	$.ajax({
		url:'/hotelManager/queryRoomTypeList',//后台提供的接口
	    dataType:'json', //返回json格式
	    async:false,
	    success:function(data){//这个data就是后台给你的json格式的数据
	    	if(data.success){
	    		roomTypeList = data.roomTypeList;
	    		if (roomTypeList.length >0) {
	    			var html = [];
	    			var optionArr = [];
					for(var i= 0; i< roomTypeList.length; i++){
						html.push('<li onclick="selectRoomType(this, event)">' + roomTypeList[i].roomType + '</li>');
						optionArr.push('<option value="'+ roomTypeList[i].roomType +'">'+ roomTypeList[i].roomType +'</option>');
					}
					$("#searchRoomTypeList").append(html.join(""));
					
					//拼接高级查询下拉列表
					$("#roomTypeSelect").append(optionArr.join(''));
					$("#roomTypeSelect").selectpicker("refresh");
				}
			}else{
				showErrorMessage(data.message);
			}
	        
	    }
	});
}

//计算日期相隔天数
function calculateDays(date, own){
	if($(own).hasClass("startTime")){
		var endTime = $(own).parents("tr").find("input.endTime").val();
		var startTime = date;
		if(endTime != ""){
			var  indate   =   new   Date(startTime.replace(/-/g,   "/")); 
			var  outdate   =   new   Date(endTime.replace(/-/g,   "/")); 
			if(indate > outdate){
				parent.showInfoMessage("入住时间不能大于退房时间");
				$(own).val("");
				return false;
			}
			var strSeparator = "-"; //日期分隔符
		    var oDate1;
		    var oDate2;
		    var iDays;
		    oDate1= startTime.split(strSeparator);
		    oDate2= endTime.split(strSeparator);
		    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
		    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
		    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
		    if(indate.getTime() == outdate.getTime()){
		    	iDays = add(iDays,1);
		    	$(own).parents("tr").find("input.idays").val(iDays);
		    	return false;
		    }
		    $(own).parents("tr").find("input.idays").val(iDays);
		}
	}
	if($(own).hasClass("endTime")){
		var startTime = $(own).parents("tr").find("input.startTime").val();
		var endTime = date;
		if(startTime != ""){
			var  indate   =   new   Date(startTime.replace(/-/g,   "/")); 
			var  outdate   =   new   Date(endTime.replace(/-/g,   "/")); 
			if(indate > outdate){
				parent.showInfoMessage("退房时间不能小于入住时间");
				$(own).val("");
				return false;
			}
			var strSeparator = "-"; //日期分隔符
		    var oDate1;
		    var oDate2;
		    var iDays;
		    oDate1= startTime.split(strSeparator);
		    oDate2= endTime.split(strSeparator);
		    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
		    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
		    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
		    if(indate.getTime() == outdate.getTime()){//比较日期是否相等
		    	
		    	iDays = add(iDays,1);
		    	$(own).parents("tr").find("input.idays").val(iDays);
		    	return false;
		    }
		    $(own).parents("tr").find("input.idays").val(iDays);
		}
	}
}



//初始化百度地图弹窗
function initBaiduMapWin(){
	$("#baiduMapWindow").jqxWindow({
		width: 800,
        height: 550,
        autoOpen: false,
        resizable: false,
        isModal: true,
        maxWidth: 1300,
//        showCloseButton: false,
        initContent: function(){
        	
        }
	});
	
}

var inputButtonIndex;//区分拍摄地定位按钮
var map;
var point;
var num = 0;
var hasDrag = false;//是否拖动

//显示百度地图弹窗
function showMapWindow(own){
	var $this = $(own);
	inputButtonIndex = $this;
	var lng = $("#location").attr("log");//经度
	var lat = $("#location").attr("lat");//纬度
	if((lng != "" && lat != "") && (lng != undefined && lat != undefined)){
		point = new BMap.Point(lng, lat);
	}else{
		var p;
		point = p;
	}
	$("#baiduMapWindow").jqxWindow("open",function(){
		initBaiDuMap(lng, lat);
	});
	
	/*var width = window.document.body.scrollWidth;
	var height = window.document.body.scrollHeight;
	$("#winModalZIndex").css({"width": width, "height": height}).show();
	$("#baiduMapWindow").show();
	
	initBaiDuMap();*/
}




//初始化地图
function initBaiDuMap(){
	map = new BMap.Map("allmap");
    //初始化自动完成
	initAutoComplete(); 
//	map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);                  // 初始化地图,设置城市和地图级别。
	if (point&&point.lng!=null&&point.lat!=null){
		map.clearOverlays();    //清除地图上所有覆盖物
		var marker = new BMap.Marker(point); // 创建标注
		marker.enableDragging(); // 可拖拽
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
		map.centerAndZoom(point, 16);
		map.addOverlay(marker); // 将标注添加到地图中
	}else{
        map.clearOverlays();    //清除地图上所有覆盖物
		
		var p = new BMap.Point(116.405285, 39.904989);
		map.centerAndZoom(p,11);
		point = p;
	
		var marker = new BMap.Marker(p); // 创建标注
		marker.enableDragging(); // 可拖拽
		map.addOverlay(marker); // 将标注添加到地图中
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
		locateCurrentCity();
	}
	
	map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放

}

//获取当前城市信息
function locateCurrentCity() {
	$.getJSON('https://route.showapi.com/632-1?showapi_appid=12437&showapi_sign=6c2a2ad77d644fee81c6d71a5ee9310d', function(data){
		var currentLat = data.showapi_res_body.lat; 
		var currentLon = data.showapi_res_body.lnt; 
		var p = new BMap.Point(currentLon, currentLat);
		map.setCenter(p);
		point = p;
		
		
		map.clearOverlays();    //清除地图上所有覆盖物
		
		var marker = new BMap.Marker(p); // 创建标注
		marker.enableDragging(); // 可拖拽
		map.addOverlay(marker); // 将标注添加到地图中
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
	});
}


function initAutoComplete(){
	var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
		{"input" : "suggestId"
		,"location" : map
	});

	ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
	var str = "";
		var _value = e.fromitem.value;
		var value = "";
		if (e.fromitem.index > -1) {
			value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		}    
		str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
		
		value = "";
		if (e.toitem.index > -1) {
			_value = e.toitem.value;
			value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		}    
		str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
		G("searchResultPanel").innerHTML = str;
	});

	var myValue;
	ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
	var _value = e.item.value;
		myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
		
		setPlace();
	});

	function setPlace(){
		map.clearOverlays();    //清除地图上所有覆盖物
		function myFun(){
			var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
			map.centerAndZoom(pp, 18);
			point = pp;
			hasDrag = true;
			var marker = new BMap.Marker(pp); //创建标注
			marker.enableDragging(); // 可拖拽
			map.addOverlay(marker);    //添加标注
			marker.addEventListener("dragend", function(e){
				point.lng = e.point.lng;
				point.lat = e.point.lat;
				hasDrag = true;
			});
		}
		var local = new BMap.LocalSearch(map, { //智能搜索
		  onSearchComplete: myFun
		});
		local.search(myValue);
	}
}


//百度地图API功能
function G(id) {
	return document.getElementById(id);
}

//保存经纬度
function saveLocation(){
	var p = point;  //获取marker的位置
	
	//获得定位按钮前一个对象
	var spanIndex = inputButtonIndex.prev("span.transfer-info-span");
	var shootId = spanIndex.attr("id");
	var vName = spanIndex.text();
	
	var geoc = new BMap.Geocoder();    
    geoc.getLocation(p, function(rs){
    	
		addComp = rs.addressComponents;
		province = addComp.province;
		city = addComp.city;
	    district = addComp.district;
	    var detailAddress;//详细地址
    	var cityName;
	    if(province == city){
	    	cityName = city+"-"+district;
	    	detailAddress = addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}else{
			cityName = province+"-"+city;
			detailAddress = addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}
	    saveDetailLocation(shootId, vName, cityName, detailAddress, p.lng, p.lat);
    });
	$("#baiduMapWindow").jqxWindow("close");
}

//保存经纬度等信息
function saveDetailLocation(shootId, vName, cityName, detailAddress, lng, lat){
//	$.ajax({
//		url: '/sceneViewInfoController/updateSceneViewInfo',
//		type: 'post',
//		data: {"id":shootId, "vName": vName, "vCity":cityName, "vAddress":detailAddress, "vLongitude": lng, "vLatitude":lat},
//		datatype: 'json',
//		success: function(response){
//			if(response.success){
//				inputButtonIndex.attr("log", lng);
//				inputButtonIndex.attr("lat", lat);
//				inputButtonIndex.addClass("already-set-location");
//			}else{
//				parent.showErrorMessage(response.message);
//			}
//		}
//	});
	$("#location").val(detailAddress);
	$("#location").attr("log", lng);
	$("#location").attr("lat", lat);
}


//校验输入的内容是否是数字
function checkOutNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	
}

//格式化金额
function formatCost(own){
	var $this = $(own);
	if($this.val() != ""){
		$this.val(fmoney($this.val().replace(/,/g, "")));
	}else{
		return;
	}
}

var hotelModel;
var checkInList;

//加载宾馆详细信息
function loadHotelDetailInfo(){
	var hotelId = $("#hotelId").val();
	$.ajax({
		url: '/hotelManager/queryHotelAndCheckIninfo',
		type: 'post',
		data: {"hotelId": hotelId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				hotelModel = response.hotelModel;
				checkInList = response.checkInList;
				createDetailOfHotel(hotelModel, checkInList);
			}
		}
	});
}

//生成详细信息
function createDetailOfHotel(hotelModel, checkInList){
	$("#hotelName").val(hotelModel.hotelName);
	$("#location").val(hotelModel.hotelAddress);
	$("#location").attr("log", hotelModel.longitude);
	$("#location").attr("lat", hotelModel.latitude);
	$("#hotelNum").val(hotelModel.hotelPhone);
	$("#roomNum").val(hotelModel.roomNumber);
	$("#contacts").val(hotelModel.contactPeople);
	$("#phone").val(hotelModel.contactPhone);
	$("#priceDescription").val(hotelModel.priceRemark);
	var html = [];
	if(checkInList.length != 0){
		
		for(var i= 0; i< checkInList.length; i++){
			var day = new Date();
			var checkoutDate = new Date(checkInList[i].checkoutDate.replace(/-/g,   "/"));
			if(i != 0){//第一行
				var num = i-1;
				if(checkInList[i].roomNo == checkInList[num].roomNo){
					
					if(day < checkoutDate){
						html.push('<tr class="tr-'+ num +' right-popup-table-tr" arr="tr-'+ num +'" id="'+ checkInList[i].id +'">');
					}else{
						html.push('<tr class="tr-'+ num +' right-popup-table-tr" arr="tr-'+ num +'" id="'+ checkInList[i].id +'">');
					}
					//房间号
					html.push('<td style="width: 8%; min-width: 8%; max-width: 8%; border-top: 0px;"><input type="text" style="text-align: center;"  value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
				}else{
					if(day < checkoutDate){
						html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
					}else{
						html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
					}
					//房间号
					html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" style="text-align: center;" value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
				}
			}else{
				if(day < checkoutDate){
					html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
				}else{
					html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
				}
				//房间号
				html.push('<td  style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" style="text-align: center;" value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
			}
			//单选框
			html.push('<td style="width: 3%; min-width: 3%; max-width: 3%;"><input type="checkbox" name="setDate" onclick="isCheckAll()"></td>');
			
			//姓名
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;"><span class="td-span">');
			html.push('<input type="text" value="'+ checkInList[i].peopleName +'" onclick="showPersonName(this,event)" onkeyup="searchPersonName(this)">');
			html.push(' <span class="operate-btn"><input class="delet-row-btn" type="button" onclick="deleteHotelPerson(this)"></span></span>');
			html.push('</td>');
			//入住时间
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;">');
			html.push(' <input class="startTime" type="text" style="text-align: center;" value="'+ checkInList[i].checkInDate +'" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})">');
			html.push('</td>');
			//退房时间
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;">');
			html.push(' <input class="endTime" type="text" style="text-align: center;" value="'+ checkInList[i].checkoutDate +'" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})">');
			html.push('</td>');
			//入住天数
			if((checkInList[i].inTimes == null) || (checkInList[i].inTimes == "") || (checkInList[i].inTimes == "null")){
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input class="idays" type="text" style="text-align: right;" value=""></td>');
			}else{
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input class="idays" type="text" style="text-align: right;" value="'+ checkInList[i].inTimes +'"></td>');
			}
			
			//房间类型
			var roomType = checkInList[i].roomType;
			if (roomType == null || roomType == '' || roomType == "null") {
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"  class="room-type"><input class="insert-input" type="text" style="text-align: center;" value="" onclick="showSelectWin(this,event)" onkeyup="searchRoomType(this)"></td>');
			}else {
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"  class="room-type"><input class="insert-input" type="text" style="text-align: center;" value="'+ roomType +'" onclick="showSelectWin(this,event)" onkeyup="searchRoomType(this)"></td>');
			}
			
			//房价
			if(checkInList[i].roomPrice == null || checkInList[i].roomPrice == "" || checkInList[i].roomPrice == "null"){
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" style="text-align: right;" value="" onkeyup="checkOutNumber(this)" onblur="formatCost(this)"></td>');
			}else{
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" style="text-align: right;" value="'+ fmoney(checkInList[i].roomPrice) +'" onkeyup="checkOutNumber(this)" onblur="formatCost(this)"></td>');
			}
			//分机号
			html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" value="'+ checkInList[i].extension +'"></td>');
			
			//备注
			if(checkInList[i].remark == null || checkInList[i].remark == "" || (checkInList[i].remark == "null")){
				html.push('<td style="width: 13%; min-width: 13%; max-width: 13%;"><input type="text" value=""></td>');
			}else{
				html.push('<td style="width: 13%; min-width: 13%; max-width: 13%;"><input type="text" value="'+ checkInList[i].remark +'"></td>');
			}
			
			html.push('</tr>');
		}
		$("#hotelPeopleData").empty();
		$("#hotelPeopleData").append(html.join(""));
		$("#selectAll").attr("disabled", false);
		
		
		if (isHotelInfoReadonly) {
			$(".delet-row-btn").remove();
		}
	}else{
		html.push('<tr class="blank-tr">');
		html.push('<td style="text-align: center; vertical-align: center;" colspan="9">暂无数据</td>');
		html.push('</tr>');
		$("#hotelPeopleData").empty();
		$("#hotelPeopleData").append(html.join(""));
		$("#selectAll").attr("disabled", true);
	}
	
}

//增加住宿人员
function addHotePeople(){
	var blankTr = 0;
	$("#hotelPeopleData tr.blank-tr").each(function(){
		blankTr++;
	});
	if(blankTr > 0){
		$("#hotelPeopleData").empty();
		$("#selectAll").attr("disabled", false);
	}/*else{
		var lastTr = $("#hotelPeopleData").find("tr:last-child");
		var name = lastTr.find("input[type=text]").eq(0).val();
		var roomNumber = lastTr.find("input[type=text]").eq(1).val();
		var extension = lastTr.find("input[type=text]").eq(2).val();
		var value = lastTr.find("input[type=text]").eq(3).val();
		var startDate = lastTr.find("input[type=text]").eq(4).val();
		var endDate = lastTr.find("input[type=text]").eq(5).val();
		var days = lastTr.find("input[type=text]").eq(6).val();
		var remark = lastTr.find("input[type=text]").eq(7).val();
		if(name == "" && roomNumber == "" && extension == "" && value == "" && startDate == "" && endDate == "" && days == "" && remark == ""){
			parent.showInfoMessage("请完善信息后再进行添加");
			return;
		}
		if(name == ""){
			parent.showInfoMessage("请填写姓名");
			return;
		}
		if(roomNumber == ""){
			parent.showInfoMessage("请填写房间号");
			return;
		}
		if(startDate == ""){
			parent.showInfoMessage("请填写入住时间");
			return;
		}
		if(endDate == ""){
			parent.showInfoMessage("请填写退房时间");
			return;
		}
		if(days == ""){
			parent.showInfoMessage("请填写入住天数");
			return;
		}
	}*/
	
	var html = [];
	var tr = $('<tr class="right-popup-table-tr"></tr>');
	html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" value=""></td>');
	html.push('<td style="width: 3%; min-width: 3%; max-width: 3%;"><input type="checkbox" name="setDate" ></td>');
	html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;"><span class="td-span">');
	html.push('<input type="text" value="" onclick="showPersonName(this,event)" onkeyup="searchPersonName(this)">');
	html.push('<span class="operate-btn"><input class="delet-row-btn" type="button" onclick="deleteHotelPerson(this)"></span>');
	html.push('</span></td>');
	html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;"><input class="startTime" type="text" style="text-align: center;" value="" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})"></td>');
    html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;"><input class="endTime" type="text" style="text-align: center;" value="" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})"></td>');
    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input class="idays" type="text" style="text-align: right;" value=""></td>');
    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"  class="room-type"><input class="insert-input" type="text" style="text-align: center;" value="" onclick="showSelectWin(this,event)" onkeyup="searchRoomType(this)"></td>');
    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" style="text-align: right;" value="" onkeyup="checkOutNumber(this)" onblur="formatCost(this)"></td>');
    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" value=""></td>');
    html.push('<td style="width: 13%; min-width: 13%; max-width: 13%;"><input type="text" value=""></td>');
    
    tr.append(html.join(""));
    $("#hotelPeopleData").append(tr);
    $("#hotelPeopleData").find("tr:last-child").find("input[type=text]").eq(0).focus();
    if (isHotelInfoReadonly) {
		$(".delet-row-btn").remove();
	}
}

//删除入住人员
function deleteHotelPerson(own){
	var id = $(own).parents("tr").attr("id");
	if(id != "" && id != undefined && id != null){
		parent.popupPromptBox("提示", "是否要删除该住宿人员信息？", function(){
			$.ajax({
				url: '/hotelManager/deleteCheckIninfo',
				type: 'post',
				data: {"checkinId": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						if($(own).parents("tr").find("input[type=text]").eq(0).is(":hidden")){
							$(own).parents("tr").remove();
						}else{
							var arrIndex = $(own).parents("tr").attr("arr");
							$(own).parents("tr").siblings("tr." + arrIndex).find("input[type=text]").eq(0).css({"display":"block"}).parent("td").css({"border-top":"1px solid #ccc"});
							$(own).parents("tr").remove();
						}
						parent.showSuccessMessage("删除成功");
						
					}else{
						parent.showErrorMessage(response.message);
					}
				}
			});
		});
		
	}else{
		parent.showSuccessMessage("删除成功");
		$(own).parents("tr").remove();
	}
}

//关闭滑动窗口
function closeRightWin(){	
	parent.closeRightPopWin();
}

function closeCheckRightWin(){
	var valueFlag = false;//值是否改变的标志
	if(hotelModel != undefined && checkInList != undefined){
		if($("#hotelName").val() != hotelModel.hotelName){
			valueFlag = true;
			if(($("#hotelName").val() == "" && hotelModel.hotelName == null) || ($("#hotelName").val() == "" && hotelModel.hotelName == "")){
				valueFlag = false;
			}
		}
		if(!valueFlag){
			if($("#location").val() != hotelModel.hotelAddress){
				valueFlag = true;
				if(($("#location").val() == "" && hotelModel.hotelAddress == null) || ($("#location").val() == "" && hotelModel.hotelAddress == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#location").attr("log") != hotelModel.longitude){
				valueFlag = true;
				if(($("#location").attr("log") == "" && hotelModel.longitude == null) || ($("#location").attr("log") == "" && hotelModel.longitude == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#location").attr("lat") != hotelModel.latitude){
				valueFlag = true;
				if(($("#location").attr("lat") == "" && hotelModel.latitude == null) || ($("#location").attr("lat") == "" && hotelModel.latitude == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#hotelNum").val() != hotelModel.hotelPhone){
				valueFlag = true;
				if(($("#hotelNum").val() == "" && hotelModel.hotelPhone == null) || ($("#hotelNum").val() == "" && hotelModel.hotelPhone == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#roomNum").val() != hotelModel.roomNumber){
				valueFlag = true;
				if(($("#roomNum").val() == "" && hotelModel.roomNumber == null) || ($("#roomNum").val() == "" && hotelModel.roomNumber == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#contacts").val() != hotelModel.contactPeople){
				valueFlag = true;
				if(($("#contacts").val() == "" && hotelModel.contactPeople == null) || ($("#contacts").val() == "" && hotelModel.contactPeople == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#phone").val() != hotelModel.contactPhone){
				valueFlag = true;
				if(($("#phone").val() == "" && hotelModel.contactPhone == null) || ($("#phone").val() == "" && hotelModel.contactPhone == "")){
					valueFlag = false;
				}
			}
		}
		if(!valueFlag){
			if($("#priceDescription").val() != hotelModel.priceRemark){
				valueFlag = true;
				if(($("#priceDescription").val() == "" && hotelModel.priceRemark == null) || ($("#priceDescription").val() == "" && hotelModel.priceRemark == "")){
					valueFlag = false;
				}
			}
		}
		var trArray = $("#hotelPeopleData").find("tr[class!='blank-tr']");
		if(trArray.length != checkInList.length){
			valueFlag = true;
		}else{
			$.each(trArray, function(i){
				var _this = $(this);
				if(checkInList.length != 0){
					for(var j= 0; j< checkInList.length; j++){
						if(i == j){
							//入住人姓名
							var personName = _this.find("input[type=text]").eq(1).val();
							if(!valueFlag){
								if(personName != checkInList[i].peopleName){
									valueFlag = true;
								}
							}
							//房间号
							var roomNum = _this.find("input[type=text]").eq(0).val();
							if(!valueFlag){
								if(roomNum != checkInList[j].roomNo){
									valueFlag = true;
								}
							}
							//分机号
							var extension = _this.find("input[type=text]").eq(7).val();
							if(!valueFlag){
								if(extension != checkInList[j].extension){
									valueFlag = true;
									if(extension == "" && (checkInList[j].extension == null) || extension == "" && (checkInList[j].extension == "") || extension == "" && (checkInList[j].extension == "null")){
										valueFlag = false;
									}
								}
							}
							//房价
							var price = _this.find("input[type=text]").eq(6).val().replace(/,/g,"");
							if(price != ""){
								price = Number(price);
							}
							if(!valueFlag){
								if(price != checkInList[j].roomPrice){
									valueFlag = true;
									if(price == "" && (checkInList[j].roomPrice == null) || price == "" && (checkInList[j].roomPrice == "") || price == "" && (checkInList[j].roomPrice == "null")){
										valueFlag = false;
									}
								}
							}
							//入住日期
							var startDate = _this.find("input[type=text]").eq(2).val();
							if(!valueFlag){
								if(startDate != checkInList[j].checkInDate){
									valueFlag = true;
								}
							}
							//退房时间
							var endDate = _this.find("input[type=text]").eq(3).val();
							if(!valueFlag){
								if(endDate != checkInList[j].checkoutDate){
									valueFlag = true;
								}
							}
							//入住天数
							var idays = _this.find("input[type=text]").eq(4).val();
							if(!valueFlag){
								if(idays != checkInList[j].inTimes){
									valueFlag = true;
									if(idays == "" && (checkInList[j].inTimes == null) || idays == "" && (checkInList[j].inTimes == "") || idays == "" && (checkInList[j].inTimes == "null")){
										valueFlag = false;
									}
								}
							}
							//备注
							var remark = _this.find("input[type=text]").eq(8).val();
							if(!valueFlag){
								if(remark != checkInList[j].remark){
									valueFlag = true;
									if(remark == "" && (checkInList[j].remark == null) || remark == "" && (checkInList[j].remark == "") || remark == "" && (checkInList[j].remark == "null")){
										valueFlag = false;
									}
								}
							}
							
							//房间类型
							var roomType = _this.find("input[type=text]").eq(5).val();
							if(!valueFlag){
								if(roomType != checkInList[j].roomType){
									valueFlag = true;
									if(roomType == "" && (checkInList[j].roomType == null) || roomType == "" && (checkInList[j].roomType == "") || roomType == "" && (checkInList[j].roomType == "null")){
										valueFlag = false;
									}
								}
							}
							
						}
					}
				}
			});
		}
		
		if(valueFlag){
			if (!isHotelInfoReadonly) {
				parent.tipInfoBox("提示","您修改了信息，是否要保存？", function (){
					saveHotelInfo();//保存
				});
			}else {
				closeRightWin();
			}
		}else{
			closeRightWin();
		}
		
	}else{
		closeRightWin();
	}
}


//统一设置入住时间
function reSetStartTime(date, own){
	if($(own).hasClass("set-start-date")){
		var endTime = $("#checkout_standard").val();
		if(endTime != ""){
			var  indate   =   new   Date(date.replace(/-/g,   "/")); 
			var  outdate   =   new   Date(endTime.replace(/-/g,   "/")); 
			if(indate > outdate){
				parent.showInfoMessage("入住时间不能大于退房时间");
				$(own).val("");
				return false;
			}
		}
	}
	if($(own).hasClass("set-end-date")){
		var startTime = $("#checkin_standard").val();
		if(startTime != ""){
			var  indate   =   new   Date(startTime.replace(/-/g,   "/")); 
			var  outdate   =   new   Date(date.replace(/-/g,   "/")); 
			if(indate > outdate){
				parent.showInfoMessage("退房时间不能小于入住时间");
				$(own).val("");
				return false;
			}
		}
	}
}
	

//统一设置退房时间
function reSetEndTime(date, own){
	$(own).val("");
	var checkedLength = 0;
	$("input[type=checkBox][name=setDate]:checked").each(function(){
		checkedLength ++;
	});
	if(checkedLength > 0){
		$("input[type=checkBox][name=setDate]:checked").each(function(){
			var startDate = $(this).parents("tr").find("input[type=text]").eq(4).val();
			if(startDate != ""){
				var  indate   =   new   Date(startDate.replace(/-/g,   "/")); 
				var  outdate   =   new   Date(date.replace(/-/g,   "/")); 
				if(indate > outdate){
					parent.showInfoMessage("退房时间不能小于入住时间");
					$(this).val("");
					return false;
				}
				var strSeparator = "-"; //日期分隔符
			    var oDate1;
			    var oDate2;
			    var iDays;
			    oDate1= startDate.split(strSeparator);
			    oDate2= date.split(strSeparator);
			    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
			    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
			    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
			    if(indate.getTime() == outdate.getTime()){
			    	iDays = add(iDays,1);
			    	$(this).parents("tr").find("input.idays").val(iDays);
			    	//return false;
			    }
			    $(this).parents("tr").find("input.idays").val(iDays);
			    $(this).parents("tr").find("input[type=text]").eq(5).val(date);
			}
		});
	}else{
		parent.showInfoMessage("请选择需要统一设置退房时间的住宿人员");
		return false;
	}
}

//设置全选
function selectAll(own){
	if($(own).is(":checked")){
		$("input[type=checkBox][name=setDate]").each(function(){
			$(this).prop("checked", true);
		});
	}else{
		$("input[type=checkBox][name=setDate]").each(function(){
			$(this).prop("checked", false);
		});
	}
}

//行checkbox事件只判断是否全选
function isCheckAll(){
	
	var _tableObj = $("#hotelPeopleData");
	var checkboxs = _tableObj.find(":checkbox");
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#selectAll").prop("checked",false);
	}else{
		$("#selectAll").prop("checked",true);
	}
}



//保存住宿信息
function saveHotelInfo(){
	var subData = {};
	subData.id = $("#hotelId").val();
	subData.hotelName = $("#hotelName").val();
	subData.hotelAddress  = $("#location").val();
	subData.vLongitude  = $("#location").attr("log");
	subData.vLatitude  = $("#location").attr("lat");
	subData.hotelPhone = $("#hotelNum").val();
	subData.roomNumber = $("#roomNum").val();
	subData.contactPeople = $("#contacts").val();
	subData.contactPhone = $("#phone").val();
	subData.priceRemark = $("#priceDescription").val();
	if($("#hotelName").val() == ""){
		parent.showInfoMessage("酒店名称不能为空");
		return;
	}
	var checkIninfoStr = [];
	var trArray = $("#hotelPeopleData").find("tr[class!='blank-tr']");
	var blankFlag = false;//是否为空标志
	var allBlank = false;//是否全部为空
	var infoMessage = "";
	if(trArray.length != 0){
		var roomPriceInfo = {};	//房间号对应的房价信息
		var priceConflictRoomArray = [];
		
		$.each(trArray, function(i){
			i++;
			if(blankFlag){
				return;
			}
			var checkIninfoArray = "";
			
			infoMessage = "第" + i + "行";
			//姓名
			var personName = $(this).find("input[type=text]").eq(1).val();
			if(personName == ""){
				infoMessage += "姓名为空,";
				blankFlag = true;
			}else{
				checkIninfoArray += personName + ";";
			}
			//房间号
			var roomNum = $(this).find("input[type=text]").eq(0).val();
			if(roomNum == ""){
				infoMessage +=  "房间号为空,";
				blankFlag = true;
			}else{
				checkIninfoArray += roomNum + ";";
			}
			
			//分机号
			var extension = $(this).find("input[type=text]").eq(7).val();
			checkIninfoArray += extension + ";";
			//房价
			var price = $(this).find("input[type=text]").eq(6).val().replace(/,/g,"");
			checkIninfoArray += price + ";";
			//入住时间
			var startDate = $(this).find("input[type=text]").eq(2).val();
			if(startDate == ""){
				infoMessage +=  "入住时间为空,";
				blankFlag = true;
			}else{
				checkIninfoArray += startDate + ";";
			}
			//退房时间
			var endDate = $(this).find("input[type=text]").eq(3).val();
			if(endDate == ""){
				infoMessage +=  "退房时间为空,";
				blankFlag = true;
			}else{
				checkIninfoArray += endDate + ";";
			}
			//入住天数
			var idays = $(this).find("input[type=text]").eq(4).val();
			if(idays == ""){
				infoMessage +=  "入住天数为空,";
				blankFlag = true;
			}else{
				checkIninfoArray += idays + ";";
			}
			
			//房间类型
			var roomType =  $(this).find("input[type=text]").eq(5).val();
			checkIninfoArray += roomType + ";";
			//备注
			var remark = $(this).find("input[type=text]").eq(8).val();
			checkIninfoArray += remark + ";";
			//入住信息id
			var id = $(this).find("input[type=text]").eq(0).attr('sval');
			if (id == undefined || id == null) {
				id = '';
			}
			checkIninfoArray += id;
			
			if(personName == "" && roomNum == "" && extension == "" && price == "" && startDate == "" && endDate == "" && idays == "" && remark == "" && roomType == ""){
				allBlank = true;
			}else{
				checkIninfoStr.push(checkIninfoArray);
			}
			if (roomNum != "" && price != "") {
				if (roomPriceInfo[roomNum] == undefined) {
					roomPriceInfo[roomNum] = price;
				} else if (roomPriceInfo[roomNum] != price && $.inArray(roomNum, priceConflictRoomArray) == -1) {
					priceConflictRoomArray.push(roomNum);
				}
			}
		});
	}
	
	if(blankFlag && (!allBlank)){
		parent.showInfoMessage(infoMessage);
		return;
	}
	
	subData.checkIninfoStr = checkIninfoStr.join("##");
	if (priceConflictRoomArray && priceConflictRoomArray.length > 0) {
		parent.popupPromptBox("提示", priceConflictRoomArray.join(",") + "房价填写不一致，是否继续保存？", function() {
			$.ajax({
				url: '/hotelManager/saveHotelAndCheckInInfo',
				type: 'post',
				data: subData,
				datatype: 'json',
				success: function(response){
					if(response.success){
						parent.showSuccessMessage("保存成功");
						closeRightWin();
					}else{
						parent.showErrorMessage(response.message);
						return;
					}
				}
			});
		});
	} else {
		$.ajax({
			url: '/hotelManager/saveHotelAndCheckInInfo',
			type: 'post',
			data: subData,
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("保存成功");
					closeRightWin();
				}else{
					parent.showErrorMessage(response.message);
					return;
				}
			}
		});
	}
	
}

//删除宾馆信息
function deleteHotelInfo(){
	parent.popupPromptBox("提示", "是否要删除该宾馆信息？", function(){
		$.ajax({
			url: '/hotelManager/deleteHotelInfo',
			type: 'post',
			data: {"hotelId": $("#hotelId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除成功");
					closeRightWin();
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	});
	
}

//获取宾馆下拉列表数据
function getHotelSelectData(){
	var hotelId =$("#hotelId").val();
	$.ajax({
		url: '/hotelManager/queryPeopleNameList',
		type: 'post',
		data:{hotelId:hotelId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				//入住人员姓名列表
				var nameList = response.nameList;
				//房间号列表
				var roomNoList = response.roomNoList;
				//分机号列表
				var extensionList = response.extensionList;
				if(nameList.length != 0){
					var html = [];
					for(var i= 0; i< nameList.length; i++){
						html.push('<li onclick="selectPersonName(this, event)">' + nameList[i] + '</li>');
					}
					$("#searchPersonList").append(html.join(""));
				}
				
				//房间号下拉框
				if (roomNoList != null && roomNoList != undefined) {
					for(var i=0; i <roomNoList.length; i++){
						$("#roomNumSelect").append("<option value='"+ roomNoList[i].roomNo +"'>"+ roomNoList[i].roomNo +"</option>");
					}
				}
				$("#roomNumSelect").selectpicker("refresh");
				
				//入住人姓名下拉框
				if (nameList != null && nameList != undefined) {
					for(var i=0; i <nameList.length; i++){
						$("#peopleNameSelect").append("<option value='"+ nameList[i] +"'>"+ nameList[i] +"</option>");
					}
				}
				$("#peopleNameSelect").selectpicker("refresh");
				
				//分机号下拉框
				if (extensionList != null && extensionList != undefined) {
					for(var i=0; i <extensionList.length; i++){
						if (extensionList[i].extension != null && extensionList[i].extension != '') {
							$("#extensionSelect").append("<option value='"+ extensionList[i].extension +"'>"+ extensionList[i].extension +"</option>");
						}
					}
				}
				$("#extensionSelect").selectpicker("refresh");
			}
			
		}
	});
}

function showPersonName(own,ev){
	$("#searchRoomTypeList").hide();
	inputIndex= $(own);
	var $this = $(own);
	$("#searchPersonList").css({"left": $this.position().left-25, "top": $this.position().top+$this.outerHeight()}).show();
	$("#searchPersonList").children("li").show();
	ev.stopPropagation();
}


var inputIndex;
//检索住宿人员
function searchPersonName(own){
	var $this = $(own);
	inputIndex = $(own);
	if($this.val() != ""){
		var _subList = $("#searchPersonList").children("li");
		_subList.hide();
		var searchFlag = false;
		$.each(_subList, function(){
			if($(this).text().search($.trim($this.val()))!=-1){
        	   searchFlag = true;
               $(this).show();
               $("#searchPersonList").css({"left": $this.position().left, "top": $this.position().top+$this.outerHeight()}).show();
           }else{
        	
               $(this).hide();
//               $("#searchPersonList").hide();
           }
		});
		
		if (!searchFlag) {
			$("#searchPersonList").hide();
	    }
	}else{
		$("#searchPersonList").hide();
	}
}

//选择人员名称
function selectPersonName(own, ev){
	var name = $(own).text();
	inputIndex.val(name);
	$("#searchPersonList").hide();
	ev.stopPropagation();
}




//显示统一设置列表
function showRepeatSetList(own,ev){
	var checkedLength = 0;
	$("input[type=checkBox][name=setDate]:checked").each(function(){
		checkedLength ++;
	});
	if(checkedLength > 0){
		var position = $(own).position();
		var height = $(own).outerHeight();
		$("#value_standard").val("").attr("disabled", true).prev("label").find("input[type=checkbox]").prop("checked", false);
		$("#checkout_standard").val("").attr("disabled", true).prev("label").find("input[type=checkbox]").prop("checked", false);
		$("#checkin_standard").val("").attr("disabled", true).prev("label").find("input[type=checkbox]").prop("checked", false);
		$("#repeatSetList").slideDown(300).css({"left": position.left, "top": position.top+height});
		ev.stopPropagation();
	}else{
		parent.showInfoMessage("请选择需要统一设置入住时间的住宿人员");
		return;
	}
	
}
//阻止冒泡事件
function preventPopEvent(ev){
	ev.stopPropagation();
}

//设置是否可编辑
function setDisabled(own){
	if($(own).is(':checked')){
		$(own).parent("label").next("input[type=text]").attr("disabled", false);
	}else{
		$(own).parent("label").next("input[type=text]").attr("disabled", true);
	}
}

function repeatSetValue(){
	var startDate = $("#checkin_standard").val();
	var endDate = $("#checkout_standard").val();
	var value = $("#value_standard").val();
	 
	
	if(($("#checkin_standard").attr("disabled") != "disabled") && ($("#checkout_standard").attr("disabled") != "disabled")){
		if(startDate != "" && endDate != ""){
			var indate   =   new   Date(startDate.replace(/-/g,   "/")); 
			var outdate   =   new   Date(endDate.replace(/-/g,   "/"));
			var day; 
			if(indate <= outdate){
				day = calculateDayMethod(startDate, endDate);
				$("input[type=checkBox][name=setDate]:checked").each(function(){
				    $(this).parents("tr").find("input[type=text]").eq(2).val(startDate);
				    $(this).parents("tr").find("input[type=text]").eq(3).val(endDate);
				    
				    $(this).parents("tr").find("input.idays").val(day);
				    if($("#value_standard").attr("disabled") != "disabled"){
				    	$(this).parents("tr").find("input[type=text]").eq(6).val(value);
				    }
				});
			}
		}
		
		
	}else{
		if(($("#checkin_standard").attr("disabled") != "disabled") && ($("#checkin_standard").val() != "")){
			$("input[type=checkBox][name=setDate]:checked").each(function(){
			    $(this).parents("tr").find("input[type=text]").eq(2).val(startDate);
			    var date = $(this).parents("tr").find("input[type=text]").eq(3).val();
			    var checkinDate   =   new   Date(startDate.replace(/-/g,   "/")); 
				if(date != ""){
					var checkOutDate   =   new   Date(date.replace(/-/g,   "/"));
					if(checkinDate > checkOutDate){
						$(this).parents("tr").find("input[type=text]").eq(3).val("");
						$(this).parents("tr").find("input.idays").val("");
					}else{
						var day = calculateDayMethod(startDate, date);
					    $(this).parents("tr").find("input.idays").val(day);
					}
				}
				
			});
		}
		if(($("#checkout_standard").attr("disabled") != "disabled") && ($("#checkout_standard").val()!= "")){
			$("input[type=checkBox][name=setDate]:checked").each(function(){
			    $(this).parents("tr").find("input[type=text]").eq(3).val(endDate);
			    var date = $(this).parents("tr").find("input[type=text]").eq(2).val();
				var checkOutDate   =   new   Date(endDate.replace(/-/g,   "/"));
				if(date != ""){
					var checkinDate   =   new   Date(date.replace(/-/g,   "/")); 
					if(checkinDate > checkOutDate){
						$(this).parents("tr").find("input[type=text]").eq(3).val("");
						$(this).parents("tr").find("input.idays").val("");
					}else{
						var day = calculateDayMethod(date, endDate);
					    $(this).parents("tr").find("input.idays").val(day);
					}
				}
				
			});
		}
		if(($("#value_standard").attr("disabled") != "disabled") && ($("#value_standard").val()!= "")){
			$("input[type=checkBox][name=setDate]:checked").each(function(){
				$(this).parents("tr").find("input[type=text]").eq(6).val(value);
			});
	    	
	    }
	}
	$("#repeatSetList").slideUp(300);
}

//清空值
function cancelSetValue(){
	$("#value_standard").val("");
	$("#checkout_standard").val("");
	$("#checkin_standard").val("");
}

//计算入住天数
function calculateDayMethod(startDate, endDate){
	var  indate   =   new   Date(startDate.replace(/-/g,   "/")); 
	var  outdate   =   new   Date(endDate.replace(/-/g,   "/")); 
	if(indate > outdate){
		parent.showInfoMessage("入住时间不能大于退房时间");
		$(own).val("");
		return false;
	}
	var strSeparator = "-"; //日期分隔符
    var oDate1;
    var oDate2;
    var iDays;
    oDate1= startDate.split(strSeparator);
    oDate2= endDate.split(strSeparator);
    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
    if(iDays == 0){
    	iDays = iDays + 1;
    }
    return iDays;
}

//显示下拉列表框
function showSelectWin(own,ev) {
	$("#searchPersonList").hide();
	inputIndex= $(own);
	var $this = $(own);
	$("#searchRoomTypeList").css({"left": $this.position().left, "top": $this.position().top+$this.outerHeight()}).show();
	$("#searchRoomTypeList").children("li").show();
	ev.stopPropagation();
}

//选择房间类型
function selectRoomType(own,ev) {
	var name = $(own).text();
	inputIndex.val(name);
	$("#searchRoomTypeList").hide();
	ev.stopPropagation();
}

//检索房间类型
function searchRoomType(own) {
	var $this = $(own);
	inputIndex = $(own);
	if($this.val() != ""){
		var _subList = $("#searchRoomTypeList").children("li");
		_subList.hide();
		var searchFlag = false;
		$.each(_subList, function(){
			if($(this).text().search($.trim($this.val()))!=-1){
        	   searchFlag = true;
               $(this).show();
               $("#searchRoomTypeList").css({"left": $this.position().left, "top": $this.position().top+$this.outerHeight()}).show();
           }else{
        	
               $(this).hide();
//               $("#searchPersonList").hide();
           }
		});
		
		if (!searchFlag) {
			$("#searchRoomTypeList").hide();
	    }
	}else{
		$("#searchRoomTypeList").hide();
	}

}

//显示备注面板
function showRemarkPanel(own, ev){
	$("span[class*='open']").each(function() {
		var changeMessage = "";
		var textArr = $(this).siblings("div").find("input[type=text]");
		if (textArr != undefined && textArr != null) {
			for(var i=0; i<textArr.length; i++){
				var text = $(textArr[i]).val();
				if (text != '' && text != null) {
					changeMessage = text;
				}
			}
		}
		
		if (changeMessage == '') {
			$(this).removeClass("open");
		}
		$(this).siblings("div").slideUp(300);
	});
	
	if(!$(own).hasClass("open")){
		$(own).addClass("open");
	}
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#hotelRemarkPanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	ev.stopPropagation();
}

//显示价格区间面板
function showRoomPricePanel(own, ev){
	$("span[class*='open']").each(function() {
		var changeMessage = "";
		var textArr = $(this).siblings("div").find("input[type=text]");
		if (textArr != undefined && textArr != null) {
			for(var i=0; i<textArr.length; i++){
				var text = $(textArr[i]).val();
				if (text != '' && text != null) {
					changeMessage = text;
				}
			}
		}
		
		if (changeMessage == '') {
			$(this).removeClass("open");
		}
		$(this).siblings("div").slideUp(300);
	});
	if(!$(own).hasClass("open")){
		$(own).addClass("open");
	}
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#hotelRoomPricePanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	ev.stopPropagation();
}

//显示入住天数面板
function showIndaysPanel(own, ev){
	$("span[class*='open']").each(function() {
		var changeMessage = "";
		var textArr = $(this).siblings("div").find("input[type=text]");
		if (textArr != undefined && textArr != null) {
			for(var i=0; i<textArr.length; i++){
				var text = $(textArr[i]).val();
				if (text != '' && text != null) {
					changeMessage = text;
				}
			}
		}
		
		if (changeMessage == '') {
			$(this).removeClass("open");
		}
		$(this).siblings("div").slideUp(300);
	});
	if(!$(own).hasClass("open")){
		$(own).addClass("open");
	}
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#hotelIndaysPanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	ev.stopPropagation();
}

//显示选择日期面板
function showCheckInDatePanel(own, ev){
	$("span[class*='open']").each(function() {
		var changeMessage = "";
		var textArr = $(this).siblings("div").find("input[type=text]");
		if (textArr != undefined && textArr != null) {
			for(var i=0; i<textArr.length; i++){
				var text = $(textArr[i]).val();
				if (text != '' && text != null) {
					changeMessage = text;
				}
			}
		}
		
		if (changeMessage == '') {
			$(this).removeClass("open");
		}
		$(this).siblings("div").slideUp(300);
	});
	if(!$(own).hasClass("open")){
		$(own).addClass("open");
	}
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#hotelCheckInDatePanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	ev.stopPropagation();
}

//显示选择日期面板
function showCheckOutDatePanel(own, ev){
	$("span[class*='open']").each(function() {
		var changeMessage = "";
		var textArr = $(this).siblings("div").find("input[type=text]");
		if (textArr != undefined && textArr != null) {
			for(var i=0; i<textArr.length; i++){
				var text = $(textArr[i]).val();
				if (text != '' && text != null) {
					changeMessage = text;
				}
			}
		}
		
		if (changeMessage == '') {
			$(this).removeClass("open");
		}
		$(this).siblings("div").slideUp(300);
	});
	if(!$(own).hasClass("open")){
		$(own).addClass("open");
	}
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#hotelCheckOutDatePanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	ev.stopPropagation();
}

//查询备注的数据
function queryRemarkData(ev){
	var remark = $("#remarkText").val();
	if (remark == '') {
		$("#selectRemarkBtn").removeClass("open");
	}
	$("#hotelRemarkPanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//清空备注的数据
function clearRemarkData(ev){
	$("#remarkText").val('');
	$("#selectRemarkBtn").removeClass("open");
	$("#hotelRemarkPanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}


//查询天数区间的数据
function queryIndaysTimeData(ev){
	var indaysStart = $("#indaysStart").val();
	var indaysEnd = $("#indaysEnd").val();
	if (indaysStart == '' && indaysEnd == '') {
		$("#selectIndaysDateBtn").removeClass("open");
	}
	$("#hotelIndaysPanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//清空天数区间的数据
function clearIndaysTimeData(ev){
	$("#indaysStart").val('');
	$("#indaysEnd").val('');
	$("#selectIndaysDateBtn").removeClass("open");
	$("#hotelIndaysPanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//查询价格区间的数据
function queryRoomPriceData(ev){
	//最小房价
	var startRoomPrice = $("#roomPriceStart").val();
	//最大房价
	var endRoomPrice = $("#roomPriceEnd").val();
	if (startRoomPrice == '' && endRoomPrice == '') {
		$("#selectRoomPriceDateBtn").removeClass("open");
	}
	$("#hotelRoomPricePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//清空价格区间
function clearRoomPriceData(ev) {
	$("#roomPriceStart").val('');
	$("#roomPriceEnd").val('');
	$("#selectRoomPriceDateBtn").removeClass("open");
	$("#hotelRoomPricePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//查询时间段的数据
function queryCheckInDateTimeData(ev){
	var checkInStartDate = $("#checkInStartDate").val();
	var checkInEndDate = $("#checkInEndDate").val();
	if (checkInStartDate == '' && checkInEndDate == '') {
		$("#selectCheckInDateBtn").removeClass("open");
	}
	$("#hotelCheckInDatePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}


//清空入住时间
function clearCheckInDateTimeData(ev) {
	$("#checkInStartDate").val('');
	$("#checkInEndDate").val('');
	$("#selectCheckInDateBtn").removeClass("open");
	$("#hotelCheckInDatePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//查询时间段的数据
function queryCheckOutDateTimeData(ev){
	var checkOutStartDate = $("#checkOutStartDate").val();
	var checkOutEndDate = $("#checkOutEndDate").val();
	if (checkOutStartDate == '' && checkOutEndDate =='') {
		$("#selectCheckOutDateBtn").removeClass("open");
	}
	$("#hotelCheckOutDatePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//清空退房时间
function clearCheckOutDateTimeData(ev) {
	$("#checkOutStartDate").val('');
	$("#checkOutEndDate").val('');
	$("#selectCheckOutDateBtn").removeClass("open");
	$("#hotelCheckOutDatePanel").slideUp(300);
	confirmSreachCheckinHotel();
	ev.stopPropagation();
}

//点击筛选框查询数据
function initSelectData() {
	//房间号筛选
	$('#roomNumSelect').on('change',function(){
		//获取选中的条件
		confirmSreachCheckinHotel();
		//更改箭头
		var $roomNum = $("#roomNumSelect").val();
		var $span = $("div[class*='roomNo-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='roomNo-select'] span[class='up-arrow']");
		}
		if ($roomNum == '' || $roomNum == undefined || $roomNum == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//入住人员姓名
	$('#peopleNameSelect').on('change',function(){
		//获取选中的条件
		confirmSreachCheckinHotel();
		
		//更改箭头
		var $peopleName = $("#peopleNameSelect").val();
		var $span = $("div[class*='people-name-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='people-name-select'] span[class='up-arrow']");
		}
		if ($peopleName == '' || $peopleName == undefined || $peopleName == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//房间类型
	$('#roomTypeSelect').on('change',function(){
		//获取选中的条件
		confirmSreachCheckinHotel();
		//更改箭头
		var $roomType = $("#roomTypeSelect").val();
		var $span = $("div[class*='room-type-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='room-type-select'] span[class='up-arrow']");
		}
		if ($roomType == '' || $roomType == undefined || $roomType == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//分机号
	$('#extensionSelect').on('change',function(){
		//获取选中的条件
		confirmSreachCheckinHotel();
		//更改箭头
		var $extension = $("#extensionSelect").val();
		var $span = $("div[class*='extension-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='extension-select'] span[class='up-arrow']");
		}
		if ($extension == '' || $extension == undefined || $extension == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
}

//阻止冒泡时间
function stopSlideup(ev) {
	ev.stopPropagation();
}

//高级查询
function confirmSreachCheckinHotel() {
	//房间号
	var roomNo = $("#roomNumSelect").val();
	if(roomNo!= null && roomNo!=""){
		var roomNoStr = '';
		for(var i=0;i<roomNo.length;i++){
			roomNoStr+=roomNo[i]+",";
		}
		roomNoStr=roomNoStr.substring(0,roomNoStr.length-1);
		filter.roomNo=roomNoStr;
	}else{
		filter.roomNo="";
	}
	
	//入住人姓名
	var peopleName = $("#peopleNameSelect").val();
	if(peopleName!= null && peopleName!=""){
		var peopleNameStr = '';
		for(var i=0;i<peopleName.length;i++){
			peopleNameStr+=peopleName[i]+",";
		}
		peopleNameStr=peopleNameStr.substring(0,peopleNameStr.length-1);
		filter.peopleName=peopleNameStr;
	}else{
		filter.peopleName="";
	}
	
	//入住开始时间
	var checkInStartDate = $("#checkInStartDate").val();
	if (checkInStartDate == undefined) {
		checkInStartDate = null;
	}
	filter.checkInStartDate = checkInStartDate;
	
	//入住结束时间
	var checkInEndDate = $("#checkInEndDate").val();
	if (checkInEndDate == undefined) {
		checkInEndDate = null;
	}
	filter.checkInEndDate = checkInEndDate;
	
	//退房开始时间
	var checkoutStartDate = $("#checkOutStartDate").val();
	if (checkoutStartDate == undefined) {
		checkoutStartDate = null;
	}
	filter.checkoutStartDate = checkoutStartDate;
	
	//退房结束时间
	var checkOutEndDate = $("#checkOutEndDate").val();
	if (checkOutEndDate == undefined) {
		checkOutEndDate = null;
	}
	filter.checkOutEndDate = checkOutEndDate;
	
	//入住最小天数
	var startInTimes = $("#indaysStart").val();
	if (startInTimes == undefined) {
		startInTimes = '';
	}
	if (isNaN(startInTimes)) {
		parent.showErrorMessage("最小入住天数必须为数字！");
		return false;
	}
	filter.startInTimes = startInTimes;
	
	//入住最大天数
	var endInTimes = $("#indaysEnd").val();
	if (endInTimes == undefined) {
		endInTimes = '';
	}
	if (isNaN(endInTimes)) {
		parent.showErrorMessage("最大入住天数必须为数字！");
		return false;
	}
	filter.endInTimes = endInTimes;
	
	//房间类型
	var roomType = $("#roomTypeSelect").val();
	if(roomType!= null && roomType!=""){
		var roomTypeStr = '';
		for(var i=0;i<roomType.length;i++){
			roomTypeStr+=roomType[i]+",";
		}
		roomTypeStr=roomTypeStr.substring(0,roomTypeStr.length-1);
		filter.roomType=roomTypeStr;
	}else{
		filter.roomType="";
	}
	
	//最小房价
	var startRoomPrice = $("#roomPriceStart").val();
	if (startRoomPrice == undefined) {
		startRoomPrice = '';
	}
	filter.startRoomPrice = startRoomPrice;
	
	//最大房价
	var endRoomPrice = $("#roomPriceEnd").val();
	if (endRoomPrice == undefined) {
		endRoomPrice = '';
	}
	filter.endRoomPrice = endRoomPrice;
	
	//分机号
	var extension = $("#extensionSelect").val();
	if(extension!= null && extension!=""){
		var extensionStr = '';
		for(var i=0;i<extension.length;i++){
			extensionStr+=extension[i]+",";
		}
		extensionStr=extensionStr.substring(0,extensionStr.length-1);
		filter.extension=extensionStr;
	}else{
		filter.extension="";
	}
	
	//备注
	var remark = $("#remarkText").val();
	if (remark == undefined) {
		remark = '';
	}
	filter.remark = remark;
	//酒店id
	var hotelId = $("#hotelId").val();
	filter.hotelId = hotelId;
	$.ajax({
		url:'/hotelManager/queryCheckinInfoList',//后台提供的接口
		type:'POST',
		data:filter,
	    dataType:'json', //返回json格式
	    success:function(data){//这个data就是后台给你的json格式的数据
	    	if(data.success){
	    		var checkInfoList = data.checkInfoList;
	    		//清空之前的shuju
	    		$("#hotelPeopleData").html('');
	    		reloadDetailOfHotel(checkInfoList);
	    	}else{
				parent.showErrorMessage(data.message);
			}
	        
	    }
	});
}

//生成详细信息
function reloadDetailOfHotel(checkInList){
	var html = [];
	if(checkInList.length != 0){
		
		for(var i= 0; i< checkInList.length; i++){
			var day = new Date();
			var checkoutDate = new Date(checkInList[i].checkoutDate.replace(/-/g,   "/"));
			if(i != 0){//第一行
				var num = i-1;
				if(checkInList[i].roomNo == checkInList[num].roomNo){
					
					if(day < checkoutDate){
						html.push('<tr class="tr-'+ num +' right-popup-table-tr" arr="tr-'+ num +'" id="'+ checkInList[i].id +'">');
					}else{
						html.push('<tr class="tr-'+ num +' right-popup-table-tr" arr="tr-'+ num +'" id="'+ checkInList[i].id +'">');
					}
					//房间号
					html.push('<td style="width: 8%; min-width: 8%; max-width: 8%; border-top: 0px;"><input type="text" style="text-align: center;"  value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
				}else{
					if(day < checkoutDate){
						html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
					}else{
						html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
					}
					//房间号
					html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" style="text-align: center;" value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
				}
			}else{
				if(day < checkoutDate){
					html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
				}else{
					html.push('<tr class="tr-'+ i +'" arr="tr-'+ i +'" id="'+ checkInList[i].id +'">');
				}
				//房间号
				html.push('<td  style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" style="text-align: center;" value="'+ checkInList[i].roomNo +'" sval="'+ checkInList[i].id +'"></td>');
			}
			//单选框
			html.push('<td style="width: 3%; min-width: 3%; max-width: 3%;"><input type="checkbox" name="setDate" onclick="isCheckAll()"></td>');
			
			//姓名
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;"><span class="td-span">');
			html.push('<input type="text" value="'+ checkInList[i].peopleName +'" onclick="showPersonName(this,event)" onkeyup="searchPersonName(this)">');
			html.push(' <span class="operate-btn"><input class="delet-row-btn" type="button" onclick="deleteHotelPerson(this)"></span></span>');
			html.push('</td>');
			//入住时间
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;">');
			html.push(' <input class="startTime" type="text" style="text-align: center;" value="'+ checkInList[i].checkInDate +'" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})">');
			html.push('</td>');
			//退房时间
			html.push('<td style="width: 12%; min-width: 12%; max-width: 12%;">');
			html.push(' <input class="endTime" type="text" style="text-align: center;" value="'+ checkInList[i].checkoutDate +'" onfocus="WdatePicker({isShowClear:false,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)}})">');
			html.push('</td>');
			//入住天数
			if((checkInList[i].inTimes == null) || (checkInList[i].inTimes == "") || (checkInList[i].inTimes == "null")){
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input class="idays" type="text" style="text-align: right;" value=""></td>');
			}else{
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input class="idays" type="text" style="text-align: right;" value="'+ checkInList[i].inTimes +'"></td>');
			}
			
			//房间类型
			var roomType = checkInList[i].roomType;
			if (roomType == null || roomType == '' || roomType == "null") {
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"  class="room-type"><input class="insert-input" type="text" style="text-align: center;" value="" onclick="showSelectWin(this,event)" onkeyup="searchRoomType(this)"></td>');
			}else {
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"  class="room-type"><input class="insert-input" type="text" style="text-align: center;" value="'+ roomType +'" onclick="showSelectWin(this,event)" onkeyup="searchRoomType(this)"></td>');
			}
			
			//房价
			if(checkInList[i].roomPrice == null || checkInList[i].roomPrice == "" || checkInList[i].roomPrice == "null"){
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" style="text-align: right;" value="" onkeyup="checkOutNumber(this)" onblur="formatCost(this)"></td>');
			}else{
				html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" style="text-align: right;" value="'+ fmoney(checkInList[i].roomPrice) +'" onkeyup="checkOutNumber(this)" onblur="formatCost(this)"></td>');
			}
			//分机号
			html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" value="'+ checkInList[i].extension +'"></td>');
			
			//备注
			if(checkInList[i].remark == null || checkInList[i].remark == "" || (checkInList[i].remark == "null")){
				html.push('<td style="width: 13%; min-width: 13%; max-width: 13%;"><input type="text" value=""></td>');
			}else{
				html.push('<td style="width: 13%; min-width: 13%; max-width: 13%;"><input type="text" value="'+ checkInList[i].remark +'"></td>');
			}
			
			html.push('</tr>');
		}
		$("#hotelPeopleData").empty();
		$("#hotelPeopleData").append(html.join(""));
		$("#selectAll").attr("disabled", false);
		
		
		if (isHotelInfoReadonly) {
			$(".delet-row-btn").remove();
		}
	}else{
		html.push('<tr class="blank-tr">');
		html.push('<td style="text-align: center; vertical-align: center;" colspan="9">暂无数据</td>');
		html.push('</tr>');
		$("#hotelPeopleData").empty();
		$("#hotelPeopleData").append(html.join(""));
		$("#selectAll").attr("disabled", true);
	}
	
}