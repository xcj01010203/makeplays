var filter = {};
$(function() {
	//生成车辆管理主列表
	loadCarListGrid();
	initTipWindow();
	//初始化导入车辆信息窗口
	initImportCarDetailWin();
	$(document).on("click", function(){
		$("#selectDateBtn").removeClass("open");
		$("#hotelDatePanel").slideUp(300);
	});
	
});

//初始化消息弹出窗口
function initTipWindow(){
	$('#eventAll').jqxWindow({
         maxHeight: 150, 
         maxWidth: 280, 
         minHeight: 30, 
         minWidth: 250, 
         height: 145, 
         width: 270,
         modalZIndex: 20010,
         resizable: false, 
         isModal: true, 
         modalOpacity: 0.3,
         theme:theme,
         okButton: $('#sure'), 
         cancelButton: $('#closeBtn'),
         autoOpen: false,
         initContent: function () {
             $('#sure').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').on("click", function(){
            	 closeRightWin();//关闭
            	 $('#eventAll').jqxWindow('close');
             });
         }
     });
}

//加载车辆管理主列表
function loadCarListGrid() {
	//每次加载之前清空数据列表
	var trs = $("#dataTable").children("tr");
	if (trs != undefined && trs != null && trs.length > 1) {
		for(var i = 0; i < trs.length; i++){
			if (i != 0) {
				$(trs[i]).remove();
			}
		}
	}
	$.ajax({
		url: '/carManager/queryAllCarInfo',
		type: "post",
		data:filter,
		dataType : "json",
		success:function(data){
			//获取主列表
			var carInfoList = data.result;
			var $table = $("#dataTable");
			var dataArr = [];
			
			if (carInfoList != null && carInfoList.length>0) {
				for(var i=0; i<carInfoList.length; i++){
					dataArr.push("<tr onclick='changeColor(this)' sval='"+ carInfoList[i].carId +"'>");
					//编号
					dataArr.push("<td><div class='jqx-column align-center'>");
					dataArr.push("	<a carid='"+ carInfoList[i].carId +"' href='javascript:modifyCarInfo(\""+carInfoList[i].carId+"\")'>" + carInfoList[i].carNo + "</a></div></td>");
					//部门
					if (carInfoList[i].departments == null) {
						carInfoList[i].departments = '';
					}
					dataArr.push("<td><div class='jqx-column' title='"+ carInfoList[i].departments +"'>"+ carInfoList[i].departments +"</div></td>");
					//用途
					if (carInfoList[i].useFor == null) {
						carInfoList[i].useFor = '';
					}
					dataArr.push("<td><div class='jqx-column' title='"+ carInfoList[i].useFor +"'>"+ carInfoList[i].useFor +"</div></td>");
					//车牌号
					if (carInfoList[i].carNumber == null) {
						carInfoList[i].carNumber = '';
					}
					dataArr.push("<td><div class='jqx-column'>");
					dataArr.push("	<a carid='"+ carInfoList[i].carId +"' title='"+ carInfoList[i].carNumber +"' href='javascript:modifyCarInfo(\""+carInfoList[i].carId+"\")'>" + carInfoList[i].carNumber + "</a></div></td>");
					//车辆类型
					if (carInfoList[i].carModel == null) {
						carInfoList[i].carModel = '';
					}
					dataArr.push("<td><div class='jqx-column' title='"+ carInfoList[i].carModel +"'>"+ carInfoList[i].carModel +"</div></td>");
					//dataArr.push("	<a carid='"+ carInfoList[i].carId +"' href='javascript:modifyCarInfo(\""+carInfoList[i].carId+"\")'>" + carInfoList[i].carModel + "</a></div></td>");
					//电话号码
					if (carInfoList[i].phone == null) {
						carInfoList[i].phone = '';
					}
					dataArr.push("<td><div class='jqx-column' title='"+ carInfoList[i].phone +"'>"+ carInfoList[i].phone +"</div></td>");
					//累计油费
					if (carInfoList[i].totalMoney != null) {
						dataArr.push("<td><div class='jqx-column align-right'>"+ fmoney(carInfoList[i].totalMoney) + "</div></td>");
					}else {
						dataArr.push("<td><div class='jqx-column align-right'></div></td>");
					}
					//累计里程
					if (carInfoList[i].totalMiles != null) {
						dataArr.push("<td><div class='jqx-column align-right'>"+ fmoney(carInfoList[i].totalMiles) + "</div></td>");
					}else {
						dataArr.push("<td><div class='jqx-column align-right'></div></td>");
					}
					//累计油量
					if (carInfoList[i].totalOil != null) {
						dataArr.push("<td><div class='jqx-column align-right'>"+ fmoney(carInfoList[i].totalOil) + "</div></td>");
					}else {
						dataArr.push("<td><div class='jqx-column align-right'></div></td>");
					}
					//实际油耗
					if (carInfoList[i].oilConsume != null) {
						dataArr.push("<td><div class='jqx-column align-right'>"+ fmoney(carInfoList[i].oilConsume) + "</div></td>");
					}else {
						dataArr.push("<td><div class='jqx-column align-right'></div></td>");
					}
					//状态
					if(carInfoList[i].status == 0){
						dataArr.push("<td><div class='jqx-column'>离组</div></td>");
					}
					if(carInfoList[i].status == 1){
						dataArr.push("<td><div class='jqx-column'>在组</div></td>");
					}
				}
				$table.append(dataArr.join(''));
				
				var fixHelper = function(e, ui) {  
		            //console.log(ui)   
		            ui.children().each(function() {  
		                $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
		            });  
		            return ui;
		        };
		        if (!isCarInfoReadonly) {
		        	$("#dataTable").sortable({                //这里是talbe tbody，绑定 了sortable   
		        		helper: fixHelper,                  //调用fixHelper   
		        		axis:"y",  
		        		start:function(e, ui){  
		        			//ui.helper.css({"background":"#fff"})     //拖动时的行，要用ui.helper   
		        			return ui;  
		        		},  
		        		stop:function(e, ui){  
		        			//ui.item.removeClass("ui-state-highlight"); //释放鼠标时，要用ui.item才是释放的行   
		        			return ui;  
		        		}
		        	}).disableSelection();
		        	$("#dataTable").on( "sortstop", function( event, ui ) {
		        		updateCarSort();
		        	});
					
				}
			}
		}
	});
}

//添加
function addCarInfo(){
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	$("#carInfoDetailIframe").attr("src", "/carManager/toCarDetailListPage");
}

//修改
function modifyCarInfo(carId){
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	$("#carInfoDetailIframe").attr("src", "/carManager/toCarDetailListPage?carId="+carId);
}
//关闭
function closeRightWin(){
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
	loadCarListGrid();
}

/**
 * 弹出提示框
 * @param title 标题
 * @param content 内容
 */
function tipInfoBox(title,content,obj){
	$('#eventAll').jqxWindow('open');
	if(title!=undefined || title!=null)
		$('#eventAll').jqxWindow('setTitle', title);
	if(content!=undefined || content!=null)
		$('#eventContent').html(content);
	if(content.length > 15)
		$('#eventContent').css("margin-top","13px");
	$('#eventAll').unbind("close");
	$('#eventAll').on('close', function (event) {
		if (event.args.dialogResult.OK) {
			$(obj);
        }
    });
}


//导出车辆信息
function exportCarInfo(){
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
	var searchCarNo = '';
	if (filter.searchCarNo != null || filter.searchCarNo != undefined) {
		searchCarNo = filter.searchCarNo;
	}
	
	window.location.href= "/carManager/exportCarList?searchCarNo="+ searchCarNo;
	_LoadingHtml.hide();
	$(".opacityAll").hide();
	
}

//初始化导入窗口
function initImportCarDetailWin() {
	$("#importExportCarDetailWin").jqxWindow({
		theme: theme,
		height: 540,
		width: 482,
		resizable: false,
		showCloseButton: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
		}
	});
}

//显示导入窗口
function showImportWin() {
	$("#importExportCarDetailWin").jqxWindow("open");
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/carManager/importCarDetailInfo&&needIsCover=true&&refreshUrl=/carManager/toCarListPage&&templateUrl=/template/import/车辆信息导入模板.xls");
}

//关闭导入窗口
function closeImportWin(){
	$("#importExportCarDetailWin").jqxWindow("close");
}
//显示车辆信息
function showCarInfo(){
	$("#car_information").addClass("tab_li_current");
	$("#car_costs").removeClass("tab_li_current");
	$(".car-public").hide();
	$(".car-info-modal").show();
	//将（车辆费用）未清空的筛选状态清空
	var $span = $("span.up-arrow");
	if($span != undefined){
		$span.removeClass("up-arrow");
		$span.addClass("caret");
	}
	$("#selectDateBtn").removeClass("open");
	$("#hotelDatePanel").hide();
}
//显示车辆费用信息
function showCarFinance(){
	$("#car_costs").addClass("tab_li_current");
	$("#car_information").removeClass("tab_li_current");
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").hide().css("right", 0-width);
	$(".car-public").hide();
	$(".car-finance-modal").show();
	$("#startDate").val('');
	$("#endDate").val('');
	subData.startDate = '';
	subData.endDate = '';
	subData.searchCarNumber = '';
	$(".car-finance-body").scrollTop(0);
	queryCarOilMoneyInfo();
	initSelectDateEvent();
	initDropdownList();
	$('.selectpicker').selectpicker({
        size: 7
    });
}


/****************************************车辆加油升数和加油金额汇总信息*******************************************************/
var subData = {};
function queryCarOilMoneyInfo(){
	$.ajax({
		url: '/carOilMoneyManager/queryCarOilMoneyInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				//生成汇总表
				produceCarOilCount(response.totalMoney, response.resultList);
			}
		}
	});
}

//生成车辆加油升数和加油金额汇总表
function produceCarOilCount(totalMoney, resultList){
	var html = [];
	if(resultList != null && resultList.length != 0){
		for(var i= 0; i< resultList.length; i++){
			var carList = resultList[i].carList;
			if(carList.length != 0){
				for(var j= 0; j< carList.length; j++){
					html.push('<tr>');
					if(j== 0){
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%;" rowspan="'+ carList.length +'">');
						html.push('<div class="cell-date">' + resultList[i].workDate + '</div>');
						html.push('<div class="cell-money">' + resultList[i].dayTotalMoney + '</div>');
						html.push('</td>');
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: left; box-sizing: border-box; padding-left: 5px;">' + carList[j].carNumber + '</td>');
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: right; box-sizing: border-box; padding-right: 5px;">' + carList[j].totalLiters + '</td>');
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: right; box-sizing: border-box; padding-right: 5px;">' + fmoney(carList[j].totalMoney) + '</td>');
					}else{
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: left; box-sizing: border-box; padding-left: 5px;">' + carList[j].carNumber + '</td>');
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: right; box-sizing: border-box; padding-right: 5px;">' + carList[j].totalLiters + '</td>');
						html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: right; box-sizing: border-box; padding-right: 5px;">' + fmoney(carList[j].totalMoney) + '</td>');
					}
					html.push('</tr>');
				}
			}else{
				
			}
			
		}
	}else{
		html.push('<tr>');
		html.push('<td colspan="4" style="text-align: center; vertical-align: middle;">暂无数据</td>');
		html.push('</tr>');
	}
	$("#carFinanceGrid").empty();
	$("#carFinanceGrid").append(html.join(""));
	if(totalMoney != null && totalMoney != undefined){
		$("#oilCostCount").html(fmoney(totalMoney));
	}else{
		$("#oilCostCount").html('0.00');
	}
	
	//初始化行点击事件
	initRowClick();
}

//初始化行点击事件
function initRowClick(){
	$("#carFinanceGrid").on("click", "tr", function(){
		$(this).siblings("tr").removeClass("row-click");
		$(this).addClass("row-click");
	});
}

//初始化下拉列表
function initDropdownList(){
	$.ajax({
		url: '/carOilMoneyManager/queryDropDownData',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var carNumber = response.carNumber;
				if(carNumber != null && carNumber.length != 0){
					var carNumberHtml = [];
					for(var i= 0; i< carNumber.length; i++){
						carNumberHtml.push('<option value = "'+ carNumber[i] +'">' + carNumber[i] + '</option>');
					}
					$("#carNoSelect").empty();
					$("#carNoSelect").append(carNumberHtml.join(""));
				}else{
					$("#carNoSelect").empty();
				}
				$("#carNoSelect").selectpicker("refresh");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	//车牌号筛选
	$("#carNoSelect").on("change", function(){
		if($(this).val() == null){
			subData.searchCarNumber = ""; 
			queryCarOilMoneyInfo();
		}else{
			subData.searchCarNumber = $(this).val().toString();
			queryCarOilMoneyInfo();
		}
		//改变箭头放向
		var $airAtmo = $("#carNoSelect").val();
		var $span = $("div[class*='car-no-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='car-no-select'] span[class='up-arrow']");
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

//导出车辆费用列表
function exportCarFinance(){
	var startDate = subData.startDate;
	var endDate = subData.endDate;
	var searchCarNumber = subData.searchCarNumber;
	if (searchCarNumber == null || searchCarNumber == undefined) {
		searchCarNumber = '';
	}
	window.location.href="/carOilMoneyManager/exportCarOilMoneyInfo?startDate="+startDate+"&&endDate="+ endDate +"&&searchCarNumber="+searchCarNumber;
}

//初始化选择时间事件
function initSelectDateEvent(){
	$("#hotelDatePanel").on("click", function(ev){
		ev.stopPropagation();
	});
	
}

//显示选择日期面板
function showDatePanel(own, ev){
	if($(own).hasClass("open")){
		$("#selectDateBtn").removeClass("open");
		$("#hotelDatePanel").slideUp(300);
	}else{
		$(own).addClass("open");
		var position = $(own).position();
		var height = $(own).outerHeight();
		$("#hotelDatePanel").slideDown(300).css({"top":position.top + height, "left": position.left-70});
	}
	ev.stopPropagation();
}

//查询时间段的数据
function queryDateTimeData(ev){
	subData.startDate = $("#startDate").val();
	subData.endDate = $("#endDate").val();
	$("#selectDateBtn").removeClass("open");
	$("#hotelDatePanel").slideUp(300);
	queryCarOilMoneyInfo();
	ev.stopPropagation();
}

//搜索车牌号
function searchCarNoInfo(ev) {
	//当按下回车键时，进行搜索
	if (ev.keyCode == 13) {
		confirmSearchCarInfo();
	}
}

//确认查询车辆信息
function confirmSearchCarInfo() {
	//取出输入内容
	var inputCarNo = $("#searchCarNoText").val();
	
	filter.searchCarNo = inputCarNo;
	
	loadCarListGrid();
}

//添加选中颜色
function changeColor(own) {
	//移除所有的选中颜色
	$("#dataTable tr").each(function(){
		$(this).removeClass("click-tr-color");
	});
	//设置当前行被选中
	$(own).addClass("click-tr-color");
}

/**
 * 更新表格排序顺序
 */
function updateCarSort(){
	var carIdArr = [];
	$("#dataTable tr").each(function() {
		var carId = $(this).attr("sval");
		if (carId != undefined && carId != '' && carId != null) {
			carIdArr.push(carId);
		}
	});
	var carIds = carIdArr.join(',');
	$.ajax({
		url:"/carManager/updateCarSequence",
		type:"post",
		dataType:"json",
		data:{carIds:carIds},
		success:function(data){
			if (data.success) {
				/*showSuccessMessage(data.message);*/
			}else {
				showErrorMessage(data.message);
			}
		}
	});
	
}