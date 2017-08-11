<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<title>注册账号</title>
    <base href="<%=basePath%>">
    
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="description" content="This is register page">
	
    <link rel="stylesheet" href="<%=basePath%>/css/register.css" type="text/css" />
    
	<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	
	<script>
	   $(document).ready(function() {
		   var phoneValid = false;
	       var codeValid = false;
	       var realNameValid = false;
	       var passwordValid = false;
	       var confirmPassValid = false;
	       
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
	               data: {phone: phone, type: 2},
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
	                       $("#phoneErrorMessage").text(response.message);
	                   }
	               }
	           });
	       });
	       
	       //注册
	       $("#registerBtn").on("click", function() {
	           if ($(this).hasClass("disabled")) {
	               return false;
	           }
	       
	           var phone = $("#phone").val();
	           var verifyCode = $("#verifyCode").val();
	           var realName = $("#realName").val();
	           var password = $("#password").val();
	           var confirmPassword = $("#confirmPassword").val();
	       
	           if (phone == "" || verifyCode == "" || realName == "" || password == "" || confirmPassword == "") {
	               $("#errorMessage").text("部分信息不完整，请检查");
	               return false;
	           }
	           
	           if (!phoneValid || !codeValid || !realNameValid || !passwordValid || !confirmPassValid) {
	               $("#errorMessage").text("部分信息不完整，请检查");
	               return false;
	           }
	           
	           $.ajax({
                   url: "/userManager/register",
                   type: "post",
                   data: {"phone": phone, "verifyCode": verifyCode, "realName": realName, "password": password, "confirmPassword": confirmPassword},
                   success: function(response) {
                       if (response.success) {
                           $("#registerBtn").hide();
                           $("#successMessage").text("注册成功，3秒后自动返回登录页面");
                           
                           var totalSecond = 3;
                           var int = setInterval(function(event) {
                               totalSecond --;
                               $("#successMessage").text("注册成功，"+ totalSecond +"秒后自动返回登录页面");
                               
                               if (totalSecond == 0) {
                                   window.clearInterval(int);
                                   toLoginPage();
                               }
                           }, 1000);
                           
                       } else {
                           $("#errorMessage").text(response.message);
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
               var exists = false;
               $.ajax({
                   url: "/userManager/checkPhoneExist",
                   type: "post",
                   data: {"phone": value},
                   async: false,
                   success: function(response) {
                       if (response.success) {
                           if (response.exists) {
                               $("#phoneErrorMessage").text("该手机号已被注册");
                               exists = true;
                           }
                       } else {
                           $("#phoneErrorMessage").text(response.message);
                       }
                   }
               });
               
               if (exists) {
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
           
           //真实姓名文本框失去焦点校验非空
           $("#realName").on("blur", function() {
               $(this).siblings(".descript").hide();
               var value = $(this).val();
               if (value == "") {
                   $("#realNameErrorMessage").text("请填写真实姓名");
                   return false;
               }
               realNameValid = true;
           });
           $("#realName").on("focus", function() {
               $("#realNameErrorMessage").text("");
               $(this).siblings(".descript").show();
               $("#errorMessage").text("");
           });
           
	       //密码文本框失去焦点事件，判断是否符合密码规则
	       $("#password").on("blur", function() {
	           $(this).siblings(".descript").hide();
	       
	           var value = $(this).val();
	           if (value == "") {
	               $("#passwordErrorMessage").text("请设置密码");
	               return false;
	           }
	           if (value.length < 6) {
	               $("#passwordErrorMessage").text("密码至少6位");
	               return false;
	           }
               if (value.length > 18) {
                   $("#passwordErrorMessage").text("密码至最多18位");
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
                   $("#passwordErrorMessage").text("请设置密码");
                   return false;
               }
               if (confirmPass == "") {
                   $("#confirmPassErrorMessage").text("请设置密码");
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
           
           //同意条款点击事件，选中则注册按钮可用，不选中则注册按钮不可用
           $("#agree").on("click", function() {
               if ($("#agree:checked").length == 1) {
                   $("#registerBtn").removeClass("disabled");
               } else if (!$("#registerBtn").hasClass("disabled")) {
                   $("#registerBtn").addClass("disabled");
               }
           });
	   });
	
	
	   //跳转到登录页面
	   function toLoginPage() {
	       window.location.href = "/toLoginPage";
	   }
	   
	   //注册协议
	   function registAgreement() {
	       window.open("/userManager/toAgreementPage");
	   }
	</script>
	
  </head>
  
  <body>
	  <div style="width: 100%; height: 100%; overflow: auto; background: linear-gradient(#01579b,#039be5, #01579b);">
	    <div class="register">
	        <ul>
	            <li class="register-title">
	                <label>注册</label> 
	                <a class="login-link" href="javascript:(0)" onclick="toLoginPage()">登录>></a>
	            </li>
	            <li class="error-message-li">
	                <label id="errorMessage"></label> 
	            </li>
	            <li>
	                <input id="phone" type="text" placeHolder="手机号" autofocus />
	                <label class="necessary">*</label>
	                <label class="descript">仅支持大陆手机号</label>
	                <label class="error-message" id="phoneErrorMessage"></label>
	            </li>
	            <li>
	                <input id="verifyCode" type="text" placeHolder="验证码，有效期为1分钟" />
	                <input class="get-verifycode-btn" id="getVerifyCodeBtn" type="button" value="获取验证码">
	                <label class="necessary">*</label>
	                <label class="error-message" id="verifyCodeErrorMessage"></label>
	            </li>
	            <li>
	                <input id="realName" type="text" placeHolder="真实姓名" />
	                <label class="necessary">*</label>
	                <label class="descript">为方便剧组管理，请使用真实姓名</label>
	                <label class="error-message" id="realNameErrorMessage"></label>
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
	            <li>
	                <label class="descript"><input id="agree" type="checkbox" checked/>我已阅读并接受 
	                    <a href="javascript:(0)" class="server-clause" onclick="registAgreement()">服务条款</a>
	                </label>
	            </li>
	            <li class="register-btn-li">
	                <input class="register-button" id="registerBtn" type="button" value="立即注册" />
	            </li>
	            <li class="success-msg-li">
	                <label class="success-message" id="successMessage"></label>
	            </li>
	        </ul>
	    </div>
	  </div>
  </body>
</html>
