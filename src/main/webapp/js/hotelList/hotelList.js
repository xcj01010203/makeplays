$(function(){
	//默认显示酒店信息
	$("#hotel_information").addClass("tab_li_current");
	$("#lodging_costs").removeClass("tab_li_current");
	$(".hotel-public").hide();
	$(".hotel-manage-div").show();
	//初始化宾馆信息主列表
	initHotelInfoGrid();
	//初始化消息弹出窗口
	initTipWindow();
	
	//判断是否有权限
	if(isHotelListReadonly) {
		$("#addNewHotel").remove();
	}
	if (!hasExportInhotelCostAuth) {
		$("#exportInhotelCostBtn").remove();
	}
	
	
	$(document).on("click", function(){
		$("#selectDateBtn").removeClass("open");
		$("#hotelDatePanel").slideUp(300);
	});
	
});

//初始化宾馆信息主列表
function initHotelInfoGrid(){
//	 var data = {};
//     var hotelNames = ["七天快捷酒店", "如家快捷酒店", "樱桃快捷宾馆", "格林豪泰", "万德隆酒店", "香格里拉大酒店"];
//     var location = ["北京市朝阳区", "北京市昌平区", "北京市丰台区", "北京市通州区", "北京莲花路", "北京朝阳大街"];
//     var timeSlot = ["2017-02-25~2017-03-15", "2017-02-25~2017-03-15", "2017-02-25~2017-03-15", "2017-02-25~2017-03-15", "2017-02-25~2017-03-15", "2017-02-25~2017-03-15"];
//     var roomNum = ["30", "15", "10", "15", "20", "9"];
//     var hotelPeople = ["30", "20", "25", "40", "35", "28"];
//     var cost = [10005, 13008, 9000, 5980, 12004, 10080];
//     var generaterow = function(i){
//    	 var row = {};
//         row["hotelName"] = hotelNames[Math.floor(Math.random() * hotelNames.length)];
//         row["location"] = location[Math.floor(Math.random() * location.length)];
//         row["timeSlot"] = timeSlot[Math.floor(Math.random() * timeSlot.length)];
//         row["roomNum"] = roomNum[Math.floor(Math.random() * roomNum.length)];
//         row["hotelPeople"] = hotelPeople[Math.floor(Math.random() * hotelPeople.length)];
//         row["cost"] = cost[Math.floor(Math.random() * cost.length)];
//         
//         return row;
//     };
//     for (var i = 0; i < 10; i++) {
//         var row = generaterow(i);
//         data[i] = row;
//     }
     
     var source ={
         url:'/hotelManager/queryHotelInfoList',
         datatype: "json",
         type: "post",
         datafields:
         [
             { name: 'id', type: 'string' },
             { name: 'hotelName', type: 'string' },
             { name: 'hotelAddress', type: 'string' },
             { name: 'roomNumber', type: 'string' },
             { name: 'checkInDate', type: 'string' },
             { name: 'checkOutDate', type: 'string' },
             { name: 'peopleCount', type: 'int' },
             { name: 'totalMoney', type: 'double' }
         ]
     };
     
     var dataAdapter = new $.jqx.dataAdapter(source);
     
     var hotelNameRender = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	    var html = [];
	 		html.push("<div class='jqx-column'>");
	 		html.push("	<a class='float-left' href='javascript:modifyHotelDetailInfo(\""+row+"\")'>" + rowdata.hotelName + "</a>");
            html.push("</div>");
 		    return html.join(""); 
     };
     var timeSlot = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
 	    var html = [];
 	    if(rowdata.checkInDate != "" && rowdata.checkOutDate != ""){
 	    	html.push("<div class='jqx-column align-center'>"+ rowdata.checkInDate + "~" + rowdata.checkOutDate + "</div>");
 	    }else{
 	    	html.push("<div class='jqx-column align-center'></div>");
 	    }
 		
		return html.join(""); 
     };
     var costRender = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
 	    var html = [];
 	    if(rowdata.totalMoney != "" && rowdata.totalMoney != null && rowdata.totalMoney != undefined){
 	    	html.push("<div class='jqx-column align-right'>"+ fmoney(rowdata.totalMoney)+ "</div>");
 	    }else{
 	    	html.push("<div class='jqx-column align-right'></div>");
 	    }
 		
		return html.join(""); 
     };
     
     $("#hotelMainGrid").jqxGrid({
         width: 'calc(100% - 2px)',
         height: 'calc(100% - 2px)',
         columnsheight: 35,
         rowsheight: 30,
         source: dataAdapter,
         showtoolbar: false,
         columns: [
                   { text: '酒店', datafield: 'hotelName', cellsrenderer: hotelNameRender,  width: '20%' },
                   { text: '位置', datafield: 'hotelAddress', width: '25%' },
                   { text: '时段', cellsrenderer: timeSlot, width: '20%', align: "center", cellsalign: 'center'},
                   { text: '房间数', datafield: 'roomNumber', width: '10%', align: "center", cellsalign: 'center'},
                   { text: '入住人数', datafield: 'peopleCount', width: '10%', align: "center", cellsalign: 'center'},
                   { text: '费用', datafield: 'totalMoney', cellsrenderer: costRender, width: '15%',  align: "center"}
                 ]
     });    
}

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
         theme:theme,
         modalOpacity: 0.3,
         okButton: $('#sure'), 
         cancelButton: $('#closeBtn'),
         autoOpen: false,
         initContent: function () {
             $('#sure').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').on("click", function(){
            	 closeRightPopWin();//关闭
            	 $('#eventAll').jqxWindow('close');
             });
         }
     });
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


//添加酒店信息
function addHotelInfo(){
	$("#rightPopUpWin").animate({"right": "0px"}, 300).show();
	$("#hotelDetailIframe").attr("src", "/hotelManager/toCheckInHotelPage");
}

//修改酒店信息
function modifyHotelDetailInfo(editrow){
	var dataRecord = $("#hotelMainGrid").jqxGrid('getrowdata', editrow);
	$("#rightPopUpWin").animate({"right": "0px"}, 300).show();
	$("#hotelDetailIframe").attr("src", "/hotelManager/toCheckInHotelPage?hotelId="+dataRecord.id);
}


//关闭滑动窗口
function closeRightPopWin(){
	var width = $("#rightPopUpWin").width();
	clearInterval(timer);
	$("#rightPopUpWin").animate({"right": 0-width},300);
	
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 300);
	$("#hotelMainGrid").jqxGrid("updatebounddata");
}
//显示酒店信息
function showHotelInfo(){
	$("#hotel_information").addClass("tab_li_current");
	$("#lodging_costs").removeClass("tab_li_current");
	var width = $("#contactRightWin").width();
	$("#contactRightWin").hide().css("right", 0-width);
	$(".hotel-public").hide();
	$(".hotel-manage-div").show();
	//将（住宿费用）未清空的筛选状态清空
	var $span = $("span.up-arrow");
	if($span != undefined){
		$span.removeClass("up-arrow");
		$span.addClass("caret");
	}
	$("#selectDateBtn").removeClass("open");
	$("#hotelDatePanel").hide();
}
//显示住宿费用
function showHotelFinance(){
	$("#lodging_costs").addClass("tab_li_current");
	$("#hotel_information").removeClass("tab_li_current");
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").hide().css("right", 0-width);
	$(".hotel-public").hide();
	$(".hotel-finance-div").show();
	subData.startDate = '';
	subData.endDate = '';
	subData.hotelName = '';
	$("#startDate").val('');
	$("#endDate").val('');
	$(".table-main-body-div").scrollTop(0);
	//加载住宿费用主列表所需数据 
	getHotelData();
	//获取下拉列表接口
	getDropDownList();
	//初始化选择时间事件
	initSelectDateEvent();
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
}




/*********************************************************住宿费用****************************************************************/
var subData = {};
subData.startDate = '';
subData.endDate = '';
subData.hotelName = '';

//表格的滑动和点击事件
function trEvent(){
	$("#tableMainBody tr ").on("click", function(){
		$(this).siblings("tr").css({"background": "none"});
		$(this).css({"background": "#e3e3e4"});
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
					$("#hotelNameSelect").empty();
					$("#hotelNameSelect").append(hotelNamehtml.join(""));
				}
				
				$("#hotelNameSelect").selectpicker("refresh");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
	//宾馆名称筛选
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
	var rightPopWidth = $("#contactRightWin").width();
	$("#contactRightWin").animate({"right": 0-rightPopWidth},300);
	
	var timer = setTimeout(function(){
		$("#contactRightWin").hide();
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
				$("#contactRightWin").show().animate({"right":"0px"}, 500);
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
	var startDate = subData.startDate;
	var endDate = subData.endDate;
	var hotelName = subData.hotelName;
	window.location.href='/inHotelCostController/exportInHotelCostDetailInfo?startDate='+startDate+'&endDate='+ endDate +'&hotelName='+hotelName;
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
	getHotelData();
	ev.stopPropagation();
}

