var isHaveData=true;
$(document).ready(function() {
	//校验是否需要财务密码
	checkNeedFinancePwd();
	//加载总费用进度
	loadTotalFinance();
	//加载预算支出图形
	loadFinanceChart('0');
	//初始化自定义科目组窗口
	initSelfWin();
	//初始化财务科目
	initFinanceSubjTree();
});
//加载总费用进度
function loadTotalFinance() {
    $.ajax({
        url: "/financeStatisticManager/queryTotalFinance",
        type: "post",
        dataType: "json",
        success: function(response) {
            if (response.success) {
            	var bgColor='#3B6BA1';
            	var barColor='rgba(139,189,104,0.5)';
            	var totalFinance=isNull(response.totalFinance);
            	var totalBudgetMoney=isNull(totalFinance.totalBudgetMoney);
            	if(totalBudgetMoney) {
            		totalBudgetMoney=fmoney(totalBudgetMoney,2);
            	}
            	var totalPayedMoney=isNull(totalFinance.totalPayedMoney);
            	if(totalPayedMoney){
            		totalPayedMoney=fmoney(totalPayedMoney,2);
            	}
	    	    $('#jqmeter-container').jQMeter({
	 				goal:totalBudgetMoney+'',
	     			raised:totalPayedMoney+'',
	     			width:'70%;',
	     			height:'50px',
	 				bgColor:bgColor,
	 				barColor:barColor,
	 			});
            	var htmltite="<table style='font-size:12px;line-height:16px;text-align:left;'><tr><td colspan='2'>总费用</td></tr>"
            		+"<tr><td style='color:"+bgColor+";'>预算：</td><td style='font-weight:bold;text-align:right;'>"+totalBudgetMoney+"</td></tr>"
            		+"<tr><td style='color:rgba(139,189,104,1);'>支出：</td><td style='font-weight:bold;text-align:right;'>"+totalPayedMoney+"</td></tr></table>"
                $("#jqmeter-container").jqxTooltip({ content: htmltite, position: 'bottom',autoHideDelay: 90000  });
            } else {
//            	showErrorMessage(response.message);
            }
        }
    });
}
//预算支出概况
function loadBudgetPayedInfo(parentId,name){
	var statType = $('input[name="statType"]:checked').val();
	var params={statType:statType};
	if(statType=='1'){
		params.parentId=parentId;
	}
	$.ajax({
        url: "/financeStatisticManager/queryBudgetPayedInfo",
        type: "post",
        dataType: "json",
        data:params,
        async: false,
        success: function(response) {
            if (response.success) {
        		isHaveData=true;
            	var budgetPayedInfo=response.budgetPayedInfo;
            	if(!budgetPayedInfo || budgetPayedInfo.length==0) {
            		isHaveData=false;
            		if(statType!='1' || parentId=='0'){
            			$("#statis_budgetpayed2").hide();
                		showInfoMessage('暂无数据');
            		} else {
                		//showInfoMessage('已经是最后一级科目');
            			swal({
            			    title: "提示",
            		        text: '是否要查看该科目支付明细？',
            		        type: "warning",
            		        showCancelButton: true,
            		        confirmButtonColor: "rgba(255,103,2,1)",
            		        confirmButtonText: "是",   
            		        cancelButtonText: "否",   
            		        closeOnConfirm: true,   
            		        closeOnCancel: true
            			},function (isConfirm){
            				if(!isConfirm) {
            					closeRightWin();
            					return;
            				}
            				//弹出财务科目明细
            				showSubjectDetail(parentId);
            			});
            		}
            		return;
            	}
            	//获取option数据
            	var legendData=['预算','支出'];
            	var xAxisData=[];
            	var seriesData=[];
            	var budgetData=[];
            	var payedData=[];
            	for(var i=0;i<budgetPayedInfo.length;i++){
            		xAxisData.push(budgetPayedInfo[i].name);
            		budgetData.push({value:isNull(budgetPayedInfo[i].budgetMoney),id:budgetPayedInfo[i].id});
            		payedData.push({value:isNull(budgetPayedInfo[i].payedMoney),id:budgetPayedInfo[i].id});
            	}
            	seriesData.push({name:'预算',type:'bar',barGap:'-40%',data:budgetData,itemStyle:{normal:{color:'#3B6BA1'}}});
            	seriesData.push({name:'支出',type:'bar',data:payedData,itemStyle:{normal:{color:'#71A450'}}});
            	var title='预算支出概况';
            	if(name){
            		title=name+'-'+title;
            	}
            	if(statType=='1'){
                	initBarChart('statis_budgetpayed',title,legendData,xAxisData,seriesData);
            	} else {
            		initBarChart2('statis_budgetpayed2',title,legendData,xAxisData,seriesData);
            	}
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
}
//显示财务科目明细
function showSubjectDetail(subjectId) {
	$("#exportSubjectDetailBtn").attr('subjectId',subjectId);
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	//加载该科目账务详情
	loadSubjectDetail(subjectId);
	//加载财务科目货币统计信息
	loadSubjectTotal(subjectId);
}
//关闭右侧滑出框
function closeRightWin() {
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
}
//加载该科目账务详情
function loadSubjectDetail(subjectId) {
	var source = {
		url: '/getCostManager/queryFinanceRunningAccount',
		type: "post",
		dataType : "json",
		data: {financeSubjIds:subjectId, isAsc:true, status:1},
		datafields : [
		    { name: 'receiptDate',type: 'string' },//日期
			{ name: 'receiptNo',type: 'string' },//票据编号
			{ name: 'summary',type: 'string' },//摘要
			{ name: 'contractNo',type: 'string' },//关联合同号
	        { name: 'financeSubjName',type: 'string' },//财务科目
	        { name: 'collectMoneyStr',type: 'string' },//收款金额
	        { name: 'payedMoneyStr',type: 'string' },//付款金额
	        { name: 'status',type: 'int' },//状态
	        { name: 'formType',type: 'int' },//单据类型
	        { name: 'formTypeStr',type: 'string' },//单据类型
	        { name: 'aimPersonName',type: 'string' },//收/付款方
	        { name: 'paymentWay',type: 'string' },//付款方式
	        { name: 'hasReceipt',type: 'int' },//有无发票
	        { name: 'billCount',type: 'int' },//票据张数
	        { name: 'agent',type: 'string' }//记账
		],
		root: 'runningAccountList'
	};
	var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) {
        	if(!data.success) {
        		showErrorMessage(data.message);
        	}
        },
        loadError: function (xhr, status, error) { }
    });
	//摘要
    var summaryRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.summary) {
			html.push("<div class='jqx-column column-align-left' title='"+ rowdata.summary +"'>" + rowdata.summary + "</div>");
		}else{
			html.push("<div class='jqx-column column-align-left' title=''></div>");
		}
		html.join("");
		return html;
	};
	//财务科目
	var financeSubjNameRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		if(rowdata.financeSubjName != null){
			html.push("<div class='jqx-column column-align-left' title='"+ rowdata.financeSubjName +"'>" + rowdata.financeSubjName + "</div>");
		}else{
			html.push("<div class='jqx-column column-align-left'></div>");
		}
		
		html.join("");
		return html;
	};
	 
	//状态
	var statusRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
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
	
	//有无发票
	var hasReceiptRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
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
	
	//设置每行数据的格式
    var gridColumns=[{text: "日期", datafield:'receiptDate', width: '108px', cellsAlign: 'center', align: 'center'},
			{text: "票据编号", datafield:'receiptNo', width: '108px', cellsAlign: 'center', align: 'center'},
			{text: "摘要", datafield:'summary',cellsrenderer: summaryRenderer, width: '306px', cellsAlign: 'left', align: 'center'},
			{text: "关联合同号", datafield:'contractNo', width: '78px', cellsAlign: 'center', align: 'center'},
			{text: "财务科目", datafield:'financeSubjName',cellsrenderer: financeSubjNameRenderer, width: '320px', cellsAlign: 'left', align: 'center'},
//			{text: "收款金额", datafield: 'collectMoneyStr', style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center'},
			{text: "付款金额", datafield: 'payedMoneyStr', style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center'},
			//{text: "资金余额", datafield: 'leftMoney', cellsrenderer: leftMoneyRenderer, style:'text-align: right;', width: '178px', cellsAlign: 'right', align: 'center'},
//			{text: "状态", datafield: 'status', cellsrenderer:statusRenderer, width: '70px', cellsAlign: 'center', align: 'center'},
//			{text: "单据类型", datafield: 'formTypeStr', width: '140px', cellsAlign: 'center', align: 'center'},
			{text: "付款方", datafield:'aimPersonName', width: '200px', cellsAlign: 'center', align: 'center'},
			{text: "付款方式", datafield:'paymentWay',width: '160px', cellsAlign: 'center', align: 'center'},
			{text: "有无发票", datafield: 'hasReceipt', cellsrenderer: hasReceiptRenderer, width: '70px', cellsAlign: 'center', align: 'center'},
			{text: "票据张数", datafield:'billCount', width: '70px', cellsAlign: 'center', align: 'center'},
			{text: "记账", datafield:'agent',width: '120px', cellsAlign: 'center', align: 'center'}];
    $("#subjectDetail").remove();
	$("#subjectTotal").before('<div id="subjectDetail"></div>');
    $("#subjectDetail").jqxGrid({
		width: "100%",
		height: "100%",
		source: dataAdapter,
		columns:gridColumns,
		showToolbar: false,
		columnsResize: true,
		columnsHeight:35,
		rowsHeight:35,
		localization: localizationobj
	});
}
//获取财务科目货币统计信息
function loadSubjectTotal(subjectId){
	$.ajax({
		url: '/getCostManager/queryRunnigAccountStatistic',
		type: 'post',
		data: {financeSubjIds:subjectId, status:1},
		datatype: 'json',
		success: function(response){
			if(response.success){				
				var currencyList = response.currencyList;
				var html = [];
				html.push("<table class='currency-list-table' cellspacing=0 cellpadding=0>");
				html.push("<thead><tr><td class='red-td' width='20%'>统计</td><td width='40%'>币种</td><td width='40%'>付款金额</td></tr><thead>");
			
				if(currencyList != null){
					html.push("<tbody>");
					for(var i= 0; i< currencyList.length; i++){
						if(i== 0){
							html.push("<tr>");
							html.push("<td rowspan='"+ currencyList.length+"'>合计</td>");
							html.push("<td>" + currencyList[i].currencyCode + "</td>");
//							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].collectMoney) + "</td>");
							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].payedMoney) + "</td>");
//							html.push("<td class='column-align-right'>" + fmoney(currencyList[i].leftMoney) + "</td>");
							html.push("</tr>");
						}else{
							html.push("<tr>");
							html.push("<td class='td-border'>" + currencyList[i].currencyCode + "</td>");
//							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].collectMoney) + "</td>");
							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].payedMoney) + "</td>");
//							html.push("<td class='td-border column-align-right'>" + fmoney(currencyList[i].leftMoney) + "</td>");
							html.push("</tr>");
						}
					}
					html.push("</tbody>");
				}
				html.push("</table>");
				$("#subjectTotal").html(html.join(""));
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//导出财务科目支出明细
function exportSubjectDetail(){
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
		data: {financeSubjIds:$("#exportSubjectDetailBtn").attr('subjectId'), isAsc:true, status:1, templateName:'subject'},
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
			var form = $("<form style='display:none;'></form>");
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
//判断对象是否空，为空置为空字符串
function isNull(obj){
	if(!obj){
		return '';
	}
	return obj;
}
//加载日支出、累计日支出
function loadDayPayedInfo(parentId,name){
	var statType = $('input[name="statType"]:checked').val();
	var params={statType:statType};
	if(statType=='1'){
		params.parentId=parentId;
	}
	$.ajax({
        url: "/financeStatisticManager/queryDayPayedInfo",
        type: "post",
        dataType: "json",
        data:params,
        async: false,
        success: function(response) {
            if (response.success) {
            	var dayPayedInfo=response.dayPayedInfo;
            	//获取option数据
            	var legendData=['支出'];
            	var xAxisData=[];
            	var daypayedData=[];
            	var totaldaypayedData=[];
            	for(var i=0;i<dayPayedInfo.length;i++){
            		xAxisData.push(dayPayedInfo[i].paymentDate);
            		daypayedData.push(dayPayedInfo[i].dayPayedMoney);
            		totaldaypayedData.push(dayPayedInfo[i].totalDayPayedMoney);
            	}
            	//日支出
            	var title='日支出';
            	var title2='累计日支出';
            	if(name){
            		title=name+'-'+title;
            		title2=name+'-'+title2;
            	}
            	initLineChart('statis_daypayed',title,legendData,xAxisData,[{name:'支出',type:'line',itemStyle:{normal:{color:'#71A450'}},smooth:true,areaStyle: {normal: {}},data:daypayedData}]);
            	//累计日支出
            	initLineChart('statis_totaldaypayed',title2,legendData,xAxisData,[{name:'支出',type:'line',itemStyle:{normal:{color:'#71A450'}},smooth:true,areaStyle: {normal: {}},data:totaldaypayedData}]);
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
}
//加载柱图图形 flag：标识是否有“点击查看下级科目”
function getBarOption(title,legendData,xAxisData,seriesData,flag) {
	var option = {
			title:{
				show:true,
				text:title,
				left:'center'
			},
			tooltip : {
		        trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        },
		        formatter:function(params){
		        	var returnStr=params[0].name;
		        	for(var i=0;i<params.length;i++){
		        		var val=params[i].value;
		        		if(val) {
		        			val=fmoney(val,2);
				        	returnStr+='<br><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:'+params[i].color+'"></span>'+params[i].seriesName+'：<div style="float:right;text-align:right;">'+val+'</div>';
		        		}
		        	}
		        	if(flag!=1){
		        		returnStr+='<br><font color="#f98a66">点击查看下级科目</font>';
		        	}
		        	return returnStr;
		        }
		    },
		    grid: {
		        left: '2%',
		        right: '80',
		        bottom: '80',
		        containLabel: true
		    },
		    legend: {
		        data:legendData,
		        right:'4%'
		    },
		    xAxis : [
		        {
		            type : 'category',
		            data : xAxisData,
		            axisLabel:{
		            	interval:0,
		            	rotate:30
		            }
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value'
		        }
		    ],
		    series : seriesData
		};
	return option;
}
function initBarChart(divId,title,legendData,xAxisData,seriesData) {
	$("#"+divId).show();
	var option = getBarOption(title, legendData, xAxisData, seriesData);
	var myChart = echarts.init(document.getElementById(divId));
	myChart.on('click', function (params) {
	    chartClick(params.data.id,params.name);
	});
	myChart.setOption(option);
}
function initBarChart2(divId,title,legendData,xAxisData,seriesData) {
	$("#"+divId).show();
	var option = getBarOption(title, legendData, xAxisData, seriesData,1);
	var myChart = echarts.init(document.getElementById(divId));
	myChart.setOption(option);
}
//点击柱图事件
function chartClick(id,name){
	//显示选择科目
	loadFinanceChart(id,name);
	if(isHaveData) {
		$("#subject_label").append("<span> > </span><span class='span_select' onclick='subjectClick(this,\""+id+"\",\""+name+"\")'>"+name+"</span>");
	}
}
//预算科目点击
function subjectClick(obj,id,name){
	//设置radio选中
	$('input[name="statType"][value="1"]').prop('checked','checked');
	//隐藏自定义科目组相关内容
	$("#statis_budgetpayed2").hide();
	$("#self_label").hide();
	$("#selfaddbutton").hide();
	//将当前科目之后的内容删掉
	if(obj) {
		$(obj).nextAll().remove();
	} else {
		$("#subjectbase").nextAll().remove();
	}
	//加载图形
	loadFinanceChart(id,name);
	var ev=ev||window.event;
	ev.stopPropagation();
}
//加载图形
function loadFinanceChart(id,name){
	$("#statis_budgetpayed2").hide();
	loadBudgetPayedInfo(id,name);
	loadDayPayedInfo(id,name);
}
//加载曲线图
function initLineChart(divId,title,legendData,xAxisData,seriesData) {
	$("#"+divId).show();
	var option = {
		title:{
			show:true,
			text:title,
			left:'center'
		},
		tooltip : {
	        trigger: 'trim'
	    },
	    grid: {
	        left: '2%',
	        right: '80',
	        containLabel: true
	    },
	    legend: {
	        data:legendData,
	        right:'4%'
	    },
	    dataZoom: [
           {
               show: true,
               realtime: true,
               start: 0,
               end: 100
           }
        ],
	    xAxis : [
	        {
	            type : 'category',
	            data : xAxisData,
	            axisLabel:{
	            	rotate:30
	            }
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value'
	        }
	    ],
	    series : seriesData
	};

	var LineChart = echarts.init(document.getElementById(divId));
	LineChart.setOption(option);
}
//点击自定义科目组radio
function selfClick(){
	//设置radio选中
	$('input[name="statType"][value="2"]').prop('checked','checked');
	//显示自定义科目label,'+'按钮
	$("#self_label").show();
	$("#selfaddbutton").show();
	//显示已定义的科目组
	$.ajax({
        url: "/financeStatisticManager/queryAllFinanceAccountGroup",
        type: "post",
        dataType: "json",
        success: function(response) {
            if (response.success) {
            	//清空自定义科目
            	$("#self_label").empty();
            	var financeAccountGroupList=response.financeAccountGroupList;
            	if(financeAccountGroupList && financeAccountGroupList.length>0){
            		var html=[];
            		for(var i=0;i<financeAccountGroupList.length;i++){
            			var obj=financeAccountGroupList[i];
            			html.push('<span class="span_select self_span" onclick="selfsubjectClick(\''+obj.groupId+'\')">'+obj.groupName+'</span>');
            		}
                	//添加自定义科目
                	$("#self_label").append(html.join(""));
            	}
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
	//加载自定义科目组图形--预算支出概况
	loadBudgetPayedInfo();
	//不显示日支出、日累计支出
	$("#statis_budgetpayed").hide();
	$("#statis_daypayed").hide();
	$("#statis_totaldaypayed").hide();
}
//自定义科目点击
function selfsubjectClick(groupId){
	$.ajax({
        url: "/financeStatisticManager/queryOneFinanceAccountGroup",
        type: "post",
        dataType: "json",
        data:{groupId:groupId},
        success: function(response) {
            if (response.success) {
            	var group=response.financeAccountGroup.group;
            	var groupMap=response.financeAccountGroup.groupMap;
            	$("#groupName").val(group.groupName);
            	$("#groupId").val(group.groupId);
            	//清空树
            	$('#subjTree').jqxTree('uncheckAll');
            	$('#subjTree').jqxTree('collapseAll');
            	//设置树选中
            	for(var i=0;i<groupMap.length;i++){
            		$("#subjTree").jqxTree('checkItem', $("#"+groupMap[i].accountId)[0],true);
            	}
            	//打开窗口
            	$("#selfWin").jqxWindow("open");
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
}
//显示自定义科目组窗口
function showSelfWin(){
	//清空内容
	$("#groupName").val('');
	$("#groupId").val('');
	$('#subjTree').jqxTree('uncheckAll');
	$('#subjTree').jqxTree('collapseAll');
	//打开窗口
	$("#selfWin").jqxWindow("open");
}
//初始化自定义科目组窗口
function initSelfWin(){
	$("#selfWin").jqxWindow({
		theme: theme,
		width: '400',
		height: '380',
		resizable: false,
		isModal: true,
		autoOpen: false
	});	
}

//初始化财务科目
function initFinanceSubjTree(){
	var data=[];
	 $.ajax({
			 url: '/financeSubject/querySubjectListWithJqxTreeFormat',
			 type: 'post',
			 datatype: 'json',
			 success: function(response){
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
						 $("#subjTree").jqxTree({
							 theme: theme,
							 width: 'calc(100% - 20px)',
							 height: '220',
							 source: records,
							 hasThreeStates: true,
							 checkboxes: true,
							 
						 });
						 //选择财务科目时的操作
						 $('#subjTree').on('checkChange', function (event){
									
								   var content = "";
								   var financeIds ="";
								   var items = $('#subjTree').jqxTree('getCheckedItems');
								   
								   for(var i= 0; i< items.length; i++){
								   var item=items[i];
								   if(item.hasItems == false){
									   //财务科目名称
									   content += item.label + ",";
									   //财务科目id
									   financeIds += item.id+",";		
								   }
								   $("#subjectId").val(financeIds);
							  }
						}); 
				 }else{
//					 showErrorMessage(response.message);
				 }
			}
	 });		 
}

//关闭添加/修改自定义科目窗口
function closeSelfWin(){
	$("#selfWin").jqxWindow("close");
}
//保存自定义科目组
function saveFinanceAccountGroup(){
	var groupName=$("#groupName").val();
	if(!groupName) {
		showInfoMessage("名称不能为空！");
		return;
	}
	var subjectId=$("#subjectId").val();
	if(!subjectId) {
		showInfoMessage("请选择财务科目！");
		return;
	}
	var groupId=$("#groupId").val();
	$.ajax({
        url: "/financeStatisticManager/saveFinanceAccountGroup",
        type: "post",
        dataType: "json",
        data:{groupId:groupId,groupName:groupName,subjectId:subjectId},
        success: function(response) {
            if (response.success) {
            	showSuccessMessage('保存成功');
            	//关闭添加/修改自定义科目组窗口
            	closeSelfWin();
            	//在导航条上显示自定义科目组
            	selfClick();
            } else {
            	showErrorMessage(response.message);
            }
        }
    });	
}
//删除自定义科目组
function deleteFinanceAccountGroup(){
	var groupId=$("#groupId").val();
	$.ajax({
        url: "/financeStatisticManager/deleteOneFinanceAccountGroup",
        type: "post",
        dataType: "json",
        data:{groupId:groupId},
        success: function(response) {
            if (response.success) {
            	showSuccessMessage('删除成功');
            	//关闭添加/修改自定义科目组窗口
            	closeSelfWin();
            	//重新显示
            	selfClick();
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
}