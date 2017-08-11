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
			<meta http-equiv="description" content="This is my page">
			<!--
			<link rel="stylesheet" type="text/css" href="styles.css">
			-->
			<%-- <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" /> --%>
			<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/usercenter/userDetailInfo.css">
			
			<%-- <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
			<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxcore.js"></script>
			<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script>
			<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxpanel.js"></script> --%>
			
			<script type="text/javascript" src="<%=basePath%>/js/usercenter/userDetailInfo.js"></script>
  </head>
  
  <body>
      <div class="my-container">
      <!--个人信息-->
      <div class="person-detail-info">
        <ul>
         <li>
            <p>手机号码:</p>
            <div class="phone-number" id="phone"></div>
            <span class="modify-phone-number" id="modifyPhoneNumber" onclick="showModifyPhoneWin()">修改</span>
          </li>
          <li>
             <p>密&nbsp;&nbsp;&nbsp;&nbsp;码:</p>
             <div class="password"></div>
             <span class="modify-password" id="modifypassword" onclick="showModifyPassword()">修改</span>
          </li>
          <li>
            <p>姓&nbsp;&nbsp;&nbsp;&nbsp;名:</p>
            <input type="text" id="realName">
          </li>
          <li>
            <p>性&nbsp;&nbsp;&nbsp;&nbsp;别:</p>
            <input type="radio" name="sex" value=1>男
            <input type="radio" name="sex" value=0>女
          </li>
          <li>
            <p>年&nbsp;&nbsp;&nbsp;&nbsp;龄:</p>
            <input type="text" id="age">
          </li>
          
          <li>
            <p>邮&nbsp;&nbsp;&nbsp;&nbsp;箱:</p>
            <input type="text" id="email">
          </li>
        </ul>
        <div class="modify-baseinfo-btn-div">
          <input class="modify-baseinfo-btn" type="button" value="修 改" onclick="updateUserBaseInfo()">
        </div>
      </div>
      <!-- 修改手机号码窗口 -->
      <div class="jqx-window" id="modifyPhoneWin">
          <div>修改手机号</div>
          <div class="modify-phone-detail">
	          <form id="modifyPhoneDetailForm">
	              <ul>
	                  <li>
	                      <p>密码:</p>
	                      <input type="password" name="password" autofocus>
	                  </li>
	                  <li>
	                      <p>新手机号:</p>
	                      <input type="text" name="phone">
	                  </li>
	                  <li>
	                      <p>验&nbsp;证&nbsp;码&nbsp;:</p>
	                      <input class="verification-code-text" type="text" name="verifyCode">
	                      <input class="verification-code-btn" type="button" value="获取验证码" onclick="sendVerifyCode(this)">
	                  </li>
	              </ul>
	              <div class="win-btn-list">
	                  <input type="button" value="确定" onclick="modifyPhoneNumber()">
	                  <input type="button" value="取消" onclick="closeModifyPhoneWin()">
	              </div>
              </form>
          </div>
      </div>
      <div class="jqx-window" id="moidfyPasswordWin">
          <div>修改密码</div>
          <div class="modify-password">
              <ul>
                  <li>
                      <p>旧&nbsp;密&nbsp;码:</p>
                      <input type="password" id="oldPassword" autofocus>
                  </li>
                  <li>
                      <p>新&nbsp;密&nbsp;码:</p>
                      <input type="password" id="newPassword">
                  </li>
                  <li>
                      <p>确认密码:</p>
                      <input type="password" id="confirmPassword">
                  </li>
              </ul>
              <div class="win-btn-list">
                  <input type="button" value="确定" onclick="modifyPassword()">
                  <input type="button" value="取消" onclick="closeModifyPassowrdWin()">
              </div>
          </div>
      </div>
    </div>
  </body>
</html>
