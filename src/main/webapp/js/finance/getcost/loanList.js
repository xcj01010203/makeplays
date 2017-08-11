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
	
	//加载借款信息表格
	loadLoanInformationList();
	//获取所有货币统计信息
	getAllCurrencyInfo();
	//初始化单个借款单详细信息弹窗
	initLoanOrderWin();
	//初始化付款详情弹窗
    initPaymentDetail();   
    
    $("#loanDetailWin").on('close', function (event){ 
		if (payUploader.getFiles('inited').length != 0){
			var files = payUploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				payUploader.removeFile(files[i].id, true);
			}
			$("#showAttachmentFile").empty();
		
	    }
	});
    
});


//内部表格变量
var detailGrid;


//加载借款信息表格
function loadLoanInformationList(){
	
	var source= {
		url: '/loanInfoManager/queryPayeeListWithMoneyInfo',
		type: 'post',
		datatype: 'json',
		datafields: [
		    {name: 'payeeName', type: 'string'},
		    {name: 'currencyId', type: 'string'},
		    {name: 'currencyCode', type: 'string'},
		    {name: 'exchangeRate', type: 'double'},
		    {name: 'loanMoney', type: 'double'},
		    {name: 'payedMoney', type: 'double'},
		    {name: 'leftMoney', type: 'double'}
		],
		root: 'payeeList',
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var cellclass = function (row, columnfield, value) {
			
			return "yellow1";
			
	};
	var rendergridrows = function (params) {
    	//调用json返回的列表数据
        return params.data;
    };
    
    //欠款金额列
    var leftMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var html = [];
    	html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.leftMoney));
    	if (!singleCurrencyFlag) {
    		html.push("(" + rowdata.currencyCode + ")");
    	}
    	html.push("</div>");
		return html.join("");
    };
    
    //原始借款金额列
    var loanMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var html = [];
    	html.push("<div class='jqx-column column-align-right'>"+ fmoney(rowdata.loanMoney));
    	if (!singleCurrencyFlag) {
    		html.push("(" + rowdata.currencyCode + ")");
    	}
    	html.push("</div>");
		return html.join("");
    };
    
    //已还金额列
    var payedMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var html = [];
    	html.push("<div class='jqx-column column-align-right'>"+ fmoney(rowdata.payedMoney));
    	if (!singleCurrencyFlag) {
    		html.push("(" + rowdata.currencyCode + ")");
    	}
    	html.push("</div>");
		return html.join("");
    };
    
    
    
    /************************************************内部表格--借款单详细信息***********************************************************************/
    var initrowdetails = function (index, parentElement, gridElement, datarecord) {
        detailGrid = $($(parentElement).children()[0]);
    	var source= {
    		url: '/loanInfoManager/queryLoanInfoList',
    		type: 'post',
    		data: {payeeName: datarecord.payeeName, currencyId: datarecord.currencyId},
    		datatype: 'json',
    		datafields: [
    		 		    {name: 'loanId', type: 'string'},
    		 		    {name: 'loanDate', type: 'string'},
    		 		    {name: 'receiptNo', type: 'string'},
    		 		    {name: 'currencyId', type: 'string'},
    		 		    {name: 'currencyCode', type: 'string'},
    		 		    {name: 'exchangeRate', type: 'double'},
    		 		    {name: 'money', type: 'double'},
    		 		    {name: 'summary', type: 'string'},
    		 		    {name: 'payedMoney', type: 'double'},
    		 		    {name: 'paymentCount', type: 'int'},
    		 		    {name: 'leftMoney', type: 'double'},
    		 		    
    		 ],
    		 root: 'loanInfoList',
    	};
    	
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	
	    var cellclass = function (row, columnfield, value) {
		   return "yellow1";				
		};
		
		//票据编号列
		var receiptNoRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			var loanId = rowdata.loanId;
			var html = [];
	    	html.push("<div class='jqx-column'>");
	    	html.push("  <a href='javascript:void(0)' lid='"+ rowdata.loanId + "' onclick='updataLoanOrder(this)'>"+ rowdata.receiptNo +"</a>");
	    	html.push("  <span class='payment-detail operate-btn-list'><a href='javascript:showPaymentDetailWin(\""+ loanId + "\")'>付款详情</a></span>");
	    	html.push("</div>");
	    	
	    	return html.join("");
		};
		//欠款余额列
		var leftMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			var html = [];
			html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.leftMoney));
			if (!singleCurrencyFlag) {
				html.push("<span class='code-color'>("+ rowdata.currencyCode +")</span>");
			}
			html.push("</div>");
			return html.join("");;
		};
		//借款金额列
		var moneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			var html = [];
			html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.money));
			if (!singleCurrencyFlag) {
				html.push("<span class='code-color'>("+ rowdata.currencyCode +")</span>");
			}
			html.push("</div>");
			return html.join("");
		};
		//已还金额
		var payedMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
			var html = [];
			html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.payedMoney));
			if (!singleCurrencyFlag) {
				html.push("<span class='code-color'>("+ rowdata.currencyCode +")</span>");
			}
			html.push("</div>");
			return html.join("");
		};
		
    	
    	
    	
    	detailGrid.jqxGrid({
    		theme: theme,
    		width: '98%',
    		height: 200,
    		source: dataAdapter,
    		enabletooltips: true,
	        selectionmode:"none",
	        cellhover: function (cellhtmlElement, x, y){},
	        columns: [
	            {text:'票据编号', datafield: 'receiptNo', cellsrenderer: receiptNoRenderer, width: '20%', align: "center", cellclassname: cellclass},
	            {text:'摘要', datafield: 'summary', width: '20%', align: "center", cellsAlign: "left", cellclassname: cellclass},
	            {text:'欠款余额', datafield: 'leftMoney', cellsrenderer: leftMoneyRenderer, width: '20%', align: "center", cellclassname: cellclass},
	            {text:'借款金额', datafield: 'money', cellsrenderer: moneyRenderer, width: '20%', align: "center", cellclassname: cellclass},
	            {text:'已还金额', datafield: 'payedMoney', cellsrenderer: payedMoneyRenderer, width: '20%', align: "center", cellclassname: cellclass}
	        ],
		    
	        cellhover: function(e) {
				var $this = $(e);
				//显示操作按钮
				$this.on("mouseover",function(){
					$this.find("span.operate-btn-list").show();
				}).on("mouseout", function(){
					$this.find("span.operate-btn-list").hide();
				});
	        }
    	});
    	
    	
    };
    
    
    
	
	$("#loanInformationList").jqxGrid({
		theme: theme,
		width: 'calc(100% -2px)',
	    height: '100%',
	    source: dataAdapter,
	    rowdetails: true,
	    rowdetailstemplate: {rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 220, rowdetailshidden: true},
	    initrowdetails: initrowdetails,
	    pageable: true,
	    pagesize: 50,
        pagerbuttonscount: 5,
        pagesizeoptions: ['50', '100', '全部'],
        showToolbar: true,
        rendergridrows:rendergridrows,
        localization:localizationobj,//表格文字设置
        rendertoolbar: function (toolbar) {
        	var container = [];
        	container.push("<div class='toolbar'>");
        	if(hasExportLoanAuth) {
            	container.push("<input type='button' class='export-btn' id='exportBtn' onclick='exportList()'>");
        	}
        	container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportLoanAuth) {
				$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
			}
        },
	    columns: [
	         {text: '姓名', datafield: 'payeeName', width: '25%', cellclassname: cellclass, cellsAlign: "center", align:"center"},
	         {text: '欠款金额', datafield: 'leftMoney', cellsrenderer: leftMoneyRenderer, width: '25%', cellclassname: cellclass, cellsAlign: "center", align:"center"},
	         {text: '借款金额合计', datafield: 'loanMoney', cellsrenderer: loanMoneyRenderer, width: '25%', cellclassname: cellclass, cellsAlign: "center", align:"center"},
	         {text: '已报销金额', datafield: 'payedMoney', cellsrenderer: payedMoneyRenderer, width: '25%', cellclassname: cellclass, cellsAlign: "center", align:"center"},
	    ]	
        
	});

	
	$("#loanInformationList").on('rowexpand', function(event){
        var rows = $('#loanInformationList').jqxGrid('getdisplayrows');
        var args = event.args;
        rowBoundIndex = args.rowindex;
        for (var i = 0; i < rows.length; i++) {
             if (i != args.rowindex) {
                 $('#loanInformationList').jqxGrid('hiderowdetails', i);
             } else {
                 $('#loanInformationList').jqxGrid('selectrow', i);
             }
        }
	});
}


//获取所有货币统计信息
function getAllCurrencyInfo(){
	$.ajax({
		url: '/loanInfoManager/queryPayeeListWithMoneyInfo',
		type: 'post',
		datatype: 'json',
		data: {pagesize: 1000},
		success: function(response){
			if(response.success){
				var currencyList= response.currencyList;
				var standardMoneyMap= response.standardMoneyMap;
				var html = [];
				html.push("<table class='currency-list-table' cellspacing=0 cellpadding=0>");
				
				if(currencyList.length != 0){
					for(var i= 0; i< currencyList.length; i++){
						if(i == 0){
							html.push("<tr>");
							html.push("<td rowspan='" + currencyList.length +"'>合计</td>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].leftMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].loanMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].payedMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("</tr>");
						}else{
							html.push("<tr>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].leftMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].loanMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("<td class='align-right'>" + fmoney(currencyList[i].payedMoney));
							if (!singleCurrencyFlag) {
								html.push("("+ currencyList[i].currencyCode +")");
							}
							html.push("</td>");
							html.push("</tr>");
						}
						
					}
					
				}
				
				if(standardMoneyMap != null){
					html.push("<tr>");
					html.push("<td>折合本位币</td>");
					html.push("<td class='align-right'>" + fmoney(standardMoneyMap.leftMoney));
					if (!singleCurrencyFlag) {
						html.push("("+ standardMoneyMap.currencyCode +")");
					}
					html.push("</td>");
					html.push("<td class='align-right'>" + fmoney(standardMoneyMap.loanMoney));
					if (!singleCurrencyFlag) {
						html.push("("+ standardMoneyMap.currencyCode +")");
					}
					html.push("</td>");
					html.push("<td class='align-right'>" + fmoney(standardMoneyMap.payedMoney));
					if (!singleCurrencyFlag) {
						html.push("("+ standardMoneyMap.currencyCode +")");
					}
					html.push("</td>");
					html.push("</tr>");
				}
				html.push("</table>");
				$("#currInformationSum").empty();
				$("#currInformationSum").append(html.join(""));
				
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}



//显示单个借款单详细信息弹窗
function updataLoanOrder(own){
	own= $(own);
	var loanId = own.attr("lid");
//	$("#fullReceiptDiv").load("/loanInfoManager/toLoanDetailInfoPage?loanId="+loanId);
	//请求附件列表
	$.ajax({
		url: '/loanInfoManager/queryLoanDetailInfo',
		type: 'post',
		data: {"loanId": loanId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var attachmentList = response.attachmentList;
				//TODO;
				if(attachmentList != undefined){
					initUploadFilesInfo(attachmentList);
				}
				$("#loanReceiptDiv").attr("src", "/loanInfoManager/toLoanDetailInfoPage?loanId="+loanId);
				//初始化上传附件
				initUploader();
				$("#loanDetailWin").jqxWindow("open");
			}
		}
	});
	
	//权限设置
	if(isRunningAccountReadonly){
		$("#saveModifyAfterLoan").remove();
		$("#deleteModifyAfterLoan").remove();
	}
	
}

//初始化单个借款单详细信息弹窗
function initLoanOrderWin(){
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
	$("#loanDetailWin").jqxWindow({
		theme: theme,
		width: jqxWinWidth,
	    height: 600,
	    maxWidth: 1866,
		maxHeight: 768,
	    resizable: false,
		isModal: true,
		autoOpen: false
	});
}



//保存修改后的借款单
function saveModifyAfterLoan(){
	var _frame = $('#loanReceiptDiv').contents();//得到iframe页面的数据
	var subData= {};
	//获取数据
	subData.loanId = _frame.find("#loanId").val();
	subData.receiptNo = _frame.find("#billsNum").val();
	subData.loanDate = _frame.find("#billsDateTime").val();
	subData.payeeName = _frame.find("#loanMoneyParty").val();
	subData.summary = _frame.find("#summary").val();
	subData.money = _frame.find("#money").next("input[type=hidden]").val();
	subData.currencyId = _frame.find("#currency").val();
	subData.paymentWay = _frame.find("#paymentWay").val();
	subData.agent = _frame.find("#agent").val();
	subData.financeSubjName = _frame.find("#financeSubjName").val();
	subData.financeSubjId = _frame.find("#financeSubjId").val();
	subData.attpacketId = _frame.find("#attachmentPacketId").val();
	
	$.ajax({
		url: '/loanInfoManager/saveLoanInfos',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				if (payUploader.getFiles('inited').length != 0){
					var attpackId = response.attpacketId;
					payUploader.option('formData', {
		    			attpackId: attpackId
		    		});
					payUploader.upload();
			    }else{
			    	showSuccessMessage("保存成功");
					$("#loanDetailWin").jqxWindow("close");
					detailGrid.jqxGrid("updatebounddata");
					getAllCurrencyInfo();
			    }
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//删除借款单信息
function deleteModifyAfterLoan(){
	var _frame = $('#loanReceiptDiv').contents();//得到iframe页面的数据
	var loanId = _frame.find("#loanId").val();
	  popupPromptBox("提示", "是否要删除借款单？", function() {
		  $.ajax({
			  url: '/loanInfoManager/deleteLoanInfo',
			  type: 'post',
			  data: {loanId:loanId},
			  datatype: 'json',
			  success: function(response){
				  if(response.success){
					  showSuccessMessage(response.message);
					  window.location.reload();
				  }else{
					  showErrorMessage(response.message);
				  }
			  }
		  });
      });
}

//取消修改借款单
function cancelModifyAfterLoan(){
	$("#loanDetailWin").jqxWindow("close");
}


//显示付款详情弹窗
function showPaymentDetailWin(loanId){
	
	var loanIdValue= loanId;
	
	//显示付款详情列表
	var source = {
		url: '/paymentManager/queryPaymentByLoanId',
		type: 'post',
		data: {loanId: loanIdValue},
		datatype: 'json',
		datafields: [
 		 		    {name: 'paymentId', type: 'string'},
 		 		    {name: 'receiptNo', type: 'string'},
 		 		    {name: 'summary', type: 'string'},
 		 		    {name: 'financeSubjName', type: 'string'},
 		 		    {name: 'currencyCode', type: 'string'},
 		 		    {name: 'payedMoney', type: 'double'},
 		 		    {name: 'loanBalance', type: 'double'},
 		 		    {name: 'forLoanMoney', type: 'double'}
 		 		   		 		    
 		 ]
 		 
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	//付款金额列
	var payedMoneyRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.payedMoney));
		if (!singleCurrencyFlag) {
			html.push("<span class='code-color'>("+ rowdata.currencyCode +")</span>");
		}
		html.push("</div>");
		return html.join("");
	};
	//余额列
	var loanBalanceRenderer= function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		html.push("<div class='jqx-column column-align-right'>" + fmoney(rowdata.forLoanMoney));
		if (!singleCurrencyFlag) {
			html.push("<span class='code-color'>("+ rowdata.currencyCode +")</span>");
		}
		html.push("</div>");
		return html.join("");
	};
	
	$("#paymentDetailGrid").jqxGrid({
		theme: theme,
		width: '99%',
		height: '98%',
		source: dataAdapter,
        columns: [
            {text:'票据编号', datafield: 'receiptNo', width: '20%', align: "center", cellsAlign: "center"},
            {text:'摘要', datafield: 'summary', width: '20%', align: "center", cellsAlign: "left" },
            {text:'财务科目', datafield: 'financeSubjName', width: '20%', align: "center", cellsAlign: "left" },
            {text:'付款金额', datafield: 'payedMoney', cellsrenderer: payedMoneyRenderer, width: '20%', align: "center"},
            {text:'还借款', datafield: 'forLoanMoney', cellsrenderer: loanBalanceRenderer, width: '20%', align: "center"}
        ]
	});
	$("#paymentDetailWin").jqxWindow("open");
	
}
//初始化付款详情弹窗
function initPaymentDetail(){
	$("#paymentDetailWin").jqxWindow({
		theme: theme,
		width: 800,
	    height: 500,
	    maxWidth: 1000,
		maxHeight: 700,
	    resizable: false,
		isModal: true,
		autoOpen: false
	});
}


//导出

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
	
    var fileAddress;
	$.ajax({
		url: 'loanInfoManager/exportPayeeListWithMoneyInfo',
		type: 'post',
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
		showSuccessMessage("保存成功");
		$("#loanDetailWin").jqxWindow("close");
		detailGrid.jqxGrid("updatebounddata");
		getAllCurrencyInfo();
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
	if(attachmentList !=null || attachmentList.length != 0){
		var html = [];
		for(var i= 0; i< attachmentList.length; i++){
			var attachment = attachmentList[i];
			var suffix =attachment.suffix.toLowerCase();
			if(suffix == ".doc" || suffix == ".docx"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/word.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else if(suffix == ".pdf"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/pdf.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else if(suffix == ".xls" || suffix == ".xlsx"){
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")' sv = '"+ attachment.id +"'><img src='../images/excel.jpg' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
			}
			else{
				html.push("<li class='upload-file-list-li' id='"+ attachment.attpackId +"' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\")' sv = '"+ attachment.id +"'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"' title='"+ attachment.name +"'><a class='download-tag' onclick='downLoadFile(this,event)' title='下载'></a><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
//				html.push("<li class='upload-file-list-li'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"' title='"+ attachment.name +"'><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>")
			}
		
		}
		html.join("");
		$("#showAttachmentFile").empty();
		$("#showAttachmentFile").append(html);
		
		/*$("#showFileRealNameAndSaveId").viewer();*/
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
	            address = response.downLoadAddress;
	            fileName = response.fileName;
			}else{
				showErrorMessage(response.message);
			}
		}
	});
    window.location.href="/fileManager/downloadFileByAddr?address="+address +"&fileName="+fileName;
    ev.stopPropagation();
}