var currencyList;
var saveOrSubmit;
$(function(){
	//判断是否是财务
//	financialStaff();
	//查询币种信息
	queryCurrentInfo();
	//判断审批单据类型
//	approvalType();
	//查询审批人列表
//	queryApprovalList();
	//初始化文件上传
	showUpLoadFileList();
	if($("#receiptId").val() != ""){//修改状态
		queryApprovalDetailInfo();
	}else{
		$("#approValDetailTwo").empty();
		$("#approValDetailOne").show();
		//初始化文件上传
		showUpLoadFileList();
		approvalType();
		//判断审批单据类型
		isStaffReceiptType();
		queryApprovalList();
		$("#deleteBtn").hide();
		$("#approvalProgress").hide();//新增状态
	}
	$(document).on("click", function(){
		$("#dropDownList").hide();
	});
	
	$("#myLoader").dimmer({
		closable: false
	});
});

//判断是否是财务
var isFinance = false;
function financialStaff(){
	$.ajax({
		url: '/userManager/judgeUserIsFinance',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.isFinance){
					isFinance = true;
					$("#endBtn").show();
				}else{
					$("#endBtn").hide();
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//判断审批列表类型
function approvalType(){
	//判断单据类型
	if($("#listType").val() == 1){//我的申请
		$("#saveBtn").show();
		$("#submitBtn").show();
		$("#deleteBtn").show();
	}
	if($("#listType").val() == 2){//我已审批
		
	}
	if($("#listType").val() == 3){//待我审批
		$("#agreenBtn").show();
		$("#refuseBtn").show();
		$("#return").show();
	}
	
}
//判断审批单据类型
function isStaffReceiptType(){
	if($("#receiptType").val() == 1){
		$("#moneyCapition").html("借款金额:");
		$("#expenseTips").html("借款说明:");
	}else if($("#receiptType").val() == 2){
		$("#moneyCapition").html("报销金额:");
		$("#expenseTips").html("报销说明:");
	}else{
		$("#moneyCapition").html("预算金额:");
		$("#expenseTips").html("预算说明:");
	}
}



//查询币种信息
function queryCurrentInfo(){
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		type: 'post',
		data: {"ifEnable": true},
		datatype: 'json',
		success: function(response){
			if(response.success){
				currencyList = response.currencyInfoList;
				var html = [];
				if(currencyList.length > 1){
					$("#moneyCurrentSelect").show();
					$("#moneyCurrentSelect").next("p").show();
					for(var i= 0; i< currencyList.length; i++){
						html.push('<option value="'+ currencyList[i].id+'">'+ currencyList[i].code +'</option>');
					}
					$("#moneyCurrentSelect").append(html.join(""));
				}else{
					$("#moneyCurrentSelect").hide();
					$("#moneyCurrentInput").next("p").hide();
					$("#moneyCurrentCode").text(currencyList[0].code);
					$("#moneyCurrentCode").attr("currid", currencyList[0].id);
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//查询审批单据详细信息
function queryApprovalDetailInfo(){
	$.ajax({
		url: '/receiptInfoManager/queryReceiptDetailInfo',
		type: 'post',
		data: {"receiptId": $("#receiptId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
//				$("#type").val(response.type);
				$("#receiptType").val(response.type);
				var approverList = response.approverList;
				var pictureList = response.pictureList;
				var attachmentList = response.attachmentList;
				var showApprovalProgress = response.showApprovalProgress;
				
				var approverName = [];
				var approverId = [];
				if((response.status == 1 || response.status == 3) && ($("#listType").val() == 1)){//我的申请-草稿、被拒绝
					$("#approValDetailTwo").empty();
					$("#approValDetailOne").show();
					//初始化文件上传
					showUpLoadFileList();
					//判断列表类型（待我审批、我已审批、我的审请）
					approvalType();
					//判断审批单据类型
					isStaffReceiptType();
					//审批人列表
					queryApprovalList();
					$("#receiptNo").val(response.receiptNo);
					$("#financeMoney").val(response.money);
					if(currencyList.length > 1){
						$("#moneyCurrentSelect").val(response.currencyId);
					}else{
						$("#moneyCurrentInput").val(response.currencyCode);
						$("#moneyCurrentInput").attr("currid", response.currencyId);
					}
					$("#capition").val(response.description);
					//是否是只读
					if(isApprovalReadonly){
						$("#uploadImageBtn").remove();
					}
				}else{//审批中、待审批、已审批
					$("#approValDetailOne").empty();
					$("#approValDetailTwo").show();
					//判断是否是财务-显示完结按钮
					financialStaff();
					//判断列表类型（待我审批、我已审批、我的审请）
					approvalType();
					//判断审批单据类型
					isStaffReceiptType();//改变审批单据的金额说明名称
					var listType = $("#listType").val();
					
					if(response.status == 4){//已完结状态
						$("#endBtn").hide();
						$("#submitBtn").hide();
						$("#saveBtn").hide();
						
						if((currentUserId == response.doneUserId) && listType == 2){//完结人可激活--已审批
							$("#endBtn").hide();
							$("#activation").show();//完结人可以激活
							$("#approvalPerson").attr("onclick", "");//不可添加审批人
						}
					}
					if(response.status == 2 && (listType == 1)){//审批中状态--我的申请
						$("#deleteBtn").hide();
						$("#endBtn").hide();
						$("#submitBtn").hide();
						$("#saveBtn").hide();
						$("#withdrawBtn").show();
						$("#addApprover").show();//显示添加审批人按钮
						$("#approvalPerson").focus();
					}else{//其他状态都 不可填加审批人
						$("#deleteBtn").hide();
						$("#approvalPerson").attr("onclick", "");
					}
					if(listType != 3 && !isFinance){
						$("#approvalOpionion").attr("disabled", true);
					}
					if((listType == 2 && (!isFinance || response.status == 4)) || listType == 1){//我已审批和我的申请都不显示审批意见
						$(".approval-info").find("li:last-child").remove();
					}
					if(response.status == 3){//被拒绝状态
						$("#endBtn").hide();
					}
					//查询审批人列表
					queryApprovalList();
					$("#expenseMoney").html(response.money + "(<span style='color: #e48945; float: none;'>" + response.currencyCode + "</span>)");
					$("#expenseMoney").attr("currencyid", response.currencyId);
					$("#expenseCapition").text(response.description);
					$("#receiptNo").val(response.receiptNo);//票据编号
					$("#orderNum").text(response.receiptNo);
//					if(approverList != null && approverList.length != 0){//审批人赋值
//						var _approver = $("#dropPersonList").find("li");
//						for(var i = 0; i< approverList.length; i++){
//							if(i != 0){//去掉发起人
//								approverName.push(approverList[i].userName);
//								approverId.push(approverList[i].userId);
//								$.each(_approver, function(){
//									if($(this).attr("id") == approverList[i].userId){
//										$(this).addClass("select");
//									}
//								});
//							}
//							
//						}
//					}
//					$("#approvalPerson").val(approverName.join(","));
//					$("#approvalPersonId").val(approverId.join(","));
//					//生成审批时间轴
//					$("#approvalProgress").show();
//					productApprovalTimer(approverList);
//					//附件列表
//					productAttachList(pictureList,attachmentList);
				}
				//是否是只读
				if(isApprovalReadonly){
					$(".header-btn-list").find("input[type=button]").hide();
					$("#approvalOpionion").attr("disabled", true);
					$("#arrowUp").show();
				}
				
				//审批人赋值
				if(approverList != null && approverList.length != 0){//审批人
					var _approver = $("#dropPersonList").find("li");
					var spanString = [];
					for(var i = 0; i< approverList.length; i++){
						if(i != 0){//去掉发起人
//							approverName.push(approverList[i].userName);
//							approverId.push(approverList[i].userId);
							if(approverList[i].resultType == 3){//同意--审批后的不能改变顺序
								spanString.push('<span class="no-drag" pid="'+ approverList[i].userId +'">' + approverList[i].userName + '</span>');
							}else{
								spanString.push('<span class="drag-span" draggable="true" pid="'+ approverList[i].userId +'">' + approverList[i].userName + '</span>');
							}
							$.each(_approver, function(){
								if($(this).attr("id") == approverList[i].userId){
									$(this).addClass("select");
									if(approverList[i].resultType == 3){
										$(this).attr("onclick", "cannotDelete()");
									}
								}
							});
						}
					}
//					$("#approvalPerson").html(approverName.join(","));
					$("#approvalPerson").html(spanString.join(""));
					$("#approvalPersonId").val(approverId.join(","));
					//生成审批时间轴
					$("#approvalProgress").show();
					productApprovalTimer(approverList);
				}
				//初始化审批人的拖动事件
				initDragEvent();
				//附件列表
				productAttachList(pictureList,attachmentList);
				if (!showApprovalProgress) {
					$("#approvalProgress").hide();
				}
			}else {
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//生成审批时间轴
function productApprovalTimer(approverList){
	var html = [];
	for(var i = 0; i< approverList.length; i++){
		if(i == 0){
			html.push('<div class="timer-viwer-div">');
		}else{
			html.push('<div class="timer-viwer-div" approvalid="'+ approverList[i].userId+'">');
		}
		html.push('<div class="timer-content-div">');
		if(i == 0){
			html.push('<div class="timer-flag shenpi"></div>');
			html.push('<div class="timer-content">');
			html.push('<div class="timer-title">');
			html.push('<span class="name-tips font-gray">' + approverList[i].userName + '(发起)</span>');
		}else{
			if(approverList[i].resultType == 1){
				html.push('<div class="timer-flag shenpi"></div>');
				html.push('<div class="timer-content">');
				html.push('<div class="timer-title">');
				html.push('<span class="name-tips font-gray">' + approverList[i].userName + '(审批中)</span>');
			}else if(approverList[i].resultType == 2){
				html.push('<div class="timer-flag red"></div>');
				html.push('<div class="timer-content">');
				html.push('<div class="timer-title">');
				html.push('<span class="name-tips font-red">' + approverList[i].userName + '(不同意)</span>');
			}else if(approverList[i].resultType == 3){
				html.push('<div class="timer-flag green"></div>');
				html.push('<div class="timer-content">');
				html.push('<div class="timer-title">');
				html.push('<span class="name-tips font-green">' + approverList[i].userName + '(通过)</span>');
			}else {
				html.push('<div class="timer-flag gray"></div>');
				html.push('<div class="timer-content">');
				html.push('<div class="timer-title">');
				html.push('<span class="name-tips font-return">' + approverList[i].userName + '(退回)</span>');
			}
			
		}
		
		html.push('<span class="date-tips">' + approverList[i].approvalTime + '</span>');
		html.push('</div>');
		if(approverList[i].comment == null){
			approverList[i].comment  = "";
		}
		html.push('<p class="timer-tips-content">'+ approverList[i].comment +'</p>');
		html.push('</div>');
		html.push('</div>');
		html.push('</div>');
	}
	$(".timer-view-container").empty();
	$(".timer-view-container").append(html.join(""));
}


//生成附件列表
function productAttachList(pictureList,attachmentList){
	var html = [];
	if(pictureList != null && pictureList.length != 0){
		for(var i= 0; i< pictureList.length; i++){
			html.push("<li class='upload-file-list-li' onclick='previewAtts(\""+ pictureList[i].attpackId +"\", \""+ pictureList[i].type +"\")' title='点击预览'>");
			html.push("<img src='/fileManager/previewAttachment?address="+ pictureList[i].hdPreviewUrl+"'>");
			if((($("#receiptStatus").val() == 1) || ($("#receiptStatus").val() == 3)) && (!isApprovalReadonly)){
				html.push("<a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ pictureList[i].attachmentId +"\")'></a>");
			}
			html.push("<p class='file-list-tips' title='" + pictureList[i].name +"'>" + pictureList[i].name + "</p>");
			html.push('</li>');
		}
		
	}
	if(attachmentList != null && attachmentList.length != 0){
		for(var i= 0; i< attachmentList.length; i++){
			var attachment = attachmentList[i];
			var suffix =attachment.suffix.toLowerCase();
			if(suffix == ".doc" || suffix == ".docx"){
				html.push("<li class='upload-file-list-li' onclick=' downLoadAttachment(event,this,\""+ attachment.hdPreviewUrl +"\",\""+attachment.name+"\")' title='点击下载'>");
				html.push("<img src='../images/word.jpg'>");
				if((($("#receiptStatus").val() == 1) || ($("#receiptStatus").val() == 3)) && (!isApprovalReadonly)){
					html.push("<a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId + "\")'></a>");
				}
				html.push("<p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p>");
				html.push("</li>");
			}
			else if(suffix == ".pdf"){
				console.log(isApprovalReadonly);
				html.push("<li class='upload-file-list-li' onclick=' downLoadAttachment(event,this,\""+ attachment.hdPreviewUrl +"\",\""+attachment.name+"\")' title='点击下载'>");
				html.push("<img src='../images/pdf.jpg'>");
				if((($("#receiptStatus").val() == 1) || ($("#receiptStatus").val() == 3)) && (!isApprovalReadonly)){
					html.push("<a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId + "\")'></a>");
				}
				html.push("<p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p>");
				html.push("</li>");
			}else if(suffix == ".xls" || suffix == ".xlsx"){
				html.push("<li class='upload-file-list-li' onclick=' downLoadAttachment(event,this,\""+ attachment.hdPreviewUrl +"\",\""+attachment.name+"\")' title='点击下载'>");
				html.push("<img src='../images/excel.jpg'>");
				if((($("#receiptStatus").val() == 1) || ($("#receiptStatus").val() == 3)) && (!isApprovalReadonly)){
					html.push("<a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId + "\")'></a>");
				}
				html.push("<p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p>");
				html.push("</li>");
			}else{
				html.push("<li class='upload-file-list-li' onclick=' downLoadAttachment(event,this,\""+ attachment.hdPreviewUrl +"\",\""+attachment.name+"\")' title='点击下载'>");
				html.push("<div class='no-img'></div>");
				if((($("#receiptStatus").val() == 1) || ($("#receiptStatus").val() == 3)) && (!isApprovalReadonly)){
					html.push("<a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId + "\")'></a>");
				}
				html.push('<p class="img-format">' + suffix + '</p>');
				html.push("<p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p>");
				html.push("</li>");
			}
		}
	}
	$("#uploadFileList").empty();
	$("#uploadFileList").append(html.join(""));
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
//下载文件附件
function downLoadAttachment(ev,own, address, name){
	ev.stopPropagation();
	//根据地址下载附件
	if (address != "") {
		window.location.href="/fileManager/downloadFileByAddr?address="+address+"&fileName=" + name;
	}
}
//删除上传成功的文件附件
function deleteUploadedFile(ev,own,attId){
	own= $(own);
	parent.popupPromptBox("提示","是否删除该附件？", function () {
		$.ajax({
	    	url:'/attachmentManager/deleteAttachment',
	    	type:'post',
	    	dataType:'json',
	    	data:{attachmentId: attId},
	    	success:function(data){
	    		if(data.success){
	    			own.parent("li").remove();
	    			parent.showSuccessMessage("删除成功！");
	    		}else{
	    			parent.showErrorMessage('删除附件失败');
	    		}
	    		
	    	}   	
		});
    });
	ev.stopPropagation();
}


//查询审批人列表
function queryApprovalList(){
	$.ajax({
		url: '/userManager/queryCrewAllUserListWithFletter',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var userList = response.userList;
				var html= [];
				if(userList != null && userList.length != 0){
					for(var i = 0; i< userList.length; i++){
//						html.push('<li onclick="checkapprovalPerson(this,event)" id="'+ userList[i].userId +'" title="'+ userList[i].userName + '-' + userList[i].roleNames +'">'+ userList[i].userName + '-' + userList[i].roleNames +'</li>');
						html.push('<li onclick="checkapprovalPerson(this,event)" id="'+ userList[i].userId +'" title="'+ userList[i].userName + '-' + userList[i].roleNames +'">');
						html.push('<span class="user-name">'+ userList[i].userName +'</span>');
						html.push('<span class="job-name">'+ userList[i].roleNames+'</span>');
						html.push('</li>');
					}
					$("#dropPersonList").empty();
					$("#dropPersonList").append(html.join(""));
				}else{
					html.push('<li style="text-align: center;">暂无数据</li>');
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//显示审批人列表
function showPersonList(ev, own){
	var position = $(own).position();
	var height = $(own).outerHeight();
	$("#dropDownList").css({"left": position.left, "top": position.top+height}).show();
	$(".search-input").focus();
	ev.stopPropagation();
}
//阻止冒泡事件
function stopPagation(ev){
	ev.stopPropagation();
}
//不允许删除审批人
function cannotDelete(){
	parent.showInfoMessage("不能删除已经审批过的的审批人员");
	return;
}



//选择审批人
function checkapprovalPerson(own, ev){
	var name = $(own).find("span.user-name").text();
	var id = $(own).attr("id");
	
	
	if($(own).hasClass("select")){
		$(own).removeClass("select");
		var spanObj = $("#approvalPerson").find("span.drag-span");
		$.each(spanObj, function(){
			var pid = $(this).attr("pid");
			if(pid == id){
				$(this).remove();
			}
		});
		
	}else{
		$(own).addClass("select");
		var spanString = '<span class="drag-span" draggable="true" pid="'+ id +'">' + name + '</span>';
		$("#approvalPerson").append(spanString);
	}
	ev.stopPropagation();
	initDragEvent();
}


var $curElemForDrag = null;	// 用于存放当前拖动的$dom
var $afterElem = null;
var $container = null;
//初始化拖动事件
function initDragEvent(){
	$container = $("#approvalPerson");
	$("#approvalPerson").on('dragover', 'span', function(ev) {
		/* 拖拽元素在目标元素头上移动的时候 */
		ev.preventDefault();
		return true;
	}).on('dragenter', 'span.drag-span', function() {
		/* 拖拽元素进入目标元素头上的时候 */
		if ($curElemForDrag) {
			this.style.marginLeft = $curElemForDrag.outerWidth() + 'px';
		}

		if ($afterElem){
			$afterElem.css('margin-left', '0');
		}
		$afterElem = $(this);
		return true;
	}).on('drop', 'span', function(ev) {
		/* 拖拽元素进入目标元素头上，同时鼠标松开的时候 */
		drop({
			'top' : ev.originalEvent.pageY,
			'left' : ev.originalEvent.pageX
		});
		return false;
	}).on('dragover', function(ev) {
		/* 拖拽元素在目标元素头上移动的时候 */
		ev.preventDefault();
		return true;
	}).on('drop', function(ev) {
		drop({
			'top' : ev.originalEvent.pageY,
			'left' : ev.originalEvent.pageX
		});

		return false;
	}).on('dragstart', 'span', function(ev) {
		// 开始拖拽
		$curElemForDrag = $(ev.target);
		ev.originalEvent.dataTransfer.setData('Text', '');
		ev.originalEvent.dataTransfer.effectAllowed = 'move';
		this.className += ' dropping';
		return true;
	}).on('dragen', 'span', function(ev) {
		// 开始结束
		$curElemForDrag = null;
		return false;
	});
}
//删除节点信息
function drop(mouseOffset) {
	if (!$curElemForDrag){
		return;
	}
	if ($curElemForDrag.isNew){
		$curElemForDrag = $curElemForDrag.clone();
	}
	
	if ($afterElem) {
		var afterElemOffset = $afterElem.offset();

		if ((afterElemOffset.left + $afterElem.outerWidth()) < mouseOffset.left || afterElemOffset.top + $afterElem.outerHeight() < mouseOffset.top) {
			$container.append($curElemForDrag);
			
		} else {
			$afterElem.before($curElemForDrag);
		}
		$afterElem.css('margin-left', '0');
		$afterElem = null;
	} else {
		$container.append($curElemForDrag);
	}
	$curElemForDrag.removeClass('require').removeClass('label-new');
	$curElemForDrag = null;
}





//检索输入名称
function checkOutPerson(own, ev){
	var _this=$(own), _subList=$("#dropPersonList").children('li');
    var searchFlag = false;
    _subList.each(function(){
 	   
        if($(this).text().search($.trim(_this.val()))!=-1){
     	   searchFlag = true;
     	  $("#dropPersonList").show();
            $(this).show();
        }else{
            $(this).hide();
        }
    });
    
    if (!searchFlag) {
    	$("#dropPersonList").hide();
    }
    ev.stopPropagation();
}


//添加审批人操作
function addApproverBtn(){
	var approverIds = [];
	var spanObj = $("#approvalPerson").find("span");
	$.each(spanObj, function(){
		approverIds.push($(this).attr("pid"));
	});
	$.ajax({
		url: '/receiptInfoManager/addApprover',
		type: 'post',
		data: {"receiptId": $("#receiptId").val(), "approverIds":approverIds.join(",")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("添加审批人成功");
				closeApprovalDetail();
				parent.refreshList(1);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//添加审批进度
function addApprovalProgress(id, name, date){
	var html = [];
	html.push('<div class="timer-viwer-div" approvalid="'+ id +'">');
	html.push('<div class="timer-content-div">');
	html.push('<div class="timer-flag shenpi"></div>');
	html.push('<div class="timer-content">');
	html.push('<div class="timer-title">');
	html.push('<span class="name-tips font-gray">' + name + '(审批中)</span>');
	html.push('<span class="date-tips"></span>');
	html.push('</div>');
	html.push('<p class="timer-tips-content"></p>');
	html.push('</div>');
	html.push('</div>');
	html.push('</div>');
	$(".timer-view-container").append(html.join(""));
}
//去掉审批进度
function removeApprovalProgress(id){
	$(".timer-viwer-div[approvalid="+ id +"]").remove();
}


//校验是否是非数字
function checkNum(own){
	$(own).val($(own).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$(own).val($(own).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$(own).val($(own).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$(own).val($(own).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}

//初始化文件上传
function showUpLoadFileList(){
	uploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/attachmentManager/uploadAttachment',
		timeout: 30*60*1000,//超时
		pick : '#uploadImageBtn',
		threads: 5,
		thumb: {
	    	   width: 200,
	    	   height: 200,
	    	   crop: false
	       },
	    method:'POST'
	});
	
	
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		parent.showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var $li = $("<li class='upload-file-list-li'></li>");
		uploader.makeThumb( file, function( error, ret ) {
			var suffix = file.ext.toLowerCase();
            
			if (suffix == "doc" || suffix == "docx") {
				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#uploadFileList").append($li);
			} else if (suffix == "pdf"){
				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#uploadFileList").append($li);
			}else if(suffix == "xls" || suffix == "xlsx"){
				$li.append("<img alt='' src= '../images/excel.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#uploadFileList").append($li);
			}  else if(suffix == "jpg" || suffix == "gif" || suffix == "jpeg" || suffix == "png"){
	        	$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' /><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#uploadFileList").append($li);
	        }else{
	        	$li.append("<div class='no-img' title='"+ file.name +"'></div><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append('<p class="img-format">' + suffix + '</p>');
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	        	$("#uploadFileList").append($li);
	        }
//			else if (error){
//	            $li.html("预览错误");
//	            $("#uploadFileList").append($li);
//	        }
	    });
	});
	
	// 当有文件添加进来之前
	uploader.on('beforeFileQueued', function(file) {
		/*var ext = file.ext.toLowerCase();
		var type = file.type;
		if (ext != "doc" && ext != "docx" && ext != "pdf" && type.indexOf("image") == -1 && ext != "xls" && ext != "xlsx") {
			return false;
		}
		return true;*/
	});
	
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	
	uploader.on('uploadFinished', function(file) {
		$('#myLoader').dimmer("hide");
		if($("#receiptNo").val() == ""){
			closeApprovalDetail();
			parent.refreshList(1);
			if(saveOrSubmit == 1){
				parent.showSuccessInfo("保存成功，当前单据编号为" + publicReceiptNo);
			}
			if(saveOrSubmit == 2){
				parent.showSuccessInfo("提交成功，当前单据编号为" + publicReceiptNo);
			}
		}else{
			closeApprovalDetail();
			parent.refreshList(1);
			if(saveOrSubmit == 1){
				parent.showSuccessMessage("保存成功");
			}
			if(saveOrSubmit == 2){
				parent.showSuccessMessage("提交成功");
			}
			
		}
		
	});
}
//删除未上传的文件附件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	uploader.removeFile(fileId, true);
	own.parent("li").remove();
}


var publicReceiptNo;

//保存单据
function saveApprovalInfo(own,flag){
	//设置当前按钮不可用
	var _this = $(own);
	_this.attr('disabled','disabled');
	
	var subData = {};
	subData.receiptId = $("#receiptId").val();
	subData.receiptType = parseInt($("#receiptType").val());
	subData.money = $("#financeMoney").val();
	if(currencyList.length > 1){
		subData.currencyId = $("#moneyCurrentSelect").val();
	}else{
		subData.currencyId = $("#moneyCurrentCode").attr("currid");
	}
	subData.description = $("#capition").val();
	var spans = $("#approvalPerson").find("span");
	var approvalIds = [];
	$.each(spans, function(){
		approvalIds.push($(this).attr("pid"));
	});
	subData.approverIds = approvalIds.join(",");
//	subData.approverIds = $("#approvalPersonId").val();
	subData.operateType = flag;
	saveOrSubmit = flag;
	$.ajax({
		url: '/receiptInfoManager/saveReceiptInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){//激活当前按钮
			_this.removeAttr('disabled');
			if(response.success){
				
				var attpackId = response.attpackId;
				if (uploader.getFiles().length == 0) {
					if($("#receiptNo").val() == ""){
						if(flag == 1){
							parent.showSuccessInfo("保存成功，当前单据编号为" + response.receiptNo);
						}
						if(flag == 2){
							parent.showSuccessInfo("提交成功，当前单据编号为" + response.receiptNo);
						}
					}else{
						if(flag == 1){
							parent.showSuccessMessage("保存成功");
						}
						if(flag == 2){
							parent.showSuccessMessage("提交成功");
						}
					}
					
	    			closeApprovalDetail();
	    			parent.refreshList(1);
	    		} else {
	    			publicReceiptNo = response.receiptNo;
	    			uploader.option('formData', {
		    			attpackId: attpackId
		    		});
		    		uploader.upload();
	    		}
				
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}


//审批单据
function approvalReceipt(type){
	var receiptId = $("#receiptId").val();
	var comment = $("#approvalOpionion").val();
	$.ajax({
		url: '/receiptInfoManager/approveReceipt',
		type: 'post',
		data: {"receiptId": receiptId, "resultType": type, "comment": comment},
		datatype: 'json',
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("审批单据成功");
				closeApprovalDetail();
				if($("#listType").val() == 2){
					parent.refreshList(2);
				}
				if($("#listType").val() == 3){
					parent.refreshList(3);
				}
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//撤销单据
function widthdrawReceipt(){
	parent.popupPromptBox("提示","确定要撤回该条信息吗？", function (){
		$.ajax({
			url: '/receiptInfoManager/revokeReceipt',
			type: 'post',
			data: {"receiptId": $("#receiptId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("撤回申请成功");
					closeApprovalDetail();
					if($("#listType").val() == 1){
						parent.refreshList(1);
					}
					if($("#listType").val() == 2){
						parent.refreshList(2);
					}
					if($("#listType").val() == 3){
						parent.refreshList(3);
					}
				}
			}
		});
	});
}

//删除单据
function deleteReceipt(){
	parent.popupPromptBox("提示","确定要删除该条信息吗？", function (){
		$.ajax({
			url: '/receiptInfoManager/deleteReceipt',
			type: 'post',
			data: {"receiptId": $("#receiptId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除成功");
					closeApprovalDetail();
					if($("#listType").val() == 1){
						parent.refreshList(1);
					}
					if($("#listType").val() == 2){
						parent.refreshList(2);
					}
					if($("#listType").val() == 3){
						parent.refreshList(3);
					}
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	});
}
//激活
function activationReceipt(){
	parent.popupPromptBox("提示","确定要激活该条信息吗？", function (){
		$.ajax({
			url: '/receiptInfoManager/activeReceipt',
			type: 'post',
			data: {"receiptId": $("#receiptId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("激活成功");
					closeApprovalDetail();
					if($("#listType").val() == 1){
						parent.refreshList(1);
					}
					if($("#listType").val() == 2){
						parent.refreshList(2);
					}
					if($("#listType").val() == 3){
						parent.refreshList(3);
					}
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	});
}


//关闭事件
function closeApprovalDetail(){
	parent.closeRightPopWin();
}

