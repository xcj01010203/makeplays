var int;
$(document).ready(function(){
	//初始化修改手机号码弹窗
	initModifyPhoneWin();
	//初始化修改密码窗口
	initModifyPasswordWin();
	//获取用户信息
	getUserInfo();
});

//初始化修改手机号码弹窗
function initModifyPhoneWin(){
	$("#modifyPhoneWin").jqxWindow({
		theme: theme,
		height: 300,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//显示修改手机号码弹窗
function showModifyPhoneWin(){
	$("#modifyPhoneWin").find("input[type='password']").val('');
	$("#modifyPhoneWin").find("input[type='text']").val('');
	if(typeof(int) != 'undefined' && int){
		window.clearInterval(int);
	}
	$(".verification-code-btn").removeClass("disabled");
    $(".verification-code-btn").val("获取验证码");
	$("#modifyPhoneWin").jqxWindow("open");
}
//关闭修改弹窗
function closeModifyPhoneWin(){
	$("#modifyPhoneWin").jqxWindow("close");
}

//初始化修改密码窗口
function initModifyPasswordWin(){
	$("#moidfyPasswordWin").jqxWindow({
		theme: theme,
		height: 300,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//显示修改密码弹窗
function showModifyPassword(){
	$("#moidfyPasswordWin").find("input[type='password']").val('');
	$("#moidfyPasswordWin").jqxWindow("open");
}
function closeModifyPassowrdWin(){
	$("#moidfyPasswordWin").jqxWindow("close");
}

//获取用户信息
function getUserInfo(){
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var userInfo = response.userInfo;
				if(userInfo != null){
					$("#phone").text(userInfo.phone);
					$("#realName").val(userInfo.realName);
					if(userInfo.sex == 1){
						$("input[type=radio][name=sex]").eq(0).attr("checked",true);
					}else{
						$("input[type=radio][name=sex]").eq(1).attr("checked",true);
					}
					$("#age").val(userInfo.age);
					$("#email").val(userInfo.email);
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//修改用户基本信息
function updateUserBaseInfo(){
	var subData = {};
	subData.realName = $("#realName").val();
	subData.sex = $("input[name=sex]:checked").val()-0;
	subData.age = $("#age").val();
	subData.email = $("#email").val();
	$.ajax({
		url: '/userManager/updateUserBaseInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("修改成功");
				getUserInfo();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//修改密码
function modifyPassword(){
	var subData = {};
	subData.password = $("#oldPassword").val();
	subData.newPassword = $("#newPassword").val();
	if($("#confirmPassword").val() != $("#newPassword").val()){
		showErrorMessage("新旧密码不一致");
		return;
	}
	$.ajax({
		url: '/userManager/updatePassword',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#moidfyPasswordWin").jqxWindow("close");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//获取验证码
function sendVerifyCode(own){
	
	var data = $("#modifyPhoneDetailForm").serializeObject();
	if(data.phone == ""){
		showInfoMessage("请填写手机号码");
		return;
	}
	if ($(own).hasClass("disabled")) {
		return false;
	}
	$.ajax({
		url: '/interface/verifyCodeManager/sendVerifyCode',
		type: 'post',
		data: {phone: data.phone, type: 3},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$(own).addClass("disabled");
                $(own).val("重新发送（60s）");
                
                var totalSecond = 60;
                int = setInterval(function(event) {
                    totalSecond --;
                    $(own).val("重新发送（"+ totalSecond +"s）");
                    
                    if (totalSecond == 0) {
                        window.clearInterval(int);
                        $(own).removeClass("disabled");
                        $(own).val("获取验证码");
                    }
                }, 1000);
                
                $(own).prev("input").focus();
                
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//修改手机号
function modifyPhoneNumber(){
	var data = $("#modifyPhoneDetailForm").serializeObject();
	if(data.phone == ""){
		showInfoMessage("请填写手机号码");
		return;
	}
	if(data.password == ""){
		showInfoMessage("请填写密码");
		return;
	}
	if(data.verifyCode == ""){
		showInfoMessage("请填写验证码");
		return;
	}
	$.ajax({
		url: '/userManager/updatePhone',
		type: 'post',
		data: data,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				getUserInfo();
				$("#modifyPhoneWin").jqxWindow("close");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
}

$.fn.serializeObject = function()
{
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [o[this.name]];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};