<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is find back password page">
	
    <link rel="stylesheet" href="<%=basePath%>/css/findPassword.css" type="text/css" />
    
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script>
        $(document).ready(function() {
           var phoneValid = false;
           var codeValid = false;
           var passwordValid = false;
           var confirmPassValid = false;
           
           var verifyCodeChecked = false;
           var verifyCodeInt;
            //获取验证码点击事件
           $("#getVerifyCodeBtn").on("click", function() {
               if ($(this).hasClass("disabled")) {
                   return false;
               }
               if (!phoneValid) {
                   return false;
               }
               var phone = $("#phone").val();
               $.ajax({
                   url: "/interface/verifyCodeManager/sendVerifyCode",
                   type: "post",
                   data: {"phone": phone, "type":1},
                   success: function(response) {
                       if (response.success) {
                           $("#getVerifyCodeBtn").addClass("disabled");
                           $("#getVerifyCodeBtn").val("重新发送（60s）");
                           
                           var totalSecond = 60;
                           verifyCodeInt = setInterval(function(event) {
                               totalSecond --;
                               $("#getVerifyCodeBtn").val("重新发送（"+ totalSecond +"s）");
                               
                               if (totalSecond == 0) {
                                   window.clearInterval(verifyCodeInt);
                                   $("#getVerifyCodeBtn").removeClass("disabled");
                                   $("#getVerifyCodeBtn").val("获取验证码");
                               }
                           }, 1000);
                           
                           $("#verifyCode").focus();
                       } else {
                           $("#phoneErrorMessage").text(response.message);
                       }
                   }
               });
           });
           //校验验证码是否正确
           $("#verifyCodeBtn").on("click", function() {
               if ($(this).hasClass("disabled")) {
                   return false;
               }
               
               var phone = $("#phone").val();
               var verifyCode = $("#verifyCode").val();
               
               if (!phoneValid || !codeValid) {
                   return false;
               }
               
               $.ajax({
                   url: "/interface/verifyCodeManager/checkVerifyCode",
                   data: {"phone": phone, "verifyCode": verifyCode, "type": 1},
                   type: "post",
                   success: function(response) {
                       if (response.success) {
                           //手机号、验证码只读，获取验证码和确定按钮不可用
                           $("#phone").attr("disabled", "disabled");
                           $("#verifyCode").attr("disabled", "disabled");
                           window.clearInterval(verifyCodeInt);
                           $("#getVerifyCodeBtn").addClass("disabled");
                           $("#getVerifyCodeeBtn").val("获取验证码");
                           
                           $("#verifyCodeBtn").hide();
                           $("#checkSuccessMessage").text("校验成功");
                           
                           $("#passwordDiv").show();
                       } else {
                           $("#checkErrorMessage").text(response.message);
                       }
                   }
               });
               
           });
           //修改密码
           $("#modifyPassBtn").on("click", function() {
               if ($(this).hasClass("disabled")) {
                   return false;
               }
               
               var phone = $("#phone").val();
               var verifyCode = $("#verifyCode").val();
               var password = $("#password").val();
               var confirmPassword = $("#confirmPassword").val();
               
               if (!phoneValid || !codeValid || !passwordValid || !confirmPassValid) {
                   return false;
               }
               
               $.ajax({
                   url: "/userManager/findbackPassword",
                   data: {"phone": phone, "verifyCode": verifyCode, "newPassword": password, "confirmPassword": confirmPassword},
                   type: "post",
                   success: function(response) {
                       if (response.success) {
                           //修改密码按钮隐藏、成功操作信息显示、跳转到登录页面
                           $("#modifyPassBtn").hide();
                           $("#modifySuccessMessage").text("密码修改成功，3秒后自动返回登录页面");
                           
                           var totalSecond = 3;
                           var modifySuccessInt = setInterval(function(event) {
                               totalSecond --;
                               $("#modifySuccessMessage").text("密码修改成功，"+ totalSecond +"秒后自动返回登录页面");
                               
                               if (totalSecond == 0) {
                                   window.clearInterval(modifySuccessInt);
                                   toLoginPage();
                               }
                           }, 1000);
                           
                       } else {
                           $("#modifyErrorMessage").text(response.message);
                       }
                   }
               });
           });
           
           
           //手机号文本框失去焦点事件，判断是否符合手机号规则
           $("#phone").on("blur", function() {
               $(this).siblings(".descript").hide();
               var value = $(this).val();
           
               if (value == "") {
                   $("#phoneErrorMessage").text("请填写手机号");
                   return false;
               }
           
               //验证手机号
               if (!/^\d{11}$/.test(value)) {
                   $("#phoneErrorMessage").text("手机号不合法");
                   return false;
               }
               
               //校验手机号是否被注册
               var exists = true;
               $.ajax({
                   url: "/userManager/checkPhoneExist",
                   type: "post",
                   data: {"phone": value},
                   async: false,
                   success: function(response) {
                       if (response.success) {
                           if (!response.exists) {
                               $("#phoneErrorMessage").text("该手机号未注册");
                               exists = false;
                           }
                       } else {
                           $("#phoneErrorMessage").text(response.message);
                       }
                   }
               });
               
               if (!exists) {
                   return false;
               }
               
               phoneValid = true;
           });
           $("#phone").on("focus", function() {
               $("#phoneErrorMessage").text("");
               $(this).siblings(".descript").show();
               $("#errorMessage").text("");
           });
           
           $("#verifyCode").on("blur", function() {
               var value = $(this).val();
               if (value == "") {
                   $("#verifyCodeErrorMessage").text("请填写验证码");
                   return false;
               }
               codeValid = true;
           });
           $("#verifyCode").on("focus", function() {
               $("#verifyCodeErrorMessage").text("");
               $("#errorMessage").text("");
           });
           
           //密码文本框失去焦点事件，判断是否符合密码规则
           $("#password").on("blur", function() {
               $(this).siblings(".descript").hide();
           
               var value = $(this).val();
               if (value == "") {
                   $("#passwordErrorMessage").text("请输入密码");
                   return false;
               }
               if (value.length < 6) {
                   $("#passwordErrorMessage").text("密码至少6位");
                   return false;
               }
               passwordValid = true;
           });
           $("#password").on("focus", function() {
               $(this).siblings(".descript").show();
               $("#passwordErrorMessage").text("");
               $("#errorMessage").text("");
           })
           //"确认密码文本框"失去焦点事件，判断是否和密码一样
           $("#confirmPassword").on("blur", function() {
               var confirmPass = $(this).val();
               var password = $("#password").val();
               
               if (password == "") {
                   $("#passwordErrorMessage").text("请输入密码");
                   return false;
               }
               if (confirmPass == "") {
                   $("#confirmPassErrorMessage").text("请输入密码");
                   return false;
               }
               if (password != confirmPass) {
                   $("#confirmPassErrorMessage").text("确认密码和密码不一致");
                   return false;
               }
               confirmPassValid = true;
           });
           $("#confirmPassword").on("focus", function() {
               $("#errorMessage").text("");
               $("#confirmPassErrorMessage").text("");
           });
           
           
        });
        
        //跳转到登录页面
		function toLoginPage() {
		    window.location.href = "/toLoginPage";
		}
    </script>
  </head>
  
  <body>
	  <div style="width: 100%; height: 100%; overflow: auto; background: linear-gradient(#01579b,#039be5, #01579b);">
	    <div class="find-password">
	        <div id="phoneDiv" class="find-password-sub-div">
	            <div class="find-pass-title">
	                <div class="title-sub-div">
	                    <label>找回密码</label> 
	                    <a class="login-link" href="javascript:(0)" onclick="toLoginPage()">登录>></a>
	                </div>
	            </div>
	            <ul>
		            <li class="error-message-li">
		                <label id="checkErrorMessage"></label> 
		            </li>
	                <li>
	                    <input id="phone" type="text" placeHolder="手机号" autofocus />
	                    <label class="necessary">*</label>
	                    <label class="error-message" id="phoneErrorMessage"></label>
	                </li>
	                <li>
	                    <input id="verifyCode" type="text" placeHolder="验证码，有效期为1分钟" />
	                    <input class="get-verifycode-btn" id="getVerifyCodeBtn" type="button" value="获取验证码">
	                    <label class="necessary">*</label>
	                    <label class="error-message" id="verifyCodeErrorMessage"></label>
	                </li>
	                <li class="find-pass-btn-li">
	                    <input class="find-pass-button" id="verifyCodeBtn" type="button" value="确  定" />
	                </li>
	                <li class="success-msg-li">
	                    <label class="success-message" id="checkSuccessMessage"></label>
	                </li>
	            </ul>
	        </div>
	        <div id="passwordDiv" class="find-password-sub-div password-div">
	            <ul>
		            <li class="error-message-li">
		                <label id="modifyErrorMessage"></label> 
		            </li>
	                <li>
	                    <input id="password" type="password" placeHolder="密码" />
	                    <label class="necessary">*</label>
		                <label class="descript">6~18位字母、数字、下划线</label>
		                <label class="error-message" id="passwordErrorMessage"></label>
	                </li>
	                <li>
	                    <input id="confirmPassword" type="password" placeHolder="确认密码" onpaste="return false" />
		                <label class="necessary">*</label>
		                <label class="error-message" id="confirmPassErrorMessage"></label>
	                </li>
	                <li class="find-pass-btn-li">
	                    <input class="find-pass-button"  id="modifyPassBtn" type="button" value="确  定" />
	                </li>
		            <li class="success-msg-li">
		                <label class="success-message" id="modifySuccessMessage"></label>
		            </li>
	            </ul>
	        </div>
	    </div>
	  </div>
  </body>
</html>
