$(document).ready(function () {
	//加载项目信息列表
	loadProjectList();
	//tab页切换事件
	$(".tab_wrap li").click(function(){
		if(!$(this).hasClass('tab_li_current')) {
			$(this).siblings().removeClass('tab_li_current');
			$(this).addClass('tab_li_current');
			var obj=$(".main-content").children().eq($(this).index());
			$(obj).siblings().hide();
			$(obj).show();
		}
	});
});
//加载项目信息列表
function loadProjectList(){
	var source = {
		url: '/projectManager/queryProjectList',
		type: "post",
		dataType : "json",
		data: {},
		datafields : [
		    { name: 'crewId',type: 'string' },//项目ID
			{ name: 'company',type: 'int' },//公司
			{ name: 'crewName',type: 'string' },//项目名称
			{ name: 'recordNumber',type: 'string' },//广电备案
	        { name: 'seriesNo',type: 'int' },//立项集数
	        { name: 'coProduction',type: 'int' },//合拍协议
	        { name: 'coProMoney',type: 'double' },//合拍协议金额
	        { name: 'budget',type: 'double' },//剧组执行预算
	        { name: 'investmentRatio',type: 'double' },//我方投资比例
	        { name: 'shootStartDate',type: 'date' },//开拍时间
	        { name: 'shootEndDate',type: 'date' },//预计杀青时间
	        { name: 'days',type: 'int' },//预计拍摄天数
	        { name: 'finishedDays',type: 'int' },//实际已拍摄天数
	        { name: 'remainingDays',type: 'int' },//剩余拍摄天数/(超期天数)
	        { name: 'status',type: 'int' },//目前状态
	        { name: 'remark',type: 'string' },//重要事项说明及重要情况预警
	        { name: 'lastRemark',type: 'string' }//前次重要事项说明及重要情况预警
		],
		root: 'result'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) {
        	if(!data.success) {
        		showErrorMessage(data.message);
        	}
        },
        loadError: function (xhr, status, error) { }
    });
	var gridcolumns=[
				    { text: '公司',datafield:'company', width: '15%', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							return '<span class="jqxgrid-column text-align-left" title="'+value+'">'+value+'</span>';
			         	}
				    },
				    { text: '项目名称',datafield:'crewName', width: '15%', cellsAlign: 'center', align: 'center',enabletooltips:true,
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		        		  	var html= [];
							html.push("<span class='jqxgrid-column text-align-left' title='《"+value+"》'>");
							html.push("	<a href='javascript:showProjectDetail(\""+rowdata.crewId+"\")'>《" + value + "》</a>");
							html.push("</span>");
							return html.join("");
			         	}
				    },
				    { text: '广电备案',datafield:'recordNumber', width: '160px', cellsAlign: 'left', align: 'center',enabletooltips:true },
				    { text: '立项集数',datafield:'seriesNo', width: '65px', cellsAlign: 'center', align: 'center' },
				    { text: '合拍协议',datafield:'coProduction', width: '65px', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							var coProduction=rowdata.coProduction;
							var str='';
							switch(coProduction){
							  	case 0:
							  		str='无';
							  		break;
								case 1:
									str='已签订';
								  	break;
							}
							return '<span class="jqxgrid-column text-align-center">'+str+'</span>';
			         	}
				    },
				    { text: '合拍协议金额',datafield:'coProMoney', width: '105px', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			        		var coProMoney=rowdata.coProMoney;
			        		var coProduction=rowdata.coProduction;
			        		if(coProMoney) {
			        			value=fmoney(coProMoney);
			        		}else if(coProduction=='0'){
			        			value='-';
			        		}
			         		return '<span class="jqxgrid-column text-align-center">'+value+'</span>';
			         	}
				    },
				    { text: '剧组执行预算',datafield:'budget', width: '105px', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			        		var budget=rowdata.budget;
			        		if(budget) {
			        			value=fmoney(budget);
			        		}
			         		return '<span class="jqxgrid-column text-align-center">'+value+'</span>';
			         	}
			        },
				    { text: '我方投资比例',datafield:'investmentRatio', width: '90px', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			        		var investmentRatio=rowdata.investmentRatio;
			        		if(investmentRatio) {
			        			value=fmoney(investmentRatio)+'%';
			        		}
			         		return '<span class="jqxgrid-column text-align-center">'+value+'</span>';
			         	}
			        },
				    { text: '开拍时间',datafield:'shootStartDate', width: '95px', cellsAlign: 'center', align: 'center', cellsformat: 'yyyy-MM-dd' },
				    { text: '预计杀青时间',datafield:'shootEndDate', width: '95px', cellsAlign: 'center', align: 'center', cellsformat: 'yyyy-MM-dd' },
				    { text: '预计拍摄天数',datafield:'days', width: '95px', cellsAlign: 'center', align: 'center' },
				    { text: '实际已拍摄天数',datafield:'finishedDays', width: '105px', cellsAlign: 'center', align: 'center' },
				    { text: '剩余拍摄天数/(超期天数)',datafield:'remainingDays', width: '160px', cellsAlign: 'center', align: 'center' },
				    { text: '目前状态',datafield:'status', width: '80px', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							var status=rowdata.status;
							var str='';
							switch(status){
							  	case 1:
							  		str='筹备中';
							  		break;
								case 2:
									str='拍摄中';
									break;
								case 3:
									str='后期制作中';
									break;
								case 4:
									str='已完成';
									break;
								case 5:
									str='播出中';
									break;
								case 6:
									str='暂停';
							  		break;
							}
							return '<span class="jqxgrid-column text-align-left">'+str+'</span>';
			         	}
				    },
				    { text: '更新重要事项说明及重要情况预警',datafield:'remark', width: '15%', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							return '<span class="jqxgrid-column text-align-left" title="'+value+'">'+value+'</span>';
			         	}
				    },
				    { text: '前次重要事项说明及重要情况预警',datafield:'lastRemark', width: '15%', cellsAlign: 'center', align: 'center',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							return '<span class="jqxgrid-column text-align-left" title="'+value+'">'+value+'</span>';
			         	}
				    }
				];
	$("#projectListDiv").jqxGrid({
		width: "100%",
		height: "100%",
		source: dataAdapter,
		columns:gridcolumns,
		showToolbar: true,
		columnsResize: true,
		columnsHeight:35,
		rowsHeight:35,
		toolbarHeight: 35,
		localization: localizationobj,
		rendertoolbar: function(toolbar) {							
			var container = [];
			container.push("<div class='toolbar'>");
			//判断权限
			container.push("<input type='button' class='export-btn' id='exportBtn' onclick='exportProjectInfo()'>");
			container.push("</div>");
			toolbar.append($(container.join("")));
			$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
		}
	});
}
//导出项目信息
function exportProjectInfo() {
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");			    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	
    window.location.href="/projectManager/exprotProjectList";
    _LoadingHtml.hide();
	$(".opacityAll").hide();
}
//显示项目明细情况
function showProjectDetail(id) {
	$(".tab_wrap li").eq(0).click();
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	loadProjectBudgetPayed(id);
	loadTotalInfo(id);
	loadProductionSchedule(id);
}
//关闭右侧滑出框
function closeRightWin() {
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
}
//加载项目预算支出汇总信息
function loadProjectBudgetPayed(id) {
	var source = {
			url: '/projectManager/queryFinanceBudgetPayed',
			type: 'post',
			datatype: 'json',
			data: {crewId:id},
			dataFields: [
			    {name: 'financeSubjId', type: 'string'},
			    {name: 'financeSubjName', type: 'string'},
			    {name: 'financeSubjParentId', type: 'string'},
			    {name: 'remark', type: 'string'},
			    {name: 'hasChildren', type: 'boolean'},
			    {name: 'totalBadgetMoney', type: 'string'},
			    {name: 'totalPayedMoney', type: 'string'},
			    {name: 'totalLeftMoney', type: 'string'},
			    {name: 'totalPayedRate', type: 'string'}
			    
			], 
			 hierarchy: {
					keyDataField: {name:'financeSubjId'},
					parentDataField: {name:'financeSubjParentId'}
			 },
			 id: 'financeSubjId'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var balanceRenderer = function(row, column, value, rowData){
		if(genOriginalMoney(value) < 0){
			return "<span class='jqxTreeGrid-column text-align-right red-color' title='"+ value +"'>" + value + "</span>";
		}
		return "<span class='jqxTreeGrid-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	
	var rateRenderer = function(row, column, value, rowData) {
		if(genOriginalRate(value) > 100){
			return "<span class='jqxTreeGrid-column text-align-right red-color' title='"+ value +"'>" + value + "</span>";
		}
		return "<span class='jqxTreeGrid-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	var cellsTitle = function(row, column, value, rowData){
		return "<span class='jqxTreeGrid-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	
	var columnInformation = [];
	columnInformation.push({text: '财务科目', datafield: 'financeSubjName', width: '20%', cellsAlign: 'left', align: 'center'});
	columnInformation.push({text: '总预算', datafield: 'totalBadgetMoney', cellsrenderer: cellsTitle, width: '20%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '总支出', datafield: 'totalPayedMoney', cellsrenderer: cellsTitle, width: '20%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '总结余', datafield: 'totalLeftMoney', cellsrenderer: balanceRenderer, width: '20%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '支出比例', datafield: 'totalPayedRate', cellsrenderer: rateRenderer, width: '20%', cellsAlign: 'right', align: 'center'});
	
	$("#budgetPayedDiv").remove();
	$("#budgetPayedTotal").before('<div id="budgetPayedDiv"></div>');
	$("#budgetPayedDiv").jqxTreeGrid({
		width: '100%',
		height: 'calc(100% - 35px)',
		source: dataAdapter,
		showToolbar: false,
		localization: localizationobj,
		rendertoolbar: function(toolbar) {
		},
		columns: columnInformation
	});
	
	$('#budgetPayedDiv').on('bindingComplete', function (event){
		$("#budgetPayedDiv").jqxTreeGrid("expandAll");
	});
}
//结算汇总统计信息
function loadTotalInfo(id){
	$.ajax({
		url: '/projectManager/queryTotalBudgetPayed',
		type: 'post',
		data: {crewId:id},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var html = [];
				html.push("<table class='settle-account-table' cellspacing=0 cellpadding=0>");
				html.push("<tr>");
				html.push("<td width='20%'><div>合计</div></td>");
				html.push("<td width='20%'><div class='text-align-right'>" + response.totalBudgetMoney + "</div></td>");
				html.push("<td width='20%'><div class='text-align-right'>" + response.totalPayedMoney + "</div></td>");
				if (genOriginalMoney(response.totalLeftMoney) >= 0) {
					html.push("<td width='20%'><div class='text-align-right'>" + response.totalLeftMoney + "</div></td>");
				} else {
					html.push("<td width='20%'><div class='text-align-right red-color'>" + response.totalLeftMoney + "</div></td>");
				}
				if (genOriginalRate(response.totalPayedRate) <= 100) {
					html.push("<td width='20%'><div class='text-align-right'>" + response.totalPayedRate + "</div></td>");
				} else {
					html.push("<td width='20%'><div class='text-align-right red-color'>" + response.totalPayedRate + "</div></td>");
				}				
				
				html.push("</tr>");
				html.push("</table>");
				
				$("#budgetPayedTotal").html(html.join(""));
			    
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//加载制作进度
function loadProductionSchedule(id){
	$.ajax({
		url: '/projectManager/queryProductionSchedule',
		type: 'post',
		data: {crewId:id},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var html=[];
				html.push('<table class="schedule-table" cellspacing=0 cellpadding=0>');
				html.push('<tr><th width="20%">项目</th><th width="20%">预计总量</th><th width="20%">实际完成量</th><th width="20%">剩余量</th><th width="20%">完成进度</th></tr>');
				html.push('<tr><td class="text-align-left">场次</td><td>'+response.totalViewCount+'</td><td>'+response.finishedViewCount+'</td><td>'+response.unfinishedViewCount+'</td><td>'+response.viewFinishedRate+'</td></tr>');
				html.push('<tr><td class="text-align-left">页数</td><td>'+response.totalPageCount+'</td><td>'+response.finishedPageCount+'</td><td>'+response.unfinishedPageCount+'</td><td>'+response.pageFinishedRate+'</td></tr>');
				html.push('<tr><td class="text-align-left">工作时间(天数)</td><td>'+response.totalShootDate+'</td><td>'+response.shootDate+'</td><td>'+response.unfinishedShootDate+'</td><td>'+response.dateFinishedRate+'</td></tr>');
				html.push('<tr><td class="text-align-left">资金投入</td><td>'+response.totalBudget+'</td><td>'+response.totalInput+'</td><td>'+response.remainInput+'</td><td>'+response.inputRate+'</td></tr>');
				html.push('<tr><td class="text-align-left">资金支出</td><td>'+response.totalBudget+'</td><td>'+response.totalPayed+'</td><td>'+response.remainPayed+'</td><td>'+response.payedRate+'</td></tr>');
				html.push('</table>');
				$("#productionSchedule").html(html.join(''));
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//获取原始的金额信息
function genOriginalMoney(value) {
	return parseFloat(value.replace(/,/g, ""));
}
//获取原始的比例信息
function genOriginalRate(value) {
	return parseFloat(value.replace(/%/g, ""));
}