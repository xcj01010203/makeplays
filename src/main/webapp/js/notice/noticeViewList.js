var crewType = null;
var viewNoArr = [];
var filter ={};
var noticeDate = null;
var firstRightNoArr = [];
var isFirstLoad = true;
var divHeigth = null;
var viewNoflag = true;
var contantFlag = true;
var reamrkFlag =true;
var viewSelectCount = 0;
var pageSelectCount = 0;
var eventWindow = null;
var setStatusWindow = null;
var setAddressWindow;
var tempPage = 0.0;
$("document").ready(function(){
	
	//获取剧组类型
	getCrewType();
	
	initPopupPromptBox();
	
	//加载通告shijian
	loadNoticeDate();
	
	//加载通告单中的场景信息
	loadNoticeViewData();
	
	//选中状态
	defaultSelect();
	
	//隐藏下拉菜单
	$(document).click(function(){
		$("#collScenceDrop").slideUp("fast");
		$("#collContentDrop").slideUp("fast");
		$("#collRemarkDrop").slideUp("fast");
		
		//判断集场下拉框中是否有输入值
		if (crewType == 0 || crewType == 3) { //电影或网大
			var startViewNo = $("#startViewNo").val();
			var endViewNo = $("#endViewNo").val();
			var viewNos = $("#viewNos").val();
			if (startViewNo == '' || startViewNo == undefined) {
				if (endViewNo == '' || endViewNo == undefined) {
					if (viewNos == '' || viewNos == undefined) {
						if (!$("#isAll").prop("checked")) {
							$("div[class*='viewno-div']").css("background", "url('../../images/select-picture.png') no-repeat");
						}
					}
				}
			}
		}else {//电视剧或网剧
			var startSeriesNo = $("#startSeriesNo").val();
			var startViewNo = $("#startViewNo").val();
			var endSeriesNo = $("#endSeriesNo").val();
			var endViewNo = $("#endViewNo").val();
			var seriesViewNos = $("#seriesViewNos").val();
			if (startSeriesNo == '' || startSeriesNo == undefined) {
				if (startViewNo == '' || startViewNo == undefined) {
					if (endSeriesNo == '' || endSeriesNo == undefined) {
						if (endViewNo == '' || endViewNo == undefined) {
							if (seriesViewNos == '' || seriesViewNos == undefined) {
								if (!$("#isAll").prop("checked")) {
									$("div[class*='viewno-div']").css("background", "url('../../images/select-picture.png') no-repeat");
								}
							}
						}
					}
				}
			}
		}
		
		//判断内容输入框是否有值
		var $content = $("#mainContent").val();
		if ($content == '' || $content == null || $content == undefined) {
			$("div[class*='content-div']").css("background", "url('../../images/select-picture.png') no-repeat");
		}
		
		//判断内容输入框是否有值
		var $content = $("#ReamrkContent").val();
		if ($content == '' || $content == null || $content == undefined) {
			$("div[class*='reamrk-div']").css("background", "url('../../images/select-picture.png') no-repeat");
		}
		
		/*$("#atmosphereDrop").slideUp("fast");*/
		$("#scenceDrop").slideUp("fast");
		$("#mainActorDrop").slideUp("fast");
		$("#specialActorDrop").slideUp("fast");
		$("#publicActorDrop").slideUp("fast");
		$("#clothPropDrop").slideUp("fast");
	});

	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
	//下拉菜单的初始化
	initDropDownMenu();
	
	initCloseSelect();
	
	//初始化销场窗口
	loadCancleWindow();
	
	
	/*$("#smoothView").css("color","#FFFFFF");*/
});


//改变滚动条的距离
function changeScroll(){
	var scrollLeft = document.getElementById("tableMainBodyDiv").scrollLeft;
	/*document.getElementById("tableMainHeader").scrollLeft = scrollLeft;*/
	$("#tableMainHeader").css({"left": 0-scrollLeft});
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
	            
	           var $collFirstLi = $(".coll-first-li");
	           var liDataArr = [];
	            if (crewType == 0 || crewType == 3) { //电影
	            	liDataArr.push(" <label style='display: inline-block;margin-top: 10px;'>场次区间：</label>");
					liDataArr.push(" <input type='text' name='startSeriesNo' id='startSeriesNo' style='display:none;' value='1'/>");
					liDataArr.push(" <input type='text' name='startViewNo' id='startViewNo' style='width:70px;height:25px;' placeholder='场'/>&nbsp;到");
					liDataArr.push(" <input type='text' name='endSeriesNo' id='endSeriesNo' style='display:none;' value='1'/>");
					liDataArr.push(" <input type='text' name='endViewNo' id='endViewNo' style='width:70px;height:25px;' placeholder='场'/>");
					liDataArr.push(" &nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' id='isAll' />含已拍");
					liDataArr.push(" <label style='display:inline-block;margin-top:10px;'>场次编号：</label><input type='text' id='viewNos'style='width:170px;height:25px;' placeholder='输入范例：1,2,3'>");
	            }else { //电视剧
					liDataArr.push(" <label style='display: inline-block;margin-top: 10px;'>集场区间：</label>");
					liDataArr.push(" <input type='text' name='startSeriesNo' id='startSeriesNo' style='width:30px;height:25px;' placeholder='集' />&nbsp;-");
					liDataArr.push(" <input type='text' name='startViewNo' id='startViewNo' style='width:30px;height:25px;' placeholder='场'/>&nbsp;到");
					liDataArr.push(" <input type='text' name='endSeriesNo' id='endSeriesNo' style='width:30px;height:25px;' placeholder='集'/>&nbsp;-");
					liDataArr.push(" <input type='text' name='endViewNo' id='endViewNo' style='width:30px;height:25px;' placeholder='场'/>");
					liDataArr.push(" &nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' id='isAll' />含已拍");
					liDataArr.push(" <label style='display: inline-block;margin-top: 10px;'>集场编号：</label><input type='text' id='seriesViewNos' style='height: 27px;width:170px' placeholder='输入范例：1-1,1-2,1-3,...'>");
				}
	            $collFirstLi.append(liDataArr.join(""));
	            
	            var $viewNoTr = $("#viewNoTr");
	            var noticeViewNoTr = $("#noticeViewNoTr");
	            if (crewType == 0 || crewType == 3) { //电影
	            	$viewNoTr.append(" <th>场</th>");
	            	noticeViewNoTr.append(" <th>场</th>");
				}else {
					$viewNoTr.append(" <th>集场</th>");
					noticeViewNoTr.append(" <th>集场</th>");
				}
	            
	            var $collScenceDrop = $("#collScenceDrop");
	            if (crewType == 0 || crewType == 3) { //电影
	            	$collScenceDrop.before("场<div class='select-picture-div viewno-div'></div>");
				}else {
					$collScenceDrop.before("集场<div class='select-picture-div viewno-div'></div>");
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//加载通告时间
function loadNoticeDate(){
	var noticeId = $("#viewNoticeId").val();
	$.ajax({
		url: '/notice/getNoticeDate',
		type: 'post',
		data:{noticeId:noticeId},
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
				noticeDate = response.noticeDate;
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//跳转页面时，加载左侧集场列表
/*function loadLeftData(){
	var noticeId = $("#viewNoticeId").val();
	
	//加载左侧集场号数据
	if (isFirstLoad) {
		$.ajax({
			url: '/notice/loadNoticeView',
			type: 'post',
			timeout: 5000,//超时设置5秒
			data: {pageNo: 1, pagesize: 15,pageFlag: true,isAll: '0',noticeId:noticeId},
			datatype: 'json',
			async: true,
			success: function(response){
				var viewList = response.viewList;
				//加载表格数据
				for (var i = 0; i < viewList.length; i++) {
					var viewMap = viewList[i];
					if (crewType == 0 || crewType == 3) { //电影
						firstRightNoArr.push(viewMap['viewNo']);
					}else {
						firstRightNoArr.push(viewMap['seriesNo'] + "-" + viewMap['viewNo']);
					}
				}
			}
		});
	}
}*/

//跳转通告单场景页面时，需要加载的数据
function loadNoticeViewData(){
	var noticeId = $("#viewNoticeId").val();
	//显示loading效果
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDataDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	
	$.ajax({
		url: '/notice/loadNoticeView',
		type: 'post',
		data:{noticeId:noticeId,pageFlag:false},
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
				
				//取出通告单id
				var viewList = response.viewList;
				var totalPage = parseFloat(response.totalPage).toFixed(1);
				//对隐藏的文本赋值
				if (viewList != null && viewList.length == 1) {
					var map = viewList[0];
					var viewId = map['viewId'];
					if (viewId == null || viewId =='' || viewId == undefined) {
						$("#totalView").val(0);
						$("#totalPage").val(0);
					}else {
						$("#totalView").val(viewList.length);
						$("#totalPage").val(totalPage);
					}

				}else {
					$("#totalView").val(viewList.length);
					$("#totalPage").val(totalPage);
				}
				var grid = $("#grid");
				//每次加载之前先清空上次的数据
				loadNoticeViewTable(grid,viewList,noticeId, crewType,totalPage);
				
				_LoadingHtml.hide();
				$(".opacityAll").hide();
				//加载集场号
				var $noticeViewNoTr = $("#noticeViewNoTr");
				var addDataArr = [];
//				addDataArr.push(" <tr name='nitoceRightViewNoTr'><td></td></tr>");
//				for(var c =0; c<firstRightNoArr.length; c++){
//					var viewNo = firstRightNoArr[c];
//					addDataArr.push(" <tr name='nitoceRightViewNoTr'><td>" + viewNo + "</td></tr>");
//				}
//				//先清空在添加
				var trs = $("tr[name='nitoceRightViewNoTr']");
				for(var d=0; d<trs.length; d++){
					trs[d].remove();
				}
//				addDataArr.push(" <tr name='nitoceRightViewNoTr'><td><span class='add-view-span'>点击添加场景至通告单</span></td></tr>");
//				$noticeViewNoTr.after(addDataArr.join(""));
				
				//取出请假信息
				var leaveInfo = response.actorLeaveInfo;
				if (leaveInfo != '' && leaveInfo != null && leaveInfo != 'undefined') {
					showInfoMessage(leaveInfo);
					//showErrorMessage(leaveInfo);
				}
				
				//重置数据列表滚动条、
				//取出div的高度
				divHeigth = $("#noticeSceneList").height();
				//计算出滚动条的距离
				var topDistance = divHeigth - 100;
				$("#ca").css("height", 'calc(100% - 21px)');
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//加载通告单场景列表
function loadBackupViewTableData (viewList){
	//取消全选
	$("#selectAll").prop("checked", false);
	isFirstLoad = false;
	//清空集场号集合
	firstRightNoArr = [];
	//拼接数据
	/*var $noticeViewTable = $("#firstTr");*/
	var $noticeViewTable = $("#tableMainBody");
	var viewArr =[];
	if (viewList != null || viewList.length>0) {
		for (var i = 0; i < viewList.length; i++) {
			var viewMap = viewList[i];
			
			var viewIds = viewMap['viewId'];
			if (viewIds == null || viewIds == undefined) {
				continue;
			}
			//页数
			var pageCount = viewMap['pageCount'];
			if (pageCount == null || pageCount == '' || pageCount == 'undefined') {
				pageCount =0;
			}
			viewArr.push(" <tr name='tableName' onclick='selectCheckBox(this)'>");
			viewArr.push(" <td style='width: 50px; min-width: 50px; max-width: 50px; text-align: center; padding-left: 3px;'><input type='checkbox' name='viewListCheckBox' value='" + viewMap['viewId'] + "' tpage='"+ subStringPage(pageCount) +"' onclick='clickCheckBox(this)'></td>");
			//集场号
			if (crewType == 0 || crewType == 3) { //电影
				viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'>" + viewMap['viewNo'] + "</td>");
				firstRightNoArr.push(viewMap['viewNo']);
			}else {
				viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'>" + viewMap['seriesNo'] + "-" + viewMap['viewNo'] + "</td>");
				firstRightNoArr.push(viewMap['seriesNo'] + "-" + viewMap['viewNo']);
			}
			
			//气氛
			var atmosphereName = viewMap['atmosphereName'];
			if (atmosphereName == null || atmosphereName == '' || atmosphereName == 'undefined') {
					viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'></td>");
			}else {
					viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'>" + atmosphereName + "</td>");
			}
			
			//内外景
			var site = viewMap['site'];
			if (site == null || site == '' || site == 'undefined') {
				viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'></td>");
			}else {
					viewArr.push(" <td style='width: 70px; min-width: 70px; max-width: 70px; padding-left: 3px;text-align: center;'>" + site + "</td>");
			}
			
			//拍摄地点
			var shootLocation = viewMap['shootLocation'];
			if (shootLocation == null || shootLocation == '' || shootLocation == 'undefined') {
				viewArr.push(" <td style='width: 125px; min-width: 125px; max-width: 125px;'></td>");
			}else {
//				if(viewMap['shootRegion'] == null || viewMap['shootRegion'] == undefined){
//				}else{
//					shootLocation += "(" + viewMap['shootRegion'] + ")";
//				}
				viewArr.push(" <td style='width: 125px; min-width: 125px; max-width: 125px;' title='" + shootLocation + "' style='text-align:left;'><div class='jqx-column' >" + shootLocation + "</div></td>");
			}
			
			//主场景
			var majorView = viewMap['majorView'];
			var minorView = viewMap['minorView'];
			if (majorView == null || majorView == '' || majorView == 'undefined') {
				//次场景
				if (minorView == null || minorView == '' || minorView == 'undefined') {
					viewArr.push(" <td style='width: 120px; min-width: 120px; max-width: 120px;'></td>");
				}else {
					viewArr.push(" <td style='width: 120px; min-width: 120px; max-width: 120px;' title='" + minorView + "'><div class='jqx-column major-view-div'>" + minorView + "</div></td>");
				}
				
			}else {
				if (minorView == null || minorView == '' || minorView == 'undefined') {
					viewArr.push(" <td style='width: 120px; min-width: 120px; max-width: 120px;' title='" + majorView + "'><div class='jqx-column major-view-div'>" + majorView + "</div></td>");
				}else {
					viewArr.push(" <td style='width: 120px; min-width: 120px; max-width: 120px;' title='" + majorView + " | " + minorView + "'><div class='jqx-column major-view-div'>" + majorView + " | " + minorView + "</div></td>");
				}
			}
			
//			//次场景
//			if (minorView == null || minorView == '' || minorView == 'undefined') {
//				viewArr.push(" <td></td>");
//			}else {
//				viewArr.push(" <td title='" + minorView + "'><div class='jqx-column'>" + minorView + "</div></td>");
//			}
			
			//页数
			viewArr.push(" <td style='width: 60px; min-width: 60px; max-width: 60px;text-align: center;'><div class='width-pageCount-div'>" + subStringPage(pageCount) + "</div></td>");
			
			
			//内容
			var mainContent = viewMap['mainContent'];
			if (mainContent == null || mainContent == '' || mainContent == 'undefined') {
				viewArr.push(" <td style='width: 190px; min-width: 190px; max-width: 190px;' class='content-column'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 190px; min-width: 190px; max-width: 190px;' class='content-column left-text' title='" + mainContent + "'><div class='jqx-column content-value-div'>" + mainContent + "</div></td>");
			}
			
			//主要演员
			var roleList = viewMap['roleList'];
			if (roleList == null || roleList == '' || roleList == 'undefined') {
				viewArr.push(" <td style='width: 130px; min-width: 130px; max-width: 130px;' class='main-actor'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 130px; min-width: 130px; max-width: 130px;' class='main-actor left-text'><div class='jqx-column major-info-div' title='" + roleList + "'>" + roleList + "</div></td>");
			}
			
			//特约演员
			var guestRoleList = viewMap['guestRoleList'];
			if (guestRoleList == null || guestRoleList == '' || guestRoleList == 'undefined') {
				viewArr.push(" <td style='width: 110px; min-width: 110px; max-width: 110px;' class='special-actor'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 110px; min-width: 110px; max-width: 110px;' class='special-actor left-text'><div class='jqx-column' title='" + guestRoleList + "'>" + guestRoleList + "</div></td>");
			}
			
			//群众演员
			var massRoleList = viewMap['massRoleList'];
			if (massRoleList == null || massRoleList == '' || massRoleList == 'undefined') {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='public-actor'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='public-actor left-text'><div class='jqx-column' title ='" + massRoleList + "'>" + massRoleList + "</div></td>");
			}
			
			//化妆
			var makeupName = viewMap['makeupName'];
			if (makeupName == null || makeupName == '' || makeupName == 'undefined') {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop left-text'><div class='jqx-column' title='" + makeupName + "'>" + makeupName + "</div></td>");
			}
			//服装
			var clothesName = viewMap['clothesName'];
			if (clothesName == null || clothesName == '' || clothesName == 'undefined') {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop left-text'><div class='jqx-column' title='" + clothesName + "'>" + clothesName + "</div></td>");
			}
			//道具
			var propsList = viewMap['propsList'];
			var specialPropsList = viewMap['specialPropsList'];
			if (specialPropsList == null || specialPropsList == '' || specialPropsList == 'undefined') {
				if (propsList == null || propsList == '' || propsList == 'undefined') {
					viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop'><div class='jqx-column'></div></td>");
				}else {
					viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop left-text'><div class='jqx-column' title='" + propsList + "'>" + propsList + "</div></td>");
				}
			}else {
				if (propsList == null || propsList == '' || propsList == 'undefined') {
					viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop left-text'><div class='jqx-column' title='" + specialPropsList + "'>" + specialPropsList + "</div></td>");
				}else {
					viewArr.push(" <td style='width: 103px; min-width: 103px; max-width: 103px;' class='cloth-dress-prop left-text'><div class='jqx-column' title='" + propsList + "|" + specialPropsList + "'>" + propsList + "|" + specialPropsList + "</div></td>");
				}
			}
			
			//备注
			var remark = viewMap['remark'];
			if (remark == null || remark == '' || remark == 'undefined') {
				viewArr.push(" <td style='width: 201px; min-width: 201px; max-width: 201px;'  class='remark-column'><div class='jqx-column'></div></td>");
			}else {
				viewArr.push(" <td style='width: 201px; min-width: 201px; max-width: 201px;' class='remark-column left-text' title='" + remark + "'><div class='jqx-column'>" + remark + "</div></td>");
			}
			
		}
	}
	//添加之前先清空数据
	var trArr = $("tr[name='tableName']");
	for (var a = 0; a < trArr.length; a++) {
		trArr[a].remove();
	}
	
	/*$noticeViewTable.after(viewArr.join(""));*/
	$noticeViewTable.append(viewArr.join(""));
	
/*************************************加载左侧面板上的数据*******************************************/
	//加载场数和页数
	var totalView = $("#totalView").val(); //总场数
	var totalPage = $("#totalPage").val(); //总页数
	var $viewPageCountDiv = $("#viewPageCountDiv");
	$viewPageCountDiv.html("");
	
	if (tempPage == 0.0) {
		$viewPageCountDiv.append("  共 <span>" + totalView + "</span>场/<span>" + subStringPage(totalPage) + "</span>页");
	}else {
		$viewPageCountDiv.append("  共 <span>" + totalView + "</span>场/<span>" + subStringPage(tempPage) + "</span>页");
	}
	
	//加载集场号
	/*var $viewNoTr = $("#viewNoTr");*/
	var $viewNoTr = $("#breakSecentTableData");
	var addTrArr = [];
	/*addTrArr.push("<tr></tr>");*/
	for(var c =0; c<viewNoArr.length; c++){
		var viewNo = viewNoArr[c];
		addTrArr.push(" <tr name='viewNoTr'><td>" + viewNo + "</td></tr>");
	}
	//先清空在添加
	var trs = $("tr[name='viewNoTr']");
	for(var d=0; d<trs.length; d++){
		trs[d].remove();
	}
	/*$viewNoTr.after(addTrArr.join(""));*/
	$viewNoTr.append(addTrArr.join(""));
}

//点击checkbox事件
function clickCheckBox(own){
	var checked = $(own).prop("checked");
	if (checked) {
		$(own).prop("checked", false);
	}else {
		$(own).prop("checked", true);
	}
}

//点击行选中
function selectCheckBox(own){
	//取出当前行的状态
	var checked = $(own).find("input").prop("checked");
	if (checked) {
		$(own).find("input").prop("checked", false);
		$(own).css("background", "");
	}else {
		$(own).find("input").prop("checked", true);
		//设置背景颜色
		$(own).css("background", "#E3E3E4");
	}
	var checkbox = $("#tableMainBody input[name='viewListCheckBox']");
	var isAllCheck = true;
	for(var i=0; i<checkbox.length;i++){
		//取出每个checkbox的选中状态
		var checked = $(checkbox[i]).prop("checked");
		if (!checked) {
			isAllCheck = false;
			break;
		}
	}
	if (isAllCheck) {
		$("#selectAll").prop("checked", true);
	}else {
		$("#selectAll").prop("checked", false);
	}
	
	updateVIewPageCount(own);
}

//修改选中的场数和页数的值
function updateVIewPageCount(own){
	//清空选中
	$("#viewPageCount").html('');
	//取出每个checkbox的选中状态
	var checked = $(own).find("input").prop("checked");
	if (checked) {
		viewSelectCount = viewSelectCount +1;
		//获取页数
		var pageCount = $(own).find("input").attr("tpage");
		pageSelectCount = floatAdd(parseFloat(pageSelectCount) , parseFloat(pageCount)).toFixed(1);
	}else {
		viewSelectCount = viewSelectCount - 1;
		//获取页数
		var pageCount = $(own).find("input").attr("tpage");
		pageSelectCount = (floatSub(parseFloat(pageSelectCount), parseFloat(pageCount))).toFixed(1);
	}
	
	//改变显示的值
	$("#viewPageCount").append("<span>"+ viewSelectCount+"场/"+pageSelectCount+"页" +"</span>");
}

//小数运算
function floatAdd(arg1,arg2){
    var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length;}catch(e){r1=0;}
    try{r2=arg2.toString().split(".")[1].length;}catch(e){r2=0;}
    m=Math.pow(10,Math.max(r1,r2));
    return (arg1*m+arg2*m)/m;
}

//小数运算
function floatSub(arg1, arg2) { 
	var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length;}catch(e){r1=0;}
    try{r2=arg2.toString().split(".")[1].length;}catch(e){r2=0;}
    m=Math.pow(10,Math.max(r1,r2));
    return (arg1*m-arg2*m)/m;
} 

//截取页数只显示小数点后两位数
function subStringPage(PageCount){
	var pageCountStr = PageCount+"";
	var subStr = pageCountStr.substring(0,pageCountStr.indexOf(".")+3);
	return subStr;
}

//默认选中分场表
function defaultSelect(){
	$("#breakScenceBtn").addClass("on");
	$("#breakScenceBtn").on("click", function(){
		$(this).addClass("on");
		$("#planOneBtn").removeClass("on");
		$("#planTwoBtn").removeClass("on");
	});
	
	$("#planOneBtn").on("click", function(){
		$(this).addClass("on");
		$("#breakScenceBtn").removeClass("on");
		$("#planTwoBtn").removeClass("on");
	});
	
	$("#planTwoBtn").on("click", function(){
		$(this).addClass("on");
		$("#breakScenceBtn").removeClass("on");
		$("#planOneBtn").removeClass("on");
	});
	
	
}

//全选
function selectAll(own){
	viewSelectCount = 0;
	pageSelectCount = 0;
	//清空显示
	$("#viewPageCount").html('');
	
	own = $(own);
	
	if(own.is(":checked")){
		$("#tableMainBody input[name='viewListCheckBox']").prop("checked", true);
		var trs = $("#tableMainBody tr");
		for(var i =0 ; i<trs.length; i++){
			$(trs[i]).css("background", "#E3E3E4");
		}
	}else{
		$("#tableMainBody input[name='viewListCheckBox']").prop("checked", false);
		var trs = $("#tableMainBody tr");
		for(var i =0 ; i<trs.length; i++){
			$(trs[i]).css("background", "");
		}
	}
	
	//判断页数和场数
	var checkbox = $("#tableMainBody input[name='viewListCheckBox']");
	for(var i=0; i<checkbox.length;i++){
		//取出每个checkbox的选中状态
		var checked = $(checkbox[i]).prop("checked");
		if (checked) {
			viewSelectCount = viewSelectCount +1;
			//获取页数
			var pageCount = $(checkbox[i]).attr("tpage");
			pageSelectCount = floatAdd(parseFloat(pageSelectCount) , parseFloat(pageCount)).toFixed(1);
		}
	}
	
	//改变显示的值
	$("#viewPageCount").append("<span>"+ viewSelectCount+"场/"+pageSelectCount+"页" +"</span>");
}



//初始化表格分页，并加载表格数据
function initTablePage(){
	viewSelectCount = 0;
	pageSelectCount = 0;
	$("#viewPageCount").html('');
	//改变显示的值
	$("#viewPageCount").append("<span>0场/0页</span>");
	//查询不是拍摄完成/加戏完成/删戏的场景
	if($("#isAll").prop("checked")){
		filter.isAll="1";
	}else{
		filter.isAll="0";
	}
	//取出通告单id
	var noticeId = $("#viewNoticeId").val();
	//ajax请求数据
	$.ajax({
		url: '/notice/loadNoticeView',
		type: 'post',
		data: {noticeId: noticeId,pageNo: 1, pagesize: 15,pageFlag: true,atmosphere: filter.atmosphere,site: filter.site,major: filter.major,minor: filter.minor,
			clothes: filter.clothes, makeup: filter.makeup, props: filter.props, roles: filter.roles,guest: filter.guest,mass: filter.mass,
			shootLocation: filter.shootLocation,seriesNo: filter.seriesNo, viewNo: filter.viewNo,startSeriesNo: filter.startSeriesNo,endSeriesNo:filter.endSeriesNo, 
			startViewNo: filter.startViewNo, endViewNo: filter.endViewNo, seriesViewNos: filter.seriesViewNos, mainContent: filter.mainContent, remark: filter.remark,
			isAll: filter.isAll, sortType: filter.sortType,searchMode: 1},//recordCount每页显示条数
		datatype: 'json',
		success: function(response){
			var total = response.total;
			if (total == 0 || total == null) {
				total = 1;
			}
			var viewList = response.viewList;
			$('#tablePage').html("");
			//加载表格数据
			loadBackupViewTableData(viewList);
			
			$('#tablePage').Paging({
				pagesize:15,
				count:total,
				callback:function(page,size,count){
					$.ajax({
						url: '/notice/loadNoticeView',
						type: 'post',
						data: {noticeId: noticeId,pageNo: page, pagesize: 15,pageFlag: true,atmosphere: filter.atmosphere,site: filter.site,major: filter.major,
							minor: filter.minor,clothes: filter.clothes, makeup: filter.makeup, props: filter.props, roles: filter.roles,
							guest: filter.guest,mass: filter.mass,shootLocation: filter.shootLocation,seriesNo: filter.seriesNo,
							startSeriesNo: filter.startSeriesNo,endSeriesNo:filter.endSeriesNo, startViewNo: filter.startViewNo, 
							endViewNo: filter.endViewNo, seriesViewNos: filter.seriesViewNos,mainContent: filter.mainContent, remark: filter.remark, 
							viewNo: filter.viewNo, isAll: filter.isAll,sortType: filter.sortType,searchMode: 1},
						datatype: 'json',
						success: function(response){
							var viewList = response.viewList;
							viewSelectCount = 0;
							pageSelectCount = 0;
							//加载表格数据
							$("#viewPageCount").html('');
							//改变显示的值
							$("#viewPageCount").append("<span>0场/0页</span>");
							loadBackupViewTableData(viewList);
						}
					});
				}
			});
		}
	});
}


//选择集场
function selectCollScence(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-view-table #collScenceDrop").css({'left': left-35, 'top': top+own.outerHeight()}).slideToggle("fast");
	ev.stopPropagation();
	$("#collScenceDrop").on("click",function(ev){
		ev.stopPropagation();
	});
	if (viewNoflag) {
		//改变图标
		$("div[class*='viewno-div']").css("background", "url('../../images/up-select.png') no-repeat");
		viewNoflag = false;
	}else {
		$("div[class*='viewno-div']").css("background", "url('../../images/select-picture.png') no-repeat");
		viewNoflag = true;
	}
}

//选择内容
function selectContent(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-view-table #collContentDrop").css({'left': left-35, 'top': top+own.outerHeight()}).slideToggle("fast");
	ev.stopPropagation();
	$("#collContentDrop").on("click",function(ev){
		ev.stopPropagation();
	});
	if (contantFlag) {
		//改变图标
		$("div[class*='content-div']").css("background", "url('../../images/up-select.png') no-repeat");
		contantFlag = false;
	}else {
		contantFlag = true;
		$("div[class*='content-div']").css("background", "url('../../images/select-picture.png') no-repeat");
	}
}

//选择备注
function selectRemark(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-view-table #collRemarkDrop").css({'left': left-50, 'top': top+own.outerHeight()}).slideToggle("fast");
	ev.stopPropagation();
	$("#collRemarkDrop").on("click",function(ev){
		ev.stopPropagation();
	});
	if (reamrkFlag) {
		//改变图标
		$("div[class*='reamrk-div']").css("background", "url('../../images/up-select.png') no-repeat");
		reamrkFlag = false;
	}else {
		reamrkFlag = true;
		$("div[class*='reamrk-div']").css("background", "url('../../images/select-picture.png') no-repeat");
	}
}

//下拉菜单的初始化及数据绑定
function initDropDownMenu(){
	
	$.ajax({
        url:"/viewManager/loadAdvanceSerachData",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
            if(data.success) {
                var viewFilterDto = data.viewFilterDto;
                //气氛
                var atmosphereList = viewFilterDto.atmosphereList;
                //内外景
                var siteList = viewFilterDto.siteList;
                //场景地点
                //var viewLocationList = viewFilterDto.viewLocationList;
                //主场景
                var firstLocationList = viewFilterDto.firstLocationList;
                //次场景
                var secondLocationList = viewFilterDto.secondLocationList;
                //三级场景
                //var thirdLocationList = viewFilterDto.thirdLocationList;
                //主要演员
                var majorRoleList = viewFilterDto.majorRoleList;
                //特约演员
                var guestRoleList = viewFilterDto.guestRoleList;
                //群众演员
                var massesRoleList = viewFilterDto.massesRoleList;
                //普通道具
                var commonPropList = viewFilterDto.commonPropList;
                //特殊道具
                //var specialPropList  = viewFilterDto.specialPropList;
                //服装
                var clotheList = viewFilterDto.clotheList;
                //化妆
                var makeupList = viewFilterDto.makeupList;
                //拍摄地点
                var shootLocationList = viewFilterDto.shootLocationList;
//                var shootLocationRegionList = viewFilterDto.shootLocationRegionList;
                
                //选择气氛
               /* $("#airAtmoSelect").append("<option value='0' style='background-color:#FFCC80;'>【&nbsp;&nbsp;搜索&nbsp;&nbsp;】</option>");*/
                for(var atmosphere in atmosphereList){
                	$("#airAtmoSelect").append("<option value='" + atmosphere + "'>" + atmosphereList[atmosphere] + "</option>");
                }
                $("#airAtmoSelect").selectpicker("refresh");
                
                //选择内外景
                for(var site in siteList){
                	$("#siteSelect").append("<option value='" + siteList[site] + "'>" + siteList[site] + "</option>");
                }
                $("#siteSelect").selectpicker("refresh");
                
                //选择主场景
                for(var firstLocation in firstLocationList){
                	$("#firstScenceSelect").append("<option value='" + firstLocationList[firstLocation] + "'>" + firstLocationList[firstLocation] + "</option>");
                }
                $("#firstScenceSelect").selectpicker("refresh");
                
                //选择次场景
                for(var secondLocation in secondLocationList){
                	$("#secondScenceSelect").append("<option value='" + secondLocationList[secondLocation] + "'>" + secondLocationList[secondLocation] + "</option>");
                }
                $("#secondScenceSelect").selectpicker("refresh");
                
                //选择拍摄地
                for(var shootLocation in shootLocationList){
                	$("#shootLocationSelect").append("<option value='" + shootLocation + "'>" + shootLocationList[shootLocation] + "</option>");
                }
                $("#shootLocationSelect").selectpicker("refresh");
                
                //选择主要演员
                for(var majorRole in majorRoleList){
                	$("#mainActorSelect").append("<option value='" + majorRole + "'>" + majorRoleList[majorRole] + "</option>");
                }
                $("#mainActorSelect").selectpicker("refresh");
                
                //选择特约演员
                for(var guestRole in guestRoleList){
                	$("#specialActorSelect").append("<option value='" + guestRole + "'>" + guestRoleList[guestRole] + "</option>");
                }
                $("#specialActorSelect").selectpicker("refresh");
                
                //选择群众演员
                for(var massesRole in massesRoleList){
                	$("#publicActorSelect").append("<option value='" + massesRole + "'>" + massesRoleList[massesRole] + "</option>");
                }
                $("#publicActorSelect").selectpicker("refresh");
                
                //选择化妆
                for(var makeup in makeupList){
                	$("#makeUpSelect").append("<option value='" + makeup + "'>" + makeupList[makeup] + "</option>");
                }
                $("#makeUpSelect").selectpicker("refresh");
                
                //选择服装
                for(var clothe in clotheList){
                	$("#clothSelect").append("<option value='" + clothe + "'>" + clotheList[clothe] + "</option>");
                }
                $("#clothSelect").selectpicker("refresh");
                
                //选择道具
                for(var commonProp in commonPropList){
                	$("#propSelect").append("<option value='" + commonProp + "'>" + commonPropList[commonProp] + "</option>");
                }
                $("#propSelect").selectpicker("refresh");
            }
        }
    });
    
    //显示清空按钮
	$('table.notice-view-table th').on('mouseover', function(event) {
        if ($(this).find("select").val() != null && $(this).find("select").val() != undefined) {
            $(this).find(".clearSelection").show();
        }
    });
	
    
    $('table.notice-view-table th').on('mouseout', function(event) {
        $(this).find(".clearSelection").hide();
    });
}

//清空气氛
function clearAtmoSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	var $span =$("div[class*='air-atmo-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空内外
function clearSiteSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='site-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空主场景
function clearFirstSceneSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='first-scence-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空次场景
function clearSecondSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='second-scence-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空拍摄地点
function clearShootLocationSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='shoot-location-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}
//清空拍摄地域
function clearShootRegionSelection(own){
	$("#setShootReginInfo").selectpicker('deselectAll');
	$(own).hide();
}

//清空主要演员
function clearMajorSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='main-actor-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空特约演员
function clearSpecialSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='special-actor-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空群众演员
function clearPublicSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='public-actor-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空化妆
function clearMakeUpSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='dress-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span[i].addClass("caret");
}

//清空服装
function clearClosthSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='cloth-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//清空道具
function clearPropSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	
	var $span =$("div[class*='prop-select'] span[class='up-arrow']");
	$span.removeClass("up-arrow");
	$span.addClass("caret");
}

//点击集场次筛选
function searchViewNo(){
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	//隐藏下拉框
	$("#collScenceDrop").slideUp("fast");
	
	//判断集场下拉框中是否有输入值
	if (crewType == 0 || crewType == 3) { //电影或网大
		var startViewNo = $("#startViewNo").val();
		var endViewNo = $("#endViewNo").val();
		var viewNos = $("#viewNos").val();
		if (startViewNo == '' || startViewNo == undefined) {
			if (endViewNo == '' || endViewNo == undefined) {
				if (viewNos == '' || viewNos == undefined) {
					if (!$("#isAll").prop("checked")) {
						$("div[class*='viewno-div']").css("background", "url('../../images/select-picture.png') no-repeat");
					}
				}
			}
		}
	}else {//电视剧或网剧
		var startSeriesNo = $("#startSeriesNo").val();
		var startViewNo = $("#startViewNo").val();
		var endSeriesNo = $("#endSeriesNo").val();
		var endViewNo = $("#endViewNo").val();
		var seriesViewNos = $("#seriesViewNos").val();
		if (startSeriesNo == '' || startSeriesNo == undefined) {
			if (startViewNo == '' || startViewNo == undefined) {
				if (endSeriesNo == '' || endSeriesNo == undefined) {
					if (endViewNo == '' || endViewNo == undefined) {
						if (seriesViewNos == '' || seriesViewNos == undefined) {
							if (!$("#isAll").prop("checked")) {
								$("div[class*='viewno-div']").css("background", "url('../../images/select-picture.png') no-repeat");
							}
						}
					}
				}
			}
		}
	}
}

//点击内容筛选
function searchContent(){
	$("#collContentDrop").slideUp("fast");
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	//隐藏下拉框
	
	//判断集场下拉框中是否有输入值
	var $content = $("#mainContent").val();
	if ($content == '' || $content == null || $content == undefined) {
		$("div[class*='content-div']").css("background", "url('../../images/select-picture.png') no-repeat");
	}
}

//点击备注筛选
function searchRemark(){
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
	//隐藏下拉框
	$("#collRemarkDrop").slideUp("fast");
	
	//判断集场下拉框中是否有输入值
	var $content = $("#ReamrkContent").val();
	if ($content == '' || $content == null || $content == undefined) {
		$("div[class*='reamrk-div']").css("background", "url('../../images/select-picture.png') no-repeat");
	}
}

//初始化下拉菜单收起时的事件
function initCloseSelect(){
	
	//设置下拉框的长度
	$('.selectpicker').selectpicker({
		  size: 15
		});
	
	$(".selectpicker").on('change', function(event){
		var eventId = event.target.id;
		if(eventId != "setShootReginInfo"){
			return;
		}
		if($("#addressInput").val() != ""){
			var value = event.target.value;
			var prevSelectedValue = $(".preValue").val(); 
			if(prevSelectedValue && prevSelectedValue != $("#"+ eventId).val()){
				$("#"+ eventId).find('option[value='+prevSelectedValue+']').prop('selected', false).removeAttr('selected');
			}
	        $("#"+ eventId).selectpicker('render');
	        
	        //$("#selectpickerPreValue").val($("#"+ eventId).val());
	        $(".preValue").val($("#"+ eventId).val());
		}else{
			$("#"+ eventId).selectpicker('deselectAll');    //首先取消所有选中
		}
		
	});
	
	
	//气氛筛选
	$('#airAtmoSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		//更改箭头
		var $airAtmo = $("#airAtmoSelect").val();
		var $span = $("div[class*='air-atmo-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='air-atmo-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//内外筛选
	$('#siteSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		//更改箭头
		var $airAtmo = $("#siteSelect").val();
		var $span = $("div[class*='site-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='site-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//主场景筛选
	$('#firstScenceSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#firstScenceSelect").val();
		var $span = $("div[class*='first-scence-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='first-scence-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//次场景筛选
	$('#secondScenceSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#secondScenceSelect").val();
		var $span = $("div[class*='second-scence-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='second-scence-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//拍摄地点筛选
	$('#shootLocationSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#shootLocationSelect").val();
		var $span = $("div[class*='shoot-location-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='shoot-location-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//主要演员筛选
	$('#mainActorSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#mainActorSelect").val();
		var $span = $("div[class*='main-actor-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='main-actor-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//特约演员筛选
	$('#specialActorSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#specialActorSelect").val();
		var $span = $("div[class*='special-actor-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='special-actor-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//群众演员筛选
	$('#publicActorSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#publicActorSelect").val();
		var $span = $("div[class*='public-actor-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='public-actor-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//化妆筛选
	$('#makeUpSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#makeUpSelect").val();
		var $span = $("div[class*='dress-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='dress-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//服装筛选
	$('#clothSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#clothSelect").val();
		var $span = $("div[class*='cloth-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='cloth-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//道具筛选
	$('#propSelect').on('change',function(){
		//获取选中的条件
		loadSearchResult();
		//重新加载数据
		initTablePage();
		
		//更改箭头
		var $airAtmo = $("#propSelect").val();
		var $span = $("div[class*='prop-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='prop-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
}


//设置拍摄地
function setShootLocationAddress(){
	//取出场景号
	var rowindexes =$("#noticeViewTbody input[type='checkbox']:checked");
	
	var viewIds = "";
	$.each(rowindexes,function(){
		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	
	if (viewIds == null || viewIds == undefined || viewIds == '') {
		showErrorMessage("请选择要设置的场景！");
		return;
	}
	loadSetAddressWindow();
}

//加载设置拍摄地窗口
function loadSetAddressWindow() {
	if (setAddressWindow == null || setAddressWindow == undefined) {
        setAddressWindow = $("#setAddressWindow").jqxWindow({
//            theme:theme,  
			width: 640,
			height: 420, 
			autoOpen: false,
			isModal: true,
			resizable: false,
			cancelButton: $('#setAddressClose'),
			initContent: function() {
				//查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
				setAddressWindow.on("open", function(){
					$.ajax({
						url:"/sceneViewInfoController/queryShootLocationList",
						dataType:"json",
						type:"post",
						async: false,
						success:function(data){
							var shootLocationList = data.shootLocationList;
							$("#addressList").empty();
							$.each(shootLocationList, function() {
								if(this.vcity == null || this.vcity == undefined){
									this.vcity = "";
								}
								var vname="";
								if(this.vname) {
									vname+=this.vname;
								}
								if(this.vcity) {
									vname+="("+this.vcity+")";
								}
								$("#addressList").append("<span class='addressOpt' value="+ this.vname +" shoot-regin='"+ this.vcity +"'>"+ vname +"</span>");
							});
							$("#addressList span").unbind('click');
							$("#addressList span").on('click', function(event) {
								$("#addressInput").val($(this).attr('value'));
								var shootRegin = $(this).attr("shoot-regin");
								$("#setShootReginInfo").selectpicker('val', shootRegin);
//								$("#setShootReginInfo").find('option[value='+ shootRegin +']').prop('selected', true).attr('selected', 'selected');
								$(this).siblings("span").removeClass("mouse_click");
								$(this).addClass("mouse_click");
							});
						}
					});
				});
				
				
				$.ajax({
					url: '/sceneViewInfoController/queryProCityList',
					type: 'post',
					datatype: 'json',
					async: false,
					success: function(response){
						if(response.success){
							var shootRegionList = response.shootRegionList;
							if(shootRegionList != null){
								var html = [];
								for(var i=0; i< shootRegionList.length; i++){
									html.push('<option  value="'+ shootRegionList[i] +'">'+ shootRegionList[i] +'</option>');
								}
								$("#setShootReginInfo").append(html.join(""));
				                $("#setShootReginInfo").selectpicker("refresh");
				                $('.shoot-region').on('mouseover', function(event) {
							        if ($("#setShootReginInfo").val() != null && $("#setShootReginInfo").val() != undefined) {
							        	$(this).next(".clearSelection").show();
							        }
							    });
							}
						}
					}
				});
				
				$("#setAddressClose, #setAddressButton").jqxButton({/*theme: theme,*/ height: 25, width: 80});
				$("#addressInput").jqxInput({/*theme: theme*/});
				
				
				
				$("#addressInput").unbind("keyup.textchange");
				$("#addressInput").on('keyup.textchange', function() {
					var _this = $(this);
					var addressList = $(this).siblings("#addressList").find("span");
					addressList.each(function(){
						var addValue = $(this).text();
						if(addValue.search($.trim(_this.val())) != -1){
							$(this).show();
						} else {
							$(this).hide();
						}
					});
				}); 
				
			}
		});
	}
     
     $("#setAddressButton").unbind("click");
//     $("#setAddressButton").on("click", function(){
//    	
//     });
     setAddressWindow.jqxWindow("open");
 }
//保存拍摄地和拍摄地域
function saveLocationRegion(){
	var shootLocation = "";
	var shootRegin = "";
	shootLocation = $("#addressInput").val();
	shootRegin = $("#setShootReginInfo").val();
	if(shootRegin){
		shootRegin = $("#setShootReginInfo").val().toString();
	}

	//选出当前选中的每一行的场景id
	var rowindexes =$('#noticeViewTbody :checked');
	
	var viewIds = "";
	$.each(rowindexes,function(){
		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	$.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegin},
		datatype: 'json',
		success: function(response){
			if(response.success){				 
				 if (shootLocation == null || shootLocation == "") {
					 popupPromptBox("提示", "是否确定将所选场景的拍摄地设置为空？", function() {
				        setShootAddress(shootLocation, shootRegin, viewIds);
				     });
				     return;
				 }
				 setShootAddress(shootLocation, shootRegin, viewIds);
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){					 
					 if (shootLocation == null || shootLocation == "") {
						 popupPromptBox("提示", "是否确定将所选场景的拍摄地设置为空？", function() {
					        setShootAddress(shootLocation, shootRegin, viewIds);
					     });
					     return;
					 }
					 setShootAddress(shootLocation, shootRegin, viewIds);
				});
			}
		}
	});
}

//设置拍摄地址信息
function setShootAddress(address, shootRegion, viewIds) {
   if (address != null && address != "") {
       var option = $("#addressList").find("span[value="+ address +"]");
      
       if (option.length == 0) {
           /*$("#addressList").append("<span class='addressOpt' value="+ address +">"+ address +"</span>");*/
           $("#shootLocationSelect").append("<option value='"+address+"'>"+address+"</option>");
           $("#shootLocationSelect").selectpicker('refresh');
           
           /*$("#addressList").find("span[value="+ address +"]").on('click', function(event) {
        	   var text=$(this).text();
				text=text.replace(/\(/g,'').replace(/\)/g,'');
				$("#addressInput").val(text);
               $(this).siblings("span").removeClass("mouse_click");
               $(this).addClass("mouse_click");
           });*/
       }
   }
   
   $.ajax({
       url:"/viewManager/saveAddress",
       data:{viewIds:viewIds,addressStr:address,shootRegion:shootRegion},
       dataType:"json",
       type:"post",
       async:false,
       success:function(data){
           if(data.success){
        	   showSuccessMessage("设置成功！");
        	   //延迟一秒从新加载剧本分析页面
        	   setAddressWindow.jqxWindow("close");
               setTimeout(function (){
            	   reloadNoticeViewList();
               }, 1000);
           }else{
        	   showErrorMessage("系统异常");
           }
       }
   });
}

////显示拍摄地域信息
//function showShootReginInfo(){
//	$("#addressList").hide();
//	$("#shootReginList").show();
//}
//显示拍摄地信息
//function showAddressList(){
//	$("#shootReginList").hide();
//	$("#addressList").show();
//}




//销场
function cancleView(){
	//取出场景号
	var rowindexes =$('#noticeViewTbody :checked');
	
	var viewIds = "";
	$.each(rowindexes,function(){
		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	
	if (viewIds == null || viewIds == undefined || viewIds == '') {
		showErrorMessage("请选择要销场的场次！");
		return;
	}
	//重置状态选择
	$("#shootStatus").val('');
	setStatusWindow.jqxWindow('open');
	
}

var shootStatus = null;
//加载销场窗口
function loadCancleWindow(){
	$("#finishDate").jqxInput({
//		theme: theme, 
		height:30, 
		width:'300px'
		});
	if (setStatusWindow == null) {
		
		setStatusWindow = $('#setStatusWindow').jqxWindow({ 
//		theme:theme, 
			width: 400,
			isModal: true,
			height: 380, 
			autoOpen:false,
			resizable: false,
			cancelButton: $('#closeWindow'),
			initContent: function () {
//				debugger;
//				var shootStatusSource =
//					[{text:"请选择",value:""},{text:"甩戏",value:"0"},{text:"完成",value:"2"},{text:"部分完成",value:"1"},{text:"删戏",value:"3"},{text:"加戏部分完成",value:"4"},{text:"加戏已完成",value:"5"}];
//				
//				if (shootStatus == null) {
//					
//					shootStatus = $("#shootStatus").jqxDropDownList({
//						
//						/*theme:theme,*/selectedIndex:0,source: shootStatusSource, displayMember: "text", valueMember: "value", width: '300px', height: 30,placeHolder: ""
//							,dropDownHeight: getHeight(shootStatusSource)
//					});
//				}
				$("#shootStatus").on('change', function() {
//					var args = event.args;
//					var item = args.item;
//					
					var value = $(this).val();
					if (value == '2' || value == '5') {
						var $finishDate = $("#finishDateTd");
						$finishDate.html("");
						$finishDate.append("完成日期：");
						var date = new Date(noticeDate);
						$("#finishDate").val(date.Format("yyyy-MM-dd"));
						$("#finishDateDl").show();
					} else if (value == '1' || value == '4') {
						var $finishDate = $("#finishDateTd");
						$finishDate.html("");
						$finishDate.append("拍摄日期：");
						var date = new Date(noticeDate);
						$("#finishDate").val(date.Format("yyyy-MM-dd"));
						$("#finishDateDl").show();
					}else if (value == '0' || value == '3') {
						//如果选择删戏或则甩戏状态，则需要查询当前场次的剪辑信息
						var noticeId= $("#viewNoticeId").val();
						//取出场景号
						var rowindexes =$('#noticeViewTbody :checked');
						
						var viewIds = "";
						$.each(rowindexes,function(){
							viewIds+=$(this).val()+",";
						});
						viewIds=viewIds.substring(0,viewIds.length-1);
						var hasCutViewList = null;
						$.ajax({
							url:"/notice/queryCutViewInfoByViewId",
							type:"post",
							dataType:"json",
							async: false,
							data:{noticeId: noticeId,viewIds:viewIds},
							success:function(data){
								if(data.success){
									hasCutViewList = data.hasCutViewList;
								} else {
									showErrorMessage(data.message);
								}
							}
						});
						
						var cutViewMessage= '';
						if (hasCutViewList != null && hasCutViewList.length >0) {
							for(var i=0; i<hasCutViewList.length; i++){
								if (cutViewMessage == '') {
									cutViewMessage = $("#"+hasCutViewList[i]).attr("sval");
								}else {
									cutViewMessage = cutViewMessage + "，" + $("#"+hasCutViewList[i]).attr("sval");
								}
							}
						}
						var selectStatus = $("#shootStatus").val();
						if (cutViewMessage != '') {
							 swal({
									title: "提示",
									text: cutViewMessage + "有未清空的剪辑信息，如果改变状态，会造成剪辑数据的丢失，是否要继续更改状态？",
									type: "warning",
									showCancelButton: true,
									confirmButtonColor: "rgba(255,103,2,1)",
									confirmButtonText: '确定',
									cancelButtonText: '取消',
									closeOnConfirm: true,
									closeOnCancel: true
								},
								function(isConfirm){
									if(isConfirm){
										$("#shootStatus").val(selectStatus);
									}else{
										 $("#shootStatus").val('');
									}
								});
						}
						$("#finishDateDl").hide();
					}else {
						$("#finishDateDl").hide();
					}
				});
				
				
				$("#tapNo").jqxInput({/*theme: theme*/});
				$("#saveButton, #closeWindow").jqxButton({/*theme:theme,*/ height:25, width:80});
				$("#saveButton").on("click",function(){
					//取出场景号
					var rowindexes =$('#noticeViewTbody :checked');
					
					var viewIds = "";
					$.each(rowindexes,function(){
						viewIds+=$(this).val()+",";
					});
					viewIds=viewIds.substring(0,viewIds.length-1);
					
					var shootStatus=$("#shootStatus").val();
					if(shootStatus==""){
						showErrorMessage("请选择状态！");
						return;
					}
					var shootDate = $("#finishDate").val();
					var tapNo=$("#tapNo").val();
					var remark=$("#remark").val();
					
					var noticeId= $("#viewNoticeId").val();
					var selectedViewId= viewIds;
					$.ajax({
						url:"/notice/saveNoticeViewShootStatus",
						type:"post",
						dataType:"json",
						data:{noticeId:noticeId,shootStatus:shootStatus,shootDate:shootDate,viewIds:selectedViewId,statusRemark:remark,tapNo:tapNo},
						success:function(data){
							if(data.success){
								showSuccessMessage("销场成功");
								//延迟一秒从新加载剧本分析页面
								$("#setStatusWindow").jqxWindow("close");
								setTimeout(function (){
									reloadNoticeViewList();
								}, 1000);
							} else {
								showErrorMessage("设置失败");
							}
						}
					});
				});
			}
		});
	}
	$('#setStatusWindow').on('close', function() {
		$("#shootStatus").jqxDropDownList('selectIndex', 0 );
		$("#tapNo").val("");
		$("#remark").val("");
	});
}

//移除场景
function removeView(viewId, shootStatus){
	//选出当前选中的每一行的场景id
	var noticeId = $("#viewNoticeId").val();
	var rowindexes =$('#noticeViewTbody :checked');
	if(rowindexes.length==0){
		showErrorMessage("请选择要移除的场次！");
		return;
	}
	var finishedView = "";
	$.each(rowindexes,function(){
		var shootStatus = $(this).attr("shootStatus");
		//拍摄状态为已完成
		if (shootStatus == "完成" || shootStatus == '部分完成' || shootStatus == '加戏部分完成' || shootStatus == '加戏已完成') {
			finishedView = "所选场景中有已完成的场次，不能移除场景！";
			return false;
		}
	});
	var viewIds = "";
	$.each(rowindexes,function(){
		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	
	if (finishedView != '' && finishedView != null) {
		showErrorMessage(finishedView);
		return false;
	}
	
	loadRemoveViewWindow(noticeId, viewIds, finishedView);
}
//初始化移除场景窗口
function loadRemoveViewWindow(noticeId, viewIds, finishedView){
	popupPromptBox("提示", finishedView + "确定移出所选场次？", function() {
    	$.ajax({
    		url:"/notice/deleteNoticeView",
    		data:{viewIds:viewIds,noticeId:noticeId},
    		dataType:"json",
    		type:'post',
   			async:false,
   			success:function(data){
   				if(data.success){
   					showSuccessMessage("删除成功");
		        	   //延迟一秒从新加载剧本分析页面
		               setTimeout(function (){
		            	   reloadNoticeViewList();
		               }, 1000);
   				}else{
   					showErrorMessage("删除失败！");
   				}
   			}
    	});
	});
}

//完成场景编辑
function finishEditView(){
	var noticeId = $("#viewNoticeId").val();
	window.location.href="/notice/toGenerateNotice?noticeId="+noticeId;
	$("li.click").removeClass("click").removeClass("center-li-click").prev("li").addClass("click");
	$("li.click").addClass("center-li-click");
}

//显示通告单中的场景
function showNoticeView(){
	//加载集场号
	/*//加载集场号
	var $noticeViewNoTr = $("#noticeViewNoTr");
	var addDataArr = [];
	addDataArr.push(" <tr name='nitoceRightViewNoTr'><td></td></tr>");
	for(var c =0; c<firstRightNoArr.length; c++){
		var viewNo = firstRightNoArr[c];
		addDataArr.push(" <tr name='nitoceRightViewNoTr'><td>" + viewNo + "</td></tr>");
	}
	//先清空在添加
	var trs = $("tr[name='nitoceRightViewNoTr']");
	for(var d=0; d<trs.length; d++){
		trs[d].remove();
	}
	$noticeViewNoTr.after(addDataArr.join(""));*/
	
	$("#noticeViewDiv").show().animate({"right": "0px"},300);
	$("#backupViewDiv").hide().animate({"left": "2000px"},300);
}

//显示备选场景表
function showBackupView(){
	//只读权限，点击添加场景至通告单提示无权限
	if(isNoticeReadonly) {
		showErrorMessage("对不起，您没有权限进行该操作");
		return false;
	}
	
	//加载分场表
	queryFenView();
	
	$("#noticeViewDiv").hide().animate({"right": "2000px"},300);
	$("#backupViewDiv").show().animate({"left":"0px"},300);
	
}

//添加场景到通告单
function addViewToNotice(){
	//选出当前选中的每一行的场景id
	var chooseViewTr = $("#tableMainBody input:checked");
	var noticeId = $("#viewNoticeId").val();
	var viewIds = '';
	for(var i=0; i<chooseViewTr.length; i++){
		if ($(chooseViewTr[i]).val() == '' || $(chooseViewTr[i]).val() == 'on' || $(chooseViewTr[i]).val() == undefined) {
			viewIds = '';
		}else {
			viewIds += $(chooseViewTr[i]).val()+",";
		}
	}
	if (viewIds == null || viewIds == undefined || viewIds == '') {
		showErrorMessage("请选择要添加的场次！");
		return;
	}
	
	//判断当前场景中是否有演员请假
	$.ajax({
		url:"/notice/checkIsLeave",
		data:{viewIds:viewIds,noticeId:noticeId},
		dataType:"json",
		type:'post',
			async:false,
			success:function(data){
				if(!data.success){
					showErrorMessage(data.message);
				}else{
					var leaveInfo = data.leaveInfo;
					if (leaveInfo != null && leaveInfo != '' && leaveInfo != 'undefined') {
						
						noticeDoubleCallBackFun("是否重新选择", leaveInfo,"重新选择", "添加到通告单", null, function () {
							$.ajax({
								url:"/notice/addNoticeView",
								data:{viewIds:viewIds,noticeId:noticeId},
								dataType:"json",
								type:'post',
								async:false,
								success:function(data){
										if(!data.success){
											showErrorMessage(data.message);
										}else{
											showSuccessMessage(data.message);  
							        	   //延迟一秒从新加载剧本分析页面
							               setTimeout(function (){
							            	   reloadNoticeViewList();
							            	   showNoticeView();
							               }, 800);
										}
									}
								});
						});
						  
					}else {
						$.ajax({
			    			url:"/notice/addNoticeView",
			    			data:{viewIds:viewIds,noticeId:noticeId},
			    			dataType:"json",
			    			type:'post',
			    				async:false,
			    				success:function(data){
			    					if(!data.success){
			    						showErrorMessage(data.message);
			    					}else{
			    						showSuccessMessage(data.message);  
		    			        	   //延迟一秒从新加载剧本分析页面
		    			               setTimeout(function (){
		    			            	   reloadNoticeViewList();
		    			            	   showNoticeView();
		    			               }, 800);
			    					}
			    				}
			    			});
					}
				}
			}
	});
	
}


//重新加载通告单场景列表
function reloadNoticeViewList(){
	var noticeId = $("#viewNoticeId").val();
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDataDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	$.ajax({
		url: '/notice/loadNoticeView',
		type: 'post',
		data:{noticeId:noticeId,pageFlag:false},
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				//取出通告单id
				var viewList = response.viewList;
				var totalPage = response.totalPage;
				var grid = $("#grid");
				//重新加载数据
				reloadNoticeView(grid,viewList,noticeId,totalPage);
				
				_LoadingHtml.hide();
				$(".opacityAll").hide();
				/*//加载集场号
				var $noticeViewNoTr = $("#noticeViewNoTr");
				var addDataArr = [];
				addDataArr.push(" <tr name='nitoceRightViewNoTr'><td></td></tr>");
				for(var c =0; c<firstRightNoArr.length; c++){
					var viewNo = firstRightNoArr[c];
					addDataArr.push(" <tr name='nitoceRightViewNoTr'><td>" + viewNo + "</td></tr>");
				}
				//先清空在添加
				var trs = $("tr[name='nitoceRightViewNoTr']");
				for(var d=0; d<trs.length; d++){
					trs[d].remove();
				}
				addDataArr.push(" <tr name='nitoceRightViewNoTr'><td><span class='add-view-span'>添加场景至通告单</span></td></tr>");
				$noticeViewNoTr.after(addDataArr.join(""));*/
				
				//取出请假信息
				var leaveInfo = response.actorLeaveInfo;
				if (leaveInfo != '' && leaveInfo != null && leaveInfo != 'undefined') {
					showInfoMessage(leaveInfo);
					//showErrorMessage(leaveInfo);
				}
				
				//重置数据列表滚动条、
				//计算出滚动条的距离
				var topDistance = divHeigth - 100;
				$("#ca").css("height", 'calc(100% - 21px)');
				//对隐藏的文本赋值
				if (viewList != null && viewList.length == 1) {
					var map = viewList[0];
					var viewId = map['viewId'];
					if (viewId == null || viewId =='' || viewId == undefined) {
						$("#totalView").val(0);
						$("#totalPage").val(0);
					}else {
						$("#totalView").val(viewList.length);
						$("#totalPage").val(totalPage);
					}
				}else {
					$("#totalView").val(viewList.length);
					$("#totalPage").val(totalPage);
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//加载高级查询结果
function loadSearchResult(){
	//气氛
	var atmosphere =$("#airAtmoSelect").val();
	//内外
	var site =$("#siteSelect").val();
	//主场景
	var major =$("#firstScenceSelect").val();
	//次场景
	var minor =$("#secondScenceSelect").val();
	//内容
	var mainContent = $("#mainContent").val();
	//备注
	var remark = $("#ReamrkContent").val();
	//服装
	var clothes =$("#clothSelect").val();
	//化妆
	var makeup =$("#makeUpSelect").val();
	//道具
	var props = $("#propSelect").val(); 
	//主要演员
	var roles = $("#mainActorSelect").val(); 
	//特约演员
	var guestRole = $("#specialActorSelect").val(); 
	//群众演员
	var massRole = $("#publicActorSelect").val(); 
	//拍摄地点
	var shootLocation =$("#shootLocationSelect").val();
	//集场号
	var seriesViewNos = $("#seriesViewNos").val();
	//场号
    var viewNos = $("#viewNos").val();
	
	//气氛
	if(atmosphere!= null && atmosphere!=""){
		var atmosphereStr = "";
		
		for(var i=0;i<atmosphere.length;i++){
			atmosphereStr+=atmosphere[i]+",";
		}
		atmosphereStr=atmosphereStr.substring(0,atmosphereStr.length-1);
		filter.atmosphere=atmosphereStr;
	}else{
		filter.atmosphere="";
	}
	
	//内外景
	if(site!= null && site!=""){
		var siteStr = "";
		
		for(var i=0;i<site.length;i++){
			if (site[i] != '0') {
				siteStr+=site[i]+",";
			}
		}
		siteStr=siteStr.substring(0,siteStr.length-1);
		filter.site=siteStr;
	}else{
		filter.site="";
	}
	
	//主场景
	if(major!= null && major!=""){
		var majorStr = "";
		
		for(var i=0;i<major.length;i++){
			majorStr+=major[i]+",";
		}
		majorStr=majorStr.substring(0,majorStr.length-1);
		filter.major=majorStr;
	}else{
		filter.major="";
	}
	
	//次场景
	if(minor!= null && minor!=""){
		var minorStr = "";
		
		for(var i=0;i<minor.length;i++){
			minorStr+=minor[i]+",";
		}
		minorStr=minorStr.substring(0,minorStr.length-1);
		filter.minor=minorStr;
	}else{
		filter.minor="";
	}
	
	//主要内容
	if (mainContent != "" && mainContent != null) {
	 filter.mainContent = mainContent;
	} else {
	 filter.mainContent = "";
	}
	
	//备注
	if (remark != "" && remark != null) {
     filter.remark = remark;
    } else {
     filter.remark = "";
    }
	
	//服装
	if(clothes!= null && clothes!=""){
		var clothe = "";
		
		for(var i=0;i<clothes.length;i++){
			clothe+=clothes[i]+",";
		}
		clothe=clothe.substring(0,clothe.length-1);
		filter.clothes=clothe;
	}else{
		filter.clothes="";
	}
	
	//化妆
	if(makeup!= null && makeup!=""){
		var makeupStr = "";
		
		for(var i=0;i<makeup.length;i++){
			makeupStr+=makeup[i]+",";
		}
		makeupStr=makeupStr.substring(0,makeupStr.length-1);
		filter.makeup=makeupStr;
	}else{
		filter.makeup="";
	}
	
	//主演
	if(roles!= null && roles!=""){
		var role = "";
		
		for(var i=0;i<roles.length;i++){
			role+=roles[i]+",";
		}
		role=role.substring(0,role.length-1);
		filter.roles=role;
	}else{
		filter.roles="";
	}
	
	//道具
	if(props!= null && props!=""){
		var prop = "";
		
		for(var i=0;i<props.length;i++){
			prop+=props[i]+",";
		}
		prop=prop.substring(0,prop.length-1);
		filter.props=prop;
	}else{
		filter.props="";
	}
	
	//特约演员
	if(guestRole!= null && guestRole!=""){
		var guest = "";
		
		for(var i=0;i<guestRole.length;i++){
			guest+=guestRole[i]+",";
		}
		guest=guest.substring(0,guest.length-1);
		filter.guest=guest;
	}else{
		filter.guest="";
	}
	
	//群众演员
	if(massRole!= null && massRole!=""){
		var mass = "";
		
		for(var i=0;i<massRole.length;i++){
			mass+=massRole[i]+",";
		}
		mass=mass.substring(0,mass.length-1);
		filter.mass=mass;
	}else{
		filter.mass="";
	}
	
	//拍摄地点
	if(shootLocation!= null && shootLocation!=""){
		var shootLocationStr = "";
		
		for(var i = 0; i< shootLocation.length;i++){
			shootLocationStr += shootLocation[i]+",";
		}
		shootLocationStr=shootLocationStr.substring(0,shootLocationStr.length-1);
		filter.shootLocation=shootLocationStr;
	}else{
		filter.shootLocation="";
	}
	
	//开始集场号
	if($("#startSeriesNo").val()!=""){
		filter.startSeriesNo=$("#startSeriesNo").val();
	}else{
        filter.startSeriesNo="";
    }
	if (isNaN($("#startSeriesNo").val())) {
		showErrorMessage("集次号只能填写数字！");
		return;
	}
	
	if($("#startViewNo").val()!=""){
		filter.startViewNo=$("#startViewNo").val();
	}else{
		filter.startViewNo="";
	}
	
	if($("#endSeriesNo").val()!=""){
		filter.endSeriesNo=$("#endSeriesNo").val();
	}else{
		filter.endSeriesNo="";
	}
	if (isNaN($("#endSeriesNo").val())) {
		showErrorMessage("集次号只能填写数字！");
		return;
	}
	
	if($("#endViewNo").val()!=""){
		filter.endViewNo=$("#endViewNo").val();
	}else{
		filter.endViewNo="";
	}
	
	if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
		showErrorMessage("请填写集数");
		return;
	}
	if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
		showErrorMessage("请填写集数");
		return;
	}
	
	filter.seriesNo="";
	filter.viewNo="";
	
	
	filter.seriesViewNos = "";
    //集场编号（非电影剧本时使用）
    if (seriesViewNos != null && seriesViewNos != "") {
        var seriesViewNoArr = seriesViewNos.split(/，|；|,|;/);
        for (var i = 0; i < seriesViewNoArr.length; i++) {
            if (seriesViewNoArr[i] != null && seriesViewNoArr[i] != "" && !/^\d+( )*(-|－|——)( )*.+/.test(seriesViewNoArr[i])) {
            	showErrorMessage("《" + seriesViewNoArr[i] + "》场集场编号不符合规范，请重新输入");
                return;
            }
            
            var singleSeriesViewNoArr = seriesViewNoArr[i].split(/-|－|——/);
            var seriesNo = singleSeriesViewNoArr[0];
            if (isNaN(seriesNo)) {
            	showErrorMessage("《" + seriesViewNoArr[i] + "》场集号只能输入数字，请重新输入");
                return;
            }
        }
    
        filter.seriesViewNos = seriesViewNos;
    }
    
    //场次编号（电影剧本时使用）
    if (viewNos != null && viewNos != "") {
        var viewNoArr = viewNos.split(/，|；|,|;/);
        
        var dealedViewNos = "";
        for (var i = 0; i < viewNoArr.length; i++) {
            var seriesViewNo = "1-" + viewNoArr[i];
            if (i == 0) {
                dealedViewNos = seriesViewNo;
            } else {
                dealedViewNos += "," + seriesViewNo;
            }
        }
    
        filter.seriesViewNos = dealedViewNos;
    }
	
	filter.pageFlag=true;
	filter.fromAdvance = true;
}

//顺场查询
function queryShunView (){
	filter.sortType=1;
	//顺场
	$("#smoothView").css("background","#76A3CB");
	$("#smoothView").css("color","white");
	//分场
	$("#groupView").css("background","white");
	$("#groupView").css("color","");
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
}

//分场查询
function queryFenView(){
	 
	filter.sortType=2;
	//分场
	$("#groupView").css("background","#76A3CB");
	$("#groupView").css("color","white");
	//顺场
	$("#smoothView").css("background","#FFFFFF");
	$("#smoothView").css("color","#333333");
	//获取选中的条件
	loadSearchResult();
	//重新加载数据
	initTablePage();
}

//清空集场面板
function clearViewNo(){
	if (crewType == 0 || crewType == 3) { //电影
		$("#startViewNo").val("");
		$("#endViewNo").val("");
		$("#viewNos").val("");
		$("#isAll").prop("checked", false);
	}else {
		$("#startSeriesNo").val("");
		$("#startViewNo").val("");
		$("#endSeriesNo").val("");
		$("#endViewNo").val("");
		$("#seriesViewNos").val("");
		$("#isAll").prop("checked", false);
	}
}

//清空内容筛选框
function clearContent(){
	$("#mainContent").val("");
}

//清空备注筛选框
function clearRemark(){
	$("#ReamrkContent").val("");
}

//返回通告单列表界面
function backToNoticeList(){
	var noticeId = $("#viewNoticeId").val();
	$("#createNotice").hide();
	$(".btn-list").hide();
	//要load的页面
	$("#loadNoticeContent").empty();
	//$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+noticeId);
	parent.$("#loadNoticeContent").hide();
	parent.$("#noticeContentIframe").show();
	parent.$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+noticeId);
	parent.$("li.click").removeClass("click");
	parent.$(".nav-ul li:nth-child(4)").addClass("click");
	parent.$("#stepPage").val('3');
	
	/*//判断要跳转的页面
	var source = $("#source").val();
	var noticeId = $("#noticeId").val();
	if (source == '1') { //通告单修改页面跳转
		window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=generateNotice";
	}else if (source == '2') {
		window.location.href="/notice/toNoticeList";
	}*/
}

//返回通告单命名页面
function setNoticeName(){
	var noticeId = $("#viewNoticeId").val();
	parent.window.location.href="/notice/toNoticeList?noticeId="+noticeId +"&source=createNoticePage&stepPage=1";
	//$("#createNotice").show();
		//要load的页面
	//$("#loadNoticeContent").empty();
	//$("#loadNoticeContent").load("/notice/toNoticeList?noticeId="+noticeId +"&source=generateNotice&stepPage=1");
	//改变导航条
	$("li.click").removeClass("click");
	$(".nav-ul li:nth-child(2)").addClass("click");
	$(".btn-list").show();
}	

//消息框
function showInfoMessage(message){
	swal({
        title: "",   
        text: "<p style='padding:5px;'>"+ message +"</p>", 
        html: true,
        showConfirmButton: true,
        });
}
//成功提示框
function showSuccessMessage(message){
	swal({
		title: '',
        text: "<p style='padding:5px;'>"+ message +"</p>",
        timer: 800,   
        showConfirmButton: false,
        type:"success",
        html: true
      });
}
//错误提示框
function showErrorMessage(message){
	swal({
        title: "",   
        text: "<p style='padding:5px;'>"+ message +"</p>", 
        html: true,
        type: "error",
        showConfirmButton: true,
        });
}

/**
 * 弹出提示框
 * @param title 标题
 * @param content 内容
 */
function popupPromptBox(title,content,obj){
	eventWindow.jqxWindow('open');
	if(title!=undefined || title!=null)
		$('#eventWindow').jqxWindow('setTitle', title);
	if(content!=undefined || content!=null)
		$('#eventWindowContent').html(content);
	if(content.length > 15)
		$('#eventWindowContent').css("margin-top","13px");
	$('#eventWindow').unbind("close");
	$('#eventWindow').on('close', function (event) {
		if (event.args.dialogResult.OK) {
			$(obj);
        }
    });
}

function initPopupPromptBox(){
	if (eventWindow == null ) {
		eventWindow = $('#eventWindow').jqxWindow({
			maxHeight: 170, maxWidth: 280, minHeight: 30, minWidth: 250, height: 165, width: 270,modalZIndex: 20010,
			resizable: false, isModal: true, modalOpacity: 0.3,/*theme:theme,*/
			okButton: $('#ok'), cancelButton: $('#mainCloseBtn'),autoOpen: false,
			initContent: function () {
				$('#ok').jqxButton({/*theme:theme,*/ width: '65px',height:'25px' });
				$('#mainCloseBtn').jqxButton({/*theme:theme,*/ width: '65px',height:'25px' });
			}
		});
	}
}

//回调
function noticeDoubleCallBackFun(title,content,confirmButtonText, cancelButtonText, confirm, cancel){
	if (confirmButtonText == null) {
		confirmButtonText = "确定";
	}
	
	if (cancelButtonText == null) {
		cancelButtonText = "取消";
	}
	
	if (confirm == null) {
		confirm = swal.close;
	}
	
	if (cancel == null) {
		cancel = swal.close;
	}
	
	swal({
		title: title,
		text: content,
		type: "warning",
		showCancelButton: true,
		confirmButtonColor: "rgba(255,103,2,1)",
		confirmButtonText: confirmButtonText,
		cancelButtonText: cancelButtonText,
		closeOnConfirm: true,
		closeOnCancel: true
	},
	function(isConfirm){
		if(isConfirm){
			$(confirm);
		}else{
			$(cancel);
		}
	});
}

//设置备场
function setRemarkView(){
	var noticeId = $("#viewNoticeId").val();
	//取出场景号
	var rowindexes =$('#noticeViewTbody :checked');
	
	var viewIds = "";
	$.each(rowindexes,function(){
		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	
	if (viewIds == null || viewIds == undefined || viewIds == '') {
		showErrorMessage("请选择要设置的场次！");
		return;
	}
	
	//保存信息
	parent.popupPromptBox("提示","是否将当前选中的场景设置为备戏？", function (){
		$.ajax({
			url: '/notice/saveNoticeViewMapInfo',
			type: 'post',
			async: false,
			data:{noticeId: noticeId, viewIds:viewIds, prepareView:1},
			datatype: 'json',
			success: function(response){
				if (response.success) {
					parent.showSuccessMessage(response.message);
					setTimeout(function (){
		            	   reloadNoticeViewList();
		               }, 1000);
				}else {
					parent.showErrorMessage(response.message);
				}
			}
		});
	});
}

//保存输入的页数
function saveInputPage(own){
	var noticeId = $("#viewNoticeId").val();
	var _this = $(own);
	//取出输入的页数
	var pageNum = _this.val();
	//取出场景id
	var viewId = _this.attr("sval");
	
	$.ajax({
		url: '/notice/saveNoticeViewMapInfo',
		type: 'post',
		async: false,
		data:{noticeId: noticeId, viewIds:viewId, shootPage:pageNum},
		datatype: 'json',
		success: function(response){
			if (response.success) {
				var totalPage = "";
				//计算总页数
				$("#noticeViewGrid input[class='page-count-text']").each(function(){
					//取出每一行的
					var trPage = $(this).val();
					if (totalPage == '') {
						totalPage = trPage;
					}else {
						
						totalPage = floatAdd(parseFloat(trPage) , parseFloat(totalPage)).toFixed(1);
					}
				});
				var text = $("#viewCountDiv").text();
				//截取页数
				var viewCount = text.substring(text.indexOf("共")+1,text.indexOf("场"));
				$("#viewCountDiv").text("共"+ viewCount +"场/" + parseFloat(totalPage).toFixed(1) +"页");
				tempPage = totalPage;
				$(own).val(parseFloat(pageNum).toFixed(1));
			}else {
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//点击场次编号,查询场次的详细信息,当发现右侧场景信息框中有未保存信息时,提示
function showViewContent(own){
	//获取集场号
	var viewId = $(own).attr("sval");
	//初始化右侧窗口
	var _frame=$('#viewInfoFrame').contents();
	
    //是否修改
    var isChanged = _frame.find('input#isChanged').val();
    
    if (isChanged == 1) {
        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
        	showViewDetail(viewId);
        	 _frame.find('input#isChanged').val(0);
        });
        return;
    }
    grid = undefined;
    
    showViewDetail(viewId);
    
}

//显示场景的详细信息
function showViewDetail(viewId) {
	//隐藏备选场景（右侧模块）
	/*$("#breakSecentRigth").hide();
	$("#rightHeaderTop").hide();*/
	$("#right_main").show().animate({"right": "0%"}, 500);
	$("#viewInfoFrame").attr("src","/viewManager/toViewDetailInfo?viewId="+viewId+"&flag=view");
}

//展示选择下拉框
function showSelectPanel(data) {
	  Popup.show({
	      dataList: data.dataJson,
	      right: data.right,
	      arrowTop: data.arrowTop,
	      title: data.title,
	      multiselect: data.multiselect,
	      currentTarget: data.currentTarget,
	      top: 1
	  });
}

//保存场景详情的方法
function saveView(){
	var _frame=$('#viewInfoFrame').contents();
	var shootLocation = "";
	var shootRegin = "";
    
	shootLocation = _frame.find("#noFinishViewShootLocation").val();
	shootRegin = _frame.find("#shootReginValue").text();
	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
    if(shootLocation=="") {
    	if(shootRegin) {
    		_frame.find("#shootReginValue").text("");
    		shootRegin="";
    	}
    }	
	if(shootLocation == "" && shootRegin == ""){
		saveViewInfo();
		return;
	}
	$.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegin},
		datatype: 'json',
		success: function(response){
			if(response.success){
				saveViewInfo();
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){
					saveViewInfo();
				});
			}
		}
	});
}


//保存场景详情
function saveViewInfo(){
	if (Popup != undefined) {
		Popup.hide();
	}
	var arr = [];
    var _frame=$('#viewInfoFrame').contents();
    
	var viewId = _frame.find("input[name=viewId]").val();
	//集.
	var seriesNo = _frame.find('input.scene_set').val();
	//场
	var viewNo = _frame.find('input.scene_field').val();
	if (crewType == 0|| crewType == 3) { //电影或网大
		if (viewNo == null || viewNo == "" || viewNo == undefined) {
			parent.showErrorMessage("请填写场次信息");
			return;
		}
	}else {
		if (seriesNo == null || seriesNo == ""  || seriesNo == undefined  
				|| viewNo == null || viewNo == "" || viewNo == undefined) {
			parent.showErrorMessage("请填写集场信息");
			return;
		}
	}
	
	_frame.find('input#isChanged').val(0);
	
	//主要内容
	var mainContent = _frame.find("input.scene_content").val();
	if (mainContent.length >= 250) {
		parent.showErrorMessage("亲，主要内容太长，不能超过250个字哦");
		return;
	}
	
	//主场景
	var firstLocation = _frame.find("input.scene_first").val();
	//次场景
	var secondLocation = _frame.find("input.scene_second").val();
	//三级场景
	var thirdLocation = _frame.find("input.scene_third").val();
	if (firstLocation == "" || firstLocation == null) {
		//判断二级场景是否为空
		if (secondLocation == '' || secondLocation == null) {
			//判断三级场景是否为空
			if (thirdLocation != '' && thirdLocation != null) {
				parent.showErrorMessage("请先填写主场景和次场景，再填写三级场景");
				return false;
			}
		}else {
			parent.showErrorMessage("请先填写主场景，再填写次级场景");
			return false;
		}
	}else {
		//判断二级场景是否为空
		if (secondLocation == '' || secondLocation == null) {
			//判断三级场景是否为空
			if (thirdLocation != '' && thirdLocation != null) {
				parent.showErrorMessage("请先填写次场景，再填写三级场景");
				return false;
			}
		}
	}
	//拍摄地域:
	var shootRegin = _frame.find("#shootReginValue").text();
	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
	_frame.find('input.shoot_regin_info').val(shootRegin);
	
	//主要演员
	var majorName='';
	_frame.find('div.performer_first ul .tagInput').siblings('li').each(function(){
		var value = $(this).text();
		var subValue = value.substring(value.indexOf("(")+1,value.indexOf(")"));
		if( subValue != null && subValue.length>0){
			majorName= $.trim( $(this).text() ).substring(0,value.indexOf("("));
			majorName=majorName.replace(/\s*/g,'');
			arr.push( majorName+ '(' + subValue +')');
		}else{
			majorName=$.trim( $(this).text());
			majorName=majorName.replace(/\s*/g,'');
			arr.push(majorName);
		}
	});
	_frame.find('input.performer_first').val(arr);
	arr.length=0;
	
	//特约演员        
	_frame.find('div.performer_special ul .tagInput').siblings('li').each(function(){
		arr.push($(this).text());
	});
	_frame.find('input.performer_special').val(arr);
	arr.length=0;
	//群众演员
	var _cList=[];// 群众演员的名称
	var figurantName='';        
	_frame.find('div.performer_common ul .tagInput').siblings('li').each(function(){
		if( /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) ) != null){
			figurantName= $.trim( $(this).text() ).replace(/[\(（](\d*)[\)）]/g, '');
			figurantName=figurantName.replace(/\s*/g,'');
			arr.push( figurantName+ '_' + /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) )[1] );
		}else{
			figurantName=$.trim( $(this).text());
			figurantName=figurantName.replace(/\s*/g,'');
			arr.push(figurantName);
		}
		_cList.push(figurantName);
	});
	_frame.find('input.performer_common').val(arr);
	arr.length=0;
	
	// 主要演员
	var _fList = _frame.find('input.performer_first').val().replace(/\s*/g,'').split(','),
	// 特约演员
	_sList = _frame.find('input.performer_special').val().replace(/\s*/g,'').split(',');
	
	// 是否有重复
	var isRepeat = false;
	for(var i = 0; i < _fList.length; i++){
		// 主要演员
		var f = _fList[i];
		// 主要 与 特约 是否有重复
		if($.inArray(f, _sList) != -1 && f != ''){
			isRepeat = true;
			parent.showErrorMessage('保存失败！主要演员 与 特约演员 存在重复：' + f);    
			break;
		}
		
		// 主要 与 群众 是否有重复            
		if($.inArray(f, _cList) != -1 && f != ''){
			isRepeat = true;
			parent.showErrorMessage('保存失败！主要演员 与 群众演员 存在重复：' + f);    
			break;
		}
	}
	
	// 有重复 直接跳出
	if(isRepeat){
		return false;
	}
	//普通道具
	arr.length=0;
	_frame.find('div.tool_main ul .tagInput').siblings('li').each(function(){
		arr.push($(this).text());
	});
	_frame.find('input.tool_main').val(arr);
	
	//特殊道具
	arr.length=0;
	_frame.find('div.tool_special ul .tagInput').siblings('li').each(function(){
		arr.push($(this).text());
	});
	_frame.find('input.tool_special').val(arr);
	
	//服装
	arr.length=0;
	_frame.find('div.clothes_info ul .tagInput').siblings('li').each(function(){
		arr.push($(this).text());
	});
	_frame.find('input.clothes_info').val(arr);
	
	//化妆
	arr.length=0;
	_frame.find('div.makeup_info ul .tagInput').siblings('li').each(function(){
		arr.push($(this).text());
	});
	_frame.find('input.makeups_info').val(arr);
	
	//禁用按钮
	$("#saveViewInfo").prop("disabled","disabled");
	//拍摄状态
	var shootStatus = _frame.find('input#shootStatus').val();
	if (shootStatus ==  2 || shootStatus == 5) {
		parent.popupPromptBox("提示", "当前场景已经完成，是否要继续保存？", function() {
			publicSaveViewInfo(_frame);
		});
		//启用保存按钮
		$("#saveViewInfo").prop("disabled",false);
	}else if (shootStatus == 3) {
		parent.popupPromptBox("提示", "当前场景已删戏，是否要继续保存？", function() {
			publicSaveViewInfo(_frame);
		});
		//启用保存按钮
		$("#saveViewInfo").prop("disabled",false);
	}else{
		publicSaveViewInfo(_frame);
	}
}

//保存场景的公用方法
function publicSaveViewInfo(_frame) {
	var viewId = _frame.find("input[name=viewId]").val();
	$.ajax({
		url:'/viewManager/saveViewInfo',
		data:_frame.contents().find('form').serialize(),
		type:"post",
		success:function(response){
			if (response.success) {
				
				$("#viewInfoFrame").attr("src","/viewManager/toViewDetailInfo?viewId="+viewId );
				
				//刷新通告单场景表
				setTimeout(function (){
	            	   reloadNoticeViewList();
	               }, 1000);
				
				parent.showSuccessMessage(response.message);
			} else {
				parent.showErrorMessage(response.message);
			}
			//启用保存按钮
			$("#saveViewInfo").prop("disabled",false);
		},
		error: function() {
			parent.showErrorMessage("发送请求失败");
		}
	});
}

//关闭修改页面
function cancleViewIframe() {
	if (Popup != undefined) {
		Popup.hide();
	}
	//初始化右侧窗口
	var _frame=$('#viewInfoFrame').contents();
	
    //是否修改
    var isChanged = _frame.find('input#isChanged').val();
    
    if (isChanged == 1) {
        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
        	$("#right_main").hide();
        	$(".break-view-detail-right").css("right", "-20%");
        	_frame.find('input#isChanged').val(0);
        });
        return;
    }
	
	$("#right_main").hide();
	$(".break-view-detail-right").css("right", "-20%");
}