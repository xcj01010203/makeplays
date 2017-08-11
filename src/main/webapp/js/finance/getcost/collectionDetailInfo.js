$(document).ready(function(){
	//首先清空collectionId的值
	/*$("#collectionId").val("");*/
	//校验是否需要财务密码
	/*checkNeedFinancePwd();*/
	
	//获取当前日期
	getNowFormatDate();
	//获取新的收款单编号
	if($("#collectionId").val() == ""){
		getNewCollectionNum();
	}
	//获取货币列表
	getcurrencyId();
	//获取前付款方式列表
	getPayMethodList();
	//输入金额限制,自动转换大写金额
	formatAmount();
	//关闭下拉列表
	$(document).click(function(){    	
    	$('.dropdown_box').hide();	
    	$('.paymethod-dropdown-box').hide();
    });
	
	//判断是增加收款单还是修改收款单
	if($("#collectionId").val() != ""){
		loadCollectionOrderInfo();
	} else {
		//获取记账人信息
		getAccounting();
	}
	
});

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



//获取新的收款单编号
function getNewCollectionNum(){
	var originalReceipNo = $("#originalReceipNo").val();
	if($("#collectionId").val() != ""){
		var originalCollectionDate = $("#originalCollectionDate").val().substring(0, 7);
		var currCollectionDate = $("#billsDateTime").val().substring(0, 7);
		if (originalCollectionDate == currCollectionDate) {
			$("#billsNum").val(originalReceipNo);
			return;
		}
	}
	
	$.ajax({
		url: '/collectionManager/queryNewReceiptNo',
		type: 'post',
		data: {collectionDate: $("#billsDateTime").val(), originalReceipNo: originalReceipNo},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var newReceiptNo = response.newReceiptNo;
				$("#billsNum").val(newReceiptNo);
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
		
	});
}






//拼接付款人下拉列表
function dropBox(data, id, left, top){
	//清空下拉框里的所有子元素
	$(id).empty();
	if(data != null && data.length != 0){
		var _li = [];
		for(var i = 0; i < data.length; i++){
			_li.push('<li class="drop-down-li"><a href="javascript:void(0)">'+ data[i] +'</a></li>');
		}
		
		$(id).append($(_li.join("")));
	}
	$(id).css({"left":left,"top":top});
}



//选择付款人
function getPayNameList(own){
	own= $(own);
	$.ajax({
		url: '/getCostManager/queryDropDownData',
		type: 'post',
		data: {includePayment: false,includeCollection: true,includeLoan: false},
		datatype: 'json',
		success: function(response){
			
			if(response.success){
				aimPeople = response.aimPeople;
				if(aimPeople.length > 0){
					own.next("ul.dropdown_box").find("li").remove();
					$.each(aimPeople, function(i,value){
						own.next("ul.dropdown_box").append("<li class='drop-down-li' title='" + value + "'><a href='javascript:void(0)'>" + value + "</a></li>");
					});
					own.next("ul.dropdown_box").css({left: own.position().left, top: own.position().top+own.outerHeight()}).show();
				}
				
				$("#payMoneyParty").keyup(function(){
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
				   });
					 
				$('.dropdown_box li').off('click');
				$('.dropdown_box').on('click','li',function(ev){
					       var _this=$(this);
					       $('#payMoneyParty').val($(this).text());        
					       _this.parent().hide();
				});
				
			}else{
				showErrorMessage(response);
			}
		}
	});
}


//获取货币列表
function getcurrencyId(){
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		data: {ifEnable: true, ifStandard: ''},
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var currencyInfoList = response.currencyInfoList;
				var html = [];
				for(var i = 0; i < currencyInfoList.length; i++){
					html.push("<option value='" + currencyInfoList[i].id + "'>" + currencyInfoList[i].name+"-"+ currencyInfoList[i].code +"</option>");
				}
				$("#currency").append($(html.join("")));
				
			}
		}
	});
}

//获取付款方式列表
function getPayMethodList(){
	$.ajax({
		url: 'financePaymentWay/queryPaywayNameList',
		type: 'get',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var obj = $("#paymentWay");
				var left = obj.position().left;
				var top = obj.position().top-84;
				if(response.wayNameList.length > 0){
					
					 //设置默认值
					/*$("#paymentWay").val($(".paymethod-dropdown-box li:nth-child(1)").text());*/
					$("#paymentWay").val(response.wayNameList[0]);
					
					$("#paymentWay").click(function(ev){
						dropBox(response.wayNameList, '.paymethod-dropdown-box',left, top);
						$('.collection-table .paymethod-dropdown-box').show();
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
	 
		 
	 $('ul.paymethod-dropdown-box li').off('click');
	 $('ul.paymethod-dropdown-box').on('click','li',function(ev){
		       var _this=$(this);
		       $('#paymentWay').val($(this).text());
		               
		       _this.parent().hide();
	  });
}




//输入金额限制,自动转换大写金额
function formatAmount(){

	$("#money").on("keyup", function(){
		$(this).val($(this).val().replace(/[^\-\d.]/g,""));  //清除“数字”和“.”“-”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
		//只保留第一个-
		$(this).val($(this).val().replace(/\-{2,}/g,"-"));
		$(this).val($(this).val().replace("-","$#$").replace(/\-/g,"").replace("$#$","-"));
	}).on("blur",function(){
		
		var money = $(this).val().replace(/,/g,"")-0;
		$(this).next("input[type=hidden]").val(money);
		if(money != ""){
			$(this).val(fmoney(money));
			$("#capitalAccountMoney").text("(大写)"+numberToCapital(money));
		}else{
			$("#capitalAccountMoney").text("");
		}
	});
}

//获取记账人信息
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



//修改付款单-获取付款单信息
function loadCollectionOrderInfo(){
	$.ajax({
		url: '/collectionManager/queryCollectionDetailInfo',
		type: 'post',
		data: {collectionId: $("#collectionId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var collectionInfo = response.collectionInfo;
				var attachmentList = response.attachmentList;//附件列表
				if(collectionInfo != null){
					
					$("#billsNum").val(collectionInfo.receiptNo);
					$("#billsDateTime").val(collectionInfo.collectionDate);
					$("#payMoneyParty").val(collectionInfo.otherUnit);
					$("#summary").val(collectionInfo.summary);
					$("#currency").val(collectionInfo.currencyId);
					$("#money").val(fmoney(collectionInfo.money));
					$("#money").next("input[type=hidden]").val(collectionInfo.money);
					$("#paymentWay").val(collectionInfo.paymentWay);
					$("#capitalAccountMoney").text("(大写)"+numberToCapital(collectionInfo.money));
					$("#agent").val(collectionInfo.agent);
					
					$("#originalReceipNo").val(collectionInfo.receiptNo);
					$("#originalCollectionDate").val(collectionInfo.collectionDate);
					
					//权限设置
					if (isRunningAccountReadonly){
						$("#saveModifyAfterCollection").hide();
						$("#payMoneyParty").attr("disabled", true);
						$("#summary").attr("disabled", true);
						$("#money").attr("disabled", true);
						$("#billsDateTime").attr("disabled", true);
						$("#currency").attr("disabled", true);
						$("#money").attr("disabled", true);
						$("#paymentWay").attr("disabled", true);
						$("#agent").attr("disabled", true);
					}
					
					
				}
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


