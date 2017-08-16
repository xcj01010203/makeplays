var singleCurrencyFlag = false;

$(document).ready(function(){
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
    
 //加载制作合同列表
    loadProduceContractList();
 //获取制作合同的总信息
    totlContractList();
  
    $('.selectpicker').selectpicker({
        size: 3
    });
  //加载高级查询窗口
    initAdvanceSearch();
    
    //显示下拉列表
    showDropDown();
    
    //清空选中
    clearSelected();
    
    //导出窗口舒适化
    initExportWindow();
    
    //初始化导入窗口
    initImportProduceWin();
});


//查询制作合同列表
//定义查询制作合同的变量
var data = {};
//加载制作合同列表
function loadProduceContractList(){
	var source = {
			url: '/contractProduce/queryContractList',
			type: "post",
			data: data,
			dataType : "json",
			datafields : [
			     {name: "contractId", type: "string"},
			     {name: "company", type: "string"},
			     {name: "contractNo", type: "string"},
			     {name: "contactPerson", type: "string"},
			     {name: "totalMoney", type: "Double"},
			     {name: "payedMoney", type: "Double"},
			     {name: "leftMoney", type: "Double"},
			     {name: "currencyCode", type: "string"},
			     
			],
			root: 'contractPersonList'
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	//对方公司
	var companyNameRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='produce-name-column'>");
		html.push("	<a class='jqx-column float-left' href='javascript:modifyProduceContractInfo(\""+row+"\")'>" + rowdata.company + "</a>");
      html.push("</div>");
		return html.join("");
	};
	//合同编码
	var contractNoRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = [];
		html.push("<div class='contract-number-column'>");
		html.push("	<a class='jqx-column float-left' href='javascript:modifyProduceContractInfo(\""+row+"\")'>" + rowdata.contractNo + "</a>");
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
	
	$("#produceContractList").jqxGrid({
		width: "calc(100% - 2px)",
		height: "calc(100% - 140px)",
		columnsheight: 35,
		rowsheight: 30,
		source: dataAdapter,
		showtoolbar: true,
		columns: [
			        { text: '对方公司', datafield: 'company', cellsrenderer: companyNameRenderer, cellsAlign: "center", align: "center", width: '15%' },
			        { text: '合同编码', datafield: 'contractNo', cellsrenderer: contractNoRenderer, cellsAlign: "center", align: "center", width: '17%' },
			        { text: '负责人', datafield: 'contactPerson', cellsAlign: "center", align: "center", width: '17%' },
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
				container.push("<input type='button' class='export-contact' id='exportContractProduceBtn' onclick='exportContractProduceList()'>");
			}
			if(!isContractReadonly && hasImportContractAuth) {
				container.push("<input type='button' class='import-btn' id='importProduceBtn' onclick='showImportWin()'>");
			}
            container.push("</div>");
			
			toolbar.append($(container.join("")));
			
			$("#advanceSearchBtn").jqxTooltip({content: "高级搜索", position: "bottom"});
			if(!isContractReadonly){
				$("#addContractBtn").jqxTooltip({content: "添加合同", position: "bottom"});
			}
			if(hasExportContractAuth) {
				$("#exportContractProduceBtn").jqxTooltip({content: "导出制作合同", position: "bottom"});
			}
			if(!isContractReadonly && hasImportContractAuth) {
				$("#importProduceBtn").jqxTooltip({content: "导入制作合同", position: "bottom"});
			}
		}
	});
}


//获取制作合同总的统计信息列表
function totlContractList(){
	$.ajax({
		url: '/contractProduce/queryContractMoneyStatistics',
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
				$(".produce-contract-total").empty();
				$(".produce-contract-total").append($(html.join("")));
				
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
		height: 480,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			
		}
	});
}


//显示对方单位、联系人下拉框
function showDropDown(){
	$.ajax({
		url: '/contractProduce/queryDropDownList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var companyList = response.companyList;
				var contactPersonList = response.contactPersonList;
				var financeSubjList = response.financeSubjList;
				var html = [];
				var text = [];
				var financeHtml = [];
				if(companyList.length != 0){
					for(var i = 0; i < companyList.length; i++){
						html.push("<option value='" + companyList[i] +"'>" + companyList[i] + "</option>");
					}
				}
				$("#company").empty();
				$("#company").append(html.join(""));
				$('#company').selectpicker("refresh");
				if(contactPersonList.length != 0){
					for(var j = 0; j < contactPersonList.length; j++){
						text.push("<option value='" + contactPersonList[j] +"'>" + contactPersonList[j] + "</option>");	
					}
				}
				$("#contactPerson").empty();
				$("#contactPerson").append(text.join(""));
				$("#contactPerson").selectpicker("refresh");
				if(financeSubjList.length != 0){
					for(var i = 0; i < financeSubjList.length; i++){
						financeHtml.push("<option value='" + financeSubjList[i].financeSubjId +"'>" + financeSubjList[i].financeSubjName + "</option>");
					}
				}
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
function advanceQueryProduce(){
	var companys=$("#company").val();
	var result = "";
	var company="";
	if(companys != null){
		for(var i = 0; i< companys.length; i++){
	    	result += companys[i] + ",";
	    	
	    }
		company = result.substring(0, result.length-1);
	}
    
	data.companys = company;
	
	var contactPersons=$("#contactPerson").val();
	var outCome = "";
	var contactPerson="";
	if(contactPersons != null){
		for(var j = 0; j< contactPersons.length; j++){
			outCome += contactPersons[j] + ",";
	    	
	    }
		contactPerson = outCome.substring(0, outCome.length-1);
	}
	if($("#financeSubjId").val() != null){
		data.financeSubjIds = $("#financeSubjId").val().toString();
	}else{
		data.financeSubjIds = "";
	}
	data.payWay = $("input[type=radio][name=payWay]:checked").val();
	data.contactPersons = contactPerson;
	data.paymentTerm = $("#paymentTerm").val();
	data.remark = $("#remark").val();
	
	$("#advanceSearchWin").jqxWindow("close");
	$("#produceContractList").jqxGrid("updatebounddata");
}
//关闭高级查询
function closeQuery(){
	$("#advanceSearchWin").jqxWindow("close");
}
//清空高级查询
function clearQuery(){
	$('.selectpicker').selectpicker('deselectAll');
	$("#paymentTerm").val("");
	$("#remark").val("");
	$("input[type=radio][name=payWay]:checked").each(function(){
		$(this).prop("checked", false);
	});
}


//添加窗口
function openaddContract(){
	$("#contractId").val("");
	$(".right-popup-win").css({"width":"84%"}).show().animate({"right":"0"}, 300);
	/*$('#iframePage').attr('src','/contractWorker/toContractWorkerDetailPage');*/
	$("#contractDetailIframe").attr("src", "/contractProduce/toContractProduceDetailPage?contractId");
	
}

//关闭添加
//关闭添加
function closeAddPage(){
	var width = $(".right-popup-win").width();
	clearInterval(timer);
	$(".right-popup-win").animate({"right": 0-width},300);
	
	var timer = setTimeout(function(){
		$(".right-popup-win").hide();
	}, 300);
//	$(".right-popup-win").hide().animate({"right":"0"},200);
	$("#produceContractList").jqxGrid("updatebounddata");
	//显示对方单位、联系人下拉框
	showDropDown();
	//获取制作合同总的统计信息列表
	totlContractList();
	/*window.location.href = "/contractManager/toContractPage?contractType=3";*/
}



//修改制作合同信息
function modifyProduceContractInfo(editrow){

	var dataRecord = $("#produceContractList").jqxGrid('getrowdata', editrow);
	var contractIdValue = dataRecord.contractId;
	$(".right-popup-win").css({"width":"84%"}).show().animate({"right":"0"},200);
	$("#contractDetailIframe").attr("src", "/contractProduce/toContractProduceDetailPage?contractId="+ contractIdValue );
	
}

//导出窗口初始化
function initExportWindow(){
	$("#exportContractProduceWindow").jqxWindow({
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

//导出制作合同
function exportContractProduceList(){
	$("#exportContractProduceWindow").jqxWindow("show");
}

//确认导出制作合同
function confirmExportProduce(){
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
			url: '/contractProduce/exportContractProduceList',
			type: 'post',
			data:{companys:data.companys, contactPersons:data.contactPersons, financeSubjIds:data.financeSubjIds,
				payWay:data.payWay, paymentTerm:data.paymentTerm, remark:data.remark},
			datatype: 'json',
			success: function(response) {
				if (response.success) {
					var downLoadPath = response.downloadPath;
					var form = $("<form></form>");
	                form.attr("action","/fileManager/downloadFileByAddr");
	                form.attr("method","post");
	                form.append("<input type='hidden' name='address'>");
	                form.append("<input type='hidden' name='fileName' value='制作合同（统计列表）.xls'>");
	                form.find("input[name='address']").val(downLoadPath);
	                $("body").append(form);
					form.submit();
					form.remove();
	            	_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractProduceWindow").jqxWindow("close");
				}else {
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractProduceWindow").jqxWindow("close");
					showErrorMessage(response.message);
				}
			}
		});
	}else if (checkValue == '2') { //导出详细信息列表
		$.ajax({
			url: '/contractProduce/exportContractProduceDetail',
			type: 'post',
			datatype: 'json',
			success: function(response) {
				if (response.success) {
					var downLoadPath = response.downloadPath;
					var form = $("<form></form>");
	                form.attr("action","/fileManager/downloadFileByAddr");
	                form.attr("method","post");
	                form.append("<input type='hidden' name='address'>");
	                form.append("<input type='hidden' name='fileName' value='制作合同（详情）.xls'>");
	                form.find("input[name='address']").val(downLoadPath);
	                $("body").append(form);
					form.submit();
					form.remove();
	            	_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractProduceWindow").jqxWindow("close");
				} else {
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					$("#exportContractProduceWindow").jqxWindow("close");
					showErrorMessage(response.message);
				}
			}
		});
	}
}

//初始化导入窗口
function initImportProduceWin() {
	$("#importExportProduceWin").jqxWindow({
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
	$("#importExportProduceWin").jqxWindow("open");
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/contractProduce/imporContractProduceDetail&&needIsCover=true&&refreshUrl=/contractManager/toContractPage?contractType=3&&templateUrl=/template/import/制作合同详细信息导入模板.xls");
}

//关闭导入窗口
function closeImportWin(){
	$("#importExportProduceWin").jqxWindow("close");
}
