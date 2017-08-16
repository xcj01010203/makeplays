$(document).ready(function(){
	//校验是否进行了单据设置
	checkReceiptHasSetted();
	//校验是否需要财务密码
	checkNeedFinancePwd();
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
    //隐藏财务科目列表
    $(document).on("click", function(){
    	$('.fin_subj').css("display", "none");
    });
    
    $("#filterSearch").on("click", function(ev){
		ev.stopPropagation();
	});
    //先获取带有总金额的货币列表
    getContractCurrencyInfo();
	//加载表格的主体部分
	loadContractToPaidList();
	//获取当前用户信息
	getUserInfo();
	//获取当前日期
    getNowFormatDate();
  //获得科目信息-下拉选项
    getSubjNameInfo();
    //获得合同列表信息
    getContractNameInfo();
    //初始化筛选窗口
    initScreenWin();
  //初始化制作报销单窗口
    initMakeReimbursement();
    //初始化实付清单窗口
    initRealPaymentWin();
  //初始化下拉列表
	$('.selectpicker').selectpicker({
        size: 7
    });
	//显示清空按钮
	showClearBtn();
	clearSelected();
	//数字校验，只允许输入数字
	$("#payTotalMoney").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
});




//校验是否进行了单据设置
function checkReceiptHasSetted() {
	$.ajax({
		url: "/financeSettingManager/nopassword/checkBillHasSetted",
		type: "post",
		async: false,
		success: function(response) {
			if (!response.hasSetted) {
				window.location.href = "/financeSettingManager/toFinanceSettingPage?activeTagType=2";
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

//筛选条件
var data = {};
//货币列表
var currency;

//定义ajax方法
function sendRequest(options){
	$.ajax({
		url: options.url,
		type: options.type,
		data: options.data,
		datatype: options.datatype,
		success: options.getResult
	});
}


//加载货币信息
function getContractCurrencyInfo(){
	
	//先获取带有总金额的货币列表
	$.ajax({
		url: '/contractToPaidController/queryManyByMutiCondition',
		type: 'post',
		data: {ifEnable : true},
		datatype: 'json',
		async: false,
		success: function(response){
			//货币列表
			currency = response.currencyInfoList;
			
		}
	});
}

var jqxGridData;


//加载待付清单列表
function loadContractToPaidList(){
	data.status = '0,1';//默认只显示未付和待付
	var source = {
			url: 'contractToPaidController/queryContractToPaidList',
			type: "post",
			dataType : "json",
			data: data,
			datafields : [
			     {name: "paiddate", type: "string"},
			     {name: "contactNO", type: "string"},
			     {name: "contactname", type: "string"},
			     {name: "contacttype", type: "int"},
			     {name: "crewName", type: "string"},
			     {name: "crewid", type: "string"},
			     {name: "id", type: "string"},
			     {name: "money", type: "double"},
			     {name: "param", type: "string"},
			     {name: "paymentno", type: "string"},
			     {name: "subjectId", type: "string"},
			     {name: "contractId", type: "string"},
			     {name: "paymentId", type: "string"},
			     {name: "status", type: "int"},
			     {name: "subjectName", type: "string"},
			     {name: "subjectNameDetail", type: "string"},
			     {name: "subjectNameMain", type: "string"},
			     {name: "summary", type: "string"}
			],
			root: 'contractToPaidList'
	};
	
	for(var i= 0; i< currency.length; i++){
		var paidMoneyInfo = {name: currency[i].id, type: 'string'};
		source.datafields.push(paidMoneyInfo);
	}
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	console.log(dataAdapter);
	//代付款日期列
	var paiddateRender = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column'><p class='no-paid-color'>" + rowdata.paiddate + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 1){
			html.push("<div class='jqx-column'><p class='wait-paid-color'>" + rowdata.paiddate + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 2){
			html.push("<div class='jqx-column'><p class='comp-paid-color'>" + rowdata.paiddate + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 3){
			html.push("<div class='jqx-column'><p class='comp-paid-color'>" + rowdata.paiddate + "</p></div>");
			return html.join("");
		}
		else{
			html.push("<div class='jqx-column'><p class='comp-paid-color'></p></div>");
			return html.join("");
		}
	};
	//合同编码列
	var contactNoRender = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column'><p class='no-paid-color'>" + rowdata.contactNO + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 1){
			html.push("<div class='jqx-column'><p class='wait-paid-color'>" + rowdata.contactNO + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 2){
			html.push("<div class='jqx-column'><p class='comp-paid-color'>" + rowdata.contactNO + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 3){
			html.push("<div class='jqx-column'><p class='comp-paid-color'>" + rowdata.contactNO + "</p></div>");
			return html.join("");
		}
		else{
			html.push("<div class='jqx-column'><p class='comp-paid-color'></p></div>");
			return html.join("");
		}
	};
	//待付合同款项
	var fundDetailsRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			if(value != ""){
				html.push("<div class='jqx-column'><p class='no-paid-color text-align-right'>" + fmoney(value) + "</p></div>");
			}else{
				html.push("<div class='jqx-column'><p class='no-paid-color text-align-right'>" + value + "</p></div>");
			}
			
			return html.join("");
		}
		else if(rowdata.status == 1){
			if(value != ""){
				html.push("<div class='jqx-column'><p class='wait-paid-color text-align-right'>" + fmoney(value) + "</p></div>");
			}else{
				html.push("<div class='jqx-column'><p class='wait-paid-color text-align-right'>" + value + "</p></div>");
			}
			
			return html.join("");
		}
		else if(rowdata.status == 2){
			if(value != ""){
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + fmoney(value) + "</p></div>");
			}else{
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + value + "</p></div>");
			}
			
			return html.join("");
		}
		else if(rowdata.status == 3){
			if(value != ""){
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + fmoney(value) + "</p></div>");
			}else{
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + value + "</p></div>");
			}
			
			return html.join("");
		}
		else{
			if(value != ""){
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + fmoney(value) + "</p></div>");
			}else{
				html.push("<div class='jqx-column'><p class='comp-paid-color text-align-right'>" + value + "</p></div>");
			}
			
			return html.join("");
		}
	};
	//摘要
	var summaryRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column' title='"+ rowdata.summary +"'><p class='no-paid-color text-align-left'>" + rowdata.summary + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 1){
			html.push("<div class='jqx-column' title='"+ rowdata.summary +"'><p class='wait-paid-color text-align-left'>" + rowdata.summary + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 2){
			html.push("<div class='jqx-column' title='"+ rowdata.summary +"'><p class='comp-paid-color text-align-left'>" + rowdata.summary + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 3){
			html.push("<div class='jqx-column' title='"+ rowdata.summary +"'><p class='comp-paid-color text-align-left'>" + rowdata.summary + "</p></div>");
			return html.join("");
		}
		else {
			html.push("<div class='jqx-column' title='"+ rowdata.summary +"'><p class='comp-paid-color text-align-left'></p></div>");
			return html.join("");
		}
	};
	//科目
	var subjectNameRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column' title='"+ rowdata.subjectName+"'><p class='no-paid-color text-align-left'>" + rowdata.subjectName + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 1){
			html.push("<div class='jqx-column' title='"+ rowdata.subjectName+"'><p class='wait-paid-color text-align-left'>" + rowdata.subjectName + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 2){
			html.push("<div class='jqx-column' title='"+ rowdata.subjectName+"'><p class='comp-paid-color text-align-left'>" + rowdata.subjectName + "</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 3){
			html.push("<div class='jqx-column' title='"+ rowdata.subjectName+"'><p class='comp-paid-color text-align-left'>" + rowdata.subjectName + "</p></div>");
			return html.join("");
		}
		else{
			html.push("<div class='jqx-column' title='"+ rowdata.subjectName+"'><p class='comp-paid-color text-align-left'></p></div>");
			return html.join("");
		}
	};
	//状态
	var statusRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column'><p class='no-paid-color text-align-left'>未付</p></div>");
			return html.join("");
		}
		else if(rowdata.status == 1){
			html.push("<div class='jqx-column'><p class='wait-paid-color float-p text-align-left'>已生成待付单&nbsp;&nbsp;&nbsp;&nbsp;<a class='wait-paid-color' pid='"+ rowdata.id +"' conname='"+ rowdata.contactname +"' href='javascript:examinePaidDetail(\""+row+"\",\""+rowdata.status+"\");'>查看</a></p></div>");
			return html.join("");
		}
		else if(rowdata.status == 2){
			html.push("<div class='jqx-column'><p class='comp-paid-color float-p text-align-left'>已生成付款单&nbsp;&nbsp;&nbsp;&nbsp;(<a class='comp-paid-color'  href='javascript:examinePayment(\""+row+"\");'>"+ rowdata.paymentno +")</a></p></div>");
			return html.join("");
		}
		else if(rowdata.status == 3){
			html.push("<div class='jqx-column'><p class='comp-paid-color float-p text-align-left'>已结算&nbsp;&nbsp;&nbsp;&nbsp;(<a class='comp-paid-color'  href='javascript:examinePayment(\""+row+"\");'>"+ rowdata.paymentno +")</a></p></div>");
			return html.join("");
		}
		else{
			html.push("<div class='jqx-column'><p class='comp-paid-color float-p text-align-left'>状态异常</p></div>");
			return html.join("");
		}
	};
	
	var columnInformation = [];
	if(currency.length== 3){
		columnInformation.push({text: "待付款日期", datafield: 'paiddate', cellsrenderer: paiddateRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "合同编号", datafield: 'contactNO', cellsrenderer: contactNoRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '15%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "科目", datafield: 'subjectName', cellsrenderer: subjectNameRenderer, width: '20%', cellsAlign: 'left', align: 'center', sortable: false});
		var width = 0.324/currency.length*100;
		for(var k = 0; k < currency.length; k++){
			var columnFund = {text: "待付合同款(" + currency[k].code + ")", datafield: currency[k].id, cellsrenderer: fundDetailsRenderer,  width: width+"%", cellsAlign: 'right', align: 'center', sortable: false};
			columnInformation.push(columnFund);
		}
		columnInformation.push({text: "状态", datafield: 'status', cellsrenderer: statusRenderer, width: '15%', cellsAlign: 'left', align: 'center', sortable: false});
	}else if(currency.length == 2){
		columnInformation.push({text: "待付款日期", datafield: 'paiddate', cellsrenderer: paiddateRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "合同编号", datafield: 'contactNO', cellsrenderer: contactNoRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '20%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "科目", datafield: 'subjectName', cellsrenderer: subjectNameRenderer, width: '25%', cellsAlign: 'left', align: 'center', sortable: false});
		var width = 0.224/currency.length*100;
		for(var k = 0; k < currency.length; k++){
			var columnFund = {text: "待付合同款(" + currency[k].code + ")", datafield: currency[k].id, cellsrenderer: fundDetailsRenderer,  width: width+"%", cellsAlign: 'right', align: 'center', sortable: false};
			columnInformation.push(columnFund);
		}
		columnInformation.push({text: "状态", datafield: 'status', cellsrenderer: statusRenderer, width: '15%', cellsAlign: 'left', align: 'center', sortable: false});
	}else if(currency.length == 1){
		columnInformation.push({text: "待付款日期", datafield: 'paiddate', cellsrenderer: paiddateRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "合同编号", datafield: 'contactNO', cellsrenderer: contactNoRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '22.4%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "科目", datafield: 'subjectName', cellsrenderer: subjectNameRenderer, width: '25%', cellsAlign: 'left', align: 'center', sortable: false});
		var width = 0.15/currency.length*100;
		for(var k = 0; k < currency.length; k++){
			var columnFund = {text: "待付合同款", datafield: currency[k].id, cellsrenderer: fundDetailsRenderer,  width: width+"%", cellsAlign: 'right', align: 'center', sortable: false};
			columnInformation.push(columnFund);
		}
		columnInformation.push({text: "状态", datafield: 'status', cellsrenderer: statusRenderer, width: '20%', cellsAlign: 'left', align: 'center', sortable: false});
	}else if(currency.length == 0){
		columnInformation.push({text: "待付款日期", datafield: 'paiddate', cellsrenderer: paiddateRender, width: '10%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "合同编号", datafield: 'contactNO', cellsrenderer: contactNoRender, width: '10%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '25.4%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "科目", datafield: 'subjectName', cellsrenderer: subjectNameRenderer, width: '30%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "状态", datafield: 'status', cellsrenderer: statusRenderer, width: '23%', cellsAlign: 'left', align: 'center', sortable: false});
	}else{
		columnInformation.push({text: "待付款日期", datafield: 'paiddate', cellsrenderer: paiddateRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "合同编号", datafield: 'contactNO', cellsrenderer: contactNoRender, width: '8%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '13%', cellsAlign: 'left', align: 'center', sortable: false});
		columnInformation.push({text: "科目", datafield: 'subjectName', cellsrenderer: subjectNameRenderer, width: '18%', cellsAlign: 'left', align: 'center', sortable: false});
		var width = 0.364/currency.length*100;
		for(var k = 0; k < currency.length; k++){
			var columnFund = {text: "待付合同款(" + currency[k].code + ")", datafield: currency[k].id, cellsrenderer: fundDetailsRenderer,  width: width+"%", cellsAlign: 'right', align: 'center', sortable: false};
			columnInformation.push(columnFund);
		}
		columnInformation.push({text: "状态", datafield: 'status', cellsrenderer: statusRenderer, width: '15%', cellsAlign: 'left', align: 'center', sortable: false});
	}
	
	
	
	$("#paidOrderList").jqxGrid({
		width: "calc(100% - 2px)",
		height: "calc(100% - 40px)",
		source: dataAdapter,
		columnsheight: 35,
		rowsheight: 30,
		showtoolbar: true,
		selectionmode: "checkbox",
		/*localization: localizationobj,*/
		rendertoolbar: function(toolbar) {
			var container = [];
			container.push("<div class='toolbar'>");
			container.push('<input class="screen-btn" id="screenBtn" type="button" onclick="showScreenWin()">');
			//只读权限，不能进行生成代付单、生成实付单
			if(!isContractReadonly) {
				container.push('<input class="bill-paid-btn" id="billPaidBtn" type="button" onclick="showBillPaidWin()">');
				container.push('<input class="actual-pay-btn" id="actualPayBtn" type="button" onclick="actualPayBtn()">');
			}
            container.push("</div>");
			
			toolbar.append($(container.join("")));
			$("#screenBtn").jqxTooltip({content: "筛选", position: "bottom"});
			if(!isContractReadonly) {
				$("#billPaidBtn").jqxTooltip({content: "生成待付单", position: "bottom"});
				$("#actualPayBtn").jqxTooltip({content: "生成实付单", position: "bottom"});
			}
		},
		columns: columnInformation,
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

//获取当前用户信息
function getUserInfo(){
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var userInfo = response.userInfo;
				if(userInfo != null){
					$("#userAgent").text(userInfo.realName);
				}
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}


//初始化筛选窗口
function initScreenWin(){
	$("#ScreenWin").jqxWindow({
		theme: theme,
		width: 640,
		height: 380,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
	});
}

//获取当前日期
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
   /* $("#startDate").val(currentdate);
    $("#endDate").val(currentdate);*/
}


//获得合同列表信息-下拉选项
function getContractNameInfo(){
	$.ajax({
		url: 'contractToPaidController/queryDropList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var contractNameList= response.contractNameList;
				var subjectNameList = response.subjectNameList;
				var subjHtml=[],conHtml=[];
				for(var j= 0; j<contractNameList.length; j++){
					conHtml.push("<option value='"+ contractNameList[j] +"'>" + contractNameList[j] + "</option>");
				}
				$("#contractName").append(conHtml.join(""));
				$("#contractName").selectpicker("refresh");
				/*for(var i= 0; i< subjectNameList.length; i++){
					if(subjectNameList[i].subjectid == "blank"){
						subjHtml.push("<option value='blank'>[空]</option>");
					}else{
						subjHtml.push("<option value='"+ subjectNameList[i].subjectid +"'>" + subjectNameList[i].subjectname + "</option>");
					}
					
				}
				
				$("#paidSubjectName").append(subjHtml.join(""));
				$("#paidSubjectName").selectpicker("refresh");*/
				
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
	
}

//获得科目下拉列表
function getSubjNameInfo(){
	var source = {
			url: 'contractToPaidController/queryDropList',
			datatype: 'json',
			dataFields: [
			    {name: 'id',type: 'string'},
			    {name: 'name',type: 'string'},
			    {name: 'level',type: 'int'},
			    {name: 'parentId',type: 'string'},
			],
			hierarchy:{
				keyDataField: {name:'id'},
				parentDataField: {name:'parentId'}
			},
			id: 'id',
			root: 'subjectNameList',
		};
	 var dataAdapter = new $.jqx.dataAdapter(source);
	 $("#subjectTreeSearch").jqxTreeGrid({
		 width: 220,
		 height:200,
		 source: dataAdapter,
	     /*filterable: true,*/
	     //filterMode:'advanced',
		 showHeader: false,
		 ready: function(){},
		    columns: [
		          { text: '财务科目', dataField: 'name', width: 200, align: "center" }
		        ]
     });
     $('#subjectTreeSearch').on('rowSelect', function (event){
         
         var args = event.args;
         var key = args.key;
         var row = $("#subjectTreeSearch").jqxTreeGrid('getRow', key);
         
         if(row.expanded == true){
               $("#subjectTreeSearch").jqxTreeGrid('collapseRow', key);
         }else{
               $("#subjectTreeSearch").jqxTreeGrid('expandRow', key);
         }
         $("#subjectTreeSearch").jqxTreeGrid('clearSelection');
         var records = row.records;
         if(records == undefined){
//        	 	var text = $("input[name=subval]").val();
                var subjectName = row.name;
                var b = true;
                var par = row;
                while(b){
	                if(par.parent != undefined){
	                	subjectName += "-" + par.parent.name;
	                 	par = par.parent;
	                }else{
	                 	b = false;
	                }
                }
		          var names = subjectName.split("-");
		          var name = "";
		          for(var i = names.length-1;i>=0;i--){
		              name += names[i];
		              if(i != 0){
		                  name += "-";
		              }
		          }
		          $("#subjectName").val(name);
		          $("#subjectName").attr("fid", row.id);
		          $('.fin_subj').css("display", "none");
             }
        });
		$("#filterSearchBtn").click(function(ev){
		    var name = $("#filter").val();
		    var filtertype = 'stringfilter';
		    // create a new group of filters.
		    var filtergroup = new $.jqx.filter();
		    var filter_or_operator = 1;
		    var filtervalue = name;
		    var filtercondition = 'CONTAINS';
		    var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
		    filtergroup.addfilter(filter_or_operator, filter);
		    // add the filters.
		    $("#subjectTreeSearch").jqxTreeGrid('addFilter', 'name', filtergroup);
		    // apply the filters.
		    $("#subjectTreeSearch").jqxTreeGrid('applyFilters');
		    $("#subjectTreeSearch").jqxTreeGrid('expandAll');
		    ev.stopPropagation();
		});
		
		$("#filterSearch").on("keydown", function(ev){
			if(ev.keyCode == 13) {
				var name = $("#filterSearch").val();
				var filtertype = 'stringfilter';
			      // create a new group of filters.
			      var filtergroup = new $.jqx.filter();
			      var filter_or_operator = 1;
			      var filtervalue = name;
			      var filtercondition = 'CONTAINS';
			      var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
			      filtergroup.addfilter(filter_or_operator, filter);
			      // add the filters.
			      $("#subjectTreeSearch").jqxTreeGrid('addFilter', 'name', filtergroup);
			      // apply the filters.
			      $("#subjectTreeSearch").jqxTreeGrid('applyFilters');
			      $("#subjectTreeSearch").jqxTreeGrid('expandAll');
			}
			ev.stopPropagation();
		});
		
		//阻止冒泡事件
		$("#levelPopupSearch").on("click", function(ev){
			ev.stopPropagation();
		});
		
		
		$('#subjectName').click(function(ev){
			var obj = $(this);
			$('.fin_subj').css({left:obj.position().left,top:obj.position().top+32, "display": "block"});
			$('.fin_subj #filterSearch').focus();
		    ev.stopPropagation();
		});
}




//显示清空按钮
function showClearBtn(){
	//显示清空按钮
	$('.screen-list li').on('mouseover', function(event) {
        if ($(this).find("select").val() != null && $(this).find("select").val() != undefined) {
            $(this).find(".clearSelection").show();
        }
    });
	
    
	$('.screen-list li').on('mouseout', function(event) {
        $(this).find(".clearSelection").hide();
    });
}
//清空下拉选项
function clearSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
}



//显示筛选窗口
function showScreenWin(){
	$("#ScreenWin").jqxWindow("open");
}
//关闭筛选窗口
function cancelScrren(){
	$("#ScreenWin").jqxWindow("close");
}
function searchContractToPaidInfoList(){
	var startDate = $('#startDate').val();
	var endDate = $('#endDate').val();
	var contractType = "";
	$('input[name=contractType]:checked').each(function(){
		contractType +=$(this).val()+",";
	});
	var contractStatus ="";
	$('input[name=contractStatus]:checked').each(function(){
		contractStatus += $(this).val()+",";
	});
	if($('#contractName').val()!= null){
		data.contractName = $("#contractName").val().toString();
	}else{
		data.contractName= "";
	}
	data.financeSubjectId = $("#subjectName").attr("fid");
	data.startDate = startDate;
	data.endDate = endDate;
	data.contractType = contractType.substr(0, contractType.length-1);
	data.status = contractStatus.substr(0, contractStatus.length-1);
	//解绑事件
	$("#paidOrderList").unbind('bindingcomplete');
	$("#paidOrderList").jqxGrid("updatebounddata");
	$('#paidOrderList').jqxGrid('clearselection');
	cancelScrren();
}

//清空查询条件
function clearScreen(){
	$('.selectpicker').selectpicker('deselectAll');
	$("#subjectName").val("");
	$("#subjectName").attr("fid","");
	$("#startDate").val("");
	$("#endDate").val("");
	$("#ScreenWin input[type=checkbox]:checked").each(function(){
		$(this).prop("checked", false);
	});
}

/*
 * 所用变量说明
 * pid：查看代付单的id
 * payid:查看实付单的id
 * paymentId:付款单的id****/


//初始化生成待付单窗口
function initMakeReimbursement(){
	$("#makeReimbursementWin").jqxWindow({
		theme: theme,
		width: 1310,
		height: 600,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
	$("#makeReimbursementWin").on("close", function(event){
		$("#paidOrderList").jqxGrid("updatebounddata");
	});
	$("#realPaymentWin").on("close", function(event){
		$("#paidOrderList").jqxGrid("updatebounddata");
	});
}
//显示生成待付单窗口
function showBillPaidWin(){
	var rowindexes = $("#paidOrderList").jqxGrid("getselectedrowindexes");
	var ids= [];//存放选择的id
	if(rowindexes.length <=0){
		showInfoMessage("请选择合同");
		return;
	}else{
		$("#personnelList").find("li").remove();
		var rowindexes = $("#paidOrderList").jqxGrid("getselectedrowindexes");
		
		for(var i= 0; i< rowindexes.length; i++){
			var html = [];
			var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			if(status == 2){
				showErrorMessage("所选合同包含已经支付的合同,请重新选择");
				return;
			}
			if(status == 1){
				showErrorMessage("所选合同包含已经生成待付单的合同,请重新选择");
				return;
			}
			var pid= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			ids.push(pid);
			var pname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).param;
			var contactname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).contactname;
			html.push("<li><a href='javascript:void(0);' pid='"+ pid +"' onclick='clickPersonalList(this)'>"+ pname +"-"+ contactname +"</a></li>");
			$("#personnelList").append(html.join(""));
			if(i==0){
				$("#personnelList li").addClass("click-style");
				//默认将第一个的id传过去
				getOnlyContractInfo(pid);
			}
		}
		
		$("#makeReimbursementWin").jqxWindow("open");
		changeStatus(ids);
	}
	
}

//人员列表的单击事件
function clickPersonalList(own){
	var $this = $(own);
	$this.parent("li").addClass("click-style");
	$this.parent("li").siblings("li.click-style").removeClass("click-style");
	var id = $this.attr("pid");
	//传id
	getOnlyContractInfo(id);
}

//获取单个合同的信息-待付单
function getOnlyContractInfo(id){
	var options= {
			url: 'contractToPaidController/queryContractToPaidListById',
			type: 'post',
			data: {'id':id},
			datatype: 'json',	
			getResult: function(response){
				if(response.success){
					var contractToPaidMap = response.contractToPaidMap;
					if(contractToPaidMap.length != 0){
						for(var i= 0; i< contractToPaidMap.length; i++){
							$("#currentCrewName").text("《"+ contractToPaidMap[i].crewName + "》");
							$("#detailInfoTableOne").find("tr.detail-info-tr").eq(0).find("div").eq(2).text(contractToPaidMap[i].summary);
							$("#detailInfoTableOne").find("tr.detail-info-tr").eq(0).find("div").eq(3).text(contractToPaidMap[i].subjectNameMain);
							$("#detailInfoTableOne").find("tr.detail-info-tr").eq(0).find("div").eq(4).text(contractToPaidMap[i].subjectNameDetail);
							$("#detailInfoTableOne").find("tr.detail-info-tr").eq(0).find("input[type=text]").eq(0).val(fmoney(contractToPaidMap[i].money));
							$("#detailInfoTableOne").find("tr.money-tr").eq(0).find("div").eq(0).text(numberToCapital(contractToPaidMap[i].money));
							$("#detailInfoTableOne").find("tr.money-tr").eq(0).find("div").eq(1).text(fmoney(contractToPaidMap[i].money));
							$("#detailInfoTableOne").find("tr.money-tr").eq(0).find("input[type=hidden]").eq(0).val(contractToPaidMap[i].money);
						}
						
					}
					
				}else{
					showErrorMessage(response.message);
				}
			}
	};
	sendRequest(options);
}


//更改状态
function changeStatus(ids){
	var idsArray  = ids;
	ids= ids.join(",");
	$.ajax({
		url: 'contractToPaidController/updateContractToPaidInfo',
		type: 'post',
		data: {id: ids, status: 1},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#paidOrderList").jqxGrid("updatebounddata");
				$("#paidOrderList").on('bindingcomplete', function () {
					changeCheckStatus(idsArray);
				 });
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//重新选中复选框
function changeCheckStatus(idsArray){
	$('#paidOrderList').jqxGrid('clearselection');
	var rows = $('#paidOrderList').jqxGrid('getrows');
	var jqxRowIndexes;
	if(rows.length != 0 || idsArray.length != 0){
		for(var i= 0; i< idsArray.length; i++){
			for(var j= 0; j< rows.length; j++){
				if(idsArray[i] == rows[j].id){
					jqxRowIndexes = j;
					$("#paidOrderList").jqxGrid('selectrow', jqxRowIndexes);
				}
			}
			
		}
		
	}
}




//查看待付单
function examinePaidDetail(editrow){
	var dataRecord = $("#paidOrderList").jqxGrid('getrowdata', editrow);
	var nowPid= dataRecord.id;
	
	/*$("#personnelList").find("li").remove();
	$("#makeReimbursementWin").jqxWindow("open");*/
	var flag = true;
	var idsArray = [];
	var rowindexes = $("#paidOrderList").jqxGrid("getselectedrowindexes");

	if(rowindexes.length != 0){
		$.each(rowindexes,function(i){
			var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			if(status != dataRecord.status){
				showErrorMessage("请选择状态相同的单据查看");
				flag= false;
			}
		});
		if(flag== false){
			return;
		}
		
		$.each(rowindexes,function(i){
			var id=$("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			idsArray.push(id);
		});
		
		for(var j= 0; j< idsArray.length; j++){
			if(dataRecord.id != idsArray[j]){
				
			}else{
				flag = false;
			}
		}
		
		if(flag == false){
			 
		 }else{
			 showErrorMessage("请选择选中的单据查看");
			 return;
		 }
		
		
		
		$("#personnelList").find("li").remove();
		/*$("#makeReimbursementWin").jqxWindow("open");*/
		
		
		for(var i= 0; i< rowindexes.length; i++){
			var html = [];
			var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			if(status == 0 && status == 2){
				showErrorMessage("所选合同包含未付或者已付的合同,请重新选择");
				return;
			}
			
			var pid= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			var pname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).param;
			var contactname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).contactname;
			html.push("<li><a href='javascript:void(0);' pid='"+ pid +"' onclick='clickPersonalList(this)'>"+ pname +"-"+ contactname +"</a></li>");
			$("#personnelList").append(html.join(""));
			
		}
	}else{
		$("#personnelList").empty();
		var pid = nowPid;
		var pname=  dataRecord.param;
		var contactname = dataRecord.contactname;
		var html = [];
		html.push("<li><a href='javascript:void(0);' pid='"+ pid +"' onclick='clickPersonalList(this)'>"+ pname +"-"+ contactname +"</a></li>");
		$("#personnelList").append(html.join(""));
	}
	
	$("#personnelList li").find("a").each(function(){
		if($(this).attr("pid") == nowPid){
			$(this).parent("li").addClass("click-style");
			getOnlyContractInfo(nowPid);
		}
	});
	
	$("#makeReimbursementWin").jqxWindow("open");
	
}







//上一张按钮
function prevOrder(){
	$("#personnelList li.click-style").each(function(){
		if($(this).prev("li").length == 0){
			showInfoMessage("已经是第一张");
			return;
		}
		$(this).prev("li").addClass("click-style");
	    $(this).removeClass("click-style");
	    var id= $("#personnelList li.click-style").find("a").attr("pid");
	    getOnlyContractInfo(id);
	});
}

//下一张按钮
function nextOrder(){
	$("#personnelList li.click-style").each(function(){
		if($(this).next("li").length == 0){
			showInfoMessage("已经到最后一张");
			return;
		}
		$(this).next("li").addClass("click-style");
		$(this).removeClass("click-style");
		var id= $("#personnelList li.click-style").find("a").attr("pid");
	    getOnlyContractInfo(id);
	});
}


//当前金额
var nowMoney;

//保存待付清单--同步金额
function changeMoney(own){
	var $this = $(own);
	if($this.val() != ""){
		var totalMoney = $this.val().replace(/,/g, "")-0;
		if(nowMoney == totalMoney){
		}else{
			var subData = {};
		    subData.id= $("li.click-style").find("a").attr("pid");
		    if(isNaN(totalMoney)){
		    	showErrorMessage("请填写正确的金额信息");
		    	return ;
		    }
		    $this.val(fmoney(totalMoney));
		    $("#readOnlyMoney").text(fmoney(totalMoney));
			$("#capitalPayMoney").text(numberToCapital(totalMoney));
		    subData.money = totalMoney;
			$.ajax({
				url: 'contractToPaidController/updateContractToPaidInfo',
				type: 'post',
				data: subData,
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("保存成功");
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		}
		
	}else{
		showErrorMessage("请填写金额");
	}
}

//修改金额-获得焦点时
function modifyMoney(own){
	var $this = $(own);
	var nowTotalMoney = $this.val().replace(/,/g, "");
	nowMoney = nowTotalMoney;
	$this.val(nowTotalMoney);
}



//初始化实付清单窗口
function initRealPaymentWin(){
	$("#realPaymentWin").jqxWindow({
		theme: theme,
		width: 1290,
		height: 600,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
	});
}

//显示实付清单窗口
function actualPayBtn(){
	var rowindexes = $("#paidOrderList").jqxGrid("getselectedrowindexes");
	var ids = [];
	if(rowindexes.length <=0){
		showInfoMessage("请选择合同");
		return;
	}else{
//		$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
		$("#paymentPersonnelList").find("li").remove();
	    $("#paymentPersonnelList").empty();
	    
		for(var i= 0; i< rowindexes.length; i++){
			var html = [];
			var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			if(status == 0 || status == 2){
				showErrorMessage("所选合同包含未支付的或者已支付合同,请重新选择");
				return;
			}
			if(status == 3){
				showErrorMessage("所选合同包含已结算的合同,请重新选择");
				return;
			}
			var payId= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			ids.push(payId);
			var paymentId = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).paymentId;
			var pname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).param;
			var contactname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).contactname;
			if(paymentId == null){
				paymentId = "";
			}
			html.push("<li><a href='javascript:void(0);' status='" + status +"' paymentId='"+ paymentId +"' payid='"+ payId +"' onclick='clickPaymentList(this)'>"+ pname +"-"+ contactname +"</a></li>");
			$("#paymentPersonnelList").append(html.join(""));
			//待付状态
			if(i==0){
				$("#paymentPersonnelList li").addClass("click-style");
				//默认将第一个的id传过去
//				$("#realPaymentWin").jqxWindow("open");
				/*$("#rightModularPayment").empty();*/
				/*var id= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;*/
				queryPaymentOrder(payId);
		    }
		
		
		}
		$("#realPaymentWin").jqxWindow("open");
		
	}
	
}

//查询付款单详细信息
function queryPaymentOrder(id){
	$.ajax({
		url: 'contractToPaidController/queryContractToPaidListById',
		type: 'post',
		data: {"id" : id },
		datatype: 'post',
		success: function(response){
			if(response.success){
				$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
				var contractToPaidMap = response.contractToPaidMap;
				if(contractToPaidMap.length != 0){
					for(var i= 0; i< contractToPaidMap.length; i++){
						var contactname = contractToPaidMap[i].contactname;
						var contractId = contractToPaidMap[i].contractId;
						var contracttype = contractToPaidMap[i].contacttype;
						var currencyId = contractToPaidMap[i].currencyId+"";
						var summary = contractToPaidMap[i].summary;
						var subjectName = contractToPaidMap[i].subjectName;
						var subjectId = contractToPaidMap[i].subjectId;
						var money = contractToPaidMap[i].money;
						document.getElementById('fullReceiptDiv').onload=function(){ 
							var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
							_frame.find("#receivingParty").val(contactname);
							_frame.find("#payContractMoney").prop("checked", true).attr("disabled", true);
							_frame.find("#receivePersonDropdown").css("display","block");
							_frame.find("#contractId").val(contractId);
							_frame.find("#contractId").attr("finsubjid", subjectId);;
							_frame.find("#contractType").val(contracttype);
							_frame.find("#currency").val(currencyId);
							var addSubTr = _frame.find(".add-sub-tr").eq(0);
							addSubTr.find("input[type=text]").eq(0).val(summary);
							addSubTr.find("input[type=text]").eq(1).val(subjectName);
							addSubTr.find("input[type=text]").eq(1).attr("fid", subjectId);
							addSubTr.find("input[type=text]").eq(1).next("input[type=hidden]").val(subjectId);
							addSubTr.find("input[type=text]").eq(2).val(fmoney(money));
							addSubTr.find("input[type=text]").eq(2).next("input[type=hidden]").val(money);
							_frame.find("#readTotalMoney").val(money);
							_frame.find("#formatTotalMoney").text(fmoney(money));
							_frame.find("#totalAccountMoney").text(numberToCapital(money));
//							$("#repayLoans").css("visibility", "hidden");
							_frame.find(".addition").css("disabled", true);
							_frame.find(".subtraction").css("disabled", true);
							//设置下拉按钮的内容
					         this.contentWindow.setDorpDownContent(contactname);  
					    };
					    
						
					}
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//查看付款单
function examinePayment(editrow){
	var dataRecord = $("#paidOrderList").jqxGrid('getrowdata', editrow);
	var nowPayId = dataRecord.id;
	var idsArray = [];
	$("#paymentPersonnelList").find("li").remove();
	
	 var rowindexes = $("#paidOrderList").jqxGrid("getselectedrowindexes");
	 var flag = true;
	
	 if(rowindexes.length != 0){//当选中多个时
		
		 $.each(rowindexes, function(i){
			 var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			 if(status != dataRecord.status){
				 flag = false;
			 }
		 });
		 
		 if(flag == false){
			 showErrorMessage("请选择状态相同的单据查看");
			 return;
		 }
		 
		 $.each(rowindexes, function(i){
			 var payId= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			 idsArray.push(payId);
		 });
		 
		 for(var j=0; j< idsArray.length; j++){
			 if(dataRecord.id != idsArray[j]){
				 
			 }else{
				 flag = false;//说明选择的id包含在其中
			 }
		 }
		 if(flag == false){
			 
		 }else{
			 showErrorMessage("请选择选中的单据查看");
			 return;
		 }
		 
		 
		 
		 for(var i= 0; i< rowindexes.length; i++){
			 var html=[];
			 var status = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).status;
			 if(status == 0){
				 showErrorMessage("所选合同包含未付的合同,请重新选择");
				 return;
			 }
			 var payId= $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).id;
			 var paymentId = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).paymentId;
			 var pname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).param;
			 var contactname = $("#paidOrderList").jqxGrid("getrowdata", rowindexes[i]).contactname;
			 if(paymentId == null){
				 paymentId = "";
			 }
			 html.push("<li><a href='javascript:void(0);' paymentId='"+ paymentId +"' payid='"+ payId +"' status='"+ status +"' onclick='clickPaymentList(this)'>"+ pname +"-"+ contactname +"</a></li>");
			 $("#paymentPersonnelList").append(html.join(""));
		 }
	 }else{//没有选中，直接点击付款单号查看
		 var payId = dataRecord.id;
		 var paymentId = dataRecord.paymentId;
		 var pname = dataRecord.param;
		 var contactname = dataRecord.contactname;
		 var status = dataRecord.status;
		 var html = [];
		 if(paymentId == null){
			 paymentId == "";
		 }
		 html.push("<li><a href='javascript:void(0);' paymentId='"+ paymentId +"' payid='"+ payId +"' status='"+ status +"' onclick='clickPaymentList(this)'>"+ pname +"-"+ contactname +"</a></li>");
		 $("#paymentPersonnelList").append(html.join(""));
	 }
	 
	 $("#paymentPersonnelList li").find("a").each(function(){
		 if($(this).attr("payid") == nowPayId){
			 $(this).parent("li").addClass("click-style");
			 var paymentId = $(this).attr("paymentId");
			 var payId= $(this).attr("payid");
			 var status = $(this).attr("status");
			 if(status == 3){
				 $("#saveModifyPaymentBtn").css("visibility","hidden");
			 }else{
				 $("#saveModifyPaymentBtn").css("visibility","visible");
			 }
			 if(paymentId != ""){
				    $("#fullReceiptDiv").attr("src","/paymentManager/toPaymentDetailPage?paymentId="+paymentId +"&&isContractToPaid=true");
			 }else{
				 $("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
				 queryPaymentOrder(payId);
			    	
			 }
		 }
	 });
	 $("#realPaymentWin").jqxWindow("open");
}



//选择付款单-查看详细信息
function clickPaymentList(own){
	
	var $this= $(own);
	var status= $this.attr("status");
	$this.parent("li").addClass("click-style");
	$this.parent("li").siblings("li.click-style").removeClass("click-style");
	var paymentId = $this.attr("paymentId");
	var payId = $this.attr("payid");
	if(status == 3){
		 $("#saveModifyPaymentBtn").css("visibility","hidden");
	 }else{
		 $("#saveModifyPaymentBtn").css("visibility","visible");
	 }
//	if(status == 1){
//		
//    	
//	}
//	if(status == 2){
//		$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?paymentId="+paymentId+"&&isContractToPaid=true");
//	}
	
	
//	$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
	queryPaymentOrder(payId);
	
}

//上一张
function prevPaymentOrder(){
	$("#paymentPersonnelList li.click-style").each(function(){
		if($(this).prev("li").length == 0){
			showInfoMessage("已经是第一张");
			return;
		}
		$(this).prev("li").addClass("click-style");
	    $(this).removeClass("click-style");
//	    var paymentId= $("#paymentPersonnelList li.click-style").find("a").attr("paymentId");
	    var payId= $("#paymentPersonnelList li.click-style").find("a").attr("payid");
	    var status = $("#paymentPersonnelList li.click-style").find("a").attr("status");
	    if(status == 3){
			 $("#saveModifyPaymentBtn").css("visibility","hidden");
		 }else{
			 $("#saveModifyPaymentBtn").css("visibility","visible");
		 }
//	    if(paymentId!='null'&&paymentId != ""){
//	    	$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?paymentId="+paymentId +"&&isContractToPaid=true");
//	    }else{
//	    	
//	    }
	    
	    
//	    $("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
    	queryPaymentOrder(payId);
		
	});
}
//下一张
function nextPaymentOrder(){
	$("#paymentPersonnelList li.click-style").each(function(){
		if($(this).next("li").length == 0){
			showInfoMessage("已经是最后一张");
			return;
		}
		$(this).next("li").addClass("click-style");
	    $(this).removeClass("click-style");
//	    var paymentId= $("#paymentPersonnelList li.click-style").find("a").attr("paymentId");
	    var payId= $("#paymentPersonnelList li.click-style").find("a").attr("payid");
	    var status = $("#paymentPersonnelList li.click-style").find("a").attr("status");
	    if(status == 3){
			 $("#saveModifyPaymentBtn").css("visibility","hidden");
		 }else{
			 $("#saveModifyPaymentBtn").css("visibility","visible");
		 }
//	    if(paymentId!= "null" && paymentId != ""){
//	    	$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?paymentId="+paymentId +"&&isContractToPaid=true");
//	    }else{
//	    	
//	    }
	    
	    
	    
//	    $("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
    	queryPaymentOrder(payId);
	});
}


//获取财务科目信息
function financeSubjInfo(){
    var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var financeRecordInfo ="";
	var addSubTr = _frame.find("tr.add-sub-tr");
	$.each(addSubTr, function(i){
		
		financeRecordInfo += $(this).find("input[type=text]").eq(0).val()+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).attr("fid")+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).val()+"##";
		var noformatMoney = $(this).find("input[type=text]").eq(2).next().val();
		financeRecordInfo += noformatMoney +"&&";
	});
	return financeRecordInfo.substr(0,financeRecordInfo.length-2);
}

//保存付款单
function saveModifyPaymentOrder(){
	    var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
		var subData = {};
		subData.paymentId = _frame.find("#paymentId").val();
		subData.receiptNo = _frame.find("#billsNum").val();
		subData.paymentDate = _frame.find("#billsDateTime").val();
		subData.payeeName = _frame.find("#receivingParty").val();
		
		subData.contractId = _frame.find("#contractId").val();
		subData.contractType = _frame.find("#contractType").val();
		var loanIds=_frame.find("#loanIds").val();
//		loanIds= loanIds.substring(0, loanIds.length-1);
//		
		subData.loanIds= loanIds;
		subData.status = 0;
		
		subData.currencyId = _frame.find("#currency").val();
		subData.totalMoney = _frame.find("#readTotalMoney").val()-0;
		subData.paymentWay = _frame.find("#paymentWay").val();
		subData.contractPartIds = _frame.find("#contractPartIds").val();
		if(_frame.find("#hasReceipt").val() == "true"){
			subData.hasReceipt = true;
			subData.billType = _frame.find("#billType").val();
			if(_frame.find("#ifReceiveBill").val() == "true"){
				subData.remindTime = "";
				subData.ifReceiveBill = true;
				
			}else{
				subData.ifReceiveBill = false;
				subData.remindTime = _frame.find("#remindTimeValue").val();
				
			}
		}else{
				subData.hasReceipt = false;
				subData.ifReceiveBill = false;
				subData.billType = "";
				subData.remindTime = "";
		}
		subData.billCount = _frame.find("#billCount").val()-0;
		subData.agent = _frame.find("#agent").val();
		subData.paymentSubjMapStr = financeSubjInfo();
		subData.contractPartId = $("#paymentPersonnelList li.click-style").find("a").attr("payid");
		subData.department = _frame.find("#departmentText").val();
		$.ajax({
			url: '/paymentManager/savePaymentInfo',
			type: 'post',
			data: subData,
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("保存成功");
					$("#paidOrderList").jqxGrid("updatebounddata");
					/*$('#paidOrderList').jqxGrid('clearselection');*/
					$("#paymentPersonnelList li.click-style").find("a").attr("paymentid", response.paymentId);
					//当保存成功以后
					
					
					var li_length=0;//判断列表的个数
					$("#paymentPersonnelList li").each(function(){
						li_length++;
					});
					if(li_length == 0){
						 $("#realPaymentWin").jqxWindow("close");
					}else{
						if($("#paymentPersonnelList li.click-style").next("li").length != 0){
							$("#paymentPersonnelList li.click-style").removeClass("click-style").next("li").addClass("click-style");
							$("#paymentPersonnelList li.click-style").prev("li").remove();
							var payId = $("#paymentPersonnelList li.click-style").find("a").attr("payid");
							$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
							queryPaymentOrder(payId);
						}else if($("#paymentPersonnelList li.click-style").prev("li").length != 0){
							$("#paymentPersonnelList li.click-style").removeClass("click-style").prev("li").addClass("click-style");
							$("#paymentPersonnelList li.click-style").next("li").remove();
							var payId = $("#paymentPersonnelList li.click-style").find("a").attr("payid");
							$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?isContractToPaid=true");
							queryPaymentOrder(payId);
						}else{
							$("#realPaymentWin").jqxWindow("close");
						}
					}
					
					
					$("#paidOrderList").on('bindingcomplete', function () {
						var ids = [];
						$("#paymentPersonnelList li").each(function(){
							ids.push($(this).find("a").attr("payid"));
						});
						changeCheckStatus(ids);
					 });
				}else{
					showErrorMessage(response.message);
				}
			}
		});
}





//全部打印
function printAllPaidOrder(){
	var ids=[];
	$("#personnelList li").each(function(){
		var id= $(this).find("a").attr("pid");
		ids.push(id);
	});
	window.open('contractToPaidController/toContractToPaidPrintPage?paidId='+ids.join(","), '_blank');
}

//打印当前页面
function printPaidOrder(){
	var id= $("#personnelList li.click-style").eq(0).find("a").attr("pid");
	window.open('contractToPaidController/toContractToPaidPrintPage?paidId='+id, '_blank');
}