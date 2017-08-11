var weatherInfoWindow;
var crewType = null;
var userGrid;
var crewName = null;
var date = null;
var saveDate = null;
var isSaved = false;
var noticePublished = null;
var isAutoSave = false;
var isHaveFacdbcak = false;
var shootRegionList;
/*var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];*/
$(document).ready(function() {
	//获取地域信息
	queryAllRegionInfo();
	
	//获取剧组类型
	getCrewType();
	
	//加载分组列表
	//loadGroupListDta();
	
	//加载通告单数据
	loadNoticeData();
	
	//初始化timepicker
	/*$(".timepicker").timePicker({
	    height: 20,
	    width: 47,
	    listHeight: 150,
	    interval:15
	});*/
	/*$(".timepicker").timePicker().on('changeTime.timepicker', function(e){
		debugger;
		isSaved = true;
	});*/
	/*$(document).on("click", function(){
		$("#uniformArriveTimeDiv").jqxWindow("close");
		$("#uniformMakeupTimeDiv").jqxWindow("close");
	});*/
	userGrid = new UserListGrid("userList");
	userGrid.loadTable();
	//初始化化妆时间和出发时间弹窗
	initMakeupTimeWin();
	//新建分组
	//createNewGroup();
	//初始化地图
	initBaiduMapWin();
	/*$(window).resize(function(){
		var width = window.document.body.scrollWidth;
		var height = window.document.body.scrollHeight;
		$("#winModalZIndex").css({"width": width, "height": height});
		
	});*/
	
	//只读权限，去掉保存、发布按钮
	if(isNoticeReadonly) {		
		$(".close-botton-left").remove();
		$(".publish-button").remove();
	}
});

//跳转到通告单详情页面需要加载的shuju
function loadNoticeData(){
	//取出通告单id
	var noticeId = $("#generateNoticeId").val();
		$.ajax({
			url: '/notice/queryGenerateNoticeData',
			type: 'post',
			data:{noticeId:noticeId},
			async: true,
			datatype: 'json',
			success: function(response){
				if(response.success){
					//获取反馈列表
					var facdbackList = response.facdbackList;
					if (facdbackList != null && facdbackList.length != 0) {
						isHaveFacdbcak = true;
					}
					var dataMap = response.data;
					noticePublished = dataMap['noticePublished'];
					
					loadNoticeTableData(dataMap);
					
					var viewInfoList = dataMap['locationGroupList'];
					if (viewInfoList == null || viewInfoList.length == 0) {
						$("#publishButton").hide();
					}
					
					//重置添加场景按钮位置
					resizeAddNoticeView();
				}
			}
		});
}

//获取当前剧组类型
function getCrewType(){
	$.ajax({
		url: '/scenarioManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
			}else{
					parent.showErrorMessage(response.message);
			}
		}
	});
}

//加载返回的通告单的数据
function loadNoticeTableData(dataMap){
	
	//加载剧组名称
	$noticeTime = $("#noticeTime");
	var noticeName = dataMap['noticeName'];
	if (noticeName == null || noticeName == '' || noticeName == undefined) {
		$noticeTime.before(" <span class='crew-name-span' id='crewNameSpan'>《" + dataMap['crewName'] + "》" + dataMap['noticeDate'] + dataMap['groupName'] + "通告</span>");
	}else {
		$noticeTime.before(" <span class='crew-name-span' id='crewNameSpan'>" +noticeName + "</span>");
	}
	
	//加载时间数据
	$noticeTime.val(dataMap['noticeDate']);
	//加载分组选中数据
	/*var option = $("#noticeGroupSelect option");
	for(var i = 0; i<option.length; i++){
		if ($(option[i]).text() == dataMap['groupName']) {
			option[i].selected = 'selected';
			break;
		}
	}*/
	$("#noticeGroupSelect").val(dataMap['groupName']);
	
	//取出通告单的时间信息
	var noticeTime = dataMap['myNoticeTime'];
	var lastGroupNoticeTime = dataMap['lastGroupNoticeTime'];
	//拼接版本信息
	var $noticeTitleContent = $("#noticeTitleContent");
	if (noticeTime == null) {
		isAutoSave = true;
		$noticeTitleContent.append(" <input type='text' class='notice-version-span' id='versionInfo' value='第一版' onkeyup='changeStaus()'></input>");
	}else {
		$noticeTitleContent.append(" <input type='text' class='notice-version-span' id='versionInfo' value='"+ noticeTime.version +"' onkeyup='changeStaus()'></input>");
	}
	
	//拼接天气信息
	var weatherInfoButton = $("#getWeatherInfoButton");
	if (noticeTime != null) {
		weatherInfoButton.before(" <span class='second-row-p' id='weatherInfoSpan'>天气：<input class='wather-info-input' id='weatherInfo' value='" + noticeTime.weatherInfo + "' onkeyup='changeStaus()'><input type='text' style='display: none;' /></span>&nbsp;&nbsp;");
	}else {
		weatherInfoButton.before(" <span class='second-row-p' id='weatherInfoSpan'>天气：<input class='wather-info-input' id='weatherInfo' onkeyup='changeStaus()'><input type='text' style='display: none;' /></span>&nbsp;&nbsp;");
	}
	//拼接第多少天星期日期
	var weatherInfoSpan = $("#weatherInfoSpan");
	weatherInfoSpan.before(" <span>第" + dataMap['shootDays'] + "天，星期" + dataMap['day'] + "</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	
	//拼接拍摄地点
	var shootLocationInput = $("#shootLocationInput");
	if (noticeTime == null || noticeTime.length == 0) {
		var viewLocations = dataMap['viewLocations'];
		if (viewLocations == null || viewLocations.length == 0) {
			shootLocationInput.val();
		}else {
			isAutoSave = true;
			shootLocationInput.val(dataMap['viewLocations']);
		}
	}else {
		var shootLocationInfos = noticeTime.shootLocationInfos;
		if (shootLocationInfos == null || shootLocationInfos == '' || shootLocationInfos == undefined) {
			var viewLocations = dataMap['viewLocations'];
			if (viewLocations == null || viewLocations.length == 0) {
				shootLocationInput.val();
			}else {
				isAutoSave = true;
				shootLocationInput.val(dataMap['viewLocations']);
			}
		}else {
			shootLocationInput.val(shootLocationInfos);
		}
	}

	/*var notice_location_html = [];
	if(noticeTime == null || noticeTime.length == 0){
		var viewLocations = dataMap['viewLocations'];
		if(viewLocations == null || viewLocations.length == 0){
			notice_location_html.push('<input class="shoot-location-input" type="text">');
			notice_location_html.push('<input class="shoot-location-button" type="button" onclick="showMapWindow(this)">');
			$("#shoot_location_map").append(notice_location_html.join(""));
		}else{
			isAutoSave = true;
			var viewLocationStr = dataMap['viewLocations'].replace(/，/g, ",");
			var viewLocationArray = viewLocationStr.split(",");
			for(var i= 0; i< viewLocationArray.length; i++){
				notice_location_html.push('<input class="shoot-location-input" type="text" value="'+ viewLocationArray[i] +'">');
				notice_location_html.push('<input class="shoot-location-button" type="button" onclick="showMapWindow(this)">');
			}
			$("#shoot_location_map").append(notice_location_html.join(""));
		}
	}else{
		var shootLocationInfos = noticeTime.shootLocationInfos;
		if(shootLocationInfos == null || shootLocationInfos == '' || shootLocationInfos == undefined){
			var viewLocations = dataMap['viewLocations'];
			if(viewLocations == null || viewLocations.length == 0){
				notice_location_html.push('<input class="shoot-location-input" type="text">');
				notice_location_html.push('<input class="shoot-location-button" type="button" onclick="showMapWindow(this)">');
				$("#shoot_location_map").append(notice_location_html.join(""));
			}else{
				isAutoSave = true;
				var viewLocationStr = dataMap['viewLocations'].replace(/，/g, ",");
				var viewLocationArray = viewLocationStr.split(",");
				for(var i= 0; i< viewLocationArray.length; i++){
					notice_location_html.push('<input class="shoot-location-input" type="text" value="'+ viewLocationArray[i] +'">');
					notice_location_html.push('<input class="shoot-location-button" type="button" onclick="showMapWindow(this)">');
				}
				$("#shoot_location_map").append(notice_location_html.join(""));
				
			}
		}else{
			var shootLocationInfoStr = shootLocationInfos.replace(/，/g, ",");
			var shootLocationInfoArray = shootLocationInfoStr.split(",");
			for(var i= 0; i< shootLocationInfoArray.length; i++){
				notice_location_html.push('<input class="shoot-location-input" type="text" value="'+ shootLocationInfoArray[i] +'">');
				notice_location_html.push('<input class="shoot-location-button" type="button" onclick="showMapWindow(this)">');
			}
			$("#shoot_location_map").append(notice_location_html.join(""));
		}
	}
	*/
	
	
	//拼接导演数据
	var directorNameInput = $("#directorNameInput");
	if (noticeTime == null || noticeTime.groupDirector == null || noticeTime.groupDirector == '') {
		if (lastGroupNoticeTime == null || lastGroupNoticeTime.groupDirector == null || lastGroupNoticeTime.groupDirector == '') {
			directorNameInput.val();
		}else {
			isAutoSave = true;
			directorNameInput.val(lastGroupNoticeTime.groupDirector);
		}
	}else {
		directorNameInput.val(noticeTime.groupDirector);
	}
	//拼接早餐时间
	var breakfastTime = $("#breakfastTime");
	if (noticeTime == null || noticeTime.breakfastTime == null || noticeTime.breakfastTime == '') {
		if (lastGroupNoticeTime == null || lastGroupNoticeTime.breakfastTime == null || lastGroupNoticeTime.breakfastTime == '') {
			breakfastTime.val('06:00');
		}else{
			breakfastTime.val(lastGroupNoticeTime.breakfastTime);
		}
		isAutoSave = true;
	}else {
		breakfastTime.val(noticeTime.breakfastTime);
	}
	
	//拼接出发时间
	var moveStartTime = $("#moveStartTime");
	if (noticeTime == null || noticeTime.departureTime == null || noticeTime.departureTime == '') {
		if (lastGroupNoticeTime == null || lastGroupNoticeTime.departureTime == null || lastGroupNoticeTime.departureTime == '') {
			moveStartTime.val();
		}else {
			moveStartTime.val(lastGroupNoticeTime.departureTime);
			isAutoSave = true;
		}
	}else {
		moveStartTime.val(noticeTime.departureTime);
	}
	
	//拼接通告单中的场数据和页数
	var moveStartSpan = $("#moveStartSpan");
	var viewCount = dataMap['viewCount'];
	var pageCount = dataMap['pageCount'];
	if (viewCount == null || viewCount == undefined) {
		moveStartSpan.after(" <span class='notice-count-span'>0场/0页</span>");
	}else {
		moveStartSpan.after(" <span class='notice-count-span'>" + viewCount + "场/" + parseFloat(pageCount).toFixed(1) + "页</span>");
	}
	
/*******************************拼接表格数据开始***************************************/
	//拼接剧组联系表数据
	var concatInfoList = $("#concatInfoList");
	var concatArr = [];
	var contactUserList = dataMap['contactUserList'];
	var contactInput = $("#contactInput");
	var mynoticeTime = dataMap['myNoticeTime'];
	var myLastNoticeTime = dataMap['lastGroupNoticeTime'];
	if (mynoticeTime == null || mynoticeTime.noticeContact == null || mynoticeTime.noticeContact == '') {
		if (myLastNoticeTime == null || myLastNoticeTime.noticeContact == null || myLastNoticeTime.noticeContact == '') {
			concatInfoList.val('');
		}else {
			concatInfoList.val(myLastNoticeTime.noticeContact);
			isAutoSave = true;
		}
	}else {
		concatInfoList.val(mynoticeTime.noticeContact);
	}
	
	//var lastContactUserList = dataMap['lastContactUserList'];
	if (contactUserList == null || contactUserList == undefined) {
		contactInput.val(dataMap['lastContactUserIds']);
		
	}else {
		contactInput.val(dataMap['contactUserIds']);
	}
	concatInfoList.text(concatArr.join(""));
	
	//商植信息
	var $advertInfo = $("#advertTextArea");
	
	if (noticeTime == null|| noticeTime.insideAdvert == null || noticeTime.insideAdvert == '') {
		var allAdvertNames = dataMap['allAdvertNames'];
		if (allAdvertNames == null || allAdvertNames == "") {
			$advertInfo.append(" <textarea class='advert-content-textarea' id='advertInfo' onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>");
		}else {
			isAutoSave = true;
			$advertInfo.append(" <textarea class='advert-content-textarea' id='advertInfo' onkeyup='changeStaus()' onpaste='changeStaus()'>" + allAdvertNames + "</textarea>");
		}
	}else {
		$advertInfo.append(" <textarea class='advert-content-textarea' id='advertInfo' onkeyup='changeStaus()' onpaste='changeStaus()'>" + noticeTime.insideAdvert + "</textarea>");
	}
	
	//拼接特约演员和群众演员的信息
	var roleInfo = $("#roleInfo");
	if (noticeTime == null || noticeTime.roleInfo == null || noticeTime.roleInfo == '') {
		isAutoSave = true;
		var massCount = dataMap['massCount'];
		var massrole = dataMap['massrole'];
		var guestRoleCount = dataMap['guestRoleCount'];
		var guestrole = dataMap['guestrole'];
		if (massCount == null || massCount == undefined) {
			if (guestRoleCount == null || guestRoleCount == undefined) {
				roleInfo.text("特约演员0人" + "\r\n" + "群众演员0人" );
			}else {
				roleInfo.text("特约演员0人" + "\r\n" + guestRoleCount + "人：" + guestrole);
			}
		}else {
			if (guestRoleCount == null || guestRoleCount == undefined) {
				roleInfo.text(massCount + "人：" + massrole + "\r\n" + "群众演员0人");
			}else {
				roleInfo.text( massCount + "人：" + massrole + guestRoleCount + "人：" + guestrole);
			}
		}
	}else {
		roleInfo.text(noticeTime.roleInfo);
	}
	
	//拼接提示信息
	var pointInfo = $("#pointInfo");
	if (noticeTime != null) {
		pointInfo.text(noticeTime.note);
	}
	
	//拼接备注信息
	var remarkInfo = $("#remarkInfo");
	if (noticeTime == null || noticeTime.remark == null || noticeTime.remark == '') {
		if (lastGroupNoticeTime == null || lastGroupNoticeTime.remark == null) {
			remarkInfo.text();
		}else {
			remarkInfo.text(lastGroupNoticeTime.remark);
		}
	}else {
		remarkInfo.text(noticeTime.remark);
	}
	
	//拼接中间 角色/演员/化妆 数据
	var $roleName = $("#roleName");
	var $actorName = $("#actorName");
	var $makeupAddress = $("#makeupAddress");
	var $makeupTime = $("#makeupTime");
	var $giveMakeupTime = $("#giveMakeupTime");
	var roleNameArr = [];
	var actorNameArr = [];
	var makeupAddressArr = [];
	var makeupTimeArr = [];
	var giveMakeupTimeArr = [];
	var mainRoleList = dataMap['mainRoleList'];
	if (mainRoleList != null && mainRoleList.length>0) {
		for (var i = 0; i < mainRoleList.length; i++) {
			var roleMap = mainRoleList[i];
			//角色信息
			var viewRoleName = roleMap['viewRoleName'];
			if (viewRoleName == null || viewRoleName == '' || viewRoleName == undefined) {
				roleNameArr.push(" <td class='center-td'></td>");
			}else {
				roleNameArr.push(" <td class='center-td'>" + roleMap['viewRoleName'] + "</td>");
			}
			
			//演员信息
			var actorName = roleMap['actorName'];
			if (actorName == null || actorName == '' || actorName == undefined) {
				actorNameArr.push(" <td class='center-td'></td>");
			}else {
				actorNameArr.push(" <td class='center-td'>" + roleMap['actorName'] + "</td>");
			}
			
			//化妆地
			var makeup = roleMap['makeup'];
			if (makeup == null || makeup == '' || makeup == undefined) {
				makeupAddressArr.push(" <td class='center-td'><input class='makeup-info-input' name='role_makeup' id='makeup_" + roleMap['viewRoleId'] + "' type='text' value='化妆间' onkeyup='changeStaus()'/></td>");
				isAutoSave = true;
			}else {
				makeupAddressArr.push(" <td class='center-td'><input class='makeup-info-input' name='role_makeup' id='makeup_" + roleMap['viewRoleId'] + "' type='text' value='" + makeup + "' onkeyup='changeStaus()'/></td>");
			}
			
			//化妆时间
			var arriveTime = roleMap['arriveTime'];
			if (arriveTime == null || arriveTime == '' || arriveTime == undefined) {
				makeupTimeArr.push(" <td  class='four-center-td'><input  id='arrive_" + roleMap['viewRoleId'] + "' type='text' name='role_arriveTime' placeholder='06:00' onkeyup='checkTagFen(this)' /></td>");
			}else {
				makeupTimeArr.push(" <td  class='four-center-td'><input  id='arrive_" + roleMap['viewRoleId'] + "' type='text' name='role_arriveTime' value='" + arriveTime + "' onkeyup='checkTagFen(this)' /></td>");
			}
			
			//出发时间
			var giveMakeupTime = roleMap['giveMakeupTime'];
			if (giveMakeupTime == null || giveMakeupTime == '' || giveMakeupTime == undefined) {
				giveMakeupTimeArr.push(" <td class='five-center-td'><input  id='givemakeup_" + roleMap['viewRoleId'] + "' type='text' name='role_giveMakeupTime' placeholder='06:30' onkeyup='changeStaus()' /></td>");
			}else {
				giveMakeupTimeArr.push(" <td class='five-center-td'><input  id='givemakeup_" + roleMap['viewRoleId'] + "' type='text' name='role_giveMakeupTime' value='" + giveMakeupTime + "' onkeyup='changeStaus()' /></td>");
			}
			
		}
		//添加角色信息
		$roleName.after(roleNameArr.join(""));
		//添加演员信息
		$actorName.after(actorNameArr.join(""));
		//化妆地信息
		$makeupAddress.after(makeupAddressArr.join(""));
		//化妆时间
		$makeupTime.after(makeupTimeArr.join(""));
		//出发时间
		$giveMakeupTime.after(giveMakeupTimeArr.join(""));
	}
	
	//根据剧组类型显示不同的集场号
	var $scrianceViewNo = $("#scrianceViewNo");
	if (crewType == 0 || crewType == 3) { //电影剧本
		$scrianceViewNo.append(" <span class='title-font five-viewNo-span'>场次</span>");
	}else {
		$scrianceViewNo.append(" <span class='title-font five-viewNo-span'>集-场</span>");
	}
	
/******************************拼接table中的场景列表信息********************************/
	//拍摄地点信息
	var $fiveTr = $("#fiveTr");
	var dataTrArr = [];
	var locationGroupList = dataMap['locationGroupList'];
	if (locationGroupList != null && locationGroupList.length>0) {
		for (var i = 0; i < locationGroupList.length; i++) {
			var locationViewMap = locationGroupList[i];
			
			//拼接第六行数据
			var location = locationViewMap['location'];
			var longitude = locationViewMap['longitude'];//经度
			var latitude = locationViewMap['latitude'];//纬度
			if(longitude == null || longitude == '' || longitude == undefined){
				longitude = "";
			}
			if(latitude == null || latitude == '' || latitude == undefined){
				latitude = "";
			}
			if (location == null || location == '' || location == undefined) {
				dataTrArr.push(" <tr><td colspan=4 class='border-center-td grey-background'><span class='transfer-info-span' id='" + locationViewMap['locationId'] + "'></span></td>");
			}else {
				if(longitude == "" && latitude == ""){
					dataTrArr.push(" <tr><td colspan=4 class='border-center-td grey-background'><span class='transfer-info-span' id='" + locationViewMap['locationId'] + "'>" + location + "</span><input class='shoot-location-button' log='"+ longitude +"' lat='"+ latitude +"' type='button' onclick='showMapWindow(this)'></td>");
				}else{
					dataTrArr.push(" <tr><td colspan=4 class='border-center-td grey-background'><span class='transfer-info-span' id='" + locationViewMap['locationId'] + "'>" + location + "</span><input class='shoot-location-button already-set-location' log='"+ longitude +"' lat='"+ latitude +"' type='button' onclick='showMapWindow(this)'></td>");
				}
				
			}
			dataTrArr.push(" <td colspan='" + (mainRoleList.length+7) + "' class='border-center-td'>");
			dataTrArr.push(" <input type='hidden' class='locationViewIds' value='" + locationViewMap['locationViewIds'] + "'/>");
			dataTrArr.push(" <input type='hidden' class='locationInfo' value='" + locationViewMap['locationId'] + "'/>");
			dataTrArr.push(" <span class='title-font transfer-info-span'>提示：</span>");
			
			//转场提示信息
			var convertAddressInfo = locationViewMap['convertAddressInfo'];
			if (convertAddressInfo == null || convertAddressInfo == ''|| convertAddressInfo == undefined) {
				dataTrArr.push(" <input class='transfer-point-input' name='convertRemark' type='text' value='' onkeyup='changeStaus()'><input type='text' style='display: none;' />");
			}else {
				dataTrArr.push(" <input class='transfer-point-input' name='convertRemark' type='text' value='" + convertAddressInfo + "' onkeyup='changeStaus()'></td></tr>");
			}
			
			//第七行场景列表数据
			var viewList = locationViewMap['viewList'];
			if (viewList != null && viewList.length>0) {
				for (var a = 0; a < viewList.length; a++) {
					var viewMap = viewList[a];
					//特别(备戏/特殊道具/特殊提醒)
					dataTrArr.push(" <tr class='seven-tr'><td class='border-center-td grey-background' style='width: 52px; min-width:52px;'>");
					var specialPropsList = viewMap['specialPropsList'];
					var specialRemind = viewMap['specialRemind'];
					var prepareStatus = viewMap['prepareStatus'];
					var prepareStatusStr = '';
					if (prepareStatus == 1) {
						prepareStatusStr = "备";
					}
					if (specialPropsList == null || specialPropsList == '') {
						if (specialRemind == null || specialRemind == '') {
							if (prepareStatusStr == '') {
								dataTrArr.push(" <span class='view-content-font'></span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>"+ prepareStatusStr + "</span>");
							}
						}else {
							if (prepareStatusStr == '') {
								dataTrArr.push(" <span class='view-content-font'>" + specialRemind + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + prepareStatusStr + "|" + specialRemind + "</span>");
							}
						}
					}else {
						if (specialRemind == null || specialRemind == '') {
							if (prepareStatusStr == '') {
								dataTrArr.push(" <span class='view-content-font'>" + specialPropsList + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + prepareStatusStr + "|" + specialPropsList + "</span>");
							}
						}else {
							if (prepareStatusStr == '') {
								dataTrArr.push(" <span class='view-content-font'>" + specialPropsList + "|" + specialRemind + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + prepareStatusStr + "|" + specialPropsList + "|" + specialRemind + "</span>");
							}
						}
					}
					
					dataTrArr.push(" </td>");
					
					//集场号
					dataTrArr.push(" <td class='border-center-td grey-background' style='width: 52px; min-width:52px;'>");
					if (crewType == 0 || crewType == 3) { //电影
						dataTrArr.push(" <span class='view-content-font'>" + viewMap['viewNo'] + "</span>");
					}else { //电视剧
						dataTrArr.push(" <span class='view-content-font'>" + viewMap['seriesNo'] + "-" + viewMap['viewNo'] + "</span>");
					}
					
					//气氛
					dataTrArr.push(" </td><td class='border-center-td grey-background' style='width: 52px; min-width:52px;'>");
					var atmosphere = viewMap['atmosphere'];
					var site = viewMap['site'];
					if (atmosphere == null || atmosphere == '') {
						if (site == null || site == '') {
							dataTrArr.push(" <span class='view-content-font'></span>");
						}else {
							dataTrArr.push(" <span class='view-content-font'>" + site + "</span>");
						}
					}else {
						if (site == null || site == '') {
							dataTrArr.push(" <span class='view-content-font'>" + atmosphere + "</span>");
						}else {
							dataTrArr.push(" <span class='view-content-font'>" + atmosphere + "/" + site + "</span>");
						}
					}
					
					dataTrArr.push(" </td><td class='border-center-td grey-background' style='width: 52px; min-width:52px;'>");
					//页数
					var pageCount = viewMap['pageCount'];
					if (pageCount == null || pageCount == '') {
						dataTrArr.push(" <span class='view-content-font'></span>");
					}else {
						dataTrArr.push(" <span class='view-content-font'>" + viewMap['pageCount'] + "</span>");
					}
					
					//场景
					dataTrArr.push(" </td><td class='view-list-td grey-background'>");
					var majorView = viewMap['majorView'];
					var minorView = viewMap['minorView'];
					var thirdLevelView = viewMap['thirdLevelView'];
					if (majorView == null || majorView == '') {
						if (minorView == null || minorView == '') {
							if (thirdLevelView == null || thirdLevelView == '') {
								dataTrArr.push(" <span class='view-content-font'></span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + thirdLevelView + "</span>");
							}
						}else {
							if (thirdLevelView == null || thirdLevelView == '') {
								dataTrArr.push(" <span class='view-content-font'>" + minorView + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + minorView + " | " +thirdLevelView + "</span>");
							}
						}
					}else {
						if (minorView == null || minorView == '') {
							if (thirdLevelView == null || thirdLevelView == '') {
								dataTrArr.push(" <span class='view-content-font'>" + majorView + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + majorView + " | " +thirdLevelView + "</span>");
							}
						}else {
							if (thirdLevelView == null || thirdLevelView == '') {
								dataTrArr.push(" <span class='view-content-font'>" + majorView + " | " +minorView + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + majorView + " | " +minorView + " | " + thirdLevelView + "</span>");
							}
						}
					}
					
					//内容提要
					dataTrArr.push(" </td><td class='view-list-content-td grey-background'>");
					var mainContent = viewMap['mainContent'];
					if (mainContent == null || mainContent == '') {
						dataTrArr.push(" <span class='view-content-font'></span>");
					}else {
						dataTrArr.push(" <span class='view-content-font'>" + mainContent + "</span>");
					}
					//出发时间
					dataTrArr.push(" </td><td class='blank-time-td grey-background'></td>");
					//演员别名
					var roleList = viewMap['roleList'];
					for (var b = 0; b < mainRoleList.length; b++) {
						dataTrArr.push(" <td class='blank-time-td grey-background'>");
						var role = roleList[b];
						if (role != null && role.shortName != null && role.shortName != '') {
							dataTrArr.push(" <span class='view-content-font margin-content'>" + role.shortName + "</span>");
						}else {
							dataTrArr.push(" <span class='view-content-font margin-content'></span>");
						}
						dataTrArr.push("</td>");
					}
					
					//特约演员信息
					var guestRoleList = viewMap['guestRoleList'];
					dataTrArr.push(" <td class='spacial-role-td grey-background'>");
					if (guestRoleList == null || guestRoleList == '') {
						dataTrArr.push(" <span class='view-content-font'></span>");
					}else {
						dataTrArr.push(" <span class='view-content-font'>" + guestRoleList + "</span>");
					}
					
					//群众演员信息
					var massRoleList = viewMap['massRoleList'];
					dataTrArr.push(" </td><td class='mass-role-td grey-background'>");
					if (massRoleList ==null || massRoleList == '') {
						dataTrArr.push(" <span class='view-content-font'></span>");
					}else {
						dataTrArr.push(" <span class='view-content-font'>" + massRoleList + "</span>");
					}
					dataTrArr.push(" </td>");
					
					//服化道数据信息
					dataTrArr.push(" <td class='makeup-td grey-background'>");
					var clothesName = viewMap['clothesName'];
					var makeupName = viewMap['makeupName'];
					var propsList = viewMap['propsList'];
					if (clothesName ==null || clothesName == '') {
						if (makeupName == null || makeupName == '') {
							if (propsList == null || propsList == '') {
								dataTrArr.push(" <span class='view-content-font'></span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + propsList + "</span>");
							}
						}else {
							if (propsList == null || propsList =='') {
								dataTrArr.push(" <span class='view-content-font'>" + makeupName + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + makeupName + " | " + propsList + "</span>");
							}
						}
					}else {
						if (makeupName == null || makeupName == '') {
							if (propsList == null || propsList =='') {
								dataTrArr.push(" <span class='view-content-font'>" + clothesName + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + clothesName + "|" + propsList + "</span>");
							}
						}else {
							if (propsList == null || propsList =='') {
								dataTrArr.push(" <span class='view-content-font'>" + clothesName + "|" + makeupName + "</span>");
							}else {
								dataTrArr.push(" <span class='view-content-font'>" + clothesName + "|" + makeupName + " | " + propsList + "</span>");
							}
						}
					}
					dataTrArr.push(" </td>");
					
					//备注信息
					dataTrArr.push(" <td class='backup-content-td grey-background'>");
					var remark = viewMap['viewRemark'];
					if (remark == null || remark == '') {
						dataTrArr.push(" <span class='view-content-font'></span>");
					}else {
						dataTrArr.push(" <span class='view-content-font'>" + remark + "</span>");
					}
					dataTrArr.push(" </td></tr>");
				}
			}
		}
	}else {
		//重置添加场景按钮
		$("#addNewView").removeClass("add-view-div");
		$("#addNewView").addClass("add-new-view-div");
	}
	$fiveTr.after(dataTrArr.join(""));
/*****************************拼接table中的场景列表信息结束*****************************/
	
	//其他提示信息
	var $lastTr = $("#lastTr");
	var lastArr = [];
	if (mainRoleList != null && mainRoleList.length>0) {
		lastArr.push(" <td colspan='" + (mainRoleList.length+11) + "'>");
	}else {
		lastArr.push(" <td colspan='11'>");
	}
	lastArr.push(" <span class='title-font other-info-span'>其他提示<div class='other-info-div'></div></span>");
	var myNoticeTime = dataMap['myNoticeTime'];
	if (myNoticeTime ==null || myNoticeTime.roleConvertRemark == null || myNoticeTime.roleConvertRemark =='') {
		lastArr.push(" 	<textarea class='other-info-content' id='roleConvertRemark' onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>");
	}else {
		lastArr.push(" 	<textarea class='other-info-content' id='roleConvertRemark' onkeyup='changeStaus()' onpaste='changeStaus()'>" + myNoticeTime.roleConvertRemark + "</textarea>");
	}
	//修改制表时间
	if (myNoticeTime == null ) {
		date = new Date();
		lastArr.push(" <div class='make-notice-time grey-background'><span class='make-time-span'>制表</span><div class='make-time-div'><p><span>" + getCurrDateFormat(date) + "</span></p><p><span>" + getCurrTime(date) + "</span></p></div></div>");
	}else {
		var makeTableTime = myNoticeTime.updateTime;
		saveDate = makeTableTime;
		var makeNoticeDate = new Date(makeTableTime);
		lastArr.push(" <div class='make-notice-time grey-background'><span class='make-time-span'>制表</span><div class='make-time-div'><p><span>" + getCurrDateFormat(makeNoticeDate) + "</span></p><p><span>" + getCurrTime(makeNoticeDate) + "</span></p></div></div>");
	}
	lastArr.push(" </td>");
	$lastTr.append(lastArr.join(""));
	
/*****************************拼接表格数据结束***************************************/
	
	//判断是否有自动填充的数据，如果有则自动保存
	if (isAutoSave) {
		autoSaveNotice();
	}
	
	//只读权限，页面不可编辑，点击各种按钮没反应
	if(isNoticeReadonly) {		
		$("input[type='text']").attr('disabled',true);
		$("#weatherInfo").attr('readonly',true);
		$("#moveStartTime").attr('readonly',true);
		$("textarea").attr('readonly',true);
		$("input[type='button']").attr('disabled',true);
	}
}

//自动保存方法
function autoSaveNotice(){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var noticeId = $("#generateNoticeId").val();    //通告单ID
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式(关联关系)
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
            /*groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
            /*makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            chengePublished:'cancleChanged'
        },
        dataType:"json",
        success:function(data){
            if (data.success) {
            	//showSuccessMessage("保存成功");
            	/* setTimeout(function (){
            		 window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=generateNotice";
                 }, 1000);*/
            	isSaved = false;
            	isAutoSave = false;
            } else {
            	//showErrorMessage(data.message);
            	 setTimeout(function (){
            		 //window.location.href = "/notice/toGenerateNotice?noticeId=" + noticeId;
                 }, 1000);
            }
        }
    });
}
//点击化妆时间时教妆时间自动加半个小时
function changeRoleArriveTime(roleId){
	isSaved = true;
	//取出化妆选择的时间
	var roleMakeUpTime = $("#arrive_" + roleId).val();
	$("#givemakeup_"+ roleId).val(roleMakeUpTime+30);
}

//获取年月日（格式：yyyy.MM.dd）
function getCurrDateFormat(date){
	var curronDate = date.getFullYear()+"."+(date.getMonth()+1)+"."+date.getDate();
	return curronDate;
}

//获取小时分钟秒
function getCurrTime(date){
	var curronTime = date.getHours()+"："+date.getMinutes()+"："+date.getSeconds();
	return curronTime;
}

//获取跳转到通告单详情页面之后需要加载的分组列表数据
function loadGroupListDta(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var groupList = response.groupList;
				//初始化添加到通告单窗口
				//initAddViewToNoticeWin(groupList);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//初始化添加到通告单窗口
function initAddViewToNoticeWin(groupList){
	var $noticeGroup = $("#noticeGroupSelect");
	var groupSource = [];
	var i = 1;
    for(var key in groupList){
    	if( i== 1){
    		groupSource.push("<option value='" + key + "' selected='selected'>" + groupList[key] + "</option>");
    		i++;
    		continue;
    	}
    	groupSource.push("<option value='" + key + "'>" + groupList[key] + "</option>");
    }
    groupSource.push("<option value='99'>" + "新增分组" + "</option>");
    
    $noticeGroup.append(groupSource.join(""));
}

//新建分组
function createNewGroup(){
	$("#noticeGroupSelect").on('change', function(){
		if ($("select option:selected").val() == '99') {
			//取出当前下拉框的长度
			var selectLength = document.getElementById("noticeGroupSelect").length-2;

            if(selectLength > 25){
            	parent.showErrorMessage("目前最多选择到Z组");
                return;
            }
            
            $.ajax({
                url:"/shootGroupManager/saveGroup",
                type:"post",
                dataType:"json",
                data:{groupName:groupArray[selectLength].text},
                success:function(data){
                    if(!data.success){
                    	parent.showErrorMessage(data.message);
                    }else {
                    	var $noticeGroup = $("#noticeGroupSelect");
                    	var group = data.group;
                    	$noticeGroup.append(" <option value='" + group.groupId + "' selected='selected'>" + group.groupName + "</option>");
					}
                }
            });
		}
	});
}

//获取天气信息
function getWeather(){
	$.ajax({
        url: "/notice/obtainCityInfoByIp",
        type: "post",
        dataType: "json",
        async: true,
        success: function(response) {
            if (response.success) {
                $("#cityInp").val(response.city);
            } else {
                parent.showErrorMessage(response.message);
            }
        }
    });
    if (weatherInfoWindow != undefined) {
        weatherInfoWindow.jqxWindow("open");
        return false;
    }

    weatherInfoWindow = $("#weatherInfoWindow").jqxWindow({
        width: 300,
        height: 150,
        autoOpen: false,
        resizable: false,
        isModal: true,
        initContent: function() {
            $("#obtainWeather").on("click", function() {
                var noticeId = $("#generateNoticeId").val();    //通告单ID
                var cityName = $("#cityInp").val();
               /* if (noticeId == null || noticeId == '' || noticeId == 'undefined') {
                	showErrorMessage(" 请先保存当前通告单，在获取天气！");
					weatherInfoWindow.jqxWindow("close");
					return;
				}*/
			    $.ajax({
			        url:"/notice/obtainWeatherInfo",
			        type:"post",
			        data:{cityName: cityName, noticeId: noticeId},
			        dataType:"json",
			        success:function(response){
			            if (response.success) {
			            	var weatherInfo = $("#weatherInfo");
			            	weatherInfo.val(response.weatherInfo);
			                weatherInfoWindow.jqxWindow("close");
			                isSaved = true;
			            } else {
			            	parent.showErrorMessage(response.message);
			            }
			        }
			    });
            });   
        }
    });
    weatherInfoWindow.jqxWindow("open");
}

//剧组联系表
function UserListGrid(tableId) {
	this.tableId = tableId;
	this.selectedIds = "";
	this.records = null;
	//获取记录数据
	this.getRecords=function(){
		var records = null;
		$.ajax({
			url: "/contact/queryCrewContactList",
			type: "post",
			dataType: "json",
			data:{sourceFrom: 'notice'},
			async: false,
			success:function(data) {
				records=data;
			}
		});
		
		this.records = records;
		return this.records;
	};
	
	//加载表格
	this.loadTable=function(){
		this.getRecords();
		this.createTable();
		this.selectedIds = "";
	};
	
	//创建表格html
	this.createTable = function (){
		var _this=this;
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tableObj.append("<div id='toolbar'></div>");
		this.createToolBar(_tableObj, _tableObj.find("#toolbar"));
		
		_tableObj.append('<div class="theadDiv" id="theadDiv"><table cellpadding="0" cellspacing="0" border="0">'+
				'<thead><tr id="tableHead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#tableHead");
		
		_head.append('<td width="15px" class="bold"><input type="checkbox" id="checkedAll" class="line-height"/></td>');
		_head.append('<td width="15px" class="bold"><p style="width:150px;">名称</p></td>');
		_head.append('<td width="15px" class="bold"><p style="width:150px;">职务</p></td>');
		_head.append('<td width="15px" class="bold"><p style="width:150px;">联系电话</p></td>');
		
		_tableObj.append('<div class="tbodyDiv" id="tbodyDiv" onscroll="tableScroll()" style="height:300px; width: 578px; overflow: auto;"><table cellpadding="0" cellspacing="0" border="0"><tbody id="tableBody"></tbody></table></div>');
		//表格主体
		var _tBody = _tableObj.find("#tableBody");
		
		//所有数据
		var tableData = this.records;
		for(var i=0; i<tableData.length; i++){
			var rowData = tableData[i];
			var _row = this.createRow(_tBody, rowData, i);
			_tBody.append(_row);
		}
		
		//checkbox全选
		$("#checkedAll").on("click",function(){
			if($("#checkedAll").prop("checked")){
				$(this).trigger("checked");
			}else{
				$(this).trigger("unChecked");
			}
		});
		
		//checkbox指定全部不选
		$("#checkedAll").bind("unChecked",function(){
			$("#checkedAll").prop("checked",false);
			_tBody.find(":checkbox").trigger("unChecked");
		});
		
		//checkbox指定全选
		$("#checkedAll").bind("checked",function(){
			$("#checkedAll").prop("checked",true);
			_tBody.find(":checkbox").trigger("checked");
		});
		

		_tableObj.append("<div id='operateBtn'></div>");
		this.createOpeBtn(_tableObj, _tableObj.find("#operateBtn"));
	};

	this.createOpeBtn = function(_tableObj, _tbtn) {
		_tbtn.append("<div style='width:100%;height:100%;'><input id='add' type='button' value='确定' /></div>");
		
		_tbtn.find("#add").on("click", function() {
			var concatInfoList = $("#concatInfoList").val();
			var checkedItem = $("#userList").find("#tableBody :checked");
			$("#contact table").children().remove();
			$("#contactInput").val("");
			var textInfo = '';
			$.each(checkedItem, function() {
				var userItem = $(this).parent().siblings();
				
				var userName = userItem.eq(0).find("p").text();
				var roleName = userItem.eq(1).find("p").text();
				var phone = userItem.eq(2).find("p").text();
				
				textInfo += roleName + userName + "：" + phone + "\r\n" ;
				
				var contactInputVal = $("#contactInput").val();
				$("#contactInput").val(contactInputVal + $(this).attr("id") + ",");
			});
			$("#concatInfoList").val("");
			$("#concatInfoList").val(concatInfoList  + textInfo);
			isSaved = true;
			
			$("#userList").animate({top:0},"normal");
			$("#userList").hide();
		});
	};
	
	this.createToolBar = function(_tableObj, _tbar) {
		_tbar.append("<div style='width:100%;height:27px;padding:3px;'><span id='title'>联系表</span><span id='close'></span></div>");
		
		_tbar.find("#close").on("click", function() {
			$("#userList").animate({top:0},"normal");
			$("#userList").hide();
		});
	};
	
	//选中一行数据
	this.checkedItem = function(userId) {
		var tableId = this.tableId;
		$("#" + tableId).find(":checkbox[id="+ userId +"]").trigger("checked");
	};
	
	this.unCheckAll = function() {
		$("#" + tableId).find(":checkbox").trigger("unChecked");
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		var _this = this;
		var _row = $("<tr rowId='"+rowid+"'></tr>");
		//行点击事件
		_row.click(function(){
			if($(this).attr("class")&&$(this).attr("class").indexOf("mouse_click")>-1){
				$(this).find(":checkbox").trigger("unChecked");
			}else{
				$(this).find(":checkbox").trigger("checked");
			}
		});
		var roleName= rowData['duty'] == null ? "" : rowData['duty'];
		var phone = rowData['phone'] == null ? "" : rowData['phone'];
		var contactName = rowData['contactName'];
		
		_row.append('<td width="15px" class="bold"><input type="checkbox" id="'+rowData['contactId']+'" index="'+rowid+'" rowData = "'+ rowData +'" class="line-height"/></td>');
		_row.append('<td width="15px" class="bold"><p style="width:150px;color:#e75903;" class="userName">'+ contactName +'</p></td>');
		_row.append('<td width="15px" class="bold"><p style="width:150px;" class="roleName">'+ roleName +'</p></td>');
		_row.append('<td width="15px" class="bold"><p style="width:150px;" class="phone">'+ phone +'</p></td>');
		
		//为表格行上的复选框绑定选中和取消选中事件
		//加入选中Id
		_row.find(":checkbox").bind("checked",function(){
			$(this).prop("checked",true);
			$(this).parents("tr").addClass("mouse_click");
			if(_this.selectedIds.indexOf($(this).attr("id"))==-1){
				_this.selectedIds+=$(this).attr("id")+",";
			}

			isCheckAll();
		});
		//删除选中Id
		_row.find(":checkbox").bind("unChecked",function(){
			$(this).prop("checked",false);
			$(this).parents("tr").removeClass("mouse_click");
			_this.selectedIds=_this.selectedIds.replace($(this).attr("id")+",", "");

			isCheckAll();
		});
		
		//行checkbox事件只判断是否全选
		function isCheckAll(){
			var UnChecked = _tBody.find(":checkbox").not(function(index){
				if($(this).prop("checked")){
					return this;
				}
			}).length;
			if(UnChecked==0){
				$("#checkedAll").prop("checked",true);
			}else{
				$("#checkedAll").prop("checked",false);
			}
		}
		return _row;
	};
}

//表格滚动
function tableScroll() {
	var b = document.getElementById("tbodyDiv").scrollLeft;
	document.getElementById("theadDiv").scrollLeft = b;
}

//添加剧组联系表
function addCrewContact(){
	//只读权限，点击无反应
	if(isNoticeReadonly) {
		return false;
	}
	
	 var contactOffset = $("#concatInfoList").offset();
     var x = contactOffset.left;
	    var y = contactOffset.top;
     
     //取消选中所有人
     userGrid.unCheckAll();
     //选中已有的联系人
     //var contactUserIds = $("#contactInput").val();
    // var contactUserArray = contactUserIds.split(",");
     /*$.each(contactUserArray, function() {
         if (this == null || this == "") {
             return;
         }
         userGrid.checkedItem(this);
     });*/
     
     $("#userList").show();
     
     $("#userList").css("left", x);
     $("#userList").css("top", y);
}



//保存通告单时抽取公共方法
function publicSaveNotice(chengePublished){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var noticeId = $("#generateNoticeId").val();    //通告单ID
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式(关联关系)
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
           /* groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
            /*makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            chengePublished: chengePublished
        },
        dataType:"json",
        success:function(data){
            if (data.success) {
            	parent.showSuccessMessage("保存成功");
            	isSaved = false;
            	setTimeout(function (){
            		/*window.location.href = "/notice/toNoticeList?source=createNoticePage&stepPage=3&noticeId=" + noticeId;*/
            		//重新加载
            		parent.reLocation(noticeId);
                 }, 1000);
            } else {
            	parent.showErrorMessage(data.message);
            }
        }
    });
}

//保存通告单信息
function saveGenerateNotice() {
	if (isSaved) {
		//判断当前通告单是否已经发布
		if (noticePublished || isHaveFacdbcak) {
			//有修改，跳出确认框，提示是否修改
			swal({
				title: "是否撤销通告单",
				text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
				type: "warning",
				showCancelButton: true,  
				confirmButtonColor: "rgba(255,103,2,1)",
				confirmButtonText: "是",   
				cancelButtonText: "否",   
				closeOnConfirm: true,   
				closeOnCancel: true
			}, function (isConfirm) {
				if (isConfirm) {
					publicSaveNotice('');
					noticePublished = false;
					isHaveFacdbcak = false;
				}else {
					publicSaveNotice('cancleChanged');
				}
			});
			
		}else {
			publicSaveNotice('');
		}
	}else {
		publicSaveNotice('cancleChanged');
	}

}

//保存通告单时抽取公共方法
function saveNoticeNoMessage($this,chengePublished){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var noticeId = $("#generateNoticeId").val();    //通告单ID
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式(关联关系)
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
           /* groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
            /*makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            chengePublished: chengePublished
        },
        dataType:"json",
        success:function(data){
            if (data.success) {
            	//showSuccessMessage("保存成功");
            	/* setTimeout(function (){
            		 window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=generateNotice";
                 }, 1000);*/
            	parent.$("#stepPage").val('1');
            	parent.$this.addClass("click");
            	parent.$(".win-btn-list").hide();
            	parent.$(".btn-list").show();
            	parent.$("#noticeContentIframe").hide();
            	parent.$("#loadNoticeContent").empty();
            	parent.$("#loadNoticeContent").show();
            	parent.$("#createNotice").show();
            	$this.addClass("click");
            	isSaved = false;
            } else {
            	parent.showErrorMessage(data.message);
            }
        }
    });
}

//保存通告单信息
function saveNoticeWithOutMessage($this) {
    //先保存通告单在跳转到发布页面
    //判断当前通告单是否有修改
    if (isSaved) { //有修改，跳出确认框，提示是否修改
    	 swal({
		        title: "是否保存通告单",
		        text: '通告单有改动',
		        type: "warning",
		        showCancelButton: true,  
		        confirmButtonColor: "rgba(255,103,2,1)",
		        confirmButtonText: "保存",   
		        cancelButtonText: "不保存",   
		        closeOnConfirm: false,   
		        closeOnCancel: true
		    }, function (isConfirm) {
		    	if (isConfirm) {
		    		//如果当前通告单已经发布，则给出提示信息
		    		if (noticePublished || isHaveFacdbcak) {
		    			 //有修改，跳出确认框，提示是否修改
		    	   	 swal({
		    			        title: "是否撤销通告单",
		    			        text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
		    			        type: "warning",
		    			        showCancelButton: true,  
		    			        confirmButtonColor: "rgba(255,103,2,1)",
		    			        confirmButtonText: "是",   
		    			        cancelButtonText: "否",   
		    			        closeOnConfirm: true,   
		    			        closeOnCancel: true
		    			    }, 
		    			    function (isConfirm) {
		    			    	if (isConfirm) {
		    			    		saveNoticeNoMessage($this, '');
		    			    		noticePublished = false;
		    			    		isHaveFacdbcak = false;
		    			    	}else {
									//保存修改但是不撤销通告单
		    			    		saveNoticeNoMessage($this,'cancleChanged');
								}
		    			    });
		    	   	 
		    		}else {
		    			saveNoticeNoMessage($this, '');
		    			swal.close();
		    		}
		    	}else {
		    		 parent.$("#stepPage").val('1');
		    		    parent.$("li.click").removeClass("click");
		    		    parent.$(".win-btn-list").hide();
		    		    parent.$(".btn-list").show();
		    		    parent.$("#noticeContentIframe").hide();
		    		    
		    		    parent.$("#loadNoticeContent").empty();
		    		    parent.$("#loadNoticeContent").show();
		    		    parent.$("#createNotice").show();
		    		    $this.addClass("click");
				}
		    });
    	 
		
	}else {
		 parent.$("#stepPage").val('1');
		 parent.$("li.click").removeClass("click");
		 parent.$(".win-btn-list").hide();
		 parent.$(".btn-list").show();
		 parent.$("#noticeContentIframe").hide();
		 parent.$("#loadNoticeContent").empty();
		 parent.$("#loadNoticeContent").show();
		 parent.$("#createNotice").show();
		 $this.addClass("click");
	}
}

//发布通告单时，保存通告单
function publishSaveNotice(chengePublished){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	 var noticeId = $("#generateNoticeId").val();    //通告单ID
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    //通告单名称
    var noticeName = "《" + crewName + "》 " + noticeDateStr + $("#noticeGroupSelect").val()+"通告";
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
           /* groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
           /* makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            noticeName: noticeName,
            chengePublished: chengePublished
        },
        dataType:"json",
        async: false,
        success:function(data){
            if (data.success) {
            	parent.showSuccessMessage(data.message);
            	//跳转到预览界面
            	setTimeout(function (){
            		window.open("/notice/printView?noticeId=" + noticeId);
                }, 1000);
            	isSaved = false;
            } else {
            	//swal("添加 ", "保存失败！", "error");
            	parent.showErrorMessage(data.message);
            	 
            }
        }
    });
}

//发布通告单
function publishNotice(){
    var noticeId = $("#generateNoticeId").val();
    
    //先保存通告单在跳转到发布页面
    //判断当前通告单是否有修改
    if (isSaved) { //有修改，跳出确认框，提示是否修改
    	 swal({
		        title: "是否保存通告单",
		        text: '通告单有改动',
		        type: "warning",
		        showCancelButton: true,  
		        confirmButtonColor: "rgba(255,103,2,1)",
		        confirmButtonText: "保存",   
		        cancelButtonText: "不保存",   
		        closeOnConfirm: false,   
		        closeOnCancel: true
		    }, function (isConfirm) {
		    	if (isConfirm) {
		    		//如果当前通告单已经发布，则给出提示信息
		    		if (noticePublished || isHaveFacdbcak) {
		    			 //有修改，跳出确认框，提示是否修改
		    	   	 swal({
		    			        title: "是否撤销通告单",
		    			        text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
		    			        type: "warning",
		    			        showCancelButton: true,  
		    			        confirmButtonColor: "rgba(255,103,2,1)",
		    			        confirmButtonText: "是",   
		    			        cancelButtonText: "否",   
		    			        closeOnConfirm: true,   
		    			        closeOnCancel: true
		    			    }, function (isConfirm) {
		    			    	if (isConfirm) {
		    			    		publishSaveNotice('');
		    			    		noticePublished = false;
		    			    		isHaveFacdbcak = false;
		    			    	}else {
									//保存通告单修改，但是不撤销
		    			    		publishSaveNotice('cancleChanged');
								}
		    			    });
		    	   	 
		    		}else {
		    			publishSaveNotice('');
		    			swal.close();
		    		}
		    	}else {
		    			//跳转到预览界面
		    			window.open("/notice/printView?noticeId=" + noticeId);
					}
		    });
    	 
		
	}else {
		window.open("/notice/printView?noticeId=" + noticeId);
	}
}

//想通告单中添加场景是保存通告单
function addViewSaveNotice(chengePublished){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	var noticeId = $("#generateNoticeId").val();    //通告单ID
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    //通告单名称
    var noticeName = "《" + crewName + "》 " + noticeDateStr + $("#noticeGroupSelect").val()+"通告";
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
            /*groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
           /* makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            noticeName: noticeName,
            chengePublished: chengePublished
        },
        dataType:"json",
        async: false,
        success:function(data){
            if (data.success) {
            	parent.showSuccessMessage(data.message);
            	//跳转到预览界面
    			window.location.href="/notice/toNoticeList?noticeId="+noticeId+"&source=1"; //表示是通告单编辑页面跳转过来的
            	isSaved = false;
            } else {
            	//swal("添加 ", "保存失败！", "error");
            	parent.showErrorMessage(data.message);
            	 setTimeout(function (){
                 }, 1000);
            }
        }
    });
}

//向通告单添加场景
function addViewToNotice(){
	var noticeId = $("#generateNoticeId").val();
	
    //先保存通告单在跳转到发布页面
    //判断当前通告单是否有修改
    if (isSaved) { //有修改，跳出确认框，提示是否修改
    	 swal({
		        title: "是否保存通告单",
		        text: '通告单有改动',
		        type: "warning",
		        showCancelButton: true,   
		        confirmButtonColor: "rgba(255,103,2,1)",   
		        confirmButtonText: "保存",   
		        cancelButtonText: "不保存",   
		        closeOnConfirm: false,   
		        closeOnCancel: true
		    }, function (isConfirm) {
		    	if (isConfirm) { 
		    		//如果当前通告单已经发布，则给出提示信息
		    		if (noticePublished || isHaveFacdbcak) {
		    			 //有修改，跳出确认框，提示是否修改
		    	   	 swal({
		    			        title: "是否保存通告单",
		    			        text: '当前通告单已经发布，若保存通告单则会将当前通告单变为未发布状态',
		    			        type: "warning",
		    			        showCancelButton: true,  
		    			        confirmButtonColor: "rgba(255,103,2,1)",
		    			        confirmButtonText: "保存",   
		    			        cancelButtonText: "不保存",   
		    			        closeOnConfirm: true,   
		    			        closeOnCancel: true
		    			    }, function (isConfirm) {
		    			    	if (isConfirm) {
		    			    		addViewSaveNotice();
		    			    		noticePublished = false;
		    			    		isHaveFacdbcak = false;
		    			    	}
		    			    });
		    	   	 
			    		}else {
			    			addViewSaveNotice();
			    			swal.close();
			    		}
		    	
		    		}else {
		    			//跳转到预览界面
		    			window.location.href="/notice/toNoticeList?noticeId="+noticeId+"&source=1"; //表示是通告单编辑页面跳转过来的
					}
		    });
    	 
		
	}else {
		window.location.href="/notice/toNoticeList?noticeId="+noticeId+"&source=1"; //表示是通告单编辑页面跳转过来的
	}
}

//重置添加场景按钮位置
function resizeAddNoticeView(){
	//取出浏览器宽度
	//var boswerWidth = document.body.clientWidth;
	//取出table的宽度
	var tableWidth = $(".notice-content-table").width();
	//取出table高度
	//var tableHeight = $(".notice-content-table").height();
	//计算div距离上边距的距离
	//var topDistance = (tableHeight-72-20-66);
	//计算出div的位置
	/*//var leftDistance = tableWidth/2+81;
	//var $addNewView = $("#addNewView");
	$addNewView.css('display', 'none');
	$addNewView.css("margin-left", -leftDistance);
	$addNewView.css("margin-top", -topDistance);*/
	
	//计算拍摄地点距离左边框的距离
	//var shootLeftDistance = (boswerWidth-tableWidth)/2+10;
	$(".notice-location-div").css("width", tableWidth);
	
	//计算场数页数显示的距离
	//var countSpan = $(".notice-count-span").width();
	//var pageDinstance = (tableWidth-650) - countSpan - 20;
    //$(".notice-count-span").css("width", tableWidth);
	
	//重置制表时间位置
	/*var makeNoticeTime = $(".make-notice-time");
	var timeDistance = 31 + (tableWidth*0.8)+30;
	makeNoticeTime.css("margin-left", timeDistance);*/
}

//获取当前时间
function getNowFormatDate() { 
	var day = new Date(); 
	var Year = 0; 
	var Month = 0; 
	var Day = 0; 
	var CurrentDate = ""; 
	//初始化时间 
	Year= day.getFullYear();//ie火狐下都可以 
	Month= day.getMonth()+1; 
	Day = day.getDate(); 
	CurrentDate += Year + "-"; 
	if (Month >= 10 ) 	{ 
	CurrentDate += Month + "-"; 
	} 
	else 	{ 
	CurrentDate += "0" + Month + "-"; 
	} 
	if (Day >= 10 )	{ 
	CurrentDate += Day ; 
	} 
	else 	{ 
	CurrentDate += "0" + Day ; 
	} 
	return CurrentDate; 
} 

//提示未保存信息
function changeStaus(){
	isSaved = true;
}

//返回列表界面公共方法
function closeSaveNotice(chengePublished){
	//通告日期
	var noticeDateStr = $("#noticeTime").val();
	var noticeId = $("#generateNoticeId").val();    //通告单ID
	/*//分组id
	var groupId = $("#noticeGroupSelect option:selected").val();*/
    var breakfastTime = $("#breakfastTime").val();  //早餐时间
    var departureTime = $("#moveStartTime").val();  //出发时间
    var note = $("#pointInfo").val();    //提示
    var contact = $("#contactInput").val();  //联系方式
    var noticeContact = $("#concatInfoList").val(); //通告单中的联系人
    var remark = $("#remarkInfo").val();    //备注
    var roleInfo = $("#roleInfo").val();    //演员信息
    var shootLocationInfos = $("#shootLocationInput").val(); //拍摄地点
    var weatherInfo = $("#weatherInfo").val(); //天气
    
    var imgStorePath = $("#imgStorePath").val();
    var smallImgStorePath = $("#smallImgStorePath").val();
    
    //版本信息
    var version = $("#versionInfo").val();
    //组导演
    var groupDirector = $("#directorNameInput").val();
    //摄影
    var shootGuide = $("#shootGuide").val();
    //商务植入
    var insideAdvert = $("#advertInfo").val();
    
    //化妆地
    var role_makeup = "";
    $("input[name='role_makeup']").each(function(){
        role_makeup+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    //化妆时间
    var role_arriveTime ="";
    $("input[name='role_arriveTime']").each(function(){
        role_arriveTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    //交妆
    var role_giveMakeupTime ="";
    $("input[name='role_giveMakeupTime']").each(function(){
        role_giveMakeupTime+=$(this).attr("id")+"|"+$(this).val()+",";
    });
    
    
    //转场信息
    var convertRemark = "";
    $("input[name='convertRemark']").each(function() {
        var locationId = $(this).parent().parent().find(".locationInfo").val();
        var locationViewIds = $(this).parent().parent().find(".locationViewIds").val();
    
        convertRemark+=locationId + "_" + locationViewIds + "_" + $(this).val() + "|";
    });
    //演员转场提示
    var roleConvertRemark = $("#roleConvertRemark").val();
    
    $.ajax({
        url:"/notice/saveGeneratedNotice",
        type:"post",
        data:{
            noticeId: noticeId, 
            breakfastTime: breakfastTime,
            departureTime: departureTime,
            note: note,
            contact: contact, 
            remark: remark, 
            roleInfo: roleInfo,
            version: version,
            groupDirector: groupDirector,
            shootGuide: shootGuide,
            insideAdvert: insideAdvert,
            roleMakeup: role_makeup,
            roleArriveTime: role_arriveTime,
            roleGiveMakeupTime: role_giveMakeupTime,
            convertRemark: convertRemark,
            roleConvertRemark: roleConvertRemark,
            shootLocationInfos: shootLocationInfos,
            weatherInfo: weatherInfo,
            /*groupId: groupId,*/
            noticeDateStr: noticeDateStr,
            imgStorePath: imgStorePath,
            smallImgStorePath: smallImgStorePath,
           /* makeTableTimeStr: makeTableTime,*/
            concatInfoList: noticeContact,
            chengePublished: chengePublished
        },
        dataType:"json",
        async: false,
        success:function(data){
            if (data.success) {
            	//跳转到预览界面
            	//window.location.href="/notice/toNoticeList";
            	$("#createNotice").hide();
            	$(".btn-list").hide();
            	//要load的页面
            	parent.$("#loadNoticeContent").hide();
            	parent.$("#noticeContentIframe").show();
            	/*parent.$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);*/
    			parent.$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+noticeId);
            	
    			parent.$("li.click").removeClass("click").prev("li").addClass("click");
        		
            	isSaved = false;
            } else {
            	swal("保存 ", "保存失败！", "error");
            	//showErrorMessage(data.message);
            }
        }
    });
    parent.$("#stepPage").val('2');
}

//关闭通告单预览页面
function closeGenNotice(){
	
	 if (isSaved) { //有修改，跳出确认框，提示是否修改
    	 swal({
		        title: "是否保存通告单",
		        text: '通告单有改动',
		        type: "warning",
		        showCancelButton: true,   
		        confirmButtonColor: "rgba(255,103,2,1)",   
		        confirmButtonText: "保存",   
		        cancelButtonText: "不保存",   
		        closeOnConfirm: false,   
		        closeOnCancel: true
		    }, function (isConfirm) {
		    	if (isConfirm) { 
		    		//如果当前通告单已经发布，则给出提示信息
		    		if (noticePublished || isHaveFacdbcak) {
		    			 //有修改，跳出确认框，提示是否修改
		    	   	 swal({
		    			        title: "是否撤销通告单",
		    			        text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
		    			        type: "warning",
		    			        showCancelButton: true,  
		    			        confirmButtonColor: "rgba(255,103,2,1)",
		    			        confirmButtonText: "是",   
		    			        cancelButtonText: "否",   
		    			        closeOnConfirm: true,   
		    			        closeOnCancel: true
		    			    }, function (isConfirm) {
		    			    	if (isConfirm) {
		    			    		closeSaveNotice('');
		    			    		noticePublished = false;
		    			    		isHaveFacdbcak = false;
		    			    	}else {
		    			    		closeSaveNotice('cancleChanged');
								}
		    			    });
		    	   	 
			    		}else {
			    			closeSaveNotice('');
			    			//关闭提示框
			    			swal.close();
			    		}
		    	
		    		}else {
		    			var noticeId = $("#generateNoticeId").val();
		    			$("#createNotice").hide();
		    			$(".btn-list").hide();
	            		//要load的页面
//		    			$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);
//		    			parent.$("#noticeContentIframe").hide();
//		            	parent.$("#loadNoticeContent").show();
//		            	parent.$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);
		    			parent.$("#loadNoticeContent").hide();
		    			parent.$("#noticeContentIframe").show();
		    			parent.$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+noticeId);
		    			parent.$("li.click").removeClass("click").prev("li").addClass("click");
					}
		    });
    	 
		
	}else {
		var noticeId = $("#generateNoticeId").val();
		$("#createNotice").hide();
		$(".btn-list").hide();
		//要load的页面
//		$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);
		parent.$("#loadNoticeContent").hide();
    	parent.$("#noticeContentIframe").show();
		parent.$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+noticeId);
		parent.$("li.click").removeClass("click").prev("li").addClass("click");
	}
	 
	 parent.$("#stepPage").val('2');
}





//初始化百度地图弹窗
function initBaiduMapWin(){
	$("#baiduMapWindow").jqxWindow({
		width: 1000,
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
	var lng = $this.attr("log");//经度
	var lat = $this.attr("lat");//纬度
	if(lng != "" && lat != ""){
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
//关闭百度地图弹窗
//function closeMapWin(){
//	$("#baiduMapWindow").hide();
//	$("#winModalZIndex").hide();
//}



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

//获取所有地域信息
function queryAllRegionInfo(){
	$.ajax({
		url: '/sceneViewInfoController/queryProCityList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response){
				shootRegionList = response.shootRegionList;
			}
		}
	});
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
    		if(province=="宁夏回族自治区"){
    			province ="宁夏";
	    	}else if(province == "新疆维吾尔自治区"){
	    		province = "新疆";
	    	}else if(province == "内蒙古自治区"){
	    		province = "内蒙古";
	    	}else if(province == "广西壮族自治区"){
	    		province = "广西";
	    	}else if(province == "西藏自治区"){
	    		province = "西藏";
	    	}else  {//去掉"市"
	    		if(province.indexOf("市") != -1){
	    			province = province.replace(/市/g, "");
	    		}
	    	}
    		var nowAddress = province+district;
    		var newAddress;
    		for(var i=0; i< shootRegionList.length; i++){
    			if(nowAddress.indexOf(shootRegionList[i]) != -1){
    				newAddress = shootRegionList[i];
    				break;
    			}
    		}
	    	cityName = newAddress;
	    	detailAddress = addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}else{
			if(province=="宁夏回族自治区"){
	    		province ="宁夏";
	    	}else if(province == "新疆维吾尔自治区"){
	    		province = "新疆";
	    	}else if(province == "内蒙古自治区"){
	    		province = "内蒙古";
	    	}else if(province == "广西壮族自治区"){
	    		province = "广西";
	    	}else if(province == "西藏自治区"){
	    		province = "西藏";
	    	}else  {//去掉"省"
	    		province = province.replace(/省/g, "");
	    	}
			var nowAddress = province+city;
    		var newAddress;
			for(var i=0; i< shootRegionList.length; i++){
    			if(nowAddress.indexOf(shootRegionList[i]) != -1){
    				newAddress = shootRegionList[i];
    				break;
    			}
    		}
			cityName = newAddress;
			detailAddress = addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}
	    saveDetailLocation(shootId, vName, cityName, detailAddress, p.lng, p.lat);
    });
	$("#baiduMapWindow").jqxWindow("close");
}

//保存经纬度等信息
function saveDetailLocation(shootId, vName, cityName, detailAddress, lng, lat){
	$.ajax({
		url: '/sceneViewInfoController/updateSceneViewInfo',
		type: 'post',
		data: {"id":shootId, "vName": vName, "vCity":cityName, "vAddress":detailAddress, "vLongitude": lng, "vLatitude":lat},
		datatype: 'json',
		success: function(response){
			if(response.success){
				inputButtonIndex.attr("log", lng);
				inputButtonIndex.attr("lat", lat);
				inputButtonIndex.addClass("already-set-location");
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}



//初始化化妆时间和出发时间弹窗
function initMakeupTimeWin(){
//	$("#uniformArriveTimeDiv").jqxWindow({
//		width: 100,
//        height: 41,
//        minHeight: 30,
//        autoOpen: false,
//        resizable: false,
//        isModal: true,
//        draggable: false,
//        isModal: false,
//        showCloseButton: false
//	});
//	$("#uniformMakeupTimeDiv").jqxWindow({
//		width: 150,
//        height: 50,
//        autoOpen: false,
//        resizable: false,
//        isModal: true,
//        draggable: false,
//        isModal: false,
//        showCloseButton: false
//	});
}

//统一设置化妆时间弹窗
function uniformArrivTime(){
	
	var top = $("#makeupTime").position().top;
	var left= $("#makeupTime").position().left;
	var width = $("#makeupTime").width();
	var num = divide(width, 3);
	var height = $("#makeupTime").height();
	/*$("#uniformMakeupTimeDiv").jqxWindow("close");
	$('#uniformArriveTimeDiv').jqxWindow({ position: { x: left + width, y: top + height }});
	$("#uniformArriveTimeDiv").jqxWindow("open");*/
	$("#uniformMakeupTimeDiv").hide();
	$("#arriveTimeInput").val("");
	$('#uniformArriveTimeDiv').css({"left":left+width-num, "top":top+height}).show();
	
}
//统一设置出发时间弹窗
function uniformMakeupValue(){
	var top = $("#giveMakeupTime").position().top;
	var left= $("#giveMakeupTime").position().left;
	var width = $("#giveMakeupTime").width();
	var num = divide(width, 3);
	console.log(num);
	var height = $("#giveMakeupTime").height();
	/*$("#uniformArriveTimeDiv").jqxWindow("close");
	$('#uniformMakeupTimeDiv').jqxWindow({ position: { x: left + width, y: top + height }});
	$("#uniformMakeupTimeDiv").jqxWindow("open");*/
	$("#uniformArriveTimeDiv").hide();
	$("#makeupTimeInput").val("");
	$("#uniformMakeupTimeDiv").css({"left":left+width-num, "top":top+height}).show();
}
function confirmArriveTime(){
	var time = $("#arriveTimeInput").val();
	if(time == ""){
		/*$("#uniformArriveTimeDiv").jqxWindow("close");*/
		$("#uniformArriveTimeDiv").hide();
		return;
	}
	$("input[name=role_arriveTime][type=text]").each(function(){
		$(this).val(time);
	});
	isSaved = true;
	/*$("#uniformArriveTimeDiv").jqxWindow("close");*/
	$("#uniformArriveTimeDiv").hide();
}
function confirmMakeupTime(){
	var time = $("#makeupTimeInput").val();
	if(time == ""){
		/*$("#uniformMakeupTimeDiv").jqxWindow("close");*/
		$("#uniformMakeupTimeDiv").hide();
		return;
	}
	$("input[name=role_giveMakeupTime][type=text]").each(function(){
		$(this).val(time);
	});
	isSaved = true;
	/*$("#uniformMakeupTimeDiv").jqxWindow("close");*/
	$("#uniformMakeupTimeDiv").hide();
}


function checkTagFen(own){
	$(own).val($(own).val().replace(/：/g, ":"));
	isSaved = true;
}