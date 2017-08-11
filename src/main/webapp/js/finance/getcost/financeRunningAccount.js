var singleCurrencyFlag = false;
$(document).ready(function(){
	//校验是否需要财务密码
	checkNeedFinancePwd();
	//初始化上传插件
	initUploader();
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
	
	//获取总的货币信息
	getCurrencyList();
	//初始化高级查询窗口
	initAdvanceQueryWin();
	//初始化修改单据窗口
	initModifyReceipts();
	//初始化财务科目列表
	loadFinanceSubjList();
	//初始化下拉按钮
	initDropDownBtn();
	//清空下拉列表中选中的内容
	showClearBtn();
	
	//加载带有会计科目的付款单列表
//	loadAccountPaymentList();
	//加载财务流水账列表
	loadFinanceRunning();
	//显示票据日期下拉列表-带有会计科目信息的查询窗口
//	showPaymentDateList();
	//显示会计科目列表
	showAccountFinanceList();
	//获取票据日期列表
	getaimDataList();
	//初始化导入窗口
	initImportWin();
	
	
	//初始化下拉列表
	$('.selectpicker').selectpicker({
        size: 7
    });
	
	$(document).on("click", function(){
		
		$("#levelPopup").slideUp("fast");
	});
	
	window.onresize = function(){
		resizeViewGird();
	};
	
	//初始化选择借款单窗口
	initSelectLoanOrder();
	//初始化合同联系人弹窗
	initContractPopupWin();
	//初始化合同详细信息弹窗
	initContractDetailWin();
	getcurrencyId();
	
	$("#modifyReceiptWin").on('close', function (event){ 
		if (payUploader.getFiles('inited').length != 0){
			var files = payUploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				payUploader.removeFile(files[i].id, true);
			}
			$("#showAttachmentFile").empty();
		
	    }
	});
	
});
var currencyIdCodeMap = {};
//获取币种列表
function getcurrencyId(){
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		type: 'post',
		data: {ifEnable: true, ifStandard: ''},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var currencyInfoList = response.currencyInfoList;
				var html = [];
				if(currencyInfoList.length != 0){
					for(var i =0,le = currencyInfoList.length;i<le;i++){
						currencyIdCodeMap[currencyInfoList[i].id] = currencyInfoList[i].code;
						
					}
				}
			}
		}
	});
}
//初始化选择借款单窗口
function initSelectLoanOrder(){
	$("#queryNotPayedLoanList").jqxWindow({
		theme:theme,
		height: 500,
		width: 1000,
//		minHeight: 50,
//		minWidth: 100,
		maxWidth: 1500,
		maxHeight: 600,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//显示选择借款单窗口
function showSelectLoanOrder(){
	payedLoanFinanceData();
}
//确定选择借款单按钮
function reimbursementLoanOrder(){
	confirmSelectLoanOrder();
}
//取消选择借款单按钮
function cancelSelectLoanOrder(){
	$("#queryNotPayedLoanList").jqxWindow("close");
}

//初始化合同联系人弹窗
function initContractPopupWin(){
	$("#payedContractMoneyWin").jqxWindow({
		theme: theme,
		height: 520,
		width: 710,
//		minHeight: 50,
//		minWidth: 100,
		maxWidth: 800,
		maxHeight: 600,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){}
	});
}

//初始化合同详细信息弹窗
function initContractDetailWin() {
	$("#contractDetailWin").jqxWindow({
		theme: theme,
		height: 600,
		width: 1200,
		maxWidth: 10000,
		maxHeight: 1000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){}
	});
}


//查询财务流水账需要的参数值
var subData = {};
subData.financeSubjIds= "";
subData.aimPeopleNames= "";
subData.aimDates= "";
subData.aimMonth= "";
subData.agents= "";
subData.formType= "";
subData.hasReceipt= "";
subData.status= "";
subData.summary= "";
subData.minMoney= "";
subData.maxMoney= "";
subData.includeLoan= "";
subData.department = "";

//查询带有会计科目的付款单需要的参数值
var accountSubData = {};

//新表格对象
var grid;
//是否刷新统计
var summaryFlag=true;

var source;
var gridColumns=new Array();
var filter={};
//工具条的防止重新渲染
var toolbarIndex = 0;

function loadFinanceRunning(){
//	source = {
//			url: '/getCostManager/queryFinanceRunningAccount',
//			type: "post",
//			dataType : "json",
//			data: filter,
//			datafields : [
//			     {name: "receiptId", type: "string"},
//			     {name: "receiptDate", type: "string"},
//			     {name: "receiptNo", type: "int"},
//			     {name: "summary", type: "string"},
//			     {name: "financeSubjName", type: "string"},
//			     {name: "contractNo", type: "string"},
//			     {name: "contractName", type: "string"},
//			     {name: "collectMoney", type: "double"},
//			     {name: "payedMoney", type: "double"},
//			     {name: "leftMoney", type: "double"},
//			     {name: "status", type: "string"},
//			     {name: "formType", type: "int"},
//			     {name: "aimPersonName", type: "string"},
//			     {name: "paymentWay", type: "string"},
//			     {name: "hasReceipt", type: "string"},
//			     {name: "billCount", type: "string"},
//			     {name: "agent", type: "string"},
//			     {name: "currencyCode", type: "string"}
//			],
//			root: 'runningAccountList'	
//	};
	//票据日期
	receiptDataRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-center'>" + rowdata.receiptDate + "</div>");
		return html;
	};
	//票据编号
	receiptNoRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		if(rowdata.formType == 3){
			html.push("<div class='jqx-column column-align-center'>" + rowdata.receiptNo +"</a></div>");
		}else{
			html.push("<div class='jqx-column column-align-center'><a href='javascript:void(0)' onclick='showModifyReceipt(\""+rowdata.formType+"\",\""+rowdata.receiptId+"\",this)'>" + rowdata.receiptNo + "</a></div>");
		}
		
		html.join("");
		return html;
	};
	//摘要
   summaryRenderer = function(columnfield, value, columnproperties, rowdata){
	   var summary = rowdata.summary;
	   if (summary == null) {
		   summary = "";
	   }
	   
		var html = [];
		html.push("<div class='jqx-column column-align-left' title='"+ summary +"'>" + summary + "</div>");
		html.join("");
		return html;
	};
	//关联合同号
	contractNoRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		if(rowdata.contractNo && rowdata.contractNo != null){
			html.push("<div class='jqx-column column-align-left contract-column' onclick='showContractDetail(\""+ rowdata.contractType +"\", \""+ rowdata.contractId +"\")'>" + rowdata.contractNo + "</div>");
		}else{
			html.push("<div class='jqx-column column-align-left'></div>");
		}
		
		html.join("");
		return html;
	};
	//财务科目
	financeSubjNameRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		if(rowdata.financeSubjName != null){
			html.push("<div class='jqx-column column-align-left' title='"+ rowdata.financeSubjName +"'>" + rowdata.financeSubjName + "</div>");
		}else{
			html.push("<div class='jqx-column column-align-left'></div>");
		}
		
		html.join("");
		return html;
	};
	
	//收款金额
	collectMoneyRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-right'>" + rowdata.collectMoneyStr + "</div>");
		html.join("");
		return html;
	};
	
	//付款金额
	payedMoneyRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-right'>" + rowdata.payedMoneyStr + "</div>");
		html.join("");
		return html;
	};
	
	//资金余额
	leftMoneyRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-right'>" + rowdata.leftMoneyStr + "</div>");
		html.join("");
		return html;
	};
	 
	//状态
	statusRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		if(rowdata.status == 0){
			html.push("<div class='jqx-column column-align-center font-style'>未结算</div>");
		}
		if(rowdata.status == 1){
			html.push("<div class='jqx-column column-align-center'>已结算</div>");
		}
		html.join("");
		return html;
	};
	
	//单据类型
	formTypeRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-center'>" + rowdata.formTypeStr + "</div>");
		html.join("");
		return html;
	};
	
	//部门
	departmentNameRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		var department = ''
		if (rowdata.department != null && rowdata.department != undefined) {
			department = rowdata.department;
		}
		html.push("<div class='jqx-column column-align-center'>" + department + "</div>");
		html.join("");
		return html;
	};
	//收付款方
	aimPersonNameRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-center'>" + rowdata.aimPersonName + "</div>");
		html.join("");
		return html;
	};
	//付款方式
	paymentWayRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-center'>" + rowdata.paymentWay + "</div>");
		html.join("");
		return html;
	};
	
	
	//有无发票
	hasReceiptRenderer = function(columnfield, value, columnproperties, rowdata){
		if(rowdata.hasReceipt== 1){
			var html = [];
			html.push("<div class='jqx-column column-align-center'>有发票</div>");
			html.join("");
			return html;
		}
		else if(rowdata.hasReceipt== 0){
			var html = [];
			html.push("<div class='jqx-column column-align-center'>无发票</div>");
			html.join("");
			return html;
		}
		else{
			var html = [];
			html.push("<div class='jqx-column column-align-center'>"+ rowdata.hasReceipt+"</div>");
			html.join("");
			return html;
		}
	};
	
	//票据张数
	billCountRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		if(rowdata.billCount != null){
			html.push("<div class='jqx-column column-align-center'>"+ rowdata.billCount+"</div>");
		}else{
			html.push("<div class='jqx-column column-align-center'></div>");
		}
		html.join("");
		return html;
	};
	//记账
	agentRenderer = function(columnfield, value, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-center'>"+ rowdata.agent +"</div>");
		html.join("");
		return html;
	};
	
	//设置每行数据的格式
    gridColumns=[{ width: '40px',isCheckbox:true }];
    gridColumns.push(
    		{text: "日期", isSort:true,  title:'', datafield:'receiptDate',cellsrenderer: receiptDataRenderer,  width: '108px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "票据编号", isSort:true, title:'', datafield:'receiptNo',cellsrenderer: receiptNoRenderer, width: '108px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "摘要", datafield:'summary',cellsrenderer: summaryRenderer, width: '306px', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "关联合同号", datafield:'contractNo',cellsrenderer: contractNoRenderer, width: '78px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "财务科目", datafield:'financeSubjName',cellsrenderer: financeSubjNameRenderer, width: '320px', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "收款金额", datafield: 'collectMoneyStr', cellsrenderer: collectMoneyRenderer, style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center', sortable: false},
			{text: "付款金额", datafield: 'payedMoneyStr', cellsrenderer: payedMoneyRenderer, style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center', sortable: false},
			//{text: "资金余额", datafield: 'leftMoney', cellsrenderer: leftMoneyRenderer, style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center', sortable: false},
			{text: "状态", datafield: 'status', cellsrenderer:statusRenderer, width: '70px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "单据类型", datafield: 'formType', cellsrenderer: formTypeRenderer, width: '140px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "部门", datafield:'aimPersonName',cellsrenderer: departmentNameRenderer, width: '170px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "收/付款方", datafield:'aimPersonName',cellsrenderer: aimPersonNameRenderer, width: '170px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "付款方式", datafield:'paymentWay',cellsrenderer: paymentWayRenderer, width: '160px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "有无发票", datafield: 'hasReceipt', cellsrenderer: hasReceiptRenderer, width: '70px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "票据张数", datafield:'billCount',cellsrenderer: billCountRenderer, width: '70px', cellsAlign: 'center', align: 'center', sortable: false},
			{text: "记账", datafield:'agent',cellsrenderer: agentRenderer, width: '120px', cellsAlign: 'center', align: 'center', sortable: false}		
    );
    rendertoolbar = function (toolbar){
    	if(toolbarIndex == 0){
    		var container = [];
    		container.push("<div class='toolbar'>");
    		container.push("<input type='button' class='advance-search-btn' id='advanceQueryBtn' onclick='showAdvanceQueryWin()'>");
//    		if(!isRunningAccountReadonly){
    			container.push("<input type='button' class='account-subject-btn' id='accountSubjectBtn' onclick='AccountSubjectPage()'>");
//    		}
    		if(hasExportFinanceDetailAuth) {
        		container.push("<input type='button' class='export-btn' id='exportBtn' onclick='exportList()'>");
    		}
    		if(!isRunningAccountReadonly){
    			container.push("<input type='button' class='settlement-btn' id='settlementBtn' onclick='settlePaymentBatch()'>");
    			container.push("<input type='button' class='ticket-change-btn' id='ticketChangeBtn' onclick='changeTicket()'>");
    			container.push("<input type='button' class='print-btn' id='printBtn' onclick='printPayments()'>");
    		}
    		if(!isRunningAccountReadonly && hasImportFinanceDetailAuth){
    			container.push("<input type='button' class='import-btn' id='importBtn' onclick='showImportWin()'>");
    		}
    		
    		container.push("<select class='select-aimdate-btn' id='selectAimdateBtn' onchange='selectAimdate(this)'></select>");
    		
    		container.push("<input class='include-loan-btn' type='button' id='includeLoanBtn' value='含借款' onclick='isIncludeLoanBtn(this)'>");
    		container.push("</div>");
    		toolbar.append($(container.join("")));
    		
    		$("#advanceQueryBtn").jqxTooltip({content: "高级查询", position: "bottom"});
			$("#accountSubjectBtn").jqxTooltip({content: "会计科目", position: "bottom"});
    		if(!isRunningAccountReadonly){
    			$("#settlementBtn").jqxTooltip({content: "结算", position: "bottom"});
    			$("#ticketChangeBtn").jqxTooltip({content: "无票改有票", position: "bottom"});
    			$("#printBtn").jqxTooltip({content: "批量打印付款单", position: "bottom"});
    		}
    		if(!isRunningAccountReadonly && hasImportFinanceDetailAuth){
    			$("#importBtn").jqxTooltip({content: "导入", position: "bottom"});
    		}
    		if(hasExportFinanceDetailAuth) {
    			$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
    		}
    		toolbarIndex++;
    	}else{
    		return;
    	}
    	
    };
    grid = new SimpleGrid("runningAccountList","/getCostManager/queryFinanceRunningAccount",100,1,gridColumns,filter,"resultList",rendertoolbar);
    grid.loadTable();
}







var requestFlag = true;
var hasDataFlag = true;
//生成财务流水账表格
function SimpleGrid(tableId,url,pageSize,pageNo,columns,filter,root,rendertoolbar){
	var $this = this;
	var _LoadingHtml = "";
	$this.recordsLength = 0;
	//结果记录
	this.tableId=tableId;
	this.source= {columns:columns};
	this.url=url;
	this.page={pageSize:pageSize,pageNo:pageNo};
	this.columns=columns;
	this.filter=filter;
	this.root=root;
	this.rendertoolbar=rendertoolbar;
	
	//是否加载统计
	/*this.summaryFlag=true;*/
	
	this.isRowClick=true;
	//设置筛选条件
	this.setFilter=function(filter){
		this.filter=filter;
		$this.loadTable();
	};
	
	//当第一次操作的请求还没有结束时, 第二次操作的请求发起了, 那么问题就来了. 全局标识是否应该继续发送请求
	this.continueAjaxRecords = false;
	
	//异步加载反馈 //需要一次握手之后才能继续
	this.handshake = true;
	
	//动态加载表格数据
//	this.getAjaxRecords = function(callback, filter){
//		
//		filter.pageNo = filter.pageNo + 1;
//		
//		$.ajax({
//			url:$this.url,
//			type:"post",
//			dataType:"json",
//			data: filter,
//			success:function(data){
//				
//				$this.source.data = $this.source.data.concat(data.runningAccountList);
//				
//				$this.loadGridContent(data.runningAccountList);
//				
//				//显示第200条
//				if(data.pageNo == 2){
//					$("#tablebody tbody:last").removeClass("hidden-tbody");
//				}
//				
//				//判断是否还存在异步数据, 没有的话就开启全选按钮
//				if($this.continueAjaxRecords && data.pageNo != data.pageCount){
//					callback(callback, filter);
//				}else{
//					$("#checkedAll").prop("disabled",false);
//					$this.handshake = true;
//					//滚动条滚动到底部
//					scrollToBottom();
//				}
//			}
//		});
//	};
	
	//获取记录数据
	this.getRecords = function(){
		this.filter.sortType = parseInt($("#sortOfType").val());
		this.filter.isAsc = true;
		this.filter.pageSize = $this.page.pageSize;
		this.filter.pageNo = $this.page.pageNo;
		if(hasDataFlag){
			$.ajax({
				url:this.url,
				type:"post",
				dataType:"json",
				data:this.filter,
				success:function(data){
					
					if(!root){
						throw Exception("root不可为空");
					}
					
//					var total= data.total;
//					var pageCount = data.pageCount;
					var records = data.runningAccountList;
					if(records == null || records.length == 0){
						hasDataFlag = false;
						records = [];
						$this.loadGridContent(records);
						return;
					}else{
						if($this.page.pageNo == 1){
							$this.source.data = records;
						}else{
							$this.source.data = $this.source.data.concat(data.runningAccountList);
						}
						$this.recordsLength = records.length;
						//加载表格每行及其内容
						$this.loadGridContent(records);
						
						//绑定事件
						$this.initTableEvent();
						
						//显示第一个100条数据
//						$("#tablebody tbody").removeClass("hidden-tbody");
						
						if($this.page.pageCount > 1){
							$this.continueAjaxRecords = true;
							$this.handshake = false;
							
							//在所有的数据没有加载完之前, 全选是禁用的
							$("#checkedAll").prop("disabled",true);
							$this.getAjaxRecords($this.getAjaxRecords, $this.filter);
						}
					}
					
					
//					//为表格中page属性赋值
//					$this.page.total = total;
//					
//					//设置分页数据
//					if(($this.page.pageNo*$this.page.pageSize+parseInt($this.page.pageSize))>total){
//						
//						if($this.page.pageNo*$this.page.pageSize==0){
//							$this.page.start=1;
//						}else{
//							$this.page.start=$this.page.pageNo*$this.page.pageSize;
//						}
//						$this.page.end=total;
//						
//					}else if($this.page.pageNo==0){
//						$this.page.start=1;
//						$this.page.end=$this.page.pageSize;
//					}else{
//						$this.page.start=$this.page.pageNo*$this.page.pageSize;
//						$this.page.end=$this.page.pageNo*$this.page.pageSize+parseInt($this.page.pageSize);
//					}
//					
//					$this.page.pageCount = pageCount;
//					$this.source.data = records;
					
					
				}
			});
		}
		
	};
	
	//加载表格
	this.loadTable = function(){
		//重新赋值分页数据
		$this.page.pageNo = pageNo;
		$this.page.pageSize = pageSize;
		hasDataFlag = true;
		//此处创建的是表格的框架，还不带有任何数据
		this.createTable();
		
		
		/**********************************显示加载中***********************************************************/
		/*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
	    var _PageHeight = document.documentElement.clientHeight;
	    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	    //在页面未加载完毕之前显示的loading Html自定义内容
	     _LoadingHtml = $("#loadingTable");
	    
	    //呈现loading效果
	    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	    _LoadingHtml.show();
	    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
		
		
		
		//此处获取数据的方式为异步，获取数据后，调用填充数据方法把表格数据以及分页信息更新到页面上
		this.getRecords();
	    
		//重置文本框的高度
		resizeViewGird();
		//重置选择统计
		$("#_already_selected").html($("#tablebody :checked").length);
		
	};
	
	//兼容行index 但是要保证顺序调用
	var _tbodyIndex = 0;
	
	//创建表格html
	this.createTable = function (){
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tbodyIndex = 0;
		
		_tableObj.append('<div class="t_i_h table-header-con" id="hh"><div class="ee table-header-div"><table cellpadding="0" cellspacing="0" border="0">'+
				'<thead><tr id="tableHead"></tr></thead></table></div></div>');
		
		var _head = _tableObj.find("#tableHead");
		
		//所有列
		var columns = this.source.columns;
		
		for(var i=0; i<columns.length; i++){
			if(columns[i].isCheckbox){
				_head.append('<td width="20px" class="bold"><input class="div-blank" type="checkbox" id="checkedAll" class="line-height"/></td>');
			
			}else{
				
				if(columns[i].style){
					if(columns[i].isSort){//是否有排序按钮
					
						if((parseInt($("#sortOfType").val()) == 0) && (i == 1)){//是否是排序依据
								_head.append('<td style="'+columns[i].style+' width:'+ columns[i].width +'; min-width:'+ columns[i].width +';"><p><span>'+columns[i].text+'</span><input type="button" class="sort select-sort" title="'+ columns[i].title +'" onclick="sortFom(this,0)"></p></td>');
						}else if((parseInt($("#sortOfType").val()) == 1) && (i == 2)){
							_head.append('<td style="'+columns[i].style+' width:'+ columns[i].width +'; min-width:'+ columns[i].width +';"><p><span>'+columns[i].text+'</span><input type="button" class="sort select-sort" title="'+ columns[i].title +'" onclick="sortFom(this,1)"></p></td>');
						}else{
							if(parseInt($("#sortOfType").val()) == 0){
								_head.append('<td style="'+columns[i].style+' width:'+ columns[i].width +'; min-width:'+ columns[i].width +';"><p><span>'+columns[i].text+'</span><input type="button" class="sort" title="'+ columns[i].title +'" onclick="sortFom(this,1)"></p></td>');
							}else{
								_head.append('<td style="'+columns[i].style+' width:'+ columns[i].width +'; min-width:'+ columns[i].width +';"><p><span>'+columns[i].text+'</span><input type="button" class="sort" title="'+ columns[i].title +'" onclick="sortFom(this,0)"></p></td>');
							}
							
						}
						
					}else{
						_head.append('<td style="'+columns[i].style+' width:'+ columns[i].width +'; min-width:'+ columns[i].width +';"><p>'+columns[i].text+'</p></td>');
					}
					
				}else{
					if(columns[i].isSort){
						if((parseInt($("#sortOfType").val()) == 0) && (i == 1)){//是否是排序依据
							_head.append('<td style="width:'+ columns[i].width +'; min-width:'+ columns[i].width+';"><p><span>'+columns[i].text+'</span><input type="button" class="sort select-sort" title="'+ columns[i].title +'" onclick="sortFom(this,0)"></p></td>');
						}else if((parseInt($("#sortOfType").val()) == 1) && (i == 2)){
							    _head.append('<td style="width:'+ columns[i].width +'; min-width:'+ columns[i].width+';"><p><span>'+columns[i].text+'</span><input type="button" class="sort select-sort" title="'+ columns[i].title +'" onclick="sortFom(this,1)"></p></td>');
						}else{
							if(parseInt($("#sortOfType").val()) == 0){
								_head.append('<td style="width:'+ columns[i].width +'; min-width:'+ columns[i].width+';"><p><span>'+columns[i].text+'</span><input type="button" class="sort" title="'+ columns[i].title +'" onclick="sortFom(this,1)"></p></td>');
							}else{
								_head.append('<td style="width:'+ columns[i].width +'; min-width:'+ columns[i].width+';"><p><span>'+columns[i].text+'</span><input type="button" class="sort" title="'+ columns[i].title +'" onclick="sortFom(this,0)"></p></td>');
							}
							
						}
						
					}else{
						_head.append('<td style="width:'+ columns[i].width +'; min-width:'+ columns[i].width+';"><p>'+columns[i].text+'</p></td>');
					}
					
				}
			}
		}
		
		//滚动条预留列
		_head.append('<td><p style="width:20px;"></p></td>');	
		
		//表格主体部分
		_tableObj.append('<div class="cc table-body-con" id="cc" onscroll="aa()"><div id="_table_doc"><table id="tablebody" cellpadding="0" cellspacing="0" border="0"></table></div></div>');
		
		if(this.rendertoolbar){
			this.rendertoolbar($("#rendertoolbar"));
		}
		
		//加载统计
		if(this.summaryFlag){
			this.loadSummary();
		}
		
		this.summaryFlag = true;
	};
	
	//加载表格主体部分
	this.loadGridContent = function(tableData) {
		
		
		var _this = this;
		var _tableObj = $("#tablebody");
		
//		row_array = ['<tbody class="hidden-tbody" >'];
		var row_array = [];
		if((tableData.length) == 0 && ($this.page.pageNo == 1)){
			row_array.push("<tr><td colspan='16' style='min-width: 2450px; text-align:center; border: none;'>暂无数据</td></tr>");
		}else{
			for(var i=tableData.length-1; i>=0;i--){
				
				row_array.push(this.createRow(null, tableData[i], (_tbodyIndex*100) + i));
			}
			
//			row_array.push('</tbody>');
		}
		
		
		/***********************************取消显示加载中*******************************************************************/
		_LoadingHtml.hide();
        $(".opacityAll").hide();
        
		
		_tableObj.prepend(row_array.join(''));
		
		requestFlag = true;
		//第一次加载滚动条到达底部
		if($this.page.pageNo == 1){
			scrollToBottom();
		}else{
            //每次加载成功后重新设置滚动条距离顶部的距离
			if($this.recordsLength != 0){
				var scrollHeight = multiply($this.recordsLength, 33);
				$(".cc").scrollTop(scrollHeight);
			}
		}
		
		_tbodyIndex++;
		
		var tbody = _tableObj.find("tbody:last");
		
		//加入选中Id
		tbody.on('click', ':checkbox', function(e){
			
			
			
			//判断是否全选
			isCheckAll();
			e.stopPropagation();
			
		});
		
		
		//一个body的高度
//		var bodyheight = 0;
//		$.each(_tableObj.find("tbody"), function(index, item){
//			var height = $(item).height();
//			bodyheight += height;
//		});
//		console.log(bodyheight);
//		$(".cc").scrollTop(bodyheight);
		
		/*单击行选中复选框*/
		/* .on('click', '>tr', function(){
			
			if(_this.isRowClick){
				//单击行选中复选框
				$(this).find(":checkbox").trigger("click");
			}else{
				_this.isRowClick=true;
			}
		});*/
	};
	
	//全不选
	this.unCheckAll=function(){
		$("#checkedAll").prop("checked", false);
		$("#"+this.tableId).find("tbody :checkbox").prop("checked",false);
		$("#_already_selected").html($("#tablebody :checked").length);
	};
	this.unSelectAll = function() {
		$("#"+this.tableId).find("tbody tr").removeClass("selected");
	};
	
	this.initTableEvent = function(){
		
		var _tableObj = $("#"+this.tableId);
		
		var pageCount = this.page.pageCount;
		var total = this.page.total;
		
		//checkbox全选
		$("#checkedAll").click(function(){
			
			if(this.checked){
				_tableObj.find("tbody :checkbox").prop("checked",true);
			}else{
				_tableObj.find("tbody :checkbox").prop("checked",false);
			}
			
			$("#_already_selected").html($("#tablebody :checked").length);
		});
		
		//一个body的高度
		var bodyheight = _tableObj.find("tbody:eq(0)").height();
		
		//设置文档的高度
		$("#_table_doc").css("height", bodyheight/100 * total);
		
		//拉动滚动条产生的重复事件
		var timeoutnum = 0;
		
		//记录那个body在窗口显示
		var preId = 0;
		
		$("#cc").scroll(function(evt){
			
			var target = $(evt.currentTarget);
			var scrollTop = target.scrollTop();
			if(scrollTop == 0){
				if(requestFlag){
					$('#checkedAll').attr("checked",false);
					requestFlag = false;
					$this.page.pageNo = $this.page.pageNo + 1;
					$this.getRecords();
				}
				
			}
			
			
//			
//			//清除重复的事件
//			window.clearTimeout(timeoutnum);
//			
//			timeoutnum = setTimeout(function(){
//			
//				var target = $(evt.currentTarget);
//				
//				var scollTop = target.scrollTop();
//				
//				var id = (scollTop - scollTop % bodyheight)/bodyheight;
//				
//				//如果相同就不更新数据
//				if(preId != id){
//					preId = id;
//					
//					//解决滚动条长距离拉动的问题
//					_tableObj.find("tbody").addClass("hidden-tbody");
//					
//					//如果在异步加载之前滚动, 判断异步加载是否完成
//					if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
//						
//						(function pollingBody(){
//							
//							setTimeout(function(){
//								
//								if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
//									
//									pollingBody();
//								}else{
//									
//									_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
//									_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
//									_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
//									
//									$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
//								}
//								
//							},1000);
//						}).call(this);
//						
//					}else{
//						
//						_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
//						_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
//						_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
//						
//						$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
//					}
//				}
//				
//			},0);
			
		});
	};
	
	//分页信息,暂时无用
	this.loadPage = function() {
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find(".pageturn").remove();
		
		//分页部分
		var pageHTML = '<div class="pageturn"><ul class="page">'
		+ '<li>总场数：'+this.page.total+'</li>'
		+ '<li>每页显示场数：<div style="float: right;margin-top: 4px;" id="pageSize"></div></li>'
		+ '<li>当前场数：'+this.page.start+'-'+this.page.end+'</li>'
		+ '<li>总页数：'+ this.page.pageCount +'</li>'
		+ '<li>当前页数：<input class="search_text" type="text" onkeyup="lmaNumber(this, ' + this.page.pageCount + ');" id="pagenum" value="'+(parseInt(this.page.pagenum)+1)+'" /><input id="oldPageNum" type="hidden"  value="'+(parseInt(this.page.pagenum)+1)+'"></li>'
		+ '<li><input class="previous_button"  title="上一场" type="button" id="previous_button"  />&nbsp;<input class="next_button" title="下一场" type="button" id="next_button" /></li>';
		+ '</ul></div>';
		
		_tableObj.append(pageHTML);
		var source = [
	                    {text:"100",value:100},
	                    {text:"500",value:500},
	                    {text:"全部",value:99999999}];
		
		$("#pageSize").jqxDropDownList({theme:theme, enableBrowserBoundsDetection: true, source: source,autoDropDownHeight: true, displayMember: "text", valueMember: "value", width: '50', height: '18'});
		
		var selectPageSizeIndex=0;
		if(this.page.pagesize==100){
			selectPageSizeIndex=0;
		}else if(this.page.pagesize==500){
			selectPageSizeIndex=1;
		}else if(this.page.pagesize==99999999){
			selectPageSizeIndex=2;
		}
		
		$("#pageSize").jqxDropDownList("selectIndex",selectPageSizeIndex);
		//页长改变事件绑定
		$("#pageSize").bind("change",{grid:this,pageNo:0, pageCount: this.page.pageCount},pageChanged);
		//上一页
		$("#previous_button").bind("click",{grid:this,pageNo:this.page.pagenum-1, pageCount: this.page.pageCount},pageChanged);
		//下一页
		$("#next_button").bind("click",{grid:this,pageNo:this.page.pagenum+1,  pageCount: this.page.pageCount},pageChanged);
		//指定页
		$("#pagenum").bind("change",{grid:this,pageNo:null,  pageCount: this.page.pageCount},pageChanged);
	};
	
	//统计信息
	this.loadSummary = function(){
		var _this=this;
		$.ajax({
			url:"/viewManager/loadSummary",
			data:_this.filter,
			dataType:"json",
			type:"post",
			async: true,
				success:function(data){
					
					var viewStatistics = data.viewStatistics;
					//总场数
					var statisticsViewCount = viewStatistics.statisticsViewCount;
					//总页数
					var statisticsPageCount = viewStatistics.statisticsPageCount;
					//状态分类统计
					var statisticsShootStatus = viewStatistics.statisticsShootStatus;
					//内外景统计
					var statisticsSite = viewStatistics.statisticsSite;
					//场景总数
					var statisticsHTML = "统计：共"+statisticsViewCount[0].funResult+"场";
					
					statisticsHTML+="/"+statisticsPageCount[0].funResult.toFixed(2)+"页";
					
					//状态统计
					var shootStatusKeys = shootStatusMap.keys();
					for(var i=0;i<shootStatusKeys.length;i++){
						var shootStatusKey = shootStatusKeys[i];
						for(var j = 0;j<statisticsShootStatus.length;j++){
							if(shootStatusKey==statisticsShootStatus[j].shootStatus){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+statisticsShootStatus[j].funResult+"场 ";
								break;
							}else if(j==statisticsShootStatus.length-1){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+"0场 ";
							}
						}
					}
					
					//内外景统计
					var siteKeys = siteMap.keys();// |  &nbsp;气氛2场 &nbsp;
					for(var i=0;i<siteKeys.length;i++){
						var siteKey = siteKeys[i];
						for(var j = 0;j<statisticsSite.length;j++){
							if(siteKey==statisticsSite[j].site){
								statisticsHTML+="|  "+siteMap.get(siteKey)+statisticsSite[j].funResult+"场 ";
								break;
							}else if(j==statisticsSite.length-1){
								statisticsHTML+="|  "+siteMap.get(siteKey)+"0场 ";
							}
						}
					}
					
					$("#statistics").text(statisticsHTML);
				}
		});
	};
	
	//获取选中行的Id
	this.getSelectIds = function(){
		
		var result = "";
		
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find("tbody :checkbox:checked").each(function(index){
			
			if(index == 0){
				
				result = $(this).attr("id");
			}else{
				result += ","+$(this).attr("id");
			}
		});
		
		return result;
	};
	
	//跳转到页面
	this.goToPage = function(pageNo){
		
		this.continueAjaxRecords = false;
		
		var inter = null;
		
		//判断上一次的操作的异步加载是否结束, 如果没有结束, 在下一次告诉他结束异步加载, 并且反馈回来.
		inter = setInterval(function(){
			
			if($this.handshake){
				
				clearInterval(inter);
				
				$this.summaryFlag = true;
				
				var pageSize = $("#pageSize").val();
				
				if(pageNo==null){
					
					pageNo=$("#pagenum").val()-1;
				}
				
				$this.page.pagenum=pageNo;
				
				if(pageSize){
					$this.page.pagesize=pageSize;
				}
				
				$this.loadTable();
			}
			
		},50);
	};
	
	this.refresh = function(){
		
		this.loadTable();
	};
	
	this.getRowData = function(index){
		return this.source.data[index];
	};
	
	this.getRowIndex = function(viewId){
		
		if(!viewId){
			return null;
		}
		
		return parseInt($("#"+viewId).attr("index"));
	};
	
	this.selectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	this.unSelectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	//获取选中行的行号
	this.getSelectedIndexs = function() {
		
		var resultArray = new Array();
		
		var _tBody = $(".cc");
		_tBody.find(":checkbox").each(function(event) {
			if ($(this).prop("checked")) {
				resultArray.push($(this).attr("index"));
			}
		});
		
		return resultArray;
	};
	
	//更新单行数据
	this.updaterowdata = function(rowIndex, rowData) {
		
		var _tBody = $(".cc");
		
		rowData.selected = true;
		var _row = this.createRow(_tBody, rowData, rowIndex);
		var $row = $(_row);
		
		//然后替换表格中指定的行
		_tBody.find("tr[rowid="+ rowIndex +"]").replaceWith($row);
		
		//$("#_already_selected").html($("#tablebody :checked").length);
		//$("#checkedAll").prop("checked",false);
	};

	//更新一行中的一列数据 createCell方法还未完成，cellIndex获取方法也未完成
	this.updatecell = function(rowIndex, cellIndex, cellData) {
		var _tBody = $(".cc");
		var _row = _tBody.find(" tr[rowid="+ rowIndex +"]");

		//生成表格的列
		var _cell = this.createCell();
		
		//替换掉表格中指定的列
		_row.find("td[cellid="+ cellIndex +"]").replaceWith(_cell);
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		
		/*var style = "";*/
		
		/*if(rowData.shootStatus != ""){
			
			style = " style='background-color:"+getColor(rowData.shootStatus)+";' ";
		}*/
		
		/*var _row =["<tr rowId='"+rowid+"'" + style + " >"];*/
		var _row;
		if (rowData.selected) {
			_row = ["<tr rowId ='" + rowid +"' class='selected' onclick='toggleClick(this)'>"];
		} else {
			_row = ["<tr rowId ='" + rowid +"' onclick='toggleClick(this)'>"];
		}
		for(var j=0;j<columns.length;j++){
			
			if(columns[j].isCheckbox){
				if(rowData.formType == 1){
//					var hasChecked = rowData.hasChecked;
//					if(hasChecked){
//						_row.push('<td cellid="'+ j +'" class="bold"><input class="div-checkbox" type="checkbox" checked rid="'+ rowData.receiptId +'" id="'+rowData.receiptId+'" index="'+rowid+'" class="line-height"/></td>');
//					}else{
						_row.push('<td cellid="'+ j +'" class="bold"><input class="div-checkbox" type="checkbox" rid="'+ rowData.receiptId +'" id="'+rowData.receiptId+'" index="'+rowid+'" class="line-height"/></td>');
//					}
				}else{
					_row.push('<td cellid="'+ j +'" class="bold"><div class="div-blank"></div></td>');
				}
				
			}else{
				if(columns[j].cellsrenderer){
					_row.push('<td cellid="'+ j +'" style="height:14px; width:'+ columns[j].width+'; min-width:'+ columns[j].width +'; max-width:'+ columns[j].width +';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</td>');
				}else{
					var columnData = rowData[columns[j].filedName];
					if (columnData == null) {
						columnData = "";
					}
					_row.push('<td cellid="'+ j +'" style="height:14px;width:' + columns[j].width +'; min-width:'+ columns[j].width +'; max-width:'+ columns[j].width +'; overflow-y:hidden;">'+columnData+'</td>');
				}
			}
		}
		_row.push("</tr>");
		
		return _row.join('');
	};
}


//重置文本框的高度
function resizeViewGird() {
//    var tableheadHeight = $("#tableHead").height();
//    var currencyListHeight = $("#currencyListDiv").height();
//    $(".cc").css("height", window.innerHeight - 150 - tableheadHeight - currencyListHeight);
	  $(".cc").css("height", "calc(100% - 30px)");
}

//滚动条自动到底部
function scrollToBottom(){
	var tableHeight = $("#_table_doc").height();
	$(".cc").scrollTop(tableHeight);
}


//修改表格调用
function aa() {
	var b = document.getElementById("cc").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}
//行checkbox事件只判断是否全选
function isCheckAll(){
	
	var _tableObj = $("#tablebody");
	var checkboxs = _tableObj.find(":checkbox");
	
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#checkedAll").prop("checked",false);
	}else{
		$("#checkedAll").prop("checked",true);
	}
}


//初始化导入窗口
function initImportWin() {
	$("#importRunningAccountWin").jqxWindow({
		theme: theme,
		height: 540,
		width: 482,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			
		}
	});
}
//显示导入窗口
function showImportWin() {
	$("#importRunningAccountWin").jqxWindow("open");
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/getCostManager/importRunningAccount&&needIsCover=true&&refreshUrl=/getCostManager/toFinanceRunningAccountPage&&templateUrl=/template/import/财务流水账导入模板.xls");
}

//关闭导入窗口
function closeImportWin(){
	$("#importRunningAccountWin").jqxWindow("close");
}

//ajax请求
function sendRequest(options){
	$.ajax({
		url: options.url,
		type: options.type,
		data: options.data,
		datatype: options.datatype,
		success: options.getResult
	});
}


//获取货币统计信息
function getCurrencyList(){
	var options = {
		url: '/getCostManager/queryRunnigAccountStatistic',
		type: 'post',
		/*data: subData,*/
		data: filter,
		datatype: 'json',
		getResult: function(response){
			if(response.success){
				
				var currencyList = response.currencyList;
				var html = [];
				html.push("<table class='currency-list-table' cellspacing=0 cellpadding=0>");
				html.push("<thead><tr><td class='red-td'>统计</td><td>币种</td><td>收款金额</td><td>付款金额</td><td>余额</td></tr><thead>");
			
				if(currencyList != null){
					html.push("<tbody>");
					for(var i= 0; i< currencyList.length; i++){
						if(i== 0){
							html.push("<tr>");
							html.push("<td rowspan='"+ currencyList.length+"'>合计</td>");
							html.push("<td>" + currencyList[i].currencyCode + "</td>");
							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].collectMoney) + "</td>");
							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].payedMoney) + "</td>");
							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].leftMoney) + "</td>");
							html.push("</tr>");
						}else{
							html.push("<tr>");
							html.push("<td class='td-border'>" + currencyList[i].currencyCode + "</td>");
							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].collectMoney) + "</td>");
							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].payedMoney) + "</td>");
							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].leftMoney) + "</td>");
							html.push("</tr>");
						}
					}
					html.push("</tbody>");
				}
				html.push("</table>");
				$("#currencyListDiv").empty();
				$("#currencyListDiv").append(html.join(""));
				var currencyHeight = $(".currency-list-table").height();
				$("#currencyListDiv").height(currencyHeight);
				$("#runningAccountListDiv").css({"height": "calc(100% - "+ currencyHeight +"px)"});
			}else{
				//showErrorMessage(response.message);
			}
		}
	};
	sendRequest(options);

}



//获取筛选票据日期列表
function getaimDataList(){
	var options = {
		url: '/getCostManager/queryDropDownData',
		type: 'post',
		data: {includePayment: true, includeCollection: true, includeLoan: true},
		datatype: 'json',
		getResult: function(response){
			if(response.success){
				
				var html = [];
				html.push("<option value=''>全部</option>");
				var receiptMoonList = response.receiptMoonList;
				if(receiptMoonList != null){
					
					for(var i= 0; i< receiptMoonList.length; i++){
						html.push("<option value='"+ receiptMoonList[i]+"'>"+receiptMoonList[i]+"</option>");
					}
				}
				$("#selectAimdateBtn").empty();
				$("#selectAimdateBtn").append(html.join(""));
				
			}else{
				//showErrorMessage(response.message);
			}
		}
	};
	sendRequest(options);
}


//显示高级查询窗口
function showAdvanceQueryWin(){
	//清空查询下拉列表
	$("#department").html('');
	$("#agents").html('');
	$("#aimPeopleNames").html('');
	$("#aimDates").html('');
	$("#queryPaymentWay").html('');
	showDropDownData();
	$("#advanceQueryWin").jqxWindow("open");
}
//初始化高级查询窗口
function initAdvanceQueryWin(){
	$("#advanceQueryWin").jqxWindow({
		theme: theme,
		height: 400,
		width: 720,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			//showDropDownData();
		}
	});
	//带有会计科目的高级查询窗口
	$("#hasAccountQueryWin").jqxWindow({
		theme: theme,
		height: 300,
		width: 720,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			showPaymentDateList();
		}
	});
}


//检索金额
function indexOfMoney(own){
	own= $(own);
	own.val(own.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	own.val(own.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	own.val(own.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	own.val(own.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}


//查询按钮
function determineQuery(){
	/*subData.financeSubjIds= $('#financeSubjectTree').jqxTree('getCheckedItems');*/
	var financeSubjIds= $("#financeIds").val();
	financeSubjIds= financeSubjIds.substr(0, financeSubjIds.length-1);
	filter.financeSubjIds= financeSubjIds;
	if($("#aimPeopleNames").val()!= null){
		
		filter.aimPeopleNames= $("#aimPeopleNames").val().toString();
	}else{
		filter.aimPeopleNames= "";
	}
	if($("#aimDates").val()!= null){
		filter.aimDates= $("#aimDates").val().toString();
	}else{
		filter.aimDates= "";
	}
	//记账人
	if($("#agents").val()!= null){
		filter.agents= $("#agents").val().toString();
	}else{
		filter.agents= "";
	}
	//部门
	if($("#department").val()!= null){
		filter.department= $("#department").val().toString();
	}else{
		filter.department= "";
	}
	
	if($("#includeLoanBtn").hasClass("on")){
		filter.includeLoan= true;
	}else{
		filter.includeLoan= false;
	}
	
	if($("#minMoney").val()-0 > $("#maxMoney").val()-0){
		
		showErrorMessage("最大金额不能小于最小金额");
		return;
	}
	
	
	filter.aimMonth= $("#selectAimdateBtn").val();
	if($("input[name=formType]:checked").val()==""){
		filter.formType="";
	}else{
		filter.formType= $("input[name=formType]:checked").val()-0;
	}
	
	
	if($("input[name=hasReceipt]:checked").val() == "true"){
		filter.hasReceipt= true;
	}
    if($("input[name=hasReceipt]:checked").val() == "false"){
    	filter.hasReceipt= false;
	}
    if($("input[name=hasReceipt]:checked").val()==""){
    	filter.hasReceipt= "";
    }
	if($("input[name=status]:checked").val() == ""){
		
		filter.status= "";
	}else{
		filter.status= $("input[name=status]:checked").val()-0;
	}
	if($("input[name=billType]:checked").val() == ""){
		
		filter.billType= "";
	}else{
		filter.billType= $("input[name=billType]:checked").val()-0;
	}
	
	filter.summary= $("#querySummary").val();
	filter.minMoney= $("#minMoney").val();
	filter.maxMoney= $("#maxMoney").val();
	if($("input[name=formType][value=3]").is(':checked')){
        $("#includeLoanBtn").addClass("on");
        filter.includeLoan = true;
	}else{
		$("#includeLoanBtn").removeClass("on");
		filter.includeLoan = false;
	}
	
	filter.paymentWayId = $("#queryPaymentWay").val();
	
	$("#advanceQueryWin").jqxWindow("close");
	grid.setFilter(filter);
	$("#currencyListDiv").empty();
	getCurrencyList();
	//成功后清空财务科目Id
//	$("#financeIds").val("");
}

//清空高级查询条件
function clearQuery(){
	 $('#financeSubjectTree').jqxTree('uncheckAll');
	 $("#financeSubjIds").jqxDropDownButton('setContent', '');
	 $('.selectpicker').selectpicker('deselectAll');
	 
     $("input[name=formType][type=radio][value='']").prop("checked", true);
     $("input[name=hasReceipt]:eq(0)").prop("checked", true);
     $("input[name=status]:eq(0)").prop("checked", true);
     $("input[name=billType]:eq(0)").prop("checked", true);
	 $("#querySummary").val("");
	 $("#minMoney").val("");
	 $("#maxMoney").val("");
	 //清空付款单id和财务科目id
	 $("#paymentIds").val("");
	 $("#financeIds").val("");
	 $("#queryPaymentWay").val("");
}

//关闭高级查询窗口
function closeQuery(){
	$("#advanceQueryWin").jqxWindow("close");
}

//初始化下拉按钮
function initDropDownBtn(){
	$("#financeSubjIds").jqxDropDownButton({width: 200, height: 30});
	$("#accountFinanceSubjIds").jqxDropDownButton({width: 200, height: 30});
}


//加载财务科目列表
function loadFinanceSubjList(){
	
	 var data=[];
	 var options= {
			 url: '/financeSubject/querySubjectListWithJqxTreeFormat',
			 type: 'post',
			 datatype: 'json',
			 
			 getResult: function(response){
				 if(response.success){
					 var subjectList= response.subjectList;
					 
					 for(var i= 0; i< subjectList.length; i++){
						 data.push({'id': subjectList[i].id, 'parentId': subjectList[i].parentId, 'text': subjectList[i].text});
					 }
					 
									//初始化财务科目
					 var source = {
						      datatype: "json",
						      datafields: [
						          { name: 'id' },
						          { name: 'parentId' },
						          { name: 'text' },
						      ],
						      
						      localdata: data,
						      id: 'id'
						      
						 };
						 var dataAdapter = new $.jqx.dataAdapter(source);
						 dataAdapter.dataBind();
						 var records= dataAdapter.getRecordsHierarchy('id','parentId','items', [{name: 'text', map:'label'}]);
						 $("#financeSubjectTree").jqxTree({
							 theme: theme,
							 width: 200,
							 height:200,
							 source: records,
							 hasThreeStates: true,
							 checkboxes: true,
							 
						 });
						 
						 $('#financeSubjectTree').on('checkChange', function (event){
									
								   var content = "";
								   var financeIds ="";
								   var items = $('#financeSubjectTree').jqxTree('getCheckedItems');
								   
								   for(var i= 0; i< items.length; i++){
								   var item=items[i];
								   if(item.hasItems == false){
									   content += item.label + ",";
									   
									   financeIds += item.id+",";		
								   }
							  }
							  $("#financeIds").val(financeIds);
							  $("#financeSubjIds").jqxDropDownButton('setContent', content);
						}); 
						
						
						/************************************带有会计科目信息查询时的财务科目列表***********************************/
						 $("#accountFinanceSubjectTree").jqxTree({
							 theme: theme,
							 width: 200,
							 height:200,
							 source: records,
							 hasThreeStates: true,
							 checkboxes: true,
							 
						 });
						 $('#accountFinanceSubjectTree').on('checkChange', function (event){
								
							   var content = "";
							   var financeIds ="";
							   var items = $('#accountFinanceSubjectTree').jqxTree('getCheckedItems');
							   
							   for(var i= 0; i< items.length; i++){
							   var item=items[i];
							   if(item.hasItems == false){
								   content += item.label + ",";
								   
								   financeIds += item.id+",";		
							   }
						  }
						  $("#accountFinanceIds").val(financeIds);
						  $("#accountFinanceSubjIds").jqxDropDownButton('setContent', content);
					}); 
					 
				 }else{
					 //showErrorMessage(response.message);
				 }
			 }
	 };
	 sendRequest(options);
	
}


//显示清空按钮
function showClearBtn(){
	//显示清空按钮
	$('.query-condition-table td').on('mouseover', function(event) {
        if ($(this).find("select").val() != null && $(this).find("select").val() != undefined) {
            $(this).find(".clearSelection").show();
        }
    });
	
    
    $('.query-condition-table td').on('mouseout', function(event) {
        $(this).find(".clearSelection").hide();
    });
}

//清空选中的收/付款人员
function clearSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
}

//显示账务详情高级查询中下拉列表
function showDropDownData(){
	var options= {
		url: '/getCostManager/queryDropDownData',
		type: 'post',
		data: {includePayment: true, includeCollection: true, includeLoan: true},
		datatype: 'json',
		getResult: function(response){
			if(response.success){
				//部门
				var departmentList = response.departmentList;
				var deparmentHtml = [];
				for(var i= 0; i< departmentList.length; i++){
					var departmentStr = departmentList[i].department;
					if (departmentStr != "" && departmentStr != null) {
						deparmentHtml.push("<option value='"+ departmentList[i].department +"'>" + departmentList[i].department + "</option>");
					}
				}
				$("#department").append(deparmentHtml.join(""));
				if (filter.department == null || filter.department == undefined) {
					filter.department = "";
				}
				$("#department").selectpicker('val', filter.department.split(','));
				$("#department").selectpicker("refresh");
				
				//记账人
				var agentList = response.agentList;
				var agentHtml = [];
				
				for(var i= 0; i< agentList.length; i++){
					agentHtml.push("<option value='"+ agentList[i] +"'>" + agentList[i] + "</option>");
				}
				
				$("#agents").append(agentHtml.join(""));
				if (filter.agents == null || filter.agents == undefined) {
					filter.agents = "";
				}
				$("#agents").selectpicker('val', filter.agents.split(','));
				$("#agents").selectpicker("refresh");
				//收/付款人
				var aimPeople = response.aimPeople;
				var aimPeopleHtml = [];
				for(var i= 0; i< aimPeople.length; i++){
					aimPeopleHtml.push("<option value='"+ aimPeople[i] +"'>" + aimPeople[i] + "</option>");
				}
				$("#aimPeopleNames").append(aimPeopleHtml.join(""));
				if (filter.aimPeopleNames == null || filter.aimPeopleNames == undefined) {
					filter.aimPeopleNames = "";
				}
				$("#aimPeopleNames").selectpicker('val', filter.aimPeopleNames.split(','));
				$("#aimPeopleNames").selectpicker("refresh");
				
				var receiptMoonHtml = [];
				receiptMoonHtml.push("<option value=''>全部</option>");
				var receiptMoonList = response.receiptMoonList;
				if(receiptMoonList != null){
					for(var i= 0; i< receiptMoonList.length; i++){
						receiptMoonHtml.push("<option value='"+ receiptMoonList[i]+"'>"+receiptMoonList[i]+"</option>");
					}
				}
				
				//票据日期
				var receiptDateList = response.receiptDateList;
				var receiptDateHtml = [];
				
				for(var i= 0; i< receiptDateList.length; i++){
					receiptDateHtml.push("<option value='"+ receiptDateList[i] +"'>" + receiptDateList[i] + "</option>");
				}
				
				$("#aimDates").append(receiptDateHtml.join(""));
				if (filter.aimDates == null || filter.aimDates == undefined) {
					filter.aimDates = "";
				}
				$("#aimDates").selectpicker('val', filter.aimDates.split(','));
				$("#aimDates").selectpicker("refresh");
				
				//付款方式
				var paymentWayList = response.paymentWayList;
				var paymentWayHtml = [];
				paymentWayHtml.push("<option value=''>全部</option>");
				for(var i= 0; i< paymentWayList.length; i++){
					paymentWayHtml.push("<option value='"+ paymentWayList[i].wayId +"'>" + paymentWayList[i].wayName + "</option>");
				}
				$("#queryPaymentWay").append(paymentWayHtml.join(""));
				if (filter.paymentWayId == null || filter.paymentWayId == undefined) {
					filter.paymentWayId = "";
				}
				$("#queryPaymentWay").selectpicker('val', filter.paymentWayId.split(','));
				$('#queryPaymentWay').selectpicker("refresh");
				
				//设置选中
				if (filter != null && filter.length >0) {
					$("input[name=formType][value="+ filter.formType +"]").prop('checked', true);
					$("input[name=hasReceipt][value="+ filter.hasReceipt +"]").prop('checked', true);
					$("input[name=status][value="+ filter.status +"]").prop('checked', true);
					$("input[name=billType][value="+ filter.billType +"]").prop('checked', true);
				}
				
			}else{
				//showErrorMessage(response.message);
			}
		}
	};
	sendRequest(options);
}

//是否包含借款单
function isIncludeLoanBtn(own){
	own= $(own);
	if(own.hasClass("on")){
		own.removeClass("on");
		filter.includeLoan= false;
		grid.setFilter(filter);
		$("#currencyListDiv").empty();
		getCurrencyList();
		
	}else{
		own.addClass("on");
		filter.includeLoan= true;
		grid.setFilter(filter);
		$("#currencyListDiv").empty();
		getCurrencyList();
	}
}

//选择票据日期
function selectAimdate(own){
	own= $(own);
	
	filter.aimMonth= own.val();
	getCurrencyList();
	grid.setFilter(filter);
}

//结算
function settlePaymentBatch(){
	$("#paymentIds").val("");
	var count=0;
	var paymentIds;
	$(".div-checkbox").each(function(i){
		
		if($(this).is(':checked')){
			count++;
			paymentIds= $("#paymentIds").val();
			
			paymentIds+= "," + $(this).attr("rid")+ ",";
			paymentIds= paymentIds.replace(/(^\,*)|(\,*$)/g, "");
			$("#paymentIds").val(paymentIds);
		}
		
	});
	if(count<= 0){
		showErrorMessage("请选择未结算的数据");
		return;
	}
	
	popupPromptBox("提示","确定要结算所选择的单据？",function(){
		var options={
				url: '/paymentManager/settlePaymentBatch',
				type: 'post',
				data: {paymentIds: $("#paymentIds").val()},
				datatype: 'json',
				getResult: function(response){
					if(response.success){
						
						showSuccessMessage("操作成功");
						$("#paymentIds").val("");
						
						var idArray = grid.getSelectedIndexs();
						grid.unSelectAll();
						grid.unCheckAll();
						for(var i=0,le = idArray.length;i<le;i++){
							var index = idArray[i];
							var data = grid.getRowData(parseInt(index));
							data.status = 1;
							grid.updaterowdata(index, data);
						}
					}else{
						showErrorMessage(response.message);
					}
				}
		};
		sendRequest(options);
	});
	
}


//无票改有票
function changeTicket(){
	var count=0;
	var paymentIds;
	$(".div-checkbox").each(function(i){
		
		if($(this).is(':checked')){
			count++;
			paymentIds= $("#paymentIds").val();
			
			paymentIds+= "," + $(this).attr("rid")+ ",";
			paymentIds= paymentIds.replace(/(^\,*)|(\,*$)/g, "");
			$("#paymentIds").val(paymentIds);
		}
		
	});
	if(count<= 0){
		showErrorMessage("请选择无票的数据");
		return;
	}
	var options={
			url: '/paymentManager/setPaymentHasReceiptBatch',
			type: 'post',
			data: {paymentIds: $("#paymentIds").val()},
			datatype: 'json',
			getResult: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					$("#paymentIds").val("");
					
					var idArray = grid.getSelectedIndexs();
					grid.refresh();
					for(var i=0,le = idArray.length;i<le;i++){
						var index = idArray[i];
						var data = grid.getRowData(parseInt(index));
						data.hasReceipt = true;
						grid.updaterowdata(index, data);
					}
				}else{
					showErrorMessage(response.message);
				}
			}
	};
	sendRequest(options);
}


//加载带有会计科目的付款单列表
function loadAccountPaymentList(){
	var source = {
			url: '/paymentManager/queryWithAccSubjAndFinaSubjInfo',
			type: "post",
			dataType : "json",
			data: accountSubData,
			datafields : [
			     {name: "paymentDate", type: "string"},
			     {name: "receiptNo", type: "string"},
			     {name: "accountCode", type: "string"},
			     {name: "accountName", type: "string"},
			     {name: "subjectName", type: "string"},
			     {name: "summary", type: "string"},
			     {name: "money", type: "double"},
			     {name: "payeeName", type: "string"},
			     {name: "hasReceipt", type: "int"},
			     {name: "billType", type: "int"},
			     {name: "wayName", type: "string"},
			     {name: "currencyCode", type: "string"},
			     
			],
			root: 'paymentList'
		};
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		
		//金额列
		var moneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			var html = [];
			html.push("<div class='jqx-column column-align-left'>" + rowdata.money +"(" +rowdata.currencyCode +")" + "</div>");
			html.join("");
			return html;
		};
		//发票类型列
		var billTypeRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			if(rowdata.billType == 1){
				var html = [];
				html.push("<div class='jqx-column column-align-center'>普通发票</div>");
				html.join("");
				return html;
			}
			if(rowdata.billType == 2){
				var html = [];
				html.push("<div class='jqx-column column-align-center'>增值税发票</div>");
				html.join("");
				return html;
			}
		};
		
	//生成会计科目表格
	$("#accountSubjectTable").jqxGrid({
		theme: theme,
		width: "100%",
		height: "100%",
		source: dataAdapter,
		showtoolbar: true,
		localization: localizationobj,
		rendertoolbar: function(toolbar){
			var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='return-btn' id='returnBtn' onclick='returnLastPage()'>");
			container.push("<input type='button' class='advance-search-finance-btn' id='accountSubjQueryBtn' onclick='showHasAccountQueryWin()'>");
			if(hasExportFinanceDetailAuth) {
				container.push("<input type='button' class='export-btn' id='exportAccountSubjBtn' onclick='exportAccountSubjList()'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			
			$("#returnBtn").jqxTooltip({content: "返回", position: "bottom"});
			$("#accountSubjQueryBtn").jqxTooltip({content: "高级查询", position: "bottom"});
			if(hasExportFinanceDetailAuth) {
				$("#exportAccountSubjBtn").jqxTooltip({content: "导出", position: "bottom"});
			}
		},
		columns: [
					{text: "日期", datafield: 'paymentDate', width: '10%', cellsAlign: 'center', align: 'center', sortable: false},
					{text: "会计科目编码", datafield: 'accountCode', width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
					{text: "会计科目", datafield: 'accountName', width: '10%', cellsAlign: 'center', align: 'center', sortable: false},
					{text: "预算科目", datafield: 'subjectName', width: '10%', cellsAlign: 'center', align: 'center', sortable: false},
					{text: "摘要", datafield: 'summary', width: '20%', cellsAlign: 'left', align: 'center', sortable: false},
					{text: "支付方式", datafield: 'wayName', width: '10%', cellsAlign: 'center', align: 'center', sortable: false},
					{text: "金额", datafield: 'money', cellsrenderer: moneyRenderer, width: '10%', cellsAlign: 'right', align: 'center', sortable: false},
					{text: "收款人", datafield: 'payeeName', width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
					{text: "发票类型", datafield: 'billType', cellsrenderer:billTypeRenderer, width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
					
				],
		cellhover: function(e) {
					/*var $this = $(e);*/
		}
	});
}


//会计科目
function AccountSubjectPage(){
	//将当前窗体隐藏
	loadAccountPaymentList();
	$("#myContainerWin").hide();
	$("#accountSubjectPage").show();
	
}




/*//导出
function exportList(){
    var form = $("<form></form>");
    form.attr("action","/getCostManager/exportRunningAccountInfo");
    form.attr("method","post");
    
    form.append("<input type='hidden' name='financeSubjIds'>");
    form.find("input[name='financeSubjIds']").val(filter.financeSubjIds);
    
    form.append("<input type='hidden' name='aimPeopleNames'>");
    form.find("input[name='aimPeopleNames']").val(filter.aimPeopleNames);
    
    form.append("<input type='hidden' name='aimDates'>");
    form.find("input[name='aimDates']").val(filter.aimDates);
    
    form.append("<input type='hidden' name='aimMonth'>");
    form.find("input[name='aimMonth']").val(filter.aimMonth);
    
    form.append("<input type='hidden' name='agents'>");
    form.find("input[name='agents']").val(filter.agents);
    
    form.append("<input type='hidden' name='formType'>");
    form.find("input[name='formType']").val(filter.formType);
    
    form.append("<input type='hidden' name='hasReceipt'>");
    form.find("input[name='hasReceipt']").val(filter.hasReceipt);
    
    form.append("<input type='hidden' name='status'>");
    form.find("input[name='status']").val(filter.status);
    
    form.append("<input type='hidden' name='summary'>");
    form.find("input[name='summary']").val(filter.summary);
    
    form.append("<input type='hidden' name='minMoney'>");
    form.find("input[name='minMoney']").val(filter.minMoney);
    
    form.append("<input type='hidden' name='maxMoney'>");
    form.find("input[name='maxMoney']").val(filter.maxMoney);
    
    form.append("<input type='hidden' name='includeLoan'>");
    form.find("input[name='includeLoan']").val(filter.includeLoan);
    
    form.submit();
}*/
//导出按钮
function exportList(){
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
	
	$.ajax({
		url: '/getCostManager/exportFinanceRunningAccount',
		type: 'post',
		data: filter,
		datatype: 'json',
		success: function(response){
			_LoadingHtml.hide();
            $(".opacityAll").hide();
			if (response.success) {
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
}


//批量打印付款单
function printPayments(){
	$("#paymentIds").val("");
	var count=0;
	var paymentIds;
	$(".div-checkbox").each(function(i){
		
		if($(this).is(':checked')){
			count++;
			paymentIds= $("#paymentIds").val();
			
			paymentIds+= "," + $(this).attr("rid")+ ",";
			paymentIds= paymentIds.replace(/(^\,*)|(\,*$)/g, "");
			$("#paymentIds").val(paymentIds);
		}
		
	});
	if(count<= 0){
		showErrorMessage("请选择要打印的付款单");
		return;
	}
	if(count > 5){
		showErrorMessage("批量打印最多可选择5张单据");
		return;
	}
	
	/*window.location.href='/paymentManager/toPrintPaymentInfoPage?paymentIds='+$("#paymentIds").val();*/
	window.open('/paymentManager/toPrintPaymentInfoPage?paymentIds='+$("#paymentIds").val()+"&&needClosePage=true", '_blank');
	
}



//点击行的数据
var selectedIndex ;
var billFormType;
//var hasChecked;
//显示修改付款单，收款单
function showModifyReceipt(billType, billId,own){
	
//	var checkbox = $(own).parents('tr').find('input[type=checkbox]');
//	if(checkbox){
//		if(checkbox.is(':checked')){
//			hasChecked = true;
//		}
//	}
	
	
	billFormType = billType;
	
	/*var dataRecord = $("#runningAccountList").jqxGrid('getrowdata', editrow);*/
	//付款单
	if(billType == 1){
		var index = grid.getRowIndex(billId);
		selectedIndex = index;
		var paymentId= billId;
		$("#fullReceiptDiv").empty();
//		$("#fullReceiptDiv").load("/paymentManager/toPaymentDetailPage?paymentId="+paymentId);
		$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage?paymentId="+paymentId);
		$(".modify-payment-btn-list").show();
		$(".modify-collection-btn-list").hide();
		//设置权限
		if(isRunningAccountReadonly){
			$("#saveModifyAfterPayment").remove();
		}
		
		$.ajax({
			url: '/paymentManager/queryPaymentDetail',
			type: 'post',
			data: {"paymentId": paymentId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					$("#modifyReceiptWin").jqxWindow("open");
					$("#paymentUploadWrap").show();
					//初始化上传插件
					initUploader();
					//加载已上传的附件信息
					var  attachmentList = response.attachmentList;
					if(attachmentList != undefined){
						initUploadFilesInfo(attachmentList);
					}
					
				}
			}
		});
	}
	//收款单
	if(billType == 2){
		var index = $(own).parents('tr').attr('rowid');
		selectedIndex = index;
		var collectionId = billId;
		$("#fullReceiptDiv").empty();
		$("#fullReceiptDiv").attr("src", "/collectionManager/toCollectionDetailInfo?collectionId="+collectionId);
		$(".modify-collection-btn-list").show();
		$(".modify-payment-btn-list").hide();
		if(!isRunningAccountReadonly){
			
		}else{
			$("#saveModifyAfterColletion").remove();
			/*$("#paymentOrderSettleBtn").css("display", "none");*/
		}
		$("#paymentUploadWrap").hide();
		$("#modifyReceiptWin").jqxWindow("open");
		
	}
}

//初始化修改付款单，收款单
function initModifyReceipts(){
	var screenWidth = window.screen.width;//屏幕分辨率
	var jqxWinWidth;//窗口的宽度
	if(screenWidth >= 1366 && screenWidth <= 1399){
		jqxWinWidth = 1200;
	}else if(screenWidth >= 1400 && screenWidth <= 1440){
		jqxWinWidth = 1300;
	}else if(screenWidth > 1440 && screenWidth <= 1600){
		jqxWinWidth = 1400;
	}else{
		jqxWinWidth = 1536;
	}
	$("#modifyReceiptWin").jqxWindow({
		theme: theme,
		width: jqxWinWidth,
	    height: 720,
	    maxWidth: 1866,
		maxHeight: 768,
		modalZIndex: 0,
	    resizable: false,
		isModal: true,
		autoOpen: false
	});
    
}


//关闭修改付款单，收款单
function closeModifyReceiptWin(){
	$("#modifyReceiptWin").jqxWindow("close");
}

//生成财务关联信息
function financeSubjInfo(){
    var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var financeRecordInfo ="";
	var addSubTr = _frame.find("tr.add-sub-tr");
	$.each(addSubTr, function(i){
		
		financeRecordInfo += $(this).find("input[type=text]").eq(0).val()+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).attr("fid")+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).val()+"##";
		var noformatMoney = $(this).find("input[type=text]").eq(2).next("input[type=hidden]").val();
		financeRecordInfo += noformatMoney +"&&";
	});
	return financeRecordInfo.substr(0,financeRecordInfo.length-2);
}


//获取付款单的数据
function saveOrderData(status){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var subData = {};
	subData.paymentId = _frame.find("#paymentId").val();
	subData.receiptNo = _frame.find("#billsNum").val();
	subData.paymentDate = _frame.find("#billsDateTime").val();
	subData.payeeName = _frame.find("#receivingParty").val();
	
	subData.contractId = _frame.find("#contractId").val();
	subData.contractType = _frame.find("#contractType").val();
	subData.loanIds = _frame.find("#loanIds").val();
	subData.contractNo = _frame.find("#contractNo").val();
	subData.status = status;
	subData.attpacketId = _frame.find("#attachmentPcketId").val();
	
	subData.currencyId = _frame.find("#currency").val();
	subData.totalMoney = _frame.find("#readTotalMoney").val()-0;
	subData.paymentWay = _frame.find("#paymentWay").val();
	subData.department = _frame.find("#departmentText").val();
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
	subData.contractPartId = _frame.find("#contractPartId").val();
	
	return subData;	
}

//保存\结算付款单
function saveModifyAfterPayment(status){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	if(_frame.find("#receivingParty").val()==""){
		showErrorMessage("收款人(单位)不能为空");
		return;
	}
	var financeSubjName="";
	var addSubTr = _frame.find("tr.add-sub-tr");
	$.each(addSubTr, function(i){
		financeSubjName+=$(this).find("input[type=text]").eq(1).val();
	});
	if(financeSubjName==""){
		showErrorMessage("财务科目不能为空");
		return;
	}
	$.each(addSubTr, function(){
		
		if($(this).find("input[type=text]").eq(2).val()==""){
			showErrorMessage("金额不能为空");
			throw new Error("金额不能为空");
		}
		
	});
	
	var subData = saveOrderData(status);
	
	//拼装单行刷新需要的数据
	var updateOnRowData = {};
	updateOnRowData.receiptId = subData.paymentId;
	updateOnRowData.receiptDate = subData.paymentDate;
	updateOnRowData.receiptNo = subData.receiptNo.replace(new RegExp(/(-)/g),'');
	
	updateOnRowData.paymentWay = subData.paymentWay;
	updateOnRowData.billCount = subData.billCount;
	updateOnRowData.department = subData.department;
	
	var pinfo = subData.paymentSubjMapStr.split('&&');
	var summary = [];
	var financeSubjName = [];
	$.each(pinfo, function(index, value) {
		var singleFinanceSubjInfo = value.split("##");
		var mySummary = singleFinanceSubjInfo[0];
		var myFinanceSubjName = singleFinanceSubjInfo[2];
		
		if (mySummary != '' && mySummary != null && mySummary != undefined) {
			summary.push(mySummary);
		}
		financeSubjName.push(myFinanceSubjName);
	});
	
	if (summary != null && summary.length > 0) {
		updateOnRowData.summary  = summary.join(" | ");
	}else {
		updateOnRowData.summary  = summary.join("");
	}
	updateOnRowData.financeSubjName  = financeSubjName.join(" | ");
	
	
	updateOnRowData.aimPersonName = subData.payeeName;
	var btype = billFormType;
	var currencyId = subData.currencyId;
	updateOnRowData.formType = btype;
	if(btype == 1){
		updateOnRowData.formTypeStr = '付款单';
		updateOnRowData.payedMoney = subData.totalMoney;
		if (singleCurrencyFlag) {
			updateOnRowData.payedMoneyStr = fmoney(subData.totalMoney);
			updateOnRowData.collectMoneyStr = '0.00';
		} else {
			updateOnRowData.payedMoneyStr = fmoney(subData.totalMoney)+"("+currencyIdCodeMap[currencyId]+")";
			updateOnRowData.collectMoneyStr = '0.00'+"("+currencyIdCodeMap[currencyId]+")";
		}
		
		updateOnRowData.collectMoney = 0;
		
	}else if(btype == 2){
		updateOnRowData.formTypeStr = '收款单';
		updateOnRowData.collectMoney = subData.totalMoney;
		
		updateOnRowData.payedMoney = 0;
		
		if (singleCurrencyFlag) {
			updateOnRowData.collectMoneyStr = fmoney(subData.totalMoney);
			updateOnRowData.payedMoneyStr = '0.00';
		} else {
			updateOnRowData.collectMoneyStr = fmoney(subData.totalMoney)+"("+currencyIdCodeMap[currencyId]+")";
			updateOnRowData.payedMoneyStr = '0.00'+"("+currencyIdCodeMap[currencyId]+")";
		}
	}
	updateOnRowData.hasReceipt = subData.hasReceipt;
	updateOnRowData.agent = subData.agent;
	updateOnRowData.contractNo = subData.contractNo;
	updateOnRowData.contractType = subData.contractType;
	updateOnRowData.isCheckbox = true;
	
	updateOnRowData.contractId = subData.contractId;
	updateOnRowData.currencyId = subData.currencyId;
	
	updateOnRowData.ifReceiveBill = subData.ifReceiveBill;
	updateOnRowData.remindTime = subData.remindTime;
	updateOnRowData.attpacketId = subData.attpacketId;
	
//	updateOnRowData.hasChecked = hasChecked;
	
	/****************************保存开始**************************/
	if(status == 0 && _frame.find("#paymentStatus").val() == 1){
		swal({
			    title: "提示",
		        text: '您修改并保存了已结算单据，是否同时将单据状态改为未结算？',
		        type: "warning",
		        showCancelButton: true,  
		        confirmButtonColor: "rgba(255,103,2,1)",
		        confirmButtonText: "是",   
		        cancelButtonText: "否",   
		        closeOnConfirm: false,   
		        closeOnCancel: false
		},function (isConfirm){
			if (isConfirm){
				subData.status = 0;
				modifyStatus(updateOnRowData,subData);
			}else{
				subData.status = 1;
				modifyStatus(updateOnRowData,subData);
			}
		});
		
	} else if (status == 1) {
		popupPromptBox("提示", "确定结算该付款单？", function() {
			modifyStatus(updateOnRowData, subData);
		});
	} else {
		subData.status = _frame.find("#paymentStatus").val();
		modifyStatus(updateOnRowData,subData);
	}
	/****************************保存结束**************************/
}

//保存付款单数据
function modifyStatus(updateOnRowData,subData){
	var op = {
			url: '/paymentManager/savePaymentInfo',
			type: 'post',
			data: subData,
			datatype: 'json',
			getResult: function(response){
				if(response.success){
					if (payUploader.getFiles('inited').length != 0){
						var attpackId = response.attpacketId;
						payUploader.option('formData', {
			    			attpackId: attpackId
			    		});
						payUploader.upload();
				    }else{
				    	showSuccessMessage("操作成功");
						$("#modifyReceiptWin").jqxWindow("close");
						//grid.setFilter(filter);
						getCurrencyList();
				    }
				    
				    updateOnRowData.status = subData.status;
				    grid.unSelectAll();
					grid.updaterowdata(selectedIndex, updateOnRowData);
				}else{
					showErrorMessage(response.message);
				}
			}
	};
	sendRequest(op);
}


//删除未结算的付款单
function deleteModifyPayment(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	popupPromptBox("提示","确定要删除该单据？",function(){
		$.ajax({
			url: '/paymentManager/deletePaymentInfo',
			type: 'post',
			data: {paymentId: _frame.find("#paymentId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					$("#modifyReceiptWin").jqxWindow("close");
					/*$("#runningAccountList").jqxGrid("updatebounddata");*/
					grid.setFilter(filter);
					getCurrencyList();
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	});
	
}

//打印修改后的付款单
function printModifyAfterPayment(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	window.open('/paymentManager/toPrintPaymentInfoPage?needClosePage=true&paymentIds='+ _frame.find("#paymentId").val(), '_blank','width:500, height: 400');
}

//保存修改后的收款单
function saveModifyAfterColletion(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var subData = {};
	if(_frame.find("#payMoneyParty").val() == ""){
		showErrorMessage("付款人(单位)不能为空");
		return;
	}
	if(_frame.find("#money").val() == ""){
		showErrorMessage("金额不能为空");
		return;
	}
	subData.collectionId = _frame.find("#collectionId").val();
	subData.otherUnit = _frame.find("#payMoneyParty").val();
	subData.summary = _frame.find("#summary").val();
	subData.money = _frame.find("#money").next("input[type=hidden]").val();
	subData.currencyId = _frame.find("#currency").val();
	subData.paymentWay = _frame.find("#paymentWay").val();
	subData.agent = _frame.find("#agent").val();
	subData.collectionDate = _frame.find("#billsDateTime").val();
	subData.receiptNo = _frame.find("#billsNum").val();
	var upRowData = {};
	
	upRowData.receiptId = subData.collectionId;
	upRowData.receiptDate = subData.collectionDate;
	upRowData.receiptNo = subData.receiptNo.replace(new RegExp(/(-)/g),'');
	upRowData.agent = subData.agent;
	
	var currencyId = subData.currencyId;
	upRowData.formTypeStr = '收款单';
	upRowData.formType = '2';
	upRowData.collectMoney = subData.money;
	
	if (!singleCurrencyFlag) {
		upRowData.collectMoneyStr = fmoney(subData.money)+"("+currencyIdCodeMap[currencyId]+")";
		upRowData.payedMoneyStr = '0.00'+"("+currencyIdCodeMap[currencyId]+")";
	} else {
		upRowData.collectMoneyStr = fmoney(subData.money);
		upRowData.payedMoneyStr = '0.00';
	}
	
	upRowData.currencyId = currencyId;
	upRowData.paymentWay = subData.paymentWay;
	upRowData.summary = subData.summary;
	
	upRowData.payedMoney = '0.00';
	
	upRowData.aimPersonName = _frame.find("#payMoneyParty").val();
	upRowData.hasReceipt = "/";
	upRowData.billCount = "/";
	upRowData.status = "/";
	
	
	var op = {
			url: '/collectionManager/saveCollectionInfo',
			type: 'post',
			data: subData,
			datatype: 'json',
			getResult: function(response){
				if (response.success) {
					//单行刷新
					grid.unSelectAll();
					grid.updaterowdata(selectedIndex, upRowData);
					
					getCurrencyList();
					showSuccessMessage("操作成功");
			    	$("#modifyReceiptWin").jqxWindow("close");
				} else {
					showErrorMessage(response.message);
				}
			}
	};
	sendRequest(op);
}


//打印修改后的收款单
function printModifyAfterColletcion(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	window.open('/collectionManager/toPrintCollectionInfoPage?needClosePage=true&collectionIds='+_frame.find("#collectionId").val(), '_blank','width:500, height: 400');
}




/****************************************************查询带有会计科目的付款单信息页面************************************************/

//返回按钮
function returnLastPage(){
	//刷新当前页面
	location.reload();
}

//高级搜索
function showHasAccountQueryWin(){
	$("#hasAccountQueryWin").jqxWindow("open");
}


//显示会计科目高级查询窗口下拉项
function showPaymentDateList(){
	var options= {
			url: '/getCostManager/queryDropDownData',
			type: 'post',
			data: {includePayment: true, includeCollection: false, includeLoan: false},
			datatype: 'json',
			getResult: function(response){
				if(response.success){
					//收款日期
					var receiptDateList = response.receiptDateList;
					var receiptDateHtml = [];
					
					for(var i= 0; i< receiptDateList.length; i++){
						receiptDateHtml.push("<option value='"+ receiptDateList[i] +"'>" + receiptDateList[i] + "</option>");
					}
					
					$("#accountPaymentDates").append(receiptDateHtml.join(""));
					
					//收款人
					var aimPeople = response.aimPeople;
					var aimPeopleHtml = [];
					for(var i= 0; i< aimPeople.length; i++){
						aimPeopleHtml.push("<option value='"+ aimPeople[i] +"'>" + aimPeople[i] + "</option>");
					}
					$("#accountAimPeopleName").append(aimPeopleHtml.join(""));
					
					$(".selectpicker").selectpicker("refresh");
				}else{
					//showErrorMessage(response.message);
				}
			}
		};
		sendRequest(options);
}

//显示会计科目列表
function showAccountFinanceList(){
	$.ajax({
		url: '/accountSubject/queryAccSubjList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var accSubjList = response.accSubjList;
				var html = [];
				for(var i= 0; i< accSubjList.length; i++){
					html.push("<option value='" + accSubjList[i].code +"'>" + accSubjList[i].code + "</option>");
				}
				$("#accountSubjectCodes").append(html.join(""));
				$("#accountSubjectCodes").selectpicker("refresh");
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}


//查询
function queryHasAccoutPayment(){
	var financeSubjIds= $("#accountFinanceIds").val();
	financeSubjIds= financeSubjIds.substr(0, financeSubjIds.length-1);
	accountSubData.finaSubjIds= financeSubjIds;
    if($("#accountPaymentDates").val()!= null){
		
		accountSubData.paymentDates= $("#accountPaymentDates").val().toString();
	}else{
		accountSubData.paymentDates= "";
	}
	if($("#accountSubjectCodes").val()!= null){
		accountSubData.accSubjectCodes= $("#accountSubjectCodes").val().toString();
	}else{
		accountSubData.accSubjectCodes= "";
	}
	if($("#accountAimPeopleName").val()!= null){
		accountSubData.payeeNames= $("#accountAimPeopleName").val().toString();
	}else{
		accountSubData.payeeNames= "";
	}
	
	accountSubData.summary= $("#accountSummary").val();
	
	if($("#accountMinMoney").val()-0 > $("#accountMaxMoney").val()-0){
		
		showErrorMessage("最大金额不能小于最小金额");
		return;
	}
	accountSubData.minMoney= $("#accountMinMoney").val();
	accountSubData.maxMoney= $("#accountMaxMoney").val();
	
	$("#hasAccountQueryWin").jqxWindow("close");
	$("#accountSubjectTable").jqxGrid("updatebounddata");
}

//清空高级查询条件
function clearHasAccountPayment(){
	 $("#accountFinanceSubjIds").jqxDropDownButton('setContent', '');
	 $('#accountFinanceSubjIds').jqxTree('uncheckAll');
	 $('.selectpicker').selectpicker('deselectAll');
	 
   
	 $("#accountSummary").val("");
	 $("#accountMinMoney").val("");
	 $("#accountMaxMoney").val("");
	/* accountSubData= {};*/

}

//关闭高级查询窗口
function closeHasAccountPayment(){
	$("#hasAccountQueryWin").jqxWindow("close");
}


//导出带有会计科目的付款单
function exportAccountSubjList(){
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
	$.ajax({
		url: '/paymentManager/exportWithAccSubj',
		type: 'post',
		data: accountSubData,
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
	            form.submit();
			}
	});
}

//下载导入模板
function downloadImportTemplate() {
	window.location.href = basePath + "/template/import/财务流水账导入模板.xls";
}

//删除收款单
function deleteModifyColletion(){
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	//取出收款单id
	var collectionId = _frame.find("#collectionId").val();
	 popupPromptBox("提示", "是否要删除收款单？", function() {
		 $.ajax({
				url: '/getCostManager/deleteCollectionInfo',
				type: 'post',
				data: {collectionId: collectionId},
				datatype: 'json',
				success: function(response){
					if (response.success) {
						showSuccessMessage(response.message);
						$("#modifyReceiptWin").jqxWindow("close");
						grid.setFilter(filter);
						getCurrencyList();
					}else {
						showErrorMessage(response.message);
					}
				}
			});
	 });
}

//账务详情表格行点击事件
function toggleClick(own) {
	grid.unSelectAll();
	$(own).toggleClass("selected");	
}

//显示合同详细信息
function showContractDetail(contractType, contractId) {
	if (contractType == 1) {
		$("#contractDetailIframe").attr("src", "/contractWorker/toContractWorkerDetailPage?readonly=true&contractId=" + contractId);
	}
	if (contractType == 2) {
		$("#contractDetailIframe").attr("src", "/contractActor/toContractActorDetailPage?readonly=true&contractId=" + contractId);
	}
	if (contractType == 3) {
		$("#contractDetailIframe").attr("src", "/contractProduce/toContractProduceDetailPage?readonly=true&contractId=" + contractId);
	}
	
	$("#contractDetailWin").jqxWindow("open");
}
//附件上传
var payUploader;
function initUploader(){
	payUploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/getCostManager/upoloadCostAttachment',
		pick : '#uploadFileBtn',
		timeout: 30*60*1000,//超时
		threads: 5,
		thumb: {
	    	   width: 200,
	    	   height: 200,
	    	   crop: false
	       },
	    method:'POST',
	});
	
	
	// 当有文件添加进来的时候
	payUploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var fileUl = $("#showAttachmentFile");
		var $li = $("<li class='upload-file-list-li'></li>");
		payUploader.makeThumb( file, function( error, ret ) {
			var suffix = file.ext.toLowerCase();
		
			if (suffix == "doc" || suffix == "docx") {
				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFile").append($li);
			} else if (suffix == "pdf"){
				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFile").append($li);
			} else if(suffix == "xls" || suffix == "xlsx"){
				$li.append("<img alt='' src= '../images/excel.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFile").append($li);
			} else if (error){
	            $li.html("预览错误");
	            $("#showAttachmentFile").append($li);
	        } else {
	        	$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' /><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#showAttachmentFile").append($li);
	        }
	    });
	});
	
	// 当有文件添加进来的时候
	payUploader.on('beforeFileQueued', function(file) {
		var ext = file.ext.toLowerCase();
		var type = file.type;
		if (ext != "doc" && ext != "docx" && ext != "pdf" && ext != "xls" && ext != "xlsx" && type.indexOf("image") == -1) {
			return false;
		}
		return true;
	});
	
	payUploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	
	payUploader.on('uploadFinished', function(file) {
		$('#myLoader').dimmer("hide");
		//不清楚statusFlag的作用
		showSuccessMessage("操作成功");
		$("#showAttachmentFile").empty();
		$("#modifyReceiptWin").jqxWindow("close");
		//grid.setFilter(filter);
		getCurrencyList();
	});
}

//删除付款单未上传的文件附件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	payUploader.removeFile(fileId, true);
	own.parent("li").remove();
}

//加载已上传的附件信息
function initUploadFilesInfo(attachmentList){
	//上传合同附件信息
	if(attachmentList !=null && attachmentList.length != 0){
		var html = [];
		for(var i= 0; i< attachmentList.length; i++){
			var attachment = attachmentList[i];
			var suffix =attachment.suffix.toLowerCase();
			if(suffix == ".doc" || suffix == ".docx"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/word.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else if(suffix == ".pdf"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/pdf.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else if(suffix == ".xls" || suffix == ".xlsx"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/excel.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else{
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\")' sv = '"+ attachment.id +"'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
		
		}
		html.join("");
		$("#showAttachmentFile").empty();
		$("#showAttachmentFile").append(html);
		
	}else{
		$("#showAttachmentFile").empty();
	}
}


//删除上传成功的文件附件
function deleteUploadedFile(ev,own,attId){
	
	own= $(own);
	popupPromptBox("提示","是否删除该附件？", function () {
		$.ajax({
	    	url:'/attachmentManager/deleteAttachment',
	    	type:'post',
	    	dataType:'json',
	    	data:{attachmentId: attId},
	    	success:function(data){
	    		if(data.success){
	    			own.parent("li").remove();
	    			showSuccessMessage("删除成功！");
	    		}else{
	    			showErrorMessage('删除附件失败');
	    		}
	    		
	    	}   	
		});
    });
	ev.stopPropagation();
}


//预览附件
function previewAtts(attpackId, type, hdStorePath) {
	if (type == 2) {
		window.open("/attachmentManager/toPreviewPage?attpackId='" + attpackId + "'&type=" + type);
	} else {
		window.open("/fileManager/previewAttachment?address=" + hdStorePath);
		return;
	}

}


//下载附件
function downLoadFile(own, ev){
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
    var address = "";
    var fileName = "";
    var attachmentId = $(own).parents("li").attr("sv");
    $.ajax({
		url: '/attachmentManager/queryAttachmentById',
		async: false,
		type: 'post',
		data: {"attachmentId": attachmentId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				_LoadingHtml.hide();
	            $(".opacityAll").hide();
				/*var form = $("<form></form>");
	            form.attr("action","/fileManager/downloadFileByAddr");
	            form.attr("method","post");
	            form.append("<input type='hidden' name='address'>");
	            form.find("input[name='address']").val();
	            $("body").append(form);
	            form.submit();
	            
	            form.remove();*/
	            address = response.downLoadAddress;
	            fileName = response.fileName;
			}else{
				showErrorMessage(response.message);
			}
		}
	});
    window.location.href="/fileManager/downloadFileByAddr?address="+address + "&fileName="+fileName;
    ev.stopPropagation();

}

//排序方式
function sortFom(own, sortType){
	if(sortType == 0){
		$(".select-sort").removeClass("select-sort");
		$(own).addClass("select-sort");
		$("#sortOfType").val(0);
		grid.loadTable();
	}else{
		$(".select-sort").removeClass("select-sort");
		$(own).addClass("select-sort");
		$("#sortOfType").val(1);
		grid.loadTable();
	}
}
