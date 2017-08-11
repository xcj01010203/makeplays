var filter = {};
filter.listType = 3;//待我审批(默认)
$(function(){
	//初始化高级查询窗口
	initAdvanceWin();
	//初始化待我审批列表
	initApprovalList();
	window.onresize = function(){
		var width = $("#mainContainer").width();
		var height = $("#mainContainer").height();
		$("#approvalList").jqxGrid({height: height});
		$("#approvalList").jqxGrid({width: width});
	};
});





//初始化待我审批列表
function initApprovalList(){
	var source = {
			url: '/receiptInfoManager/queryReceiptList',
			type: "post",
			dataType : "json",
	        data:filter,
	        root:'receiptList',
			datafields : [
			     {name: "id", type: "string"},
			     {name: "type", type: "int"},
			     {name: "receiptNo", type: "string"},
			     {name: "applyerName", type: "string"},
			     {name: "receiptDate", type: "string"},
			     {name: "money", type: "double"},
			     {name: "currencyName", type: "string"},
			     {name: "currencyCode", type: "string"},
			     {name: "description", type: "string"},
			     {name: "status", type: "int"},
			     {name: "approverList", type: "array"}
			],
	        beforeprocessing: function(data){
	        	source.totalrecords = data.total;
	        }
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	var receiptNoRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		html.push('<div class="jqx-column align-left">');
		if(rowdata.receiptNo == undefined || rowdata.receiptNo == null || rowdata.receiptNo == ""){
			rowdata.receiptNo = "";
		}
		html.push("<a href='javascript:approvalDetailInfo(\""+row+"\")'>" + rowdata.receiptNo + "</a>");
		html.push('</div>');
		return html.join("");
	};
	var typeRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		
		if(rowdata.type == 1){
			html.push('<div class="jqx-column align-left">借款</div>');
		}else if(rowdata.type == 2){
			html.push('<div class="jqx-column align-left">报销</div>');
		}else {
			html.push('<div class="jqx-column align-left">预算</div>');
		}
		return html.join("");
	};
	var descriptionRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		return '<div class="jqx-column align-left" title="' + value + '">' + value + '</div>';
	};
    var approverListRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		var approverList = rowdata.approverList;
		var title=[];
		if(rowdata.status == 1){
			html.push('<span class="font-gray">草稿</span>');
			title.push('草稿');
		}else if(rowdata.status == 2){
			html.push('<span class="font-gray">审批中</span>');
			title.push('审批中');
		}else if(rowdata.status == 3){
			html.push('<span class="font-red">被拒绝</span>');
			title.push('被拒绝');
		}else{
			html.push('<span class="font-green">完结</span>');
			title.push('完结');
		}
		if(approverList && approverList.length!=0) {
			html.push('(');
			title.push('(');
			for(var i= 0; i<approverList.length; i++){
				if(i != 0){
					html.push('-');
					title.push('-');
				}
				if(approverList[i].resultType == 1){
					html.push('<span style="color: #333;">' + approverList[i].userName + '</span>');
				}else if(approverList[i].resultType == 2){
					html.push('<span style="color: red;">' + approverList[i].userName + '</span>');
				}else if(approverList[i].resultType == 3){
					html.push('<span style="color: green;">' + approverList[i].userName + '</span>');
				}else{
					html.push('<span style="color: #8c8c8c;">' + approverList[i].userName + '</span>');
				}
				title.push(approverList[i].userName);
			}
			html.push(')');
			title.push(')');
		}
		return '<div class="jqx-column align-left" title="' + title.join("") + '">' + html.join("") + '</div>';
	};
	var rendergridrows = function (params) {
        return params.data;
	};
	$("#approvalList").remove();
	$("#mainContainer").append('<div id="approvalList"></div>');
	$("#approvalList").jqxGrid({
		width: "100%",
		height: "100%",
		source: dataAdapter,
		pagesize: 20,
        pageable: true,
        pagesizeoptions: ['20', '50', '100'],
		localization: localizationobj,
		rowsheight: 35,
		columnsheight: 35,
		showToolbar: true,
		toolbarHeight: 35,
		rendergridrows: rendergridrows,
		virtualmode: true,
		rendertoolbar: function (toolbar) {
			var container = [];
			container.push("<div class='toolbar'>");
			container.push('<input type="button" class="search-btn" onclick="openAdvanceSearch()">');
			if((filter.listType == 1) && (!isApprovalReadonly)){
				container.push("<div id='approvalBtn' class='approval-btn' title='添加申请'>");
				container.push("<div class='jqx-tree-drop' id='jqxTreeDrop'>");
				container.push("<ul style='display:block;'>");
				container.push("<li  value='1' onclick='addReceipt(1)'>借款</li>");
				container.push("<li  value='2' onclick='addReceipt(2)'>报销</li>");
				container.push("<li  value='3' onclick='addReceipt(3)'>预算</li>");
				container.push("</ul>");
				container.push('</div>');
				container.push('</div)');
			}
			container.push('</div');
			toolbar.empty();
			toolbar.append(container.join(""));
			if((filter.listType == 1) && (!isApprovalReadonly)){
				//初始化发起申请下拉按钮
				$("#approvalBtn").jqxDropDownButton({theme:theme, height: 24, width: 24});
				//改动插件
				$("#dropDownButtonArrowapprovalBtn").jqxDropDownButton({ width: '24px'});
				$("#jqxTreeDrop").jqxTree({theme:theme, width: 100});
			}
        },
		columns: [
			{text: "单号", datafield: 'receiptNo', cellsrenderer: receiptNoRenderer, width: '10%',  align: 'center', sortable: false},
			{text: "发起人", datafield: 'applyerName', width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "类型", datafield: 'type', cellsrenderer: typeRenderer,  width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "金额",  datafield: 'money',  width: '10%',  align: 'center', cellsAlign:'right', sortable: false},
			{text: "说明", datafield: 'description', cellsrenderer: descriptionRenderer, width: '20%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "发起时间", datafield: 'receiptDate', width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "审批状态", datafield: 'approverList', cellsrenderer: approverListRenderer, width: '30%',  align: 'center', sortable: false}
		]
	});
}


//初始化高级查询窗口
function initAdvanceWin(){
	$("#advanceSearch").jqxWindow({
		theme: theme,
		width: 420,
		height: 460,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			
		}
	});
}

//打开高级查询窗口
function openAdvanceSearch(){
	$("#advanceSearch").jqxWindow("open");
}

//关闭高级查询窗口
function cancelSearch(){
	$("#advanceSearch").jqxWindow("close");
}

//校验是否是非数字
function checkNum(own){
	$(own).val($(own).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$(own).val($(own).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$(own).val($(own).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$(own).val($(own).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}


//点击待我审批
function waitingApproval(own){
	$(own).addClass("tab_li_current");
	$(own).siblings("li").removeClass("tab_li_current");
	$("#mainContainer").show();
	$("#listTypeFlag").val(3);
	filter = {};
	filter.listType = 3;
	initApprovalList();
	//清空高级查询的内容
	clearQueryContent();
}
//点击我已审批
function alreadyApproval(own){
	$(own).addClass("tab_li_current");
	$(own).siblings("li").removeClass("tab_li_current");
	$("#listTypeFlag").val(2);
	filter = {};
	filter.listType = 2;
	initApprovalList();
	//清空高级查询的内容
	clearQueryContent();
}
//点击我的申请
function myApproval(own){
	$(own).addClass("tab_li_current");
	$(own).siblings("li").removeClass("tab_li_current");
	$("#listTypeFlag").val(1);
	filter = {};
	filter.listType = 1;
	initApprovalList();
	//清空高级查询的内容
	clearQueryContent();
}

//清空高级查询的内容
function clearQueryContent(){
	$("input[name=receiptType]:checked").attr("checked", false);
	$("#receiptNoInput").val("");
	$("#applyerName").val("");
	$("#minMoney").val("");
	$("#maxMoney").val("");
	$("#minDate").val("");
	$("#maxDate").val("");
	$("#description").val("");
}



//添加单据
function addReceipt(type){
	$("#approvalBtn").jqxDropDownButton("close");
	$("#rightPopupWin").animate({"right": 0}, 200).show();
	$("#approvalIframe").attr("src", "/receiptInfoManager/toApprovalDetailPage?listType=1&&receiptType="+type);
}



//跳转到审批详细信息页面
function approvalDetailInfo(editrow){
	var dataRecord = $("#approvalList").jqxGrid('getrowdata', editrow);
	var typeFlag;
	if(filter.listType == 1){
		typeFlag = 1;
	}else if(filter.listType == 2){
		typeFlag = 2;
	}else{
		typeFlag = 3;
	}
	$("#rightPopupWin").animate({"right": 0}, 200).show();
	$("#approvalIframe").attr("src", "/receiptInfoManager/toApprovalDetailPage?listType="+ typeFlag +"&&receiptType="+dataRecord.type + "&&receiptId=" + dataRecord.id + "&&receiptStatus=" + dataRecord.status);
}

//关闭审请详细信息
function closeRightPopWin(){
	var width = $("#rightPopupWin").width();
	$("#rightPopupWin").animate({"right": 0-width}, 200);
	timer = setTimeout(function(){
		$("#rightPopupWin").hide();
	}, 200);
}
//刷新列表
function refreshList(type){
	if(type == 1){
		filter.listType = 1;//我的申请
	}else if(type == 2 ){
		filter.listType = 2;//我己审批
	}else{
		filter.listType = 3;//待我审批
	}
	initApprovalList();
}

//第一次保存或提交成功弹窗-需显示单据编号
function showSuccessInfo(message){
	alert("", message, function () {
        //after click the confirm button, will run this callback function
    }, {type: 'success', confirmButtonText: '确定'});
}


//高级搜索
function confirmSearch(){
    filter.receiptType = $("input[name=receiptType]:checked").val();
	filter.receiptNo = $("#receiptNoInput").val();
	filter.applyerName = $("#applyerName").val();
	filter.minMoney = $("#minMoney").val();
	filter.maxMoney = $("#maxMoney").val();
	filter.startDate = $("#minDate").val();
	filter.endDate = $("#maxDate").val();
	filter.description = $("#description").val();
	if($("#listTypeFlag").val() == 1){
		filter.listType = 1;
		initApprovalList();
	}else if($("#listTypeFlag").val() == 2){
		filter.listType = 2;//我已审批
		initApprovalList();
	}else{
		filter.listType = 3;//待我审批
		initApprovalList();
	}
	cancelSearch();
}