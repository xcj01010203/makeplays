$(document).ready(function(){

	//校验是否需要财务密码
	checkNeedFinancePwd();
	//加载币种设置列表
	currencySetting();
	//加载添加币种窗口
	initAddCurrency();
	//菜单的单击事件
	menuClik();	
	//币种名称下拉框
	currecyNameDrop();
	//判断是否为本位币事件
	isStandardMethod();
	

	//查询财务设置信息
	$.ajax({
		url: '/financeSettingManager/querySettingInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				financeSetting =response.financeSetting;
				if (financeSetting != null && financeSetting.hasReceiptStatus != null && financeSetting.payStatus != null) {
					
					//赋值（带参数）
					setReceipStatus(financeSetting.hasReceiptStatus, financeSetting.payStatus);
					//修改控件状态
					disableReceipDom();
				}
				
				if (financeSetting != null && financeSetting.pwdStatus != null && financeSetting.pwdStatus) {
					hasPwdRenderer();
				} else {
					notHasPwdRenderer();
				}
				
				if(financeSetting != null && financeSetting.ipStatus != null && financeSetting.ipStatus) {
					$("#setValidUserIp").prop('checked',true);
				}
				
				if (financeSetting.monthDayType == 1) {
					$("#natureDayInput").eq(0).attr("checked", "checked");
				} else {
					$("#thirtyDayInput").eq(0).attr("checked", "checked");
				}
				
				$("#contractAdvanceRemindDays").val(financeSetting.contractAdvanceRemindDays);
				$("#taxFinanSubjName").val(financeSetting.taxFinanSubjName);
				$("#taxFinanSubjId").val(financeSetting.taxFinanSubjId);
				$("#taxRate").val(financeSetting.taxRate);
				if (financeSetting.taxFinanSubjName) {
					$("#clearFinanceSubj").show();
				}
			}else{
				//showErrorMessage(response.message);
			}
         }

	});
	
	//阻止冒泡事件
	$("#levelPopup").on("click", function(ev){
		ev.stopPropagation();
	});
	$(document).click(function(){
    	$('#levelPopup').css("display", "none");
    });
	loadFinanceSubject();
	
	
	var activeTagType = $("#activeTagType").val();
	if (activeTagType == 1) {
		$(".setting-li-current").click();
	}
	if (activeTagType == 2) {
		$(".receipts-set-tab").click();
	}
	if (activeTagType == 3) {
		$(".pass-set-tab").click();
	}
	//只读权限
	if(isFinanceSetReadonly) {
		//不能在进行单据设置
		disableReceipDom();
		//不能进行安全设置
		$("#setFinancePass").attr('disabled',true);
		$("#setValidUserIp").attr('disabled',true);
		$(".set-new-pass-info").find('input').attr("disabled","disabled").css("cursor","no-drop");
		$(".set-old-pass-info").find('input').attr("disabled","disabled").css("cursor","no-drop");
		//不能进行其他设置
		$("input[name='monthDayType']").attr('disabled',true);
	}
});


function isStandardMethod(){
	//初始化是否为本位币事件
	$("#ifStandard").on("change",function(){
		if($("#ifStandard").val() == "true"){
			$("#exchangeRate").val(1).attr("disabled", true);
			$("#ifEnable").val("true").attr("disabled", true);
			$(".currency-rate-error-tips").css("display", "none");
			$("#exchangeRate").removeClass("error-currency-tips");
		}else{
			$("#exchangeRate").val("").attr("disabled", false);
			$("#ifEnable").val("").attr("disabled", false);
		}
    });
}



function disableReceipDom() {
	$("input[name=payStatus]").attr("disabled", true);
	$("input[name=hasReceiptStatus]").attr("disabled", true);
	$("#billSetBtn").css("display","none");
}

//该变量接收财务设置信息返回值
var financeSetting;


//菜单单击事件
function menuClik(){
	
	$(".setting-li-current").on("click",function(){
		$(".current-menu-name").text("币种设置");
		$(this).addClass("setting-li-current");
		$(this).siblings().removeClass("setting-li-current");
		$(".finance-show-con").show();
		$(".finance-pass").hide();
		$(".finance-set-content").hide();
		$("#taxSettingContent").hide();
		$("#otherSetting").hide();
	});
	$(".receipts-set-tab").on("click", function(){
		$(".current-menu-name").text("单据设置");
		$(this).addClass("setting-li-current");
		$(this).siblings().removeClass("setting-li-current");
		$(".finance-show-con").hide();
		$(".finance-pass").hide();
		$(".finance-set-content").show();
		$("#taxSettingContent").hide();
		$("#otherSetting").hide();
	});
	$(".pass-set-tab").on("click", function(){
		$(".current-menu-name").text("安全设置");
		$(this).addClass("setting-li-current");
		$(this).siblings().removeClass("setting-li-current");
		$(".finance-show-con").hide();
		$(".finance-pass").show();
		$(".finance-set-content").hide();
		$("#taxSettingContent").hide();
		$("#otherSetting").hide();
	});
	$(".tax-set-tab").on("click", function(){
		$(".current-menu-name").text("付款单缴税设置");
		$(this).addClass("setting-li-current");
		$(this).siblings().removeClass("setting-li-current");
		$(".finance-show-con").hide();
		$(".finance-pass").hide();
		$(".finance-set-content").hide();
		$("#taxSettingContent").show();
		$("#otherSetting").hide();
	});
	$(".other-set-tab").on("click", function(){
		$(".current-menu-name").text("其他设置");
		$(this).addClass("setting-li-current");
		$(this).siblings().removeClass("setting-li-current");
		$(".finance-show-con").hide();
		$(".finance-pass").hide();
		$(".finance-set-content").hide();
		$("#taxSettingContent").hide();
		$("#otherSetting").show();
	});
}

//获取下拉菜单数据的方法	
function dropBox(data, tag, id, left, top){
	//清空下拉框里的所有子元素
	$(id).empty();
	if(data != null){
		var _li = [];
		
		
			$.each($(data).find(tag),function(index,element){
				_li.push('<li class="drop-down-li" code="'+$(element).find("currencycode").text()+'"><a href="javascript:void(0)">'+$(element).attr("name")+'</a></li>');
			});
			$(id).append($(_li.join("")));
			
	}
	
	
	$(id).css({"left":left,"top":top});
	
}



//币种名称下拉框
function currecyNameDrop(){
	//获取下拉列表
	$.ajax({
		url : '/css/finance/code.xml',
		type: 'get',
		datatype: 'json',
		success: function(response){
			dropBox(response, "currenyname", '.dropdown_box','110px', '65px');
		}
	});
	 
	// 输入查询
	   $('#name').keyup(function(){
	   	//alert();
	       var _this=$(this)
	           ,_subList=_this.siblings('ul').children('li');
	       _subList.each(function(){
	           if($(this).text().search($.trim(_this.val()))!=-1){
	               $(this).show();
	           }else{
	               $(this).hide();
	           }
	       });
	   });
	 
	 //区分drop-box是否有值
 
	 $('#name').click(function(ev){
	   	var _this=$(this); 
	   	var _subList=_this.siblings('ul').children('li');
	    _subList.each(function(){
	        if($(this).text().search($.trim(_this.val()))!=-1){
	            $(this).show();
	        }else{
	            $(this).hide();
	        }
	    });
		_this.next('.dropdown_box').show();
	   	ev.stopPropagation();//阻止冒泡事件
	   });
	 
	  $('.dropdown_box li').off('click');
	  $('.dropdown_box').on('click','li',function(ev){
	       var _this=$(this);
	       $('#name').val($(this).text());
	       $("#name").removeClass("error-currency-tips");
	       $(".currency-name-error-tips").css("display", "none");
	       $("#code").val($(this).attr("code"));		        
	       _this.parent().hide();
	   });
}
$(document).click(function(){
	$('.dropdown_box').hide();	
	
}); 



//币种设置
function currencySetting(){
	//定义提交的参数
	
	var source = {
			url: '/currencyManager/queryCurrencyList',
            type:'post',
            dataType : "json",
            datafields: [
				{ name: 'id', type: 'string' },
				{ name: 'crewId', type: 'string'},
				{ name: 'name', type: 'string' },
				{ name: 'code', type: 'string' },
				{ name: 'ifStandard', type: 'boolean' },
				{ name: 'ifEnable', type: 'boolean'},
				{ name: 'exchangeRate', type: 'double'}
            ]
           
//            root:'currencyInfoList'
           	
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	//操作列
	var operationRender = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
    	html.push("<div class='modify-col'>");
    	//只读权限，不能进行编辑
    	if(!isFinanceSetReadonly) {
        	html.push("<a href='javascript:void(0);' aria-disabled='false' onclick = 'modifyCurrency(\""+row+"\")'>编辑</a>");
    	}
    	html.push('</div>');
    	
    	return html.join("");
	};
	
	//本位币列
	var ifStandardRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		
		if(value){
			html.push("<div class='standard-col'><span>是</span></div>");
		}else{
			html.push("<div class='standard-col'><span>否</span></div>");
		}
		return html.join("");
	};
	
	//状态列
	var ifEnableRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html = [];
		
		if(value){
			html.push("<div class='enable-col'><span>启用</span></div>");
		}else{
			html.push("<div class='enable-col'><span>禁用</span></div>");
		}
		return html.join("");
	};
	
	$("#financeShow").jqxGrid({
		theme: theme,
		width: 809,
	    source: dataAdapter,                
	    columns: [
	        { text: '名称', datafield: 'name', cellsAlign: "center", align: "center", width: '20%' },
	        { text: '编码', datafield: 'code', cellsAlign: "center", align: "center", width: '20%' },
	        { text: '本位币', datafield: 'ifStandard', cellsrenderer: ifStandardRenderer, cellsAlign: "center", align: "center", width: '20%' },
	        { text: '汇率 ', datafield: 'exchangeRate', cellsAlign: "center", align: "center", minwidth: '20%' },
	        { text: '状态', datafield: 'ifEnable', cellsrenderer: ifEnableRenderer, cellsAlign: "center", align: "center", minwidth: '20%' },
	        { text: '操作 ', cellsrenderer:operationRender, cellsAlign: "center", align: "center", minwidth: 70 }
	    ],
	    localization: localizationobj,
	    showtoolbar: true,
        autoheight: true,
        rendertoolbar: function (toolbar){
        	var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='add-currency-btn' id='addCurrencyBtn' onclick='addCurrency()'>");
            container.push("</div>");
			
			toolbar.append($(container.join("")));
			
			
			$("#addCurrencyBtn").jqxTooltip({content: "添加币种", position: "bottom"});
			
			//只读权限，去掉添加按钮
	    	if(isFinanceSetReadonly) {
	    		$("#addCurrencyBtn").remove();
	    	}
        }
	            	
	            	
	});
	
}


//添加币种窗口
function addCurrency(){
	$('#financeShow').jqxWindow('setTitle', '添加币种');
	
	$("#exchangeRate").val("").attr("disabled", false);
	$("#ifEnable").val("").attr("disabled", false);
	$("#ifStandard").attr("disabled", false);
	
	//初始化值 
	$("#id").val("");
	$("#name").val("");
	$("#code").val("");
	$("#exchangeRate").val("");
	$("#ifStandard").val("");
	$("#ifEnable").val("");
	
	
	//清空样式
	$(".currency-name-error-tips").css("display", "none");
	$("#name").removeClass("error-currency-tips");
	$(".currency-code-error-tips").css("display", "none");
	$("#code").removeClass("error-currency-tips");
	$(".currency-rate-error-tips").css("display", "none");
	$("#exchangeRate").removeClass("error-currency-tips");
	
	$("#addCurrencyWin").jqxWindow("open");
}

//修改币种窗口
function modifyCurrency(editrow){
	var dataRecord = $("#financeShow").jqxGrid('getrowdata', editrow);
	//修改标题
	$('#financeShow').jqxWindow('setTitle', '修改币种');
	//清空样式
	$(".currency-name-error-tips").css("display", "none");
	$("#name").removeClass("error-currency-tips");
	$(".currency-code-error-tips").css("display", "none");
	$("#code").removeClass("error-currency-tips");
	$(".currency-rate-error-tips").css("display", "none");
	$("#exchangeRate").removeClass("error-currency-tips");
	
	$("#exchangeRate").val("").attr("disabled", false);
	$("#ifEnable").val("").attr("disabled", false);
	$("#ifStandard").attr("disabled", false);
	//初始化值
	$("#id").val(dataRecord.id);
	$("#name").val(dataRecord.name);
	$("#code").val(dataRecord.code);
	$("#exchangeRate").val(dataRecord.exchangeRate);
	$("#ifStandard").val("" + dataRecord.ifStandard);
	$("#ifEnable").val("" + dataRecord.ifEnable);
	if($("#ifStandard").val() != "" && $("#ifStandard").val() == "true"){
		$("#exchangeRate").attr("disabled", true);
		$("#ifEnable").attr("disabled", true);
		$("#ifStandard").attr("disabled", true);
	}
	$("#addCurrencyWin").jqxWindow("open");
}
//初始化添加币种窗口
function initAddCurrency(){
	$("#addCurrencyWin").jqxWindow({
		theme: theme,
		width: '480',
		height: '430',
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			//校验文本框只能输入整数或小数
			$("#exchangeRate").keyup(function(){    
				$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
				$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
				$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
				$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
			});
			 $("#name").on("blur", function(){
					if($("#name").val() == ""){
						$("#name").addClass("error-currency-tips");
						$(".currency-name-error-tips").css("display", "block");
					} 
				 });
			 $("#name").on("focus", function(){
					 $("#name").removeClass("error-currency-tips");
					 $(".currency-name-error-tips").css("display", "none");
			 });
			 $("#code").on("blur", function(){
					if($("#code").val() == ""){
						$("#code").addClass("error-currency-tips");
						$(".currency-code-error-tips").css("display", "block");
					} 
				 });
			 $("#code").on("focus", function(){
					 $("#code").removeClass("error-currency-tips");
					 $(".currency-code-error-tips").css("display", "none");
			 });
			 $("#exchangeRate").on("blur", function(){
					if($("#exchangeRate").val() == ""){
						$("#exchangeRate").addClass("error-currency-tips");
						$(".currency-rate-error-tips").css("display", "block");
					} 
				 });
			 $("#exchangeRate").on("focus", function(){
					 $("#exchangeRate").removeClass("error-currency-tips");
					 $(".currency-rate-error-tips").css("display", "none");
			 });
			 	
		}
	});

}

//保存货币信息
function savecurrencyInfo(){
	if($("#name").val() == ""){
		$("#name").addClass("error-currency-tips");
		$(".currency-name-error-tips").css("display", "block");
		return false;
	}
	if($("#code").val() == ""){
		$("#code").addClass("error-currency-tips");
		$(".currency-code-error-tips").css("display", "block");
		return false;
	}
	if($("#exchangeRate").val() == ""){
		$("#exchangeRate").addClass("error-currency-tips");
		$(".currency-rate-error-tips").css("display", "block");
		return false;
	} 
	var subData = {};
	subData.id = $("#id").val();
	subData.name = $("#name").val();
	subData.code = $("#code").val();
	subData.ifStandard = $("#ifStandard").val();
	subData.ifEnable = $("#ifEnable").val();
	subData.exchangeRate = $("#exchangeRate").val();

	$.ajax({
		url: '/currencyManager/saveCurrencyInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#addCurrencyWin").jqxWindow("close");
				$("#financeShow").jqxGrid("updatebounddata");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}




//有无单据设置显示方法
function setReceipStatus(hasReceiptStatus, payStatus){
	if(hasReceiptStatus){
		$('input:radio[name=hasReceiptStatus]:nth(0)').attr('checked',true);
	}
	if(!hasReceiptStatus){
		$('input:radio[name=hasReceiptStatus]:nth(1)').attr('checked',true);
	}
	if(payStatus){
		$('input:radio[name=payStatus]:nth(0)').attr('checked',true);
	}
	if(!payStatus){
		$('input:radio[name=payStatus]:nth(1)').attr('checked',true);
	}
}

//单据设置



function saveBillSettingInfo(){
	var subData = {};
	
	subData.payStatus = $("input[name=payStatus]:checked").val();
	subData.hasReceiptStatus = $("input[name=hasReceiptStatus]:checked").val();
	$.ajax({
		url: '/financeSettingManager/saveBillSettingInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				disableReceipDom();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//有财务密码时候的控件渲染
function hasPwdRenderer() {
	$("#setFinancePass").attr('checked', true);
	$(".set-new-pass-info").hide();
	$(".set-old-pass-info").show();
	$(".set-old-pass-info ul").show();
	
	$(".modify-old-pass").on("focus", function(){
		$(".modify-old-pass").removeClass("text-error-show");
		$(".finance-old-pass-tips").css("visibility", "hidden");
	});
	$(".modify-new-pass").on("focus", function(){
		$(".modify-new-pass").removeClass("text-error-show");
		$(".finance-modify-pass-tips").css("visibility", "hidden");
	});
	$(".modify-repeat-pass").on("focus", function(){
		$(".modify-repeat-pass").removeClass("text-error-show");
		$(".modify-repeat-pass-tips").css("visibility", "hidden");
	});
	
	
	
	$("#setFinancePass").unbind("click");
	$("#setFinancePass").on("click", function(){
		if($("#setFinancePass").prop('checked')){
			$("ul.set-old-pass-info-chec").show();
		} else {
			$("ul.set-old-pass-info-chec").hide();
		}
	});
}

//初始化财务密码dom行为
function notHasPwdRenderer() {
	$("#setFinancePass").attr('checked', false);
	//设置不能编辑
	$(".set-new-pass-info").find('p').addClass('label-disabled');
	$(".set-new-pass-info").find('input').attr("disabled","disabled");
	$(".set-new-pass-info").find('input').css("cursor","no-drop");
	$(".set-new-pass-info").show();
	$(".set-old-pass-info").hide();
	
	 //初始化修改密码页面的控件行为
	$(".new-password").on("focus",function(){
    	$(".finance-new-pass-tips").css("visibility", "hidden");
        $(".new-password").removeClass("text-error-show");
    });
    $(".repeat-password").on("focus",function(){
    	$(".finance-repeat-pass-tips").css("visibility", "hidden");
        $(".repeat-password").removeClass("text-error-show");
    });
	

    $("#setFinancePass").unbind("click");
	$("#setFinancePass").on("click", function(){
		if($("#setFinancePass").prop("checked")){
			$(".set-new-pass-info").find('p').removeClass('label-disabled');
			$(".set-new-pass-info").find('input').removeAttr("disabled");
			$(".set-new-pass-info").find('input').removeAttr('style');
		} else {
			//清除已填写内容及样式
			$(".set-new-pass-info").find('input[type="password"]').val('');
			$(".finance-new-pass-tips").css("visibility", "hidden");
	        $(".new-password").removeClass("text-error-show");
	        $(".finance-repeat-pass-tips").css("visibility", "hidden");
	        $(".repeat-password").removeClass("text-error-show");
			$(".set-new-pass-info").find('p').addClass('label-disabled');
			$(".set-new-pass-info").find('input').attr("disabled","disabled");
			$(".set-new-pass-info").find('input').css("cursor","no-drop");
		}
	});
}


/*有无财务密码显示*/
function queryFinancePass(){
	
	//初始化值
	$(".new-password").val("");
	$(".repeat-password").val("");
	$(".modify-old-pass").val("");
	$(".modify-new-pass").val("");
	$(".modify-repeat-pass").val("");
	
	
	
	$(".finance-new-pass-tips").css("visibility", "hidden");
    $(".new-password").removeClass("text-error-show");
    

	$("#setFinancePass").on("click", function(){
		if($("#setFinancePass").prop("checked")){
			$(".set-new-pass-info ul").css("display", "block");
		}		
	}).on("click", function(){
		if(! $("#setFinancePass").prop("checked")){
			$(".set-new-pass-info ul").css("display", "none");
		}
	});
			
	
}

/*新增财务密码*/
function saveNewPassInfo(){
	if($(".new-password").val() == ""){
		$(".finance-new-pass-tips").text("密码不能为空");
		$(".finance-new-pass-tips").css("visibility", "visible");
        $(".new-password").addClass("text-error-show");
		return false;
		
	}
	if($(".repeat-password").val() == ""){
		$(".finance-repeat-pass-tips").text("确认密码不能为空");
		$(".finance-repeat-pass-tips").css("visibility", "visible");
        $(".repeat-password").addClass("text-error-show");
		return false;
		
	}
	if(($(".new-password").val()).length < 6){
		$(".finance-new-pass-tips").text("密码不能少于6位");
		$(".finance-new-pass-tips").css("visibility", "visible");
        $(".new-password").addClass("text-error-show");
        return false;
	}
	if($(".new-password").val() != $(".repeat-password").val()){
		$(".finance-repeat-pass-tips").text("密码不一致");
		$(".finance-repeat-pass-tips").css("visibility", "visible");
        $(".repeat-password").addClass("text-error-show");
        return false;
	}
	
	
	var subData = {};
	subData.operateFlag = 1;
	subData.pwdStatus = $("#setFinancePass").prop("checked");
	subData.newPassword = $(".new-password").val();
	subData.repeatPassword = $(".repeat-password").val();
	$.ajax({
		url: '/financeSettingManager/savePasswordInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$(".new-password").val("");
				$(".repeat-password").val("");
				hasPwdRenderer();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}



//修改财务密码
function saveNewPassInfoToo(){
	$(".finance-old-pass-tips").css("visibility", "hidden");
	$(".modify-old-pass").removeClass("text-error-show");
	$(".finance-modify-pass-tips").css("visibility", "hidden");
	$(".modify-new-pass").removeClass("text-error-show");
	$(".modify-repeat-pass-tips").css("visibility", "hidden");
	$(".modify-repeat-pass").removeClass("text-error-show");
	
	
	
	//取“是否启用财务密码”复选框的值
	var isModify = $("#setFinancePass").prop('checked');
	
	
	
	if($(".modify-old-pass").val() == ""){
		$(".finance-old-pass-tips").text("旧密码不能为空");
		$(".finance-old-pass-tips").css("visibility", "visible");
		$(".modify-old-pass").addClass("text-error-show");
		return false;
	}
	if(isModify && $(".modify-new-pass").val() == ""){
		$(".finance-modify-pass-tips").text("密码不能为空");
		$(".finance-modify-pass-tips").css("visibility", "visible");
		$(".modify-new-pass").addClass("text-error-show");
		return false;
	}
	if(isModify && $(".modify-repeat-pass").val() == ""){
		$(".modify-repeat-pass-tips").text("确认密码不能为空");
		$(".modify-repeat-pass-tips").css("visibility", "visible");
		$(".modify-repeat-pass").addClass("text-error-show");
		return false;
	}
	if(isModify && $(".modify-new-pass").val() != $(".modify-repeat-pass").val()){
		$(".modify-repeat-pass-tips").text("密码不一致");
		$(".modify-repeat-pass-tips").css("visibility", "visible");
		$(".modify-repeat-pass").addClass("text-error-show");
		return false;
	}
	if((isModify && $(".modify-new-pass").val()).length < 6){
		$(".finance-modify-pass-tips").text("密码不能少于6位");
		$(".finance-modify-pass-tips").css("visibility", "visible");
		$(".modify-new-pass").addClass("text-error-show");
		return false;
	}

	
	
	var subData = {};
	subData.operateFlag = 2;
	subData.pwdStatus = $("#setFinancePass").prop("checked");
	subData.oldPassword = $(".modify-old-pass").val();
	subData.newPassword = $(".modify-new-pass").val();
	subData.repeatPassword = $(".modify-repeat-pass").val();
	
	$.ajax({
		url: '/financeSettingManager/savePasswordInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				if (isModify) {
					//清空修改密码表单
					$(".modify-old-pass").val("");
					$(".modify-new-pass").val("");
					$(".modify-repeat-pass").val("");
				} else {
					
					$(".modify-old-pass").val("");
					//隐藏修改密码表单
					notHasPwdRenderer();
				}
						
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//设置是否根据用户IP地址变化验证用户手机号
function setValidUserIpFunc(){
	$.ajax({
		url: '/financeSettingManager/setIpStatus',
		type: 'post',
		data: {ipStatus:$("#setValidUserIp").prop("checked")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");			
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//设置月份天数
function saveOtherSetting() {
	var monthDayType = $("input[name=monthDayType]:checked").val();
	var contractAdvanceRemindDays = $("#contractAdvanceRemindDays").val();
	
	$.ajax({
		url: '/financeSettingManager/saveOtherSetting',
		type: 'post',
		data: {monthDayType: monthDayType, contractAdvanceRemindDays: contractAdvanceRemindDays},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");			
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//校验是否是数字
function checkIsNumber(own) {
	var value = $(own).val();
	if (isNaN(value)) {
		$(own).val("");
	}
}

//显示选择财务科目面板
function showSelectFinanSubjDiv(own, ev) {
	var obj = $(own);
	$('#levelPopup').css({left:obj.offset().left-3,top:obj.offset().top+32, "display": "block"});
	$('#levelPopup #filter').focus();
	ev.stopPropagation();
}

//加载财务科目
function loadFinanceSubject(){
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

	 $("#subjectTree").jqxTreeGrid({
		 width: 220,
		 height:200,
		 source: dataAdapter,
		 showHeader: false,
		 ready: function(){},
		    columns: [
		          { text: '财务科目', dataField: 'name', width: 200, align: "center" }
		        ]
     });
	 $('#subjectTree').on('rowSelect', function (event){
         
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
//        	 	var text = $("input[name=subval]").val();
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
		          $("#taxFinanSubjName").val(name);
		          $("#taxFinanSubjId").val(row.id);
		          $('#levelPopup').css("display", "none");
		          $("#clearFinanceSubj").show();
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
		
		$("#filter").on("keydown", function(ev){
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
			ev.stopPropagation();
		});
}

//清空财务科目
function clearFinanceSubj(){
     $("#taxFinanSubjName").val("");
     $("#taxFinanSubjId").val("");
     $("#clearFinanceSubj").hide();
}

//设置缴税信息
function setTaxInfo() {
	var taxFinanSubjId = $("#taxFinanSubjId").val();
	var taxRate = $("#taxRate").val();
	if (!taxFinanSubjId) {
		showErrorMessage("请选择税务科目");
		return;
	}
	if (!taxRate) {
		showErrorMessage("请填写税率");
		return;
	}
	if (taxRate > 1) {
		showErrorMessage("税率不能大于1");
		return;
	}
	
	$.ajax({
		url: "/financeSettingManager/saveTaxInfo",
		data: {taxFinanSubjId: taxFinanSubjId, taxRate: taxRate},
		type: "post", 
		dataType: "json", 
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return;
			}
			showSuccessMessage("操作成功");
		}
	});
}