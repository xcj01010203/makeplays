$(document).ready(function(){
	
	//校验是否进行了单据设置，如果没有，跳转到单据设置页面
	checkReceiptHasSetted();
    initValidUserWin();
    //初始化付款单附件上传
    showUpLoadFileList();
    //初始化收款单附件上传
//    initCollectionUpload();
    //初始化借款单附件上传
    initLoanUploader();
	//校验是否需要财务密码
	checkNeedFinancePwd();
    //从打印页面返回的判断
    if($("#receiptType").val() == "1"){
    	paymentOrderTab();
    	//初始化付款单的上传插件（隐藏的元素不能进行初始化）
    	showUpLoadFileList();
    } else if($("#receiptType").val() == "2"){
    	collectionOrderTab();
    	//初始化收款单的上传插件（隐藏的元素不能进行初始化）
//    	initCollectionUpload();
		$("#receiptType").val("");
    } else if($("#receiptType").val() == "3"){
    	loanOrderTab();
    	//初始化借款单的上传插件（隐藏的元素不能进行初始化）
    	initLoanUploader();
    	$("#receiptType").val("");
    } else {
    	paymentOrderTab();
    	//初始化付款单的上传插件（隐藏的元素不能进行初始化）
    	showUpLoadFileList();
    }
    
    
    
  //初始化选择借款单窗口
  initSelectLoanOrder();
  //初始化合同联系人弹窗
  initContractPopupWin();
  
});
//付款单
function paymentOrderTab(){
	if (paymentUploader.getFiles('inited').length != 0){
		var files = paymentUploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			paymentUploader.removeFile(files[i].id, true);
		}
		$("#showAttachmentFilePay").empty();
	
    }
//	//判断是否有待上传的文件(收款单和借款单)
//	if(collectionUploader.getFiles('inited').length != 0){
//			var files = collectionUploader.getFiles('inited');
//			for(var i= 0; i< files.length; i++){
//				collectionUploader.removeFile(files[i].id, true);
//			}
//			$("#showAttachmentFileColl").empty();
//	}
	if(loanUploader.getFiles('inited').length != 0){
		var files = loanUploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			loanUploader.removeFile(files[i].id, true);
		}
		$("#showAttachmentFileLoan").empty();
    }
	$("#tab_1_paymentOrder").addClass("tab_li_current");
	$("#tab_1_paymentOrder").siblings().removeClass("tab_li_current");
	$("#paymentDiv").show();
	$("#collectionDiv").hide();
	$("#loanDiv").hide();
	$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage");
	showUpLoadFileList();
}
//收款单
function collectionOrderTab(){
//	if(collectionUploader.getFiles('inited').length != 0){
//		var files = collectionUploader.getFiles('inited');
//		for(var i= 0; i< files.length; i++){
//			collectionUploader.removeFile(files[i].id, true);
//		}
//		$("#showAttachmentFileColl").empty();
//    }
	//判断是否还有待上传的文件（付款单和借款单）
	if (paymentUploader.getFiles('inited').length != 0){
			var files = paymentUploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				paymentUploader.removeFile(files[i].id, true);
			}
			$("#showAttachmentFilePay").empty();
		
	}
	if(loanUploader.getFiles('inited').length != 0){
		var files = loanUploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			loanUploader.removeFile(files[i].id, true);
		}
		$("#showAttachmentFileLoan").empty();
    }
	$("#tab_2_receiptOrder").addClass("tab_li_current");
	$("#tab_2_receiptOrder").siblings().removeClass("tab_li_current");
	$("#collectionDiv").show();
	$("#paymentDiv").hide();
	$("#loanDiv").hide();
	$("#collectionOrderIframe").attr("src", "/collectionManager/toCollectionDetailInfo");
//	initCollectionUpload();
	
}
//借款单
function loanOrderTab(){
	if(loanUploader.getFiles('inited').length != 0){
		var files = loanUploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			loanUploader.removeFile(files[i].id, true);
		}
		$("#showAttachmentFileLoan").empty();
    }
	//判断是否有待上传的文件(收款单和付款单)
	if (paymentUploader.getFiles('inited').length != 0){
			var files = paymentUploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				paymentUploader.removeFile(files[i].id, true);
			}
			$("#showAttachmentFilePay").empty();
	}
	
//	if(collectionUploader.getFiles('inited').length != 0){
//			
//			var files = collectionUploader.getFiles('inited');
//			for(var i= 0; i< files.length; i++){
//				collectionUploader.removeFile(files[i].id, true);
//			}
//			$("#showAttachmentFileColl").empty();
//	}
	$("#tab_3_loanbillOrder").addClass("tab_li_current");
	$("#tab_3_loanbillOrder").siblings().removeClass("tab_li_current");
	$("#loanDiv").show();
	$("#paymentDiv").hide();
	$("#collectionDiv").hide();
	$("#loanOrderIframe").attr("src", "/loanInfoManager/toLoanDetailInfoPage");
	initLoanUploader();
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
	/*$("#loanOrderId").html("");
	$("#payedMoneyCount").html("");*/
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





//ajax请求方法
function sendRequest(options){
	$.ajax({
		url: options.url,
		type: options.type,
		data: options.data,
		datatype: options.datatype,
		success: options.getResult
	});
}

//校验是否进行了单据设置
function checkReceiptHasSetted() {
	$.ajax({
		url: "/financeSettingManager/nopassword/checkBillHasSetted",
		type: "post",
		async: false,
		success: function(response) {
			if (!response.hasSetted) {
				window.location.href = "/financeSettingManager/toFinanceSettingPage?activeTagType=2";
			}
		}
	});
}


//获取单据信息
function saveOrderData(status){
	var subData = {};
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	subData.paymentId = _frame.find("#paymentId").val();
	subData.receiptNo = _frame.find("#billsNum").val();
	subData.paymentDate = _frame.find("#billsDateTime").val();
	subData.payeeName = _frame.find("#receivingParty").val();
	
	subData.contractId = _frame.find("#contractId").val();
	subData.contractType = _frame.find("#contractType").val();
	subData.loanIds = _frame.find("#loanIds").val();
	
	subData.status = status;
	
	subData.currencyId = _frame.find("#currency").val();
	subData.totalMoney = _frame.find("#readTotalMoney").val()-0;
	subData.paymentWay = _frame.find("#paymentWay").val();
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
	//部门
	subData.department = _frame.find("#departmentText").val();
	return subData;	
}


/**
 * 为上传成功后要执行的代码做标志**/
var orderPayId;//付款单：orderpayId:表示付款单ID,为保存并打印需要的参数；
//var orderColl, orderCollId;//收款单：orderColl: 0,表示保存；1，表示保存并打印； orderCollId :表示收款单ID;
var orderLoanId;//借款单： orderLoanId :表示借款单ID;



//获取财务科目信息
function financeSubjInfo(){
    var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var financeRecordInfo ="";
	var addSubTr = _frame.find("tr.add-sub-tr");
	$.each(addSubTr, function(i){
		financeRecordInfo += $(this).find("input[type=text]").eq(0).val()+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).attr("fid")+"##";
		financeRecordInfo += $(this).find("input[type=text]").eq(1).val()+"##";
		var noformatMoney = $(this).find("input[type=text]").eq(2).next().val();
		financeRecordInfo += noformatMoney +"&&";
	});
	return financeRecordInfo.substr(0,financeRecordInfo.length-2);
}

var payIsPrint;//付款单-判断是否同时打印单据
var collIsPrint;//收款单-判断是否同时打印单据
var loanIsPrint;//借款单-判断是否同时打印单据
//付款单保存方法
function savePaymentMethod(isSet, isPrint){
	payIsPrint = isPrint;
	var _frame = $('#fullReceiptDiv').contents();//得到iframe页面的数据
	var flag = true;
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
			flag = false;
			return;
		}
	});
	//校验金额是否为空
	if(flag == false){
		return;
	}
	

	$(".payment-btn").attr("disabled", true);
	var subData;
	if(isSet == 1){
		var status = isSet;
		subData = saveOrderData(status);
		savePaymentData(subData, true);
	}else{
		var status = isSet;
		subData = saveOrderData(status);
		savePaymentData(subData, false);
	}
}

//付款单数据保存
function savePaymentData(data, isSettlement){
	var options = {
			url: '/paymentManager/savePaymentInfo',
			type: 'post',
			data: data,
			datatype: 'json',
			getResult: function(response){
				$(".payment-btn").attr("disabled", false);
				if(response.success){
					if(payIsPrint){
						orderPayId = response.paymentId;
					}
					if (paymentUploader.getFiles().length == 0) {
		    			showSuccessMessage("操作成功");
		    			loanConfirmed = false;
		    			if(payIsPrint){//是否同时打印单据
		    				window.location.href="/paymentManager/toPrintPaymentInfoPage?paymentIds="+response.paymentId+"&&needBacktoPage=true";
		    			}else{
		    				$("#paymentDiv").show();
							$("#loanDiv").hide();
							$("#collectionDiv").hide();
							$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage");
		    			}
						
		    		} else {
		    			var attpackId = response.attpacketId;
		    			paymentUploader.option('formData', {
			    			attpackId: attpackId
			    		});
		    			paymentUploader.upload();
		    		}
				}else{
					showErrorMessage(response.message);
				}
				
			}
	};
	$(".payment-btn").attr("disabled", false);
	if(isSettlement){
		popupPromptBox("提示","确定要结算该付款单？",function(){
			sendRequest(options);
	    });
	}else{
		sendRequest(options);
	}
}


//收款单保存方法
function saveCollectionMthod(isPrint){
	collIsPrint = isPrint;
	var _coll_frame = $('#collectionOrderIframe').contents();//得到收款单iframe页面的数据
	_coll_frame.find("#collectionId").val("");
	var subData = {};
	
	if(_coll_frame.find("#payMoneyParty").val() == ""){
		parent.showErrorMessage("付款人(单位)不能为空");
		return;
	}
	if(_coll_frame.find("#money").val() == ""){
		parent.showErrorMessage("金额不能为空");
		return;
	}

	$(".collection-btn").attr("disabled", true);
	subData.receiptNo = _coll_frame.find("#billsNum").val();
	subData.collectionDate = _coll_frame.find("#billsDateTime").val();
	subData.otherUnit = _coll_frame.find("#payMoneyParty").val();
	subData.summary = _coll_frame.find("#summary").val();
	subData.money = _coll_frame.find("#money").next("input[type=hidden]").val();
	subData.currencyId = _coll_frame.find("#currency").val();
	subData.paymentWay = _coll_frame.find("#paymentWay").val();
	subData.agent = _coll_frame.find("#agent").val();
	var options = {
			url: '/collectionManager/saveCollectionInfo',
			type: 'post',
			data: subData,
			datatype: 'json',
			getResult: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					if(collIsPrint){
						window.location.href="/collectionManager/toPrintCollectionInfoPage?collectionIds="+ response.collectionId + "&&needBacktoPage=true";
					}else{
						$("#collectionDiv").show();
						$("#loanDiv").hide();
						$("#paymentDiv").hide();
						$("#collectionOrderIframe").attr("src", "/collectionManager/toCollectionDetailInfo");
					}
				}else{
					showErrorMessage(response.message);
				}
				$(".collection-btn").attr("disabled", false);
			}
	};
	sendRequest(options);
}



//保存借款单的方法
function saveLoanMethod(isPrint){
	loanIsPrint = isPrint;
	$(".loan-btn").attr("disabled", true);
	var _loan_frame = $('#loanOrderIframe').contents();//得到借款单iframe页面的数据
	var subData= {};
	//获取数据
	subData.receiptNo = _loan_frame.find("#billsNum").val();
	subData.loanDate = _loan_frame.find("#billsDateTime").val();
	subData.payeeName = _loan_frame.find("#loanMoneyParty").val();
	subData.summary = _loan_frame.find("#summary").val();
	subData.money = _loan_frame.find("#money").next("input[type=hidden]").val()-0;
	subData.currencyId = _loan_frame.find("#currency").val();
	subData.paymentWay = _loan_frame.find("#paymentWay").val();
	subData.agent = _loan_frame.find("#agent").val();
	subData.financeSubjName = _loan_frame.find("#financeSubjName").val();
	subData.financeSubjId = _loan_frame.find("#financeSubjId").val();
		
	var options = {
			url: '/loanInfoManager/saveLoanInfos',
			type: 'post',
			data: subData,
			datatype: 'json',
			getResult: function(response){
				if(response.success){
					if (loanUploader.getFiles().length == 0) {
						showSuccessMessage("操作成功");
						if(!loanIsPrint){
							//刷新当前页面
							$("#loanDiv").show();
							$("#paymentDiv").hide();
							$("#collectionDiv").hide();
							
							$("#loanOrderIframe").attr("src", "/loanInfoManager/toLoanDetailInfoPage");
						}else{
							window.location.href="/loanInfoManager/toPrintLoanInfoPage?loanIds=" + response.loanId + "&&needBacktoPage=true";
						}
						
		    		} else {
		    			var attpackId = response.attpacketId;
		    			loanUploader.option('formData', {
			    			attpackId: attpackId
			    		});
		    			loanUploader.upload();
		    		}
					
				}else{
					showErrorMessage(response.message);
				}
				$(".loan-btn").attr("disabled", false);
			}
	};
	sendRequest(options);
	
}



//================================财务密码相关方法start======================================
//校验是否需要财务密码、用户手机号(ip)
function checkNeedFinancePwd() {
  var need = false;
  $.ajax({
      url: "/financeSettingManager/nopassword/checkPasswordHasSetted",
      type: "post",
      data: {},
      dataType: "json",
      async: false,
      success: function(response) {
  		$(".financepassli").hide();
  		$(".useripli").hide();
      	var flag = false;
      	var validType=-1;
      	if(response.needPwd) {
      		flag = true;
      		$(".financepassli").show();
      		if(response.needUserIp){
      			validType=0;//全部验证
      		} else {
      			validType=1;//仅验证财务密码
      		}
      		$("#financePwd").focus();
      	}else{
      		if(response.needUserIp) {
      			validType=2;//仅验证用户手机号
      			$("#verifyCode").focus();
      		}
      	}
      	if(response.needUserIp){
      		flag = true;
      		$(".useripli").show();
      	}
      	$("#validType").val(validType);
          if (flag) {
              $("#validUserWin").jqxWindow("open");
              need = true;
          }
      }
  });
  
  return need;
}


function finanPwdWinKeyup(event) {
  var key = event.keyCode;
  if (key == 13) {
      checkPassword();
  }
}

//获取验证码点击事件
function obtainVerifyCode(obj){
	if ($(obj).hasClass("disabled")) {
      return false;
  }
  
  var phone = '<%=userInfo.getPhone()%>';
  $.ajax({
      url: "/interface/verifyCodeManager/sendVerifyCode",
      type: "post",
      data: {phone: phone, type: 4},
      success: function(response) {
          if (response.success) {
              $("#getVerifyCodeBtn").addClass("disabled");
              $("#getVerifyCodeBtn").val("重新发送（60s）");
              
              var totalSecond = 60;
              var int = setInterval(function(event) {
                  totalSecond --;
                  $("#getVerifyCodeBtn").val("重新发送（"+ totalSecond +"s）");
                  
                  if (totalSecond == 0) {
                      window.clearInterval(int);
                      $("#getVerifyCodeBtn").removeClass("disabled");
                      $("#getVerifyCodeBtn").val("获取验证码");
                  }
              }, 1000);
              
              $("#verifyCode").focus();
          } else {
              showErrorMessage(response.message);
          }
      }
  });
}


//校验财务密码、验证码
function checkPassword(own) {
	var params={};
	var validType=$("#validType").val();
	params.validType=validType;
	if(validType==0 || validType==1){//财务密码
		var financePwd=$("#financePwd").val();
		if(!financePwd) {
			showErrorMessage("请填写财务密码");
	        return false;
		}
		params.password=financePwd;
	}
	if(validType==0 || validType==2){//验证码
		var verifyCode=$("#verifyCode").val();
		if(!verifyCode) {
			showErrorMessage("请填写验证码");
	        return false;
		}
		params.verifyCode=verifyCode;
	}
	var phone = '<%=userInfo.getPhone()%>';
	params.phone=phone;
  $.ajax({
      url: "/financeSettingManager/nopassword/checkPasswordCorrect",
      type: "post",
      data: params,
      dataType: "json",
      async: false,
      success: function(response) {
          if (!response.success) {
              showErrorMessage(response.message);
          } else {
              $("#validUserWin").jqxWindow("close");
              window.location.reload();
          }
      }
  });
}

//关闭财务密码窗口
function closePwdWindow() {
  $("#validUserWin").jqxWindow("close");
}


//初始化安全验证窗口
function initValidUserWin(){
	$("#validUserWin").jqxWindow({
      theme: theme,
      height: 250,
      width: 400,
      resizable: false,
      isModal: true,
      autoOpen: false,
      initContent: function(){
          
      }
  });
  
  $("#validUserWin").on("close", function() {
      window.location.href = "/toIndexPage";
  });
}

//================================财务密码相关方法end======================================



/*********************************************上传附件********************************************************/
//定义文件上传变量
var paymentUploader, paySuccess = false;//付款单
//var collectionUploader; //收款单
var loanUploader; //借款单
////文件上传
function showUpLoadFileList(){
	/***********************************************付款单**********************************************************/
	paymentUploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/getCostManager/upoloadCostAttachment',
		timeout: 30*60*1000,//超时
		pick : '#uploadPaymentBtn',
		threads: 5,
		thumb: {
	    	   width: 200,
	    	   height: 200,
	    	   crop: false
	       },
	    method:'POST',
	});
	
	
	// 当有文件添加进来的时候
	paymentUploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var fileUl = $("#showAttachmentFilePay");
		var $li = $("<li class='upload-file-list-li'></li>");
		paymentUploader.makeThumb( file, function( error, ret ) {
			var suffix = file.ext.toLowerCase();
			if (suffix == "doc" || suffix == "docx") {
				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFilePay").append($li);
			} else if (suffix == "pdf"){
				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFilePay").append($li);
			} else if (suffix == "xls" || suffix == "xlsx"){
				$li.append("<img alt='' src= '../images/excel.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFilePay").append($li);
			} else if (error){
	            $li.html("预览错误");
	            $("#showAttachmentFilePay").append($li);
	        } else {
	        	$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' /><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#showAttachmentFilePay").append($li);
	        }
	    });
	});
	
	// 当有文件添加进来的时候
	paymentUploader.on('beforeFileQueued', function(file) {
		var ext = file.ext.toLowerCase();
		var type = file.type;
		if (ext != "doc" && ext != "docx" && ext != "pdf" && ext != "xls" && ext != "xlsx" && type.indexOf("image") == -1) {
			return false;
		}
		return true;
	});
	
	paymentUploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	
	paymentUploader.on('uploadFinished', function(file) {
		paySuccess = true;
		$('#myLoader').dimmer("hide");
		if(!payIsPrint){
			showSuccessMessage("操作成功");
			$("#showAttachmentFilePay").empty();
			$("#paymentDiv").show();
			$("#loanDiv").hide();
			$("#collectionDiv").hide();
			$("#fullReceiptDiv").attr("src", "/paymentManager/toPaymentDetailPage");
			loanConfirmed = false;
			return;
		}
		if(payIsPrint){
			showSuccessMessage("操作成功");
			loanConfirmed = false;
			window.location.href="/paymentManager/toPrintPaymentInfoPage?paymentIds="+ orderPayId +"&&needBacktoPage=true";
		}
		
	});
	
}
/***************************************************收款单*************************************************************/
//function initCollectionUpload(){
//	collectionUploader = WebUploader.create({
//		// 不压缩image
//		resize : false,
//		// 文件接收服务端。
//		server : '/getCostManager/upoloadCostAttachment',
//		pick : '#uploadCollectionBtn',
//		threads: 5,
//		thumb: {
//	    	   width: 200,
//	    	   height: 200,
//	    	   crop: false
//	    },
//	    method:'POST',
//	});
//	
//	
//	// 当有文件添加进来的时候
//	collectionUploader.on('fileQueued', function(file) {
//		var fileUl = $("#showAttachmentFileColl");
//		if(!fileUl.length > 0) {
//		}
//		var $li = $("<li class='upload-file-list-li'></li>");
//		collectionUploader.makeThumb( file, function( error, ret ) {
//			var suffix = file.ext.toLowerCase();
//			
//			if (suffix == "doc" || suffix == "docx") {
//				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteCollReadyUpload(this,\""+ file.id +"\")'></a>");
//				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
//				$("#showAttachmentFileColl").append($li);
//			} else if (suffix == "pdf"){
//				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteCollReadyUpload(this,\""+ file.id +"\")'></a>");
//				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
//				$("#showAttachmentFileColl").append($li);
//			} else if (error){
//	            $li.html("预览错误");
//	            $("#showAttachmentFileColl").append($li);
//	        } else {
//	        	$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' /><a class='closeTag' onclick='deleteCollReadyUpload(this,\""+ file.id +"\")'></a>");
//	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
//	            $("#showAttachmentFileColl").append($li);
//	        }
//	    });
//	});
//	
//	// 当有文件添加进来的时候
//	collectionUploader.on('beforeFileQueued', function(file) {
//		var ext = file.ext.toLowerCase();
//		var type = file.type;
//		if (ext != "doc" && ext != "docx" && ext != "pdf" && type.indexOf("image") == -1) {
//			return false;
//		}
//		return true;
//	});
//	
//	collectionUploader.on("startUpload", function() {
//		$('#myLoader').dimmer("show");
//	});
//	
//	collectionUploader.on('uploadFinished', function(file) {
//		$('#myLoader').dimmer("hide");
//		if(orderColl == 0){
//			$("#showAttachmentFileColl").empty();
//			showSuccessMessage("操作成功");
//			$("#collectionDiv").show();
//			$("#loanDiv").hide();
//			$("#paymentDiv").hide();
//			$("#collectionOrderIframe").attr("src", "/collectionManager/toCollectionDetailInfo");
//		}
//		if(orderColl == 1){
//			$("#showAttachmentFileColl").empty();
//			showSuccessMessage("操作成功");
//			window.location.href="/collectionManager/toPrintCollectionInfoPage?collectionIds="+ orderCollId + "&&needBacktoPage=true";
//		}
//	});
//}

//初始化借款单附件上传
function initLoanUploader(){
	loanUploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/getCostManager/upoloadCostAttachment',
		pick : '#uploadloanBtn',
		threads: 5,
		timeout: 30*60*1000,//超时
		thumb: {
	    	   width: 200,
	    	   height: 200,
	    	   crop: false
	    },
	    method:'POST',
	});
	
	
	// 当有文件添加进来的时候
	loanUploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var fileUl = $("#showAttachmentFileLoan");
		var $li = $("<li class='upload-file-list-li'></li>");
		loanUploader.makeThumb( file, function( error, ret ) {
			var suffix = file.ext.toLowerCase();
			
			if (suffix == "doc" || suffix == "docx") {
				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteLoanReadyUplad(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFileLoan").append($li);
			} else if (suffix == "pdf"){
				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteLoanReadyUplad(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFileLoan").append($li);
			} else if(suffix == "xls" || suffix == "xlsx"){
				$li.append("<img alt='' src= '../images/excel.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteLoanReadyUplad(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showAttachmentFileLoan").append($li);
			} else if (error){
	            $li.html("预览错误");
	            $("#showAttachmentFileLoan").append($li);
	        } else {
	        	$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' /><a class='closeTag' onclick='deleteLoanReadyUplad(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#showAttachmentFileLoan").append($li);
	        }
	    });
	});
	
	// 当有文件添加进来的时候
	loanUploader.on('beforeFileQueued', function(file) {
		var ext = file.ext.toLowerCase();
		var type = file.type;
		if (ext != "doc" && ext != "docx" && ext != "pdf" && ext != "xls" && ext != "xlsx"  && type.indexOf("image") == -1) {
			return false;
		}
		return true;
	});
	
	loanUploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	
	loanUploader.on('uploadFinished', function(file) {
		$('#myLoader').dimmer("hide");
		if(!loanIsPrint){
			$("#showAttachmentFileLoan").empty();
			showSuccessMessage("操作成功");
			$("#loanDiv").show();
			$("#paymentDiv").hide();
			$("#collectionDiv").hide();
			$("#loanOrderIframe").attr("src", "/loanInfoManager/toLoanDetailInfoPage");
		}
		if(loanIsPrint){
			$("#showAttachmentFileLoan").empty();
			showSuccessMessage("操作成功");
			window.location.href="/loanInfoManager/toPrintLoanInfoPage?loanIds=" + orderLoanId + "&&needBacktoPage=true";
		}
	});
}


//删除付款单未上传的文件附件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	paymentUploader.removeFile(fileId, true);
	own.parent("li").remove();
}
//删除收款单未上传的文件附件
//function deleteCollReadyUpload(own, fileId){
//	own= $(own);
//	collectionUploader.removeFile(fileId, true);
//	own.parent("li").remove();
//}
//删除借款单未上传的文件附件
function deleteLoanReadyUplad(own, fileId){
	own= $(own);
	loanUploader.removeFile(fileId, true);
	own.parent("li").remove();
}

