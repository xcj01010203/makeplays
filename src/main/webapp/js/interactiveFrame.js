var singleCurrencyFlag = false;
var loanConfirmed = false;	//是否点击过还借款窗口的确定按钮
var prePaymentId = "";	//上一份付款单的ID
var prePayeeName = "";	//上一个收款方
var preTotalPayedMoney = "";	//上一个付款单总金额
var preCurrency = "";	//上一个币种
//var _frame=$('#fullReceiptDiv').contents();
$(document).ready(function() {
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
	
	initSetTaxWin();
	//阻止冒泡事件
	$("#levelPopup").on("click", function(ev){
		ev.stopPropagation();
	});
	$(document).click(function(){
    	$('#levelPopup').css("display", "none");
    });
	loadFinanceSubject();
});

//还借款方面的数据
function payedLoanFinanceData(){
	var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
	var currency = _frame.find("#currency").find("option:selected").text();
	var currencyCode = currency.split("-")[0];
	var receivingParty = _frame.find("#receivingParty").val();
	console.log(receivingParty);
	if(_frame.find("#receivingParty").val() == ""){
		showErrorMessage("请填写收款人(单位)");
		return false;
	}
	if(_frame.find("#readTotalMoney").val() == ""){
		parent.showErrorMessage("请填写金额");
		return false;
	}
	$("#name").text(receivingParty);
	var html = [];
	html.push("合计金额("+ currencyCode +"): <span class='money-count-color'>"+_frame.find("#formatTotalMoney").text() +"</span>");
	html.join("");
	$("#loanAccountMoney").empty();
	$("#loanAccountMoney").append(html);
	
	$("#queryNotPayedLoanList").jqxWindow("open");
	
	var myPaymentId = _frame.find("#paymentId").val();
	var myPayeeName = _frame.find("#receivingParty").val();
	var myTotalPayMoney = _frame.find("#readTotalMoney").val();
	
	if (preCurrency != currency || preTotalPayedMoney != myTotalPayMoney || prePayeeName != myPayeeName || myPaymentId != prePaymentId || !loanConfirmed) {
		if (_frame.find("#loanIds").val() == '') {
			saveLoanIds = '';
		} else {
			saveLoanIds = _frame.find("#loanIds").val() + ",";
		}
		
		//如果收款人换了或如果币种换了，则清空已有的借款单信息
//		if ((prePayeeName != "" && prePayeeName != myPayeeName)) {
//			saveLoanIds = '';
//		}
		
		publicPayedMoney = myTotalPayMoney;
		$("#loanOrderId").html("借款。");
		$("#payedMoneyCount").html("");
		$("#balanceTips").text("");
		
		loadLoanDetail();
		
		prePaymentId = myPaymentId;
		prePayeeName = myPayeeName;
		preTotalPayedMoney = myTotalPayMoney;
		preCurrency = currency;
	}
}


//加载借款单详细信息
function loadLoanDetail(){
	var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
	var paymentId = _frame.find("#paymentId").val();
	var source = {
		url: '/loanInfoManager/queryNotPayedLoanList',
		type: 'post',
		data: {payeeName: _frame.find("#receivingParty").val(), paymentId: paymentId},
		datatype: 'json',
		datafields : [
		 		     {name: "loanId", type: "string"},
		 		     {name: "loanDate", type: "string"},
		 		     {name: "receiptNo", type: "string"},
		 		     {name: "currencyId", type: "string"},
		 		     {name: "financeSubjId", type: "string"},
		 		     {name: "financeSubjName", type: "string"},
		 		     {name: "currencyCode", type: "string"},
		 		     {name: "exchangeRate", type: "double"},
		 		     {name: "money", type: "double"},
		 		     {name: "summary", type: "string"},
		 		     {name: "payedMoney", type: "double"},
		 		     {name: "selected", type: "boolean"},
		 		     {name: "myPayedMoney", type: "double"}
		 		],
		 root: 'loanInfoList'
	};
	
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var checkboxRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
		var currency = _frame.find("#currency").find("option:selected").text();
		var currencyCode = currency.split("-")[1];
		var html;
		if(rowdata.currencyCode != currencyCode){
			if (rowdata.selected) {
				html = "<div class='jqx-column align-center disable-color'><input class='jqx-column-checkbox' type='checkbox' curr='"+ rowdata.currencyId +"' name='"+ rowdata.loanId +"' receiptNo='"+ rowdata.receiptNo +"' rowIndex='checkbox"+ row +"' money='"+ rowdata.money+ "' payedMoney='"+ rowdata.payedMoney +"' fid='"+ rowdata.financeSubjId +"' fname='" + rowdata.financeSubjName + "' onclick='inputClickEvent(this)' checked disabled></div>";
			} else {
				html = "<div class='jqx-column align-center enable-color'></div>";
			}
		}else{
			if (rowdata.selected) {
				html = "<div class='jqx-column align-center'><input class='jqx-column-checkbox' type='checkbox' curr='"+ rowdata.currencyId +"' name='"+ rowdata.loanId +"' receiptNo='"+ rowdata.receiptNo +"' rowIndex='checkbox"+ row +"' money='"+ rowdata.money+ "' payedMoney='"+ rowdata.payedMoney +"' fid='"+ rowdata.financeSubjId +"' fname='" + rowdata.financeSubjName + "' onclick='inputClickEvent(this)' checked></div>";
			} else {
				var balanceMoney = subtract(rowdata.money, rowdata.payedMoney);
				html = "<div class='jqx-column align-center'>";
				if (balanceMoney > 0) {
					html += "<input class='jqx-column-checkbox' type='checkbox' curr='"+ rowdata.currencyId +"' name='"+ rowdata.loanId +"' receiptNo='"+ rowdata.receiptNo +"' rowIndex='checkbox"+ row +"' money='"+ rowdata.money+ "' payedMoney='"+ rowdata.payedMoney +"' fid='"+ rowdata.financeSubjId +"' fname='" + rowdata.financeSubjName + "' onclick='inputClickEvent(this)'>";
				}
				html += "</div>";
			}
		}	
		return html;
		
	};
	var columnRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
		var currency = _frame.find("#currency").find("option:selected").text();
		var currencyCode = currency.split("-")[1];
		var html;
		if(rowdata.currencyCode != currencyCode){
			html = "<div class='jqx-column align-center disable-color'>" + value + "</div>";
		}else{
			html = "<div class='jqx-column align-center enable-color'>" + value + "</div>";
		}
		return html;
	};
	var moneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
		var currency = _frame.find("#currency").find("option:selected").text();
		var currencyCode = currency.split("-")[1];

		var money = fmoney(rowdata.money, 2);
		var html;
		if(rowdata.currencyCode != currencyCode){
			html = "<div class='jqx-column align-right disable-color money-cell'>" + money + "</div>";
		}else{
			html = "<div class='jqx-column align-right enable-color money-cell'>" + money + "</div>";
			html += "<input class='loan-money-input' type='hidden' value="+ rowdata.money +">";
		}
		return html;
	};
	var paymoneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
		var currency = _frame.find("#currency").find("option:selected").text();
		var currencyCode = currency.split("-")[1];
		
		var money = fmoney(rowdata.payedMoney, 2);
		
		var html;
		if(rowdata.currencyCode != currencyCode){
			html = "<div class='jqx-column align-right disable-color payedmoney-cell'>" + money + "</div>";
		}else{
			html = "<div class='jqx-column align-right enable-color payedmoney-cell'>" 
				+ money
				+ "</div>" 
				+ "<input class='all-payed-money' type='hidden' value='"+ rowdata.payedMoney +"'>";
			
			if (rowdata.selected) {
				var myPayedMoney = rowdata.myPayedMoney;
				html += "<input class='payment-payed-money' loanId='"+ rowdata.loanId +"' type='hidden' value='"+ myPayedMoney +"'>";
			}
		}
		return html;
	};
	var balanceRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var _frame=$('#fullReceiptDiv').contents();//得到iframe页面的数据
		var currency = _frame.find("#currency").find("option:selected").text();
		var currencyCode = currency.split("-")[1];
		
		var money = fmoney(subtract(rowdata.money, rowdata.payedMoney));
		
		var html;
		if(rowdata.currencyCode != currencyCode){
			html = "<div class='jqx-column align-right disable-color banlance-cell'>" + money + "</div>";
		}else{
			html = "<div class='jqx-column align-right enable-color banlance-cell'>" + money + "</div>";
		}
		return html;
	};
	
	//表格列
	var columns = [];
	columns.push({text: "", cellsrenderer: checkboxRenderer, width: '6%', cellsAlign: 'center', align: 'center'});
	if (!singleCurrencyFlag) {
		columns.push({text: "票据编号", datafield: 'receiptNo', cellsrenderer:columnRenderer, width: '10%', cellsAlign: 'center', align: 'center'});
	} else {
		columns.push({text: "票据编号", datafield: 'receiptNo', cellsrenderer:columnRenderer, width: '16%', cellsAlign: 'center', align: 'center'});
	}
	columns.push({text: "日期", datafield: 'loanDate', cellsrenderer: columnRenderer, width: '16%', cellsAlign: 'center', align: 'center'});
	columns.push({text: "摘要", datafield: 'summary', cellsrenderer: columnRenderer, width: '18%', cellsAlign: 'left', align: 'center'});
	if (!singleCurrencyFlag) {
		columns.push({text: "币种", datafield: 'currencyCode', cellsrenderer: columnRenderer, width: '6%', cellsAlign: 'left', align: 'center'});
	}
	columns.push({text: "金额", datafield: 'money', cellsrenderer: moneyRenderer, width: '14%', cellsAlign: 'right', align: 'center'});
	columns.push({text: "报销/还款金额", datafield: 'payedMoney', cellsrenderer: paymoneyRenderer, width: '16%', cellsAlign: 'right', align: 'center'});
	columns.push({text: "余额", cellsrenderer: balanceRenderer, width: '14%', cellsAlign: 'right', align: 'center'});
	
	$("#queryLoanList").jqxGrid({
		width: "100%",
		height: "100%",
		source: dataAdapter,
		columns: columns,
		rendered: function() {
			var payedMoneyInput = $("input.payment-payed-money");
			for (var i = 0; i < payedMoneyInput.length; i++) {
				var payedMoney = $(payedMoneyInput[i]).val();
				publicPayedMoney = subtract(publicPayedMoney, payedMoney);
			}
			 
			//补领金额
			$("#balanceTips").text(fmoney(publicPayedMoney));
			
			//报销的借款单票据编号和金额
			calculateSelectedLoanInfo();
		 }
		 
	});
	
}

var publicPayedMoney;	//可以用来还借款的金额
//存放选择的借款单的id
var saveLoanIds = "";

//选择借款单的单击事件
function inputClickEvent(own){
	
    /*
     * publicPayedMoney存放报销总金额
     * payedMoney已经报销的钱
     *money总借款金额
     **/

	own = $(own);
	
	var payedMoney = own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text().replace(/,/g,"")-0;	//报销/还款金额
	var staticPayedMoney = own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").siblings("input.all-payed-money").val();	//原来已报销/还款金额
	
	if (own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").siblings("input.payment-payed-money").length > 0) {
		//该付款单已报销/还款金额
		var paymentPayedMoney = own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").siblings("input.payment-payed-money").val();
		
		staticPayedMoney = subtract(staticPayedMoney, paymentPayedMoney);
	}
	
	var money = own.attr("money").replace(/,/g,"")-0;	//总借款金额

	var flag= own.is(':checked');
	if(flag){//文本框选中时
		if (publicPayedMoney <= 0) {
			showErrorMessage("已勾选的借款单已足额抵扣付款");
			event.preventDefault();
			return ;
		}
		saveLoanIds +=own.attr("name")+",";
		
		 var nowBalance = subtract(subtract(money, publicPayedMoney), payedMoney);	//把publicPayedMoney中记录的金额全部用来还款时，借款单剩余的金额
		 
		 var myPayedMoney = 0.00;	//当前借款单报销、还款金额
		 var myBalance = 0.00;	//当前借款单余额
		 if (nowBalance > 0) {
			 myPayedMoney = add(publicPayedMoney, payedMoney);
			 myBalance = nowBalance;
			 
			 publicPayedMoney = 0;
		 } else {
			 myPayedMoney = money;
			 myBalance = 0;
			 
			 publicPayedMoney = -nowBalance;
		 }
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text(fmoney(myPayedMoney, 2));
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.banlance-cell").text(fmoney(myBalance, 2));
	 } else {
		 var myBalance = subtract(money, staticPayedMoney);
		 
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text(fmoney(staticPayedMoney, 2));
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.banlance-cell").text(fmoney(myBalance, 2));
		
		 publicPayedMoney = subtract(add(publicPayedMoney, payedMoney), staticPayedMoney);
		 
		 saveLoanIds = saveLoanIds.replace(own.attr("name")+",",'');
	 }
	
	//补领金额
	$("#balanceTips").text(fmoney(publicPayedMoney));
	
	//报销的借款单票据编号和金额
	calculateSelectedLoanInfo();
}

//计算已选择借款单信息
function calculateSelectedLoanInfo() {
	//报销的借款单票据编号和金额
	var loanOrderId = "";
	var payedMoneyCount = 0;
	$("#queryNotPayedLoanList input[type=checkbox]:checked").each(function() {
		var receiptno = $(this).attr("receiptno");
		var myPayedMoney = $(this).parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text().replace(/,/g,"")-0;
		/*var myLoanOrderId = "(" + receiptno + "，<span class='money-count-color'>" + myPayedMoney + "</span>)";*/
		payedMoneyCount = add(payedMoneyCount, myPayedMoney);
		var myLoanOrderId = receiptno;
		if (loanOrderId == "") {
		 loanOrderId = myLoanOrderId;
		} else {
		 loanOrderId = loanOrderId+ "," + myLoanOrderId;
		}
	});
	$("#loanOrderId").html("("+ loanOrderId+ "),");
	$("#payedMoneyCount").html("<span class='money-count-color'>" + fmoney(payedMoneyCount, 2) + "</span>");
}


//确定选择借款单按钮
function confirmSelectLoanOrder(){
	loanConfirmed = true;
	
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var currency = "";
	
	$("#queryNotPayedLoanList input[type=checkbox]:checked").each(function(i){
		if(i == 0){
			currency = $(this).attr("curr");
		}
	});
	
	var forLoanMoney = "";//已勾选金额
	var loanLeftMoney = "";	//借款余额
	var payLeftMoney = "";	//补领金额
	var loanIds = "";	//已勾选借款单ID
	if (saveLoanIds == null || saveLoanIds == "") {
		if ($("#queryNotPayedLoanList input[type=checkbox").length > 0) {
			popupPromptBox("提示", "当前付款未关联任何借款单，是否确定退出？", function() {
				$("#queryNotPayedLoanList").jqxWindow("close");
				loanIds = "";
				
				//计算借款余额
				$.each($(".banlance-cell.enable-color"), function(index, value) {
					var myLeftMoney =  $(value).text().replace(/,/g,"")-0;
					loanLeftMoney = add(loanLeftMoney, myLeftMoney);
				});
				
				loanLeftMoney = "借款余额：" + fmoney(loanLeftMoney);
				
				setPaymentLoanMoneyInfo(loanIds, forLoanMoney, loanLeftMoney, payLeftMoney);
			});
		} else {
			$("#queryNotPayedLoanList").jqxWindow("close");
			loanIds = "";
			
			setPaymentLoanMoneyInfo(loanIds, forLoanMoney, loanLeftMoney, payLeftMoney);
		}
	} else {
		loanIds = saveLoanIds.replace(/^,*|,*$/g,'');
		_frame.find("#currency").val(currency);
		$("#queryNotPayedLoanList").jqxWindow("close");
		
		//计算已勾选金额
		var paymentMoney = _frame.find("#readTotalMoney").val();
		forLoanMoney = "本次还款：" + fmoney(subtract(paymentMoney, publicPayedMoney));
		if (publicPayedMoney > 0) {
			payLeftMoney = "补领金额：" + fmoney(publicPayedMoney);
		}
		//计算借款余额
		$.each($(".banlance-cell.enable-color"), function(index, value) {
			var myLeftMoney =  $(value).text().replace(/,/g,"")-0;
			loanLeftMoney = add(loanLeftMoney, myLeftMoney);
		});
		
		loanLeftMoney = "借款余额：" + fmoney(loanLeftMoney);
		
		setPaymentLoanMoneyInfo(loanIds, forLoanMoney, loanLeftMoney, payLeftMoney);
	}
}

//设置付款单上的借款单金额信息
function setPaymentLoanMoneyInfo(loanIds, forLoanMoney, loanLeftMoney, payLeftMoney) {
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	_frame.find("#loanIds").val(loanIds);
	_frame.find("#forLoanMoney").text(forLoanMoney);
	_frame.find("#loanLeftMoney").text(loanLeftMoney);
	_frame.find("#payLeftMoney").text(payLeftMoney);
}


/****************************************************************还借款部分完毕******************************************************/

/****************************************************************付合同款部分开始******************************************************/
function contractWin(owns){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据

	var own= _frame.find($(owns));
	/*$("#contractId").val(own.attr("cid"));
	$("#contractId").attr("finsubjid",own.attr("finId"));*/
	/*$("#contractType").val(own.attr("ctype"));*/
//	var fName= own.next("input[type=hidden]").val();
//	var dropDownButton = _frame.find("#dropDownButton");
//	dropDownButton.jqxDropDownButton("setContent",'<div style="margin-top:4px; margin-left: 10px;">'+ own.html() +'</div>');
//	dropDownButton.jqxDropDownButton('close');
//	$(".paymethod-dropdown-box").hide();
	var title="";
	if(own.attr("ctype") == "1"){
		title="职员合同-";
	}
	if(own.attr("ctype") == "2"){
		title="演员合同-";
	}
	if(own.attr("ctype") == "3"){
		title="制作合同-";
	}
	title+= own.attr("contractNo")+"-";
	title+= own.text();
	$('#payedContractMoneyWin').jqxWindow('setTitle', title);
	$.ajax({
		url: '/contractManager/queryPayWayByContractId',
		type: 'post',
		data: {contractType: own.attr("ctype"), contractId: own.attr("cid")},
		datatype: 'json',
		success: function(response){
			if (!response.success) {
				showErrorMessage(response.message);
				return ;
			}

			var contractId = response.contractId;
			var aimPeopleName = response.aimPeopleName;
			var contractNo = response.contractNo;
			var contractType = response.contractType;
			var currencyId = response.currencyId;
			var financeSubjId = response.financeSubjId || "";
			var financeSubjName = response.financeSubjName;
			
			//支付方式
			if(response.payWay == 1){
				$("#payWay").text("按阶段支付");
				$("#payWay").attr("pay","1");
				$("span.by-month-tips").hide();
			}
			if(response.payWay == 2){
				$("#payWay").text("按月支付");
				$("#payWay").attr("pay","2");
				$("span.by-month-tips").show();
			}
			if(response.payWay == 3){
				$("#payWay").text("按日支付");
				$("#payWay").attr("pay","3");
				$("span.by-month-tips").show();
			}
			if(response.payWay == 4){
				$("#payWay").text("按日支付");
				$("#payWay").attr("pay","4");
				$("span.by-month-tips").show();
			}
			//银行信息
			if(response.bankName != ""){
				$("#bankName").text("银行名称为: "+ response.bankName+";");
			}else{
				$("#bankName").text("银行名称为空");
			}
			if(response.bankAccountName != ""){
				$("#bankAccountName").text("账户名称为: "+ response.bankAccountName+";");
			}else{
				$("#bankAccountName").text("账户名称为空");
			}
			if(response.bankAccountNumber != ""){
				$("#bankAccountNumber").text("银行账号为: "+ response.bankAccountNumber);
			}else{
				$("#bankAccountNumber").text("银行账号为空");
			}
			
			$("#contractWindowTable").empty();
			//支付信息
			var contractStagePayWayList = response.contractStagePayWayList;
			var contractMonthPayDetailList = response.contractMonthPayDetailList;
			if(contractStagePayWayList != null && response.payWay == 1){
				var html =[];
				html.push("<table class='pay-info-table' id='payInfoTable' cellspacing = 0, cellpadding = 0><thead>");
				html.push("  <tr>");
				html.push("  <td><input type='checkbox' id='checkedPayAll' onclick='checkedPayAll()'></td><td>阶段</td><td>支付条件</td><td>薪酬</td>");
				html.push("  </tr></thead>");
				html.push("<tbody>");
				for(var i= 0; i< contractStagePayWayList.length; i++){
					html.push("<tr>");
					//html.push("<td><div class='pay-image' onclick='settlementContract(this)' conname='"+ own.text() +"' cpid='"+contractStagePayWayList[i].id+"' cid='"+ own.attr("cid") +"' ctype='"+ own.attr("ctype") +"' currid='"+ own.attr("currid") +"' finId='"+ own.attr("finId") +"'></div><input type='hidden' value='"+ fName +"'</td>");
					/*
					 * cpid:单条支付信息的id
					 * cid: 合同的id
					 * ctype: 合同的类型
					 * currid: 币种id
					 * findId: 财务科目id
					 * fName : 财务科目名称*/
					
					html.push("<td><input type='checkbox' class='input-check' conname='"+ aimPeopleName +"' cpid='"+contractStagePayWayList[i].id+"' cid='"+ contractId +"' contractNo='"+ contractNo +"' ctype='"+ contractType +"' currid='"+ currencyId +"' finId='"+ financeSubjId +"' onclick='isCheckPayAll()'><input type='hidden' value='"+ financeSubjName +"'></td>");
					html.push("<td>第"+ contractStagePayWayList[i].stage +"期</td>");
					html.push("<td>"+ contractStagePayWayList[i].remark +"</td>");
					html.push("<td>"+ fmoney(contractStagePayWayList[i].money, 2) +"</td>");
					html.push("</tr>");
				}
				html.push("<tbody>");
				html.push("</table>");
				html = html.join("");
				$("#contractWindowTable").append(html);
			}
			if(contractMonthPayDetailList.length != 0 && (response.payWay == 2 || response.payWay == 3 || response.payWay == 4)){
				var html = [];
				html.push("<table class='pay-info-table' id='payInfoTable' cellspacing = 0, cellpadding = 0><thead>");
				html.push("  <tr>");
				html.push("  <td><input type='checkbox' id='checkedPayAll' onclick='checkedPayAll()'></td><td>月份</td><td>日期</td><td>薪酬</td><td>应付款日</td>");
				html.push("  </tr></thead>");
				html.push("<tbody>");
				for(var i= 0; i< contractMonthPayDetailList.length; i++){
					html.push("<tr>");
					html.push("<td><input type='checkbox' class='input-check' conname='"+ aimPeopleName +"' cpid='"+contractMonthPayDetailList[i].id+"' cid='"+ contractId +"' contractNo='"+ contractNo +"' ctype='"+ contractType +"' currid='"+ currencyId +"' finId='"+ financeSubjId +"' onclick='isCheckPayAll()'><input type='hidden' value='"+ financeSubjName +"'</td>");
					html.push("<td>"+ contractMonthPayDetailList[i].month +"</td>");
					html.push("<td>"+ contractMonthPayDetailList[i].startDate + "-" + contractMonthPayDetailList[i].endDate +"</td>");
					html.push("<td>"+ fmoney(contractMonthPayDetailList[i].money, 2) +"</td>");
					html.push("<td>"+ contractMonthPayDetailList[i].payDate +"</td>");
					html.push("</tr>");
				}
				html.push("</tbody>");
				html.push("</table>");
				html = html.join("");
				$("#contractWindowTable").append(html);
			}
			var payInfo = [];
			payInfo.push( "合同总金额: " + fmoney(response.totalMoney, 2) + " ");
			payInfo.push(" 已付金额: " + fmoney(response.payedMoney, 2) + " ");
			payInfo.push(" 未付金额: "+ fmoney(response.leftMoney, 2));
			payInfo= payInfo.join("");
			$("#payInfoSpan").text(payInfo);
		}
	});
		
	$("#payedContractMoneyWin").jqxWindow("open");
	
}


/*
 * contractType: 合同类型
 * contractId: 合同id
 * fName: 财务科目名称
 * finId: 财务科目id
 * currid: 币种id
 * cpid:单条支付信息的id
 * conname: 合同名称
 * money: 每条支付信息的金额
 * totalMoney: 支付总金额*/
//付合同款的金额按钮
function settlementContract(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var contractType = null;
	var contractId = null;
	var contractNo = null;
	var fName = null;
	var finId = null;
	var currid = null;
	var cpid = [];
	var conname = null;
	var money = 0;
	var totalMoney = 0.00;
	var _tableObj = $("#payInfoTable tbody");
	var checkboxs = _tableObj.find(":checked");
    $.each(checkboxs,function(i){
    	if(i == 0){
			contractType = $(this).attr("ctype");
			contractId = $(this).attr("cid");
			contractNo = $(this).attr("contractNo");
			fName = $(this).next("input[type=hidden]").val();
			finId = $(this).attr("finId");
			currid = $(this).attr("currid");
			cpid.push($(this).attr("cpid"));
			conname = $(this).attr("conname");
			money = $(this).parent("td").parent("tr").find("td").eq(3).text().replace(/,/g,"")-0;
		
			totalMoney= add(totalMoney, money);
			$("#contractPartIds").val(cpid.join(","));
		}else{
			cpid.push($(this).attr("cpid"));
			money = $(this).parent("td").parent("tr").find("td").eq(3).text().replace(/,/g,"")-0;
			totalMoney= add(totalMoney, money);
			$("#contractPartIds").val(cpid.join(","));
		}
    });
	
	
//	var payWay = $("#payWay").attr("pay");
	
	//判断付款单中已选的财务科目与合同的财务科目是否不一致
	var flag= false;
	
	_frame.find(".text-finance-subject").each(function(){
		if(finId && $(this).attr("fid")!= finId && $(this).val() !=""){
			flag= true;
	    }
	});
	if(flag){
		swal({
			title : "提示",
			text : "是否将付款单财务科目更改为所选合同的财务科目？",
			type : "info",
			showCancelButton : true,
			confirmButtonText : "确定",
			cancelButtonText : "取消",
			closeOnConfirm : true,
			closeOnCancel : true
		}, function(isConfirm) {
			if (isConfirm) {
				setPaymentInfoByContract(true, contractId, finId, contractNo, contractType, cpid, currid, conname, fName, totalMoney);
			} else {
				setPaymentInfoByContract(false, contractId, finId, contractNo, contractType, cpid, currid, conname, fName, totalMoney);
			}
		});
	}else{	//财务科目一致或者付款单中财务科目为空
		setPaymentInfoByContract(true, contractId, finId, contractNo, contractType, cpid, currid, conname, fName, totalMoney)
	}
	
	//调用子页面中查询还借款情况方法
	$('#fullReceiptDiv')[0].contentWindow.queryLoanMoneyInfo();
}

//根据合同信息设置付款单信息
function setPaymentInfoByContract(ifChangeSubj, contractId, finId, contractNo, contractType, cpid, currid, conname, fName, totalMoney) {
	var _frame = $('#fullReceiptDiv').contents();
	
	_frame.find("#contractId").val(contractId);
	
	/*
	 * 添加此判断是为了过滤掉虽然有财务科目ID但是该财务科目在剧组中不存在的情况（通过重新导入财务科目，覆盖原有数据操作会产生这种脏数据）
	 * 待后台确保数据一致性功能完善后，此判断可以删除
	 */
	if (fName) {
		_frame.find("#contractId").attr("finsubjid", finId);
	}
	_frame.find("#contractNo").val(contractNo);
	_frame.find("#contractType").val(contractType);
	_frame.find("#contractPartId").val(cpid);
	_frame.find("#currency").val(currid);
	_frame.find("#receivingParty").val(conname);
	$("#payedContractMoneyWin").jqxWindow("close");
	
	var trObj = _frame.find("tr.add-sub-tr");
	var countMoney= 0;
	$.each(trObj, function(){
		if (ifChangeSubj && finId) {
			$(this).find(".text-finance-subject").val(fName).attr("fid", finId).next("input[type=hidden]").val(finId);
		}
		
		$(this).find(".money-td-input").val(fmoney(totalMoney));
		$(this).find(".no-format-money").val(totalMoney);
		countMoney = add(countMoney, totalMoney);
	});
	
	_frame.find("#totalAccountMoney").text(numberToCapital(countMoney));
    _frame.find("#readTotalMoney").prev("span").text(fmoney(countMoney));
    _frame.find("#readTotalMoney").val(countMoney);
}


//全选
function checkedPayAll(){
	//全选
		var checkboxLength = 0;
		$("#payInfoTable tbody :checkbox").each(function(){
			checkboxLength ++;
		});
		if(checkboxLength == 0){
			//如果可勾选的个数为零，全选按钮不可选；
			ev.preventDefault();
		}else{
			if($("#checkedPayAll").is(":checked")){
				$("#payInfoTable .input-check").each(function(){
					$(this).prop("checked", true);
				});
			}else{
				$("#payInfoTable .input-check").each(function(){
					$(this).prop("checked", false);
				});
			}
		}
		
}


//判断是否是全选
function isCheckPayAll(){
	
	var _tableObj = $("#payInfoTable tbody");
	var checkboxs = _tableObj.find(":checkbox");
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#checkedPayAll").prop("checked",false);
	}else{
		$("#checkedPayAll").prop("checked",true);
	}
}

//初始化设置税信息接口
function initSetTaxWin() {
	$("#setTaxWin").jqxWindow({
		theme:theme,
		height: 280,
		width: 480,
		resizable: false,
		isModal: true,
		autoOpen: false,
		cancelButton: "#cancelSetTaxBtn"
	});
}

//票据种类变化的时候
function billTypeChange(own) {
	var value = $(own).val();
	if (value == 2) {
		/*
		 * 弹窗设置税对应的财务科目和税率 
		 */
		//判断是否选择了财务科目
		var _frame = $('#fullReceiptDiv').contents();
		var countMoney = 0;
		var trObj = _frame.find("tr.add-sub-tr");
		$.each(trObj, function(){
			var totalMoney = $(this).find(".no-format-money").val();
			countMoney = add(countMoney, totalMoney);
		});
		
		if (countMoney > 0) {
			$.ajax({
				url: "/financeSettingManager/checkTaxHasSetted",
				type: "post",
				dataType: "json",
				async: false,
				success: function(response) {
					if (!response.success) {
						showErrorMessage(response.message);
						return;
					}
					
					//弹窗显示当前税务设置信息
					$("#taxFinanSubjName").val(response.taxFinanSubjName);
					$("#taxFinanSubjId").val(response.taxFinanSubjId);
					$("#taxRate").val(response.taxRate);
					
					//弹窗提示设置税对应的财务科目和税率
					$("#setTaxWin").jqxWindow("open");
				}
			});
		}
	}
}

//设置缴税信息
function setTaxInfo() {
	var taxFinanSubjId = $("#taxFinanSubjId").val();
	var taxRate = $("#taxRate").val();
	if (!taxFinanSubjId) {
		showErrorMessage("请选择税务科目");
		return;
	}
	if (!taxRate) {
		showErrorMessage("请填写税率");
		return;
	}
	if (taxRate > 1) {
		showErrorMessage("税率不能大于1");
		return;
	}
	
	$.ajax({
		url: "/financeSettingManager/saveTaxInfo",
		data: {taxFinanSubjId: taxFinanSubjId, taxRate: taxRate},
		type: "post", 
		dataType: "json", 
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return;
			}
			$("#setTaxWin").jqxWindow("close");
			
			addTaxFinanSubjInfo(taxRate, taxFinanSubjId, response.taxFinanSubjName);
		}
	});
}

//向付款单中添加税务科目信息
function addTaxFinanSubjInfo(taxRate, finanSubjId, finanSubjName) {
	//向付款单中添加一个财务科目，金额为总金额*税率；
	//把付款单中其他财务科目的金额变成金额*(1-税率)
	var _frame = $('#fullReceiptDiv').contents();
	var trObj = _frame.find("tr.add-sub-tr");
	var totalMoney = 0;
	$.each(trObj, function(){
		var myMoney = $(this).find(".no-format-money").val();
		$(this).find(".no-format-money").val(myMoney * (1 - taxRate));
		$(this).find(".money-td-input").val(fmoney(myMoney * (1 - taxRate)));
		totalMoney = add(totalMoney, myMoney);
	});
	
	var sequence = trObj.length + 1;
	
	var taxfinanSubjInfo = [];
	taxfinanSubjInfo.push("<tr class='add-sub-tr'>");
	taxfinanSubjInfo.push("	<td width='20'>");
	taxfinanSubjInfo.push("		<div class='td-div'>");
	taxfinanSubjInfo.push("			<div class='addition' onclick='additionTr(this);'></div>");
	taxfinanSubjInfo.push("			<div class='subtraction' onclick='subtractionTr(this);'></div>");
	taxfinanSubjInfo.push("		</div>");
	taxfinanSubjInfo.push("	</td>");
	taxfinanSubjInfo.push("	<td>");
	taxfinanSubjInfo.push("		<div class='td-div'>");
	taxfinanSubjInfo.push("			 <input class='summary' type='text' name='zhaiyao' maxlength='200' value='增值税'>");
	taxfinanSubjInfo.push("		</div> ");
	taxfinanSubjInfo.push("	</td>");
	taxfinanSubjInfo.push("	<td>");
	taxfinanSubjInfo.push("		<div class='td-div'>");
	taxfinanSubjInfo.push("			<input type='text' class='text-finance-subject finance-subject"+ sequence +"' name='textFinanceSubj' sid='financeSubjName"+ sequence +"' id="+ sequence +" readonly='' fid='"+ finanSubjId +"' value="+ finanSubjName +">");
	taxfinanSubjInfo.push("			<input type='hidden' id='financeSubjId' value="+ finanSubjId +">");
	taxfinanSubjInfo.push("		</div>");
	taxfinanSubjInfo.push("	</td>");
	taxfinanSubjInfo.push("	<td class='money-td' align='right'>");
	taxfinanSubjInfo.push("		<input class='money-td-input' type='text' name='totalMoney' maxlength='10' value="+ fmoney(totalMoney * taxRate) +">");
	taxfinanSubjInfo.push("		<input class='no-format-money' type='hidden' value="+ (totalMoney * taxRate) +">");
	taxfinanSubjInfo.push("	</td>");
	taxfinanSubjInfo.push("</tr>");
	
	_frame.find(".payment-detail-table tbody").append(taxfinanSubjInfo.join(""));
}

//显示选择财务科目面板
function showSelectFinanSubjDiv(own, ev) {
	var obj = $(own);
	$('#levelPopup').css({left:obj.offset().left-3,top:obj.offset().top+32, "display": "block"});
	$('#levelPopup #filter').focus();
	ev.stopPropagation();
}

//加载财务科目
function loadFinanceSubject(){
	var source = {
			url: '/financeSubject/querySubjectList',
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
			id: 'id'	
		};
	 var dataAdapter = new $.jqx.dataAdapter(source);

	 $("#subjectTree").jqxTreeGrid({
		 width: 220,
		 height:200,
		 source: dataAdapter,
		 showHeader: false,
		 ready: function(){},
		    columns: [
		          { text: '财务科目', dataField: 'name', width: 200, align: "center" }
		        ]
     });
	 $('#subjectTree').on('rowSelect', function (event){
         
         var args = event.args;
         var key = args.key;
         var row = $("#subjectTree").jqxTreeGrid('getRow', key);
         
         if(row.expanded == true){
               $("#subjectTree").jqxTreeGrid('collapseRow', key);
         }else{
               $("#subjectTree").jqxTreeGrid('expandRow', key);
         }
         $("#subjectTree").jqxTreeGrid('clearSelection');
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
		          $("#taxFinanSubjName").val(name);
		          $("#taxFinanSubjId").val(row.id);
		          $('#levelPopup').css("display", "none");
		          $("#clearFinanceSubj").show();
             }
        });
	 
	 $("#filterBtn").click(function(ev){
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
		      $("#subjectTree").jqxTreeGrid('addFilter', 'name', filtergroup);
		      // apply the filters.
		      $("#subjectTree").jqxTreeGrid('applyFilters');
		      $("#subjectTree").jqxTreeGrid('expandAll');
		      ev.stopPropagation();
		});
		
		$("#filter").on("keydown", function(ev){
			if(ev.keyCode == 13) {
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
			      $("#subjectTree").jqxTreeGrid('addFilter', 'name', filtergroup);
			      // apply the filters.
			      $("#subjectTree").jqxTreeGrid('applyFilters');
			      $("#subjectTree").jqxTreeGrid('expandAll');
			}
			ev.stopPropagation();
		});
}

//清空财务科目
function clearFinanceSubj(){
     $("#taxFinanSubjName").val("");
     $("#taxFinanSubjId").val("");
     $("#clearFinanceSubj").hide();
}

//校验是否是数字
function isNum(own) {
	if (isNaN($(own).val())) {
		$(own).val("");
	}
}