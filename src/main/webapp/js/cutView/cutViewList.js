var filter = {};
filter.pagenum = 0;
filter.pagesize = 100;
var pageCount;//总页数
var dateLength;
var crewType = null;
$(function(){
	//获取剧组类型
	getCrewType();
	//初始化高级查询窗口
	initAdvanceQuery();
	
	//加载剪辑数据
	loadCutViewData();
	loadCutCountInfo();
	//初始化输入正整数
	initInputInt();
});

//获取剧组类型
function getCrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
	            
	            var dataArr = [];
	            //拼接查询框
	            if (crewType == 0 || crewType == 3) { //电影、电视剧
	            	$("#querySeriesViewTd").append('<input type="text" class="query-series-view" placeholder="场" onkeyup="querySeriesView(this,event)">');
	            	dataArr.push(' <span>场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;次:</span>');
	            	dataArr.push('<input type="text" id="viewStartDate" placeholder="场">——<input type="text" id="viewEndDate" placeholder="场">');
	            	$("#querySeriesViewLi").append(dataArr.join(''));
				}else if (crewType == 1 || crewType == 2) { //电视剧、网剧
					$("#querySeriesViewTd").append('<input type="text" class="query-series-view" placeholder="集" onkeyup="querySeriesView(this,event)">-<input type="text" class="query-series-view" placeholder="场" onkeyup="querySeriesView(this,event)">');
					
					dataArr.push(' <span>集&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;场:</span>');
					dataArr.push('<input class="half-input" type="text" id="collStartDate" placeholder="集">-<input class="half-input" type="text" id="viewStartDate" placeholder="场">——<input class="half-input" type="text" id="collEndDate" placeholder="集">-<input class="half-input" type="text" id="viewEndDate" placeholder="场">');
					$("#querySeriesViewLi").append(dataArr.join(''));
				}
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//加载剪辑数据
function loadCutViewData(){
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
		url: '/cutViewManager/queryCutViewList',
		type: 'post',
		data: filter,
		datatype: 'json',
		success: function(response){
			if(response.success){
				pageCount = response.totalPage;
				dateLength = response.dateLength;
				initCutViewGrid(response.data);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//初始化剪辑表格
function initCutViewGrid(dataResult){
	var html = [];
	//append数据
	if((dataResult != null) && (dataResult.length != 0)){
		for(var key in dataResult){
			var item = dataResult[key];
			var firstFlag = true;
			for(var i= 0; i< item.length; i++){
					var itemData = item[i];
				for(var reference in itemData){
						var detailData = itemData[reference];
						for(var j= 0; j< detailData.length; j++){
							if(detailData[j].id == null){
								detailData[j].id = "";
							}
							html.push('<tr viewid="'+ detailData[j].viewId +'" cutid="'+ detailData[j].id +'" noticeid = "'+ detailData[j].noticeId +'">');
							if(i==0 && firstFlag){
								html.push('<td rowspan="'+ dateLength[key] +'" style="width: 10%;">' + key + '</td>');//第一列
								firstFlag = false;
							}
							if(j==0){
								html.push('<td rowspan="'+ detailData.length +'" style="width: 8%;">' + reference + '</td>');//第二列
							}
							var seriesView = "";
							if (crewType == 0 || crewType == 3) { //电影、网大
								seriesView = detailData[j].viewNo;
							}else if (crewType == 1 || crewType == 2) { //电视剧、网剧
								seriesView = detailData[j].seriesNo +'-' + detailData[j].viewNo;
							}
							
							html.push('<td style="width: 10%;">' + seriesView +'</td>');//第三列
							if(detailData[j].shootPage == null){
								detailData[j].shootPage = "";
							}
							html.push('<td style="width: 8%;">' + detailData[j].shootPage + '</td>');//第四列
							
							var shootStatusStr = '';
							if(detailData[j].shootStatus == null){
								detailData[j].shootStatus = "";
							}else{
								switch(detailData[j].shootStatus){
									case 0:
										shootStatusStr="甩戏";
										break;
									case 1:
										shootStatusStr ="部分完成";
										break;
									case 2:
										shootStatusStr = "完成";
										break;
									case 3:
										shootStatusStr = "删戏";
										break;
									case 4:
										shootStatusStr = "加戏部分完成";
										break;
									case 5:
										shootStatusStr = "加戏已完成";
										break;
								}
							}
							html.push('<td style="width: 10%;" class="split-line">' + shootStatusStr + '</td>');
							html.push('<td style="width: 10%;">');
							if(detailData[j].cutDtae == null){
								detailData[j].cutDtae = "";
							}
							html.push('<input type="text" name="cutDate"  value="'+ detailData[j].cutDtae +'" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked:function(dp){calculateDays(dp.cal.getNewDateStr(), dp.el)},oncleared:function(dp){clearDate(dp.el,dp.cal.getNewDateStr())}})">');
							html.push('</td>');
							html.push('<td style="width: 10%;">');
							if(detailData[j].cutLength == null){
								html.push('<input class="date-input minute" type="text" onblur="saveMethod(this)" onkeyup="onlyNumber(this)" value="">分');
								html.push('<input class="date-input second" type="text" onblur="saveMethod(this)" onkeyup="onlyNumber(this)" value="">秒');
							}else{
								var minute=  Math.floor(detailData[j].cutLength / 60);//向下整除
								var second = detailData[j].cutLength%60;
								html.push('<input class="date-input minute" type="text" onblur="saveMethod(this)" onkeyup="onlyNumber(this)" value="'+ minute +'">分');
								html.push('<input class="date-input second" type="text" onblur="saveMethod(this)" onkeyup="onlyNumber(this)" value="'+ second +'">秒');
							}
							html.push('</td>');
							html.push('<td style="width: 24%;">');
							if(detailData[j].remark == null){
								detailData[j].remark = "";
							}
							html.push('<input type="text" class="cut-Remark" value="'+ detailData[j].remark +'" onblur="saveMethod(this)">');
							html.push('</td>');
							html.push('<td style="width: 10%; text-align: center;">');
							if(detailData[j].cutstatus == 1){
								html.push('<div class="ui toggle checkbox"><input type="checkbox" name="isAllCheck" sval="'+ detailData[j].shootStatus +'" checked onclick="saveCutStatus(this)"><label></label></div>');
							}else{
								html.push('<div class="ui toggle checkbox"><input type="checkbox" name="isAllCheck" sval="'+ detailData[j].shootStatus +'" onclick="saveCutStatus(this)"><label></label></div>');
							}
							html.push('</td>');
							html.push('</tr>');
							
						}
					}
				
		}
	}
		
		$("#cutViewTable").append(html.join(""));
		var scrollWidth = document.getElementById("gridBody").offsetWidth - document.getElementById("cutViewTable").scrollWidth;
		$("#tableHeader").find("td:last-child").css("width", scrollWidth);
		
		//判断当前权限，只读权限则不能保存及修改
		if (isCutViewReadonly) {
			$("input[class='date-input']").attr("readonly", true);
			$("input[class='cut-Remark']").attr("readonly", true);
			$("input[name='isAllCheck']").attr("disabled", true);
			$("input[name='cutDate']").attr("disabled",true);
		}
		
	}else{
		//暂无数据
		html.push('<tr>');
		html.push('<td colspan="9" style="text-align: center;">暂无数据</td>');
		html.push('</tr>');
		$("#cutViewTable").append(html.join(""));
		return;
	}
	if(filter.pagenum < pageCount){
		filter.pagenum += 1;
		loadCutViewData();
	}else{
		//取消loading效果
		$("#loadingDataDiv").hide();
		$(".opacityAll").hide();
		filter.pagenum = 0;//将当前页数初始化为0；
	}
}

//加载剪辑统计信息
function loadCutCountInfo(){
	$("#viewCount").text('');
	$("#pageCount").text('');
	$("#cutViewCount").text('');
	$("#cutPageCount").text('');
	$("#minuteCount").text('');
	$.ajax({
		url: '/cutViewManager/queryStatisticsInfo',
		type: 'post',
		data: filter,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var statisticsInfo = response.statisticsInfo;
				$("#viewCount").text(statisticsInfo.totalCount);
				var totalPage = 0.0;
				if (statisticsInfo.totalPage != null) {
					totalPage = statisticsInfo.totalPage;
				}
				$("#pageCount").text(totalPage);
				$("#cutViewCount").text(statisticsInfo.finshCutCount);
				var finishCutPage = 0.0;
				if (statisticsInfo.finishCutPage != null) {
					finishCutPage = statisticsInfo.finishCutPage;
				}
				$("#cutPageCount").text(finishCutPage);
				$("#minuteCount").text(statisticsInfo.totalCutTimes);
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//初始化高级查询窗口
function initAdvanceQuery(){
	$("#advanceQuery").jqxWindow({
		theme:theme,  
		width: 480,
		height: 360, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
   });
}
//显示高级查询窗口
function showAdvanceQuery(){
	$("#advanceQuery").jqxWindow("open");
}
//关闭高级查询窗口
function closeAdvanceQuery(){
	$("#advanceQuery").jqxWindow("close");
}
//清空高级查询内容
function clearQueryContent(){
	var inputs = $(".query-content").find("input[type=text]");
	$.each(inputs, function(){
		$(this).val("");
	});
	filter={};
	filter.pagenum = 0;
	filter.pagesize = 100;
}

//查询
function confirmQuery(){
	filter.shootStartDate = $("#shootStartDate").val();
	filter.shootEndDate = $("#shootEndDate").val();
	filter.startSeriesNo = $("#collStartDate").val();
	filter.endSeriesNo = $("#collEndDate").val();
	filter.startViewNo = $("#viewStartDate").val();
	filter.endViewNo = $("#viewEndDate").val();
	filter.satrtShootPage = $("#startPage").val();
	filter.endShootPage = $("#endPage").val();
	if (crewType == 0 || crewType == 3) { //电影、网大
		filter.startSeriesNo = '1';
		filter.endSeriesNo = '1';
	}
	if($("#startLength").val() != ""){
		filter.startCutLength = multiply($("#startLength").val(),60);
	}else{
		filter.startCutLength = "";
	}
	if($("#endLength").val() != ""){
		filter.endCutLength = multiply($("#endLength").val(),60);
	}else{
		filter.endCutLength = "";
	}
	filter.startCutDate = $("#cutStartDate").val();
	filter.endCutDate = $("#cutEndDate").val();
	filter.pagenum = 0;
	$("#cutViewTable").empty();
	//判断当前是否显示已完成
	if ($("#showAllViews").is(":checked")) {
		//影藏已完成
		filter.isAll = false;
	}else {
		filter.isAll = true;
	}
	
	//加载剪辑数据
	loadCutViewData();
	loadCutCountInfo();
	closeAdvanceQuery();
}
//是否隐藏已完成
function isHideAll(own){
	if($(own).is(":checked")){
		filter.isAll = false;
	}else{
		filter.isAll = true;
	}
	$("#cutViewTable").empty();
	loadCutViewData();//重新加载数据
}
//剪辑日期排序
function sortCutDate(own){
	if($(own).hasClass("sort-desc")){
		$(own).removeClass("sort-desc");
		$(own).addClass("sort-order");
		filter.isASc = true;
	}else{
		$(own).removeClass("sort-order");
		$(own).addClass("sort-desc");
		filter.isASc = false;
	}
	$("#cutViewTable").empty();
	loadCutViewData();//重新加载数据
}

//初始化输入正整数
function initInputInt(){
	$("input.half-input").on("keyup", function(){
		$(this).val($(this).val().replace(/\D/g,''));
	}).on("afterpaste", function(){
		$(this).val($(this).val().replace(/\D/g,''));
	});
	$("input.query-series-view").eq(0).on("keyup", function(){
		$(this).val($(this).val().replace(/\D/g,''));
	}).on("afterpaste", function(){
		$(this).val($(this).val().replace(/\D/g,''));
	});
}
//只允许输入数字和小数
function onlyNumberPointer(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}
//只允许输入数字
function onlyNumber(own){
	$(own).val($(own).val().replace(/\D/g,''));
}

//计算总剪辑时长和已剪辑场数
function countFunction(own, source){
	//查询统计信息
	$.ajax({
		url: '/cutViewManager/queryStatisticsInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var statisticsInfo = response.statisticsInfo;
				$("#cutViewCount").text(statisticsInfo.finshCutCount);
				var finishCutPage = 0.0;
				if (statisticsInfo.finishCutPage != null) {
					finishCutPage = statisticsInfo.finishCutPage;
				}
				$("#cutPageCount").text(finishCutPage);
				$("#minuteCount").text(statisticsInfo.totalCutTimes);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//计算剪辑秒数
/*function caluCutSecond(own){
	var secondCount = 0;//总秒数
	var tr = $(own).parents("tr");
	var trObj = tr.prevAll("tr");
	$.each(trObj, function(){
		cutViewCount += 1;
		var second = $(this).find("input[type=text]").eq(2).val();
		if(second == ""){second = 0;}
		secondCount = add(secondCount, second);
	});
	
	var nowSecond = tr.find("input[type=text]").eq(2).val();
	if(nowSecond == ""){nowSecond = 0;}
	secondCount = add(secondCount, nowSecond);
	var minuteCount = $("#minuteCount").text();
	minuteCount = add(minuteCount, divide(secondCount, 60));
	$("#minuteCount").text(minuteCount);
}*/

//快速查询集场
function querySeriesView(own, event){
	if(event.keyCode == 13){
		var td = $(own).parent("td");
		if (crewType == 0 || crewType == 3) { //电影、网大
			filter.startSeriesNo = '1';
			filter.startViewNo = td.find("input").eq(0).val();
		}else if (crewType == 1 || crewType == 2) { //电视剧、网剧
			filter.startSeriesNo = td.find("input").eq(0).val();
			filter.startViewNo = td.find("input").eq(1).val();
		}
		$("#cutViewTable").empty();
		loadCutViewData();//重新加载数据
	}
}


//保存剪辑日期
function calculateDays(date, own){
	saveMethod(own);
}
//清空日期
function clearDate(own, date){
	$(own).val("");
	//判断剪辑时长是否还有值，有值则不能清空
	var trObj = $(own).parents("tr");
	var minute = trObj.find("input[type=text]").eq(1).val();
	var second = trObj.find("input[type=text]").eq(2).val();
	var remark = trObj.find("input[type=text]").eq(3).val();
	if(minute == ""){
		minute= 0;
	}
	if(second == ""){
		second = 0;
	}
	var cutLength = add(second,multiply(minute,60));
	if (cutLength != 0 || remark != '') {
		showErrorMessage('请先清空剪辑时长或备注信息，在清空剪辑日期');
		$(own).val(date);
	}else {
		saveMethod(own);
	}
	return true;
}

//保存方法
function saveMethod(own){
	if (!isCutViewReadonly){
		var trObj = $(own).parents("tr");
		var subData = {};
		if(trObj.attr("cutid") == undefined || trObj.attr("cutid") == ""){
			subData.id = "";
		}else{
			subData.id = trObj.attr("cutid");
		}
		
		var cutDataStr = [];
		cutDataStr.push(trObj.attr("viewid"));
		cutDataStr.push(trObj.attr("noticeid"));
		var cutDate = trObj.find("input[type=text]").eq(0).val();
		var dateString = "";
		var minute = trObj.find("input[type=text]").eq(1).val();
		var second = trObj.find("input[type=text]").eq(2).val();
		var remark = trObj.find("input[type=text]").eq(3).val();
		if(minute == ""){
			minute= 0;
		}
		if(second == ""){
			second = 0;
		}
		var cutLength = add(second,multiply(minute,60));
		if((cutLength != 0 || remark != '') && cutDate == ""){
			var date = new Date();
			var month;
			var day;
			if(date.getMonth()+1 < 10){
				month = "0"+ (date.getMonth()+1);
			}else{
				month = date.getMonth()+1;
			}
			if(date.getDate() < 10){
				day = "0" + date.getDate();
			}else {
				day = date.getDate();
			}
			var str = date.getFullYear()+"-"+ month +"-"+day;
			
			dateString = str;
			trObj.find("input[type=text]").eq(0).val(str);
		}else{
			dateString = cutDate;
		} 
		
		if (cutLength != 0 && cutLength != undefined) {
			trObj.find("input[type=checkbox]").prop("checked", true);
		}else {
			trObj.find("input[type=checkbox]").prop("checked", false);
		}
		
		cutDataStr.push(dateString);
		cutDataStr.push(cutLength);
		cutDataStr.push(remark);
		var checkbox = trObj.find("input[type=checkbox]").eq(0);
		if(checkbox.is(":checked")){
			cutDataStr.push(true);
		}else{
			cutDataStr.push(false);
		}
		subData.cutDataStr = cutDataStr.join(",");
		
		if (dateString != '' || cutLength != 0 || remark != '' || subData.id != '') {
			$.ajax({
				url: '/cutViewManager/saveCutViewInfo',
				type: 'post',
				data: subData,
				datatype: 'json',
				success: function(response){
					if(response.success){
						$(own).parents("tr").attr("cutid", response.cutId);
						countFunction();
						/*if (cutLength != 0 && cutLength != undefined) {
							if($(own).hasClass("minute")){
								countFunction(own,'minute');
							}
							if($(own).hasClass("second")){
								caluCutSecond(own);
								countFunction(own, 'second');
							}
							
						}*/
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		}
	}
	
}

//点击状态
function saveCutStatus(own) {
	var checkStatus = $(own).is(":checked");
	var trObj = $(own).parents("tr");
	var subData = {};
	if(trObj.attr("cutid") == undefined || trObj.attr("cutid") == ""){
		subData.id = "";
	}else{
		subData.id = trObj.attr("cutid");
	}
	if (subData.id == '') {
		$(own).prop("checked", !checkStatus);
		showErrorMessage("请先填写剪辑信息，在点击修改状态");
		return false;
	}
	
	//取出当前场的状态
	var shootStatus = $(own).attr("sval");
	var cutDate = trObj.find("input[type=text]").eq(0).val();
	var minute = trObj.find("input[type=text]").eq(1).val();
	var second = trObj.find("input[type=text]").eq(2).val();
	var remark = trObj.find("input[type=text]").eq(3).val();
	if(minute == ""){
		minute= 0;
	}
	if(second == ""){
		second = 0;
	}
	var cutLength = add(second,multiply(minute,60));
	//判断是否是有完成改为未完成
	if (!checkStatus) {
		//由完成改为未完成，此时需要判断当前长是否是完成状态，如果是完成状态，则提示不能修改剪辑状态
		if (shootStatus == 2 || shootStatus == 5) { //完成或加戏已完成
			//判断剪辑信息是否清空
			if (cutDate != '' || cutLength != 0 || remark != '') {
				$(own).prop("checked", true);
				//提示不能将状态改为未完成
				showErrorMessage("请先清空剪辑信息，在修改状态");
				return false;
			}
		}
	}
	
	subData.cutStatus = checkStatus;
	$.ajax({
		url: '/cutViewManager/updateCutViewStatus',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(!response.success){
				showErrorMessage(response.message);
			}
		}
	});
}