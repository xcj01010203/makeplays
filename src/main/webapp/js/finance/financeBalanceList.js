$(document).ready(function(){
	//显示费用管控表
	loadFinanceeBalance();
});


//定义表格的高度
var clientHeight=window.screen.availHeight-125;
//定义表格的宽度
var clientWidth=window.screen.availWidth-10;


//格式化金额
function fmoney(s, n)   
{   
   n = n > 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(),   
   r = s.split(".")[1];   
   t = "";   
   for(var i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }   
   return t.split("").reverse().join("") + "." + r;   
}




//加载费用管控表
function loadFinanceeBalance(){
	
	var source = {
	        url: '/balanceManager/queryBalanceList',
	        type: 'post',
	        datatype: 'json',
	        dataFields: [
	            {name: 'financeSubjId', type: 'string'},
	            {name: 'financeSubjName', type: 'string'},
	            {name: 'financeSubjParentId', type: 'string'},
	            {name: 'contractBudget', type: 'double'},
	            {name: 'loanBudget', type: 'double'},
	            {name: 'standardMoney', type: 'double'},
	            {name: 'leftMoney', type: 'double'},
	            {name: 'leftRate', type: 'double'},
	        ],
	        hierarchy: {
				keyDataField: {name:'financeSubjId'},
				parentDataField: {name:'financeSubjParentId'}
			},
			id: 'financeSubjId'
		};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var standardMoneyRender = function(row, column, value, rowData){
		var html="<div class='font_v_m'>"+'<span class="dig_sty">'+ value + '&nbsp;</span>'+"</div>";
     	return html;
	};
	var contractBudgetRender = function(row, column, value, rowData){
		html="<div class='font_v_m'>"+'<span class="dig_sty">'+ value + '&nbsp;</span>'+"</div>";
		return html;
	};
	var loanBudgetRender = function(row, column, value, rowData){
		html="<div class='font_v_m'>"+'<span class="dig_sty">'+ value + '&nbsp;</span>'+"</div>";
		return html;
	}
	var leftMoneyRender = function(row, column, value, rowData){
		html="<div class='font_v_m'>"+'<span class="dig_sty">'+ value + '&nbsp;</span>'+"</div>";
		return html;
	};
	var leftRateRenderer = function(row, column, value, rowData){
		html="<div class='font_v_m'>" + "<span class='dig_sty'>" + value + '&nbsp;</span>'+"</div>";
		return html;
	};
	
	
	
	//定义表头信息
	var columnInformation = [
		             		    {text: "财务科目", datafield: 'financeSubjName', width: '30%', cellsAlign: 'left', align: 'center', sortable: false},
		            		    {text: "预算金额", datafield: 'standardMoney', cellsrenderer: standardMoneyRender, width: '14%', cellsAlign: 'right', align: 'center', sortable: false},
		            		    {text: "关联合同款", datafield: 'contractBudget', cellsrenderer: contractBudgetRender, width: '14%', cellsAlign: 'right', align: 'center', sortable: false},
		            		    {text: "关联借款", datafield: 'loanBudget', cellsrenderer: loanBudgetRender, width: '14%', cellsAlign: 'right', align: 'center', sortable: false},
		            		    {text: "剩余", datafield: 'leftMoney', cellsrenderer: leftMoneyRender, width: '14%', cellsAlign: 'right', align: 'center', sortable: false},
		            		    {text: "剩余比例", datafield: 'leftRate', cellsrenderer: leftRateRenderer, width: '14%', cellsAlign: 'right', align: 'center', sortable: false}
		            		];
	
	
	
	$("#financeBalanceList").jqxTreeGrid({
		width: 'calc(100%)',
		height: 'calc(100% - 40px)',
		source: dataAdapter,
		columnsResize: true,
		showToolbar: true,
		toolbarHeight: 35,
		localization: localizationobj,
		rendertoolbar: function(toolbar){
			var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='pack-up-subject' id='packUpBtn' onclick='packUpSubjects(event)'>");
			container.push("<input type='button' class='pack-down-subject' id='packDownBtn' onclick='packDownSubjects(event)'>");
			container.push("<input type='button' class='export-btn' id='exportFinanaceControlBtn' onclick=''>");
			container.push("<input type='button' class='return-btn' id='returnBtn' onclick='returnPrevBtn()'>");
			container.push("</div>");
			
			toolbar.append($(container.join("")));
			
			$("#packUpBtn").jqxTooltip({content: "展开所有科目", position: "bottom"});
			$("#packDownBtn").jqxTooltip({content: "收起所有科目", position: "bottom"});
			$("#exportFinanaceControlBtn").jqxTooltip({content: "导出", position: "bottom"});
			$("#returnBtn").jqxTooltip({content: "返回", position: "bottom"});
			
			
			//导出功能
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
			$("#exportFinanaceControlBtn").on("click", function(){
				
				/*显示加载中*/
				var clientWidth=window.screen.availWidth;
				//获取浏览器页面可见高度和宽度
			    var _PageHeight = document.documentElement.clientHeight,
			        _PageWidth = document.documentElement.clientWidth;
			    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
			    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
			        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
			    //在页面未加载完毕之前显示的loading Html自定义内容
			    var _LoadingHtml = $("#loadingDiv");
			    
			    //呈现loading效果
			    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
			    _LoadingHtml.show();
			    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
				
				
				var rows = $("#financeBalanceList").jqxTreeGrid('getRows');
         	    var columnNames = new Array();
         	    for(var i = 0; i < columnInformation.length; i++){
        		   columnNames.push({dataField : columnInformation[i].datafield, text : removeHTMLTag(columnInformation[i].text)});
         	    }
         	    
        		exportData=new Array();
        		   
        		addRecord(rows, columnNames);
            	
                $.ajax({
                	url: '/commonExportExcel/exportExcel',
                	type: 'post',
                	data: {"rows" : JSON.stringify(exportData), "columns" : JSON.stringify(columnNames), "fileName" : "费用管控表"},
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
        		
            	
			});		
			
			
			
			
			
		},
		columns: columnInformation,
	    ready: function(){
	    	$.ajax({
	    		url: '/ balanceManager /queryTotalStatisticInfo',
	    		type: 'post',
	    		datatype: 'json',
	    		success: function(response){
	    			if(response.success){
	    				var html=[];
	    		    	html.push("    <table cellpadding = '0px' cellspacing = '0px' border='1px'>");
	    		    	html.push("    <tr>");
	    		    	html.push("        <th width= '30%' align = 'center'>合计:</th>");
	    		    	html.push("        <td width= '14%'>" + "<div class='font_v_m'><span class='dig_sty'>" + fmoney(response.totalBudget, 2) + "</span></div></td>");
	    		    	html.push("        <td width= '14%'>" + "<div class='font_v_m'><span class='dig_sty'>" + fmoney(response.totalContractBudget, 2) + "</span></div></td>");
	    		    	html.push("        <td width= '14%'>" + "<div class='font_v_m'><span class='dig_sty'>" + fmoney(response.totalLoanBudget, 2) + "</span></div></td>");
	    		    	html.push("        <td width= '14%'>" + "<div class='font_v_m'><span class='dig_sty'>" + fmoney(response.leftBudget, 2) + "</span></div></td>");
	    		    	html.push("        <td width= '14%'>" + "<div class='font_v_m'><span class='dig_sty'>" + response.leftRate +"%" + "</span></div></td>");
	    		    	html.push("    </tr>");
	    		    	html.push("　　　</table>");
	    		    	
	    		    	$(".table-finance-total-list").append(html.join(""));
	    			}
	    		}
	    	});
	    }
	
	});
	
	
}


//展开所有科目
function packUpSubjects(event){
	$("#financeBalanceList").jqxTreeGrid("expandAll");
	$("#packUpBtn").hide();
	$("#packDownBtn").show();
	$("#exportFinanaceControlBtn").addClass("export-btn-margin");
}
//收起所有科目
function packDownSubjects(evnet){
	$("#financeBalanceList").jqxTreeGrid("collapseAll");
	$("#packDownBtn").hide();
	$("#packUpBtn").show();
	$("#exportFinanaceControlBtn").removeClass("export-btn-margin");
}

//返回上一层
function returnPrevBtn(){
	history.go(-1);
}

/*导出需要的功能函数*/
function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}
