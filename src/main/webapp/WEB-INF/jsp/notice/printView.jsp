<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
		<head>
				<meta charset="utf-8">
				<% 
				java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date currentTime = new java.util.Date();//得到当前系统时间
				String nowDate = formatter.format(currentTime); //将日期时间格式化 
				%>
				<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
				<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
				
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/webuploader.css">
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/notice/slide.css">
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/exportLoading.css">
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/html2canvas.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/webuploader/webuploader.min.js"></script>
				
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/base.js"></script>
				<!-- sweetalter -->
				
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/notice/printView.css">
				
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/sweetalert/sweetalert.css">
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/sweetalert/sweetalert.min.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/common.js"></script>
				
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/slide.js"></script>
				
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxcore.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxnotification.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/globalization/globalize.js"></script>
				
				
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/printView.js"></script>
				
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
				<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/printView_new.js"></script>
				<title>通告单打印预览</title>
		</head>

		<body style="overflow: auto;">
		    <!-- <div class="my-container"> -->
		    <input type="hidden" name="noticeId" id="noticeId" value="${noticeId}"/>
		    <input type="hidden" id="rowHeightValue" value="30">
		    <input type="hidden" id="fontSizeValueTD" value="0.78">
		    <input type="hidden" id="fontSizeValueText" value="1">
		    
		    <!-- <div class="border-sign" id="borderSign">
		        <div class="border-sign-top"></div>
		        <p class="text-tips">A4纸页面高度</p>
		        <div class="border-sign-bottom"></div>
		    </div> -->
		    
		         <div id="tableContainer" style="background-color: white;">
		            <div class="table-header-div" id="tableHeaderDiv">
		                <h5 id="noticeTableTitle"></h5>
		                <p class="notice-date" id="date"></p>
		                <div class="header-tips" id="headerTips">
		                    <p class="header-left-tips" id="shootLocationInfos"></p>
		                    <p class="header-right-tips" id="groupDirector"></p>
		                </div>
		            </div> 
		            <div class="table-body-div" id="tableBodyDiv">
		                <table id="noticeTable" cellspacing = 0, cellpadding = 0>
		                
		                </table>
		            </div>
		           </div>
		            <div id="selectRoleWindow" style="display:none;">
						        <div>
						            <div class="mainWin">
						                <div id="toSelectUserListDiv" class="toSelectUserListDiv">
						                  <!-- 添加到全部人员按钮 -->
						                  <input type="button" class='send-all-button' value='发送给全组' onclick='selectAllUser()'>
						                    <span style="display: block;margin-top: 29px;">用户列表：</span>
						                    <!-- 待选择用户的表格 -->
						                    <div class="toSelectUserListGrid" id="toSelectUserListGrid"></div>
						                </div>
						                <div class="pushContent">
						                    <div class="selectedUserList">
						                        <div class="selectUser_title"><span>接收人：</span><input type="checkbox" id="needFedback">需要回复意见
						                        
						                        	<input type="checkbox" id="pushTips" checked="checked">发布推送消息
						                        </div>
						                        <div class="selectedUserTagList">
						                        <ul id="selectedUserTagListUl">
						                            <!-- <li>张三<a href="javascript:void(0)" class="closeTag"></a></li>
						                            <li>我是大哥<a href="javascript:void(0)" class="closeTag"></a></li> -->
						                        </ul>
						                        </div>
						                    </div>
						                    <div class="push_title">
						                        <span>标题<span style="color:red;"> *</span>：</span>
						                          <input type="text" id="noticeTitle" class="titleInput" >
						                    </div>
						                    <div class="content">
						                        <span>内容<span style="color:red;"> *</span>：</span>
						                           <textarea id="noticeContent" rows="" cols="" class="contentTa"></textarea>
						                    </div>
						                    <div class="noticeImg">
						                        <span>通告单预览图：</span><span id="uploadImg" class="uploadImg">上传通告单图片</span>
						                        <!-- <div id="imgList" class="img_list">
						                            
						                        </div> -->
						                        
						                        <div class="img_list thumbnail-container" id="thumbnail">
						                <button class="thumbnail-btn btn-prev disable"></button>
						                <button class="thumbnail-btn btn-next"></button>
						                <div class="thumbnail-slide-container">
						                    <ul class="thumbnail-list"></ul>
						                </div>
						            </div>
						                        
						                    </div>
						                </div>
						            </div>
						            <div class="publishBtn">
						                <input type="button" class='publsh-button' id="publish" value="发布" style="width:100px;height:25px;">
						                <input type="button" class='publsh-button' id="cancel" value="取消" style="width:100px;height:25px;">
						            </div>
						        </div>
    </div>
    <div id="jqxNotification"></div>
		
		<!-- 悬浮框 -->
    <div class="suspension-frame" id="suspensionFrame">
        <div class="btn-container" id="btnContainer">
            <ul>
		            <li><a href="javascript:void(0)" title="点击加减增加或减少行高">行高</a></li>
		            <li><a href="javascript:void(0)" title="点击放大或缩小字体">字体</a></li>
		            <li><a href="javascript:void(0)" onclick="printNotice();">打印</a></li>
		            <li><a href="javascript:void(0)" onclick="downloadNotice();">导出</a></li>
		            <li><a href="javascript:void(0)" onclick="selectRole();">发布</a></li>
		            <li><a href="javascript:void(0)" onclick="backToEdit();">关闭</a></li>
            </ul>
        </div>
        <div class="adjust-div">
            <span class="add-btn" onclick="addRowHeight(this)" title="增加"></span>
            <span class="sub-btn" onclick="subRowHeight(this)" title="减少"></span>
        </div>
        <div class="adjust-font-div">
            <span class="add-btn" onclick="addFontSize(this)" title="放大"></span>
            <span class="sub-btn" onclick="subFontSize(this)" title="缩小"></span>
        </div>
    </div>
    
     
    <!-- 显示正在加载中 -->
		<div id="loadingDiv" class="show-loading-container" style="display: none;">
		  <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
		</div>            
				            
		            
		   <!--  </div> -->
		   
		</body>
</html>