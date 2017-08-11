$(document).ready(function(){
	//校验是否需要财务密码
	checkNeedFinancePwd();
	
	//生成财务科目结算表
	loadFinanceSubjSettleList();
	
	//结算汇总统计信息
	settleAccountList();
	
	//初始化结算时间窗口
	initSettleTimeWin();
});


//防止重新加载按钮
var onlyRunOne= 1;

//全局提交变量
var subData = {};

//生成财务科目结算表
function loadFinanceSubjSettleList(){
	
	var source = {
			url: '/settleManager/querySettlementList',
			type: 'post',
			data: subData,
			datatype: 'json',
			dataFields: [
			    {name: 'financeSubjId', type: 'string'},
			    {name: 'financeSubjName', type: 'string'},
			    {name: 'financeSubjParentId', type: 'string'},
			    {name: 'remark', type: 'string'},
			    {name: 'hasChildren', type: 'boolean'},
			    {name: 'totalBadgetMoney', type: 'string'},
			    {name: 'totalPayedMoney', type: 'string'},
			    {name: 'totalHasReceiptMoney', type: 'string'},
			    {name: 'totalNoreceiptMoney', type: 'string'},
			    {name: 'totalLeftMoney', type: 'string'},
			    {name: 'totalPayedRate', type: 'string'},
			    {name: 'contractBudgetMoney', type: 'string'},
			    {name: 'contractPayedMoney', type: 'string'},
			    {name: 'contractLeftMoney', type: 'string'},
			    {name: 'contractPayedRate', type: 'string'},
			    {name: 'loanBudgetMoney', type: 'string'},
			    {name: 'loanPayedMoney', type: 'string'},
			    {name: 'loanLeftMoney', type: 'string'},
			    {name: 'loanPayedRate', type: 'string'},
			    {name: 'flexibleMoney', type: 'string'},
			    {name: 'flexibleRate', type: 'string'}
			    
			], 
			 hierarchy: {
					keyDataField: {name:'financeSubjId'},
					parentDataField: {name:'financeSubjParentId'}
			 },
			 id: 'financeSubjId'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var financeSubjRender = function(row, column, value, rowData){
		if(!rowData.hasChildren){
			return '<span><a href="javascript:void(0);" onclick="showFinanceBugetList(\''+ rowData.financeSubjId +'\')">' + rowData.financeSubjName + '</a></span>';
		}else{
			return '<span>' + rowData.financeSubjName + '</span>';
		}
	};
	
	
	var balanceRenderer = function(row, column, value, rowData){
		if(genOriginalMoney(value) < 0){
			return "<span class='jqx-column text-align-right red-color' title='"+ value +"'>" + value + "</span>";
		}
		return "<span class='jqx-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	
	var rateRenderer = function(row, column, value, rowData) {
		if(genOriginalRate(value) > 100){
			return "<span class='jqx-column text-align-right red-color' title='"+ value +"'>" + value + "</span>";
		}
		return "<span class='jqx-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	var cellsTitle = function(row, column, value, rowData){
		return "<span class='jqx-column text-align-right' title='"+ value +"'>" + value + "</span>";
	};
	
	//定义导出数据变量
	var exportData = [];
	//定义下载文件的地址
	var fileAddress = "";
	
	/** 获取导出数据的递归函数 */
	function addRecord(record, datafield){
		for(var i = 0; i < record.length; i++){
			
			var obj = {};
			for(var n = 0; n < datafield.length; n++){
				obj[datafield[n].dataField] = record[i][datafield[n].dataField] ;
			}
			obj["level"] = record[i]["level"] ;
			exportData.push(obj);
			if(record[i].records){
				addRecord(record[i].records,datafield);
			}
		}
	}
	
	var columnInformation = [];
	columnInformation.push({text: '财务科目', datafield: 'financeSubjName', cellsrenderer: financeSubjRender, width: '12%', cellsAlign: 'left', align: 'center'});
	columnInformation.push({text: '总预算', datafield: 'totalBadgetMoney', cellsrenderer: cellsTitle, columngroup: 'totalMoneyDetail', width: '8%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '总支出', datafield: 'totalPayedMoney',  columngroup: 'totalMoneyDetail', width: '6%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '有票总支出', datafield: 'totalHasReceiptMoney',  columngroup: 'totalMoneyDetail', width: '6%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '无票总支出', datafield: 'totalNoreceiptMoney',  columngroup: 'totalMoneyDetail', width: '6%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '总结余', datafield: 'totalLeftMoney', cellsrenderer: balanceRenderer, columngroup: 'totalMoneyDetail', width: '6%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '支出比例', datafield: 'totalPayedRate', cellsrenderer: rateRenderer, cellclassname: 'divide_line', columngroup: 'totalMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '合同款', datafield: 'contractBudgetMoney', columngroup: 'contractMoneyDetail', width: '6%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '已付', datafield: 'contractPayedMoney', columngroup: 'contractMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '未付', datafield: 'contractLeftMoney', cellsrenderer: balanceRenderer, columngroup: 'contractMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '已付比例', datafield: 'contractPayedRate', cellsrenderer: rateRenderer, cellclassname: 'divide_line', columngroup: 'contractMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '借款', datafield: 'loanBudgetMoney', columngroup: 'loanMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '已还', datafield: 'loanPayedMoney', columngroup: 'loanMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '未还', datafield: 'loanLeftMoney', cellsrenderer: balanceRenderer, columngroup: 'loanMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '还款比例', datafield: 'loanPayedRate', cellsrenderer: rateRenderer, cellclassname: 'divide_line', columngroup: 'loanMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '额度', datafield: 'flexibleMoney', cellsrenderer: balanceRenderer, columngroup: 'flexibleMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	columnInformation.push({text: '比例', datafield: 'flexibleRate', cellsrenderer: balanceRenderer, columngroup: 'flexibleMoneyDetail', width: '5%', cellsAlign: 'right', align: 'center'});
	
	
	$("#financeSubjSettleList").jqxTreeGrid({
//		theme: theme,
		width: '100%',
		height: 'calc(100% - 5px)',
		source: dataAdapter,
		showToolbar: true,
//		columnsResize: true,
		toolbarHeight: 35,
		localization: localizationobj,
//		sortable: true,
		rendertoolbar: function(toolbar) {
			
			if(onlyRunOne == 1){
				
				var container = [];
				container.push("<div class='toolbar'>");
				if(hasExportSettlementAuth) {
					container.push("<input type='button' class='export-btn' id='exportBtn'>");
				}
				container.push("<input type='button' class='finance-time-para' id='financeTimePara' onclick='queryFinanceTimePara()'>");
				container.push("</div>");
				toolbar.append($(container.join("")));
				if(hasExportSettlementAuth) {
					$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
				}
				$("#financeTimePara").jqxTooltip({content: "结算时间段", position: "bottom"});
			}
			
			
			$("#exportBtn").unbind("click");
			//导出财务预算表
			$("#exportBtn").on("click", function(){
				
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
				
				
				var rows = $("#financeSubjSettleList").jqxTreeGrid('getRows');
				    var columnNames = new Array();
				    for(var i = 0; i < columnInformation.length; i++){
				        columnNames.push({dataField: columnInformation[i].datafield, text: removeHTMLTag(columnInformation[i].text)});
				    }
				    
				exportData=new Array();
				   
				addRecord(rows, columnNames);
				
			    $.ajax({
			    	url: '/commonExportExcel/exportExcel',
			    	type: 'post',
			    	data: {rows: JSON.stringify(exportData), columns: JSON.stringify(columnNames), fileName: "费用结算表"},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
			 onlyRunOne++;
		},
		columns: columnInformation,
		columnGroups: [
		    {text: '预算资金', align: 'center', name: 'totalMoneyDetail'},
		    {text: '关联合同', align: 'center', name: 'contractMoneyDetail'},
		    {text: '关联借款', align: 'center', name: 'loanMoneyDetail'},
		    {text: '可机动费用', align: 'center', name: 'flexibleMoneyDetail'}
        ]
	});
	
	$('#financeSubjSettleList').on('bindingComplete', function (event){
		$("#financeSubjSettleList").jqxTreeGrid("expandAll");
	});
}

/*导出需要的功能函数*/
function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}

//获取原始的金额信息
function genOriginalMoney(value) {
	return parseFloat(value.replace(/,/g, ""));
}
//获取原始的比例信息
function genOriginalRate(value) {
	return parseFloat(value.replace(/%/g, ""));
}

//结算汇总统计信息
function settleAccountList(){
	$.ajax({
		url: '/settleManager/queryTotalStatisticInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var html = [];
				html.push("<table class='settle-account-table' cellspacing=0 cellpadding=0>");
				html.push("<tr>");
				html.push("<td width='12%'><div>合计<div></td>");
				html.push("<td width='8%'><div class='text-align-right'>" + response.totalBudgetMoney + "</div></td>");
				//总支出
				html.push("<td width='6%'><div class='text-align-right'>" + response.totalPayedMoney + "</div></td>");
				//有票总支出
				html.push("<td width='6%'><div class='text-align-right'>" + response.totalHasReceipt + "</div></td>");
				//无票总支出
				html.push("<td width='6%'><div class='text-align-right'>" + response.totalNoReceipt + "</div></td>");
				if (genOriginalMoney(response.totalLeftMoney) >= 0) {
					html.push("<td width='6%'><div class='text-align-right'>" + response.totalLeftMoney + "</div></td>");
				} else {
					html.push("<td width='6%'><div class='text-align-right red-color'>" + response.totalLeftMoney + "</div></td>");
				}
				if (genOriginalRate(response.totalPayedRate) <= 100) {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right'>" + response.totalPayedRate + "</div></td>");
				} else {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right red-color'>" + response.totalPayedRate + "</div></td>");
				}
				
				html.push("<td width='6%'><div class='text-align-right'>" + response.totalContractBudget + "</div></td>");
				html.push("<td width='5%'><div class='text-align-right'>" + response.totalContractPayed + "</div></td>");
				
				if (genOriginalMoney(response.totalContractLeft) >= 0) {
					html.push("<td width='5%'><div class='text-align-right'>" + response.totalContractLeft + "</div></td>");
				} else {
					html.push("<td width='5%'><div class='text-align-right red-color'>" + response.totalContractLeft + "</div></td>");
				}
				if (genOriginalRate(response.totalContractPayedRate) <= 100) {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right'>" + response.totalContractPayedRate + "</div></td>");
				} else {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right red-color'>" + response.totalContractPayedRate + "</div></td>");
				}
				
				html.push("<td width='5%'><div class='text-align-right'>" + response.totalLoanMoney + "</div></td>");
				html.push("<td width='5%'><div class='text-align-right'>" + response.totalLoanPayed + "</div></td>");
				
				if (genOriginalMoney(response.totalLoanLeft) >= 0) {
					html.push("<td width='5%'><div class='text-align-right'>" + response.totalLoanLeft + "</div></td>");
				} else {
					html.push("<td width='5%'><div class='text-align-right red-color'>" + response.totalLoanLeft + "</div></td>");
				}
				if (genOriginalRate(response.totalLoanPayedRate) <= 100) {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right'>" + response.totalLoanPayedRate + "</div></td>");
				} else {
					html.push("<td width='5%' class='divide_line'><div class='text-align-right red-color'>" + response.totalLoanPayedRate + "</div></td>");
				}
				
				if (genOriginalMoney(response.totalFlexibleMoney) >= 0) {
					html.push("<td width='5%'><div class='text-align-right'>" + response.totalFlexibleMoney + "</div></td>");
				} else {
					html.push("<td width='5%'><div class='text-align-right red-color'>" + response.totalFlexibleMoney + "</div></td>");
				}
				if (genOriginalRate(response.totalFlexibleRate) <= 100) {
					html.push("<td width='5%'><div class='text-align-right'>" + response.totalFlexibleRate + "</div></td>");
				} else {
					html.push("<td width='5%'><div class='text-align-right red-color'>" + response.totalFlexibleRate + "</div></td>");
				}
				
				
				html.push("</tr>");
				html.push("</table>");
				$("#settleAccountList").empty();
				
				$("#settleAccountList").append(html.join(""));
			    
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}

//显示结算时间窗口
function queryFinanceTimePara(){
	$("#settleTimeWin").jqxWindow("open");
}

//初始化结算时间窗口
function initSettleTimeWin(){
	$("#settleTimeWin").jqxWindow({
		theme: theme,
		width: '400',
		height: '250',
		resizable: false,
		isModal: true,
		autoOpen: false,
	});
}




//查询结算时间段的列表
function querySettleTime(){
	subData.paymentStartDate= $("#startDate").val();
	subData.paymentEndDate= $("#endDate").val();
	showSuccessMessage("操作成功");
	$("#settleTimeWin").jqxWindow("close");
	//生成财务科目结算表
	/*loadFinanceSubjSettleList();*/
	$("#financeSubjSettleList").jqxTreeGrid('updateBoundData');
	//结算汇总统计信息
	settleAccountList();
}

//展示包含当前财务科目的合同列表
function showContractList(id){
	
	//显示合同详情列表
	var source = {
		url: '/settleManager/queryFinanceSubjContract',
		type: 'post',
		data: {financeSubjId: id},
		datatype: 'json',
		datafields: [
 		 		    {name: 'financeSubjId', type: 'string'},
 		 		    {name: 'currencyMoney', type: 'double'},
 		 		    {name: 'contractNo', type: 'string'},
 		 		    {name: 'contractName', type: 'string'},
 		 		    {name: 'contractDate', type: 'string'},
 		 		    {name: 'financeSubjName', type: 'string'},
 		 		    {name: 'remark', type: 'string'},
 		 		    {name: 'payWay', type: 'string'},
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var titleRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column-cell align-left' title='"+ columnproperties +"'>" + columnproperties + "</div>");
		html.join("");
		return html;
	};
	var moneyRenderer = function(columnfield, value, columnproperties, rowdata){
		if((columnproperties != null) && (columnproperties != "")){
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + fmoney(columnproperties) + "</div>";
		}else{
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + columnproperties + "</div>";
		}
	};
	
	$("#contractListGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: 'calc(100% - 40px)',
		rowsHeight: 30,
		columnsHeight: 30,
		source: dataAdapter,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function(toolbar) {
				
			var container = [];
			container.push("<div class='toolbar sonToolbar'>");
			if(hasExportSettlementAuth) {
				container.push("<input type='button' class='export-btn' id='exportContractlistBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportSettlementAuth) {
				$("#exportContractlistBtn").jqxTooltip({content: "导出合同列表", position: "bottom"});
			}
			
			$("#exportContractlistBtn").unbind("click");
			//导出财务预算表
			$("#exportContractlistBtn").on("click", function(){
				
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
				
			    $.ajax({
			    	url: '/settleManager/exportFinanceSubjContract',
			    	type: 'post',
			    	data: {financeSubjId:id},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
		},
        columns: [
                  {text:'合同签订日期', datafield: 'contractDate',cellsrenderer: titleRenderer, width: '15%', align: "center"},
	            {text:'合同编号', datafield: 'contractNo',cellsrenderer: titleRenderer, width: '15%', align: "center", cellsAlign: "center"},
	            {text:'摘要', datafield: 'remark',cellsrenderer: titleRenderer, width: '20%', align: "center"},
	            {text:'财务科目', datafield: 'financeSubjName',cellsrenderer: titleRenderer, width: '17%', align: "center", cellsAlign: "left" },
	            {text:'金额', datafield: 'currencyMoney',cellsrenderer: moneyRenderer, width: '10%', align: "center", cellsAlign: "left" },
	            {text:'合同关系人', datafield: 'contractName', cellsrenderer: titleRenderer,width: '8%', align: "center"},
	            {text:'付款方式', datafield: 'payWay', cellsrenderer: titleRenderer,width: '15%', align: "center"}
        ]
	});
}

//展示包含当前财务科目的付款单的列表
function showPaymentList(id){
	//显示合同详情列表
	var source = {
		url: '/settleManager/queryContractPaymentList',
		type: 'post',
		data: {financeSubjId: id},
		datatype: 'json',
		datafields: [
 		 		    {name: 'receiptNo', type: 'string'},//票据编号
 		 		    {name: 'paymentDate', type: 'double'},//付款日期
 		 		    {name: 'payeeName', type: 'string'},//收款人单位名称
 		 		    {name: 'remark', type: 'string'},//备注
 		 		    {name: 'currencyMoney', type: 'double'},//合计金额
 		 		    {name: 'financeSubjName', type: 'string'},//财务科目
 		 		    {name: 'paymentWay', type: 'string'},//付款方式
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var titleRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column-cell align-left' title='"+ columnproperties +"'>" + columnproperties + "</div>");
		html.join("");
		return html;
	};
	var moneyRenderer = function(columnfield, value, columnproperties, rowdata){
		if((columnproperties != null) && (columnproperties != "")){
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + fmoney(columnproperties) + "</div>";
		}else{
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + columnproperties + "</div>";
		}
	};
	
	$("#contractPaymentListGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: 'calc(100% - 40px)',
		rowsHeight: 30,
		columnsHeight: 30,
		source: dataAdapter,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function(toolbar) {
				
			var container = [];
			container.push("<div class='toolbar sonToolbar'>");
			if(hasExportSettlementAuth) {
				container.push("<input type='button' class='export-btn' id='exportContractPaymentListBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportSettlementAuth) {
				$("#exportContractPaymentListBtn").jqxTooltip({content: "导出合同付款单列表", position: "bottom"});
			}
			
			$("#exportContractPaymentListBtn").unbind("click");
			//导出财务预算表
			$("#exportContractPaymentListBtn").on("click", function(){
				
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
				
			    $.ajax({
			    	url: '/settleManager/exportContractPaymentList',
			    	type: 'post',
			    	data: {financeSubjId:id},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
		},
		 columns: [
	                  {text:'付款日期', datafield: 'paymentDate',cellsrenderer: titleRenderer, width: '15%', align: "center"},
		            {text:'票据编号', datafield: 'receiptNo', cellsrenderer: titleRenderer,width: '15%', align: "center", cellsAlign: "center"},
		            {text:'摘要', datafield: 'remark',cellsrenderer: titleRenderer, width: '20%', align: "center"},
		            {text:'财务科目', datafield: 'financeSubjName',cellsrenderer: titleRenderer, width: '17%', align: "center", cellsAlign: "left" },
		            {text:'金额', datafield: 'currencyMoney',cellsrenderer: moneyRenderer, width: '10%', align: "center", cellsAlign: "left" },
		            {text:'收款人', datafield: 'payeeName', cellsrenderer: titleRenderer,width: '8%', align: "center"},
		            {text:'付款方式', datafield: 'paymentWay',cellsrenderer: titleRenderer, width: '15%', align: "center"}
	        ]
	});
	
}

//显示借款列表
function showLoanList(id){
	//显示合同详情列表
	var source = {
		url: '/settleManager/queryFinanceLoanList',
		type: 'post',
		data: {financeSubjId: id},
		datatype: 'json',
		datafields: [
 		 		    {name: 'receiptNo', type: 'string'},//票据编号
 		 		    {name: 'loanDate', type: 'double'},//借款日期
 		 		    {name: 'payeeName', type: 'string'},//借款人姓名
 		 		    {name: 'summary', type: 'string'},//摘要
 		 		    {name: 'currencyMoney', type: 'string'},//合计金额
 		 		    {name: 'financeSubjName', type: 'string'},//财务科目
 		 		    {name: 'paymentWay', type: 'string'},//付款方式
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var titleRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column-cell align-left' title='"+ columnproperties +"'>" + columnproperties + "</div>");
		html.join("");
		return html;
	};
	var moneyRenderer = function(columnfield, value, columnproperties, rowdata){
		if((columnproperties != null) && (columnproperties != "")){
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + fmoney(columnproperties) + "</div>";
		}else{
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + columnproperties + "</div>";
		}
	};
	
	$("#loanListGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: 'calc(100% - 40px)',
		rowsHeight: 30,
		columnsHeight: 30,
		source: dataAdapter,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function(toolbar) {
				
			var container = [];
			container.push("<div class='toolbar sonToolbar'>");
			if(hasExportSettlementAuth) {
				container.push("<input type='button' class='export-btn' id='exportFinanceLoanListBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportSettlementAuth) {
				$("#exportFinanceLoanListBtn").jqxTooltip({content: "导出借款单列表", position: "bottom"});
			}
			
			$("#exportFinanceLoanListBtn").unbind("click");
			//导出财务预算表
			$("#exportFinanceLoanListBtn").on("click", function(){
				
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
				
			    $.ajax({
			    	url: '/settleManager/exportFinanceLoanList',
			    	type: 'post',
			    	data: {financeSubjId:id},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
		},
		 columns: [
	                  {text:'借款日期', datafield: 'loanDate', cellsrenderer: titleRenderer,width: '15%', align: "center"},
		            {text:'票据编号', datafield: 'receiptNo',cellsrenderer: titleRenderer, width: '15%', align: "center", cellsAlign: "center"},
		            {text:'摘要', datafield: 'summary', cellsrenderer: titleRenderer,width: '20%', align: "center"},
		            {text:'财务科目', datafield: 'financeSubjName', cellsrenderer: titleRenderer,width: '17%', align: "center", cellsAlign: "left" },
		            {text:'金额', datafield: 'currencyMoney', cellsrenderer: moneyRenderer,width: '10%', align: "center", cellsAlign: "left" },
		            {text:'借款人', datafield: 'payeeName', cellsrenderer: titleRenderer,width: '8%', align: "center"},
		            {text:'付款方式', datafield: 'paymentWay', cellsrenderer: titleRenderer,width: '15%', align: "center"}
	        ]
	});
	
}

//显示关联借款单的付款单列表
function showLoanPaymentList(id){
	//显示合同详情列表
	var source = {
		url: '/settleManager/queryLoanpaymentList',
		type: 'post',
		data: {financeSubjId: id},
		datatype: 'json',
		datafields: [
 		 		    {name: 'paymentReceiptNo', type: 'string'},//票据编号
 		 		    {name: 'paymentDate', type: 'String'},//还款日期
 		 		    {name: 'payeeName', type: 'string'},//收款人姓名
 		 		    {name: 'paymentSummary', type: 'string'},//摘要
 		 		    {name: 'totalMoney', type: 'double'},//合计金额
 		 		    {name: 'financeSubjName', type: 'string'},//财务科目
 		 		    {name: 'paymentWay', type: 'string'},//付款方式
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var titleRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column-cell align-left' title='"+ columnproperties +"'>" + columnproperties + "</div>");
		html.join("");
		return html;
	};
	var moneyRenderer = function(columnfield, value, columnproperties, rowdata){
		if((columnproperties != null) && (columnproperties != "")){
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + fmoney(columnproperties) + "</div>";
		}else{
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + columnproperties + "</div>";
		}
		
	};
	
	$("#loanPaymentListGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: 'calc(100% - 40px)',
		rowsHeight: 30,
		columnsHeight: 30,
		source: dataAdapter,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function(toolbar) {
				
			var container = [];
			container.push("<div class='toolbar sonToolbar'>");
			if(hasExportSettlementAuth) {
				container.push("<input type='button' class='export-btn' id='exportLoanpaymentListBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportSettlementAuth) {
				$("#exportLoanpaymentListBtn").jqxTooltip({content: "导出还款单列表", position: "bottom"});
			}
			
			$("#exportLoanpaymentListBtn").unbind("click");
			//导出财务预算表
			$("#exportLoanpaymentListBtn").on("click", function(){
				
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
				
			    $.ajax({
			    	url: '/settleManager/exportLoanpaymentList',
			    	type: 'post',
			    	data: {financeSubjId:id},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
		},
		 columns: [
	                  {text:'还款日期', datafield: 'paymentDate', cellsrenderer: titleRenderer,width: '15%', align: "center"},
		            {text:'票据编号', datafield: 'paymentReceiptNo', cellsrenderer: titleRenderer,width: '15%', align: "center", cellsAlign: "center"},
		            {text:'摘要', datafield: 'paymentSummary', cellsrenderer: titleRenderer,width: '20%', align: "center"},
		            {text:'财务科目', datafield: 'financeSubjName', cellsrenderer: titleRenderer,width: '17%', align: "center", cellsAlign: "left" },
		            {text:'金额', datafield: 'totalMoney', cellsrenderer: moneyRenderer,width: '10%', align: "center", cellsAlign: "left" },
		            {text:'收款人', datafield: 'payeeName', cellsrenderer: titleRenderer,width: '8%', align: "center"},
		            {text:'付款方式', datafield: 'paymentWay', cellsrenderer: titleRenderer,width: '15%', align: "center"}
	        ]
	});
	
}

//显示预算资金中的总支出的明细列表
function showFinanceBudgetPaymentList(id) {
	//显示合同详情列表
	var source = {
		url: '/settleManager/queryFinanceBudgetPaymentList',
		type: 'post',
		data: {financeSubjId: id},
		datatype: 'json',
		datafields: [
 		 		    {name: 'receiptNo', type: 'string'},//票据编号
 		 		    {name: 'paymentDate', type: 'double'},//还款日期
 		 		    {name: 'payeeName', type: 'string'},//收款人姓名
 		 		    {name: 'agent', type: 'string'},//经办人
 		 		    {name: 'currencyMoney', type: 'string'},//付款金额金额
 		 		    {name: 'financeSubjName', type: 'string'},//财务科目
 		 		    {name: 'summary', type: 'string'},//摘要
 		 		    {name: 'paymentWay', type: 'string'},//付款方式
 		 		    {name: 'allMoney', type: 'double'},//合计金额
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function () {
        }
    });
	
	var titleRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column-cell align-left' title='"+ columnproperties +"'>" + columnproperties + "</div>");
		html.join("");
		return html;
	};
	var moneyRenderer = function(columnfield, value, columnproperties, rowdata){
		if((columnproperties != null) && (columnproperties != "")){
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + fmoney(columnproperties) + "</div>";
		}else{
			return "<div class='jqx-column-cell align-right' title='"+ columnproperties +"'>" + columnproperties + "</div>";
		}
	};
	
	$("#totalpaymentListGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: 'calc(100% - 40px)',
		rowsHeight: 30,
		columnsHeight: 30,
		source: dataAdapter,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function(sonToolbar) {
				
			var container = [];
			container.push("<div class='toolbar sonToolbar'>");
			if(hasExportSettlementAuth) {
				container.push("<input type='button' class='export-btn' id='exportFinanceBudgetPaymentListBtn'>");
			}
			container.push("</div>");
			sonToolbar.append($(container.join("")));
			if(hasExportSettlementAuth) {
				$("#exportFinanceBudgetPaymentListBtn").jqxTooltip({content: "导出预算总支出", position: "bottom"});
			}
			
			$("#exportFinanceBudgetPaymentListBtn").unbind("click");
			//导出财务预算表
			$("#exportFinanceBudgetPaymentListBtn").on("click", function(){
				
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
				
			    $.ajax({
			    	url: '/settleManager/exportFinanceBudgetPaymentList',
			    	type: 'post',
			    	data: {financeSubjId:id},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.find("input[name='address']").val(fileAddress);
			            $("body").append(form);
			            form.submit();
			            form.remove();
			    	}
			    });
		    	
			});	
			
		},
		columns: [
	                  {text:'付款日期', datafield: 'paymentDate',cellsrenderer: titleRenderer, width: '15%', align: "center"},
		            {text:'票据编号', datafield: 'receiptNo', cellsrenderer: titleRenderer,width: '15%', align: "center", cellsAlign: "center"},
		            {text:'摘要', datafield: 'summary', cellsrenderer: titleRenderer,width: '20%', align: "center"},
		            {text:'财务科目', datafield: 'financeSubjName', cellsrenderer: titleRenderer,width: '17%', align: "center", cellsAlign: "left" },
		            {text:'付款金额', datafield: 'currencyMoney', cellsrenderer: moneyRenderer,width: '10%', align: "center", cellsAlign: "left" },
		            {text:'收款人', datafield: 'payeeName',cellsrenderer: titleRenderer, width: '8%', align: "center"},
		            {text:'付款方式', datafield: 'paymentWay',cellsrenderer: titleRenderer, width: '15%', align: "center"}
	        ]
	});
	

}

//加载预算总支出合计金额
function loadSummery(url, id, div){
	//加载合计金额
	$.ajax({
		url: url,
    	type: 'post',
    	data: {financeSubjId: id},
    	datatype: 'json',
    	success: function(response){
    		if (response.success) {
    			var allMoney = response.allMoney;
    			if (allMoney == null || allMoney == undefined || allMoney == '') {
					allMoney = 0.00;
				}
				$("#" + div).text("合计：     " + fmoney(allMoney));
			}else {
				parent.showErrorMessage(response.message);
			}
    	}
	});
}



//显示费用预算相关信息
function showFinanceBugetList(id){
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	$("#financeSubjectId").val(id);
	$(".tab_li_current").removeClass("tab_li_current");
	$("#budget_expenditure").addClass("tab_li_current");
	$(".public-content-style").hide();
	$(".budget-expenditure").show();
	var id= $("#financeSubjectId").val();
	//根据id查询出财务科目
	$.ajax({
    	url: '/settleManager/queryFinanceSubjNameById',
    	type: 'post',
    	data: {financeSubjId:id},
    	datatype: 'json',
    	success: function(response){
    		if (response.success) {
				$("#financeSubjNameSpan").text(response.financeSubjName);
			}else {
				parent.showErrorMessage(response.message);
			}
    	}
    });
	
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	
	showFinanceBudgetPaymentList(id);
	//显示统计数据
	loadSummery("/settleManager/queryFinanceBudgetPaymentList", id, "paymentCountMoney");
}
//关闭滑动窗口
function closePopUpWin(){
	clearInterval(timer);
	var rightPopWidth = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-rightPopWidth},300);
	
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 300);
}

//预算总支出列表
function showBudgetExpenditure(own){
	$(".tab_li_current").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	$(".public-content-style").hide();
	$(".budget-expenditure").show();
	var id= $("#financeSubjectId").val();
	
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	showFinanceBudgetPaymentList(id);
}
//关联合同列表
function relatedContract(own){
	$(".tab_li_current").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	var id= $("#financeSubjectId").val();
	$(".public-content-style").hide();
	$(".related-contract").show();
	
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	
	showContractList(id);
	
	//显示统计信息
	loadSummery("/settleManager/queryFinanceSubjContract", id, "contractCountMoney");
}
//合同付款单列表
function paymentContract(own){
	$(".tab_li_current").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	var id= $("#financeSubjectId").val();
	$(".public-content-style").hide();
	$(".contract-payment").show();
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	
	showPaymentList(id);
	
	//显示统计信息
	loadSummery("/settleManager/queryContractPaymentList", id, "conPayCountMoney");
}
//关联借款单列表
function relatedLoanList(own){
	$(".tab_li_current").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	var id= $("#financeSubjectId").val();
	$(".public-content-style").hide();
	$(".related-loan").show();
	
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	
	showLoanList(id);
	
	//显示统计信息
	loadSummery("/settleManager/queryFinanceLoanList", id, "loanCountMoney");
}
//还款列表
function repaymentList(own){
	$(".tab_li_current").removeClass("tab_li_current");
	$(own).addClass("tab_li_current");
	var id= $("#financeSubjectId").val();
	$(".public-content-style").hide();
	$(".repayment-list").show();
	//移除工具条
	var _toolbar = $(".sonToolbar");
	if (_toolbar != undefined ) {
		//移除工具条
		_toolbar.html('');
	}
	
	showLoanPaymentList(id);
	
	//显示统计信息
	loadSummery("/settleManager/queryLoanpaymentList", id, "loanPayCountMoney");
}