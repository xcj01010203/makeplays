var seriesLgth = 43;//每集时长
var cutRadio = 90;//精剪比
var myChart1;
var myChart2;
var crewType = null;
$(function(){
	//获取剧组类型
	getCrewType();
	//初始化参数设置弹窗
	initSetParamWin();
	//初始化echarts图表
	initEcharts();
	//初始化表格数据
	initGrid();
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
	            if (response.seriesLgth != null && response.seriesLgth != undefined) {
	            	seriesLgth = response.seriesLgth;//每集时长
	            	$("#seriesLength").val(seriesLgth);
				}
	            if (response.cutRadio != null && response.cutRadio != undefined) {
	            	cutRadio = response.cutRadio * 100;
	            	$("#cutRatio").val(cutRadio);
				}
	            if (crewType == 0 || crewType == 3) {
					$("#fenJiDiv").remove();
				}
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//显示报表
function showReportForm(own){
	$(own).siblings("li").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	$(".public-style").hide();
	$(".pic-div").show();
}
//显示数据
function showDataGrid(own){
	$(own).siblings("li").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	$(".public-style").hide();
	$(".data-div").show();
}

//初始化表格数据
function initGrid(){
	var datafieldsArr = [];
	datafieldsArr.push({name: "planViewCount", type: "int"}); //计划完成
	datafieldsArr.push({name: "shootDate", type: "string"}); //日期
	datafieldsArr.push({name: "realViewCount", type: "int"}); //实际完成
	datafieldsArr.push({name: "totalViewCount", type: "double"}); //累计完成
	datafieldsArr.push({name: "avgPageCount", type: "double"}); //日均完成
	datafieldsArr.push({name: "everyDayCha", type: "double"}); //偏离天数
	if (crewType != 0 && crewType != 3) {
		datafieldsArr.push({name: "finishSeriesno", type: "string"}); //完成剧本集数
		datafieldsArr.push({name: "finishCutviewCount", type: "string"}); //已剪辑集数
		datafieldsArr.push({name: "crudeCutSeriesnos", type: "string"}); //预计粗剪集数
		datafieldsArr.push({name: "carefulCutSeriesnos", type: "string"}); //预计精剪集数
	}
	datafieldsArr.push({name: "realCutPage", type: "string"}); //剪辑页数
	datafieldsArr.push({name: "totalCutPage", type: "string"}); //累计剪辑页数
	datafieldsArr.push({name: "unCutPage", type: "string"}); //未剪辑页数
	datafieldsArr.push({name: "cutMinutes", type: "string"}); //剪辑分钟数
	datafieldsArr.push({name: "totalCutMinutes", type: "string"}); //累计分钟数
	datafieldsArr.push({name: "pageMinuteRate", type: "string"}); //页数分钟比
	datafieldsArr.push({name: "expectCutMinutes", type: "string"}); //预计粗剪分钟数
	datafieldsArr.push({name: "expectCarefulCutMinutes", type: "string"}); //预计精剪分钟数
	
	var source = {
		url: '/cutViewStatisticManager/queryCutViewStatistic',
		type: "post",
		dataType : "json",
		data: datafieldsArr,
		root: 'resultList'
	};
	var dataAdapter = new $.jqx.dataAdapter(source,{
		loadComplete: function (data) {
			if(data.success){
				var resultList = data.resultList;
				var preSeriesNoCutList = data.preSeriesNoCutList;
				var preDayCutLength = data.preDayCutLength;
//				  myChart1.showLoading();
//				  myChart2.showLoading();
				//初始化echarts图表数据
				initEchartsData(preSeriesNoCutList,resultList, preDayCutLength);
				//生成进度条
				initProgressBar(data);
				if (crewType == 0 || crewType == 3) {
					//电影剧组
					if (data.cutPageAvgDays == null || data.cutPageAvgDays == '' || data.cutPageAvgDays == undefined) {
						data.cutPageAvgDays = 0.0;
					}
					$("#page").text(parseFloat(data.cutPageAvgDays).toFixed(1)+" 页");
					$("#minute").text(data.cutMinutesAcgDys+" 分钟");
					$("#series").text(data.maxExpectCutMinutes +" 分钟");
					$("#cutCount").text(data.maxCarefulCutMinutes + " 分钟");
					
				}else {
					//电视剧、网剧
					if (data.cutPageAvgDays == null || data.cutPageAvgDays == '' || data.cutPageAvgDays == undefined) {
						data.cutPageAvgDays = 0.0;
					}
					$("#page").text(parseFloat(data.cutPageAvgDays).toFixed(1)+" 页");
					$("#minute").text(data.cutMinutesAcgDys+" 分钟");
					$("#series").text(data.expectCrudeCutSeriesnos+" 集");
					$("#cutCount").text(data.expectCarefulCutSeriesnos+" 集");
				}
			}
		},loadError: function (xhr, status, error) { }
	});
	var cellclass = function (row, columnfield, value,defaultvalue) {
		var returnstr=[];
		returnstr.push("divide_line");
		return returnstr.join(" ");
	};
	
	//实际完成
	var realViewCountRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.realViewCount == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.realViewCount +"</div>");
		}
		return html.join("");
	};
	
	//累计完成
	var totalViewCountRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.realViewCount == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.totalViewCount +"</div>");
		}
		return html.join("");
	};
	
	//剪辑页数
	var realCutPageRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.realCutPage == 0.00 || rowdata.realCutPage == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ parseFloat(rowdata.realCutPage).toFixed(1) +"</div>");
		}
		return html.join("");
	};
	
	//累计剪辑页数
	var totalCutPageRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.totalCutPage == 0.00 || rowdata.totalCutPage == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ parseFloat(rowdata.totalCutPage).toFixed(1) +"</div>");
		}
		return html.join("");
	};
	
	//未剪辑页数
	var unCutPageRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.unCutPage == 0.00 || rowdata.unCutPage == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ parseFloat(rowdata.unCutPage).toFixed(1) +"</div>");
		}
		return html.join("");
	};
	
	//剪辑分钟数
	var cutMinutesRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.cutMinutes == 0.00 || rowdata.cutMinutes == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.cutMinutes +"</div>");
		}
		return html.join("");
	};
	//累计分钟数
	var totalCutMinutesRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.totalCutMinutes == 0.00 || rowdata.totalCutMinutes == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.totalCutMinutes +"</div>");
		}
		return html.join("");
	};
	//页数分钟比
	var pageMinuteRateRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.pageMinuteRate == 0.00 || rowdata.pageMinuteRate == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.pageMinuteRate +"</div>");
		}
		return html.join("");
	};
	//预计粗剪分钟数
	var expectCutMinutesRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.expectCutMinutes == 0.00 || rowdata.expectCutMinutes == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.expectCutMinutes +"</div>");
		}
		return html.join("");
	};
	
	//预计精剪分钟数
	var expectCarefulCutMinutesRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.expectCarefulCutMinutes == 0.00 || rowdata.expectCarefulCutMinutes == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.expectCarefulCutMinutes +"</div>");
		}
		return html.join("");
	};
	
	//完成剧本集数
	var finishSeriesnoRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.finishSeriesno == 0.00 || rowdata.finishSeriesno == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.finishSeriesno +"</div>");
		}
		return html.join("");
	};
	
	//已剪集数
	var finishCutviewCountRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.finishCutviewCount == 0.00 || rowdata.finishCutviewCount == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.finishCutviewCount +"</div>");
		}
		return html.join("");
	};
	
	//预计粗剪集数
	var crudeCutSeriesnosRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.crudeCutSeriesnos == 0.00 || rowdata.crudeCutSeriesnos == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.crudeCutSeriesnos +"</div>");
		}
		return html.join("");
	};
	//预计精剪集数
	var carefulCutSeriesnosRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		if (rowdata.carefulCutSeriesnos == 0.00 || rowdata.carefulCutSeriesnos == 0) {
			html.push("<div class='jqx-column'>-</div>");
		}else {
			html.push("<div class='jqx-column'>"+ rowdata.carefulCutSeriesnos +"</div>");
		}
		return html.join("");
	};
	
	var columngroups=[
	                  { text: '日期', align: 'center', name: 'dateCol',datafield:'dateCol' },
	                  { text: '当天拍摄', align: 'center', name: 'shootCol',datafield:'shootCol' },
	                  { text: '当天粗剪', align: 'center', name: 'roughutCol',datafield:'roughutCol' },
	                  { text: '剪辑预估', align: 'center', name: 'forecastCol',datafield:'forecastCol' },
	   ];
	var gridColumnsArr = [];
	if (crewType == 0 || crewType == 3) {
		gridColumnsArr.push({ text: '',columngroup: 'dateCol',datafield:'shootDate', width: '12%', cellsAlign: 'center', align: 'center'});
		/*gridColumnsArr.push({ text: '计划完成',columngroup: 'shootCol',datafield:'planViewCount', width: '5%', cellsAlign: 'center', align: 'center'});*/
		gridColumnsArr.push({ text: '实际完成',columngroup: 'shootCol',cellsrenderer: realViewCountRenderer,datafield:'realViewCount', width: '7%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计完成',columngroup: 'shootCol',cellsrenderer: totalViewCountRenderer,datafield:'totalViewCount', width: '7%', cellsAlign: 'center', align: 'center'});
		/*gridColumnsArr.push({ text: '日均页数',columngroup: 'shootCol',datafield:'avgPageCount', width: '8%', cellsAlign: 'center', align: 'center'});*/
		/*gridColumnsArr.push({ text: '偏离天数',columngroup: 'shootCol',datafield:'everyDayCha', width: '7%', cellsAlign: 'center', align: 'center'});*/
		gridColumnsArr.push({ text: '剪辑页数',columngroup: 'roughutCol',cellsrenderer: realCutPageRenderer,datafield:'realCutPage', width: '8%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计剪辑页数',columngroup: 'roughutCol',cellsrenderer: totalCutPageRenderer,datafield:'totalCutPage', width: '10%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '未剪页数',columngroup: 'roughutCol',cellsrenderer: unCutPageRenderer,datafield:'unCutPage', width: '10%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '剪辑分钟数',columngroup: 'roughutCol',cellsrenderer: cutMinutesRenderer,datafield:'cutMinutes', width: '10%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计分钟数',columngroup: 'roughutCol',cellsrenderer: totalCutMinutesRenderer,datafield:'totalCutMinutes', width: '8%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '页数分钟比',columngroup: 'forecastCol',cellsrenderer: pageMinuteRateRenderer,datafield:'pageMinuteRate', width: '8%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '预计粗剪分钟数',columngroup: 'forecastCol',cellsrenderer: expectCutMinutesRenderer,datafield:'expectCutMinutes', width: '10%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '预计精剪分钟数(<span>'+ cutRadio +'%</span>)',columngroup: 'forecastCol',cellsrenderer: expectCarefulCutMinutesRenderer,datafield:'expectCarefulCutMinutes', width: '10%', cellsAlign: 'center', align: 'center'});
	}else {
		gridColumnsArr.push({ text: '',columngroup: 'dateCol',datafield:'shootDate', width: '10%', cellsAlign: 'center', align: 'center'});
		/*gridColumnsArr.push({ text: '计划完成',columngroup: 'shootCol',datafield:'planViewCount', width: '5%', cellsAlign: 'center', align: 'center'});*/
		gridColumnsArr.push({ text: '实际完成',columngroup: 'shootCol',cellsrenderer: realViewCountRenderer,datafield:'realViewCount', width: '6%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计完成',columngroup: 'shootCol',cellsrenderer: totalViewCountRenderer,datafield:'totalViewCount', width: '6%', cellsAlign: 'center', align: 'center'});
		/*gridColumnsArr.push({ text: '日均页数',columngroup: 'shootCol',datafield:'avgPageCount', width: '5%', cellsAlign: 'center', align: 'center'});*/
		/*gridColumnsArr.push({ text: '偏离天数',columngroup: 'shootCol',datafield:'everyDayCha', width: '5%', cellsAlign: 'center', align: 'center'});*/
		gridColumnsArr.push({ text: '完成剧本集数',columngroup: 'shootCol',cellsrenderer: finishSeriesnoRenderer,datafield:'finishSeriesno', width: '7%', cellclassname: cellclass,cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '剪辑页数',columngroup: 'roughutCol',cellsrenderer: realCutPageRenderer,datafield:'realCutPage', width: '6%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计剪辑页数',columngroup: 'roughutCol',cellsrenderer: totalCutPageRenderer,datafield:'totalCutPage', width: '7%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '未剪页数',columngroup: 'roughutCol',cellsrenderer: unCutPageRenderer,datafield:'unCutPage', width: '6%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '剪辑分钟数',columngroup: 'roughutCol',cellsrenderer: cutMinutesRenderer,datafield:'cutMinutes', width: '7%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '累计分钟数',columngroup: 'roughutCol',cellsrenderer: totalCutMinutesRenderer,datafield:'totalCutMinutes', width: '6%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '已剪集数',columngroup: 'roughutCol',cellsrenderer: finishCutviewCountRenderer,datafield:'finishCutviewCount', width: '6%', cellclassname: cellclass, cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '页数分钟比',columngroup: 'forecastCol',cellsrenderer: pageMinuteRateRenderer,datafield:'pageMinuteRate', width: '6%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '预计粗剪分钟数',columngroup: 'forecastCol',cellsrenderer: expectCutMinutesRenderer,datafield:'expectCutMinutes', width: '8%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '预计粗剪集数(<span>'+ seriesLgth +'min</span>)',columngroup: 'forecastCol',cellsrenderer: crudeCutSeriesnosRenderer,datafield:'crudeCutSeriesnos', width: '10%', cellsAlign: 'center', align: 'center'});
		gridColumnsArr.push({ text: '预计精剪集数(<span>'+ cutRadio +'%</span>)',columngroup: 'forecastCol',cellsrenderer: carefulCutSeriesnosRenderer,datafield:'carefulCutSeriesnos', width: '9%', cellsAlign: 'center', align: 'center'});
		
	}
	var gridcolumns=gridColumnsArr;
	$("#cutProgressGrid").jqxGrid({
		theme:'bootstrap',
		width: "calc(100% - 2px)",
		height: "calc(100% - 2px)",
		source: dataAdapter,
		columns:gridcolumns,
		columngroups:columngroups,
		showToolbar: true,
		columnsResize: true,
		toolbarHeight: 35,
		rowsHeight:35,
		localization: localizationobj,
		rendertoolbar: function(toolbar) {}
	});
}

//初始化参数设置弹窗
function initSetParamWin(){
	$("#paramSetWin").jqxWindow({
		theme:theme,  
		width: 400,
		height: 300, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
   });
}
//显示参数设置弹窗
function showSetParamWin(){
	//判断如果是电影剧本，则每集分钟书不能选中
	if (crewType == 0 || crewType == 3) {
		$("#seriesLength").prop("disabled", "disabled");
	}
	
	$("#paramSetWin").jqxWindow("open");
}
//取消参数设置
function cancelSetParam(){
	$("#paramSetWin").jqxWindow("close");
}
//设置参数
function confirmSetParam(){
	var subData = {};
	if($("#seriesLength").val() != ""){
		seriesLgth = $("#seriesLength").val();
		subData.lengthPerSet = seriesLgth;
	}
	if($("#cutRatio").val() != ""){
		cutRadio = $("#cutRatio").val();
		cutRadioValue = divide(cutRadio, 100);
		subData.cutRate = cutRadioValue;
	}
	if($("#seriesLength").val() == "" && $("#cutRatio").val()  == ""){
		cancelSetParam();
		return;
	}
	
	$.ajax({
		url: '/cutViewStatisticManager/updateCrewCutInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("设置成功");
				cancelSetParam();
				//初始化表格数据
				$("#cutProgressGrid").remove();
				$(".data-div").append('<div class="cut-progress" id="cutProgressGrid"></div>');
				initGrid(); 
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//初始化echarts图表
function initEcharts(){
//	myChart1.hideLoading();
//    myChart2.hideLoading();
	if((crewType != 0) && (crewType != 3)){
		myChart1=echarts.init(document.getElementById("seriesCount"));
		  myChart1.setOption({
			  title: {
			        text: ''
			    },
			    tooltip: {},
			    xAxis: {
			        data: []
			    },
			    yAxis: {},
			    dataZoom: [
			               {   // 这个dataZoom组件，默认控制x轴。
			                   type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
			                   start: 1,      // 左边在 10% 的位置。
			                   end: 60         // 右边在 60% 的位置。
			               }
			    ],
			    series: [{
			        type: 'bar',
			        data: []
			    }]
			});
	}
	  
//	  myChart1.showLoading();
	  myChart2=echarts.init(document.getElementById("dayCount"));
	  myChart2.setOption({
		  title: {
		        text: ''
		    },
		    tooltip: {},
		    xAxis: {
		        data: []
		    },
		    yAxis: {},
		    dataZoom: [
		               {   // 这个dataZoom组件，默认控制x轴。
		                   type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
		                   start: 1,      // 左边在 10% 的位置。
		                   end: 60         // 右边在 60% 的位置。
		               }
		    ],
		    series: [{
		        type: 'bar',
		        data: []
		    }]
	  });
}

//初始化echarts图表数据
function initEchartsData(preSeriesNoCutList,resultList, preDayCutLength){
	  var key_one = [], key_two = [];
	  var filed_one = [], filed_two = [];
	  var nameString = "";
	  for(var i= 0; i< preSeriesNoCutList.length; i++){
		  var item = preSeriesNoCutList[i];
		  if(item.seriesNo == undefined){
			  key_one.push(item.viewNo+"场");
			  nameString = "场";
		  }else{
			  key_one.push(item.seriesNo+"集");
			  nameString = "集";
		  }
		  
		  filed_one.push(item.toatlCutLength);
	  }
	  for(var i= 0; i< preDayCutLength.length; i++){
		  var item = preDayCutLength[i];
		  if (item.noticeDate == null) {
			  item.noticeDate = '';
		}
		  key_two.push(item.noticeDate);
		  filed_two.push(((item.cutMinutes)/60).toFixed(2));
	  }
	  var echarWidth = $(".pic-div").width();
	  $("#seriesCount").width(echarWidth-20);
	  if((crewType != 0) && (crewType != 3)){
		  myChart1.setOption({
			  title: {
			        text: ''
			    },
			    tooltip: {
			    	trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        }
			    },
			    itemStyle:{
			    	normal: {
			    		color: '#0067a4'
			    	}
			    },
			    xAxis: {
			    	name: nameString,
			        data: key_one
			    },
			    yAxis: {
			    	name: 'min'
			    },
			    series: [{
			        type: 'bar',
			        data: filed_one
			    }]
			});
	  }
	  
	  myChart2.setOption({
		  title: {
		        text: ''
		    },
		    tooltip: {
		    	trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        }
		    },
		    itemStyle:{
		    	normal: {
		    		color: '#0067a4'
		    	}
		    },
		    xAxis: {
		    	name: '日期',
		        data: key_two
		    },
		    yAxis: {
		    	name: 'min'
		    },
		    series: [{
		        type: 'bar',
		        data: filed_two
		    }]
	  });
}

//生成进度条
function initProgressBar(data){
	var viewTotalPageCount = data.viewTotalPageCount;//总页数
	var finalShootPage = data.finalShootPage;//总拍摄页数
	var finalCutPage = data.finalCutPage;//总剪辑页数
	var expectCrudeCutSeriesnos = data.expectCrudeCutSeriesnos;//预计粗剪集数
	var maxFinishCutviewCount = data.maxFinishCutviewCount;//总的已剪集数
	var totalCutMinutes = data.totalCutMinutes; //累计剪辑分钟数
	var expectCutMinutes = data.maxExpectCutMinutes;//预计粗剪分钟数
	var html = [];
	var pageRatio;
	if(finalShootPage == 0 || finalShootPage == 0){
		pageRatio = 0;
	}else{
		pageRatio = fmoney(multiply(divide(finalShootPage, viewTotalPageCount),100));
	}
	
	html.push('<div class="public-progress"><p>已拍页数:</p>');
	html.push('  <div class="progress-div">');
	html.push('    <div class="value-div">'+ pageRatio +'%</div>');
	html.push('    <div class="length-div" style="width:'+ pageRatio +'%;"></div>');
	html.push('  </div>');
	html.push('  <p>'+ parseFloat(finalShootPage).toFixed(1) +'/'+ parseFloat(viewTotalPageCount).toFixed(1) +'</p>');
	html.push('</div>');
	var cutPage;
	if(finalCutPage == 0 || viewTotalPageCount == 0){
		cutPage = 0;
	}else{
		cutPage = fmoney(multiply(divide(finalCutPage, viewTotalPageCount),100));
	}
	html.push('<div class="public-progress"><p>已剪页数:</p>');
	html.push('  <div class="progress-div">');
	html.push('    <div class="value-div">'+ cutPage +'%</div>');
	html.push('    <div class="length-div" style="width:'+ cutPage +'%;"></div>');
	html.push('  </div>');
	html.push('  <p>'+ parseFloat(finalCutPage).toFixed(1) +'/'+ parseFloat(viewTotalPageCount).toFixed(1) +'</p>');
	html.push('</div>');
	var cutSeries;
	if(maxFinishCutviewCount == 0 || expectCrudeCutSeriesnos == 0){
		cutSeries = 0;
	}else{
		if (crewType == 0 || crewType == 3) {//电影
			cutSeries = fmoney(multiply(divide(totalCutMinutes, expectCutMinutes),100));
		}else {
			cutSeries = fmoney(multiply(divide(maxFinishCutviewCount, expectCrudeCutSeriesnos),100));
		}
	}
	if (crewType == 0 || crewType == 3) {//电影
		html.push('<div class="public-progress"><p>已剪时长:</p>');
		html.push('  <div class="progress-div">');
		html.push('    <div class="value-div">'+ cutSeries +'%</div>');
		html.push('    <div class="length-div" style="width:'+ cutSeries +'%;"></div>');
		html.push('  </div>');
		html.push('  <p>'+ totalCutMinutes +'/'+ expectCutMinutes +'</p>');
	}else { //电视剧
		html.push('<div class="public-progress"><p>已剪集数:</p>');
		html.push('  <div class="progress-div">');
		html.push('    <div class="value-div">'+ cutSeries +'%</div>');
		html.push('    <div class="length-div" style="width:'+ cutSeries +'%;"></div>');
		html.push('  </div>');
		html.push('  <p>'+ maxFinishCutviewCount +'/'+ expectCrudeCutSeriesnos +'</p>');
	}
	
	html.push('</div>');
	$("#progressCutView").empty();
	$("#progressCutView").append(html.join(""));
}
//只允许输入数字和小数
function onlyNumberPointer(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}