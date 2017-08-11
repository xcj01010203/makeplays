


/**
 * 生成财务流水账列表
 * @param gridDiv--表格初始化对象
 * @param data--财务流水账数据源
 */
function loadNoticeViewTable(gridDiv,data){
	var _this = this;
	//表格对象；--包括表格头和表格体
	var table ="";
	
	/*this.isRowClick=true;*/
	
	//创建表格的方法
		gridDiv.empty();
		
		//gridDiv.addClass("box_wrap");
		gridDiv.append("<div id='toolbar' class='title toolbar' style='display:block;'></div>");
		
		loadSearchViewGrid(gridDiv.find("#toolbar"));
		
		var gridMainDiv = $("<div class='t_i table-main-div'></div>");
		
		var gridHead_array = ["<div class='t_i_h table-main-header-div' id='hh'><div class='ee table-main-header'><table class='finan-Running-table' cellspacing=0 cellpadding=0><thead>"];
		gridHead_array.push("<tr>");
		
		gridHead_array.push("<td style='width: 55px; min-width: 55px; text-align: center;'><input class='div-checkbox' type='checkbox' id='all' class='line-height' value=''/></td>");
		gridHead_array.push("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'>日期</div></td>");
		gridHead_array.push("<td style='width: 120px; min-width: 120px;'><div class='jqx-column column-align-center'>票据编号</div></td>");
		gridHead_array.push("<td style='width: 192px; min-width: 192px; max-width: 192px;'><div class='jqx-column column-align-left'>摘要</div></td>");
		gridHead_array.push("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'>关联合同号</div></td>");
		gridHead_array.push("<td style='width: 308px; min-width: 308px; max-width: 308px;'><div class='jqx-column column-align-left'>财务科目</div></td>");
		gridHead_array.push("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-center'>收款金额</div></td>");
		gridHead_array.push("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-center'>付款金额</div></td>");
		gridHead_array.push("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-center'>资金余额</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>状态</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>单据类型</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>收/付款方</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>付款方式</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>有无发票</div></td>");
		gridHead_array.push("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>票据张数</div></td>");
		gridHead_array.push("<td><div style='width: 110px; min-width: 110px;' class='jqx-column column-align-center'>记账</div></td>");
		gridHead_array.push("<td><div style='width: 18px;'></div></td>");
		
		gridHead_array.push("</tr>");
		gridHead_array.push("</thead></table></div></div>");
		
		var gridHeadDiv = $(gridHead_array.join(""));
		gridMainDiv.append(gridHeadDiv);
		gridDiv.append(gridMainDiv);
		
		var gridContentDiv = $("<div class='auto-height cc grid-content-div' id='ca' onscroll='viewGridScroll()'></div>");
		
		table=$("<table cellpadding='0' cellspacing='0' border='0' id='finRunningAccountGrid'></table>");
		
		table.append("<tbody id='finRunningAccountTbody'><tbody>");
		gridContentDiv.append(table);
		//没有数据时
		if(data.length == 0){
			table.find("#finRunningAccountTbody").append("<tr><td style='text-align: center; border-bottom: none; width: 2197px; min-width: 2197px;' colspan="+ gridHead_array.length +">暂无数据</td></tr>");
		}else{
			//渲染表格内容--每一行的数据
			$.each(data,function(financeRunningIndex,item){
				
				var _row = createRow(financeRunningIndex,item);
				table.find("#finRunningAccountTbody").append(_row);
			});
		}
		gridMainDiv.append(gridContentDiv);
		
	
	//全选复选框点击事件
	$("#all").on("click",function(event){
		if($("#all").prop("checked")){
			$(this).trigger("checked");//指定全选事件
		} else {
			$(this).trigger("unChecked");//指定全不选
		}
	});
	
	//checkbox指定全部不选
	$("#all").bind("unChecked",function(){
		$("#all").prop("checked",false);
		gridDiv.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all").bind("checked",function(){
		$("#all").prop("checked",true);
		gridDiv.find("tbody :checkbox").trigger("checked");
	});
	
}

/**
 * 重新加载表格
 * @param data
 * @param index
 * @param noticeId
 */
function reloadNoticeView(data){
	//showSuccessMessage("正在加载...");
	var table=$('#finRunningAccountGrid');
	
	var _this = this;
	table.html('');
	table.append("<tbody id='finRunningAccountTbody'><tbody>");
	
	//全选复选框反选
	$("#all").prop("checked",false);
	
	$.each(data,function(financeRunningIndex,item){
		var _row = createRow( financeRunningIndex, item);
		table.find("#finRunningAccountTbody").append(_row);
	});
	
	//全选复选框点击事件
	$("#all").on("click",function(event){
		if($("#all").prop("checked")){
			$(this).trigger("checked");
		} else {
			$(this).trigger("unChecked");
		}
	});
	
	//checkbox指定全部不选
	$("#all").bind("unChecked",function(){
		$("#all").prop("checked",false);
		table.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all").bind("checked",function(){
		$("#all").prop("checked",true);
		table.find("tbody :checkbox").trigger("checked");
	});
	
}


/**
 * 创建通告单中表格的一行
 * @param tableIndex	表格编号
 * @param rowIndex	行编号
 * @param rowData	行数据
 * @returns	行
 */
function createRow (tableIndex,rowData) {
	var _this = this;
	var table=$('#finRunningAccountGrid'+tableIndex);
	
	/*var statusClass=" style='background-color:"+getColor(rowData.shootStatus)+"' ";*/
	
	var _row = $("<tr id='finRunning"+tableIndex+"row'></tr>");
	
	/*//行点击事件
	_row.click(function(){
		if(_this.isRowClick){
			if($(this).attr("class") && $(this).attr("class").indexOf("mouse_click")>-1){
				$(this).find(":checkbox").trigger("unChecked");
			}else{
				$(this).find(":checkbox").trigger("checked");
			}
			
		}else{
			_this.isRowClick=true;
		}
	});*/
	
	//全选列
	if(rowData.formType == 1){
		_row.append("<td style='width: 55px; min-width: 55px; text-align:center;'><input class='div-checkbox' rid='"+ rowData.receiptId +"' type='checkbox' onchange='changeAllChecked(this)'></td>");//复选框
	}else{
		_row.append("<td style='width: 55px; min-width: 55px;'></td>");
	}
	//日期
	if(rowData.receiptDate != ""){
		_row.append("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'>" + rowData.receiptDate +"</div></td>");
	}else{
		_row.append("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'></div></td>");//日期
	}
	//票据编号
	if(rowData.formType == 3){
		_row.append("<td style='width: 120px; min-width: 120px;'><div class='jqx-column column-align-center'>" + rowData.receiptNo +"</a></div></td>");
	}else{
		_row.append("<td style='width: 120px; min-width: 120px;'><div class='jqx-column column-align-center'><a href='javascript:showModifyReceipt(\""+rowData.receiptId+"\", \""+rowData.formType+"\")'>" + rowData.receiptNo + "</a></div></td>");
	}
	//摘要
	_row.append("<td style='width: 192px; min-width: 192px; max-width: 192px;'><div class='jqx-column column-align-left'>" + rowData.summary + "</div></td>");
	//关联合同号
	if(rowData.contractNo != null){
		_row.append("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'>" + rowData.contractNo + "</div></td>");
	}else{
		_row.append("<td style='width: 106px; min-width: 106px;'><div class='jqx-column column-align-center'></div></td>");
	}
	
	_row.append("<td style='width: 308px; min-width: 308px; max-width: 308px;'><div class='jqx-column column-align-left'>" + rowData.financeSubjName + "</div></td>");//财务科目
	//收款金额
	if(rowData.collectMoney != null && rowData.collectMoney!= ""){
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'>" + fmoney(rowData.collectMoney) +"(" +rowData.currencyCode +")" + "</div></td>");
	}else{
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'></div></td>");
	}
	//付款金额
	if(rowData.payedMoney != null && rowData.payedMoney != ""){
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'>" + fmoney(rowData.payedMoney) +"(" +rowData.currencyCode +")" + "</div></td>");
	}else{
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'></div></td>");
	}
	//资金余额
	if(rowData.leftMoney != null && rowData.leftMoney != ""){
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'>" + fmoney(rowData.leftMoney) +"(" +rowData.currencyCode +")" + "</div></td>");
	}else{
		_row.append("<td style='width: 180px; min-width: 180px;'><div class='jqx-column column-align-right'></div></td>");
	}
	//状态
	if(rowData.status == 0){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center font-style'>未结算</div></td>");
	}
	else if(rowData.status == 1){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>已结算</div></td>");
	}else{
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>" + rowData.status +"</div></td>");
	}
	//单据类型
	if(rowData.formType == 1){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>付款</div></td>");
	}
	if(rowData.formType == 2){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>收款</div></td>");
	}
	if(rowData.formType == 3){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>借款</div></td>");
	}
	//收/付款方
	if(rowData.aimPersonName != null && rowData.aimPersonName !=""){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>" + rowData.aimPersonName + "</div></td>");
	}else{
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'></div></td>");
	}
	//付款方式
	if(rowData.paymentWay != null && rowData.paymentWay != ""){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>" + rowData.paymentWay + "</div></td>");
	}else{
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'></div></td>");
	}
	//有无发票
	if(rowData.hasReceipt== 1){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>有发票</div></td>");
	}
	else if(rowData.hasReceipt== 0){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>无发票</div></td>");
	}
	else{
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>"+ rowData.hasReceipt+"</div></td>");
	}
	//票据张数
	if(rowData.billCount != null && rowData.billCount != ""){
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'>" + rowData.billCount + "</div></td>");
	}else{
		_row.append("<td style='width: 110px; min-width: 110px;'><div class='jqx-column column-align-center'></div></td>");
	}
	//记账
	if(rowData.agent != null && rowData.agent != ""){
		_row.append("<td><div style='width: 110px; min-width: 110px;' class='jqx-column column-align-center'>" + rowData.agent + "</div></td>");
	}else{
		_row.append("<td><div style='width: 110px; min-width: 110px;' class='jqx-column column-align-center'></div></td>");
	}
	
	
	
	
   	
   	//行checkbox事件只判断是否全选(行checkbox值发生变化时调用该方法)
	function isCheckAll() {
		var UnChecked = $("#finRunningAccountTbody").find(":checkbox").not(function(index){
			if($(this).prop("checked")){
				return this;
			}
		}).length;
		if(UnChecked==0){
			$("#all").prop("checked",true);
		}else{
			$("#all").prop("checked",false);
		}
	}
	
	_row.find(":checkbox").bind("checked",function(){
		$(this).prop("checked",true);
		$(this).parents("tr").addClass("mouse_click");
		
		isCheckAll();
	});
	
	_row.find(":checkbox").bind("unChecked",function(){
		$(this).prop("checked",false);
		$(this).parents("tr").removeClass("mouse_click");

		isCheckAll();
	});
	
	return _row;
}


function changeAllChecked(own){
	var $this = $(own);
	if($this.is(":checked")){
		
	}else{
		if($("input#all[type=checkbox]:checked")){
			$("#all").prop("checked", false);
			$this.parents("tr").removeClass("mouse_click");
		}else{
			$("#all").prop("checked", true);
		}
	}
}	


/**
 * 生成表格的工具栏
 * @param toolbar 工具栏jquery对象
 * @param index	当前表格的下标
 * 
 */
function loadSearchViewGrid(toolbar){
	
	var container = toolbar;
    var toolbarContent = [];
    toolbarContent.push("<div class='toolbar'>");
    toolbarContent.push("<input type='button' class='advance-search-btn' id='advanceQueryBtn' onclick='showAdvanceQueryWin()' title='高级查询'>");
	if(!isRunningAccountReadonly){
		toolbarContent.push("<input type='button' class='account-subject-btn' id='accountSubjectBtn' onclick='AccountSubjectPage()' title='会计科目'>");
	}
	
	toolbarContent.push("<input type='button' class='export-btn' id='exportBtn' onclick='exportList()' title='导出'>");
	if(!isRunningAccountReadonly){
		toolbarContent.push("<input type='button' class='import-btn' id='importBtn' onclick='showImportWin()' title='导入'>");
		toolbarContent.push("<input type='button' class='settlement-btn' id='settlementBtn' onclick='settlePaymentBatch()' title='结算'>");
		toolbarContent.push("<input type='button' class='ticket-change-btn' id='ticketChangeBtn' onclick='changeTicket()' title='无票改有票'>");
		toolbarContent.push("<input type='button' class='print-btn' id='printBtn' onclick='printPayments()' title='批量打印'>");
	}
	
	toolbarContent.push("<select class='select-aimdate-btn' id='selectAimdateBtn' onchange='selectAimdate(this)'></select>");
	
	
	toolbarContent.push("<input class='include-loan-btn' type='button' id='includeLoanBtn' value='含借款' onclick='isIncludeLoanBtn(this)'>");
	toolbarContent.push("</div>");
	container.append(toolbarContent.join(""));
	//获取票据日期列表
	getaimDataList();
	
	//是否包含借款
	if(subData.includeLoan == true){
		$("#includeLoanBtn").addClass("on");
	}else{
		$("#includeLoanBtn").removeClass("on");
	}
}



//重置文本框的高度

function viewGridScroll() {
	var b = document.getElementById("ca").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}

function tableScroll() {
	var b = document.getElementById("theadDiv").scrollLeft;
	document.getElementById("tbodyDiv").scrollLeft = b;
}



