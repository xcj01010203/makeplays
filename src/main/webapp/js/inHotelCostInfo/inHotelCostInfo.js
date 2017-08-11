$(document).ready(function(){
	
	//加载住宿费用主列表所需数据 
	getHotelData();
	//获取下拉列表接口
	getDropDownList();
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
	//设置权限
	if(!hasExportInhotelCostAuth){
		$(".export_button").remove();
	}
});

var subData = {};
subData.showDate = '';
subData.hotelName = '';

//表格的滑动和点击事件
function trEvent(){
	$("#tableMainBody tr ").on("click", function(){
		$(this).siblings("tr").css({"background": "none"});
		$(this).css({"background": "#ff0"});
	});
}

//获取下拉列表接口
function getDropDownList(){
	$.ajax({
		url: '/inHotelCostController/queryDrowData',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var hotelnameList = response.hotelName;
				if(hotelnameList.length != 0){
					var hotelNamehtml = [];
					//加载宾馆名称下拉列表
					for(var i = 0; i< hotelnameList.length; i++){
						hotelNamehtml.push('<option value = "'+ hotelnameList[i] +'">' + hotelnameList[i] + '</option>');
					}
					
					$("#hotelNameSelect").append(hotelNamehtml.join(""));
				}
				
				$("#hotelNameSelect").selectpicker("refresh");
				var monthList = response.showDate;
				if(monthList.length != 0){
					var monthHtml = [];
					for(var i = 0; i< monthList.length; i++){
						monthHtml.push('<option value = "'+ monthList[i] +'">' + monthList[i] + '</option>');
					}
					$("#hotelDateSelect").append(monthHtml.join(""));
				}
				$("#hotelDateSelect").selectpicker("refresh");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	//日期筛选
	$("#hotelDateSelect").on("change", function(){
		if($(this).val() == null){
			subData.showDate = ""; 
			getHotelData();
		}else{
			subData.showDate = $(this).val().toString();
			getHotelData();
		}
		//改变箭头放向
		var $airAtmo = $("#hotelDateSelect").val();
		var $span = $("div[class*='hotel-date-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='hotel-date-select'] span[class='up-arrow']");
		}
		if ($airAtmo == '' || $airAtmo == undefined || $airAtmo == null) {
			$span.removeClass("up-arrow");
			$span.addClass("caret");
		}else {
			$span.removeClass("caret");
			$span.addClass("up-arrow");
		}
	});
	
	//宾馆名称筛选
	//日期筛选
	$("#hotelNameSelect").on("change", function(){
		if($(this).val() == null){
			subData.hotelName = ""; 
			getHotelData();
		}else{
			subData.hotelName = $(this).val().toString();
			getHotelData();
		}
		//改变箭头放向
		var $airAtmo = $("#hotelNameSelect").val();
		var $span = $("div[class*='hotel-name-select'] span[class='caret']");
		if ($span == undefined || $span.length == 0) {
			$span = $("div[class*='hotel-name-select'] span[class='up-arrow']");
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





//加载住宿费用主列表所需数据
function getHotelData(){
	$.ajax({
		url: '/inHotelCostController/queryInHotelCostInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var inHotelCostInfoList = response.inHotelCostInfoList;
				//生成表格主体
				loadHotelMainTable("tableMainBody", inHotelCostInfoList);
				
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//生成宾馆信息数据主列表
function loadHotelMainTable(gridDiv, rows){
	var sumCost = 0.00;

	var temp = "";
	var rowspan = 1;
	var merges = [];
	var mer = {};
	var htmlStr = [];
	if(rows.length == 0){
		htmlStr.push('<tr>');
		htmlStr.push('<td colspan = "6" style="text-align: center;">暂无数据</td>');
		htmlStr.push('</tr>');
		$("#"+ gridDiv).empty();
		$("#"+ gridDiv).append(htmlStr.join(""));
		$("#accountMoney").text(fmoney(0));
		return;
	}
	
	
	
	
	var money = 0.0;
	for(var i = 0,le = rows.length;i<le;i++){
		//合计
		
		sumCost = sumCost + parseFloat(rows[i].sprice);
		var checkindate = rows[i].checkInDate;
		
		if(temp == ''){
			mer = {};
			mer.index = i;
			temp = checkindate;
			money = parseFloat(rows[i].sprice);
		}else if(temp !=checkindate){
			temp = checkindate;
			mer.money = parseFloat(money);
			mer.rowspan = rowspan;
			
			merges.push(mer);
			money = parseFloat(rows[i].sprice);
			mer = {};
			rowspan = 1;
			mer.index = i;
		}else{
			rowspan ++;
			money += parseFloat(rows[i].sprice);
		}
		 
		if(i == le -1){
			mer.rowspan = rowspan;
			mer.money = parseFloat(money);
			merges.push(mer);
			money = 0.0;
		}
	}
	
	for(var i = 0,le = merges.length;i<le;i++){
		var index = merges[i].index;
		var rnum = merges[i].rowspan;
		var money = merges[i].money;
		for(var m = 0,lem = rows.length;m<lem;m++){
			if(index == m){
				htmlStr.push('<tr>');
				htmlStr.push('<td style="width: 17%; min-width: 17%; text-align: center;" class="hotel-date" rowspan = '+rnum+'><div class="row-check-date">'+rows[m].checkInDate+'</div><div class="row-money">¥'+ fmoney(money) +'</div></td>');
				htmlStr.push('<td style="width: 20%; min-width: 20%; text-align: left; padding-left: 5px;" class="hotel-name"><a href="javascript:void(0)" onclick="showDetailInfo(\''+ rows[m].hotelname +'\',\'' + rows[m].checkInDate + '\',\'' + rows[m].pnum + '\',\''+ rows[m].rnum + '\')">'+rows[m].hotelname+'</a></td>');
				htmlStr.push('<td style="width: 15%; min-width: 15%; text-align: right; padding-right: 5px;" class="people-num">' + rows[m].pnum + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="room-num">' + rows[m].rnum + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="average-room-cost">' + fmoney(rows[m].aprice) + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="account-cost">' + fmoney(rows[m].sprice) + '</td>');
				htmlStr.push('</tr>');
			}else if(m < index +rnum && m > index){
				htmlStr.push('<tr>');
				htmlStr.push('<td style="width: 20%; min-width: 20%; text-align: left; padding-left: 5px;" class="hotel-name"><a href="javascript:void(0)" onclick="showDetailInfo(\''+ rows[m].hotelname +'\',\'' + rows[m].checkInDate + '\',\'' + rows[m].pnum + '\',\''+ rows[m].rnum + '\')">'+rows[m].hotelname+'</a></td>');
				htmlStr.push('<td style="width: 15%; min-width: 15%; text-align: right; padding-right: 5px;" class="people-num">' + rows[m].pnum + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="room-num">' + rows[m].rnum + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="average-room-cost">' + fmoney(rows[m].aprice) + '</td>');
				htmlStr.push('<td style="width: 16%; min-width: 16%; text-align: right; padding-right: 5px;" class="account-cost">' + fmoney(rows[m].sprice) + '</td>');
				htmlStr.push('</tr>');
			}else{}
			
		}
	}
	$("#"+ gridDiv).empty();
	$("#"+ gridDiv).append(htmlStr.join(""));
    
	$("#accountMoney").text(fmoney(sumCost));
	//表格的滑动和点击事件
	trEvent();
}



function showDetailInfo(hotelname,checkInDate,pnum,rnum){
	var par = {"hotelName":hotelname,"checkInDate":checkInDate};
	
	$.ajax({
		url: '/inHotelCostController/queryInHotelCostDetailInfo',
		type: 'post',
		data:  subData,
		datatype: 'json',
		data:par,
		success: function(response){
			if(response.success){
				/*$('#showInHotelCostDetailInfoWindow').jqxWindow("open");
				$('#showInHotelCostDetailInfoWindow').jqxWindow('setTitle', "宾馆名称："+hotelname+"  人数："+pnum+"  房间数："+rnum);*/
				$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
				$("#inhotelTitle").html("<span class='hotel-name' title='"+ hotelname +"'>宾馆名称："+hotelname+"</span>  <span class='people-num'>人数："+pnum+"</span>  <span class='room-num'>房间数："+rnum+"</span>");
				initDetailCostInfo(response.inHotelCostInfoDetailList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
	
}
function initDetailCostInfo(data){
    var source ={
    	localdata: data,
        datafields:
        [
            { name: 'roomNumber', type: 'string'},
            { name: 'price', type: 'string' },
            { name: 'contactName', type: 'string' }
        ],
        datatype: "json"
    };
    var money = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
    	return "<div class='jqxcloumn'>" + fmoney(rowdata.price) + "</div>";
    };
    
    
    var columns = [
          { text: '房间号', datafield: 'roomNumber', width: 100, cellsalign: 'center', align: 'center' },
          { text: '入住人员', datafield: 'contactName',cellsalign: 'center', align: 'center' },
          { text: '房价', datafield: 'price', width: 150, cellsalign: 'right', align: 'center' ,cellsrenderer: money}
        ];
    var dataAdapter = new $.jqx.dataAdapter(source);
    $("#jqxgrid").jqxGrid({
        width: '100%',
        height: 'calc(100% - 2px)',
        source: dataAdapter,
        theme: theme,
        selectionmode: 'multiplecellsextended',
        columns: columns,
        cellhover: function(e) {
			var $this = $(e);
			$this.siblings().addClass("jqx-fill-state-hover").parent().siblings().children().removeClass("jqx-fill-state-hover");
		},
		rendered: function() {
			$("div[id^='row']").mouseout(function() {
				$(this).children().removeClass("jqx-fill-state-hover");
			});
		}
    });

}
//关闭弹窗
function closePopUpWin(){
	clearInterval(timer);
	var rightPopWidth = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-rightPopWidth},300);
	
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 300);
}
//显示详细信息
function showDetailInfo(hotelname,checkInDate,pnum,rnum){
	var par = {"hotelName":hotelname,"checkInDate":checkInDate};
	
	$.ajax({
		url: '/inHotelCostController/queryInHotelCostDetailInfo',
		type: 'post',
		datatype: 'json',
		data:par,
		success: function(response){
			if(response.success){
				/*$('#showInHotelCostDetailInfoWindow').jqxWindow("open");
				$('#showInHotelCostDetailInfoWindow').jqxWindow('setTitle', "宾馆名称："+hotelname+"  人数："+pnum+"  房间数："+rnum);*/
				$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
				$("#inhotelTitle").html("<span class='hotel-name' title='"+ hotelname +"'>宾馆名称："+hotelname+"</span>  <span class='people-num'>人数："+pnum+"</span>  <span class='room-num'>房间数："+rnum+"</span>");
				initDetailCostInfo(response.inHotelCostInfoDetailList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
	
}

//导出住宿费用信息
function exportExcel(){
	var showDate = subData.showDate;
	var hotelName = subData.hotelName;
	window.location.href='/inHotelCostController/exportInHotelCostDetailInfo?checkInDate='+showDate+'&hotelName='+hotelName;
}