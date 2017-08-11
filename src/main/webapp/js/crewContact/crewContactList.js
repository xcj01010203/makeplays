var filter={};
var clientWidth;
var depart = [];

Array.prototype.S=String.fromCharCode(2);  
Array.prototype.in_array=function(e) {
	var r=new RegExp(this.S+e+this.S);  
	return (r.test(this.S+this.join(this.S)+this.S));  
};

$(document).ready(function(){
	//初始化页面的宽高
	clientWidth=window.screen.availWidth;
	var tableWidth = 1600;
	if($(document).width()>tableWidth) {
		tableWidth = $(document).width();
	}
	
	//初始化修改个人剧组联系的窗口
	initUpdateWindow();
	
	//初始化高级查询窗口
	initSearchWindow();
	
	
	//跳转到联系表页面后需要加载的数据
	loadContactListData();
	
	//加载剧组联系列表
	loadGrid();
	
	//隐藏宾馆名称下拉列表
	$(document).on("click", function(){
		$("#dropdownBox").hide();
	});
	
	//初始化导入窗口
	initImportWin();
	
	//判断权限
	if(isContactReadonly) {
		$(".add-contact").remove();
		$(".import-contact").remove();
		
		//公开到组不可用
		$("input[type='checkbox']").attr('disabled',true);
		
		//联系人明细不可编辑
		$("#contact-btn-sure").remove();
		$("#contact-btn-delete").remove();
		$("#hotel-btn-addrow").remove();
		$("#hotel-btn-sure").remove();
		
		$("input[type='text']").attr('disabled',true);
		$("select").attr('disabled',true);
		$("input[type='radio']").attr('disabled',true);
	}
	if(!hasImportContactAuth) {
		$(".import-contact").remove();
	}
	if(!hasExportContactAuth) {
		$(".export-contact").remove();
	}
	
	//初始化消息弹出窗口
	initTipWindow();
});


function initImportWin() {
	$("#importContactWin").jqxWindow({
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
	$("#importContactWin").jqxWindow("open");
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/contact/importCrewContact&&needIsCover=true&&refreshUrl=/contact/toContactList&&templateUrl=/template/import/剧组联系表导入模板.xls");
}

//关闭导入窗口
function closeImportWin(){
	$("#importContactWin").jqxWindow("close");
}

//导出联系表
function exportContact() {
	
	//在导出之前先查询一下是否有数据
	var errorMessage = "";
	$.ajax({
		url: '/contact/queryCrewContactList',
		type: 'post',
		async: false,
		data:{contactFilter: filter},
		datatype: 'json',
		success: function(response){
			if(null == response || response.length == 0){
				errorMessage = "暂无数据，无法导出";
			}
		}
	});
	
	if (errorMessage != "") {
		showErrorMessage(errorMessage);
		return false;
	}
	
	var formData = [];
	formData.push(" <form action='/contact/queryCrewContactInfoForExport' id='exportContactForm'>");
	if(filter.contactName != undefined && filter.contactName != ''){
		formData.push(" <input type='text' name='contactName' value='" + filter.contactName + "'/>");
	}else {
		formData.push(" <input type='text' name='contactName'/>");
	}
	
	if(filter.sex != undefined && filter.sex != ''){
		formData.push(" <input type='text' name='sex' value='" + filter.sex + "'/>");
	}else {
		formData.push(" <input type='text' name='sex' value=''/>");
	}
	
	if (filter.departmentIds != undefined && filter.departmentIds != '') {
		formData.push(" <input type='text' name='departmentIds' value='" + filter.departmentIds + "'/>");
	}else {
		formData.push(" <input type='text' name='departmentIds' value=''/>");
	}
	
	if(filter.sysRoleIds != undefined && filter.sysRoleIds != ''){
		formData.push(" <input type='text' name='sysRoleIds' value='" + filter.sysRoleIds + "'/>");
	}else {
		formData.push(" <input type='text' name='sysRoleIds' value=''/>");
	}
	
	if(filter.enterDate != undefined && filter.enterDate != ''){
		formData.push(" <input type='text' name='enterDate' value='" + filter.enterDate + "'/>");
	}else {
		formData.push(" <input type='text' name='enterDate' value=''/>");
	}
	
	if (filter.leaveDate != undefined && filter.leaveDate != '') {
		formData.push(" <input type='text' name='leaveDate' value='" + filter.leaveDate + "'/>");
	}else {
		formData.push(" <input type='text' name='leaveDate' value=''/>");
	}
	
	if(filter.mealType != undefined && filter.mealType != ''){
		formData.push(" <input type='text' name='mealType' value='" + filter.mealType + "'/>");
	}else {
		formData.push(" <input type='text' name='mealType' value=''/>");
	}
	
	if (filter.hotel != undefined && filter.hotel != '') {
		formData.push(" <input type='text' name='hotel' value='" + filter.hotel + "'/>");
	}else {
		formData.push(" <input type='text' name='hotel' value=''/>");
	}
	
	formData.push(" </form>");
	
	var form = $(formData.join(""));
	
	$("body").append(form);
	form.submit();
	form.remove();
	
}
//跳转到联系表页面后需要加载的数据
function loadContactListData(){
	$.ajax({
		url: '/contact/queryContactDepart',
		type: 'post',
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var depart_response = response.depart;
				//加载部门及职务列表
				loadDepartData(depart_response);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//加载部门及职务列表
function loadDepartData(departList){
	//取出后台返回的部门列表并拼接成json字符串
	var child = [];
	for(var i=0; i<departList.length; i++){
		var item = departList[i];
		child = [];
		for(var j = 0; j<item.child.length; j++){
			var it = item.child[j];
			child.push({roleId:it.roleId, roleName:it.roleName});
		}
		depart.push({child:child,roleId:item.roleId, roleName:item.roleName});
	}
	//将取出的部门数据添加到jsp的页面中
	for(var k=0;k<depart.length;k++) {
    	var option = $("<option></option>");
    	option.attr("value",depart[k].roleId);
    	option.text(depart[k].roleName);
    	$("#department-search").append(option);
    	$("#department").append(option.clone());
	}
	
	//初始化下拉框的样式
	$('.selectpicker').selectpicker({
        size: 10,
        width:'240px'
    });
}

//初始化高级查询窗口
function initSearchWindow(){
	$('#searchWindow').jqxWindow({
		theme:theme,  
		width: 800,
        height: 285, 
        maxWidth: clientWidth,
        autoOpen: false,
        isModal: true,
        maxHeight:620,
        resizable: false,
        cancelButton: $("#contact-btn-search-close"),
        initContent: function () {
        	//初始化确定按钮
        	$("#contact-btn-search-sure").jqxButton({
        		theme:theme, 
        		width: '100', 
        		height: '25'
        	});
        	//初始化关闭按钮
        	$("#contact-btn-search-close").jqxButton({
        		theme:theme, 
        		width: '100', 
        		height: '25'
        	});
        }
	});
}

//点击高级查询窗口的查询按钮
function searchContactButton(){
	var contactName = $("#contactName-search").val();
	var sex = $("input[name=sex-search]:checked").val();
	var departmentIds = $("#department-search").val();
	var sysRoleIds = $("#duties-search").val();
	var enterDate = $("input[name=enterDate-search]").val();
	var leaveDate = $("input[name=leaveDate-search]").val();
	var mealType = $("input[name=mealType-search]:checked").val();
	var hotel = $("input[name=hotel-search]").val();
	filter = {};
	//联系人姓名
	if(contactName!= undefined && $.trim(contactName) != ''){
		filter.contactName=$.trim(contactName);
	}
	//性别
	if(sex!= undefined && $.trim(sex) != ''){
		filter.sex=$.trim(sex);
	}else{
		filter.sex="";
	}
	//部门
	if(departmentIds!= undefined && departmentIds.length>0){
		filter.departmentIds=departmentIds.toString();
	}else{
		filter.departmentIds="";
	}
	//职务
	if(sysRoleIds!= undefined && sysRoleIds.length>0){
		filter.sysRoleIds=sysRoleIds.toString();
	}else{
		filter.sysRoleIds="";
	}
	//入组时间
	if(enterDate!= undefined && $.trim(enterDate) != ''){
		filter.enterDate=$.trim(enterDate);
	}else{
		filter.enterDate="";
	}
	//离组时间
	if(leaveDate!= undefined && $.trim(leaveDate) != ''){
		filter.leaveDate=$.trim(leaveDate);
	}else{
		filter.leaveDate="";
	}
	//餐别
	if(mealType!= undefined && $.trim(mealType) != ''){
		filter.mealType=$.trim(mealType);
	}else{
		filter.mealType="";
	}
	//宾馆
	if(hotel!= undefined && $.trim(hotel) != ''){
		filter.hotel=$.trim(hotel);
	}else{
		filter.hotel="";
	}
	
	loadGrid();
	
	$('#searchWindow').jqxWindow('close');
}

//新增剧组联系人
function addPeson(){
	$("#needContactId").val("");
	$("#contactIdHidden").val("");
	$("._table input[type=text]").val('');
	$("._table input[type=radio][value=1]").attr("checked",true); 
	$("#identityCardType").val("1");
	$("input[name=mealType][value=1]").attr("checked",true);
	$("input[name=ifOpen][value=1]").attr("checked",true);
	$("#rightPopUpWin .selectpicker").selectpicker('deselectAll');
	$("#contact-btn-delete").jqxButton({
		theme:theme, 
		width: '100', 
		height: '25'
	});
	
	$("#contact-btn-delete").hide();
	//住宿信息清空
	$("#hotelDetailDate").empty();
	var html= '<tr class="blank-tr"><td style="text-align: center; vertical-align: center;" colspan= "5">暂无数据</td> </tr>';
	$("#hotelDetailDate").append(html);
	
	$("#rightPopUpWin").animate({"right": 0}, 500).show();
	//默认跳到第一个tab页
	$("#contact_information").addClass("tab_li_current");
	$("#contact_information").siblings().removeClass("tab_li_current");
	$(".public-contact").hide();
	$(".contact-info-div").show();
	
	//将联系人信息置空
	contactInfo = null;
}



//在文本框中输入想查询的姓名,在列表中展示查询的结果
function searchName(own) {
	var valueObj=$(own).val();
	
	filter = {};
	filter.contactName=valueObj;
	loadGrid();
}




//初始化修改个人剧组联系的窗口
function initUpdateWindow(){
	
	//初始化确定按钮样式
	$("#contact-btn-sure").jqxButton({
		theme:theme, 
		width: '100', 
		height: '25'
	});
	//初始化关闭按钮样式
	$("#contact-btn-close").jqxButton({
		theme:theme, 
		width: '100', 
		height: '25'
	});
	//初始化删除按钮样式
	$("#contact-btn-delete").jqxButton({
		theme:theme, 
		width: '100', 
		height: '25'
	});
	
	//提交验证方法
    $('#form').jqxValidator({
    	hintType: 'label',
    	animationDuration: 0,
    	rules: [
				{ input: '#contactName', message: '姓名不能为空!', action: 'keyup,blur', rule: 'required' },
				{ input: '#phone', message: '手机号不能为空!',action: 'blur',  rule: 'required' },
//				{ input: '#phone', message: '手机号不合法!', action: 'blur', rule: function(input, commit){
//	            	
//					if(input.val().length==0){ 
//	                    return true; 
//	                }
//					
//	            	if(input.val().length!=11){ 
//	                    return false; 
//	                } 
//	                 
//	                var myreg =   /^(1(([34578][0-9])|(76)))\d{8}$/;
//	                if(!myreg.test(input.val())){ 
//	                    return false; 
//	                } 
//	            	return true;
//	            } },
	            { input: '#idNumber', message: '证件号码不能为空!',action: 'blur',  rule: 'required' },
//				{ input: '#idNumber', message: '证件号码不合法!', action: 'blur', rule: function(input, commit){
//					var idtype = $("#identityCardType").val();
//					if(idtype == 1){
//						var myreg =   /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
//						if(!myreg.test(input.val())){ 
//							return false; 
//						} 
//					}
//	            	return true;
//	            } },
	            ]
    });
}

//点击修改个人剧组联系窗口的删除按钮
function confirmDelContatc(){
	var contactId = $("input[name=contactId]").val();
	popupPromptBox("提示","是否删除该联系人？", function () {
		$.ajax({
        	url:'/contact/deleteCrewContactBatch',
        	type:'post',
        	dataType:'json',
        	data:{contactIds:contactId},
        	success:function(data){
        		if(data.success){
        			showSuccessMessage(data.message);
        			loadGrid();
        			//关闭滑动窗口
        			closeRightPopupWin();
        		}else {
        			showErrorMessage(data.message);
				}
        	}   	
        });
	});
}

//保存联系人信息
function confirmSaveContact() {
	var contactId = $("#needContactId").val();
	var contactName = $("input[name=contactName]").val();
	var phone = $("input[name=phone]").val();
	var sex = $("input[name=sex]:checked").val();
	var identityCardType = $("#identityCardType").val();
	var identityCardNumber = $("#idNumber").val();
	var duties = $("#duties").val();
	var enterDate = $("input[name=enterDate]").val();
	var leaveDate = $("input[name=leaveDate]").val();
	var remark = $("input[name=remark]").val();
	var mealType = $("input[name=mealType]:checked").val();
	var hotel = $("input[name=hotel]").val();
	var roomNumber = $("input[name=roomNumber]").val();
	var extension = $("input[name=extension]").val();
	var checkInDate = $("input[name=checkInDate]").val();
	var checkoutDate = $("input[name=checkoutDate]").val();
	var ifOpen = $("input[name=ifOpen]:checked").val();
	
	if (contactName == undefined || contactName == '') {
		showErrorMessage("请填写姓名");
		return false;
	}
	if (phone == undefined || phone == '') {
		showErrorMessage("请填写电话号码");
		return false;
	}
	if (duties == undefined || duties == '') {
		showErrorMessage("请选择职务信息");
		return false;
	}
	if (identityCardNumber == undefined || identityCardNumber == '') {
        var str = "";
        if(identityCardType==1){
        	str = "请填写身份证号码";
        }else if(identityCardType==2){
        	str = "请填写护照号码";
        }else if(identityCardType==3){
        	str = "请填写台胞证号码";
        }else if(identityCardType==4){
        	str = "请填写军官证号码";
        }else if(identityCardType==5){
        	str = "请填写证件号码";
        }
		showErrorMessage(str);
		return false;
	}
	if (enterDate == undefined || enterDate == '') {
		showErrorMessage("请选择入组日期");
		return false;
	}
	if (leaveDate == undefined || leaveDate == '') {
		showErrorMessage("请选择离组日期");
		return false;
	}
	var param = {
		contactId:contactId,
		contactName:contactName,
		phone:phone,
		sex:sex,
		identityCardNumber:identityCardNumber,
		identityCardType:identityCardType,
		sysRoleIds:duties.toString(),
		enterDate:enterDate,
		leaveDate:leaveDate,
		remark:remark,
		mealType:mealType,
		hotel:hotel,
		roomNumber:roomNumber,
		extension:extension,
		checkInDate: checkInDate,
		checkoutDate:checkoutDate,
		ifOpen:ifOpen
	};
	
	$.ajax({
		url: "/contact/saveCrewContactInfo",
		data:param,
		dataType:'json',
		type:'post',
		success:function(data){
			if(data.success){
				showSuccessMessage("操作成功");
				/*$("input[name=contactId]").val('');
				//后台需要返回新建的联系人id
				if($("#needContactId").val() == "" && data.contactId != null){
					$("#contact-btn-delete").show();
					$("#needContactId").val(data.contactId);
					$("#contactIdHidden").val(data.contactId);
					
				}else{
					//刷新修改后的数据
					refreshContactDate();
				}*/
				closeRightPopupWin();
				loadGrid();
			}else {
				showErrorMessage(data.message);
			}
		}
	});
}

function refreshContactDate(){
	$.ajax({
		url: '/contact/queryCrewContactList',
		type: 'post',
		data: {contactId: $("#needContactId").val()},
		datatype: 'json',
		success: function(response){
			if(response.length != 0){
				valueToForm(response[0]);
				console.log(response);
			}
		}
	});
}



//点击高级查询页面中的部门下拉框,选择不同的部门
function changeSearchDepartment() {
	var role= $("#department-search").val(); 
	$("#duties-search").empty();
	if(role)
	for(var k=0;k<depart.length;k++) {
		if(role.in_array(depart[k].roleId)) {
			var optgroup = $("<optgroup></optgroup>");
			optgroup.attr("label",depart[k].roleName);
			var childs = depart[k].child;
			for(var j=0;j<childs.length;j++) {
				var option = $("<option></option>");
		    	option.attr("value",childs[j].roleId);
		    	option.text(childs[j].roleName);
		    	optgroup.append(option);
			}
        	$("#duties-search").append(optgroup);
		}
    	
    }
	$("#duties-search").selectpicker('refresh');
}

//点击人员信息列表中的部门下拉框,选择不同的部门
function changeDepartment(){
	var role= $("#department").val(); 
	$("#duties").empty();
	if(role) {
		for(var k = 0; k < depart.length; k++) {
			if(role.in_array(depart[k].roleId)) {
				var optgroup = $("<optgroup></optgroup>");
				optgroup.attr("label",depart[k].roleName);
				var childs = depart[k].child;
				for(var j=0;j<childs.length;j++) {
					var option = $("<option></option>");
			    	option.attr("value",childs[j].roleId);
			    	option.text(childs[j].roleName);
			    	optgroup.append(option);
				}
	        	$("#duties").append(optgroup);
			}
	    }
	}
	$("#duties").selectpicker('refresh');
	
}


//加载剧组联系列表
function loadGrid() {
	$("#contact").remove();
	$(".contact-body").html('<table id="contact"></table>');
	var clientHeight=$(document).height()-150;
	$("#contact").css("height",clientHeight+"px");
	$('#contact').datagrid({   
	    url:"/contact/queryCrewContactList", 
	    nowrap:true,
	    striped:true,
	    queryParams:filter,
	    singleSelect:true,
	    collapsible:true,
	    fitColumns:true,
	    columns:[[
	        {field:'contactId',title:'序号',width: '3.5%',align:'center',
	        	formatter: function(value,row,index){
					return "<div class='sequence-id' ihotelid='"+row.inhotelid+"' sid='"+row.contactId+"'>"+(index+1)+"</div>";
				}
	        },  
	        {field:'contactName',title:'姓名',width: '6.5%',align:'center',
	        	formatter: function(value, row, index){
					return '<a href="javascript:void(0);" title="编辑" onclick="updatePeson(\''+ row.contactId +'\')">'+value+'</a>';
				}
	        },   
	        {field:'sex',title:'性别',width: '3.5%',align:'center',
	        	formatter: function(value,row,index){
					if(value == 0) {
						return "女";
					} else if(value == 1) {
						return '男';
					}
	        	}
	        },
	        {field:'phone',title:'手机号',width: '6%',align:'center',
					formatter: function(value,row,index){
		        		return "<div title='"+value+"'>"+value+"</div>";
					}		
	        },
	        {field:'sysRoleNames',title:'职务',width: '7.5%',align:'center',
	        	formatter: function(value,row,index){
	        		if (value != null){
	        			return "<div title='"+value+"'>"+value+"</div>";
	        		}
				}	
	        },
	        {field:'enterDate',title:'入组时间',width: '6%',align:'center'},
	        {field:'leaveDate',title:'离组时间',width: '6%',align:'center'},
	        {field:'ifOpen',title:'公开到组',width: '4%',align:'center',
	        	formatter: function(value, row, index){
	        		var htmlArray = [];
	        		htmlArray.push("<div class='ui toggle checkbox ifopen-check-div'>");
	        		htmlArray.push("<input type='checkbox' onclick='checkInout("+ index +")' onchange='ifOpenContactInfo(this)'");
	        		if(value != 0) {
	        			htmlArray.push(" checked ");
	        		}
	        		if(isContactReadonly) {
	        			htmlArray.push(" disabled ");
	        		}
	        		htmlArray.push(">");
	        		
	        		htmlArray.push("<label></label>");
	        		htmlArray.push("</div>");
					
	        		return htmlArray.join("");
				}	
	        },
	        {field:'identityCardType',title:'证件类型',width: '7.5%',align:'center',
	        	formatter: function(value,row,index){
	        		if (value == 1) {
						return "<div>身份证</div>";
					} else if (value == 2) {
						return '<div>护照</div>';
					} else if (value == 3) {
						return '<div>台胞证</div>';
					} else if (value == 4) {
						return '<div>军官证</div>';
					} else if (value == 5) {
						return '<div>其他</div>';
					} else {
						return '<div></div>';
					}
				}	
	        },
	        {field:'identityCardNumber',title:'证件号码',width: '10.5%',align:'center',
	        	formatter: function(value,row,index){
	        	    if (value == null) {
	        	      value = "";
	        	    }
	        	    
	        		return "<div title='"+value+"'>"+value+"</div>";
				}
	        },
	        {field:'mealType',title:'餐别',width: '3.5%',align:'center',
	        	formatter: function(value,row,index){
					if(value == 1) {
						return "<div>常规</div>";
					} else if(value == 2) {
						return '<div>清真</div>';
					}else if(value == 3) {
						return '<div>素餐</div>';
					}else if(value == 4) {
						return '<div>特餐</div>';
					}
				}	
	        },
	        {field:'remark',title:'备注',width:'10%',align:'center',
	        	formatter: function(value,row,index){
                    if (value == null) {
                      value = "";
                    }
	        		return "<div title='"+value+"' style='width:184px; height: 38px; line-height: 38px; overflow: hidden; text-overflow: ellipsis;'>"+value+"</div>";
				}	
	        },
	        {field:'hotel',title:'宾馆',width: '11%',align:'center',
	        	formatter: function(value,row,index){
	        	    if (value == null) {
                      value = "";
                    }
	        	    var html = [];
	        	    html.push('<div class="jqx-column">');
	        	   
//	        	    html.push(' <span class="opera-btn-list">');
//	        	    /*
//	        	    html.push('  <a class="float-right edit-btn" title="编辑入住信息" onclick="updateInHotelInfo(this);" inhotelid=\'' + JSON.stringify(row) + '\'></a>');
//	        	    html.push('  <a class="float-right add-btn"  title="添加入住信息" onclick="addInHotelInfo(this);" inhotelid=\'' + JSON.stringify(row) + '\'></a>');*/
//	        	    html.push('  <a class="float-right hotel-detail-btn" onclick="showInHotelDetailInfo(this);" inhotelid=\'' + JSON.stringify(row) + '\' title="入住详细信息"></a>');
//	        	    html.push(' </span>');
	        	    html.push(' <span class="hotel-name" title="'+ value +'">' + value + '</span>');
	        	    html.push('</div>');
	        	    
	        		return html.join("");
				}	
	        },
	        {field:'roomNumber',title:'房号',width: '3.5%',align:'center',
	        	formatter: function(value,row,index){
                    if (value == null) {
                      value = "";
                    }
	        		return "<div title='"+value+"'>"+value+"</div>";
				}	
	        },
	        {field:'extension',title:'分机',width: '4%',align:'center',
	        	formatter: function(value,row,index){
                    if (value == null) {
                      value = "";
                    }
	        		return "<div title='"+value+"'>"+value+"</div>";
				}	
	        },
	        {field:'checkInDate',title:'入住时间',width: '6%',align:'center'},
	        {field:'checkoutDate',title:'退房时间',width: '6%',align:'center'},
	        
	    ]] ,
	    
	    onLoadSuccess:function(){
        	if(!isContactReadonly) {
        		$(this).datagrid('enableDnd');
        	}
	    	//行划入显示按钮
	    	/*$(".datagrid-body tr").bind("mouseover",function(event){
	    	 	$(this).find(".opera-btn-list").show();
	    	}).bind("mouseout",function(){
	        	$(this).find(".opera-btn-list").hide();
	    	});*/
		},
		
		onStopDrag: function(row){
			
        },
        
        onDrop:function(targetRow,sourceRow,point){
        	if(targetRow.fid!=sourceRow.fid){
          		return false;
          	}
        	var ids = [];
			 $(".sequence-id").each(function(i){
				 ids.push($(this).attr('sid'));
				 $(this).html(i+1);
			 }); 
			 ids.splice(ids.length-1,1);
			 $.ajax({
				 url:ctx+"/contact/updateContactSequence", 
				 data:{contactIds:ids.toString()},
				 dataType:'json',
				 type:'post',
				 success:function(data){
					 if(!data.success) {
						 showErrorMessage(data.message); 
					 }
				 },
				 error:function(){
					 showErrorMessage("请求失败！");
				 }
			 });	
        }
	});
}

//格式化时间信息
function formatDate(date){
	var newDate = new Date(date);
	//获取格式化时间
	return newDate.getFullYear()+"-" + (newDate.getMonth()+1) + "-" + newDate.getDate();
}

//校验选择日期是否已在其他日期之间
function checkDate(dateStr,id,th,own){
	var nameStr = $(th).attr('name');
	var ind='',outd='';
	if(nameStr =='indate'){
		ind = $(th).val();
		outd = $(th).parent('td').next('td').find('input').eq(0).val();
	}else{
		outd = $(th).val();
		ind = $(th).parent('td').prev('td').find('input').eq(0).val();
	}
	
	var index = 0;
	var flag = false;
	$("#hotelDetailDate tr").each(function(i){
		debugger;
		index++;
		errorMessage="第" + index + "行";
		var inId = $(this).find("input[type=text]").eq(0).attr('id');
		var checkInTime = $(this).find("input[type=text]").eq(4).val();//入住时间
		var checkOutTime = $(this).find("input[type=text]").eq(5).val();//退房时间
		if(checkInTime&&checkOutTime&&inId){
			var  indate   =   new   Date(checkInTime.replace(/-/g,   "/")); 
			var  outdate   =   new   Date(checkOutTime.replace(/-/g,   "/")); 
			var  nowdate   =   new   Date(dateStr.replace(/-/g,   "/"));
			
			var idd =  new   Date(ind.replace(/-/g,   "/"));
			var outdd =  new   Date(outd.replace(/-/g,   "/"));
			if(nowdate>indate && nowdate<outdate && id !=inId){
				parent.showErrorMessage("当前所选日期包含了第"+index+"行数据，将导致同一天会有两个房间");
				flag = true;
				return;
			}
			
			
			if(idd&&outdd){
				if(idd<indate && outdd>outdate && id !=inId){
					parent.showErrorMessage("当前所选日期在第"+index+"行数据的入住时间和退房时间之间，将会导致一人同一天入住多个房间");
					flag = true;
					return;
				}
			}
			
			
		}
	});
	/*var startTime = $(own).parents("tr").find("input.indate").val();
	var endTime = $(own).parents("tr").find("input.outdate").val();
	if(startTime != "" && dateStr != ""){
		var strSeparator = "-"; //日期分隔符
	    var oDate1;
	    var oDate2;
	    var iDays;
	    oDate1= startTime.split(strSeparator);
	    oDate2= dateStr.split(strSeparator);
	    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
	    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
	    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
	    $(own).parents("tr").find("td:last-child").find("input[type=text]").val(iDays);
	}else{
		$(own).parents("tr").find("td:last-child").find("input[type=text]").val("");
	}*/
	if($(th).hasClass("indate")){
		var endTime = $(th).parents("tr").find("input.outdate").val();
		var startTime = dateStr;
		if(endTime != ""){
			var strSeparator = "-"; //日期分隔符
		    var oDate1;
		    var oDate2;
		    var iDays;
		    oDate1= startTime.split(strSeparator);
		    oDate2= endTime.split(strSeparator);
		    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
		    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
		    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
		    $(own).parents("tr").find("td:last-child").find("input[type=text]").val(iDays);
		}
	}
	if($(th).hasClass("outdate")){
		var startTime = $(th).parents("tr").find("input.indate").val();
		var endTime = dateStr;
		if(startTime != ""){
			var strSeparator = "-"; //日期分隔符
		    var oDate1;
		    var oDate2;
		    var iDays;
		    oDate1= startTime.split(strSeparator);
		    oDate2= endTime.split(strSeparator);
		    var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
		    var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
		    iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
		    $(own).parents("tr").find("td:last-child").find("input[type=text]").val(iDays);
		}
	}
	
	
	if(flag){
		$(th).val('');
	}
}
//格式化金额
function formatMoney(own){
	var $this = $(own);
	if($this.val() != ""){
		var price = $this.val().replace(/,/g, "");
		$this.next("input[type=hidden]").val(price);
		$this.val(fmoney(price));
	}else {
		$this.next("input[type=hidden]").val('');
	}
}
//取消格式化的金额
function noFormatMoney(own){
	var $this = $(own);
	if($this.val() != ""){
		var price = $this.val().replace(/,/g, "");
		$this.val(price);
	}
}
//只允许输入数字
function onlyNnumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}


//定义全局变量
var contactInfo;

//关闭入住信息弹窗
function closeContactDetailInfo(){
	//是否改变标识默认没有改变
	var saveFlag = false;
	//校验数据，判断是否改变，如果改变，给出提示信息
	if (contactInfo == undefined || contactInfo == null) {
		closeRightPopupWin();
		return ;
	}
	
	//判断是否修改姓名
	var inputName = $("#contactName").val();
	var contactName = '';
	if (contactInfo.contactName != undefined && contactInfo.contactName != null && contactInfo.contactName != 'null') {
		contactName = contactInfo.contactName;
	}
	if (inputName != contactName) {
		saveFlag = true;
	}
	
	//性别
	var inputSex = $("input[name='sex']:checked").val();
	var sex = '';
	if (contactInfo.sex != undefined && contactInfo.sex != null && contactInfo.sex != 'null') {
		sex = contactInfo.sex;
	}
	if (inputSex != sex) {
		saveFlag = true;
	}
	
	//手机号
	var inputPhone = $("#phone").val();
	var phone = '';
	if (contactInfo.phone != undefined && contactInfo.phone != null && contactInfo.phone != 'null') {
		phone = contactInfo.phone;
	}
	if (inputPhone != phone) {
		saveFlag = true;
	}
	
	//证件类型
	var identityCardType = $("#identityCardType option:selected").val();
	var identityCardTypeStr = '';
	if (contactInfo.identityCardType != undefined && contactInfo.identityCardType != null && contactInfo.identityCardType != 'null') {
		identityCardTypeStr = contactInfo.identityCardType;
	}
	if (identityCardType != identityCardTypeStr) {
		saveFlag = true;
	}
	
	//证件号
	var idNumber = $("#idNumber").val();
	var identityCardNumber = '';
	if (contactInfo.identityCardNumber != undefined && contactInfo.identityCardNumber != null && contactInfo.identityCardNumber != 'null') {
		identityCardNumber = contactInfo.identityCardNumber;
	}
	if (idNumber != identityCardNumber) {
		saveFlag = true;
	}
	
	//部门
	var department = $("#department").val();
	var departmentIds = '';
	if (contactInfo.departmentIds != undefined && contactInfo.departmentIds != null && contactInfo.departmentIds != 'null') {
		departmentIds = contactInfo.departmentIds;
	}
	
	if (department!= undefined && department.length > 0) {
		for(var i =0; i <department.length; i++){
			if (departmentIds.indexOf(department[i]) == -1) {
				saveFlag = true;
			}
		}
	}else if (department != departmentIds) {
		saveFlag = true;
	}
	
	//职务
	var duties = $("#duties").val();
	var sysRoleIds = '';
	if (contactInfo.sysRoleIds != undefined && contactInfo.sysRoleIds != null && contactInfo.sysRoleIds != 'null') {
		sysRoleIds = contactInfo.sysRoleIds;
	}
	
	if (duties != undefined && duties.length>0) {
		for(var i = 0; i< duties.length; i++){
			if (sysRoleIds.indexOf(duties[i]) == -1) {
				saveFlag = true;
			}
		}
	}else if (duties != sysRoleIds) {
		saveFlag = true;
	}
	
	//入组日期
	var checkInDate = $("#enterDate").val();
	var enterDate = '';
	if (contactInfo.enterDate != undefined && contactInfo.enterDate != null && contactInfo.enterDate != 'null') {
		enterDate = contactInfo.enterDate;
	}
	if (checkInDate != enterDate) {
		saveFlag = true;
	}
	
	//离组时间
	var checkoutDate = $("#leaveDate").val();
	var leaveDate = '';
	if (contactInfo.leaveDate != undefined && contactInfo.leaveDate != null && contactInfo.leaveDate != 'null') {
		leaveDate = contactInfo.leaveDate;
	}
	if (checkoutDate != leaveDate) {
		saveFlag = true;
	}
	
	//备注
	var remark = $("#remark").val();
	var remarkStr = '';
	if (contactInfo.remark != undefined && contactInfo.remark != null && contactInfo.remark != 'null') {
		remarkStr = contactInfo.remark;
	}
	if (remark != remarkStr) {
		saveFlag = true;
	}
	
	//餐别
	var mealType = $("input[name='mealType']:checked").val();
	var mealTypeStr = '';
	if (contactInfo.mealType != undefined && contactInfo.mealType != null && contactInfo.mealType != 'null') {
		mealTypeStr = contactInfo.mealType;
	}
	if (mealType != mealTypeStr) {
		saveFlag = true;
	}
	
	//公开到组
	var ifOpen = $("input[name='ifOpen']:checked").val();
	var ifOpenStr = '';
	if (contactInfo.ifOpen != undefined && contactInfo.ifOpen != null && contactInfo.ifOpen != 'null') {
		ifOpenStr = contactInfo.ifOpen;
	}
	if (ifOpen != ifOpenStr) {
		saveFlag = true;
	}
	
	
	if(saveFlag){
		tipInfoBox("提示","您修改了信息，是否要保存？", function (){
			confirmSaveContact();//保存
		});
	}else{
		closeRightPopupWin();
	}
	
}

//在局联系列表中点击姓名,展开编辑信息窗口
function updatePeson(contactId){
//	var contactData = JSON.parse($(own).attr('sid'));
//	var contactId = contactData.contactId;
	$("#rightPopUpWin").animate({"right": 0},500).show();
	
	$('#form').jqxValidator('hide');
	
	$("#needContactId").val(contactId);
	
	$("#contact-btn-delete").show();

	$("._table input[type=text]").val('');
	$("#rightPopUpWin .selectpicker").selectpicker('deselectAll');
	
	//为了同步数据，此处重新请求联系人数据
	$.ajax({
		url:"/contact/queryCrewContactList",
		dataType:'json',
		type:'post',
		async: false,
		data:{contactId:contactId},
		success:function(response) {
			if(response.length != 0){
				//为表单赋值
				valueToForm(response[0]);
				contactInfo = response[0];
			}
		}
	});
}

//为表单赋值
function valueToForm(contactData){
	$("input[name=contactId]").val(contactData.contactId);
	$("input[name=contactName]").val(contactData.contactName);
	$("input[name=phone]").val(contactData.phone);
	$("input[name=sex][value="+contactData.sex+"]").attr("checked",true); 
	$("#identityCardType").val(contactData.identityCardType);
	$("#idNumber").val(contactData.identityCardNumber);
	var depArray = [];
	if (contactData.departmentIds != null) {
		depArray = contactData.departmentIds.split(',');
	}
	var dutyArray = [];
	if (contactData.sysRoleIds != null) {
		dutyArray = contactData.sysRoleIds.split(',');
	}
	$("#department").selectpicker("val", depArray);
	$("#duties").selectpicker("val", dutyArray);
	$("input[name=enterDate]").val(contactData.enterDate);
	$("input[name=leaveDate]").val(contactData.leaveDate);
	$("input[name=remark]").val(contactData.remark);
	$("input[name=mealType][value="+contactData.mealType+"]").attr("checked",true);
	$("input[name=hotel]").val(contactData.hotel);
	$("input[name=roomNumber]").val(contactData.roomNumber);
	$("input[name=extension]").val(contactData.extension);
	$("input[name=checkInDate]").val(contactData.stayingDate);
	$("input[name=checkoutDate]").val(contactData.checkoutDate);
	$("input[name=ifOpen][value="+contactData.ifOpen+"]").attr("checked",true);
	
	$(".selectpicker").selectpicker("refresh");
}





//点击按钮时,设置该行被选中
function checkInout(index){
	$('#contact').datagrid('selectRow',index);
}

//设置联系人是否公开到组
function ifOpenContactInfo(own){
	var ifOpen = 0;
	var _this = $(own);
	if(_this.is(":checked")){
		ifOpen = 1;
	}
	var row = $("#contact").datagrid('getSelected');
	var contactId = row.contactId;
	$.ajax({
		url:url = ctx+"/contact/setIfOpen",
		dataType:'json',
		type:'post',
		data:{contactId:contactId, ifOpen:ifOpen},
		success:function(response) {//成功后需要重新加载表格，否则信息没变
			if (!response.success) {
				showErrorMessage(response.message);
			}else{
				loadGrid();
			}
		}
	});
}

//点击高级查询按钮,打开查询界面
function searchContact(){
	$('#searchWindow').jqxWindow('open');
}



//取消按钮事件
function closeRightPopupWin(){
	clearInterval(timer);
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-width}, 500);
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	},500);
}


//基本信息
/*function contactInfo(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".public-contact").hide();
	$(".contact-info-div").show();
}*/

//旅馆信息
function hotelInfo(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".public-contact").hide();
	$(".contact-hotel-div").show();
}

/**
 * 弹出提示框
 * @param title 标题
 * @param content 内容
 */
function tipInfoBox(title,content,obj){
	$('#eventAll').jqxWindow('open');
	if(title!=undefined || title!=null)
		$('#eventAll').jqxWindow('setTitle', title);
	if(content!=undefined || content!=null)
		$('#eventContent').html(content);
	if(content.length > 15)
		$('#eventContent').css("margin-top","13px");
	$('#eventAll').unbind("close");
	$('#eventAll').on('close', function (event) {
		if (event.args.dialogResult.OK) {
			$(obj);
        }
    });
}

//初始化消息弹出窗口
function initTipWindow(){
	$('#eventAll').jqxWindow({
         maxHeight: 150, 
         maxWidth: 280, 
         minHeight: 30, 
         minWidth: 250, 
         height: 145, 
         width: 270,
         modalZIndex: 20010,
         resizable: false, 
         isModal: true,
         theme:theme,
         modalOpacity: 0.3,
         okButton: $('#sure'), 
         cancelButton: $('#closeBtn'),
         autoOpen: false,
         initContent: function () {
             $('#sure').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').on("click", function(){
            	 closeRightPopupWin();//关闭
            	 $('#eventAll').jqxWindow('close');
             });
         }
     });
}