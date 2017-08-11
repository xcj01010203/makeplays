var theme='ui-lightness';
$(document).ready(function(){
	//获取币种列表
	getcurrencyId();
	//获取当前日期
	getNowFormatDate();
	//获取部门列表数据
	initDepartmentSelect();
	//获取新的付款单票据编号
	if($("#paymentId").val() == ""){
		getNewBillNum();
	}
	//加载财务科目列表
	loadFinanceSubjList();
	
	//格式化金额输入，自动计算金额合计
	formatAmount();
	//是否显示提示时间
	isShowTimeTips();
	//是否显示付合同款列表
//	showContractPayList();
	
	//加载付合同款下拉按鈕
	loadContractPayBtn();
	//加载合同付款人列表以及查询支付情况
	aboutContractInfo();
	//初始化付合同款弹窗
//	initContractPopupWin();
	//获取付款方式列表
	getPayMethodList();
	//初始化选择借款窗口
//	initSelectLoanOrder();

	//清空存放的借款单的id
	$("#loanIds").val("");
	//关闭下拉列表
	$(document).click(function(){
    	$('.fin_subj').css("display", "none");
    	$('.dropdown_box').hide();	
    	$('.paymethod-dropdown-box').hide();
    	$("span.select-flag").hide();
    	$("#department").hide();
    });	
	
	//判断增加付款单或者修改付款单
	if($("#paymentId").val() != "" && $("#paymentId").val() != "null"){
		//加载付款单信息
		loadPaymentOrderInfo();
	} else {
		//获取记账人信息
		getAccounting();
	}
	//待付清单
	if($("#isContractToPaid").val() == "true"){
//		$("#repayLoans").hide();
		$(".loan-info-div").hide();
//		$("#repayLoans").remove();
	}
	
	
});

//存放总报销金额
var publicPayedMoney;



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
    $("#billsDateTime").val(currentdate);
}
//获取新的付款单票据编号
function getNewBillNum(){
	var originalReceipNo = $("#originalReceipNo").val();
	var billsDateTime = $("#billsDateTime").val();
	var hasReceipt = $("#hasReceipt").val();
	
	$.ajax({
		url: '/paymentManager/queryNewReceiptNo',
		type: 'post',
		data: {hasReceipt: hasReceipt, paymentDate: billsDateTime, originalReceipNo: originalReceipNo, dateChangedFlag: dateChangedFlag, hasReceiptChangeFlag: hasReceiptChangeFlag},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#billsNum").val(response.newReceiptNo);
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});
}

var dateChangedFlag = false;	//票据日期是否已经改变标识
var hasReceiptChangeFlag = false;	//有无发票是否已经改变标识

//付款日期改变的时候
function receipDateChange() {
	if($("#paymentId").val() != ""){	//修改付款单时的情况
		var originalPaymentDate = $("#originalPaymentDate").val().substring(0, 7);	//取月份
		var currPaymentDate = $("#billsDateTime").val().substring(0, 7);	//取月份
		if (originalPaymentDate == currPaymentDate) {
			dateChangedFlag = false;
		} else {
			dateChangedFlag = true;
		}
	}
	getNewBillNum();
}

//是否有票状态改变时，重新获取票据编号
function chageHasReceipt(own){
	if($("#paymentId").val() != ""){	//修改付款单时的情况
		var originalHasReceipt = $("#originalHasReceipt").val();
		var currHasReceipt = $(own).val();
		if (currHasReceipt == originalHasReceipt) {
			hasReceiptChangeFlag = false;
		} else {
			hasReceiptChangeFlag = true;
		}
	}
	getNewBillNum();
	
	var $this = $(own);
	if($this.val() == "true"){
		$("#bottomOptionDiv").css("visibility", "visible");
		$("#billType").val(1);
		$("#ifReceiveBill").val("true");
	}else{
		$("#bottomOptionDiv").css("visibility", "hidden");
		$("#billType").val("");
		$("#ifReceiveBill").val(false);
		$("#remindTimeValue").val("");
		$("#remindTime").css("display","none");
	}
}


//拼接付款方式下拉列表
function dropBox(data, id, left, top){
	
	//清空下拉框里的所有子元素
	$(id).empty();
	if(data != null && data.length != 0){
		var _li = [];
		for(var i = 0; i < data.length; i++){
			_li.push('<li class="drop-down-li" title="'+ data[i] +'"><a href="javascript:void(0)">'+ data[i] +'</a></li>');
		}
		
		$(id).append($(_li.join("")));
			
	}
		
	$(id).css({"left":left,"top":top});
	
	
}



//选择收款人
function getReceiveingParty(own){
	
	own= $(own);
	$.ajax({
		url: '/getCostManager/queryDropDownData',
		type: 'post',
		data: {includePayment: true,includeCollection: false,includeLoan: false},
		datatype: 'json',
		success: function(response){
			
			if(response.success){
				aimPeople = response.aimPeople;
				if(aimPeople.length > 0){
					own.next("ul.dropdown_box").find("li").remove();
					$.each(aimPeople, function(i,value){
						own.next("ul.dropdown_box").append("<li class='drop-down-li' title='"+ value +"'><a href='javascript:void(0)'>" + value + "</a></li>");
					});
					own.next("ul.dropdown_box").css({left: own.position().left, top: own.position().top+own.outerHeight()}).show();
				}
				
				$("#receivingParty").off("blur");
				$("#receivingParty").keyup(function(){
				       var _this=$(this), _subList=_this.siblings('ul').children('li');
				       var a = false;
				       _subList.each(function(){
				    	   
				           if($(this).text().search($.trim(_this.val()))!=-1){
				        	   a = true;
				        	   _this.siblings("ul.dropdown_box").show();
				               $(this).show();
				           }else{
				               $(this).hide();
				           }
				       });
				       
				       if (!a) {
				    	   _this.siblings("ul.dropdown_box").hide();
				       }
				   }).on("blur", function(){
					   //查询借款人未还清借款列表
					   queryLoanMoneyInfo();
				   });
				
					 
					  $('.dropdown_box li').off('click');
					  $('.dropdown_box').on('click','li',function(ev){
					       var _this=$(this);
					       $('#receivingParty').val($(this).text());
					       //查询借款人未还清借款列表
					       queryLoanMoneyInfo();
					       $("#receivingParty").removeClass("worker-name-tips");
					       $(".worker-name-error-tips").css("display", "none");		        
					       _this.parent().hide();
					   });
				
			}else{
				parent.showErrorMessage(response);
			}
		}
	});
}


//查询借款人未还清借款信息
function queryLoanMoneyInfo(){
	var paymentId = $("#paymentId").val();
	var payeeName = $("#receivingParty").val();
	if (payeeName == "") {
		$("#loanLeftMoney").text("");
		$("#forLoanMoney").text("");
		$("#payLeftMoney").text("");
		return ;
	}
	
	$.ajax({
		url: '/loanInfoManager/queryLoanLeftMoney',
		type: 'post',
		data: {payeeName: payeeName, paymentId: paymentId},
	    datatype: 'json',
	    success: function(response){
	    	if (!response.success) {
	    		parent.showErrorMessage(response.message);
	    		return ;
	    	}
	    	
    		if(response.loanLeftMoney != ""){
    			$("#loanLeftMoney").text(response.loanLeftMoney);
//    			$("#loanLeftMoney").show();
    		} else {
    			$("#loanLeftMoney").text("");
    		}
    		
    		if (response.forLoanMoney != "") {
//    			$("#forLoanMoney").show();
    			$("#forLoanMoney").text(response.forLoanMoney);
    		} else {
    			$("#forLoanMoney").text("");
    		}
    		if (response.payLeftMoney != "") {
    			$("#payLeftMoney").text(response.payLeftMoney);
    		} else {
    			$("#payLeftMoney").text("");
    		}
    		$("#loanIds").val(response.loanIds);
	    }
	});
}

//加载财务科目列表
function loadFinanceSubjList(){
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
	 //存放每个input的id
	 var inputIndex=0;

	 $("#subjectTree").jqxTreeGrid({
		 theme: 'energyblue',
		 width: 260,
		 height:335,
		 source: dataAdapter,
	     
		 showHeader: false,
		 ready: function(){},
		    columns: [
		          { text: '财务科目', dataField: 'name', width: '100%', align: "center" }
		        ]
     });
	 
	 $('#subjectTree').on('rowSelect', function (event){

		 
		 var checkboxValue = $("input[type=checkbox]:checked").val() ? true : false;
		 

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

                         
				          if($("#contractId").attr("finsubjid") 
				        		  && $("#payContractMoney").is(":checked")
				        		  && row.id != $("#contractId").attr("finsubjid")) {
				        	//弹出提示
				 			 parent.popupPromptBox("提示","当前所选科目与合同科目不一致, 是否要更改 ？", function (){
			 				 
				 				  $("#"+inputIndex).val(name);
				 				  $("#"+inputIndex).next("input[type=hidden]").val(row.id);
				 				  $("#"+inputIndex).attr("fid",row.id);
				 				  /*$("#contractId").attr("finsubjid", row.id);*/
						          $('.fin_subj').css("display", "none");
						          $("span.select-flag").hide();
				 			 });
				          }else{

				        	  $("#"+inputIndex).val(name);
				        	  $("#"+inputIndex).attr("fid",row.id);
				        	  $("#"+inputIndex).next("input[type=hidden]").val(row.id);
				        	 /* $("#contractId").attr("finsubjid", row.id);*/
				        	  $("span.select-flag").hide();
					          $('.fin_subj').css("display", "none");
				          }
				          
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
		
		$("#filter").keydown(function(ev){
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
		});
		//阻止冒泡事件
		$("#levelPopup").on("click", function(ev){
			ev.stopPropagation();
		});
		
		var scrollTop = 0;
	    //滚动条事件
	    $("#loadPayMentBox").scroll(function(){
	    	//滚动条滚动的高度
	    	scrollTop = $("#loadPayMentBox").scrollTop();
	    });
		
	    //把事件绑定到父元素上，这样就应用到所有子元素了
	    $(".payment-detail-table").on("click", '.text-finance-subject', function(ev) {
			var obj = $(this);
			$(".payment-detail-table").find("span.select-flag").hide();
			var width = add(148, add(20, add(40,22)));//140:left值，20：flag的值，40:margin值,22:td-nth-child(1)
			var secondWidth = add(obj.parents("tr").find("input.summary").width(), 3);
			var left = add(width, secondWidth);
			
			obj.parent("div.td-div").find("span.select-flag").show();
			$('.fin_subj').css({"left": left}).show();
			
			$(".fin_subj #filter").focus();
			inputIndex=$(this).attr("id");
	        ev.stopPropagation();
	    });
}




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
					for(var i = 0; i < currencyInfoList.length; i++){
						html.push("<option value='" + currencyInfoList[i].id + "'>" + currencyInfoList[i].name+"-"+ currencyInfoList[i].code +"</option>");
						if (i == 0) {
							$("#selectedCurrencyValue").val(currencyInfoList[i].id);
						}
					}
					$("#currency").append($(html.join("")));
					if((nowCurrencyType != undefined) && (nowCurrencyType != "")){//当前币种不为空(删除一行后重新赋值币种信息)
						$("#selectedCurrencyValue").val(nowCurrencyType);
						$("#currency").val(nowCurrencyType);
					}
				}
				
			}
		}
	});
}


//添加一行
function additionTr(own){
	own = $(own);
	//禁止点击
	if(own.attr("disabled")== "disabled"){
		return;
	}
	//判断的是待付单的信息标识
	if($("#isContractToPaid").val() == "true"){
		return;
	}
	var allFinanceSubjTr = $('.payment-detail-table tr.add-sub-tr').find('.text-finance-subject');
	var financeSubjRows = allFinanceSubjTr.length;
	var nowSpan = allFinanceSubjTr[financeSubjRows-1];
	//财务科目文本框的id
	var oldId = nowSpan.id;
	var newId = parseInt(oldId)+1;
	
	
	var tr = own.parents("tr.add-sub-tr");
	var trCopy = tr.clone(true);
	
	//移除合并行单元格
	trCopy.find("select").parent("td").remove();
	trCopy.find(".text-finance-subject").attr("sid","financeSubjName"+newId);
	trCopy.find(".text-finance-subject").attr("class","text-finance-subject finance-subject"+newId);
	trCopy.find("input").eq(0).val("");
	trCopy.find("input").eq(1).val("");
	trCopy.find("input.money-td-input").val("");
	trCopy.find("input.no-format-money").val("");
	tr.after(trCopy);
	//每次添加一行财务科目都会重新赋值id
	$('tr.add-sub-tr .text-finance-subject').each(function(i){
		$(this).attr('id',i+1);
	});
}

//删除一行后将币种赋值为之前的币种
var nowCurrencyType;


//删除一行
function subtractionTr(own){
	own=$(own);
	var id = own.parents("tr.add-sub-tr").find("input[name=textFinanceSubj]").attr("id");
	//禁止点击
	if(own.attr("disabled") == "disabled"){
		return;
	}
	//判断的是待付单的信息标识
	if($("#isContractToPaid").val() == "true"){
		return;
	}
	var trLength = $("tr.add-sub-tr").length;
	
	if(trLength == 1){
		parent.showErrorMessage("最后一条不能删除");
		return;		
	}
	nowCurrencyType = $("#currency").val();
	own.parents("tr.add-sub-tr").remove();
//	if(own.parent("div").parent("td").parent("tr").find("input.text-finance-subject").attr("id")==1){
//		own.parent("div").parent("td").parent("tr").next("tr").remove();
//	}else{
//		own.parent("div").parent("td").parent("tr").remove();
//	}
	
	
	//删除一行后重新赋值id
    $('tr.add-sub-tr .text-finance-subject').each(function(i){
		
		$(this).attr('id',i+1);
	});
    
    if(id == 1){//判断删除的是否是第一行财务科目
    	var td = $("#1").parent("div").parent("td");
    	td.after('<td rowspan="500"><select class="select-currency" name="currency" id="currency" onchange="currencyChange(this)"></select><input type="hidden" id="selectedCurrencyValue" ></td>');
    	getcurrencyId();
    	$("#currency").val(nowCurrencyType);
    }
    
    
	//删除一行后，总金额的值需要变化
	var totalMoney = 0;

	$(".money-td-input").each(function(){
		var numberValue = 0;
		var myValue = $(this).val().replace(/,/g, "");
		
		if(myValue != "" && !isNaN(myValue)) {
			numberValue = parseFloat(myValue);
		}
		
		totalMoney= add(numberValue, totalMoney);
		$(this).next("input").val(numberValue);
	});

	$("#totalAccountMoney").text(numberToCapital(totalMoney));
	$("#readTotalMoney").prev("span").text(fmoney(totalMoney));
	$("#readTotalMoney").val(totalMoney);

}


//格式化金额输入,自动计算金额合计
function formatAmount(){
	//只允许输入数字和小数
	$(".money-td-input").keyup(function(){    
		$(this).val($(this).val().replace(/[^\-\d.]/g,""));  //清除“数字”和“.”“-”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
		//只保留第一个-
		$(this).val($(this).val().replace(/\-{2,}/g,"-"));
		$(this).val($(this).val().replace("-","$#$").replace(/\-/g,"").replace("$#$","-"));
	});
	
	//自动计算金额合计
	$(".money-td-input").on("blur", function(){
		
		var value = $(this).val();
		var totalMoney = 0.0;
		if(value ==""){
			return;
		}
		
			
			
			$(".money-td-input").each(function(){
				var numberValue = 0;
				var myValue = $(this).val().replace(/,/g, "");
				
				if(myValue != "" && !isNaN(myValue)) {
					numberValue = parseFloat(myValue);
				}
				
				totalMoney= add(numberValue, totalMoney);
				$(this).next("input").val(numberValue);
			});

			$("#totalAccountMoney").text(numberToCapital(totalMoney));
			$("#readTotalMoney").prev("span").text(fmoney(totalMoney));
			$("#readTotalMoney").val(totalMoney);
			

			$(this).val(fmoney(value));
	});
	
}






//是否显示提示时间
function isShowTimeTips(){
	$("#ifReceiveBill").on("change", function(){
		if($("#ifReceiveBill").val() == "false"){
			$("#remindTime").show();
		}else{
			$("#remindTime").hide();
		}
	});
}




//是否显示合同收款人列表
function showContractPayList(){
	var checkboxValue = $("#payContractMoney:checked").val() ? true : false;
		if(checkboxValue == true){
			$("#receivePersonDropdown").show();
			$("#receivingParty").attr("disabled", "disabled");
		}else{
			$("#receivePersonDropdown").hide();
			$("#contractId").val("");
			$("#contractType").val("");
			$("#contractNo").val("");
			$("#receivingParty").removeAttr("disabled");
		}
}




//加载付合同款下拉按鈕
function loadContractPayBtn(){
	
	$("#dropDownButton").jqxDropDownButton({
		theme: theme,
		width: 150, 
		height: 25,
		initContent: function(){}
	});
	$("#dropDownButton").jqxDropDownButton("setContent",'<div style="margin-top:4px; margin-left: 10px;">请选择合同收款人</div>');
	
	$("#contactPayTree").jqxTree({width:148});
	
		
}



//加载合同付款人列表以及查询支付情况
function aboutContractInfo(){
	$.ajax({
		url: '/contractManager/queryContractList',
		type: 'post',
		data: {name: $("#contactInputSearch").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				
				var contractList = response.contractList;
				
				if(contractList.length >0){
					var workerHtml = [];
					var actorHtml = [];
					var produceHtml = [];
					for(var i= 0; i< contractList.length; i++){
						if(contractList[i].contractType == 1){

							workerHtml.push("  <tr>");
							workerHtml.push("  <td cid='"+ contractList[i].contractId +"' ctype='1' contractNo='"+ contractList[i].contractNo +"' onclick='contractWin(this)' currId='"+ contractList[i].currencyId+"' finId='"+ contractList[i].financeSubjId+"'>" + contractList[i].name + "</td><input type='hidden' value='"+ contractList[i].financeSubjName +"'>");
							workerHtml.push("  </tr>");

						}
						if(contractList[i].contractType == 2){

							actorHtml.push("  <tr>");
							actorHtml.push("  <td cid='"+ contractList[i].contractId +"' ctype='2' contractNo='"+ contractList[i].contractNo +"' onclick='contractWin(this)' currId='"+ contractList[i].currencyId+"' finId='"+ contractList[i].financeSubjId+"'>" + contractList[i].name + "</td><input type='hidden' value='"+ contractList[i].financeSubjName +"'>");
							actorHtml.push("  </tr>");

						}
						if(contractList[i].contractType == 3){
						
							produceHtml.push("  <tr>");
							produceHtml.push("  <td cid='"+ contractList[i].contractId +"' ctype='3' contractNo='"+ contractList[i].contractNo +"' onclick='contractWin(this)' currId='"+ contractList[i].currencyId+"' finId='"+ contractList[i].financeSubjId+"'>" + contractList[i].name + "</td><input type='hidden' value='"+ contractList[i].financeSubjName +"'>");
							produceHtml.push("  </tr>");
						
						}
					}
					
					workerHtml= workerHtml.join("");
					actorHtml = actorHtml.join("");
					produceHtml = produceHtml.join("");
                    if(workerHtml == ""){
                    	$(".worker-contract-table").empty().append("<tr><td>暂无数据</td></tr>");
					}else{
						$(".worker-contract-table").empty().append(workerHtml);
					}
                    if(actorHtml == ""){
                    	$(".actor-contract-table").empty().append("<tr><td>暂无数据</td></tr>");
                    }else{
                    	$(".actor-contract-table").empty().append(actorHtml);
                    }
                    if(produceHtml == ""){
                    	$(".produce-contract-table").empty().append("<tr><td>暂无数据</td></tr>");
                    }else{
                    	$(".produce-contract-table").empty().append(produceHtml);
                    }
			
				}else{
					var workerHtml = [];
					var actorHtml = [];
					var produceHtml = [];
					workerHtml.push("<tr>");
					workerHtml.push("<td>暂无数据</td>");
					workerHtml.push("</tr>");
					workerHtml= workerHtml.join("");
					$(".worker-contract-table").empty().append(workerHtml);
					actorHtml.push("<tr>");
					actorHtml.push("<td>暂无数据</td>");
					actorHtml.push("</tr>");
					actorHtml=actorHtml.join("");
					$(".actor-contract-table").empty().append(actorHtml);
					produceHtml.push("<tr>");
					produceHtml.push("<td>暂无数据</td>");
					produceHtml.push("</tr>");
					produceHtml= produceHtml.join("");
					$(".produce-contract-table").empty().append(produceHtml);
				}
				
				
				
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});

	
}

//下拉菜单的展开收起事件
function showDropList(own){
	own= $(own);
	if(own.hasClass("open")){
		own.removeClass("open");
		own.next().slideUp("200");
	}else{
		own.addClass("open");
		own.next().slideDown("200");
	}
	
}

//显示合同支付明细弹窗
function contractWin(own){
	var currencyId = $(own).attr("currId");
	var loanIds = $("#loanIds").val();
	var contractNo = $(own).attr('contractNo');
//	$('#contractNo').val(contractNo);
//	var paymentId = $("#paymentId").val();
	var currency = $("#currency").val();
	if (currencyId != currency && loanIds != "") {
		parent.showErrorMessage("该合同的币种和已选借款单币种不一致，请重新设置");
		return;
	}
	
	
	
	$("#dropDownButton").jqxDropDownButton("setContent",'<div style="margin-top:4px; margin-left: 10px;">'+ own.innerText +'</div>');
	$("#dropDownButton").jqxDropDownButton('close');
	//	$(".paymethod-dropdown-box").hide();
	parent.contractWin(own);
	return;
}

//下拉按钮的搜索文本框
//文本框搜索
function searchName(event){
	if(event.keyCode == 13){
		aboutContractInfo();
		if($("#contactInputSearch").val()==""){
			$("#contactPayTree dt").removeClass("open");
			$("#contactPayTree dt").next().slideUp("10");
		}else{
			$("#contactPayTree dt").addClass("open");
			$("#contactPayTree dt").next().slideDown("10");
		}
	}
}


//初始化合同联系人弹窗
//function initContractPopupWin(){
//	$("#payedContractMoneyWin").jqxWindow({
//		theme: theme,
//		height: 520,
//		width: 710,
//		maxWidth: 800,
//		maxHeight: 600,
//		resizable: false,
//		isModal: true,
//		autoOpen: false,
//		initContent: function(){}
//	});
//}

// 获取付款方式列表
function getPayMethodList(own){
	$.ajax({
		url: '/financePaymentWay/queryPaywayNameList',
		type: 'get',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.wayNameList != null){
					 //设置默认值
					$("#paymentWay").val(response.wayNameList[0]);
					
					$("#paymentWay").click(function(ev){
						dropBox(response.wayNameList, '.paymethod-dropdown-box','57px', '21px');
						$('.payment-table .paymethod-dropdown-box').show();
					   	ev.stopPropagation();//阻止冒泡事件
				    });
				}
				
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});
	// 输入查询
	 $("#paymentWay").keyup(function(){

		 var _this=$(this), _subList=_this.siblings('ul').children('li');
	       var a = false;
	       _subList.each(function(){
	    	   
	           if($(this).text().search($.trim(_this.val()))!=-1){
	        	   a = true;
	        	   _this.siblings("ul.paymethod-dropdown-box").show();
	               $(this).show();
	           }else{
	               $(this).hide();
	           }
	       });
	       
	       if (!a) {
	    	   _this.siblings("ul.paymethod-dropdown-box").hide();
	       }
	   });
	 
		 
	 $('.dropdown-box li').off('click');
	 $('.paymethod-dropdown-box').on('click','li',function(ev){
		       var _this=$(this);
		       $('#paymentWay').val($(this).text());
		               
		       _this.parent().hide();
	  });
	
}

//获取记账人
function getAccounting(){
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'get',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var realName = response.userInfo.realName;
				$("#agent").val(realName);
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});
}



//显示选择借款单窗口
function showSelectLoanOrder(){
	parent.showSelectLoanOrder();
}

//初始化选择借款单窗口
//function initSelectLoanOrder(){
//	$("#queryNotPayedLoanList").jqxWindow({
//		theme:theme,
//		height: 500,
//		width: 710,
//		maxWidth: 800,
//		maxHeight: 600,
//		resizable: false,
//		isModal: true,
//		autoOpen: false
//	});
//}


//加载借款单详细信息
//function loadLoanDetail(){
//	debugger;
//	var paymentId = $("#paymentId").val();
//	var source = {
//		url: '/loanInfoManager/queryNotPayedLoanList',
//		type: 'post',
//		data: {payeeName: $("#receivingParty").val(), paymentId: paymentId},
//		datatype: 'json',
//		datafields : [
//		 		     {name: "loanId", type: "string"},
//		 		     {name: "loanDate", type: "string"},
//		 		     {name: "receiptNo", type: "string"},
//		 		     {name: "currencyId", type: "string"},
//		 		     {name: "financeSubjId", type: "string"},
//		 		     {name: "financeSubjName", type: "string"},
//		 		     {name: "currencyCode", type: "string"},
//		 		     {name: "exchangeRate", type: "double"},
//		 		     {name: "money", type: "double"},
//		 		     {name: "summary", type: "string"},
//		 		     {name: "payedMoney", type: "double"},
//		 		],
//		 root: 'loanInfoList'
//	};
//	
//	
//	var dataAdapter = new $.jqx.dataAdapter(source);
//	
//	var checkboxRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//	
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-center'></div>";
//			
//		}else{
//			html = "<div class='jqx-column align-center'><input class='jqx-column-checkbox' type='checkbox' curr='"+ rowdata.currencyId +"' name='"+ rowdata.loanId +"' receiptNo='"+ rowdata.receiptNo +"' rowIndex='checkbox"+ row +"' money='"+ rowdata.money+ "' payedMoney='"+ rowdata.payedMoney +"' fid='"+ rowdata.financeSubjId +"' fname='" + rowdata.financeSubjName + "' onclick='inputClickEvent(this)'></div>";
//			
//		}	
//		    
//			return html;
//		
//	};
//	var receipNoRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-center disable-color'>" + rowdata.receiptNo + "</div>";
//		}else{
//			html = "<div class='jqx-column align-center enable-color'>" + rowdata.receiptNo + "</div>";
//		}
//		return html;
//	};
//	var loanDateRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-center disable-color'>" + rowdata.loanDate + "</div>";
//		}else{
//			html = "<div class='jqx-column align-center enable-color'>" + rowdata.loanDate + "</div>";
//		}
//		return html;
//	};
//	var summaryRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-left disable-color loanSummary'>" + rowdata.summary + "</div>";
//		}else{
//			html = "<div class='jqx-column align-left enable-color loanSummary'>" + rowdata.summary + "</div>";
//		}
//		return html;
//	};
//	var moneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-right disable-color money-cell'>" + fmoney(rowdata.money, 2) + "</div>";
//		}else{
//			html = "<div class='jqx-column align-right enable-color money-cell'>" + fmoney(rowdata.money, 2) + "</div>";
//		}
//		return html;
//	};
//	var paymoneyRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-right disable-color payedmoney-cell'>" + fmoney(rowdata.payedMoney, 2) + "</div>";
//		}else{
//			html = "<div class='jqx-column align-right enable-color payedmoney-cell'>" + fmoney(rowdata.payedMoney, 2) + "</div><input type='hidden' value='"+ rowdata.payedMoney +"'>";
//		}
//		return html;
//	};
//	var balanceRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
//		var currency = $("#currency").find("option:selected").text();
//		var currencyCode = currency.split("-")[1];
//		
//		var html;
//		var balanceMoney = fmoney(rowdata.money-rowdata.payedMoney);
//		if(rowdata.currencyCode != currencyCode){
//			html = "<div class='jqx-column align-right disable-color banlance-cell'>" + balanceMoney + "</div>";
//		}else{
//			html = "<div class='jqx-column align-right enable-color banlance-cell'>" + balanceMoney + "</div>";
//		}
//		return html;
//	};
//	
//	$("#queryLoanList").jqxGrid({
//		width: "100%",
//		height: "100%",
//		source: dataAdapter,
////		selectionmode: "checkbox",
////		localization: localizationobj,
//		columns: [
//					{text: "", cellsrenderer: checkboxRenderer, width: '6%', cellsAlign: 'center', align: 'center'},
//					{text: "票据编号", datafield: 'receiptNo', cellsrenderer:receipNoRenderer, width: '16%', cellsAlign: 'center', align: 'center'},
//					{text: "日期", datafield: 'loanDate', cellsrenderer: loanDateRenderer, width: '16%', cellsAlign: 'center', align: 'center'},
//					{text: "摘要", datafield: 'summary', cellsrenderer: summaryRenderer, width: '18%', cellsAlign: 'left', align: 'center'},
//					{text: "金额", datafield: 'money', cellsrenderer: moneyRenderer, width: '14%', cellsAlign: 'right', align: 'center'},
//					{text: "报销/还款金额", datafield: 'payedMoney', cellsrenderer: paymoneyRenderer, width: '16%', cellsAlign: 'right', align: 'center'},
//					{text: "余额", cellsrenderer: balanceRenderer, width: '14%', cellsAlign: 'right', align: 'center'},
//				],
//		 cellhover: function(e) {
//					/*var $this = $(e);
//					$this.attr("title",$this.find("div.jqx-column").text());	*/		
//		 }
//		 
//	});
//	
//}
				 

//还借款
function queryNotPayedLoanList(){
	
}

//存放选择的借款单的id
var saveLoanIds = "";


//该段代码没用
//选择借款单的单击事件
function inputClickEvent(own){
	
    /*publicPayedMoney存放报销总金额
     * payedMoney将要报销的钱
     *oldPayedMoney已经报销的钱
     *nowPayedMoney现应该报销的钱
     *money总借款金额
     *balance报销后所剩余额 
     *overplusPayedMoney剩余报销金额*/

	own = $(own);
	
	var payedMoney = own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text().replace(/,/g,"")-0;	//报销/还款金额
	var staticPayedMoney = own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").next("input").val();	//原来已报销/还款金额
	var money = own.attr("money").replace(/,/g,"")-0;	//总借款金额

	var flag= own.is(':checked');
	
	if(flag){//文本框选中时
		if (publicPayedMoney <= 0) {
			showErrorMessage("已勾选的借款单已足额抵扣付款");
			event.preventDefault();
			return ;
		}
		saveLoanIds +=own.attr("name")+",";
		 //现应还余额
		 var nowBalance = subtract(subtract(money, publicPayedMoney), payedMoney);
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
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.payedmoney-cell").text(fmoney(staticPayedMoney, 2));
		 own.parent("div.jqx-column").parent("div[role=gridcell]").parent("div[role=row]").find("div.banlance-cell").text(fmoney(subtract(money, staticPayedMoney), 2));
		
		 publicPayedMoney = subtract(add(publicPayedMoney, payedMoney), staticPayedMoney);
		 
		 saveLoanIds = saveLoanIds.replace(own.attr("name")+",",'');
   }
	
	//补领金额
	$("#balanceTips").text(fmoney(publicPayedMoney));
	
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
//function reimbursementLoanOrder(){
//	var loanIds= "";
//	var currency = "";
//	$("#queryNotPayedLoanList input[type=checkbox]:checked").each(function(i){
//		/*var loanSummary = $(this).parents("div[role=row]").find(".loanSummary").text();
//		var financeSubjId = $(this).attr("fid");
//		var financeSubjName = $(this).attr("fname");
//		if(i == 0){
//			
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(0).val(loanSummary);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).val(financeSubjName);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("fid", financeSubjId);
//		}else{
//			var trCopy= $("tr.add-sub-tr").eq(i-1).clone(true);
//			var id = i+1;
//			trCopy.find("#currency").parent("td").remove();
//			trCopy.attr("id", 'tr'+id);
//			
//			$("tr.add-sub-tr").eq(i-1).after(trCopy);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(0).val(loanSummary);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).val(financeSubjName);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("id", id);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("fid", financeSubjId);
//			$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).next("input[type=hidden]").val(financeSubjId);
//		}*/
//		if(i == 0){
//			 currency = $(this).attr("curr");
//		}
//		//选择的报销的借款单的Id
////		var myLoanId= $(this).attr("name");
////		loanIds= loanIds+myLoanId+",";
//		
//	});
//	if (saveLoanIds == null || saveLoanIds == "") {
//		popupPromptBox("提示", "当前付款未关联任何借款单，是否确定退出？", function() {
//			$("#queryNotPayedLoanList").jqxWindow("close");
//		});
//	} else {
//		saveLoanIds = saveLoanIds.replace(/^,*|,*$/g,'');
//		$("#loanIds").val(saveLoanIds);
//		$("#currency").val(currency);
//		$("#queryNotPayedLoanList").jqxWindow("close");
//	}
//}
//取消选择借款单按钮
function cancelSelectLoanOrder(){
	/*$("#loanOrderId").html("");
	$("#payedMoneyCount").html("");*/
	$("#queryNotPayedLoanList").jqxWindow("close");
}

	


function loadPaymentOrderInfo(){
	$.ajax({
		url: '/paymentManager/queryPaymentDetail',
		type: 'post',
		data: {paymentId: $("#paymentId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var paymentInfo= response.paymentInfo;
				$("#originalReceipNo").val(paymentInfo.receiptNo);
				$("#originalHasReceipt").val(paymentInfo.hasReceipt);
				$("#originalPaymentDate").val(paymentInfo.paymentDate);
				
				$("#billsNum").val(paymentInfo.receiptNo);
				$("#billsDateTime").val(paymentInfo.paymentDate);
				$("#receivingParty").val(paymentInfo.payeeName);
				$('#contractNo').val(response.contractNo);
				$("#attachmentPcketId").val(response.attachmentPacketId);
				if(paymentInfo.contractId != ""){
					$("#payContractMoney").prop("checked", true);
					$("#receivePersonDropdown").show();
					$("#receivingParty").attr("disabled", "disabled");
					$("#dropDownButton").jqxDropDownButton("setContent",'<div style="margin-top:4px; margin-left: 10px;">'+ response.aimPeopleName +'</div>');
					$("#contractId").attr("finsubjid", paymentInfo.contractFinanSubjId);
				}
				$("#readTotalMoney").val(paymentInfo.totalMoney);
				$("#formatTotalMoney").text(fmoney(paymentInfo.totalMoney));
				$("#totalAccountMoney").text(numberToCapital(paymentInfo.totalMoney));
				$("#hasReceipt").val(paymentInfo.hasReceipt + "");
				$("#departmentText").val(paymentInfo.department);
				if(paymentInfo.hasReceipt == false){
					$("#bottomOptionDiv").css("visibility", "hidden");
					$("#billType").val("");
					$("#remindTimeValue").val("");
					$("#remindTime").hide();
				}else{
					$("#bottomOptionDiv").css("visibility", "visible");
					if(paymentInfo.billType == null || paymentInfo.billType == ""){
						$("#billType").val(null);
					}else{
						$("#billType").val(paymentInfo.billType);
					}
					if(paymentInfo.ifReceiveBill == false){
						$("#remindTime").show();
						$("#remindTimeValue").val(paymentInfo.remindTime);
					}else{
						$("#remindTimeValue").val("");
						$("#remindTime").hide();
					}
				}
				$("#billCount").val(paymentInfo.billCount);
				$("#paymentWay").val(paymentInfo.paymentWay);
				$("#paymentStatus").val(paymentInfo.status);
				$("#agent").val(paymentInfo.agent);
				$("#ifReceiveBill").val(paymentInfo.ifReceiveBill+"");
				$("#currency").val(paymentInfo.currencyId);
				$("#selectedCurrencyValue").val(paymentInfo.currencyId);
//				$("#loanIds").val(paymentInfo.loanIds);
				$("#contractId").val(paymentInfo.contractId);
				$("#contractType").val(paymentInfo.contractType);
				//单据与财务科目的联系
				var paymentFinanSubjMapList = response.paymentFinanSubjMapList;
				 
				
				for( var i= 0; i< paymentFinanSubjMapList.length; i++){
					
					if(i == 0){
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(0).val(paymentFinanSubjMapList[i].summary);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).val(paymentFinanSubjMapList[i].financeSubjName);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("fid", paymentFinanSubjMapList[i].financeSubjId);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).next("input[type=hidden]").val(financeSubjId);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(2).val(fmoney(paymentFinanSubjMapList[i].money));
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(2).next("input[type=hidden]").val(paymentFinanSubjMapList[i].money);
					}else{
						
						var trCopy= $("tr.add-sub-tr").eq(i-1).clone(true);
						var id = i+1;
						trCopy.find("#currency").parent("td").remove();
						trCopy.attr("id", 'tr'+id);
						
						$("tr.add-sub-tr").eq(i-1).after(trCopy);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(0).val(paymentFinanSubjMapList[i].summary);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).val(paymentFinanSubjMapList[i].financeSubjName);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("id", id);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).attr("fid", paymentFinanSubjMapList[i].financeSubjId);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(1).next("input[type=hidden]").val(financeSubjId);
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(2).val(fmoney(paymentFinanSubjMapList[i].money));
						$(".add-sub-tr").eq(i).find("input[type=text]").eq(2).next("input[type=hidden]").val(paymentFinanSubjMapList[i].money);
					}
						
					
				}
				//初始化显示按钮
				parent.$("#paymentOrderSettleBtn").show();
				parent.$("#saveModifyAfterPayment").show();
				parent.$("#deleteModifyPayment").show();
				parent.$("#paymentOrderSettleBtn").attr("status", 0);
				if (paymentInfo.status== 1) {//如果已经结算
					parent.$("#paymentOrderSettleBtn").hide();
					parent.$("#deleteModifyPayment").hide();
					parent.$("#paymentOrderSettleBtn").attr("status", 1);
					
				}
				if (isRunningAccountReadonly) {
					
					//隐藏按钮，同时禁用点击事件
					parent.$("#saveModifyAfterPayment").hide();
					parent.$("#paymentOrderSettleBtn").hide();
					parent.$("#deleteModifyPayment").hide();
					$(".addition").attr("disabled", true);
					$(".subtraction").attr("disabled", true);
					
					//所有置为不可用状态
					$("#receivingParty").attr("disabled", true);
					$("#billsDateTime").attr("disabled", true);
					$(".add-sub-tr").children().find("input[type=text]").attr("disabled", true);
					$("#paymentWay").attr("disabled", true);
					$("#agent").attr("disabled", true);
					$("#currency").attr("disabled", true);
					$("#hasReceipt").attr("disabled", true);
					$("#billCount").attr("disabled", true);
					$("#billType").attr("disabled", true);
					$("#ifReceiveBill").attr("disabled", true);
					$("#payContractMoney").attr("disabled", true);
					$("#remindTimeValue").attr("disabled", true);
					$("#repayLoans").attr("disabled",true);
					$('#dropDownButton').jqxDropDownButton({disabled: true });
				}
				if (!modifySettledPayment && paymentInfo.status== 1) {
					//隐藏按钮，同时禁用点击事件
					parent.$("#saveModifyAfterPayment").hide();
					parent.$("#deleteModifyPayment").hide();
					parent.$("#paymentOrderSettleBtn").hide();
					$(".addition").attr("disabled", true);
					$(".subtraction").attr("disabled", true);
					//所有置为不可用状态
					$("#receivingParty").attr("disabled", true);
					$("#billsDateTime").attr("disabled", true);
					$(".add-sub-tr").children().find("input[type=text]").attr("disabled", true);
					$("#paymentWay").attr("disabled", true);
					$("#agent").attr("disabled", true);
					$("#currency").attr("disabled", true);
					$("#hasReceipt").attr("disabled", true);
					$("#billCount").attr("disabled", true);
					$("#billType").attr("disabled", true);
					$("#ifReceiveBill").attr("disabled", true);
					$("#payContractMoney").attr("disabled", true);
					$("#remindTimeValue").attr("disabled", true);
					$("#repayLoans").attr("disabled",true);
					$('#dropDownButton').jqxDropDownButton({disabled: true });
				}
				
				
				//如果是待付清单
				if ($("#isContractToPaid").val() == "true") {
					$(".addition").attr("disabled", true);
					$(".subtraction").attr("disabled", true);
					$("#billsDateTime").attr("disabled", true);
					$("#receivingParty").attr("disabled", true);
					$("#currency").attr("disabled", true);
					$("#payContractMoney").attr("disabled", true);
					$(".add-sub-tr").children().find("input[type=text]").attr("disabled", true);
					
					$('#dropDownButton').jqxDropDownButton({disabled: true});
//					$("#repayLoans").hide();
					$(".loan-info-div").hide();
				}
				
				
				//查询借款信息
				if (paymentInfo.loanIds != "" && paymentInfo.loanIds != null) {
					queryLoanMoneyInfo();
				}
				
//				var main = $(window.parent.document).find("#fullReceiptDiv");
//				var mainheight = $(".payment-box-dl").height()+20;
//				main.height(mainheight );	
				
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//全选
function checkedAll(){
	//全选
		var checkboxLength = 0;
		$("#payInfoTable tbody :checkbox").each(function(){
			checkboxLength ++;
		});
		if(checkboxLength == 0){
			//如果可勾选的个数为零，全选按钮不可选；
			ev.preventDefault();
		}else{
			if($("#checkedAll").is(":checked")){
				$(".input-check").each(function(){
					$(this).prop("checked", true);
				});
			}else{
				$(".input-check").each(function(){
					$(this).prop("checked", false);
				});
			}
		}
		
}


//判断是否是全选
function isCheckAll(){
	
	var _tableObj = $("#payInfoTable tbody");
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






//设置下拉按钮的内容
function setDorpDownContent(content){
	$("#payContractMoney").prop("checked", true);
	$("#receivePersonDropdown").show();
	$("#receivingParty").attr("disabled", "disabled");
	$("#dropDownButton").jqxDropDownButton("setContent",'<div style="margin-top:4px; margin-left: 10px;">'+ content +'</div>');
	$("#dropDownButton").jqxDropDownButton({disabled: true });
}

//币种改变的时候
function currencyChange(own) {
	var nowValue = $(own).val();
	var preValue = $("#selectedCurrencyValue").val();
	var loanIds = $("#loanIds").val();
	var paymentId = $("#paymentId").val();

	$("#currency").val(preValue);
	if (loanIds != "") {
		parent.popupPromptBox("提示", "您已关联了借款单，改变币种将会导致撤销关联，是否继续？", function() {
			$("#loanIds").val("");
			if (paymentId != "") {
				$.ajax({
					url: "/paymentManager/deleteLoanRelate",
					data: {paymentId: paymentId},
					dataType: "json",
					async: false,
					success: function(response) {
						if (!response.success) {
							parent.showErrorMessage(response.message);
							return;
						}
					}
				});
			}
			$("#currency").val(nowValue);
			$("#selectedCurrencyValue").val(nowValue);
			queryLoanMoneyInfo();
		});
	} else {
		$("#currency").val(nowValue);
		$("#selectedCurrencyValue").val(nowValue);
	}
}


//票据种类变化的时候
function billTypeChange(own) {
	parent.billTypeChange(own);
}

//初始化当前剧组的部门列表
function initDepartmentSelect() {
	$.ajax({
		url: '/carManager/queryCrewDepartmentList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var departmentList = response.departmentList;
				var departmentArr = [];
				//添加部门下拉框
				for(var i =0; i < departmentList.length; i++){
					departmentArr.push("<li value="+ departmentList[i].roleName + " onclick='selectDepartment(this,event)'>" + departmentList[i].roleName + "</li>");
				}
				$("#department").append(departmentArr.join(''));
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//显示部门列表
function showDepartment(ev) {
	$("#department").show();
	ev.stopPropagation();
}

//选择部门
function selectDepartment(own, ev) {
	//取出选中的值
	var department = $(own).text();
	//对文本框赋值
	$("#departmentText").val(department);
	//关闭下拉框
	$("#department").hide();
	ev.stopPropagation();
}

var inputIndex;
//检索部门
function searchDepartmentName(own){
	var $this = $(own);
	inputIndex = $(own);
	if($this.val() != ""){
		var _subList = $("#department").children("li");
		_subList.hide();
		var searchFlag = false;
		$.each(_subList, function(){
			if($(this).text().search($.trim($this.val()))!=-1){
    	   searchFlag = true;
           $(this).show();
           $("#department").show();
       }else{
    	
           $(this).hide();
//           $("#searchPersonList").hide();
       }
		});
		
		if (!searchFlag) {
			$("#department").hide();
	    }
	}else{
		$("#department").show();
		$.each($("#department").children("li"), function(){
           $(this).show();
		});
	}
}