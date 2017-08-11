var singleCurrencyFlag = false;

$(document).ready(function(){
	//判断是否生成财务科目
	$.ajax({
		url: '/financeSubject/nopassword/hasFinanceSubject',
		type: 'post',
		datatype: 'json',
		async: false,
		success: function(response){
			
			//没有生成财务科目
			if(!response.hasSubject){
				window.location = "/financeSubject/toFinanceBudgetPage?pageType=2";
			}else{
				initWindow();
			}
		}
	});
	/*//检查该剧组是否只有一个币种
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
	
	
	
	
	//校验是否需要财务密码
	checkNeedFinancePwd();
	//获取可用币种
	getEnableMoney();
	//显示费用预算表
	productFinaceBudgetList();
	//添加财务科目窗口
	initFinanceSubject();
	//排序窗口
	initSetSortWin();
	//导入窗口
	initImportWin();
	
	//制度权限，财务科目只能查看，不能修改
	if(isBudgetReadonly){
		$("#subjectwin").find("input[type='text']").attr('disabled',true);
		$("#subjectwin").find('select').attr('disabled',true);
		$("#subjectwin").find('textarea').attr('disabled',true);
		$("#subject-sure").remove();
	}*/
});

function initWindow(){
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
	//校验是否需要财务密码
	checkNeedFinancePwd();
	//获取可用币种
	getEnableMoney();
	//显示费用预算表
	productFinaceBudgetList();
	//添加财务科目窗口
	initFinanceSubject();
	//排序窗口
	initSetSortWin();
	//导入窗口
	initImportWin();
	
	//制度权限，财务科目只能查看，不能修改
	if(isBudgetReadonly){
		$("#subjectwin").find("input[type='text']").attr('disabled',true);
		$("#subjectwin").find('select').attr('disabled',true);
		$("#subjectwin").find('textarea').attr('disabled',true);
		$("#subject-sure").remove();
	}
}
//定义表格的高度
var clientHeight=window.screen.availHeight-125;

/*//初始化财务密码窗口
function initFinancePwdWin() {
	$("#financePwdWindow").jqxWindow({
		theme: theme,
		height: 250,
		width: 400,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			
		}
	});
}

//校验是否需要财务密码
function checkNeedFinancePwd() {
	$.ajax({
		url: "/financeSettingManager/nopassword/checkPasswordHasSetted",
		type: "post",
		data: {},
		dataType: "json",
		async: false,
		success: function(response) {
			if (response.needPwd) {
				$("#financePwdWindow").jqxWindow("open");
			}
		}
	});
}

//校验财务密码
function checkPassword(own) {
	var password = $("#financePwd").val();
	if (password == null || password == "") {
		showErrorMessage("请填写财务密码");
		return false;
	}
	
	$.ajax({
		url: "/financeSettingManager/nopassword/checkPasswordCorrect",
		type: "post",
		data: {password: password},
		dataType: "json",
		async: false,
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
			} else {
				$("#financePwdWindow").jqxWindow("close");
				window.location.reload();
			}
		}
	});
}

//关闭财务密码窗口
function closePwdWindow() {
	$("#financePwdWindow").jqxWindow("close");
}*/



function initImportWin() {
	$("#importBudgetWin").jqxWindow({
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

function showImportWin() {
	$.ajax({
		url: '/financeSubject/queryForReadImport',
		type: 'post',
		datatype: 'json',
		async: false,
		success: function(response){
			var title = response.title;
			var flag = false;
			if(title){
				flag = true;
			}
			
			if(flag){
				swal({
				    title: "提示",
			        text: '已有'+title+'关联到当前预算财务科目，执行导入操作将导致相关数据无法关联到财务科目，是否执行导入操作？',
			        type: "warning",
			        showCancelButton: true,  
			        confirmButtonColor: "rgba(255,103,2,1)",
			        confirmButtonText: "确定",   
			        cancelButtonText: "取消",   
			        closeOnConfirm: true,   
			        closeOnCancel: true
				},function (isConfirm){
					if (isConfirm){
						$("#importBudgetWin").jqxWindow("open");
						$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/financeSubject/importBudget&&needIsCover=false&&refreshUrl=/financeSubject/toFinanceBudgetPage&&templateUrl=/template/import/财务预算表导入模板.xls");
					}
				});
			}else{
					$("#importBudgetWin").jqxWindow("open");
					$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/financeSubject/importBudget&&needIsCover=false&&refreshUrl=/financeSubject/toFinanceBudgetPage&&templateUrl=/template/import/财务预算表导入模板.xls");
			}
		}
	});
}
//关闭导入窗口
function closeImportWin(){
	$("#importBudgetWin").jqxWindow("close");
}


//生成费用预算表
function productFinaceBudgetList(){
	//定义所需的货币信息的变量
	var currency;
	var totalMoney;
	//先获取带有总金额的货币列表
	$.ajax({
		url: '/currencyManager/queryCurrencyListWithBudget',
		type: 'post',
		data: {ifEnable : true},
		datatype: 'json',
		async: false,
		success: function(response){
			//货币列表
			currency = response.currencyInfoList;
			totalMoney = response.totalMoney;
		}
	});
	
	//jqx加载数据
	var source = {
        url: '/budgetManager/queryBudgetList',
        type: 'post',
        datatype: 'json',
        dataFields: [
            {name: 'financeSubjId', type: 'string'},
            {name: 'financeSubjName', type: 'string'},
            {name: 'financeSubjParentId', type: 'string'},
            {name: 'remark', type: 'string'},
            {name: 'hasChildren', type: 'boolean'},
            {name: 'currencyId', type: 'string'},
            {name: 'myLevel', type: 'int'},
            {name: 'sequence', type: 'int'},
            {name: 'amount', type: 'double'},
            {name: 'unitType', type: 'String'},
            {name: 'perPrice', type: 'double'},
            {name: 'money', type: 'double'},
            {name: 'budgetRate', type: 'double'}
        ],
        hierarchy: {
			keyDataField: {name:'financeSubjId'},
			parentDataField: {name:'financeSubjParentId'}
		},
		id: 'financeSubjId'
	};
	

	//设置表格的预算科目
	for(var j = 0; j < currency.length; j++){
		var budgetInformation = {name: currency[j].id, type: 'string'};
		source.dataFields.push(budgetInformation);
	}
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	//科目名称
	var financialSubjectsRender = function(row, column, value, rowData) {
		if(rowData.hasChildren){
		}
		var html = [];
		html.push("<span class='financial-subjects-column'>");
		html.push("<a style='color: #f88400;' href='javascript:editFinanceSubject(\""+row+"\")'>" + rowData.financeSubjName + "</a>");
		if(!isBudgetReadonly){
			html.push("<span class='operate-btn-list'>");
			html.push("    <a class='float-right delete-finance-subject' title='删除' href='javascript:deleteFinanceSubject(\""+row+"\")'></a>");
			/*html.push("    <a class='float-right edit-finance-subject' title='编辑' href='javascript:editFinanceSubject(\""+row+"\")'></a>");*/
			html.push("    <a class='float-right add-finance-subject' title='添加' href='javascript:addFinanceSubject(\""+row+"\")'></a>");
			html.push("    <a class='float-right move-to-level' title='移动到上一级' href='javascript:moveHigerLevel(\""+row+"\")'></a>");
			html.push("	</span>");
		}
		html.push("</span>");
		return html.join("");
	
	};	
	var financeSubjClassName = function (row, column, value, data) {
		if(data.hasChildren){
			return "background";
		}
	};
	//数量
	var amountClassName = function(row, column, value, data) {
		if(data.hasChildren){
			return "background";
		}
	};
	//单位
	var unitClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	//单价
	var perClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	
	
	//费用预算的值
	var budgetDetails = function(row, column, value, rowData){
		return "<span class='budget-details-money'>" + value + "</span>";
	};
	var budgetClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	//总预算的值
	var budgetCountDetails = function(row, column, value, rowData){
		return "<span class='budget-details-money'>" + value + "</span>";
	};
	var budgetCountClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	//预算比例的值
	var budgetRateDetails = function(row, column, value, rowData){
		return "<span class='budget-details-money'>" + value + "</span>";
	};
	var budgetRateClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	//备注
	var remarkClassName = function(row, column, value, data){
		if(data.hasChildren){
			return "background";
		}
	};
	
	//列信息
	var columnInformation = [
	             		    {text: "财务科目", datafield: 'financeSubjName', cellclassname:financeSubjClassName, cellsrenderer: financialSubjectsRender, width: '20%', cellsAlign: 'left', align: 'center', sortable: false},
	            		    {text: "数量", datafield: 'amount', cellclassname:amountClassName, width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
	            		    {text: "单位", datafield: 'unitType', cellclassname:unitClassName, width: '10%', cellsAlign: 'left', align: 'center', sortable: false},
	            		    {text: "单价", datafield: 'perPrice', cellclassname:perClassName, width: '10%', cellsAlign: 'right', align: 'center', sortable: false} 		    
	            		];
	
	for(var k = 0; k < currency.length; k++){
		var columnBudget = {text: "预算(" + currency[k].code + ")<span class='currency-money'>" + currency[k].money + "</span>", datafield: currency[k].id, cellclassname:budgetClassName, cellsrenderer: budgetDetails,  width: '15%', cellsAlign: 'right', align: 'left', sortable: false};
		if (singleCurrencyFlag) {
			columnBudget = {text: "预算<span class='currency-money'>" + currency[k].money + "</span>", datafield: currency[k].id, cellclassname:budgetClassName, cellsrenderer: budgetDetails,  width: '15%', cellsAlign: 'right', align: 'left', sortable: false};
		}
		
		columnInformation.push(columnBudget);
	}

//	columnInformation.push({text: "总预算<span class='currency-money'>" + totalMoney + "</span>", datafield: 'standardMoney', cellclassname:budgetCountClassName, cellsrenderer: budgetCountDetails, width: '15%', cellsAlign: 'right', align: 'left', sortable: false});
	
	columnInformation.push({text: "预算比例", datafield: 'budgetRate', cellclassname:budgetRateClassName, cellsrenderer: budgetRateDetails, width: '15%', cellsAlign: 'right', align: 'left', sortable: false});
	columnInformation.push({text: "备注", datafield: 'remark', cellclassname:remarkClassName, width: '20%', cellsAlign: 'left', align: 'center', sortable: false});
	//防止重新加载按钮
	/*var publicShowToolbar = 0;*/
	
	
	
	//加载费用预算表
	$("#finaceBudgetList").jqxTreeGrid({
		width: 'calc(100%)',
		height: 'calc(100% - 5px)',
		source: dataAdapter,
		showToolbar: true,
		columnsResize: true,
		toolbarHeight: 35,
		localization: localizationobj,
		rendertoolbar: function(toolbar) {
			/*if(publicShowToolbar ==  0){*/
				var container = [];
				container.push("<div class='toolbar'>");
				container.push("<input type='button' class='pack-up-subject' id='packUpBtn' onclick='packUpSubjects(event)'>");
				container.push("<input type='button' class='pack-down-subject' id='packDownBtn' onclick='packDownSubjects(event)'>");
				if(hasExportFinanceBudgetAuth) {
					container.push("<input type='button' class='export-btn' id='exportBtn'>");
				}
				if(hasImportFinanceBudgetAuth && !isBudgetReadonly) {
					container.push("<input type='button' class='import-btn' id='importBtn' onclick='showImportWin()'>");
				}				
				if(!isBudgetReadonly){
					container.push("<input type='button' class='re-budget-template' id='reSetTemplateBtn' onclick='reSetTemplate()'>");
					container.push("<input type='button' class='set-sort' id='setSortBtn' onclick='setSortWin()'>");
				}
				
				container.push("<input type='button' class='cost-control-list' id='costControlBtn' style='display:none;' onclick='costControlInfo()'>");
				container.push("<input type='button' class='accountant-course' id='accountantBtn' onclick='accountSubjectInfo()'>");
				
				container.push("</div>");
		    
				toolbar.append($(container.join("")));
			/*}*/
			/*publicShowToolbar++;*/
			
			$("#packUpBtn").jqxTooltip({content: "展开所有科目", position: "bottom"});
			$("#packDownBtn").jqxTooltip({content: "收起所有科目", position: "bottom"});
			if(hasExportFinanceBudgetAuth) {
				$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
			}
			if(!isBudgetReadonly){
				$("#reSetTemplateBtn").jqxTooltip({content: "重新设置预算模板", position: "bottom"});
				$("#setSortBtn").jqxTooltip({content: "排序", position: "bottom"});
			}
			
			$("#costControlBtn").jqxTooltip({content: "费用管控表", position: "bottom"});
			$("#accountantBtn").jqxTooltip({content: "会计科目", position: "bottom"});
			if(hasImportFinanceBudgetAuth && !isBudgetReadonly) {
				$("#importBtn").jqxTooltip({content: "导入", position: "bottom"});
			}
			
			
			//定义导出数据变量
			var exportData;
			
			//定义下载文件的地址
			var fileAddress;
			
			/**
			 * 获取导出数据的递归函数
			 */
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
			
			
			//导出财务预算表
			$("#exportBtn").on("click", function(){
				
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
				
				
				var rows = $("#finaceBudgetList").jqxTreeGrid('getRows');
         	    var columnNames = new Array();
         	    for(var i = 0; i < columnInformation.length; i++){
        		   columnNames.push({dataField : columnInformation[i].datafield, text : removeHTMLTag(columnInformation[i].text)});
         	    }
         	    
        		exportData=new Array();
        		   
        		addRecord(rows, columnNames);
            	
                $.ajax({
                	url: '/commonExportExcel/exportExcel',
                	type: 'post',
                	data: {"rows" : JSON.stringify(exportData), "columns" : JSON.stringify(columnNames), "fileName" : "财务预算表"},
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
		columns: columnInformation,
		rendered: function() {
			$("#finaceBudgetList table tr").bind("mouseover",function(event){
			 	$(this).find(".operate-btn-list").show();
			}).bind("mouseout",function(){
		    	$(this).find(".operate-btn-list").hide();
			});
			
			
			
			
		},
		ready: function(){
			
		}
			
	});
	
	$('#finaceBudgetList').on('bindingComplete', function (event){
		packUpSubjects();
	});

}


//展开所有科目
function packUpSubjects(event){
	$("#finaceBudgetList").jqxTreeGrid("expandAll");
	$("#packUpBtn").hide();
	$("#packDownBtn").show();
	$("#exportBtn").addClass("export-btn-margin");
}
//收起所有科目
function packDownSubjects(evnet){
	$("#finaceBudgetList").jqxTreeGrid("collapseAll");
	$("#packDownBtn").hide();
	$("#packUpBtn").show();
	$("#exportBtn").removeClass("export-btn-margin");
}



//显示调整预算顺序表窗口
function setSortWin(){
	$("#sortWindow").jqxWindow("open");
}
//初始化调整预算顺序窗口
function initSetSortWin(){
	$("#sortWindow").jqxWindow({
		theme: theme,
		width: '450',
		height: '600',
		modalZIndex: '500',
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			showFinanceSortGrid();
			
			
		}
	});
	//窗口的关闭事件
	$("#sortWindow").on("close", function(){
		window.location.reload();
	});	
	
}


//调整预算顺序表
function showFinanceSortGrid(){
	$("#sortGrid").treegrid({
		width: "440",
        height: "480",
        loadMsg: "正在加载财务科目..",
        animate: true,
        url: "/financeSubject/querySubjectListWithTreeFormat",
        method: 'post',
        idField: 'financeSubjId',
        treeField: 'financeSubjName',
        autoRowHeight: true,
        dnd:true,
        columns: [[
            {title:'财务科目', field:'financeSubjName', width:420}
        ]],
        onLoadSuccess: function(row){
        	 $(this).treegrid('enableDnd', row?row.financeSubjId:null); 
        } ,
        onDragOver:function(targetRow, sourceRow){
     	   if(targetRow._parentId!=sourceRow._parentId){
           		return false;
           	}
        },
        onBeforeDrag:function(Row){},
        onBeforeDrop: function(targetRow, sourceRow, point){
        	if(targetRow._parentId!=sourceRow._parentId){
           		return false;
           	}
           	if(point == 'append'){
           		return false;
           	}
        	
        },
        onStopDrag: function(row){
        	
        },
        onClickRow:function(row){
        	
        },
        onDrop:function(targetRow, sourceRow, point){
        	if(targetRow._parentId!=sourceRow._parentId){
          		return false;
          	}
          	if(point == 'append'){
          		showInfoMessage("拖动只能在同级之间进行！");
          		return false;
          	}
    	    var parent = $('tr[node-id='+sourceRow.financeSubjId+']').parent();
          	var child = [];
          	parent.children('tr').each(function(i,n){
          		var nodeid = $(this).attr('node-id');
          		if(nodeid != undefined){
          			child.push(nodeid);
          		}
          	});
           	
           	$.ajax({
           		url: '/financeSubject/updateSubjectSequence',
           		data: {ids:child.join(',')},
           		dataType: 'json',
           		type: 'post',
           		success: function(response){
           			if(response.success){
           				showSuccessMessage("更新预算表顺序成功！");
           			}else{
           				showErrorMessage(response.message);
           			}
           		}
           	});
        }
	});
}

//确定按钮的事件
function closeSortListWin(){
	$("#sortWindow").jqxWindow("close");
}




//重新设置预算模板
function reSetTemplate(){
	$.ajax({
		url: '/budgetManager/checkIsClean',
		type: 'post',
		async: 'false',
		datatype: 'json',
		success: function(response){
			if(response.isClean){
				
				popupPromptBox("提示", "是否重新选择预算表，已有的科目预算将会丢失！", function(){
					$.ajax({
						url: '/budgetManager/deleteAllBudget',
						type: 'post',
						datatype: 'json',
						success: function(response){
							if(response.success){
								window.location = "/financeSubject/toFinanceBudgetPage?pageType=2";
							}else{
								showErrorMessage(response.message);
							}
						}
					});
				   
				});
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}





//移动到上一级
function moveHigerLevel(editrow){
	var dataRecord = $("#finaceBudgetList").jqxTreeGrid('getRow', editrow);
	popupPromptBox("提示", "是否确定&nbsp;&nbsp;" + dataRecord.financeSubjName + "&nbsp;&nbsp;移动到上一级？", function(){
		$.ajax({
			url: '/financeSubject/upSubjectLevel',
			type: 'post',
			data: {"financeSubjId" : dataRecord.financeSubjId, "financeSubjParentId" : dataRecord.financeSubjParentId, "level" : dataRecord.myLevel},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
//					$("#finaceBudgetList").jqxTreeGrid('updateBoundData');
//					productFinaceBudgetList();
					window.location.reload();
					//packUpSubjects();
				}else{
					showErrorMessage(response.message);
					packUpSubjects();
				}
			}
		});
	});
	
}

//获取可用币种
function getEnableMoney(){
	//加载币种
	 
		$.ajax({
			url: '/currencyManager/queryCurrencyList',
			type: 'post',
			data: {ifEnable : true},
			datatype: 'json',
			async: false,
			success: function(response){
				if(response.success){
					var currency = response.currencyInfoList;
					var container = [];
					 for(var i = 0; i < currency.length; i++){
						 if(i==0){
							 container.push("<option value='" + currency[i].id +"' checked>" + currency[i].name + "</option>");
						 }else{
							 container.push("<option value='" + currency[i].id +"'>" + currency[i].name + "</option>");
						 }
						 
					 }
					 $("#currentId").append($(container.join("")));
				}else{
					//showErrorMessage(response.message);
				}
				
			}
		});
	 
}




//添加财务科目窗口
function addFinanceSubject(editrow){
	$('#subjectwin').jqxWindow('setTitle', '新建科目');
	
	var dataRecord = $("#finaceBudgetList").jqxTreeGrid('getRow', editrow);	
	
	var countMoney = 0;
	//初始化值
	$("#financeSubjName").removeClass("error-finance-tips");
	$(".finance-subject-tips").css("display", "none");
	
	$("li.li-amount-unit").css("display", "block");
	$("li.li-subject-price").css("display", "block");
	$("li.li-finance-money").css("display", "block");
	
	$("#financeSubjName").val("");
	$("#perPrice").val("");
	$("#amount").val("");
	$("#unitType").val("");
	$("#currentId option:first").prop("selected", 'selected');
	$("#money").val(countMoney.toFixed(2));
	$("#remark").val("");
	$("#financeSubjId").val("");
	$("#financeSubjParentId").val(dataRecord.financeSubjId);
	$("#level").val(dataRecord.myLevel);
	
	$("#subjectwin").jqxWindow("open");
}
//修改财务科目窗口
function editFinanceSubject(editrow){
	//清空弹窗内容，再显示
	$("#financeSubjName").val("");
	$("#perPrice").val("");
	$("#amount").val("");
	$("#unitType").val("");
	$("#currentId option:first").prop("selected", 'selected');
	$("#money").val('0.00');
	$("#remark").val("");
	$("#financeSubjId").val("");
	
	$('#subjectwin').jqxWindow('setTitle', '修改科目');
	//清空样式
	$("#financeSubjName").removeClass("error-finance-tips");
	$(".finance-subject-tips").css("display", "none");
	var dataRecord = $("#finaceBudgetList").jqxTreeGrid('getRow', editrow);
	console.log(dataRecord);
	//判断是否有子节点
	
			if(dataRecord.hasChildren){//有子节点
				$("#financeSubjId").val(dataRecord.financeSubjId);
				$("#financeSubjName").val(dataRecord.financeSubjName);
				$("#financeSubjParentId").val(dataRecord.financeSubjParentId);
				$("#level").val(dataRecord.level);
				
				$("li.li-amount-unit").css("display", "none");
				$("li.li-subject-price").css("display", "none");
				$("li.li-finance-money").css("display", "none");
				$("#remark").val(dataRecord.remark);
				
				$("#subjectwin").jqxWindow("open");
				
			}else{
								
				$("li.li-amount-unit").css("display", "block");
				$("li.li-subject-price").css("display", "block");
				$("li.li-finance-money").css("display", "block");
				$("#subjectwin").jqxWindow("open");
				$("#financeSubjId").val(dataRecord.financeSubjId);
				$("#financeSubjName").val(dataRecord.financeSubjName);
				$("#financeSubjParentId").val(dataRecord.financeSubjParentId);
				$("#level").val(dataRecord.level);
				$("#amount").val(dataRecord.amount);
				if(dataRecord.unitType !== undefined){
					$("#unitType").val(dataRecord.unitType);
				}
				if(dataRecord.currencyId !== undefined){
					$("#currentId").val(dataRecord.currencyId);
				}else{
					$("#currentId option:first").prop("selected", 'selected');
				}
				
				$("#perPrice").val(dataRecord.perPrice);
				if(dataRecord.money == undefined){
					
					/*$("#money").val("0.00");*/
					$("#money").attr("placeholder","0.00");
				}else{
					$("#money").val(fmoney(dataRecord.money));
				} 
				
				$("#remark").val(dataRecord.remark);
			}
}




//初始化 添加/修改 财务科目窗口
function initFinanceSubject(){
	$("#subjectwin").jqxWindow({
		theme: theme,
		height: 420,
		width: 640,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			
			//自动计算值

			 $(".subject-amount").keyup(function(){    
				 /*$(this).val($(this).val().replace(/[^0-9.]/g,''));*/
				 $(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
				 $(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
				 $(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
				 $(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
				 if($(this).val() != "" && $("#amount").val() != ""){
					 var amount = $(this).val().replace(/,/g, "")-0;
				     var price = $("#perPrice").val().replace(/,/g, "")-0;
				     var result = multiply(amount, price);
				     $(".finance-money").val(fmoney(result));
				 }
		        
		    }).bind("paste",function(){  //CTR+V事件处理    
		   	 $(this).val($(this).val().replace(/[^0-9.]/g,''));     
		    }).css("ime-mode", "disabled"); //CSS设置输入法不可用    
			 //单价文本框
			 $(".subject-price").keyup(function(){    
		        /*$(this).val($(this).val().replace(/[^0-9.]/g,''));*/ 
				 $(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
				 $(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
				 $(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
				 $(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
				 if($(this).val() != "" && $("#perPrice").val() != ""){
					 var amount = $("#amount").val().replace(/,/g, "")-0;
				     var price = $(this).val().replace(/,/g, "")-0;
				     var result = multiply(amount, price);
				     $(".finance-money").val(fmoney(result));
				 }
		        
		    }).bind("paste",function(){  //CTR+V事件处理    
		        $(this).val($(this).val().replace(/[^0-9.]/g,''));     
		    }).css("ime-mode", "disabled"); //CSS设置输入法不可用  
			 
			 //绑定错误提示事件 
			 $("#financeSubjName").on("blur", function(){
				if($("#financeSubjName").val() == ""){
					$("#financeSubjName").addClass("error-finance-tips");
					$(".finance-subject-tips").css("display", "block");
				}
			 });
			 $("#financeSubjName").on("focus", function(){
				 $("#financeSubjName").removeClass("error-finance-tips");
				 $(".finance-subject-tips").css("display", "none");
			 });
             
			 $("#perPrice").on("focus", function(){
				if($(this).val() != ""){
					var value = $(this).val().replace(/,/g,"");
					$(this).val(value);
				} 
			 }).on("blur", function(){
				 
					 if($(this).val() != ""){
						 var value = $(this).val();
						 $(this).val(fmoney(value));
					 }
					
			 });
			 
			 //将下拉框初始化
			 $.ajax({
				 url: '/budgetManager/queryUnitTypeList',
				type: 'post',
				datatype: 'json',
				success: function(response){
					if(response.success){
						var unitTypeList = response.unitTypeList;
						var _this = $("#unitTypeMainDiv");
						var htmlArr = [];
						//拼接数据
						if (unitTypeList != null && unitTypeList != undefined && unitTypeList.length >0) {
							for(var i=0; i<unitTypeList.length; i++){
								var unitTypeMap = unitTypeList[i];
								htmlArr.push("	<div class='unitType-select-div' onclick='selectUnitType(this)'>"+ unitTypeMap['unitType'] +"</div>");
							}
						}
						
						_this.append(htmlArr.join(''));
					}else{
						showErrorMessage(response.message);
					}
				}
			 });
		}
	});
}


// 添加/修改 财务科目
function saveBudgetInfo(){
	if($("#financeSubjName").val() == ""){
		$("#financeSubjName").addClass("error-finance-tips");
		$(".finance-subject-tips").css("display", "block");
		return false;
	}
	//获得请求的数据
	var subData = {};
	subData.financeSubjId = $("#financeSubjId").val();
	subData.financeSubjParentId = $("#financeSubjParentId").val();
	subData.financeSubjName = $("#financeSubjName").val();
	subData.level = parseInt($("#level").val())+1;
	subData.remark = $("#remark").val();
	subData.amount = $("#amount").val().replace(/,/g, "")-0;
	subData.unitType = $("#unitType").val();
	if($("#currentId").val() == null){
		subData.currencyId = "";
	}else{
		subData.currencyId = $("#currentId").val();
	}
	subData.perPrice = $("#perPrice").val().replace(/,/g, "")-0;
	subData.money = $("#money").val().replace(/,/g, "")-0;
	$.ajax({
		url: '/budgetManager/saveBudgetInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#subjectwin").jqxWindow("close");
				window.location.reload();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//取消 添加/修改
function cancelBudgetInfo(){
	$("#financeSubjId").val("");
	$("#financeSubjParentId").val("");
	$("#financeSubjName").val("");
	$("#level").val("");
	$("#remark").val("");
	$("#amount").val("");
	$("#unitType").val("");
	$("#currentId").val("");
	$("#perPrice").val("");
	$("#money").val("");
	
	$("#subjectwin").jqxWindow("close");
}




//删除财务科目
function deleteFinanceSubject(editrow){	
    var dataRecord = $("#finaceBudgetList").jqxTreeGrid('getRow', editrow);
    popupPromptBox("提示", "是否要删除&nbsp;&nbsp;" + dataRecord.financeSubjName + "&nbsp;&nbsp;科目？", function(){
	    $.ajax({
		    url: '/budgetManager/deleteOneSubject',
			type: 'post',
			data: {"financeSubjId" : dataRecord.financeSubjId},
			datatype: 'json',
			success: function(response){
			    if(response.success){
				    showSuccessMessage("操作成功");
				    window.location.reload();
				}else{
					showErrorMessage(response.message);
				}
			}
		});
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








//费用管控表
function costControlInfo(){
	window.location = "/balanceManager/toBalancePage";
}


//会计科目
function accountSubjectInfo(){
	window.location = "/accountSubject/toAccountSubjectPage";
}

/*function downloadImportTemplate() {
	window.location.href = basePath + "/template/import/财务预算表导入模板.xls";
}*/

//显示下拉框
function showSelect(ev){
	$("#unitTypeMainDiv").show();
	ev.stopPropagation();
}

//隐藏单位下拉框
function hideUnitTypeSelect(){
	$("#unitTypeMainDiv").hide();
}

//修改单位
function selectUnitType(own){
	//点击后修改文本框的值
	var selectVal = $(own).text();
	$("#unitType").val(selectVal);
	//隐藏下拉框
	$("#unitTypeMainDiv").hide();
}

//费用预算文本框只能输入数字
function onlyNum(own){
	//取出输入的值
	var inputVal = $("#money").val();
	if (isNaN(inputVal)) {
		$("#money").val('');
	}
}