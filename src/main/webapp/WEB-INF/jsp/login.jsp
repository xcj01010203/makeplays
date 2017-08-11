<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>剧易拍</title>
<link rel="shortcut icon" href="<%=request.getContextPath()%>/images/favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/login.css?version=20170627" type="text/css" />
	
<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dateUtils.js"></script>
   
    
<script type="text/javascript">

$(document).ready(function(){
	myBrowser();
	remindWebVersion();
    $("#loginForm").on("keyup", function(event) {
        var key = event.keyCode;
        if (key == 13) {
            submit();
        }
    });
});
function myBrowser(){
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    //判断是否chrome浏览器
    if (userAgent.indexOf("Chrome") > -1){
    }else{
      alert("您当前的浏览器可能存在兼容问题，会造成部分功能无法使用，建议使用最新版谷歌浏览器或者360浏览器极速模式！");
    }
 
}
//版本提醒
function remindWebVersion() {
	$.ajax({
	    type: 'post',
	    url: '/webVersionInfoManager/remindWebVersion',
	    data: {},
	    dataType: 'json',
	    success: function (result) {
	       if (result.success) {
	           if(result.isRemind) {
	        	   var webVersionInfo=result.webVersionInfo;
	        	   var createTime = webVersionInfo.createTime;
	        	   $("#updateTime").html(new Date(createTime).Format('yyyy-MM-dd'));
	        	   $("#updateLog").html(webVersionInfo.userUpdateLog.replace(/\n/g,'<br>'));
	        	   showVersionWin();
	           }
	       } else {
	           //$("#errorMessage").text(result.message);
	       }
	    }
	});
}
function loadTopWindow(){
    if (window.top!=null && window.top.document.URL!=document.URL) {
        window.top.location= document.URL;
    }
};

//提交登录表单
function submit() {
    var userName = $("#userName").val();
    var password = $("#password").val();
    if (userName == "") {
        $("#errorMessage").text("请输入用户名");
        return false;
    }
    if (password == "") {
        $("#errorMessage").text("请输入密码");
        return false;
    }
    
    $.ajax({
	    type: 'post',
	    url: '/login',
	    data: {"userName": userName, "password": password},
	    dataType: 'json',
	    async: false,
	    success: function (param) {
	       if (param.success) {
	           if (param.noJionCrew) {
	               window.location.href = "/userManager/toUserCenterPage?activeTagType=7";
	           } else {
	               window.location.href = "/toIndexPage";
	           }
	       } else {
	           $("#errorMessage").text(param.message);
	       }
	    }
	})
}

//跳转到注册页面
function toRegisterPage () {
    window.location.href = "/toRegisterPage";
}

//跳转到忘记密码页面
function toForgetPassPage() {
    window.location.href = "/toForgetPassWordPage";
}

//显示版本升级窗口
function showVersionWin(){
	$(".my-container").show();
	setTimeout(function(){
		if($("#flipContainer").hasClass("flip-rolate")){
			$("#flipContainer").removeClass("flip-rolate");
		}else{
			$("#flipContainer").addClass("flip-rolate");
		}
	}, 600);
}
//隐藏版本升级窗口
function hideVersionWin(event){
	$("#flipContainer").removeClass("flip-rolate");
	setTimeout(function(){
		$("#flipContainer").hide();
		$(".my-container").hide();
	},600);
	event.stopPropagation();
}

</script>

    
</head>
<body style="background-color:#045485;" onload="loadTopWindow()">
<div class="indexjudge"></div>
<div style="position: absolute;top:6%;left:2%;"><img src="<%=request.getContextPath()%>/images/logo.png"/></div>
 <div style="position: absolute;top:83%;left:50%;margin-left:-150px;"><img src="<%=request.getContextPath()%>/images/ti.png"/></div>
 <div style="position: absolute;top:32%;left:0%;width: 100%;"><img src="<%=request.getContextPath()%>/images/daban-bj.png" style="width: 100%;"/></div>    
 <div id="loginForm">    
     <div class="login">
	    <ul class="login_c">
            <div class="error-message" id="errorMessage"></div>
	        <li>
	            <div class="text_password user-name-div">
	               <input name="userName" id="userName" type="text" placeHolder="用户名/手机号" autofocus>
	            </div>
	        </li>
	        <li>
	            <div class="text_password user-password-div">
	               <input name="password"  id="password"  type="password"  placeHolder="密码">
	            </div>
	        </li>
	        <input class="login_b" type="submit" value="登  录" onclick="submit()"/>
	        <div class="registerOrForget">
	           <ul class="login-nav">
	               <li><a href="javascript:(0)" onclick="toRegisterPage()">注册账号</a></li>
	               <li>|</li>
	               <li><a href="javascript:(0)" onclick="toForgetPassPage()">忘记密码</a></li>
	           </ul>
	           <!-- <a href="javascript:(0)" onclick="toRegisterPage()">注册账号</a> | 
	           <a href="javascript:(0)" onclick="toForgetPassPage()">忘记密码</a> -->
	        </div>
	    </ul>
	</div>
  </div>  
  <!-- 版本升级提醒 -->
<div class="my-container" style="display: none;">
			<div class="model"></div>
			
			<div class="flip-container" id="flipContainer" onclick="rolateFlip()">
				<div class="flipper">
					<div class="front">
						<!--前面内容-->
						<div class="flag">剧易拍</div>
					</div>
					<div class="back">
						<!--背面内容-->
						<div class="version">
							<div class="version-title">
								<h2>剧易拍升级啦</h2>
							</div>
							<div class="version-content" id="updateContent">
								<ul>
									<li>
										<p>更新时间:</p>
										<div class="version-time" id="updateTime"></div>
									</li>
									<li>
										<p>更新内容:</p>
										<div class="version-info" id="updateLog"></div>
									</li>
								</ul>
							</div>
							<div class="upadate-tips">友情提示：本次升级涉及部分页面更改，为避免出现异常，请先清理浏览器缓存后登录（浏览器快捷键Ctrl+Shift+Delete，勾选缓存文件后清理）</div>
							<div class="btn-list">
								<input type="button" class="know-btn" onclick="hideVersionWin(event)" value="知道了">
							</div>
						</div>
					</div>
				</div>
			</div>		
		</div>
</body>
</html>