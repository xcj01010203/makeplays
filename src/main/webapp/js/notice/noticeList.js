var fedBackwindow;
var noticeFormWindow;
var crewName = null;
var crewType = null;
var addViewSourceArray=new Array();
var addViewList=new Array();
var searchGridArray = new Array();
var filter ={};
var continueAjaxRecords = true;

//是否结束加载的标识
var isFinishLoad = false;
//未销场的总页数
var totalUseNoticePageCount = 0;
//已销场的总页数
var totalCancleNoticePageCount = 0;
//月份总页数
var totalMonthCount = 0;
var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];
//要查询的gridObj
var gridObj;
//要查询的grid的source
var gridSource;

var filter={};
//加载页面数据
$(document).ready(function () {
	//获取已销场通告单的统计数据
	getcancleNoticeData();
	
	//判断要显示的页面
	initShowWindow();
	
	//获取当前剧组类型
	getCrewType();
	
	
	//加载通告单月份列表
	initCancleNoticeMonth(1, null);
	
	//加载未销场通告单
	loadUsedNoticeListData(1,'');
	
	//加载已销场通告单
	loadCanclesNoticeListData(1,'');
	
	//获取分组信息
	loadGroupListDta();
	
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 6
    });
	
	//判断只读权限，去掉新建、删除、销场、添加场景功能
	if(isNoticeReadonly) {
		//去掉新建按钮
		$(".add-notice-button").remove();
		$(".add-list-notice-button").remove();
	}
	
	/* $("#noticeListTab").click(function (event) {
		 showListWindow(); 
		 event.stopPropagation();
      });*/
});

//判断跳转过来时要显示的页面
function initShowWindow(){
	//取出展示窗口的值
	var showWindow = $("#showWindow").val();
	if (showWindow == 'list') {
		showListWindow();
	}
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
					showErrorMessage(response.message);
			}
		}
	});
}

//获取销场通告单的统计数据
function getcancleNoticeData(){
	//清空数据
	$("#summaryCancledSpan").html('');
	$.ajax({
		type: "POST",
	    url: "/notice/queryLastNoticeMonth",
	    data: {cancledNotice: 'cancledNotice'},
	    async: false,
	    success: function(data){
	    	var totalCancleCount = data.totalCancleCount;
	    	var cancledCount = data.cancledCount;
	    	//最新发布的通告单所在的月份
	    	var lastNoticeMonth = data.lastNoticeMonth;
	    	$("#lastNoticeMonth").val(lastNoticeMonth);
	    	
	    	//显示统计信息
	    	$("#summaryCancledSpan").append("(共 " + totalCancleCount + "天，" + cancledCount + " 张通告单)");
	   } 

	});
}

//查询月份列表
function initCancleNoticeMonth(pageNo, pageFlag){
	//清空数据
	$("#cancledMonthDiv").html('');
	$.ajax({
		type: "POST",
	    url: "/notice/queryMonthList",
	    data: {pagesize:9,pageNo: pageNo},
	    async: false,
	    success: function(data){
	    	if (data.success) {
	    		//通告单月份总数
	    		var totalCancleCount = data.totalCount;
	    		
	    		if (totalCancleCount == 0 || totalCancleCount == null || totalCancleCount == undefined) {
	    			totalCancleCount = 1;
				}
	    		if (pageNo == 1) {
					$("#preCancledMonth").css("background-image", "url(../../images/notice/month-left-unusable.png)");
					$("#preCancledMonth").removeClass("pre-month-useable");
				}else {
					$("#preCancledMonth").css("background-image", "url(../../images/notice/month-left.png)");
					$("#preCancledMonth").addClass("pre-month-useable");
				}
	    		
	    		if (pageNo == totalCancleCount) {
	    			$("#nextCancledMonth").css("background-image", "url(../../images/notice/month-right-unusable.png)");
					$("#nextCancledMonth").removeClass("next-month-useable");
				}else {
					$("#nextCancledMonth").css("background-image", "url(../../images/notice/month-right.png)");
					$("#nextCancledMonth").addClass("next-month-useable");
				}
	    		
	    		totalMonthCount = totalCancleCount;
	    		$("#noticeMonthInput").val(pageNo);
	    		//设置月份
	    		//取出通告单所在的所有的月
	    		var cancleNoticeMonthList = data.cancleNoticeMonthList;
	    		if (cancleNoticeMonthList != null && cancleNoticeMonthList.length != 0) {
	    			var $cancledMonthDiv = $("#cancledMonthDiv");
	    			var monthArr = [];
	    			for(var i=0; i<cancleNoticeMonthList.length; i++){
	    				var cancleNoticeMOnth = cancleNoticeMonthList[i];
	    				var lastMonth = $("#lastNoticeMonth").val();
	    				if (i == 0) {
	    					//取出默认的第一月
	    					if (lastMonth == cancleNoticeMOnth['noticeMonth']) {
	    						monthArr.push(" <div class='first-cancled-month-span curr-div' id='"+ cancleNoticeMOnth['noticeMonth'] +"' onclick='getClickMonthNotice(this)'>"+ cancleNoticeMOnth['noticeMonth'] +"</div>");
							}else {
								monthArr.push(" <div class='first-cancled-month-span' id='"+ cancleNoticeMOnth['noticeMonth'] +"' onclick='getClickMonthNotice(this)'>"+ cancleNoticeMOnth['noticeMonth'] +"</div>");
							}
	    				}else {
	    					if (lastMonth == cancleNoticeMOnth['noticeMonth']) {
	    						monthArr.push(" <div class='cancled-month-span curr-div' id='"+ cancleNoticeMOnth['noticeMonth'] +"' onclick='getClickMonthNotice(this)'>"+ cancleNoticeMOnth['noticeMonth'] +"</div>");
							}else {
								monthArr.push(" <div class='cancled-month-span' id='"+ cancleNoticeMOnth['noticeMonth'] +"' onclick='getClickMonthNotice(this)'>"+ cancleNoticeMOnth['noticeMonth'] +"</div>");
							}
	    				}
	    			}
	    			$cancledMonthDiv.append(monthArr.join(""));
	    		}
	    		
	    		/*if (pageFlag == 'left') {
	    			$("#cancledMonthDiv").animate({"left": '0px'}, 300).show();
	    		}else if (pageFlag == 'right') {
	    			$("#cancledMonthDiv").animate({"right": '0px'}, 300).show();
	    		}*/
	    		
			}
	   } 

	});
}

//加载未销场的通告单
function loadUsedNoticeListData(pageNo, flag){
	//每次添加之前清空列表
	$(".use-view-first-div").remove();
	$(".use-view-div").remove();
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
		type: "POST",
	    url: "/notice/loadNoticeList",
	    data: {pagesize:5,pageNo:pageNo,usedNotice: 'usedNotice'},
	    async: false,
	    success: function(data){
	    	var noticeList = data.result.resultList;
	    	totalUseNoticePageCount = data.result.pageCount;
	    	
	    	//当没有记录时，设置总页数为1
	    	if (totalUseNoticePageCount == 0) {
	    		totalUseNoticePageCount = 1;
			}
	    	
	    	//清空统计信息
	    	$("#summaryDataSpan").html('');
	    	//显示统计信息
	    	$("#summaryDataSpan").append("(" + data.result.total + " 张通告单)");
	    	//拼接列表数据
	    	stitchNoticeList(noticeList,flag);
	    	
	    	if (pageNo == 1) {
	    		$("#preUsedNotice").addClass("no-pre-data");
			}else {
				$("#preUsedNotice").removeClass("no-pre-data");
			}
	    	
	    	if (pageNo == totalUseNoticePageCount) {
	    		$("#nextUsedNotice").addClass("no-pre-data");
			}else {
				$("#nextUsedNotice").removeClass("no-pre-data");
			}
	    	
	    	//设置当前页
	    	$("#useCurrPageNo").val(pageNo);
	    	
	    	
	    	_LoadingHtml.hide();
			$(".opacityAll").hide();
	   }

	});
}

//加载已销场的通告单列表
function loadCanclesNoticeListData(cancledPageNo,flag){
	//每次添加之前清空列表
	$(".cancle-view-first-div").remove();
	$(".cancle-view-div").remove();
	//取出最新发布的通告单的日期
	var lastNoticeMonth = $("#lastNoticeMonth").val();
	//显示loading效果
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
		type: "POST",
	    url: "/notice/loadNoticeList",
	    data: {pagesize:6,pageNo:cancledPageNo,cancledNotice: 'cancledNotice', noticeDateMonth: lastNoticeMonth+"-01"},
	    async: true,
	    success: function(data){
	    	var noticeList = data.result.resultList;
	    	totalCancleNoticePageCount =  data.result.pageCount;
	    	
	    	if (totalCancleNoticePageCount == 0) {
	    		totalCancleNoticePageCount = 1;
			}
	    	//拼接列表数据
	    	stitchNoticeList(noticeList,flag);
	    	if (cancledPageNo == 1) {
	    		$("#preCancledNotice").addClass("no-pre-data");
			}else {
				$("#preCancledNotice").removeClass("no-pre-data");
			}
	    	
	    	if (cancledPageNo == totalCancleNoticePageCount) {
	    		$("#nextCancledNotice").addClass("no-pre-data");
			}else {
				$("#nextCancledNotice").removeClass("no-pre-data");
			}
	    	
	    	//设置当前页
	    	$("#cancledPageNo").val(cancledPageNo);
	    	_LoadingHtml.hide();
			$(".opacityAll").hide();
	   } 

	});
}

//拼接通告单表单界面
function stitchNoticeList(noticeList,flag){
	//生成echarts动态图需要的数据列表
	var echartsMap = [];
	
	//未销场列表
	var $useViewWindow = $("#usedMainDiv");
	var userArr = [];
	
	//已销场列表
	var $cancleViewWindow = $("#cancleNoticeMainDiv");
	var cancleArr = [];
	//遍历通告单列表
	for(var i = 0; i<noticeList.length; i++){
		var map = noticeList[i];
		//根据通告单状态拼接数据
		var status = map['canceledStatus'];
		//未销场
		if (status == '0') {
			//添加第一场
			if (userArr == null || userArr.length == 0) {
				userArr.push(" <div class='use-view-first-div'>");
			}else {
				userArr.push(" <div class='use-view-div'>");
			}
			userArr.push(" <div class='use-title-span'>" + map['noticeDate'] + "  " + map['groupName'] + "通告" + "</div>");
			//版本号判空
			if (map['version'] == null || map['version'] == '') {
				userArr.push(" <p class='title-p'><div class='blank-version-span'></div></p>");
			}else {
				userArr.push(" <p class='title-p'><span class='use-version-span'>" + map['version'] + "</span></p>");
			}
			
			//导演判空
			if (map['groupDirector'] == null || map['groupDirector'] == '') {
				userArr.push(" <p><span class='use-content-span'>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演： </span></p>");
			}else {
				userArr.push(" <p><span class='use-content-span'>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演：" + map['groupDirector'] + "</span></p>");
			}
			
			//拍摄地点判空
			if (map['shootLocation'] == null || map['shootLocation'] =='') {
				userArr.push(" <p><span class='use-content-span'>拍摄地点： </span></p>");
			}else {
				userArr.push(" <p><span class='use-content-span' title='" + map['shootLocation'] + "'>拍摄地点：" + map['shootLocation'] + "</span></p>");
			} 
			var sumPage = '0.0';
			if (map['sumPage'] != null && map['sumPage'] != "" && map['sumPage'] != undefined) {
				sumPage = map['sumPage']+'';
			}
			//通高量判空
			if (map['viewCount'] == null || map['sumPage'] == null) {
				userArr.push(" <p class='tonggao-p'><span class='use-content-span'>通&nbsp;&nbsp;告&nbsp;量：0场/0页</span></p>");
			}else {
				userArr.push(" <p class='tonggao-p'><span class='use-content-span'>通&nbsp;&nbsp;告&nbsp;量：" + map['viewCount'] + "场/" + parseFloat(sumPage).toFixed(1) + "页" + "</span></p>");
			}
			
			//主演判空
			if (map['mainrole'] == null || map['mainrole'] == '') {
				userArr.push(" <p><span class='use-content-span'>主要演员：<div class='role-name-div'> </div></span></p>");
			}else {
				userArr.push(" <p><span class='use-content-span'>主要演员：<div class='role-name-div' title='" + map['mainrole'] + "'>" + map['mainrole'] + "</div></span></p>");
				
			}
			
			//发布时间判空
			if (map['updateTime'] == null || map['updateTime'] == '') {
				userArr.push(" <span class='use-time-span'> </span>");
			}else {
				userArr.push(" <span class='use-time-span'>" + map['updateTime'] + "</span>");
			}
			userArr.push(" <table class='use-button-table'> <tr>");
			//只读权限，不能进行删除操作
			if(!isNoticeReadonly) {
				userArr.push(" <td class='button-td' onclick='printView(\"" + map['noticeId'] + "\")'>查看</td>");
				userArr.push(" <td class='button-td' onclick='fedBack(\"" + map['noticeId'] + "\")'>反馈</td>");
				userArr.push(" <td class='button-td' onclick='cancleNotice(\"" + map['noticeId'] + "\")'>销场</td>");
				userArr.push(" <td class='button-td' onclick='deleteNotice(\"" + map['noticeId'] + "\",\"" + map['finishCount'] + "\")'>删除</td>");
			} else {
				userArr.push(" <td class='button-td button-td-three' onclick='printView(\"" + map['noticeId'] + "\")'>查看</td>");
				userArr.push(" <td class='button-td button-td-three' onclick='fedBack(\"" + map['noticeId'] + "\")'>反馈</td>");
				userArr.push(" <td class='button-td button-td-three' onclick='cancleNotice(\"" + map['noticeId'] + "\")'>销场</td>");
			}
			userArr.push(" </tr></table></div>");
		}else if (status == '1') { //已销场
			//添加第一场
			if(cancleArr == null || cancleArr.length == 0){
				cancleArr.push(" <div class='cancle-view-first-div'>");
			}else {
				cancleArr.push(" <div class='cancle-view-div'>");
			}
			cancleArr.push(" <div class='cancle-title-div'><span class='cancle-title-span'>" +map['noticeDate'] + "    " + map['groupName'] + "</span></div>");
			
			//导演判空
			if (map['groupDirector'] == null || map['groupDirector'] == '') {
				cancleArr.push(" <p><span class='cancle-content-span'>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演： </span></p>");
			}else {
				cancleArr.push(" <p><span class='cancle-content-span' title='"+ map['groupDirector'] +"'>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演：" + map['groupDirector'] + "</span></p>");
			}
			
			//通高量判空
			/*if (map['viewCount'] == null || map['sumPage'] == null) {
				cancleArr.push(" <p><span class='use-content-span'>通&nbsp;&nbsp;告&nbsp;量：0场/0页</span></p>");
			}else {
				cancleArr.push(" <p><span class='use-content-span'>通&nbsp;&nbsp;告&nbsp;量：" + map['viewCount'] + "场/" + subStringPage(map['sumPage']) + "页" + "</span></p>");
			}*/
			
			
			//拍摄地点判空
			if (map['shootLocation'] == null || map['shootLocation'] =='') {
				cancleArr.push(" <p><span class='cancle-location-span'>拍摄地点： </span></p>");
			}else {
				cancleArr.push(" <p><span class='cancle-location-span' title='" + map['shootLocation'] + "'>拍摄地点：" + map['shootLocation'] + "</span></p>");
			}
			
			//主演判空
			if (map['mainrole'] == null || map['mainrole'] == '') {
				cancleArr.push(" <p><span class='use-content-span'>主要演员：<div class='role-name-div'> </div></span></p>");
			}else {
				cancleArr.push(" <p><span class='use-content-span'>主要演员：<div class='cancle-role-name-div' title='" + map['mainrole'] + "'>" + map['mainrole'] + "</div></span></p>");
			}
			
			//总场数
			var totalViewCount = map['viewCount'];
			if (totalViewCount == 0 || totalViewCount == null) {
				totalViewCount = 1;
			}
			//已拍摄场数
			var finishViewCount = map['finishCount'];
			if (finishViewCount == null ) {
				finishViewCount = 0;
			}
			//总页数
			var sumPage = '0.0';
			if (map['sumPage'] != null && map['sumPage']!= undefined && map['sumPage'] != '') {
				sumPage = map['sumPage']+'';
			}
			cancleArr.push(" <p class='cancle-count'>"+ map['viewCount'] + "场/" + parseFloat(sumPage).toFixed(1) + "页" +"</p>");
			cancleArr.push(" <div class='cancle-process process-div'>");
			cancleArr.push(" <div class='process-div finish-process-div' id='"+ map['noticeId'] +"'>"+ subTwoStringPage((finishViewCount/totalViewCount)*100) + "%" +"</div></div>");
			
			
			/*cancleArr.push(" <div class='cancle-process-div' id='"+ map['noticeId'] +"'> </div>");*/
			cancleArr.push(" <table class='cancle-button-table'> <tr>");
			cancleArr.push(" <td class='cancle-button-td' onclick='printView(\"" + map['noticeId'] + "\")'>查看</td>");
			cancleArr.push(" <td class='cancle-button-td' onclick='fedBack(\"" + map['noticeId'] + "\")'>反馈</td>");
			cancleArr.push(" <td class='cancle-button-td' onclick='cancleNotice(\"" + map['noticeId'] + "\")'>销场</td>");
			cancleArr.push(" </tr></table></div>");
			echartsMap.push(map);
		}
	}
	//添加未销场的数据
	$useViewWindow.append(userArr.join(""));
	
	/*在火狐浏览器中使演员超出部分正常显示样式*/
	if (navigator.userAgent.indexOf('Firefox') >= 0){//判断是否是火狐浏览器
		var role_name_div = $useViewWindow.find("div.role-name-div");//未销场主要演员列表
		$.each(role_name_div, function(){
			if($(this).text().length > 12){
				$(this).addClass("change");
			}
		});
		
		var cancle_role_name = $useViewWindow.find("div.cancle-role-name-div");//已销场主要演员列表
		$.each(cancle_role_name, function(){
			if($(this).text().length > 12){
				$(this).addClass("change");
			}
		});
	}
	
	
	
	if (flag == 'usedRight') {
		$("#usedMainDiv").animate({"right": '0px'}, 300).show();
	}else if (flag == 'usedLeft') {
		$("#usedMainDiv").animate({"left": '0px'}, 300).show();
	}else if (flag == 'cancledLeft') {
		$("#cancleNoticeMainDiv").animate({"left": '0px'}, 300).show();
	}else if (flag == 'cancledRight') {
		$("#cancleNoticeMainDiv").animate({"right": '0px'}, 300).show();
	}
	//添加已销场的数据
	$cancleViewWindow.append(cancleArr.join(""));
	
	//计算进度条宽度
	for(var a=0; a<echartsMap.length; a++){
		var cancleMap = echartsMap[a];
		//总场数
		var totalViewCount = cancleMap['viewCount'];
		if (totalViewCount == 0 || totalViewCount == null) {
			totalViewCount = 1;
		}
		//已拍摄场数
		var finishViewCount = cancleMap['finishCount'];
		if (finishViewCount == null ) {
			finishViewCount = 0;
		}
		
		$("#" + cancleMap['noticeId']).width(subTwoStringPage((finishViewCount/totalViewCount)*100) + "%");
	}
	
	//生成echarts动态图
	//genEcharts(echartsMap);
}

//点击月份获取当前月的通告单
function getClickMonthNotice(own){
	var $this = $(own);
	//取出点击的月份
	var noticeMonth = $this.attr("id");
	$("#lastNoticeMonth").val(noticeMonth);
	
	//改变样式
	$("#cancledMonthDiv div[class*='curr-div']").removeClass('curr-div');
	$this.addClass('curr-div');
	
	loadCanclesNoticeListData(1);
}

//获取分组信息
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
				loadNoticeFormWindow(groupList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//新建通告单
function addNewNotice(from){
	var isHaveView = null;
	
	//先查询当前剧组是否有场景
	$.ajax({
		url: '/notice/queryViewListByCrewId',
		type: 'post',
		async: false,
		data: {pagesize: 1,pageNo: 1},
		datatype: 'json',
		success: function(response){
			if(response.success){
				isHaveView = response.isHaveView;
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
	if (!isHaveView) {
		showErrorMessage("当前剧组还未上传剧本，请上传剧本后再新建通告单！");
	}else {
		//跳转到新建通告单页面
		window.location.href="/notice/toNoticeList?source=createNoticePage&stepPage=1&window="+from;
	}
}

//加载通告单列表视图数据
function initNoticeListWindow(totalNoticeCount, loadCount, pageNo, isLoad){
	if (isLoad && continueAjaxRecords) {
		$.ajax({
			url: '/notice/loadNoticeList',
			type: 'post',
			async: true,
			data: {pagesize: 3,pageNo: pageNo,shootLocationStr: filter.shootLocationStr,
				sceriesViewNo:filter.sceriesViewNo,viewTape:filter.viewTape,reamrkStr:filter.reamrkStr, isFromListTable:true},
			datatype: 'json',
			success: function(response){
				if(response.success){
					var noticeDataList = response.result.resultList;
					//初始化添加到通告单窗口
					loadListData(noticeDataList);
					totalNoticeCount = response.result.total;
					loadCount = loadCount + 3;
					pageNo = pageNo+1;
					if (loadCount < totalNoticeCount) { //加载未完成
						initNoticeListWindow(0,loadCount,pageNo,true);
					}else { //加载完成
						isLoad = false;
						initNoticeListWindow(0,loadCount,pageNo);
						isFinishLoad = true;
						//启用点击按钮
						$("#noticeListTab").prop( "disabled", false);
					}
					
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}
}


//拼接列表数据
function loadListData(noticeDataList){
	var noticeStrArr = [];
	var $table = $("#noticeListTable");
	var html = [];
	var noticeIdArr = [];
	for(var i =0 ; i<noticeDataList.length; i++){
		var dataMap = noticeDataList[i];
		html.push(" <tr><td style='height:10px;'></td></tr>");
		html.push(" <tr><td colspan='11'><div id='jqxgrid"+ dataMap['noticeId'] +"' class='grid-data-div'></div></td></tr>");
		noticeIdArr.push(dataMap['noticeId']);
		html.push(" <tr style='border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;background-color: rgba(170, 170, 170, 0.75);color:white;'>");
		var shootDays = dataMap['shootDays'];
		var noticeDateArr = dataMap['noticeDate'].split("-");
		//分组
		var groupName = dataMap['groupName'];
		var viewCount = dataMap['viewCount'];
		
		//拼接字符串
		var noticeStr = "第  "+ shootDays + " 天 -" + noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日" + groupName;
		
		noticeStrArr.push(noticeStr);
		
	   	if (viewCount == null) {
	   		viewCount = 0;
		}
	   	
		html.push(" <td style='background-color:white !important;width: 171px;'></td>");
		html.push(" <td></td>");
		
		//气氛内外
		/*var site = dataMap['site'];
	   	var atmosphereName = dataMap['atmosphereName'];
	   	if (site !=null && site != "") {
			if (atmosphereName != null && atmosphereName != "") {
				 html.push(" <td>"+ atmosphereName + "/"+ site +"</td>");
			}else {
				html.push(" <td>"+ site +"</td>");
			}
		}else {
			if (atmosphereName != null && atmosphereName != "") {
				html.push(" <td>"+ atmosphereName +"</td>");
			}else {
				html.push(" <td></td>");
			}
		}*/
	   	html.push(" <td><p style='width: 10px;'></p></td>");
	   	//场数
	   	var finishCount = dataMap['finishCount'];
	   	if (finishCount == null) {
	   		finishCount = 0;
		}
	   
	   	html.push(" <td>共 "+ viewCount + "场  完成" + finishCount +" 场</td>");
	   	
	   	//页数
	   	var finishPage = dataMap['finishPage'];
	   	if (finishPage == null) {
	   		finishPage = 0;
		}
	   	var sumPage = dataMap['sumPage'];
	   	if (sumPage == null) {
			sumPage = 0;
		}
	   	html.push(" <td>共  " + parseFloat(sumPage).toFixed(1) + " 页  完成 " + parseFloat(finishPage).toFixed(1) +" 页</td>");
	   	
		//拍摄地点
		var shootLocation = dataMap['shootLocation'];
		if (shootLocation == null || shootLocation =='') {
			html.push(" <td><p style='width:330px;'>拍摄地点：</p></td>");
		}else{
			html.push(" <td><p title='"+ shootLocation +"' style='width:330px;'>拍摄地点：" + shootLocation + "</p></td>");
		}
		
	   	//主要特约演员
	   	var mainrole = dataMap['mainrole'];
	  	var guestrole = dataMap['guestrole'];
	  	if (mainrole !=null && mainrole != "") {
			if (guestrole != null && guestrole != "") {
				html.push(" <td><p style='width: 400px;' title='"+ mainrole + " | "+ guestrole +"'>主演/特约："+ mainrole + " | "+ guestrole +"</p></td>");
			}else {
				html.push(" <td><p style='width: 400px;' title='"+ mainrole +"'>主演："+ mainrole +"</p></td>");
			}
		}else {
			if (guestrole != null && guestrole != "") {
				html.push(" <td><p style='width: 400px;' title='"+ guestrole +"'>特约："+ guestrole +"</p></td>");
			}else {
				html.push(" <td><p style='width: 400px;' title=''>主演/特约：</p></td>");
			}
		}
	  	
	  	//操作按钮框
	  	html.push(" <td style='border-right: 1px solid #ddd;'><div style='width: 150px;'>");
	  	//查看按钮
	  	html.push(" <input type='button' class='noticeViewToolbar modify' id='modifyNoticeBtn' onclick='printView(\"" + dataMap['noticeId'] + "\",\"list\")' title='查看通告单'>");
	  	//反馈按钮
	  	html.push(" <input type='button' class='noticeViewToolbar fedback' id='noticeFedBack' onclick='fedBack(\"" + dataMap['noticeId'] + "\")' title='查看反馈信息'>");
	  	//销场按钮
	  	html.push(" <input type='button' class='noticeViewToolbar cancelView' id='setShootStatusButton' onclick='cancleNotice(\"" + dataMap['noticeId'] + "\",\"list\")' title='销场'>");
		//只读权限，不能进行删除操作
		if(!isNoticeReadonly) {
		  	//删除按钮
		  	html.push(" <input type='button' class='noticeViewToolbar delete' id='deleteNoticeBtn' onclick='deleteListNotice(\"" + dataMap['noticeId'] + "\",\"" + dataMap['finishCount'] + "\")' title='删除通告单'>");
		}
	  	html.push(" </div></td></tr>");
	}
	
	$table.append(html.join(""));
	//展开子表
	for(var a=0; a<noticeIdArr.length; a++){
		var noticeIds = noticeIdArr[a];
		//请求当前通告单的场景列表
		$.ajax({
			url:"/notice/loadNoticeView",
			type:"post",
			dataType:"json",
			data:{noticeId:noticeIds, isNoticeListView:false},
			async: false,
			success:function(data){
				var viewlist = data.viewList;
				var grid = $("#jqxgrid"+noticeIdArr[a]);
				loadNoticeViewTable(noticeStrArr[a], grid, viewlist,a, noticeIds);
			}
		});	
	}
}

//截取页数只显示小数点后两位 数
function subStringPage(PageCount){
	var pageCountStr = PageCount+"";
	var subStr = pageCountStr.substring(0,pageCountStr.indexOf(".")+3);
	return subStr;
}

//加载新增修改通告单表单
function loadNoticeFormWindow(groupList) {
   if (noticeFormWindow != null && noticeFormWindow != undefined) {
       $('#noticeFormWindow').jqxWindow("open");
       return;
   }
   
   noticeFormWindow = $('#noticeFormWindow').jqxWindow({
        theme:theme,
        autoOpen:false,
        zIndex:20000,
        resizable: false,
        height: 380, 
        width: 400,
        isModal: true,
        title: '通告单',
        cancelButton: "#noticeCancelButton",
        initContent: function () {
            
        }
    });
    
    $('#noticeFormWindow').on("close", function() {
        $("#noticeDateDiv").val("");
    });
    
    var groupSource = [];
    for(var key in groupList){
    	groupSource.push({text: groupList[key], value: key});
    }
    groupSource.push({text:'新增组',value:'99'});
    
    $("#noticeNameInput").jqxInput({theme: theme});
    $("#noticeDateDiv").jqxInput({
        theme: theme,
        width: '300px', 
        height: '30px'
    });
    
    $("#noticeCancelButton, #saveNoticeButton").jqxButton({
        theme:theme, 
        width: 80, 
        height: 25
    });
    
    $("#noticeGroupDiv").jqxDropDownList({
        theme:theme,
        selectedIndex:0,
        source: groupSource, 
        displayMember: "text", 
        valueMember: "value", 
        width: '300px', 
        height: 30,
        dropDownHeight: getHeight(groupSource)
    });
    
    $("#noticeGroupDiv").on('change',function(event){
        var args = event.args;
        if (args) {
            var index = args.index;
            var item = args.item;
            
            if(item.value=="99"){
                
                if(index>25){
                    showErrorMessage("目前最多选择到Z组");
                    $("#noticeGroupDiv").jqxDropDownList("selectIndex",index-1);
                    return;
                }
                $.ajax({
                    url:"/shootGroupManager/saveGroup",
                    type:"post",
                    dataType:"json",
                    data:{groupName:groupArray[index-1].text},
                    success:function(data){
                        if(data.success){
                            showSuccessMessage(data.message);
                            $("#noticeGroupDiv").jqxDropDownList("selectIndex",0);
                        }
                        $("#noticeGroupDiv").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                        $("#noticeGroupDiv").jqxDropDownList("selectIndex",index);
                        
                        autoGetNoticeName();
                    }
                });
            }
            autoGetNoticeName();
        }
    });
    
    $('#noticeDivForm').jqxValidator({
        animationDuration: 1,
        rules: [{input: '#noticeNameInput', message: '通告单名称不可为空!', action: 'keyup,blur', rule: 'required' }]
    });
    
    $("#saveNoticeButton").on("click", function () {
        $("#groupIdValue").val($("#noticeGroupDiv").val());
        if ($('#noticeDivForm').jqxValidator('validate')) {
            $.ajax({
                url: "/notice/noticeSaveWinoutView",
                type: 'post',
                data: $('#noticeDivForm').serialize(),
                dataType: 'json',
                async: false,
                success: function (param) {
                    if (param.success) {
                        showSuccessMessage("操作成功");
                        
                        var noticeId = param.noticeId;
                        $('#noticeFormWindow').jqxWindow("close");
                        setTimeout(function (){
                        	window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=generateNotice";
                        }, 1000);
                        
                    } else {
                        $("#addNoticeError").html("<div>错误提示：</div><div>" + param.message + "</div>");
                    }
                }
            });
        }
    });
    
}

//自动生成通告单名称
function autoGetNoticeName() {
    var selectGroupItem = $("#noticeGroupDiv").jqxDropDownList('getSelectedItem');
    var groupName = selectGroupItem.label;
    var noticeDateStr = $("#noticeDateDiv").val();
    
    //修改通告单时，设置分组信息会触发到该方法，从而产生通告单名称不对的问题
    //解决方案：关闭新增/修改通告单窗口时，把通告单日期设置为空。修改通告单时，先为分组赋值，此时通告单日期为空，修改通告单名称文本框值的逻辑将不再执行
    if (noticeDateStr == undefined || noticeDateStr == "") {
        return false;
    }
    var noticeDateArr = noticeDateStr.split("-");
    var noticeDate = noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日";
    
    $("#noticeNameInput").val("《" + crewName + "》 " + noticeDate + groupName+"通告");
    
    $("#addNoticeError").html("");
}

//截取页数只显示2位数
function subTwoStringPage(PageCount){
	var pageCountStr = PageCount+"";
	var subStr = pageCountStr.substring(0,4);
	return subStr;
}

//通告单 新建/修改 /预览 界面
function printView(noticeId,from){
	window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=createNoticePage&stepPage=3&window="+from;
}

//通告单反馈页面（仿照原来的功能界面）
function fedBack (noticeId){

    if (fedBackwindow == null || fedBackwindow == undefined) {
        fedBackwindow = $("#noticeFedbackWindow").jqxWindow({
            theme:theme,  
            width: 855,
            height: 520, 
            maxWidth: 2000,
            autoOpen: false,
            isModal: true,
            resizable: false,
            cancelButton: $('#fedbackWinClose'),
            initContent: function() {
                //按钮
                $('#fedbackWinClose').jqxButton({
                    theme:theme,
                    height:28,
                    width:80
                });
            }
        });
    }
    //数据主表
    fedbackTable = new loadFedbackTable("fedbackGridDiv", noticeId);
    fedbackTable.loadTable();
    $("#noticeFedbackWindow").jqxWindow("open");
    
    
    var int = setInterval(function() {
        fedbackTable.refreshGrid();
        
        var totalCount = fedbackTable.getTotalCount();
        var hasFinishLookCount = fedbackTable.getFinishLookCount();
        if (totalCount == hasFinishLookCount) {
            window.clearInterval(int);
        }
    }, 5000);
    
    $("#noticeFedbackWindow").on("close", function() {
        window.clearInterval(int);
    });
	
}

//通告单销场页面(跳转到场景列表界面)
function cancleNotice(noticeId,from){
	 //window.location.href="/notice/toNoticeList?noticeId="+noticeId+"&source=2";
	 window.location.href="/notice/toNoticeList?noticeId="+noticeId + "&source=createNoticePage&stepPage=2&window="+from;
}

//通告单删除界面
function deleteNotice(noticeId, finishCount){
	//只读权限，不能进行删除操作
	if(isNoticeReadonly) {
		return false;
	}
	
	if (finishCount != "null" && finishCount != '') {
		showErrorMessage("当前通告单中有已完成的场景不能删除！");
		return false;
	}
	
	var message = "";
	var hasCutInfo = null;
	//判断其它状态（删戏，部分完成，也不能删戏；只有甩戏才能删除）
	$.ajax({
		url:"/notice/queryViewStatusByNoticeId",
        type:"post",
        dataType:"json",
        data:{noticeId: noticeId},
        async:false,
        success:function(data){
            if (!data.success) {
                message = data.message;
            } else {
				hasCutInfo = data.hasCutInfo;
			}
        }
		
	});
	
	if (message != "") {
		showErrorMessage(message);
		return;
	}
	
	//查询当前通告单是否有剪辑信息
	var deleteMessage = '';
	if (hasCutInfo) {
		deleteMessage = '该通告单中存在未清除的剪辑信息，删除后通告单与剪辑信息将不可恢复，是否确认删除？';
	}else {
		deleteMessage = '删除后将不可恢复，是否确认删除？';
	}
	
	popupPromptBox("提示",deleteMessage, function() {
		   $.ajax({
	           url:"/notice/deleteOneNotice",
	           type:"post",
	           dataType:"json",
	           data:{noticeId: noticeId},
	           async:true,
	           success:function(data){
	               if (data.success) {
	                   showSuccessMessage(data.message);
	                   //取出当前页数
	                   var currPageNo = $("#useCurrPageNo").val();
	                   //删除后刷新页面
	                   loadUsedNoticeListData(currPageNo,'');
	               } else {
	                   showErrorMessage(data.message);
	               }
	           }
	       });
		});
}

//通告单列表删除界面
function deleteListNotice(noticeId, finishCount){
	//只读权限，不能进行删除操作
	if(isNoticeReadonly) {
		return false;
	}
	
	if (finishCount != "null" && finishCount != '0') {
		showErrorMessage("当前通告单中有已完成的场景不能删除！");
		return false;
	}
	
	var message = "";
	//判断其它状态（删戏，部分完成，也不能删戏；只有甩戏才能删除）
	$.ajax({
		url:"/notice/queryViewStatusByNoticeId",
        type:"post",
        dataType:"json",
        data:{noticeId: noticeId},
        async:false,
        success:function(data){
            if (!data.success) {
                message = data.message;
            } 
        }
		
	});
	
	if (message != "") {
		showErrorMessage(message);
		return ;
	}
	
	popupPromptBox("提示","删除后将不可恢复，是否确认删除？", function() {
		   $.ajax({
	           url:"/notice/deleteOneNotice",
	           type:"post",
	           dataType:"json",
	           data:{noticeId: noticeId},
	           async:true,
	           success:function(data){
	               if (data.success) {
	                   showSuccessMessage(data.message);
	                   //每次加载前清空数据
	                   $("#noticeListTable").html("");
	                   //删除后刷新页面
		               	initNoticeListWindow(0,0,1,true);
	               } else {
	                   showErrorMessage(data.message);
	               }
	           }
	       });
		});
}


//加载反馈列表表格
function loadFedbackTable(tableId, noticeId) {
	var top;
	this.tableId = tableId;
	this.records = null;
	this.totalCount = null, hasFinishLookCount = null;
	//获取记录数据
	this.getRecords=function(){
		var records = null;
		var totalCount = null;
		var hasFinishLookCount = null;
		$.ajax({
			url: "/notice/queryNoticeFedBackInfo",
			type: "post",
			data: {noticeId: noticeId},
			dataType: "json",
			async: false,
			success:function(data) {
				records=data.fedBackList;
				totalCount = data.totalCount;
				hasFinishLookCount = data.hasFinishLookCount;
			}
		});
		this.records = records;
		this.totalCount = totalCount;
		this.hasFinishLookCount = hasFinishLookCount;
		
		return this.records;
	};
	
	//加载表格
	this.loadTable=function(){
		this.getRecords();
		this.createTable();
	};
	
	//创建表格html
	this.createTable = function (){
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tableObj.append('<div class="theadDiv" id="theadDiv"><table cellpadding="0" cellspacing="0" border="0" class="user_table">'+
				'<thead><tr id="tableHead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#tableHead");
		
		_head.append('<td><p class="userName">姓名</p></td>');
		_head.append('<td><p class="roleName">职务</p></td>');
		_head.append('<td><p class="fedbackStatus">反馈状态</p></td>');
		_head.append('<td><p class="remark">意见</p></td>');
		_head.append('<td><p class="statusUpdateTime">更新时间</p></td>');
		
		_tableObj.append('<div class="tbodyDiv" id="tbodyDiv"><table cellpadding="0" cellspacing="0" border="0"><tbody id="tableBody"></tbody></table></div>');
		//表格主体
		var _tBody=_tableObj.find("#tableBody");
		
		//所有数据
		var tableData = this.records;
		if (tableData != null) {
			for (var i=0;i<tableData.length;i++) {
				var rowData=tableData[i];
				var _row = this.createRow(_tBody, rowData, i);
				_tBody.append(_row);
			}
		}
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
		var statusColor = fedBackStatusColorMap.get(rowData.backStatus);
		
		var _row = $("<tr rowId='"+rowid+"' style='color:"+ statusColor +";'></tr>");
		
		var roleNames= rowData.roleNames == null ? "" : rowData.roleNames;
		var fedbackStatus = fedBackStatusMap.get(rowData.backStatus);
		var isSatisfied = febBackIsSatisfied.get(rowData.isSatisfied);
		var remark = rowData.remark == null ? "" : "("+rowData.remark+")";
		
		_row.append('<td title="'+rowData.realName+'"><p class="userName">'+rowData.realName+'</p></td>');
		_row.append('<td title="'+ roleNames +'"><p class="roleName">'+ roleNames +'</p></td>');
		_row.append('<td title="'+ fedbackStatus +'"><p class="fedbackStatus">'+ fedbackStatus +'</p></td>');
		_row.append('<td title="'+ isSatisfied + remark +'"><p class="remark">'+ isSatisfied + remark +'</p></td>');
		_row.append('<td title="'+ rowData.statusUpdateTime +'"><p class="statusUpdateTime">'+ rowData.statusUpdateTime +'</p></td>');
		return _row;
	};
	
	this.refreshGrid = function() {
		top = $("#tbodyDiv").scrollTop();
		this.loadTable();
		$("#tbodyDiv").scrollTop(top);
	};
	
	this.getTotalCount = function() {
		return this.totalCount;
	};
	
	this.getFinishLookCount = function() {
		return this.hasFinishLookCount;
	};
}

//显示表单视图
function showTableWindow(){
	continueAjaxRecords = false;
	//点击后禁用此按钮，不能多次点击
	$("#noticeListTab").prop( "disabled", false);
	$("#noticeTableWindow").show();
	$("#noticeListWindow").hide();
	$("#noticeTableTab").addClass("tab_li_current");
	$("#noticeListTab").removeClass("tab_li_current");
	$("#noticeCalendarTab").removeClass("tab_li_current");
	//启用点击按钮
	//$("#noticeListTab").bind("click", showListWindow);
	//重新加载月份和统计信息
	getcancleNoticeData();
	
	initCancleNoticeMonth(1,'');
	//加载未销场
	loadUsedNoticeListData(1,'');
	//加载已销场通告单
	loadCanclesNoticeListData(1);
}

//显示列表视图
function showListWindow(){
	//初始化通告单列表页面
	$("#noticeTableWindow").hide();
	$("#noticeListWindow").show();
	$("#noticeTableTab").removeClass("tab_li_current");
	$("#noticeListTab").addClass("tab_li_current");
	$("#noticeCalendarTab").removeClass("tab_li_current");
	var disabled = $("#noticeListTab").prop( "disabled");
	
	if (disabled != true) {
		continueAjaxRecords = true;
		//每次加载前清空数据
		$("#noticeListTable").empty();
		//总条数
		initNoticeListWindow(0,0,1,true);
	}
	
	//初始化高级搜索框
	initSearchWindow();
	//点击后禁用此按钮，不能多次点击
	$("#noticeListTab").prop( "disabled", true);
}

//跳转到拍摄生产报表页面
function gotoProductionReport(){
	location.href="/notice/toNoticeList?report=1";
}

//点击去色
function dropColor(){
	this.continueAjaxRecords = false;
	var trs = $(".cc table tr");
	for(var i = 0; i<trs.length; i++){
		$(trs[i]).css("background-color", "white");
	}
	
	//改变按钮颜色
	$("#addViewColor").removeClass("btn-primary");
	$("#addViewColor").addClass("btn-default");
	$("#addViewColor").removeClass("btn-default");
	$("#dropViewColor").addClass("btn-primary");
}

//上色
function addColor(){
	this.continueAjaxRecords = false;
	var trs = $(".cc table tr");
	for(var i = 0; i<trs.length; i++){
		var text = $(trs[i]).first().text();
		if (text.indexOf('完成') != -1) {
			if (text.indexOf('部分') != -1) {
				$(trs[i]).css("background-color", "#FEE9FA");
			}else {
				$(trs[i]).css("background-color", "#FFBABA");
			}
		}else if(text.indexOf('删戏') != -1){
			$(trs[i]).css("background-color", "#D3F0FF");
		}else if (text.indexOf('甩戏') != -1) {
			$(trs[i]).css("background-color", "#E2FFE6");
		}
	}
	
	//改变按钮颜色
	$("#addViewColor").removeClass("btn-default");
	$("#addViewColor").addClass("btn-primary");
	$("#dropViewColor").removeClass("btn-primary");
	$("#dropViewColor").addClass("btn-default");
}

//按下回车键尽心搜索
function searchJspValue(e){
	if (e.keyCode == 13) {
		confirmSearchView();
	}
}

//搜索js内容
function searchNoticeValue(){
	//清空样式
	var trs = $(".highlight");
	for(var i=0; i<trs.length; i++){
		$(trs[i]).removeClass("highlight");
		$(trs[i]).css("background-color","");
		$(trs[i]).css("color", "");
	}
	
	 //获取输入框的内容
	 var  searchValue = $("#search").val();
	 if (searchValue == '' || searchValue == undefined) {
		showErrorMessage("请填写需要查询的场景号！");
		return ;
	}
	 if (crewType == 0 || crewType == 3) { //电影剧本
		 if (searchValue.indexOf("-") != -1) {
			 showErrorMessage("当前是电影剧组，只输入场号即可！");
			 return;
		 }
	}else { //电视剧剧本
		if (searchValue.indexOf("-") == -1) { //不包含分割符
			 showErrorMessage("当前是电视剧剧组，请输入集场号信息 例：1-1a ！");
			 return;
		 }else {
			 var splitValue = searchValue.split('-');
			 if (splitValue[0] == '-' || splitValue.length != 2 || splitValue[1] == '') {
				 showErrorMessage("集场号格式输入错误  例：1-1a ！");
				 return;
			}
		}
	}
	 
	 $.ajax({
         url:"/notice/queryNoticeListByViewId",
         type:"post",
         dataType:"json",
         data:{viewIds: searchValue},
         async:true,
         success:function(data){
             if (data.success) {
                 //删除后刷新页面
                var noticeList = data.noticeList;
                //清空列表
                $("#searchResultUl").html('');
                if (noticeList != null && noticeList.length >0) {
                	var $select = $("#searchResultUl");
                	var optionArr = [];
                	for(var b=0; b<noticeList.length; b++){
                		var map = noticeList[b];
                		var noticeDateArr = map['noticeDate'].split("-");
                		optionArr.push(" <li value='"+ map['noticeId'] +"' onclick='moveToTr(this)'>"+ noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日" + map['groupName'] +"</li>");
                	}
                	$select.append(optionArr.join(""));
                	$("#searchResultDiv").show();
				}else {
					showErrorMessage("该场戏没有添加到任何通告单中！");
				}
             } else {
                 showErrorMessage(data.message);
             }
         }
     });
	
}

//跳转到指定行
function moveToTr(own){
	//取出选中的值
	var selected = $(own).attr("value");
	//取出输入框的值
	 var  searchValue = $("#search").val();
	//取出所有的行
	var trs = $("tr[name='"+ selected +"']");
	for(var i=0; i<trs.length; i++){
		//取出当前tr的文本值
		var trText = $(trs[i]).text();
		if (trText != '' && trText != null) {
			if (trText.indexOf(searchValue) != -1) {
				//取出td
				var tds = $(trs[i]).children();
				for(var c=0; c<tds.length; c++){
					var tdText = $(tds[c]).text();
					if (tdText.indexOf(searchValue) != -1) {
						
						//取出当前td的高度
						$(tds[c]).addClass("highlight");
						//取出当前文本的高度
						var top = $(tds[c]).offset().top;
						//设置滚动条的偏移量
						document.getElementById("bodyDiv").scrollTop = top-400;
					}
				}
			}
		}
	}
	$("#searchResultDiv").hide();
}

//初始化高级搜索框
function initSearchWindow(){
	$("#searchWindow").jqxWindow({
        theme:theme,  
        width: 790,
        height: 340, 
        autoOpen: false,
        maxWidth: 800,
        maxHeight: 600,
        resizable: true,
        isModal: true,
        showCloseButton: true,
        modalZIndex: 1000,
        initContent: function() {
        	//初始化拍摄地点下拉框
        	initShootLocationSelect();
        }
   });
}

//显示高级搜索框
function showSearchWindow(){
	//停止加载数据
	continueAjaxRecords = false;
	$("#searchWindow").jqxWindow("open");
}
//初始化拍摄地点下拉框
function initShootLocationSelect(){
	$.ajax({
        url:"/viewManager/loadAdvanceSerachData",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
        	if (data.success) {
        		var viewFilterDto = data.viewFilterDto;
        		//拍摄地点
        		var shootLocationList = viewFilterDto.shootLocationList;
        		//选择拍摄地
        		for(var shootLocation in shootLocationList){
        			$("#shootLocationSelect").append("<option value='" + shootLocation + "'>" + shootLocationList[shootLocation] + "</option>");
        		}
        		$("#shootLocationSelect").selectpicker("refresh");
			}else {
				showErrorMessage(data.message);
			}
        }
	});
}

//点击搜索框的确定按钮‘
function confirmSearchView(){
	//判断数据是否加载完成
	/*if (!isFinishLoad) {
		showErrorMessage("加载尚未完成，请稍后！");
		return;
	}*/
	
	 filter = {};
	 //集场号
	 var  searchValue = $("#search").val();
	 if (searchValue != '' && searchValue != undefined && searchValue != null) {
	
		 if (crewType == 0 || crewType == 3) { //电影剧本
			 if (searchValue.indexOf("-") != -1) {
				 showErrorMessage("当前是电影剧组，只输入场号即可！");
				 return;
			 }
		}else { //电视剧剧本
			if (searchValue.indexOf("-") == -1) { //不包含分割符
				 showErrorMessage("当前是电视剧剧组，请输入集场号信息 例：1-1a ！");
				 return;
			 }else {
				 var splitValue = searchValue.split('-');
				 if (splitValue[0] == '-' || splitValue.length != 2 || splitValue[1] == '') {
					 showErrorMessage("集场号格式输入错误  例：1-1a ！");
					 return;
				}
			 }
		}
		 filter.sceriesViewNo = searchValue;
	 }
	 //拍摄地点
	 var shootLocation = $("#shootLocationSelect").val();
	 var shootLocationStr = "";
	if(shootLocation != null && shootLocation !=""){
		for(var i = 0; i< shootLocation.length; i++){
			shootLocationStr += shootLocation[i]+",";
		}
		shootLocationStr = shootLocationStr.substring(0,shootLocationStr.length-1);
		filter.shootLocationStr = shootLocationStr;
	}
	
	 //获取服化道信息
	 /*var clothPropText = $("#clothPropInput").val();*/
	 //获取带号信息
	 var viewTape = $("#viewTapeNum").val();
	 if (viewTape != null && viewTape != '') {
		filter.viewTape = viewTape;
	}
	 
	 //获取备注信息
	 var remarkText = $("#remarkInfoInput").val();
	 if (remarkText != null && remarkText != '') {
		filter.reamrkStr = remarkText;
	}

	 //每次加载前清空数据
     $("#noticeListTable").html("");
     continueAjaxRecords = true;
     //删除后刷新页面
     initNoticeListWindow(0,0,1,true);
     $("#searchWindow").jqxWindow("close");
}

//关闭搜索框
function closeSearchWindow(){
	$("#searchWindow").jqxWindow("close");
	clearSearchContent();
}

//清空搜索框内容
function clearSearchContent(){
	$("#shootLocationSelect").selectpicker('deselectAll');
	$("#clothPropInput").val('');
	$("#viewTapeNum").val('');
	$("#remarkInfoInput").val('');
}

//点击未销场的上一页
function preUseNoticePage(){
	var usedMainDivWidth = $("#usedMainDiv").width();
	//取出当前页数
	var useCurrPage = $("#useCurrPageNo").val();
	
	if (useCurrPage == '1') {
		return;
	}else {
		var usedWidth = 0-usedMainDivWidth;
		$("#usedMainDiv").hide().empty();
		$("#usedMainDiv").css("left", usedWidth+"px");
		var prePageNo = parseInt(useCurrPage) -1;
		loadUsedNoticeListData(prePageNo,'usedLeft');
	}
}

//点击未销场的下一页
function nextUseNoticePage(){
	//取出当前页数
	var usedMainDivWidth = $("#usedMainDiv").width();
	
	var useCurrPage = $("#useCurrPageNo").val();
	
	if (useCurrPage == totalUseNoticePageCount) {
		return;
	}else {
		$("#usedMainDiv").css("left", '');
		var usedWidth = 0-usedMainDivWidth;
		$("#usedMainDiv").hide().empty();
		$("#usedMainDiv").css("right", usedWidth+"px");
		var nextPageNo = parseInt(useCurrPage) + 1;
		loadUsedNoticeListData(nextPageNo,'usedRight');
	}
	
	
}

//点击销场的上一页
function preCancledNoticePage(){
	var usedMainDivWidth = $("#cancleNoticeMainDiv").width();
	//取出销场的当前页
	var cancledPageNo = $("#cancledPageNo").val();
	if (cancledPageNo == '1') {
		return;
	}else {
		var usedWidth = 0-usedMainDivWidth;
		$("#cancleNoticeMainDiv").hide().empty();
		$("#cancleNoticeMainDiv").css("left", usedWidth+"px");
		var prePageNo = parseInt(cancledPageNo) -1;
		loadCanclesNoticeListData(prePageNo,'cancledLeft');
	}
}

//点击销场的下一页
function nextCancledNoticePage(){
	var usedMainDivWidth = $("#cancleNoticeMainDiv").width();
	//取出当前页数
	var cancledPageNo = $("#cancledPageNo").val();
	
	if (cancledPageNo == totalCancleNoticePageCount) {
		return;
	}else {
		$("#cancleNoticeMainDiv").css("left", '');
		var usedWidth = 0-usedMainDivWidth;
		$("#cancleNoticeMainDiv").hide().empty();
		$("#cancleNoticeMainDiv").css("right", usedWidth+"px");
		var nextPageNo = parseInt(cancledPageNo) + 1;
		loadCanclesNoticeListData(nextPageNo,'cancledRight');
	}
}

//通告单月份的上一页
function preNoticeMonth(){
	//取出月份
	var cancleNoticeMonth = $("#noticeMonthInput").val();
	if (cancleNoticeMonth == '1') {
		return;
	}else {
		/*var usedMainDivWidth = $("#cancledMonthDiv").width();
		var usedWidth = 0-usedMainDivWidth;
		$("#cancledMonthDiv").hide().empty();
		$("#cancledMonthDiv").css("left", usedWidth+"px");*/
		var prePageNo = parseInt(cancleNoticeMonth) -1;
		initCancleNoticeMonth(prePageNo,'left');
	}
	
}

//通告单月份的下一页
function nextNoticeMonth(){
	//取出月份
	var cancleNoticeMonth = $("#noticeMonthInput").val();
	if (cancleNoticeMonth == totalMonthCount) {
		return;
	}else {
		/*var usedMainDivWidth = $("#cancledMonthDiv").width();
		var usedWidth = 0-usedMainDivWidth;
		$("#cancledMonthDiv").css("left", '');
		$("#cancledMonthDiv").hide().empty();
		$("#cancledMonthDiv").css("right", usedWidth+"px");*/
		var prePageNo = parseInt(cancleNoticeMonth) +1;
		initCancleNoticeMonth(prePageNo,'right');
	}
}

//停止加载数据
function stopLoadData(){
	continueAjaxRecords = false;
}

//导出通告单列表
function exportNoticeList() {
	//显示loading效果
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	
	window.location.href= "/notice/exportNoticeListData?"+ filter;
	_LoadingHtml.hide();
	$(".opacityAll").hide();
}