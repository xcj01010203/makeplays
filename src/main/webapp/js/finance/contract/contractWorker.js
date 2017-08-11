var singleCurrencyFlag = false;

$(document).ready(function() {
	
	//校验是否需要财务密码
	checkNeedFinancePwd();
	
	//检查该剧组是否只有一个币种
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		type: 'post',
		data: {ifEnable: true},
		datatype: 'json',
		async: false,
		success: function(response){
			var currencyInfoList = response.currencyInfoList;
			if (currencyInfoList.length == 1) {
				singleCurrencyFlag = true;
			}
		}
	});
	
	//职员合同
    $("#contractWorkerTab").click(function(){
    	window.location = "/contractManager/toContractPage?contractType=1";
    });
//演员合同
    $("#contractActorTab").click(function(){
    	window.location = "/contractManager/toContractPage?contractType=2";
    });
//制作合同
    $("#contractProduceTab").click(function(){
    	window.location = "/contractManager/toContractPage?contractType=3";
    });
    //待付合同款清单
    $("#contractToPaidTab").click(function(){
    	window.location = "/contractManager/toContractPage?contractType=4";
    });
	
	//加载职员合同列表
	loadWorkerContractList();
	//职员合同总的统计信息
	totlWorkerContractList();
	
	//初始化高级查询窗口
	initAdvanceSearch();
	//初始化下拉列表
	$('.selectpicker').selectpicker({
        size: 4
    });
	

	//加载下拉列表
	showDropDown();
	//清空下拉采单的选中事件
	clearSelected();
	
	//初始化导出窗口
	initExportWindow();
	
	//初始化导入窗口
	initImportWorkerWin();
	
});



//定义查询职员合同的变量
var data = {};
//加载职员合同列表
function loadWorkerContractList(){
	var source = {
			url: '/contractWorker/queryContractList',
			type: "post",
			data: data,
			dataType : "json",
			datafields : [
			     {name: "contractId", type: "string"},
			     {name: "workerName", type: "string"},
			     {name: "contractNo", type: "string"},
			     {name: "department", type: "string"},
			     {name: "totalMoney", type: "Double"},
			     {name: "payedMoney", type: "Double"},
			     {name: "leftMoney", type: "Double"},
			     {name: "currencyId", type: "string"},
			     {name: "currencyCode", type: "string"}
			],
			root: 'contractWorkerList'
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	//职员姓名
	var workerNameRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='worker-name-column'>");
		html.push("	<a class='jqx-column float-left' href='javascript:modifyWorkerContractInfo(\""+row+"\")'>" + rowdata.workerName + "</a>");
        html.push("</div>");
		return html.join("");
	};
	//合同编码
	var contractNoRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='contract-number-column'>");
		html.push("	<a class='jqx-column float-left' href='javascript:modifyWorkerContractInfo(\""+row+"\")'>" + rowdata.contractNo + "</a>");
        html.push("</div>");
		
		return html.join("");
	};
	//总薪酬
	var totalMoneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='total-money-column'>");
		html.push(fmoney(rowdata.totalMoney));
		if (!singleCurrencyFlag) {
			html.push("<span>(" + rowdata.currencyCode + ")</span>");
		}
		html.push("</div>");
		return html.join("");
	};
	//已付薪酬
	var payedMoneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='pay-money-column'>");
		html.push(fmoney(rowdata.payedMoney));
		if (!singleCurrencyFlag) {
			html.push("<span>(" + rowdata.currencyCode + ")</span>");
		}
		html.push("</div>");
		return html.join("");
	};
	//未付薪酬
	var leftMoneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='left-money-column'>");
		html.push(fmoney(rowdata.leftMoney));
		if (!singleCurrencyFlag) {
			html.push("<span>(" + rowdata.currencyCode + ")</span>");
		}
		html.push("</div>");
		return html.join("");
	};
	
	$("#workerContractList").jqxGrid({
		width: "calc(100% - 2px)",
		height: "calc(100% - 140px)",
		columnsheight: 35,
		rowsheight: 30,
		source: dataAdapter,
		showtoolbar: true,
		columns: [
			        { text: '职员姓名', datafield: 'workerName', cellsrenderer: workerNameRenderer, cellsAlign: "center", align: "center", width: '15%' },
			        { text: '合同编码', datafield: 'contractNo', cellsrenderer: contractNoRenderer, cellsAlign: "center", align: "center", width: '17%' },
			        { text: '部门职务', datafield: 'department', cellsAlign: "center", align: "center", width: '17%' },
			        { text: '总合同金额 ', datafield: 'totalMoney', cellsrenderer: totalMoneyRenderer, cellsAlign: "right", align: "center", width: '17%' },
			        { text: '已付合同金额', datafield: 'payedMoney', cellsrenderer: payedMoneyRenderer, cellsAlign: "right", align: "center", width: '17%' },
			        { text: '未付合同金额 ', datafield: 'leftMoney', cellsrenderer: leftMoneyRenderer, cellsAlign: "right", align: "center", width: '17%' }
			    ],
		localization: localizationobj,
		rendertoolbar: function(toolbar) {
			var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='advance-search-btn' id='advanceSearchBtn' onclick='openAdvanceSearch()'>");
			if(!isContractReadonly){
				container.push("<input type='button' class='add-contract-btn' id='addContractBtn' onclick='openaddContract()'>");
			}
			if(hasExportContractAuth) {
				container.push("<input type='button' class='export-contact' id='exportWorkerBtn' onclick='exportContractWorkerList()'>");
			}
			if(!isContractReadonly && hasImportContractAuth){
				container.push("<input type='button' class='import-btn' id='importWorkerBtn' onclick='showImportWin()'>");
			}
            container.push("</div>");
			
			toolbar.append($(container.join("")));
			
			$("#advanceSearchBtn").jqxTooltip({content: "高级搜索", position: "bottom"});
			if(!isContractReadonly){
			    $("#addContractBtn").jqxTooltip({content: "添加合同", position: "bottom"});
			}
			if(hasExportContractAuth) {
				$("#exportWorkerBtn").jqxTooltip({content: "导出职员合同", position: "bottom"});
			}
			if(!isContractReadonly && hasImportContractAuth){
				$("#importWorkerBtn").jqxTooltip({content: "导入职员合同", position: "bottom"});
			}
		}
	});
}

//获取职员合同总的统计信息列表
function totlWorkerContractList(){
	$.ajax({
		url: '/contractWorker/queryContractMoneyStatistics',
		type: 'post',
		datatype: 'json',
		success: function(response) {
			if(response.success){
				var standardSumMoneyMap = response.standardSumMoneyMap;
				var currencySumMoneyList = response.currencySumMoneyList;
				var html = [];
				var rowspanLength = currencySumMoneyList.length+1;
				
				html.push("<table cellspacing=0 cellpadding=0>");
				html.push("<tr>");
				html.push("<td rowspan = " + rowspanLength +">合计</td>");
				html.push("<td>总金额</td>");
				html.push("<td>已付金额</td>");
				html.push("<td>未付金额</td>");
				html.push("<td>支付比例</td>");
				html.push("</tr>");
				for(var i = 0;i < currencySumMoneyList.length; i++){
					html.push("<tr>");
					html.push("<td class='align-td'>" + fmoney(currencySumMoneyList[i].totalMoney));
					if (!singleCurrencyFlag) {
						html.push("<span>(" + currencySumMoneyList[i].currencyCode + ")</span>");
					}
					html.push("</td>");
					html.push("<td class='align-td'>" + fmoney(currencySumMoneyList[i].payedMoney));
					if (!singleCurrencyFlag) {
						html.push("<span>(" + currencySumMoneyList[i].currencyCode + ")</span>");
					}
					html.push("</td>");
					html.push("<td class='align-td'>" + fmoney(currencySumMoneyList[i].leftMoney));
					if (!singleCurrencyFlag) {
						html.push("<span>(" + currencySumMoneyList[i].currencyCode + ")</span>");
					}
					html.push("</td>");
					html.push("<td class='align-td'>" + fmoney(currencySumMoneyList[i].payedRate) + "%</td>");
					html.push("</tr>");
				}
				html.push("<tr>");
				html.push("<td>折合本位币</td>");
				html.push("<td class='align-td'>" + fmoney(standardSumMoneyMap.totalMoney));
				if (!singleCurrencyFlag) {
					html.push("<span>(" + standardSumMoneyMap.currencyCode + ")</span>");
				}
				html.push("</td>");
				html.push("<td class='align-td'>" + fmoney(standardSumMoneyMap.payedMoney));
				if (!singleCurrencyFlag) {
					html.push("<span>(" + standardSumMoneyMap.currencyCode + ")</span>");
				}
				html.push("</td>");
				html.push("<td class='align-td'>" + fmoney(standardSumMoneyMap.leftMoney));
				if (!singleCurrencyFlag) {
					html.push("<span>(" + standardSumMoneyMap.currencyCode + ")</span>");
				}
				html.push("</td>");
				html.push("<td class='align-td'>" + fmoney(standardSumMoneyMap.payedRate) + "%</td>");
				html.push("</tr>");
				html.push("</table>");
				html.join("");
				$(".worker-contract-total").empty();
				$(".worker-contract-total").append($(html.join("")));
				
			}else{
				//showErrorMessage(response.message);
			}
			
			
		}
	});
}


//显示高级搜索窗口
function openAdvanceSearch() {
	$("#advanceSearchWin").jqxWindow("open");
}
function initAdvanceSearch(){
	$("#advanceSearchWin").jqxWindow({
		theme: theme,
		width: 640,
		height: 450,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			
		}
	});
}

//显示姓名下拉框
function showDropDown(){
	$.ajax({
		url: '/contractWorker/queryDropDownList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var nameList = response.workerNameList;
				var financeSubjList = response.financeSubjList;
				var nameHtml = [];
				var financeHtml = [];
				if(nameList.length != 0){
					for(var i = 0; i < nameList.length; i++){
						nameHtml.push("<option value='" + nameList[i] +"'>" + nameList[i] + "</option>");
					}
				}
				if(financeSubjList.length != 0){
					for(var i = 0; i < financeSubjList.length; i++){
						financeHtml.push("<option value='" + financeSubjList[i].financeSubjId +"'>" + financeSubjList[i].financeSubjName + "</option>");
					}
				}
				$("#workerName").empty();
				$("#workerName").append(nameHtml.join(""));
				$('#workerName').selectpicker("refresh");
				$("#financeSubjId").empty();
				$("#financeSubjId").append(financeHtml.join(""));
				$("#financeSubjId").selectpicker("refresh");
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}

//清空选中
function clearSelected(){
	$('.select-picker-li').on('mouseover', function(event) {
        if ($(this).find("select").val() != null && $(this).find("select").val() != undefined) {
            $(this).find(".clearSelection").show();
        }
    });
	
    
    $('.select-picker-li').on('mouseout', function(event) {
        $(this).find(".clearSelection").hide();
    });
    
    $(".clearSelection").on('click', function() {
        $(this).siblings(".selectpicker").selectpicker('deselectAll');
    });
}


//高级查询
function advanceQuery(){
	var workerNames=$("#workerName").val();
	var result = "";
	var workerName="";
	if(workerNames != null){
		for(var i = 0; i< workerNames.length; i++){
	    	result += workerNames[i] + ",";
	    	
	    }
		workerName = result.substring(0, result.length-1);
	}
	if($("#financeSubjId").val() != null){
		data.financeSubjIds = $("#financeSubjId").val().toString();
	}else{
		data.financeSubjIds = "";
	}
	data.payWay = $("input[type=radio][name=payWay]:checked").val();
	data.workerNames = workerName;
	data.department = $("#department").val();
	data.paymentTerm = $("#paymentTerm").val();
	data.remark = $("#remark").val();
	
	$("#advanceSearchWin").jqxWindow("close");
	$("#workerContractList").jqxGrid("updatebounddata");
}
//关闭高级查询
function closeQuery(){
	$("#advanceSearchWin").jqxWindow("close");
}
//清空高级查询
function clearQuery(){
	$('.selectpicker').selectpicker('deselectAll');
	$("#department").val("");
	$("#paymentTerm").val("");
	$("#remark").val("");
	$("input[type=radio][name=payWay]:checked").each(function(){
		$(this).prop("checked", false);
	});
}

//添加职员合同
function openaddContract(){
	$("#contractId").val("");
	$(".right-popup-win").css({"width":"84%"}).show().animate({"right":"0"}, 300);
	$("#contractDetailIframe").attr("src", "/contractWorker/toContractWorkerDetailPage");
	
}
//关闭添加
function closeAddPage(){
	var width = $(".right-popup-win").width();
	clearInterval(timer);
	$(".right-popup-win").animate({"right": 0-width},300);
	
	var timer = setTimeout(function(){
		$(".right-popup-win").hide();
	}, 300);
	
//	$(".right-popup-win").hide().animate({"right":"0"},200);
	//显示姓名下拉框
	showDropDown();
	$("#workerContractList").jqxGrid("updatebounddata");
	//获取职员合同总的统计信息列表
   totlWorkerContractList();
	/*window.location.href = "/contractManager/toContractPage?contractType=1";*/
}


//修改职员合同信息
function modifyWorkerContractInfo(editrow){

	var dataRecord = $("#workerContractList").jqxGrid('getrowdata', editrow);
	var contractIdValue = dataRecord.contractId;
	$(".right-popup-win").css({"width":"84%"}).show().animate({"right":"0"}, 300);
	$("#contractDetailIframe").attr("src", "/contractWorker/toContractWorkerDetailPage?contractId="+ contractIdValue );
	
}

//导出窗口初始化
function initExportWindow(){
	$("#exportContractWorkerWindow").jqxWindow({
		theme:theme,  
		width: 270,
		height: 130, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: true,
		isModal: true,
		showCloseButton: false,
		resizable: true,
		cancelButton: $("#cancelExpBtn"),
   });
}

//导出职员合同
function exportContractWorkerList(){
	$("#exportContractWorkerWindow").jqxWindow("show");
}

//确认导出合同
function confirmExport(){
	//取出选择的值
	var checkValue = $(".exportTvOption input[type='radio']:checked").val();
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
	if (checkValue == '1') { //导出合同的统计列表结果
		$.ajax({
			url: '/contractWorker/exportContractWorkerList',
			type: 'post',
			data:{workerNames:data.workerNames, department:data.department, financeSubjIds:data.financeSubjIds,
				payWay:data.payWay, paymentTerm:data.paymentTerm, remark:data.remark},
			datatype: 'json',
			success: function(response) {
				if (response.success) {
					var downLoadPath = response.downloadPath;
					var form = $("<form></form>");
	                form.attr("action","/fileManager/downloadFileByAddr");
	                form.attr("method","post");
	                form.append("<input type='hidden' name='address'>");
	                form.append("<input type='hidden' name='fileName'>");
	                form.find("input[name='address']").val(downLoadPath);
	                $("body").append(form);
	                form.submit();
	                
	                form.remove();
	            	_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractWorkerWindow").jqxWindow("close");
				}else {
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractWorkerWindow").jqxWindow("close");
					showErrorMessage(response.message);
				}
			}
		});
	}else if (checkValue == '2') { //导出详细信息列表
		$.ajax({
			url: '/contractWorker/exportContractWorkerDetail',
			type: 'post',
			datatype: 'json',
			success: function(response) {
				if (response.success) {
					var downLoadPath = response.downloadPath;
					var form = $("<form></form>");
	                form.attr("action","/fileManager/downloadFileByAddr");
	                form.attr("method","post");
	                form.append("<input type='hidden' name='address'>");
	                form.append("<input type='hidden' name='fileName'>");
	                form.find("input[name='address']").val(downLoadPath);
	                $("body").append(form);
	                form.submit();
	                form.remove();
	            	_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractWorkerWindow").jqxWindow("close");
				}else {
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractWorkerWindow").jqxWindow("close");
					showErrorMessage(response.message);
				}
			}
		});
	}
	
}

//初始化导入窗口
function initImportWorkerWin() {
	$("#importExportWorkerWin").jqxWindow({
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
	$("#importExportWorkerWin").jqxWindow("open");
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/contractWorker/importantContractWorkerDetail&&needIsCover=true&&refreshUrl=/contractManager/toContractPage?contractType=1&&templateUrl=/template/import/职员合同详细信息导入模板.xls");
}

//关闭导入窗口
function closeImportWin(){
	$("#importExportWorkerWin").jqxWindow("close");
}
