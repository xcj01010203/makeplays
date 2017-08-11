var viewRoleId = null;
var viewRoleName = null;
//剧组类型
$(document).ready(function() {
	//加载场景角色表
	loadViewRoleGrid();
	//初始化高级查询
	initAdvanceSearch();
	//修改角色
	initAddViewRoleInfo();
	//合并角色
	initMakeRoleToOne();
	//请假设置
	initLeaveRecord();
	//初始化戏量统计窗口
//	initRoleViewPlayStat();
	//初始化戏量统计tab、radio click事件
	initRoleViewPlayStatClick();
	/*//初始化角色场景列表
	initRoleViewList();*/
	//初始化评价窗口
	initRoleEvaluation();
});


//定义提交数据的变量
var subData = {};

//加载场景角色表
function loadViewRoleGrid() {
	var source = {
		url: '/viewRole/queryViewRoleList',
		type: "post",
		dataType : "json",
		data: subData,
		datafields : [
		     {name: "viewRoleId", type: "string"},
		     {name: "viewRoleName", type: "string"},
		     {name: "viewRoleType", type: "int"},
		     {name: "shortName", type: "string"},
		     {name: "actorId", type: "string"},
		     {name: "actorName", type: "string"},
		     {name: "seriesViewNo", type: "string"},
		     {name: "enterDate", type: "string"},
		     {name: "leaveDate", type: "strnig"},
		     {name: "totalViewCount", type: "int"},
		     {name: "totalPageCount", type: "double"},
		     {name: "finishedViewCount", type: "int"},
		     {name: "unfinishedViewCount", type: "int"},
		     {name: "leaveCount", type: "int"},
		     {name: "totalLeaveDays", type: "int"}
		],
		root: 'viewRoleList'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var viewRoleNameRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='viewrole-name-column'>");
		html.push("	<a class='jqx-column float-left viewrole-name' href='javascript:modifyViewRoleInfo(\""+row+"\")'>" + rowdata.viewRoleName + "</a>");
		html.push("	<span class='jqx-column operate-btn-list'>");
		/*html.push("		<a class='float-right role-view-list' title='角色场景表' href='javascript:showRoleViewList(\""+row+"\")'></a>");*/
		html.push("		<a class='float-right role-view-stat' title='戏量统计' href='javascript:showRoleViewPlayStat(\""+row+"\")'></a>");
		html.push("		<a class='float-right leave-record' title='请假设置' href='javascript:showLeaveRecord(\""+row+"\")'></a>");
		
		if(!isRoleReadonly && (rowdata.totalViewCount===0 || rowdata.viewRoleType == 4)){
			html.push("<a class='float-right delete-view-list' title='删除角色' href='javascript:deleteViewRoleInfo(\""+row+"\")'></a>");
		}
		html.push("	</span>");
		html.push("</div>");
		
		return html.join("");
	};
	
	var viewRoleTypeRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column align-center'>"+ viewRoleTypeMap.get(rowdata.viewRoleType) +"</div>";
		
		return html;
	};
	var leaveRecordRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "";
		if (rowdata.leaveCount > 0) {
			html = "<a class='leave-record-link' href='javascript:showLeaveRecord(\""+row+"\")'><div class='jqx-column align-center leave-statistic'>请假：" + rowdata.leaveCount + "次/共：" + rowdata.totalLeaveDays + "天</div></a>";
		}
		return html;
	};
	//显示评价
	var actorEvaluation = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		if(rowdata.actorId !== null){
			html.push("<div class='viewrole-name-column'>");
			html.push(" <div class='jqx-column float-left actor-name-padd'>"+ rowdata.actorName + "</div>");
			html.push(" <span class='jqx-column operate-btn-list'>");
			html.push("		<a class='float-right viewRole-evaluation' title='' href='javascript:showRoleEvaluation(\""+ row + "\")'>评</a>");
			html.push(" </span>");
			html.push("</div>");
		}
		return html.join("");
	};
	//首次出场
	var seriesViewNo = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column align-left'>"+ rowdata.seriesViewNo +"</div>";
		return html;
	};
	//场
	var viewRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column series-viewno'>"+ rowdata.totalViewCount +"</div>";
		return html;
	};
	//页 
	var pageRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column series-viewno'>"+ rowdata.totalPageCount +"</div>";
		return html;
	};
	//已完成场数
	var finishviewRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column series-viewno'>"+ rowdata.finishedViewCount +"</div>";
		return html;
	};
	//未完成场数
	var unfinishRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='jqx-column series-viewno'>"+ rowdata.unfinishedViewCount +"</div>";
		return html;
	};
	
	
	$("#viewRoleListGrid").jqxGrid({
		width: "100%",
		height: "100%",
		source: dataAdapter,
		showtoolbar: true,
		rowsheight: 30,
		selectionmode: "checkbox",
		localization: localizationobj,
		rendertoolbar: function(toolbar) {
			var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='advance-search-btn' id='advarceSearchBtn' onclick='openAdvanceSearch()'>");
			//权限--不能添加、合并、设置
			if(!isRoleReadonly){
				container.push("<input type='button' class='create-role-btn' id='createRoleBtn' onclick='addViewRoleInfo()'>");
				container.push("<input type='button' class='make-role-toone-btn' id='makeRoleToOneBtn' onclick='showMakeRoleToOne()'>");
				container.push("<div id='setRoleTypeDiv' class='set-roletype-btn set-roletype-div'>");
			
				container.push("<div class='jqx-tree-drop' id='jqxTreeDrop'>");
				container.push("<ul style='display:block;'>");
				container.push("<li item-selected='true' value='1'>主要演员</li>");
				container.push("<li item-expanded='true' value='2'>特约演员</li>");
				container.push("<li item-expanded='true' value='3'>群众演员</li>");
				container.push("<li item-selected='true' value='4'>待定</li>");
				container.push("</ul>");
				container.push("</div>");
				container.push("</div>");
				
				if (hasDeleteViewRoleBatchAuth) {
					container.push("<input type='button' class='delete-view-list' id='deleteViewRoleBatch' onclick='deleteViewRoleBatch()'>");
				}
			}
			container.push("<input type='button' class='export-view-btn' id='exportBtn' onclick='downLoadSence()'>");
			
			container.push("<input type='button' class='export-btn' id='exportRoleBtn' onclick='downLoadRoleTab()'>");
			container.push("</div>");
			
			toolbar.append($(container.join("")));
			
		 	
			
			//权限设置不能设置角色类型
			if(!isRoleReadonly){
				
				var screenWidth = window.screen.width;//屏幕分辨率(使其自适应)
				if(screenWidth >= 1366 && screenWidth <= 1399){
					$("#setRoleTypeDiv").jqxDropDownButton({theme:theme, height: 20, width: 20});
					//改动插件
					$("#dropDownButtonArrowsetRoleTypeDiv").jqxDropDownButton({ width: '20px'});
				}else{
					$("#setRoleTypeDiv").jqxDropDownButton({theme:theme, height: 24, width: 24});
					//改动插件
					$("#dropDownButtonArrowsetRoleTypeDiv").jqxDropDownButton({ width: '24px'});
				}
				
				$("#jqxTreeDrop").jqxTree({theme:theme, width: 100});
				$('#jqxTreeDrop').click('select', setRoleType);
			}
			$("#advarceSearchBtn").jqxTooltip({content: "高级搜索", position: "bottom"});
			//权限--不能添加、合并、设置
			if(!isRoleReadonly){
				$("#createRoleBtn").jqxTooltip({content: "创建角色", position: "bottom"});
				$("#makeRoleToOneBtn").jqxTooltip({content: "统一角色名称", position: "bottom"});			
				$("#setRoleTypeDiv").jqxTooltip({content: "设置角色类型", position: "bottom"});
			}
			$("#exportBtn").jqxTooltip({content: "导出角色场景表", position: "bottom"});
			$("#exportRoleBtn").jqxTooltip({content: "导出角色表", position: "bottom"});
			
			if (hasDeleteViewRoleBatchAuth) {
				$("#deleteViewRoleBatch").jqxTooltip({content: "批量删除", position: "bottom"});
			}
		} ,
		cellhover: function(e) {
			var $this = $(e);
			$this.siblings().addClass("jqx-fill-state-hover").parent().siblings().children().removeClass("jqx-fill-state-hover");
			
			//显示操作按钮
			$(".operate-btn-list").hide();
			$this.parent().find(".operate-btn-list").show();
		},
		columns: [
			{text: "角色名称", cellsrenderer: viewRoleNameRenderer, width: '20%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "简称", datafield: 'shortName', width: '4%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "演员类型", cellsrenderer: viewRoleTypeRenderer, width: '5%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "演员姓名", cellsrenderer: actorEvaluation, width: '8%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "首次出场", cellsrenderer: seriesViewNo, width: '6%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "场", datafield: 'totalViewCount', cellsrenderer:viewRenderer, width: '6%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "页", datafield: 'totalPageCount', cellsrenderer:pageRenderer, width: '6%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "已完成场数", datafield: 'finishedViewCount', cellsrenderer:finishviewRenderer, width: '7%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "未完成场数", datafield: 'unfinishedViewCount', cellsrenderer:unfinishRenderer, width: '7%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "入组时间", datafield: 'enterDate', width: '8%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "离组时间", datafield: 'leaveDate', width: '8%', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "请假记录", cellsrenderer: leaveRecordRenderer, width: '13%', cellsAlign: 'center', align: 'center', sortable: false}
		],
		rendered: function() {
			$("div[id^='row']").mouseout(function() {
				$(this).children().removeClass("jqx-fill-state-hover");
				$(".operate-btn-list").hide();
			});
		}
	});
		
}

/*//显示角色场景表
function showRoleViewList(editRow) {
	$("#actroCeneWindow").jqxWindow("open");
	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editRow);
	viewRoleId = dataRecord.viewRoleId;
	viewRoleName = dataRecord.viewRoleName;
	$('#fViewList').attr('src','/viewRole/toRoleViewListPage?roles=' + viewRoleId+'&viewRoleName=' + viewRoleName);
}*/

//初始化角色场景表
function initRoleViewList(){
	$("#actroCeneWindow").jqxWindow({
		theme: theme,
		height: 700,
		width: 1300,
		maxWidth: 1366,
		maxHeight: 768,
		resizable: false,
		isModal: true,
		autoOpen: false,
	});
}

//显示角色场景统计窗口--戏量统计
function showRoleViewPlayStat(editrow) {
	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editrow);
	$("#roleViewPlayStat").animate({"right": '0px'}, 300).show();
	if($("#tab_3_viewGrid").hasClass("tab_li_current")){//条件成立加载角色场景表
		//加载角色场景表
		$('#fViewList').attr('src','/viewRole/toRoleViewListPage?roles=' + dataRecord.viewRoleId +'&viewRoleName=' + dataRecord.viewRoleName);
	}
	
	$("#viewRoleGridId").val(dataRecord.viewRoleId);
	$("#viewRoleGridName").val(dataRecord.viewRoleName);

	
	//表格加载数据
	$.ajax({
		url: '/viewRole/queryViewCountStatistic',
		type: 'post',
		data: {"viewRoleId":dataRecord.viewRoleId},
		datatype: 'json',
		success: function(response){
			
		  if(response.success){
			//演员参演集数
			$(".actorNumber").html(response.serieStatInfo.roleSeriesNum+"/"+response.serieStatInfo.totalSeriesNum);
			var data = "<table class='table-play-count' width='100%' border='1px'>";
			
			for(var i=0; i<response.viewStatGrpByShootLocationList.length; i++){
				data +="	<tr>";
				data+="		<td rowspan='"+(response.viewStatGrpByShootLocationList[i].viewLocationStatList.length)+"' cellspan='0' align='left' class='table-left-td'>"+response.viewStatGrpByShootLocationList[i].shootLocation;
				data+="		<br><br>场数："+response.viewStatGrpByShootLocationList[i].finishedViewCount+"/"+response.viewStatGrpByShootLocationList[i].viewCount+"&nbsp;&nbsp;&nbsp;&nbsp;页数："+response.viewStatGrpByShootLocationList[i].finishedPageCount+"/"+response.viewStatGrpByShootLocationList[i].totalPageCount;
				data+="		<br>气氛："+response.viewStatGrpByShootLocationList[i].atmosphere+"</td>";
				for(var j=0;j<(response.viewStatGrpByShootLocationList[i].viewLocationStatList.length);j++){
					if(j>0){
					    data+="<tr>";
					}
					data+="<td align='left'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].location+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].finishedViewCount+"/"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].viewCount+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].finishedPageCount+"/"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].totalPageCount+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].atmosphere+"</td>";
					data+="</tr>";
				}
			}
			data+="</table>";
			$("#playCountDetail").html(data);
			
			var roleViewDetail = "";
			var rolePageDetail = "";
			roleViewDetail+="&nbsp;"+response.generalStatistic.viewRoleName+"&nbsp;|"+"&nbsp;演员:&nbsp;&nbsp;"+response.generalStatistic.actorName+"&nbsp;|";
			roleViewDetail+="&nbsp;场数:&nbsp;&nbsp;"+response.generalStatistic.finishedTotalViewCount+"/"+response.generalStatistic.totalViewCount+"&nbsp;|";
			roleViewDetail+="&nbsp;主场景数:&nbsp;&nbsp;"+response.generalStatistic.viewLocationCount+"&nbsp;&nbsp;<br>";			
			/*roleViewDetail += "&nbsp;内戏：" + response.generalStatistic.finishedInsideViewCount + "/" + response.generalStatistic.insideViewCount + "&nbsp;外戏："+response.generalStatistic.finishedOutsideViewCount+"/"+response.generalStatistic.outsideViewCount+"&nbsp;|";
			roleViewDetail += "&nbsp;日戏：" + response.generalStatistic.finishedDayViewCount + "/" + response.generalStatistic.dayViewCount + "&nbsp;夜戏："+response.generalStatistic.finishedNightViewCount+"/"+response.generalStatistic.nightViewCount+"&nbsp;|";
			roleViewDetail += "&nbsp;文戏：" + response.generalStatistic.finishedLiterateViewCount + "/" + response.generalStatistic.literateViewCount + "&nbsp;武戏："+response.generalStatistic.finishedKungFuViewCount+"/"+response.generalStatistic.kungFuViewCount;*/
			rolePageDetail+="&nbsp;"+response.generalStatistic.viewRoleName+"&nbsp;|"+"&nbsp;演员:&nbsp;&nbsp;"+response.generalStatistic.actorName+"&nbsp;|";
			rolePageDetail+="&nbsp;页数:&nbsp;&nbsp;"+response.generalStatistic.finishedTotalPageCount+"/"+response.generalStatistic.totalPageCount+"&nbsp;|";
			rolePageDetail+="&nbsp;主场景数:&nbsp;&nbsp;"+response.generalStatistic.viewLocationCount+"&nbsp;&nbsp;<br>";			
			/*rolePageDetail += "&nbsp;内戏：" + response.generalStatistic.finishedInsidePageCount + "/" + response.generalStatistic.insidePageCount + "&nbsp;外戏："+response.generalStatistic.finishedOutsidePageCount+"/"+response.generalStatistic.outsidePageCount+"&nbsp;|";
			rolePageDetail += "&nbsp;日戏：" + response.generalStatistic.finishedDayPageCount + "/" + response.generalStatistic.dayPageCount + "&nbsp;夜戏："+response.generalStatistic.finishedNightPageCount+"/"+response.generalStatistic.nightPageCount+"&nbsp;|";
			rolePageDetail += "&nbsp;文戏：" + response.generalStatistic.finishedLiteratePageCount + "/" + response.generalStatistic.literatePageCount + "&nbsp;武戏："+response.generalStatistic.finishedKungFuPageCount+"/"+response.generalStatistic.kungFuPageCount;*/
			
			$("#viewspan").html(roleViewDetail);
			$("#pagespan").html(rolePageDetail);
			
			Number.prototype.toFixed2=function (){
				return parseFloat(this.toString().replace(/(\.\d{2})\d+$/,"$1"));
			};
			/*进度条显示*/
			  /*按场景统计*/
			//场数进度条
			$("#viewCount").html("场数:&nbsp;" + response.generalStatistic.finishedTotalViewCount+"/"+response.generalStatistic.totalViewCount);
			//设置进度条进度的方法
			viewProgressBar($("#viewCountProgress"), $("#viewCountFlag"), response.generalStatistic.totalViewCount, response.generalStatistic.finishedTotalViewCount);
			
			 //内外进度条
			 $("#staticView").html("内戏:&nbsp;" + response.generalStatistic.finishedInsideViewCount + "/" + response.generalStatistic.insideViewCount);
			 $("#outsideView").html("外戏:&nbsp;" + response.generalStatistic.finishedOutsideViewCount+"/"+response.generalStatistic.outsideViewCount);
			 
			 twoContrastProgressBar($("#neiProgress"), $("#waiProgress"), $("#neiCountFlag"), $("#waiCountFlag"), response.generalStatistic.insideViewCount, response.generalStatistic.outsideViewCount, response.generalStatistic.finishedInsideViewCount, response.generalStatistic.finishedOutsideViewCount);
			 
			 
			 //日夜进度条
			
			 $("#dayView").html("日戏:&nbsp;" + response.generalStatistic.finishedDayViewCount + "/" + response.generalStatistic.dayViewCount);
			 $("#nightView").html("夜戏:&nbsp;" + response.generalStatistic.finishedNightViewCount+"/"+response.generalStatistic.nightViewCount);
			 twoContrastProgressBar($("#dayProgress"), $("#nightProgress"), $("#dayCountFlag"), $("#nightCountFlag"), response.generalStatistic.dayViewCount, response.generalStatistic.nightViewCount, response.generalStatistic.finishedDayViewCount, response.generalStatistic.finishedNightViewCount);
			 
			 
			 //文武戏进度条
			 
			 $("#literateView").html("文戏:&nbsp;" + response.generalStatistic.finishedLiterateViewCount + "/" + response.generalStatistic.literateViewCount);
			 $("#kongfuView").html("武戏:&nbsp;" + response.generalStatistic.finishedKungFuViewCount +"/"+response.generalStatistic.kungFuViewCount);
			 twoContrastProgressBar($("#wenProgress"), $("#wuProgress"), $("#wenCountFlag"), $("#wuCountFlag"), response.generalStatistic.literateViewCount, response.generalStatistic.kungFuViewCount, response.generalStatistic.finishedLiterateViewCount, response.generalStatistic.finishedKungFuViewCount);
			 
			 
			 
			 
			 /*进度条显示*/
			    /*按页数统计*/
			 $("#viewPage").html("页数:&nbsp;" + response.generalStatistic.finishedTotalPageCount+"/" + response.generalStatistic.totalPageCount);
			 //页数进度条
			 viewProgressBar($("#viewPageProgress"), $("#viewPageFlag"), response.generalStatistic.totalPageCount, response.generalStatistic.finishedTotalPageCount);
			 
			 
			 /*内外景*/
			 $("#staticPage").html("内戏:&nbsp;" + response.generalStatistic.finishedInsidePageCount + "/" + response.generalStatistic.insidePageCount);
			 $("#outsidePage").html("外戏:&nbsp;" + response.generalStatistic.finishedOutsidePageCount+"/"+response.generalStatistic.outsidePageCount);
			 
			 twoContrastProgressBar ($("#neiPageProgress"), $("#waiPageProgress"), $("#neiPageFlag"), $("#waiPageFlag"), response.generalStatistic.insidePageCount, response.generalStatistic.outsidePageCount, response.generalStatistic.finishedInsidePageCount, response.generalStatistic.finishedOutsidePageCount);
			 
			//日夜进度条
			
			 $("#dayPage").html("日戏:&nbsp;" + response.generalStatistic.finishedDayPageCount + "/" + response.generalStatistic.dayPageCount);
			 $("#nightPage").html("夜戏:&nbsp;" + response.generalStatistic.finishedNightPageCount+"/"+response.generalStatistic.nightPageCount); 
			 
			 twoContrastProgressBar ($("#dayPageProgress"), $("#nightPageProgress"), $("#dayPageFlag"), $("#nightPageFlag"), response.generalStatistic.dayPageCount, response.generalStatistic.nightPageCount, response.generalStatistic.finishedDayPageCount, response.generalStatistic.finishedNightPageCount);
			 
			 /*文武进度条*/
			 
			 $("#literatePage").html("文戏:&nbsp;" + response.generalStatistic.finishedLiteratePageCount + "/" + response.generalStatistic.literatePageCount);
			 $("#kongfuPage").html("武戏:&nbsp;" + response.generalStatistic.finishedKungFuPageCount+"/"+response.generalStatistic.kungFuPageCount);
			 twoContrastProgressBar ($("#wenPageProgress"), $("#wuPageProgress"), $("#wenPageFlag"), $("#wuPageFlag"), response.generalStatistic.literatePageCount, response.generalStatistic.kungFuPageCount, response.generalStatistic.finishedLiteratePageCount, response.generalStatistic.finishedKungFuPageCount);
			 
			
			//按场景统计
			var seriesViewCountMap=response.serieStatInfo.seriesViewCountMap;
			var map=seriesViewCountMap;
			//集数
			var collection1=[];
			//场数
			var fields=[];
			for(var key in map){
				collection1.push(key+"集");
				fields.push(map[key]);
			}
			
			
			//echar显示数据
			  var myChart1=echarts.init(document.getElementById("echarPlayView"));
			  var option={
					    color: ['#ff7f50'],
					    tooltip : {
					        trigger: 'axis'
					    },
					    xAxis : [
					        {
					        	data : collection1
					        }
					    ],
					    yAxis : [
					        {
					            type : 'value'
					        }
					    ],
					    series : [
					        {
					            name:'场',
					            type:'bar',	
					            data : fields
					        }			        
					    ]
			  };
			  myChart1.setOption(option);
			 
			  //按页数统计
			  var seriesPageCountMap=response.serieStatInfo.seriesPageCountMap;
				var map=seriesPageCountMap;
				//集数
				var collection2=[];
				//页数
				var pages=[];
				for(var key in map){
					collection2.push(key+"集");
					pages.push(map[key]);
				}
				
				
				//echar显示数据
				  var myChart2=echarts.init(document.getElementById("echarPlayPage"));
				  var option={
						    color: ['#ff7f50'],
						    tooltip : {
						        trigger: 'axis'
						    },
						    xAxis : [
						        {
						        	data : collection2
						        }
						    ],
						    yAxis : [
						        {
						            type : 'value'
						        }
						    ],
						    series : [
						        {
						            name:'页',
						            type:'bar',	
						            data : pages
						        }			        
						    ]
				  };
				  myChart2.setOption(option);
			}else{
				showErrorMessage(response.message);
			}
		}
		
	});	                
}

//初始化角色场景统计窗口
function initRoleViewPlayStat(){
	$("#roleViewPlayStat").jqxWindow({
		theme: theme,
		height: 800,
		width: 1000,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			initRoleViewPlayStatClick();
		}
	});
}
//初始化角色场景统计tab、radio click事件
function initRoleViewPlayStatClick(){
	$("#playGeneralDetail").show();
	//tab键菜单--戏量统计
	$("#tab_1").click(function(){
		
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").show();
		$(".danju").hide();
		$(".danju1").show();
	});
		
	$("#tab_2").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").show();
		$(".danju").hide();
		$(".danju2").show();
	});
	
	$("#tab_3_viewGrid").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").hide();
		$(".danju").hide();
		$("#viewGridPage").show();
		var viewRoleId = $("#viewRoleGridId").val();
		var viewRoleName = $("#viewRoleGridName").val();
		//初始化加载角色场景表
		$('#fViewList').attr('src','/viewRole/toRoleViewListPage?roles=' + viewRoleId +'&viewRoleName=' + viewRoleName);
		
	});
	
	//按场或页统计单选按钮
	$("#radio_1").click(function(){
		$("#echarPlayView").show();
		$("#echarPlayPage").hide();
	});
	$("#radio_2").click(function(){
		$("#echarPlayPage").show();
		$("#echarPlayView").hide();
	});
}

function showRoleTotal(id){
	if(id==1){
		$("#viewspan").show();
		$("#accountTheView").show();
		$("#pagespan").hide();
		$("#accountThePage").hide();
		$("#echarPlayView").show();
		$("#echarPlayPage").hide();
	}else{
		$("#viewspan").hide();
		$("#accountTheView").hide();
		$("#pagespan").show();
		$("#accountThePage").show();
		$("#echarPlayView").hide();
		$("#echarPlayPage").show();
	}
}

//显示删除角色场景弹窗
function deleteViewRoleInfo(editrow){
//	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editrow);
	popupPromptBox("提示","确定删除吗？",function(){
		$.ajax({
			url: '/viewRole/deleteViewRoleInfo',
			type: 'post',
			data: {viewRoleId:editrow},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					//$("#viewRoleListGrid").jqxGrid("updatebounddata");
					var rowId = $('#viewRoleListGrid').jqxGrid('getrowid', editrow);
					$('#viewRoleListGrid').jqxGrid('deleterow', rowId);
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	});
}

//显示演员请假列表
function showLeaveRecord(editrow) {
	//获取当前日期
	var date = new Date();
	var str = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editrow);
	$("#leaveStartDate").val(str);
	$("#leaveEndDate").val(str);
	$("#actorId").val(dataRecord.actorId);
	
	if(dataRecord.actorId===null){
		showInfoMessage("请为角色名称选择演员");
	}else{
		$("#setLeaveRecord").jqxWindow("open");
		
		askForLeave(dataRecord.actorId);
	}
}
//初始化演员请假列表
function initLeaveRecord(){
	$("#setLeaveRecord").jqxWindow({
		theme: theme,
		height: 420,
		width: 640,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}


//显示演员评价
function showRoleEvaluation(editrow){
	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editrow);
	//初始化
	$("#actorIdEvaluate").val(dataRecord.actorId);
	$(".grade-star>li").removeClass('full-star');
	$(".grade-star>li").removeClass('half-star');
	$(".star-info").css("display","none");
	$(".grade-df").text("0");
	$("input[type=checkbox]").attr("checked",false);
	$(".grade-py").val("");
	$("#actroEvaluateWindow").jqxWindow("open");	
}

//初始化演员评价
function initRoleEvaluation(){
	$("#actroEvaluateWindow").jqxWindow({
		theme: theme,
		height: 430,
		width: 780,
		resizable: false,
		isModal: true,
		autoOpen: false,
		cancelButton: $("#actorEvaluateCencle"),
		initContent: function(){
			//获取演员评价标签
			$.ajax({
			    	url:'/evaluateManager/queryEvaluateTagList',
			    	type: 'post',
			    	datatype: 'json',
			    	success: function(response){
			    		if(response.success){
			    			var htmlRed="<li></li>";
			    			var htmlBlack="<li></li>";
		                    for(var i=0;i<response.redTagList.length;i++){
		                    	htmlRed+="<li><input type='checkbox' name='best' id='"+response.redTagList[i].tagId+"'><span>"+response.redTagList[i].tagName+"</span></li>";
		                    }
		                    $(".best").html(htmlRed);
		                    for(var j=0;j<response.blackTagList.length;j++){
		                    	htmlBlack+="<li><input type='checkbox' name='bad' id='"+response.blackTagList[j].tagId+"'><span>"+response.blackTagList[j].tagName+"</span></li>";
		                    }
		                    $(".bad").html(htmlBlack);
			    		}
			    	}
			    });
		}
	});
}

//显示高级查询面板
function openAdvanceSearch() {
	$("#advanceSearchWin").jqxWindow("open");
}

//初始化高级查询窗口
function initAdvanceSearch () {
	$("#advanceSearchWin").jqxWindow({
		theme: theme,
		height: 300,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}

//显示创建角色面
function addViewRoleInfo() {
	//修改标题
	$('#viewRoleDetail').jqxWindow('setTitle', '创建角色');
	//清空内容
	$("#viewRoleId").val("");
	$("#viewRoleName").val("");
	$("#shortName").val("");
	$("#viewRoleType").val("");
	$("#actorName").val("");
	$("#enterDate").val("");
	$("#leaveDate").val("");	
	//清空样式
	$("span.tips1").css("display","none");
	$("#viewRoleName").removeClass("roleNameEmpty");
	$("span.tips2").css("display","none");
	//打开窗口
	$("#viewRoleDetail").jqxWindow("open");
}
//初始化创建角色窗口--修改角色窗口
function initAddViewRoleInfo() {
	//权限--不能修改
	if(isRoleReadonly){
		return false;
	}
	$("#viewRoleDetail").jqxWindow({
		theme: theme,
		height: 385,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}



//显示修改角色窗口--共用创建角色窗口
function modifyViewRoleInfo(editrow){
	//清空样式
	$("span.tips1").css("display","none");
	$("#viewRoleName").removeClass("roleNameEmpty");
	$("span.tips2").css("display","none");
	//修改标题
	$('#viewRoleDetail').jqxWindow('setTitle', '修改角色');
	var dataRecord = $("#viewRoleListGrid").jqxGrid('getrowdata', editrow);
	//先获取信息
	$("#viewRoleName").val(dataRecord.viewRoleName);
	$("#shortName").val(dataRecord.shortName);
	$("#viewRoleType").val(dataRecord.viewRoleType);
	$("#actorName").val(dataRecord.actorName);
	$("#enterDate").val(dataRecord.enterDate);
	$("#leaveDate").val(dataRecord.leaveDate);
	$("#viewRoleId").val(dataRecord.viewRoleId);
	$("#actorId").val(dataRecord.actorId);
	//打开窗口
	$("#viewRoleDetail").jqxWindow("open");
}




//显示合并角色名称窗口
function showMakeRoleToOne() {
	//权限--不能修改
	if(isRoleReadonly){
		return false;
	}
	//判断是否选中角色
	var rowindexes = $("#viewRoleListGrid").jqxGrid("getselectedrowindexes");
	if(rowindexes.length<=1){
		showInfoMessage("请选择要合并的角色名称");
	}else{
		//清空内容
		$("#makeViewRoleName").val("");
		$("#makeShortName").val("");
		$("#makeViewRoleType").val("");
		$("#makeActorName").val("");
		$("#makeEnterDate").val("");
		$("#makeLeaveDate").val("");
		//清空样式
		$("span.tips1").css("display","none");
		$("#makeViewRoleName").removeClass("roleNameEmpty");
		$("span.tips2").css("display","none");
		//打开窗口
		$("#MakeRoleToOne").jqxWindow("open");
	}
}
//初始化合并角色名称窗口
function initMakeRoleToOne() {
	$("#MakeRoleToOne").jqxWindow({
		theme: theme,
		height: 455,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}




//高级查询功能--确定--advanceQuery--
function advanceQuery(){
	var viewRoleName = $("#queryViewRoleName").val();
	var viewRoleType = $("#queryViewRoleType").val();
	var minViewCount = $("#queryMinViewCount").val();
	var maxViewCount = $("#queryMaxViewCount").val();
	subData.viewRoleName = viewRoleName;
	subData.viewRoleType = viewRoleType;
	subData.minViewCount = minViewCount;
	subData.maxViewCount = maxViewCount;
	
//加载高级查询显示的数据
//	loadViewRoleGrid();
	$("#viewRoleListGrid").jqxGrid("updatebounddata");
	$("#advanceSearchWin").jqxWindow("close");
}

//高级查询功能--取消--cancelQuery--
function cancelQuery(){
	$("#advanceSearchWin").jqxWindow("close");
}

//高级查询功能--清空--clearQuery--、
function clearQuery(){
	$("#queryViewRoleName").val("");
	$("#queryViewRoleType").val("");
	$("#queryMinViewCount").val("");
	$("#queryMaxViewCount").val("");
}
//判断场数是否为数字
function isRoleNumber(){
	var minView=$("#queryMinViewCount").val;
	if(isNaN(minView)){
		$("#queryMinViewCount").val("");
	}else{
		$("#queryMinViewCount").val(minView);
	}
}





//创建角色功能--createRole--
  //创建角色--判断角色名称是否为空
function isAddRoleNameEmpty(){
	if($("#viewRoleName").val()===""){		
		$("span.tips1").css("display","block");
		$("#viewRoleName").addClass("roleNameEmpty");
	}	
}
  //创建角色--判断角色类型是否为空
function isAddRoleTypeEmpty(){
	if($("#viewRoleType").val()===""){
		$("span.tips2").css("display","block");
	}
}

//创建角色--清楚提示信息
function clearAddNameTips(){
	$("span.tips1").css("display","none");
	$("#viewRoleName").removeClass("roleNameEmpty");
}
function clearAddTypeTips(){
	$("span.tips2").css("display","none");
}





//修改角色--判断角色名称是否为空
function isModifyRoleNameEmpty(){
	if($("#modifyViewRoleName").val()===""){
		$("span.tips1").css("display","block");
		$("#modifyViewRoleName").addClass("roleNameEmpty");
	}
}
//修改角色--判断角色类型是否为空
function isModifyRoleTypeEmpty(){
	if($("#modifyViewRoleType").val()===""){
		$("span.tips2").css("display","block");
	}
}

//修改角色--清除提示信息
function clearModifyNameTips(){
	$("span.tips1").css("display","none");
	$("#modifyViewRoleName").removeClass("roleNameEmpty");
}
function clearModifyTypeTips(){
	$("span.tips2").css("display","none");
}





//统一角色--判断角色名称是否为空
function isMakeRoleNameEmpty(){
	if($("#makeViewRoleName").val()===""){		
		$("span.tips1").css("display","block");
		$("#makeViewRoleName").addClass("roleNameEmpty");
	}	
}
//统一角色--判断角色类型是否为空
function isMakeRoleTypeEmpty(){
	if($("#makeViewRoleType").val()===""){
		$("span.tips2").css("display","block");
	}
}

//统一角色--清除提示信息
function clearMakeNameTips(){
	$("span.tips1").css("display","none");
	$("#makeViewRoleName").removeClass("roleNameEmpty");
}
function clearMakeTypeTips(){
	$("span.tips2").css("display","none");
}




//创建角色功能
function createRole(own){
	//获取值
	var viewRoleId = $("#viewRoleId").val();
	var viewRoleName = $("#viewRoleName").val();
	var shortName = $("#shortName").val();
	var actorId = $("#actorId").val();
	var viewRoleType = $("#viewRoleType").val();
	var actorName = $("#actorName").val();
	var enterDate = $("#enterDate").val();
	var leaveDate = $("#leaveDate").val();
	//定义存放数据的对象
	var roleData={};
	
	roleData.viewRoleId = viewRoleId;
	roleData.viewRoleName = viewRoleName;
	roleData.shortName = shortName;
	roleData.actorId = actorId;
	roleData.viewRoleType = viewRoleType;
	roleData.actorName = actorName;
	roleData.enterDate = enterDate;
	roleData.leaveDate = leaveDate;
	
	//判断角色名称是否为空
	if(viewRoleName===""){
		$("span.tips1").css("display","block");
		$("#viewRoleName").addClass("roleNameEmpty");
		return false;
	}
	//判断角色类型是否为空
	if(viewRoleType===""){
		$("span.tips2").css("display","block");
		$("#viewRoleType").addClass("roleTypeEmpty");
		return false;
	}
	$(own).attr("disabled", "disabled");
	$.ajax({
		url: '/viewRole/saveViewRoleInfo',
		type: 'post',
		data: roleData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#viewRoleDetail").jqxWindow("close");
				//刷新表格
				$("#viewRoleListGrid").jqxGrid("updatebounddata");
			}else{
				showErrorMessage(response.message);
			}
//			hideDimmer();
			setTimeout(function() {
				$(own).removeAttr("disabled");
			}, 2000);
		}
	});
}
//取消创建角色
function cancelCreateRole(){
	$("#viewRoleDetail").jqxWindow("close");
}




//合并角色名称
function unifiedRoleName(){
	//判断角色名称是否为空
	if($("#makeViewRoleName").val()===""){
		$("span.tips1").css("display","block");
		$("#makeViewRoleName").addClass("roleNameEmpty");
		return false;
	}
	//判断角色类型是否为空
	if($("#makeViewRoleType").val()===""){
		$("span.tips2").css("display","block");
		return false;
	}
	var unifiedData = {};
	var rowindexes = $("#viewRoleListGrid").jqxGrid("getselectedrowindexes");
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < rowindexes.length; i++) {
		var viewRoleId = $("#viewRoleListGrid").jqxGrid("getrowdata", rowindexes[i]).viewRoleId;
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	//获取值
	var viewRoleName = $("#makeViewRoleName").val();
	var shortName = $("#makeShortName").val();
	var viewRoleType = $("#makeViewRoleType").val();
	var actorName = $("#makeActorName").val();
	var enterDate = $("#makeEnterDate").val();
	var leaveDate = $("#makeLeaveDate").val();
	
	unifiedData.viewRoleIds = viewRoleIds;
	unifiedData.viewRoleName = viewRoleName;
	unifiedData.shortName = shortName;
	unifiedData.viewRoleType = viewRoleType;
	unifiedData.actorName = actorName;
	unifiedData.enterDate = enterDate;
	unifiedData.leaveDate = leaveDate;
	
	$.ajax({
		url: '/viewRole/makeRolesToOne',
		type: 'post',
		data: unifiedData,
		datatype: "json",
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#MakeRoleToOne").jqxWindow("close");
				//刷新表格
				$("#viewRoleListGrid").jqxGrid("updatebounddata");
				$('#viewRoleListGrid').jqxGrid('clearselection');
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//取消合并角色名称
function cancelUnifiedRole(){
	$("#MakeRoleToOne").jqxWindow("close");
}




//设置演员类型
function setRoleType(event){
        var item = event.target.innerHTML;
        if(item == "待定") {
        	item = 4;
        } else if (item=='主要演员'){
        	item=1;
        }else if(item=='特约演员'){
        	item=2;
        }else if(item=='群众演员'){
        	item=3;
        }
       var setData = {};
    	var rowindexes = $("#viewRoleListGrid").jqxGrid("getselectedrowindexes");
    	//获取待合并角色的Id
    	var viewRoleIds = "";
    	for(var i = 0; i < rowindexes.length; i++) {
    		var viewRoleId = $("#viewRoleListGrid").jqxGrid("getrowdata", rowindexes[i]).viewRoleId;
    		viewRoleIds += viewRoleId+",";
    	}
    	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
    	setData.viewRoleIds = viewRoleIds;
    	setData.viewRoleType = item;
    	$.ajax({
    		url: '/viewRole/updateViewRoleTypeBatch',
    		type: 'post',
    		data : setData,
    		datatype: 'json',
    		success: function(response){
    			if(response.success){
    				showSuccessMessage("操作成功");
    				//刷新表格
    				$("#viewRoleListGrid").jqxGrid("updatebounddata");
    				$('#viewRoleListGrid').jqxGrid('clearselection');
    				$("#setRoleTypeDiv").jqxDropDownButton('close');
    			}else{
    				showInfoMessage(response.message);
    			}
    		}
    	});
}

//导出角色表
function downLoadRoleTab(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    
    /*实现功能*/
	
	var form = $("<form></form>");
    form.attr("action","/viewRole/queryViewRoleListForExport");
    form.attr("method","post");
    form.submit();
    _LoadingHtml.hide();
    $(".opacityAll").hide();
}


//导出角色场景表
function downLoadSence(){
	var rowindexes = $("#viewRoleListGrid").jqxGrid("getselectedrowindexes");
	if(rowindexes.length<1){
		showInfoMessage("请选择要导出的角色");
		return false;
	}
	
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    
    /*实现功能*/
	var fileAddress;
	var subData={};	
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < rowindexes.length; i++) {
		var viewRoleId = $("#viewRoleListGrid").jqxGrid("getrowdata", rowindexes[i]).viewRoleId;
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	subData.viewRoleIds=viewRoleIds;
	
	$.ajax({
		url: '/viewRole/exportRoleViewList',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			_LoadingHtml.hide();
            $(".opacityAll").hide();
			if (response.success) {
				fileAddress = response.downloadFilePath; 
           }else{
        	   showErrorMessage(response.message);
               return;
           }
			var form = $("<form></form>");
            form.attr("action","/fileManager/downloadFileByAddr");
            form.attr("method","post");
            form.append("<input type='hidden' name='address'>");
            form.find("input[name='address']").val(fileAddress);
            form.submit();
		}
	});
}





//演员请假记录
function askForLeave(actorId){
	    var subData={};
	    subData.actorId=actorId;
        var source1 =
        {
            url: '/viewRole/queryActorOffRecordList',
            type:'post',
            data: subData,
            dataType : "json",
            datafields: [
				{ name: 'id',type: 'string' },
				{ name: 'actorId',type: 'string'},
				{ name: 'leaveStartDate',type: 'string' },
				{ name: 'leaveEndDate',type: 'string' },
				{ name: 'leaveDays',type: 'string' },
				{ name: 'leaveReason',type: 'string'}
            ],
            
            beforeprocessing:function(data){
            	
            },
            root:'leaveRecordList',
            processdata: function (data) {
                //查询之前可执行的代码
            }
        };
        
        var operateRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        	var html = "";
        	if(!isRoleReadonly){
        		html += "<div class='del-leave-div'><a href='javascript:void(0);'";
        		html += " aria-disabled='false' class='' ";
        		html += " onclick='deleteLeaveRecord(\""+rowdata.id+"\")' >删除</a></div>";
        	}
        	return html;
        };
        
        
        var dataAdapter1 = new $.jqx.dataAdapter(source1);
        
        $("#jqxGridLeaveDate").jqxGrid(
        {
        	width: 600,
    		height: 301,
    		source: dataAdapter1,
    		showtoolbar: false,
    		localization: localizationobj,
            columns: [
              { text: '开始时间',  datafield: 'leaveStartDate', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '结束时间', datafield: 'leaveEndDate', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '请假天数', datafield: 'leaveDays', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '操作',width: '25%',align:'center', cellsrenderer: operateRenderer}
            ]
        });
       }

//添加请假记录
function addLeaveDate(){
	//权限设置不可以添加请假记录
	if(isRoleReadonly){
		return false;
	}
	var subData={};
	subData.actorId=$("#actorId").val();
	subData.leaveStartDate=$("#leaveStartDate").val();
	subData.leaveEndDate=$("#leaveEndDate").val();
	
	$.ajax({
		url: '/viewRole/saveActorLeaveRecord',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#jqxGridLeaveDate").jqxGrid("updatebounddata");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	//请假设置关闭事件
	$("#setLeaveRecord").on("close", function (event) {
		$("#viewRoleListGrid").jqxGrid("updatebounddata");
	 });
}

//删除请假记录
function deleteLeaveRecord(id){
	//权限设置不可以删除请假记录
	if(isRoleReadonly){
		return false;
	}
	popupPromptBox("提示","确定要删除此条请假记录么？",function(){
	var subData = {};
	subData.id = id;
	
		$.ajax({
			url: '/viewRole/deleteActorLeaveRecord',
			type: 'post',
			data: subData,
			datatype: 'json',
			success: function(){
				$("#jqxGridLeaveDate").jqxGrid("updatebounddata");
			}
		});
	});
	//请假设置关闭事件
	$("#setLeaveRecord").on("close", function (event) {
		$("#viewRoleListGrid").jqxGrid("updatebounddata");
	 });
}

//演员评价
function actorEvaluateButton(){
	var actorId = $("#actorIdEvaluate").val();
	var spCodesTemp = "";
    $('.content input[type=checkbox]:checked').each(function(i){
    	  if(0==i){
	        spCodesTemp = $(this).attr("Id");
	      }else{
	        spCodesTemp += (","+$(this).attr("Id"));
	      }
    }); 
    
    var score=$(".grade-df").text();//得分
    var subData={};
    subData.actorId=actorId;
    subData.score=score;
    subData.evatagIds=spCodesTemp;
    subData.comment=$(".grade-py").val();
    
    $.ajax({
    	url: '/evaluateManager/evaluateActor',
    	type: 'post',
    	data: subData,
    	datatype: 'json',
    	success: function(response){
    		if(response.success){
    			showSuccessMessage("操作成功");
    			$("#actroEvaluateWindow").jqxWindow("close");
    		}else{
    			showErrorMessage("评价失败，请联系管理员！");
    		}
    	}
    });
}

//批量删除角色
function deleteViewRoleBatch() {
	var rowindexes = $("#viewRoleListGrid").jqxGrid("getselectedrowindexes");
	if(rowindexes.length < 1){
		showInfoMessage("请选择要删除的角色");
		return false;
	}
	
	var subData = {};	
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < rowindexes.length; i++) {
		var viewRoleInfo = $("#viewRoleListGrid").jqxGrid("getrowdata", rowindexes[i]);
		
		var totalViewCount = viewRoleInfo.totalViewCount;
		var viewRoleType = viewRoleInfo.viewRoleType;
		var viewRoleId = viewRoleInfo.viewRoleId;
		
		if (viewRoleType != 4 && totalViewCount != 0) {
			showErrorMessage("所选角色中存在有戏量的非待定角色");
			return false;
		}
		
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	subData.viewRoleIds = viewRoleIds;
	
	$.ajax({
		url: "/viewRole/deleteViewRoleInfoBatch",
		type: "post",
		dataType: "json",
		data: subData,
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
			}
			//刷新表格
			$("#viewRoleListGrid").jqxGrid("updatebounddata");
			$('#viewRoleListGrid').jqxGrid('clearselection');
			showSuccessMessage("删除成功");
		}
	});
}
//关闭右侧弹出层
function closeRightPopup(){
	var hiddenWidth = $("#roleViewPlayStat").width();//要隐藏的元素的宽度
	clearInterval(timer);
	$("#roleViewPlayStat").animate({"right": 0 - hiddenWidth}, 300);
	var timer = setTimeout(function(){
		$("#roleViewPlayStat").hide();
	}, 300);
	
}